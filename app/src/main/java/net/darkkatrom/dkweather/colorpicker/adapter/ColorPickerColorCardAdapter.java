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

import android.view.ContextThemeWrapper;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.colorpicker.fragment.ColorPickerFragmentNew;
import net.darkkatrom.dkweather.colorpicker.model.ColorPickerColorCard;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerColorCardAdapter extends
        RecyclerView.Adapter<ColorPickerColorCardAdapter.ViewHolder> {

    private static final int VIEW_TYPE_LIGHT_THEME = 0;
    private static final int VIEW_TYPE_DARK_THEME  = 1;

    private Context mContext;
    private List<ColorPickerColorCard> mColorPickerColorCards;
    private int mNewColor;
    private int[] mFavoriteColors;

    private OnColorCardClickedListener mOnColorCardClickedListener;

    public interface OnColorCardClickedListener {
        public void onColorCardActionSetClicked(int color);
        public void onColorCardActionFavoriteClicked(ColorPickerColorCard card, boolean isFavorite);
    }

    public ColorPickerColorCardAdapter(Context context, List<ColorPickerColorCard> items, int newColor,
            int[] favoriteColors) {
        super();

        mContext = context;
        if (items != null) {
            mColorPickerColorCards = items;
        } else {
            mColorPickerColorCards = new ArrayList<ColorPickerColorCard>();
        }
        mNewColor = newColor;
        mFavoriteColors = favoriteColors;
    }

    @Override
    public ColorPickerColorCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, viewType == VIEW_TYPE_LIGHT_THEME
                ? R.style.AppThemeLight : R.style.AppThemeDark);
        View v = LayoutInflater.from(ctw).inflate(
                R.layout.color_picker_color_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ColorPickerColorCard card = mColorPickerColorCards.get(position);
        final int color = mContext.getColor(card.getColorResId());
        final boolean isFavorite = isFavorite(color);
        int favoriteIconResId = isFavorite
                ? R.drawable.ic_action_remove_favorite : R.drawable.ic_action_add_favorite;

        holder.mTitle.setText(card.getTitleResId());
        holder.mSubtitle.setText(card.getSubtitle());
        holder.mCardView.setCardBackgroundColor(color);
        if (color == mNewColor) {
            holder.mActionSet.setOnClickListener(null);
            holder.mActionSet.setEnabled(false);
        } else {
            holder.mActionSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnColorCardClickedListener != null) {
                        mOnColorCardClickedListener.onColorCardActionSetClicked(color);
                    }
                }
            });
            holder.mActionSet.setEnabled(true);
        }
        holder.mActionFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnColorCardClickedListener != null) {
                    mOnColorCardClickedListener.onColorCardActionFavoriteClicked(card, isFavorite);
                }
            }
        });
        holder.mActionFavorite.setImageResource(favoriteIconResId);
    }

    @Override
    public int getItemCount() {
        return mColorPickerColorCards.size();
    }

    @Override
    public int getItemViewType(int position) {
        boolean useLightTheme = mColorPickerColorCards.get(position).needLightTheme();
        return useLightTheme ? VIEW_TYPE_LIGHT_THEME : VIEW_TYPE_DARK_THEME;
    }

    private boolean isFavorite(int color) {
        boolean isFavorite = false;
        for (int i = 0; i < ColorPickerFragmentNew.NUM_MAX_FAVORITES; i++) {
            if (mFavoriteColors[i] == color) {
                isFavorite = true;
                break;
            }
        }
        return isFavorite;
    }

    public void setNewColor(int newColor) {
        mNewColor = newColor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CardView mCardView;
        public TextView mTitle;
        public TextView mSubtitle;
        public TextView mActionSet;
        public ImageView mActionFavorite;

        public ViewHolder(View v) {
            super(v);

            mView = v;
            mCardView = (CardView) v.findViewById(R.id.color_picker_color_card);
            mTitle = (TextView) v.findViewById(R.id.color_picker_color_card_header_title);
            mSubtitle = (TextView) v.findViewById(R.id.color_picker_color_card_header_subtitle);
            mActionSet = (TextView) v.findViewById(R.id.color_picker_color_card_action_set_color);
            mActionFavorite = (ImageView) v.findViewById(R.id.color_picker_color_card_icon_favorite);
        }
    }

    public void setOnColorCardClickedListener(OnColorCardClickedListener onItemClickedListener) {
        mOnColorCardClickedListener = onItemClickedListener;
    }
}
