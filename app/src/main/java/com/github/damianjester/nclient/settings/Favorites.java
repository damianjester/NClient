package com.github.damianjester.nclient.settings;

import com.github.damianjester.nclient.api.components.Gallery;
import com.github.damianjester.nclient.api.components.GenericGallery;
import com.github.damianjester.nclient.async.database.Queries;

public class Favorites {


    public static boolean addFavorite(Gallery gallery) {
        Queries.FavoriteTable.addFavorite(gallery);
        return true;
    }

    public static boolean removeFavorite(GenericGallery gallery) {
        Queries.FavoriteTable.removeFavorite(gallery.getId());
        return true;
    }

    public static boolean isFavorite(GenericGallery gallery) {
        if (gallery == null || !gallery.isValid()) return false;
        return Queries.FavoriteTable.isFavorite(gallery.getId());
    }


}
