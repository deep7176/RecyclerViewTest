package com.example.app.recyclerviewtest.model;

import java.util.ArrayList;

/**
 * Created by Deep on 1/12/16.
 */
public class Group {
    boolean isPinned;
    String value;
    ArrayList<Child> children;

    public Group(String value) {
        this.value = value;
        children = new ArrayList<>();
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

    public ArrayList<Child> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Child> children) {
        this.children = children;
    }

    public void addChild(Child c){
        this.children.add(c);
    }

    public long getGroupId() {
        return 0;
    }
}
