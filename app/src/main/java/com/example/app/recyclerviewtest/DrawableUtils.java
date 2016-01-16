package com.example.app.recyclerviewtest;

import android.graphics.drawable.Drawable;

/**
 * Created by Deep on 1/12/16.
 * for RecyclerViewTest
 */
public class DrawableUtils {
    private static final int[] EMPTY_STATE = new int[] {};

    public static void clearState(Drawable drawable) {
        if (drawable != null) {
            drawable.setState(EMPTY_STATE);
        }
    }
}
