package com.github.damianjester.nclient.legacy;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.github.damianjester.nclient.R;
import com.github.damianjester.nclient.legacy.adapters.BookmarkAdapter;
import com.github.damianjester.nclient.legacy.components.activities.GeneralActivity;
import com.github.damianjester.nclient.legacy.components.widgets.CustomLinearLayoutManager;

public class BookmarkActivity extends GeneralActivity {
    BookmarkAdapter adapter;
    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Global.initActivity(this);
        setContentView(R.layout.activity_bookmark);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.manage_bookmarks);

        recycler = findViewById(R.id.recycler);
        adapter = new BookmarkAdapter(this);
        recycler.setLayoutManager(new CustomLinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
