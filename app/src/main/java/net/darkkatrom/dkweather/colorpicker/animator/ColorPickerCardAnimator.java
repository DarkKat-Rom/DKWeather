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

import net.darkkatrom.dkweather.colorpicker.adapter.ColorPickerCardAdapter.CardViewHolder;
import net.darkkatrom.dkweather.colorpicker.util.ColorPickerHelper;

import java.util.List;

public class ColorPickerCardAnimator extends BaseItemAnimator {

    private boolean mTitleSet = false;
    private boolean mSubtitleSet = false;

    @Override
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @Override
    public ItemHolderInfo recordPreLayoutInformation(RecyclerView.State state,
            RecyclerView.ViewHolder viewHolder, int changeFlags, List<Object> payloads) {
        ColorInfo colorInfo = new ColorInfo();
        colorInfo.setFrom(viewHolder);
        return colorInfo;
    }

    @Override
    public ItemHolderInfo recordPostLayoutInformation(RecyclerView.State state,
            RecyclerView.ViewHolder viewHolder) {
        ColorInfo colorInfo = new ColorInfo();
        colorInfo.setFrom(viewHolder);
        return colorInfo;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder,
            final RecyclerView.ViewHolder newHolder, ItemHolderInfo preInfo, ItemHolderInfo postInfo) {

        final CardViewHolder holder = (CardViewHolder) newHolder;
        final ColorInfo preColorInfo = (ColorInfo) preInfo;
        final ColorInfo postColorInfo = (ColorInfo) postInfo;

        final boolean setTitle = !preColorInfo.title.equals(postColorInfo.title);
        final boolean setSubtitle = !preColorInfo.subtitle.equals(postColorInfo.subtitle);
        final boolean animatePrimaryTextColor =
                preColorInfo.primaryTextColor != postColorInfo.primaryTextColor;
        final boolean animateSecondaryTextColor =
                preColorInfo.secondaryTextColor != postColorInfo.secondaryTextColor;
        final boolean animateActionApplyTextColor =
                preColorInfo.actionApplyTextColor != postColorInfo.actionApplyTextColor;

        final boolean animateActionFavoriteVisibility =
                preColorInfo.actionFavoriteVisible != postColorInfo.actionFavoriteVisible;

        final boolean animateIconAddFavoriteVisibility =
                preColorInfo.iconAddFavoriteVisible != postColorInfo.iconAddFavoriteVisible;
        final boolean animateIconRemoveFavoriteVisibility =
                preColorInfo.iconRemoveFavoriteVisible != postColorInfo.iconRemoveFavoriteVisible;

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(350);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float position = animation.getAnimatedFraction();
                int blendedBg = ColorPickerHelper.getBlendColor(preColorInfo.backgroundColor,
                        postColorInfo.backgroundColor, position);
                holder.mCardView.setCardBackgroundColor(blendedBg);
                if (setTitle && position > 0.5f && !mTitleSet) {
                    holder.mTitle.setText(postColorInfo.title);
                    mTitleSet = true;
                }
                if (setSubtitle && position > 0.5f && !mSubtitleSet) {
                    holder.mSubtitle.setText(postColorInfo.subtitle);
                    mSubtitleSet = true;
                }
                if (animatePrimaryTextColor) {
                    int blendedPrimaryTextColor = ColorPickerHelper.getBlendColor(
                            preColorInfo.primaryTextColor, postColorInfo.primaryTextColor, position);
                    holder.mTitle.setTextColor(blendedPrimaryTextColor);
                    holder.mIconAddFavorite.setImageTintList(
                            ColorStateList.valueOf(blendedPrimaryTextColor));
                    holder.mIconRemoveFavorite.setImageTintList(
                            ColorStateList.valueOf(blendedPrimaryTextColor));
                }
                if (animateSecondaryTextColor) {
                    int blendedSecondaryTextColor = ColorPickerHelper.getBlendColor(
                            preColorInfo.secondaryTextColor, postColorInfo.secondaryTextColor, position);
                    holder.mSubtitle.setTextColor(blendedSecondaryTextColor);
                }
                if (animateActionApplyTextColor) {
                    int blendedActionApplyTextColor = ColorPickerHelper.getBlendColor(
                            preColorInfo.actionApplyTextColor, postColorInfo.actionApplyTextColor, position);
                    holder.mActionApply.setTextColor(blendedActionApplyTextColor);
                }
                if (animateActionFavoriteVisibility) {
                    holder.mActionFavorite.setAlpha(postColorInfo.actionFavoriteVisible
                            ? position : 1 - position);
                } else {
                    if (animateIconAddFavoriteVisibility) {
                        holder.mIconAddFavorite.setAlpha(postColorInfo.iconAddFavoriteVisible
                                ? position : 1 - position);
                    }
                    if (animateIconRemoveFavoriteVisibility) {
                        holder.mIconRemoveFavorite.setAlpha(postColorInfo.iconRemoveFavoriteVisible
                                ? position : 1 - position);
                    }
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (setTitle) {
                    holder.mTitle.setText(preColorInfo.title);
                }
                if (setSubtitle) {
                    holder.mSubtitle.setText(preColorInfo.subtitle);
                }
                if (animateActionFavoriteVisibility) {
                    holder.mActionFavorite.setAlpha(preColorInfo.actionFavoriteVisible ? 1f : 0f);
                    if (holder.mActionFavorite.getVisibility() != View.VISIBLE) {
                        holder.mActionFavorite.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (animateIconAddFavoriteVisibility) {
                        holder.mIconAddFavorite.setAlpha(preColorInfo.iconAddFavoriteVisible ? 1f : 0f);
                        if (holder.mIconAddFavorite.getVisibility() != View.VISIBLE) {
                            holder.mIconAddFavorite.setVisibility(View.VISIBLE);
                        }
                    }
                    if (animateIconRemoveFavoriteVisibility) {
                        holder.mIconRemoveFavorite.setAlpha(preColorInfo.iconRemoveFavoriteVisible ? 1f : 0f);
                        if (holder.mIconRemoveFavorite.getVisibility() != View.VISIBLE) {
                            holder.mIconRemoveFavorite.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animatePrimaryTextColor) {
                    holder.mTitle.setTextColor(postColorInfo.primaryTextColors);
                    holder.mIconAddFavorite.setImageTintList(postColorInfo.primaryTextColors);
                    holder.mIconRemoveFavorite.setImageTintList(postColorInfo.primaryTextColors);
                }
                if (animateSecondaryTextColor) {
                    holder.mSubtitle.setTextColor(postColorInfo.secondaryTextColors);
                }
                if (animateActionApplyTextColor) {
                    holder.mActionApply.setTextColor(postColorInfo.actionApplyTextColors);
                }
                if (animateActionFavoriteVisibility) {
                    holder.mActionFavorite.setAlpha(postColorInfo.actionFavoriteVisible ? 1f : 0f);
                    holder.mActionFavorite.setVisibility(postColorInfo.actionFavoriteVisible
                            ? View.VISIBLE : View.GONE);
                } else {
                    if (animateIconAddFavoriteVisibility) {
                        holder.mIconAddFavorite.setAlpha(postColorInfo.iconAddFavoriteVisible ? 1f : 0f);
                        holder.mIconAddFavorite.setVisibility(postColorInfo.iconAddFavoriteVisible
                                ? View.VISIBLE : View.GONE);
                    }
                    if (animateIconRemoveFavoriteVisibility) {
                        holder.mIconRemoveFavorite.setAlpha(postColorInfo.iconRemoveFavoriteVisible ? 1f : 0f);
                        holder.mIconRemoveFavorite.setVisibility(postColorInfo.iconRemoveFavoriteVisible
                                ? View.VISIBLE : View.GONE);
                    }
                }
                dispatchAnimationFinished(newHolder);
            }
        });
        animator.start();
        return false;
    }

    private class ColorInfo extends ItemHolderInfo {
        String title;
        String subtitle;
        int backgroundColor;
        int primaryTextColor;
        int secondaryTextColor;
        int actionApplyTextColor;
        ColorStateList primaryTextColors;
        ColorStateList secondaryTextColors;
        ColorStateList actionApplyTextColors;
        boolean actionFavoriteVisible;
        boolean iconAddFavoriteVisible;
        boolean iconRemoveFavoriteVisible;

        @Override
        public ItemHolderInfo setFrom(RecyclerView.ViewHolder holder) {
            title = ((CardViewHolder) holder).mTitle.getText().toString();
            subtitle = ((CardViewHolder) holder).mSubtitle.getText().toString();
            backgroundColor = ((CardViewHolder) holder).mCardView.getCardBackgroundColor().getDefaultColor();
            primaryTextColor = ((CardViewHolder) holder).mTitle.getCurrentTextColor();
            secondaryTextColor = ((CardViewHolder) holder).mSubtitle.getCurrentTextColor();
            actionApplyTextColor = ((CardViewHolder) holder).mActionApply.getCurrentTextColor();
            primaryTextColors = ((CardViewHolder) holder).mTitle.getTextColors();
            secondaryTextColors = ((CardViewHolder) holder).mSubtitle.getTextColors();
            actionApplyTextColors = ((CardViewHolder) holder).mActionApply.getTextColors();
            actionFavoriteVisible = ((CardViewHolder) holder).mActionFavorite.getVisibility() == View.VISIBLE;
            iconAddFavoriteVisible = ((CardViewHolder) holder).mIconAddFavorite.getVisibility() == View.VISIBLE;
            iconRemoveFavoriteVisible = ((CardViewHolder) holder).mIconRemoveFavorite.getVisibility() == View.VISIBLE;
            return super.setFrom(holder);
        }
    }


}
