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

package com.example.app.recyclerviewtest.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.app.recyclerviewtest.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ExpandableItemIndicatorImplAnim extends ExpandableItemIndicator.Impl {
    private ImageView mImageView;
    private int mColor;

    @Override
    public void onInit(Context context, AttributeSet attrs, int defStyleAttr, ExpandableItemIndicator thiz) {
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