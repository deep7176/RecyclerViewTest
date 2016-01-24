/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.example.app.recyclerviewtest.ui.view;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.app.recyclerviewtest.R;

public class Indicator extends FrameLayout {
    static abstract class Impl {
        public abstract void onInit(Context context, AttributeSet attrs, int defStyleAttr, Indicator thiz);

        public abstract void setExpandedState(boolean isExpanded, boolean animate);
    }

    private Impl mImpl;

    public Indicator(Context context) {
        super(context);
        onInit(context, null, 0);
    }

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInit(context, attrs, 0);
    }

    public Indicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInit(context, attrs, defStyleAttr);
    }

    protected void onInit(Context context, AttributeSet attrs, int defStyleAttr) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // NOTE: VectorDrawable only supports API level 21 or later
            mImpl = new ExpandableItemIndicatorImplAnim();
        } else {
            mImpl = new ExpandableItemIndicatorImplNoAnim();
        }
        mImpl.onInit(context, attrs, defStyleAttr, this);
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        super.dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        super.dispatchThawSelfOnly(container);
    }

    public void setExpandedState(boolean isExpanded, boolean animate) {
        mImpl.setExpandedState(isExpanded, animate);
    }

    class ExpandableItemIndicatorImplNoAnim extends Indicator.Impl {
        private ImageView mImageView;

        @Override
        public void onInit(Context context, AttributeSet attrs, int defStyleAttr, Indicator thiz) {
            View v = LayoutInflater.from(context).inflate(R.layout.widget_expandable_item_indicator, thiz, true);
            mImageView = (ImageView) v.findViewById(R.id.image_view);
        }

        @Override
        public void setExpandedState(boolean isExpanded, boolean animate) {
            int resId = (isExpanded) ? R.drawable.ic_expand_less : R.drawable.ic_expand_more;
            mImageView.setImageResource(resId);
        }
    }

    class ExpandableItemIndicatorImplAnim extends Indicator.Impl {
        private ImageView mImageView;
        private int mColor;

        @Override
        public void onInit(Context context, AttributeSet attrs, int defStyleAttr, Indicator thiz) {
            View v = LayoutInflater.from(context).inflate(R.layout.widget_expandable_item_indicator, thiz, true);
            mImageView = (ImageView) v.findViewById(R.id.image_view);
            mColor = ContextCompat.getColor(context, R.color.expandable_item_indicator_color);
        }

        @Override
        public void setExpandedState(boolean isExpanded, boolean animate) {
            if (animate) {
                int resId = isExpanded ? R.drawable.ic_expand_more_to_expand_less : R.drawable.ic_expand_less_to_expand_more;
                mImageView.setImageResource(resId);
                DrawableCompat.setTint(mImageView.getDrawable(), mColor);
                ((Animatable) mImageView.getDrawable()).start();
            } else {
                int resId = isExpanded ? R.drawable.ic_expand_less : R.drawable.ic_expand_more;
                mImageView.setImageResource(resId);
                DrawableCompat.setTint(mImageView.getDrawable(), mColor);
            }
        }
    }

}
