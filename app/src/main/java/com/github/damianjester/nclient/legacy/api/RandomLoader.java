package com.github.damianjester.nclient.legacy.api;

import com.github.damianjester.nclient.legacy.RandomActivity;
import com.github.damianjester.nclient.legacy.api.components.Gallery;
import com.github.damianjester.nclient.legacy.api.components.GenericGallery;
import com.github.damianjester.nclient.legacy.utility.ImageDownloadUtility;

import java.util.ArrayList;
import java.util.List;

public class RandomLoader {
    private static final int MAXLOADED = 5;
    private final List<Gallery> galleries;
    private final RandomActivity activity;
    private boolean galleryHasBeenRequested;

    private final InspectorV3.InspectorResponse response = new InspectorV3.DefaultInspectorResponse() {
        @Override
        public void onFailure(Exception e) {
            loadRandomGallery();
        }

        @Override
        public void onSuccess(List<GenericGallery> galleryList) {
            if (galleryList.size() == 0 || !galleryList.get(0).isValid()) {
                loadRandomGallery();
                return;
            }
            Gallery gallery = (Gallery) galleryList.get(0);
            galleries.add(gallery);
            ImageDownloadUtility.preloadImage(activity, gallery.getCover());
            if (galleryHasBeenRequested)
                requestGallery();//requestGallery will call loadRandomGallery
            else if (galleries.size() < MAXLOADED) loadRandomGallery();
        }
    };

    public RandomLoader(RandomActivity activity) {
        this.activity = activity;
        galleries = new ArrayList<>(MAXLOADED);
        galleryHasBeenRequested = RandomActivity.loadedGallery == null;
        loadRandomGallery();
    }

    private void loadRandomGallery() {
        if (galleries.size() >= MAXLOADED) return;
        InspectorV3.randomInspector(activity, response, false).start();
    }

    public void requestGallery() {
        galleryHasBeenRequested = true;
        for (int i = 0; i < galleries.size(); i++) {
            if (galleries.get(i) == null)
                galleries.remove(i--);
        }
        if (galleries.size() > 0) {
            Gallery gallery = galleries.remove(0);
            activity.runOnUiThread(() -> activity.loadGallery(gallery));
            galleryHasBeenRequested = false;
        }
        loadRandomGallery();
    }
}
