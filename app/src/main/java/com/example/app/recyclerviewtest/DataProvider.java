package com.example.app.recyclerviewtest;

import android.support.v4.util.Pair;

import com.example.app.recyclerviewtest.model.Child;
import com.example.app.recyclerviewtest.model.Group;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Deep on 1/12/16.
 * for RecyclerViewTest
 */
public class DataProvider {
    private List<Group> mData;

    private Group mLastGroupDeleted;
    private Child mLastChildDeleted;

    private int mLastGroupDeletedPosition;
    private int mLastChildDeletedPosition;
    private int mLastChildDeletedParentPosition;

    public DataProvider() {
        mData = new ArrayList<>();
        getDummyData();
    }

    private void addItem() {
        Group g = new Group("Group Item");
        Child c = new Child("Child Item");

        g.addChild(c);
        g.addChild(c);
        g.addChild(c);

        mData.add(g);
    }

    private void getDummyData() {
        ArrayList<Child> children = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            children.add(new Child("Child " + (i+1)));
        }
        for(int i = 0; i < 5; i++) {
            Group g = new Group("Group " + (i+1));
            g.setChildren(children);
            mData.add(g);
        }
    }

    public int getGroupCount() {
        return mData.size();
    }

    public int getChildCount(int groupPosition) {
        return mData.get(groupPosition).getChildren().size();
    }

    public Group getGroupItem(int groupPosition) {
        return mData.get(groupPosition);
    }

    public Child getChildItem(int groupPosition, int childPosition) {
        return mData.get(groupPosition).getChildren().get(childPosition);
    }

    public void moveGroupItem(int fromGroupPosition, int toGroupPosition) {
        if (fromGroupPosition == toGroupPosition) return;

        final Group group = mData.remove(fromGroupPosition);
        mData.add(toGroupPosition, group);
    }

    public void moveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {
        if ((fromGroupPosition == toGroupPosition) && (fromChildPosition == toChildPosition)) {
            return;
        }
        final Group fromGroup = mData.get(fromGroupPosition);
        final Group toGroup = mData.get(toGroupPosition);

        if (toGroupPosition != fromGroupPosition) {

        }
    }

    public void removeChildItem(int groupPosition, int childPosition) {
        mLastGroupDeleted = null;
        mLastGroupDeletedPosition = -1;

        mLastChildDeleted = mData.get(groupPosition).getChildren().remove(childPosition);
        mLastChildDeletedPosition = childPosition;
        mLastChildDeletedParentPosition = groupPosition;
    }

    public void removeGroupItem(int groupPosition) {
        mLastChildDeleted = null;
        mLastChildDeletedPosition = -1;
        mLastChildDeletedParentPosition = -1;

        mLastGroupDeleted = mData.remove(groupPosition);
        mLastGroupDeletedPosition = groupPosition;
    }

    public long undoLastRemoval() {
        if (mLastGroupDeleted != null) {
            return undoGroupRemoval();
        } else if (mLastChildDeleted != null) {
            return undoChildRemoval();
        } else {
            return RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION;
        }
    }

    private long undoGroupRemoval() {
        int insertedPosition;
        if (mLastGroupDeletedPosition >= 0 && mLastGroupDeletedPosition < mData.size()) {
            insertedPosition = mLastGroupDeletedPosition;
        } else {
            insertedPosition = mData.size();
        }

        mData.add(insertedPosition, mLastGroupDeleted);

        mLastGroupDeleted = null;
        mLastGroupDeletedPosition = -1;

        return RecyclerViewExpandableItemManager.getPackedPositionForGroup(insertedPosition);
    }

    private long undoChildRemoval() {
        Group group = null;
        int groupPosition = -1;

        // find the group
        for (int i = 0; i < mData.size(); i++) {
            if (i == mLastChildDeletedParentPosition) {
                group = mData.get(i);
                groupPosition = i;
                break;
            }
        }

        if (group == null) {
            return RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION;
        }

        int insertedPosition;
        if (mLastChildDeletedPosition >= 0 && mLastChildDeletedPosition < group.getChildren().size()) {
            insertedPosition = mLastChildDeletedPosition;
        } else {
            insertedPosition = group.getChildren().size();
        }

        group.getChildren().add(insertedPosition, mLastChildDeleted);

        mLastChildDeletedParentPosition = -1;
        mLastChildDeletedPosition = -1;
        mLastChildDeleted = null;

        return RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, insertedPosition);
    }
}
