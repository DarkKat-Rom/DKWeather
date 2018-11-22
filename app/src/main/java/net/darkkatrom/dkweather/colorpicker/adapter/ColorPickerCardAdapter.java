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

package net.darkkatrom.dkweather.colorpicker.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.colorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkweather.colorpicker.model.ColorPickerCard;
import net.darkkatrom.dkweather.colorpicker.model.ColorPickerFavoriteCard;
import net.darkkatrom.dkweather.colorpicker.util.ColorPickerHelper;
import net.darkkatrom.dkweather.colorpicker.util.ConfigColorPicker;
import net.darkkatrom.dkweather.utils.ColorUtil;
import net.darkkatrom.dkweather.utils.Config;
import net.darkkatrom.dkweather.utils.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerCardAdapter extends
        RecyclerView.Adapter<ColorPickerCardAdapter.CardViewHolder> {

    private static final int VIEW_TYPE_LIGHT_THEME = 0;
    private static final int VIEW_TYPE_DARK_THEME  = 1;

    private Context mContext;
    private List<ColorPickerCard> mColorPickerCards;
    private int mInitialColor;
    private int mNewColor;
    private int[] mFavoriteColors;

    private OnCardClickedListener mOnCardClickedListener;

    public interface OnCardClickedListener {
        public void onCardClicked(int color, int position);
        public void onCardActionApplyClicked(int color);
        public void onColorCardActionFavoriteClicked(ColorPickerCard card, int position, boolean isFavorite);
        public void onFavoriteCardActionFavoriteClicked(ColorPickerCard card, int position, boolean addFavorite);
    }

    public ColorPickerCardAdapter(Context context, List<ColorPickerCard> items, int initialColor,
            int newColor, int[] favoriteColors) {
        super();

        mContext = context;
        if (items != null) {
            mColorPickerCards = items;
        } else {
            mColorPickerCards = new ArrayList<ColorPickerCard>();
        }
        mInitialColor = initialColor;
        mNewColor = newColor;
        mFavoriteColors = favoriteColors;
    }

    @Override
    public ColorPickerCardAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {

        View v = LayoutInflater.from(mContext).inflate(
                R.layout.color_picker_color_card, parent, false);
        CardViewHolder vh = new CardViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, final int position) {
        final ColorPickerCard card = mColorPickerCards.get(position);
        final boolean isFavoriteCard = card instanceof ColorPickerFavoriteCard;

        final int color = card.getColor();
        int cardBackgroundColor = isFavoriteCard && color == 0
                ? resolveDefaultCardBackgroundColor() : color;
        boolean needLightTheme = !ColorUtil.isColorDark(cardBackgroundColor);
        ColorStateList primaryTextColor = mContext.getColorStateList(needLightTheme
                ? R.color.primary_text_light_black : R.color.primary_text_dark_white);
        ColorStateList secondaryTextColor = mContext.getColorStateList(needLightTheme
                ? R.color.secondary_text_light_black : R.color.secondary_text_dark_white);
        ColorStateList rippleColor = mContext.getColorStateList(needLightTheme
                ? R.color.ripple_black : R.color.ripple_white);
        ColorStateList rippleColorRectangleMask = ColorStateList.valueOf(mContext.getColor(needLightTheme
                ? R.color.ripple_rectangle_mask_light : R.color.ripple_rectangle_mask_dark));

        final boolean isFavorite = isFavorite(color);
        boolean disableCardClick = color == mNewColor || (isFavoriteCard && color == 0);
        boolean disableActionApply = color == 0 || color == mInitialColor;
        String statusText = "";
        boolean showAddFavorite = true;
        if (!isFavoriteCard) {
            showAddFavorite = isFavorite ? false : true;
        } else {
            showAddFavorite = color == 0;
        }
        final boolean addFavorite = showAddFavorite;
        boolean hideActionFavorite = isFavoriteCard && !addFavorite && hideActionFavorite(color);

        holder.mTitle.setText(card.getTitle());
        holder.mSubtitle.setText(card.getSubtitle());

        holder.mCardView.setCardBackgroundColor(cardBackgroundColor);
        holder.mTitle.setTextColor(primaryTextColor);
        holder.mSubtitle.setTextColor(secondaryTextColor);
        holder.mActionApply.setTextColor(primaryTextColor);
        holder.mIconAddFavorite.setImageTintList(primaryTextColor);
        holder.mIconRemoveFavorite.setImageTintList(primaryTextColor);
        ((RippleDrawable) holder.mCardLayout.getBackground()).setColor(rippleColorRectangleMask);
        ((RippleDrawable) holder.mActionApply.getBackground()).setColor(rippleColorRectangleMask);
        ((RippleDrawable) holder.mActionFavorite.getBackground()).setColor(rippleColor);

        if (!hideActionFavorite) {
            holder.mActionFavorite.setVisibility(View.VISIBLE);
            holder.mIconAddFavorite.setVisibility(showAddFavorite ? View.VISIBLE : View.GONE);
            holder.mIconRemoveFavorite.setVisibility(showAddFavorite ? View.GONE : View.VISIBLE);
            holder.mActionFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCardClickedListener != null) {
                        if (isFavoriteCard) {
                            mOnCardClickedListener.onFavoriteCardActionFavoriteClicked(card, position, addFavorite);
                        } else {
                            mOnCardClickedListener.onColorCardActionFavoriteClicked(card, position, isFavorite);
                        }
                    }
                }
            });
        } else {
            holder.mActionFavorite.setVisibility(View.GONE);
            holder.mIconAddFavorite.setVisibility(View.GONE);
            holder.mIconRemoveFavorite.setVisibility(View.GONE);
            holder.mActionFavorite.setOnClickListener(null);
        }
        if (disableCardClick) {
            holder.mCardLayout.setOnClickListener(null);
            holder.mCardLayout.setClickable(false);
        } else {
            holder.mCardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCardClickedListener != null) {
                        mOnCardClickedListener.onCardClicked(color, position);
                    }
                }
            });
        }
        if (disableActionApply) {
            holder.mActionApply.setOnClickListener(null);
            holder.mActionApply.setEnabled(false);
        } else {
            holder.mActionApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCardClickedListener != null) {
                        mOnCardClickedListener.onCardActionApplyClicked(color);
                    }
                }
            });
            holder.mActionApply.setEnabled(true);
        }
        if (isFavoriteCard) {
            if (disableCardClick) {
                statusText = mContext.getResources().getString(color == 0
                        ? R.string.color_picker_favorite_card_status_empty
                        : R.string.color_picker_favorite_card_status_card_click_disabled);
            }
            if (disableActionApply) {
                if (statusText.isEmpty()) {
                    statusText = mContext.getResources().getString(color == mInitialColor
                            ? R.string.color_picker_favorite_card_status_action_apply_disabled_initial_color
                            : R.string.color_picker_favorite_card_status_action_apply_disabled);
                } else {
                    statusText += "\n" + mContext.getResources().getString(color == mInitialColor
                            ? R.string.color_picker_favorite_card_status_action_apply_disabled_initial_color
                            : R.string.color_picker_favorite_card_status_action_apply_disabled);
                }
            }
        } else {
            if (disableCardClick) {
                statusText = mContext.getResources().getString(
                        R.string.color_picker_color_card_status_card_click_disabled);
            }
            if (disableActionApply) {
                if (statusText.isEmpty()) {
                    statusText = mContext.getResources().getString(color == mInitialColor
                            ? R.string.color_picker_color_card_status_action_apply_disabled_initial_color
                            : R.string.color_picker_color_card_status_action_apply_disabled);
                } else {
                    statusText += "\n" + mContext.getResources().getString(color == mInitialColor
                            ? R.string.color_picker_color_card_status_action_apply_disabled_initial_color
                            : R.string.color_picker_color_card_status_action_apply_disabled);
                }
            }
        }
        if (statusText.isEmpty()) {
//            holder.mStatusText.setVisibility(View.INVISIBLE);
        } else {
//            holder.mStatusText.setVisibility(View.VISIBLE);
        }
//        holder.mStatusText.setText(statusText);
    }

    @Override
    public int getItemCount() {
        return mColorPickerCards.size();
    }

    private boolean isFavorite(int color) {
        boolean isFavorite = false;
        for (int i = 0; i < ColorPickerFragment.NUM_MAX_FAVORITES; i++) {
            if (mFavoriteColors[i] == color) {
                isFavorite = true;
                break;
            }
        }
        return isFavorite;
    }

    private boolean hideActionFavorite(int color) {
        int allowDeleteType = ConfigColorPicker.getAllowDeleteType(mContext);
        if (allowDeleteType == 0) {
            return false;
        } else if (allowDeleteType == 1) {
            return ColorPickerHelper.getColorTitle(mContext, color).isEmpty();
        } else {
            return true;
        }
    }

    private int resolveDefaultCardBackgroundColor() {
        return ThemeUtil.getColorFromThemeAttribute(mContext, R.attr.colorBackgroundFloating);
    }

    public void setNewColor(int newColor) {
        mNewColor = newColor;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CardView mCardView;
        public View mCardLayout;
        public TextView mTitle;
        public TextView mSubtitle;
        public TextView mStatusText;
        public TextView mActionApply;
        public View mActionFavorite;
        public ImageView mIconAddFavorite;
        public ImageView mIconRemoveFavorite;

        public CardViewHolder(View v) {
            super(v);

            mView = v;
            mCardView = (CardView) v.findViewById(R.id.color_picker_color_card);
            mCardLayout = v.findViewById(R.id.color_picker_color_card_content_layout);
            mTitle = (TextView) v.findViewById(R.id.color_picker_color_card_header_title);
            mSubtitle = (TextView) v.findViewById(R.id.color_picker_color_card_header_subtitle);
            mStatusText = (TextView) v.findViewById(R.id.color_picker_color_card_status_text);
            mActionApply = (TextView) v.findViewById(R.id.color_picker_color_card_action_apply_color);
            mActionFavorite = v.findViewById(R.id.color_picker_color_card_button_favorite);
            mIconAddFavorite = (ImageView) v.findViewById(R.id.color_picker_color_card_icon_add_favorite);
            mIconRemoveFavorite = (ImageView) v.findViewById(R.id.color_picker_color_card_icon_remove_favorite);
        }
    }

    public void setOnCardClickedListener(OnCardClickedListener onItemClickedListener) {
        mOnCardClickedListener = onItemClickedListener;
    }
}
