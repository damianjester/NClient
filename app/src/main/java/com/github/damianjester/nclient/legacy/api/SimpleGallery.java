package com.github.damianjester.nclient.legacy.api;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.github.damianjester.nclient.legacy.api.components.Gallery;
import com.github.damianjester.nclient.legacy.api.components.GalleryData;
import com.github.damianjester.nclient.legacy.api.components.GenericGallery;
import com.github.damianjester.nclient.legacy.api.components.Page;
import com.github.damianjester.nclient.legacy.api.components.Tag;
import com.github.damianjester.nclient.legacy.api.components.TagList;
import com.github.damianjester.nclient.legacy.api.enums.ImageExt;
import com.github.damianjester.nclient.legacy.api.enums.Language;
import com.github.damianjester.nclient.legacy.api.enums.TagStatus;
import com.github.damianjester.nclient.legacy.async.database.Queries;
import com.github.damianjester.nclient.legacy.components.classes.Size;
import com.github.damianjester.nclient.legacy.files.GalleryFolder;
import com.github.damianjester.nclient.legacy.settings.Global;
import com.github.damianjester.nclient.legacy.utility.LogUtility;
import com.github.damianjester.nclient.legacy.utility.Utility;

import org.jsoup.nodes.Element;

import java.util.Collection;
import java.util.Locale;

public class SimpleGallery extends GenericGallery {
    public static final Creator<SimpleGallery> CREATOR = new Creator<SimpleGallery>() {
        @Override
        public SimpleGallery createFromParcel(Parcel in) {
            return new SimpleGallery(in);
        }

        @Override
        public SimpleGallery[] newArray(int size) {
            return new SimpleGallery[size];
        }
    };
    private final String title;
    private final ImageExt thumbnail;
    private final int id, mediaId;
    private Language language = Language.UNKNOWN;
    private TagList tags;

    public SimpleGallery(Parcel in) {
        title = in.readString();
        id = in.readInt();
        mediaId = in.readInt();
        thumbnail = ImageExt.values()[in.readByte()];
        language = Language.values()[in.readByte()];
    }

    public boolean hasTag(Tag tag) {
        return tags.hasTag(tag);
    }

    public boolean hasTags(Collection<Tag> tags) {
        return this.tags.hasTags(tags);
    }

    public SimpleGallery(Cursor c) {
        title = c.getString(c.getColumnIndex(Queries.HistoryTable.TITLE));
        id = c.getInt(c.getColumnIndex(Queries.HistoryTable.ID));
        mediaId = c.getInt(c.getColumnIndex(Queries.HistoryTable.MEDIAID));
        thumbnail = ImageExt.values()[c.getInt(c.getColumnIndex(Queries.HistoryTable.THUMB))];
    }

    public SimpleGallery(Context context, Element e) {
        String temp;
        String tags = e.attr("data-tags").replace(' ', ',');
        this.tags = Queries.TagTable.getTagsFromListOfInt(tags);
        language = Gallery.loadLanguage(this.tags);
        Element a = e.getElementsByTag("a").first();
        temp = a.attr("href");
        id = Integer.parseInt(temp.substring(3, temp.length() - 1));
        a = e.getElementsByTag("img").first();
        temp = a.hasAttr("data-src") ? a.attr("data-src") : a.attr("src");
        mediaId = Integer.parseInt(temp.substring(temp.indexOf("galleries") + 10, temp.lastIndexOf('/')));
        String extension = temp.substring(temp.lastIndexOf('.') + 1);
        thumbnail = Page.charToExt(extension.charAt(0));
        title = e.getElementsByTag("div").first().text();
        if (context != null && id > Global.getMaxId()) Global.updateMaxId(context, id);
    }

    public SimpleGallery(Gallery gallery) {
        title = gallery.getTitle();
        mediaId = gallery.getMediaId();
        id = gallery.getId();
        thumbnail = gallery.getThumb();
    }

    private static String extToString(ImageExt ext) {
        if (ext == null) {
            return null;
        }
        switch (ext) {
            case GIF:
                return "gif";
            case PNG:
                return "png";
            case JPG:
                return "jpg";
            case WEBP:
                return "webp";
        }
        return null;
    }

    public Language getLanguage() {
        return language;
    }

    public boolean hasIgnoredTags(String s) {
        if (tags == null) return false;
        for (Tag t : tags.getAllTagsList())
            if (s.contains(t.toQueryTag(TagStatus.AVOIDED))) {
                LogUtility.d("Found: " + s + ",," + t.toQueryTag());
                return true;
            }
        return false;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Type getType() {
        return Type.SIMPLE;
    }

    @Override
    public int getPageCount() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return id > 0;
    }

    @Override
    @NonNull
    public String getTitle() {
        return title;
    }

    @Override
    public Size getMaxSize() {
        return null;
    }

    @Override
    public Size getMinSize() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(id);
        dest.writeInt(mediaId);
        dest.writeByte((byte) thumbnail.ordinal());
        dest.writeByte((byte) language.ordinal());
        //TAGS AREN'T WRITTEN
    }

    public Uri getThumbnail() {
        if (thumbnail == ImageExt.GIF) {
            return Uri.parse(String.format(Locale.US, "https://i1." + Utility.getHost() + "/galleries/%d/1.gif", mediaId));
        }
        return Uri.parse(String.format(Locale.US, "https://t1." + Utility.getHost() + "/galleries/%d/thumb.%s", mediaId, extToString(thumbnail)));
    }

    public int getMediaId() {
        return mediaId;
    }

    public ImageExt getThumb() {
        return thumbnail;
    }

    @Override
    public GalleryFolder getGalleryFolder() {
        return null;
    }

    @Override
    public String toString() {
        return "SimpleGallery{" +
            "language=" + language +
            ", title='" + title + '\'' +
            ", thumbnail=" + thumbnail +
            ", id=" + id +
            ", mediaId=" + mediaId +
            '}';
    }

    @Override
    public boolean hasGalleryData() {
        return false;
    }

    @Override
    public GalleryData getGalleryData() {
        return null;
    }
}
