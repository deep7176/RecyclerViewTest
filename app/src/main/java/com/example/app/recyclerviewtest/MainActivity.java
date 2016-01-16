package com.example.app.recyclerviewtest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import java.io.LineNumberInputStream;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, DialogFragment.EventListener {
    private static final String TAG = "MainActivity";

    private static final String FRAGMENT_TAG_ITEM_PINNED_DIALOG = "Pinned Dialog";
    private static final String FRAGMENT_LIST_VIEW = "ListViewFragment";
    private FloatingActionButton mFAB;

    private DataProvider mProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProvider = new DataProvider();
        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFAB.setOnClickListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainActivityFragment(), FRAGMENT_LIST_VIEW)
                    .commit();
        }


    }

    /**
     * This method will be called when a group item is removed
     *
     * @param groupPosition The position of the group item within data set
     */
    public void onGroupItemRemoved(int groupPosition) {
        Snackbar snackbar = Snackbar.make(mFAB, "Group Item Removed", Snackbar.LENGTH_LONG);

        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemUndoActionClicked();
            }
        });
        snackbar.show();

    }

    /**
     * This method will be called when a child item is removed
     *
     * @param groupPosition The group position of the child item within data set
     * @param childPosition The position of the child item within the group
     */
    public void onChildItemRemoved(int groupPosition, int childPosition) {
        Snackbar snackbar = Snackbar.make(mFAB, "Child Item Removed", Snackbar.LENGTH_LONG);

        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemUndoActionClicked();
            }
        });
        snackbar.show();
    }

    /**
     * This method will be called when a group item is pinned
     *
     * @param groupPosition The position of the group item within data set
     */
    public void onGroupItemPinned(int groupPosition) {
        final android.support.v4.app.DialogFragment dialog = DialogFragment
                .newInstance(groupPosition, RecyclerView.NO_POSITION);

        getSupportFragmentManager()
                .beginTransaction()
                .add(dialog, FRAGMENT_TAG_ITEM_PINNED_DIALOG)
                .commit();
    }

    /**
     * This method will be called when a child item is pinned
     *
     * @param groupPosition The group position of the child item within data set
     * @param childPosition The position of the child item within the group
     */
    public void onChildItemPinned(int groupPosition, int childPosition) {
        final android.support.v4.app.DialogFragment dialog = DialogFragment
                .newInstance(groupPosition, childPosition);

        getSupportFragmentManager()
                .beginTransaction()
                .add(dialog, FRAGMENT_TAG_ITEM_PINNED_DIALOG)
                .commit();
    }

    public void onGroupItemClicked(int groupPosition) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        DataProvider.GroupData data = getDataProvider().getGroupItem(groupPosition);

        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            ((MainActivityFragment) fragment).notifyGroupItemChanged(groupPosition);
        }
    }

    public void onChildItemClicked(int groupPosition, int childPosition) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        DataProvider.ChildData data = getDataProvider().getChildItem(groupPosition, childPosition);

        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            ((MainActivityFragment) fragment).notifyChildItemChanged(groupPosition, childPosition);
        }
    }

    private void onItemUndoActionClicked() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        final long result = getDataProvider().undoLastRemoval();

        if (result == RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION) {
            return;
        }

        final int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(result);
        final int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(result);

        if (childPosition == RecyclerView.NO_POSITION) {
            // group item
            ((MainActivityFragment) fragment).notifyGroupItemRestored(groupPosition);
        } else {
            // child item
            ((MainActivityFragment) fragment).notifyChildItemRestored(groupPosition, childPosition);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.fab:
                addItem();
                break;
        }
    }

    private void addItem() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch( item.getItemId()){
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNotifyExpandableItemPinnedDialogDismissed(
            int groupPosition, int childPosition, boolean ok) {

        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);

        if (childPosition == RecyclerView.NO_POSITION) {
            // group item
            getDataProvider().getGroupItem(groupPosition).setPinned(ok);
            ((MainActivityFragment) fragment).notifyGroupItemChanged(groupPosition);
        } else {
            // child item
            getDataProvider().getChildItem(groupPosition, childPosition).setPinned(ok);
            ((MainActivityFragment) fragment).notifyChildItemChanged(groupPosition, childPosition);
        }
    }

    public DataProvider getDataProvider() {
        return mProvider;
    }
}
