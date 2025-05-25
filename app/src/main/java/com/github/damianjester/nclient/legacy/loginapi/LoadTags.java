package com.github.damianjester.nclient.legacy.loginapi;

import android.util.JsonReader;

import androidx.annotation.Nullable;

import com.github.damianjester.nclient.legacy.adapters.TagsAdapter;
import com.github.damianjester.nclient.legacy.api.components.Tag;
import com.github.damianjester.nclient.legacy.api.enums.TagType;
import com.github.damianjester.nclient.legacy.settings.Global;
import com.github.damianjester.nclient.legacy.settings.Login;
import com.github.damianjester.nclient.legacy.utility.LogUtility;
import com.github.damianjester.nclient.legacy.utility.Utility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

import okhttp3.Request;
import okhttp3.Response;

public class LoadTags extends Thread {
    @Nullable
    private final TagsAdapter adapter;

    public LoadTags(@Nullable TagsAdapter adapter) {
        this.adapter = adapter;
    }

    private Elements getScripts(String url) throws IOException {

        Response response = Global.getClient().newCall(new Request.Builder().url(url).build()).execute();
        Elements x = Jsoup.parse(response.body().byteStream(), null, Utility.getBaseUrl()).getElementsByTag("script");
        response.close();
        return x;
    }

    private String extractArray(Element e) throws StringIndexOutOfBoundsException {
        String t = e.toString();
        return t.substring(t.indexOf('['), t.indexOf(';'));
    }

    private void readTags(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            Tag tt = new Tag(reader);
            if (tt.getType() != TagType.LANGUAGE && tt.getType() != TagType.CATEGORY) {
                Login.addOnlineTag(tt);
                if (adapter != null) adapter.addItem();
            }
        }
    }

    @Override
    public void run() {
        super.run();
        if (Login.getUser() == null) return;
        String url = String.format(Locale.US, Utility.getBaseUrl() + "users/%s/%s/blacklist",
            Login.getUser().getId(), Login.getUser().getCodename()
        );
        LogUtility.d(url);
        try {
            Elements scripts = getScripts(url);
            analyzeScripts(scripts);
        } catch (IOException | StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    private void analyzeScripts(Elements scripts) throws IOException, StringIndexOutOfBoundsException {
        if (scripts.size() > 0) {
            Login.clearOnlineTags();
            String array = Utility.unescapeUnicodeString(extractArray(scripts.last()));
            JsonReader reader = new JsonReader(new StringReader(array));
            readTags(reader);
            reader.close();
        }
    }
}
