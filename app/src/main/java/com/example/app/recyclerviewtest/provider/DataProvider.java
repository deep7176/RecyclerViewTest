package com.example.app.recyclerviewtest.provider;

import android.support.v4.util.Pair;

import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Deep on 1/12/16.
 * for RecyclerViewTest
 */
public class DataProvider {
    private static final String TAG = "DataProvider";
    private static String GROUP_ITEM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String CHILD_ITEM_CHARS = "abcdefghijklmnopqrstuvwxyz";

    private List<GroupSet> mData;
    private IdGenerator mGroupIdGenerator;

    // for undo group item
    private GroupSet mLastRemovedGroup;
    private int mLastRemovedGroupPosition = -1;

    // for undo child item
    private ConcreteChildData mLastRemovedChild;
    private long mLastRemovedChildParentGroupId = -1;
    private int mLastRemovedChildPosition = -1;

    public DataProvider() {
        mData = new LinkedList<>();
        mGroupIdGenerator = new IdGenerator();

        for (int i = 0; i < 1; i++) {
            addGroupItem(i);
            for (int j = 0; j < 3; j++) {
                addChildItem(i, j);
            }
        }
    }

    public void removeGroupItem(int groupPosition) {
        mLastRemovedGroup = mData.remove(groupPosition);
        mLastRemovedGroupPosition = groupPosition;

        mLastRemovedChild = null;
        mLastRemovedChildParentGroupId = -1;
        mLastRemovedChildPosition = -1;
    }

    public void removeChildItem(int groupPosition, int childPosition) {
        mLastRemovedChild = mData.get(groupPosition).mChildren.remove(childPosition);
        mLastRemovedChildParentGroupId = mData.get(groupPosition).mGroup.getGroupId();
        mLastRemovedChildPosition = childPosition;

        mLastRemovedGroup = null;
        mLastRemovedGroupPosition = -1;
    }

    public long undoLastRemoval() {
        if (mLastRemovedGroup != null) {
            return undoGroupRemoval();
        } else if (mLastRemovedChild != null) {
            return undoChildRemoval();
        } else {
            return RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION;
        }
    }

    private long undoGroupRemoval() {
        int insertedPosition;
        if (mLastRemovedGroupPosition >= 0 && mLastRemovedGroupPosition < mData.size()) {
            insertedPosition = mLastRemovedGroupPosition;
        } else {
            insertedPosition = mData.size();
        }

        mData.add(insertedPosition, mLastRemovedGroup);

        mLastRemovedGroup = null;
        mLastRemovedGroupPosition = -1;

        return RecyclerViewExpandableItemManager.getPackedPositionForGroup(insertedPosition);
    }

    private long undoChildRemoval() {
        GroupSet group = null;
        int groupPosition = -1;

        // find the group
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).mGroup.getGroupId() == mLastRemovedChildParentGroupId) {
                group = mData.get(i);
                groupPosition = i;
                break;
            }
        }

        if (group == null) {
            return RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION;
        }

        int insertedPosition;
        if (mLastRemovedChildPosition >= 0 && mLastRemovedChildPosition < group.mChildren.size()) {
            insertedPosition = mLastRemovedChildPosition;
        } else {
            insertedPosition = group.mChildren.size();
        }

        group.mChildren.add(insertedPosition, mLastRemovedChild);

        mLastRemovedChildParentGroupId = -1;
        mLastRemovedChildPosition = -1;
        mLastRemovedChild = null;

        return RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, insertedPosition);
    }

    public int getGroupCount() {
        return mData.size();
    }

    public int getChildCount(int groupPosition) {
        return mData.get(groupPosition).mChildren.size();
    }

    public GroupData getGroupItem(int groupPosition) {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw new IndexOutOfBoundsException("groupPosition = " + groupPosition);
        }

        return mData.get(groupPosition).mGroup;
    }

    public ChildData getChildItem(int groupPosition, int childPosition) {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw new IndexOutOfBoundsException("groupPosition = " + groupPosition);
        }

        final List<ConcreteChildData> children = mData.get(groupPosition).mChildren;

        if (childPosition < 0 || childPosition >= children.size()) {
            throw new IndexOutOfBoundsException("childPosition = " + childPosition);
        }

        return children.get(childPosition);
    }

    public void addGroupItem(int groupPosition) {
        long id = mGroupIdGenerator.next();
        String text = getOneCharString(GROUP_ITEM_CHARS, id);
        ConcreteGroupData newItem = new ConcreteGroupData(id, text);

        mData.add(groupPosition, new GroupSet(newItem));
    }

    public void addChildItem(int groupPosition, int childPosition) {
        mData.get(groupPosition).addNewChildData(childPosition);
    }

    public void moveGroupItem(int fromGroupPosition, int toGroupPosition) {
        if (fromGroupPosition == toGroupPosition) {
            return;
        }
        final GroupSet group = mData.remove(fromGroupPosition);
        mData.add(toGroupPosition, group);
    }

    public void moveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {
        if ((fromGroupPosition == toGroupPosition) && (fromChildPosition == toChildPosition)) {
            return;
        }

        final GroupSet fromGroup = mData.get(fromGroupPosition);
        final GroupSet toGroup = mData.get(toGroupPosition);

        final ConcreteChildData item = fromGroup.mChildren.remove(fromChildPosition);

        if (toGroupPosition != fromGroupPosition) {
            item.mId = fromGroup.mChildIdGenerator.next();
        }

        toGroup.mChildren.add(toChildPosition, item);

    }

    public static final class ConcreteGroupData extends GroupData {
        private final long mId;
        private final String mText;
        private boolean pinned;

        ConcreteGroupData(long id, String text) {
            mId = id;
            mText = text;
        }

        @Override
        public long getGroupId() {
            return mId;
        }

        @Override
        public String getText() {
            return mText;
        }

        @Override
        public void setPinned(boolean pinned) {
            this.pinned = pinned;
        }

        @Override
        public boolean isPinned() {
            return pinned;
        }
    }

    public static final class ConcreteChildData extends ChildData {
        private long mId;
        private boolean pinned;
        private final String mText;

        ConcreteChildData(long id, String text) {
            mId = id;
            mText = text;
        }

        @Override
        public long getChildId() {
            return mId;
        }

        @Override
        public String getText() {
            return mText;
        }

        @Override
        public void setPinned(boolean pinned) {
            this.pinned = pinned;
        }

        @Override
        public boolean isPinned() {
            return pinned;
        }
    }

    private static String getOneCharString(String str, long index) {
        return Character.toString(str.charAt((int) (index % str.length())));
    }

    private static class IdGenerator {
        long mId;

        public long next() {
            final long id = mId;
            mId += 1;
            return id;
        }
    }

    private static class GroupSet {
        private ConcreteGroupData mGroup;
        private List<ConcreteChildData> mChildren;
        private IdGenerator mChildIdGenerator;

        public GroupSet(ConcreteGroupData group) {
            mGroup = group;
            mChildren = new LinkedList<>();
            mChildIdGenerator = new IdGenerator();
        }

        public void addNewChildData(int position) {
            long id = mChildIdGenerator.next();
            String text = getOneCharString(CHILD_ITEM_CHARS, id);
            ConcreteChildData child = new ConcreteChildData(id, text);

            mChildren.add(position, child);
        }
    }

    public static abstract class BaseData {
        public abstract String getText();
        public abstract void setPinned(boolean pinned);

        public abstract boolean isPinned();
    }

    public static abstract class GroupData extends BaseData {
        public abstract long getGroupId();
    }

    public static abstract class ChildData extends BaseData {
        public abstract long getChildId();
    }
}
