package com.example.app.recyclerviewtest.model;

import java.util.ArrayList;

/**
 * Created by Deep on 1/12/16.
 */
public class Child {
    boolean isPinned;
    String value;

    public Child(String value) {
        this.value = value;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean isPinned) {
        this.isPinned = isPinned;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getChildId() {
        return 0;
    }
}
