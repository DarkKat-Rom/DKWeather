/*
 * Copyright (C) 2018 DarkKat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.darkkatrom.dkweather.colorpicker.animator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.LinearInterpolator;

import net.darkkatrom.dkweather.colorpicker.adapter.ColorPickerListAdapter.ListHeaderViewHolder;
import net.darkkatrom.dkweather.colorpicker.adapter.ColorPickerListAdapter.ListViewHolder;

import java.util.List;

public class ColorPickerListItemAnimator extends DefaultColorPickerListItemAnimator {

    @Override
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @Override
    public ItemHolderInfo recordPreLayoutInformation(RecyclerView.State state,
            RecyclerView.ViewHolder viewHolder, int changeFlags, List<Object> payloads) {
        if (!(viewHolder instanceof ListHeaderViewHolder)) {
            return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
        }
        HeaderInfo headerInfo = new HeaderInfo();
        headerInfo.setFrom(viewHolder);
        return headerInfo;
    }

    @Override
    public ItemHolderInfo recordPostLayoutInformation(RecyclerView.State state,
            RecyclerView.ViewHolder viewHolder) {
        if (!(viewHolder instanceof ListHeaderViewHolder)) {
            return super.recordPostLayoutInformation(state, viewHolder);
        }
        HeaderInfo headerInfo = new HeaderInfo();
        headerInfo.setFrom(viewHolder);
        return headerInfo;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder,
            final RecyclerView.ViewHolder newHolder, ItemHolderInfo preInfo, ItemHolderInfo postInfo) {

        if (!(oldHolder instanceof ListHeaderViewHolder) && !(newHolder instanceof ListHeaderViewHolder)) {
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
        }

        final HeaderInfo preHeaderInfo = (HeaderInfo) preInfo;
        final HeaderInfo postHeaderInfo = (HeaderInfo) postInfo;

        final boolean animate = preHeaderInfo.rotationX != postHeaderInfo.rotationX;
        if (!animate) {
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
        }

        final ListHeaderViewHolder holder = (ListHeaderViewHolder) newHolder;

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(getChangeDuration());
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float position = animation.getAnimatedFraction();
                float currentRotationX = postHeaderInfo.rotationX == 180 ? 180 * position : 180 * (1 - position);
                holder.mHeaderButton.setRotationX(currentRotationX);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                holder.mHeaderButton.setRotationX(preHeaderInfo.rotationX);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                holder.mHeaderButton.setRotationX(postHeaderInfo.rotationX);
                dispatchAnimationFinished(newHolder);
            }
        });
        animator.start();
        return false;
    }

    private class HeaderInfo extends ItemHolderInfo {
        float rotationX;

        @Override
        public ItemHolderInfo setFrom(RecyclerView.ViewHolder holder) {
            rotationX = ((ListHeaderViewHolder) holder).mHeaderButton.getRotationX();
            return super.setFrom(holder);
        }
    }

    @Override
    public long getChangeDuration() {
        return 250;
    }
}
