package com.example.app.recyclerviewtest.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.app.recyclerviewtest.R;
import com.example.app.recyclerviewtest.provider.AddRemoveProvider;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

/**
 * Created by Deep on 1/22/16.
 * for RecyclerViewTest
 */
public class AddRemoveAdapter extends AbstractExpandableItemAdapter
        <AddRemoveAdapter.MyGroupViewHolder, AddRemoveAdapter.MyChildViewHolder> {
    private static final String TAG = "MyExpandableItemAdapter";

    // NOTE: Make accessible with short name
    private interface Expandable extends ExpandableItemConstants {}

    private RecyclerViewExpandableItemManager mExpandableItemManager;
    private AddRemoveProvider mProvider;
    private View.OnClickListener mItemOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickItemView(v);
        }
    };

    public static abstract class MyBaseViewHolder extends AbstractExpandableItemViewHolder {
        public FrameLayout mContainer;
        public TextView mTextView;

        public MyBaseViewHolder(View v, View.OnClickListener clickListener) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mTextView = (TextView) v.findViewById(android.R.id.text1);

            mContainer.setOnClickListener(clickListener);
        }
    }

    public static class MyGroupViewHolder extends MyBaseViewHolder {
        private Button mAddChild;
        public MyGroupViewHolder(View v, View.OnClickListener clickListener) {
            super(v, clickListener);
            mAddChild = (Button) v.findViewById(R.id.button_add_child);
            mAddChild.setOnClickListener(clickListener);
        }
    }

    public static class MyChildViewHolder extends MyBaseViewHolder {
        public MyChildViewHolder(View v, View.OnClickListener clickListener) {
            super(v, clickListener);

        }
    }

    public AddRemoveAdapter(RecyclerViewExpandableItemManager manager, AddRemoveProvider dataProvider) {

        mExpandableItemManager = manager;
        mProvider = dataProvider;

        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true);
    }

    @Override
    public int getGroupCount() {
        return mProvider.getGroupCount();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mProvider.getChildCount(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mProvider.getGroupItem(groupPosition).getGroupId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mProvider.getChildItem(groupPosition, childPosition).getChildId();
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_group_item_with_buttons, parent, false);
        return new MyGroupViewHolder(v, mItemOnClickListener);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item_with_buttons, parent, false);
        return new MyChildViewHolder(v, mItemOnClickListener);
    }

    @Override
    public void onBindGroupViewHolder(MyGroupViewHolder holder, int groupPosition, int viewType) {
        // child item
        final AddRemoveProvider.BaseData item = mProvider.getGroupItem(groupPosition);

        // set text
        holder.mTextView.setText(item.getText());

        // mark as clickable
        holder.itemView.setClickable(true);

        // set background resource (target view ID: container)
        final int expandState = holder.getExpandStateFlags();

        if ((expandState & Expandable.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId = 0;

            if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                //bgResId = R.drawable.bg_group_item_expanded_state;
            } else {
                bgResId = R.drawable.bg_group_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        // group item
        final AddRemoveProvider.ChildData item = mProvider.getChildItem(groupPosition, childPosition);

        // set text
        holder.mTextView.setText(item.getText());

        // set background resource (target view ID: container)
        int bgResId;
        bgResId = R.drawable.bg_item_normal_state;
        holder.mContainer.setBackgroundResource(bgResId);
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // NOTE: Handles all click events manually
        return false;
    }

    void onClickItemView(View v) {
        RecyclerView.ViewHolder vh = RecyclerViewAdapterUtils.getViewHolder(v);
        int flatPosition = vh.getAdapterPosition();

        if (flatPosition == RecyclerView.NO_POSITION) {
            return;
        }

        long expandablePosition = mExpandableItemManager.getExpandablePosition(flatPosition);
        int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(expandablePosition);
        int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(expandablePosition);

        switch (v.getId()) {
            // common events
            case R.id.container:
                if (childPosition == RecyclerView.NO_POSITION) {
                    handleOnClickGroupItemContainerView(groupPosition);
                } else {
                    handleOnClickChildItemContainerView(groupPosition, childPosition);
                }
                break;
            case R.id.button_add_child:
                handleOnClickGroupItemAddChildBottomButton(groupPosition);
                break;
            default:
                throw new IllegalStateException("Unexpected click event");
        }
    }

    private void handleOnClickGroupItemAddChildBottomButton(int groupPosition) {
        int childCount = mProvider.getChildCount(groupPosition);

        mProvider.addChildItem(groupPosition, childCount);
        mExpandableItemManager.notifyChildItemInserted(groupPosition, childCount);
    }

    private void handleOnClickGroupItemContainerView(int groupPosition) {
        // toggle expanded/collapsed
        if (isGroupExpanded(groupPosition)) {
            mExpandableItemManager.collapseGroup(groupPosition);
        } else {
            mExpandableItemManager.expandGroup(groupPosition);
        }
    }

    private void handleOnClickGroupItemAddBelowButton(int groupPosition) {
        mProvider.addGroupItem(groupPosition + 1);
        mExpandableItemManager.notifyGroupItemInserted(groupPosition + 1);
    }

    private void handleOnClickChildItemContainerView(int groupPosition, int childPosition) {

    }

    // NOTE: This method is called from Fragment
    public void addGroupItem() {
        int count = 1;
        int groupPosition = mProvider.getGroupCount();

        for (int i = 0; i < count; i++) {
            mProvider.addGroupItem(groupPosition + i);
        }

        mExpandableItemManager.notifyGroupItemRangeInserted(groupPosition, count);
    }

    public void addChildItem(){
        
    }

    //
    // Utilities
    //
    private static long getPackedPositionForGroup(int groupPosition) {
        return RecyclerViewExpandableItemManager.getPackedPositionForGroup(groupPosition);
    }

    private static long getPackedPositionForChild(int groupPosition, int childPosition) {
        return RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, childPosition);
    }

    private int getGroupItemFlatPosition(int groupPosition) {
        long packedPosition = getPackedPositionForGroup(groupPosition);
        return getFlatPosition(packedPosition);
    }

    private int getChildItemFlatPosition(int groupPosition, int childPosition) {
        long packedPosition = getPackedPositionForChild(groupPosition, childPosition);
        return getFlatPosition(packedPosition);
    }

    private int getFlatPosition(long packedPosition) {
        return mExpandableItemManager.getFlatPosition(packedPosition);
    }

    private boolean isGroupExpanded(int groupPosition) {
        return mExpandableItemManager.isGroupExpanded(groupPosition);
    }
}