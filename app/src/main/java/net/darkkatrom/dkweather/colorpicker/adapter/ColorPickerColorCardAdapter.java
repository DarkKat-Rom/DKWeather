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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.colorpicker.model.ColorPickerColorCard;
import net.darkkatrom.dkweather.colorpicker.widget.ColorViewButton;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerColorCardAdapter extends
        RecyclerView.Adapter<ColorPickerColorCardAdapter.ViewHolder> {

    private Context mContext;
    private int mBorderColor;
    private List<ColorPickerColorCard> mColorPickerColorCards;

    private OnColorCardClickedListener mOnColorCardClickedListener;

    public interface OnColorCardClickedListener {
        public void onColorCardClicked(int color);
    }

    public ColorPickerColorCardAdapter(Context context, List<ColorPickerColorCard> items) {
        super();

        mContext = context;
        TypedValue tv = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.colorControlHighlight, tv, true);
        if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            mBorderColor = tv.data;
        } else {
            mBorderColor = mContext.getColor(tv.resourceId);
        }
        if (items != null) {
            mColorPickerColorCards = items;
        } else {
            mColorPickerColorCards = new ArrayList<ColorPickerColorCard>();
        }
    }

    @Override
    public ColorPickerColorCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {
        View v = LayoutInflater.from(mContext).inflate(
                R.layout.color_picker_color_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ColorPickerColorCard card = mColorPickerColorCards.get(position);
        String subtitle = card.getSubtitle();
        final int color = mContext.getColor(card.getColorResId());

        holder.mTitle.setText(card.getTitleResId());
        holder.mSubtitle.setText(subtitle);
        holder.mPreview.setColor(color);
        holder.mPreview.setBorderColor(mBorderColor);
        if (color != 0) {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnColorCardClickedListener != null) {
                        mOnColorCardClickedListener.onColorCardClicked(color);
                    }
                }
            });
            holder.mView.setOnLongClickListener(null);
            holder.mCardView.setCardBackgroundColor(color);
            holder.mPreview.setShowFavoriteIcon(false);
        } else {
            holder.mView.setOnClickListener(null);
            holder.mView.setOnLongClickListener(null);
            holder.mCardView.setCardBackgroundColor(resolveDefaultCardBackgroundColor());
            holder.mPreview.setShowFavoriteIcon(true);
        }
        if (subtitle.isEmpty()) {
            holder.mSubtitle.setVisibility(View.INVISIBLE);
        } else {
            holder.mSubtitle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mColorPickerColorCards.size();
    }

    private int resolveDefaultCardBackgroundColor() {
        int color = 0;
        TypedValue tv = new TypedValue();

        mContext.getTheme().resolveAttribute(R.attr.colorBackgroundFloating, tv, true);
        if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            color = tv.data;
        } else {
            color = mContext.getColor(tv.resourceId);
        }
        return color;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CardView mCardView;
        public TextView mTitle;
        public TextView mSubtitle;
        public ColorViewButton mPreview;

        public ViewHolder(View v) {
            super(v);

            mView = v;
            mCardView = (CardView) v.findViewById(R.id.color_picker_color_card);
            mTitle = (TextView) v.findViewById(R.id.color_picker_color_card_title);
            mSubtitle = (TextView) v.findViewById(R.id.color_picker_color_card_subtitle);
            mPreview = (ColorViewButton) v.findViewById(R.id.color_picker_color_card_preview);
        }
    }

    public void setOnColorCardClickedListener(OnColorCardClickedListener onItemClickedListener) {
        mOnColorCardClickedListener = onItemClickedListener;
    }
}
