package com.example.app.recyclerviewtest.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.app.recyclerviewtest.R;
import com.example.app.recyclerviewtest.provider.AddRemoveProvider;

public class AddActivity extends AppCompatActivity {
    private static final String FRAGMENT_LIST_VIEW = "Fragment";

    private AddRemoveProvider mProvider;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProvider = new AddRemoveProvider();
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AddActivityFragment(), FRAGMENT_LIST_VIEW)
                    .commit();
        }

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public FloatingActionButton getFab(){
        return mFab;
    }

    public AddRemoveProvider getDataProvider() {
        return mProvider;
    }
}
