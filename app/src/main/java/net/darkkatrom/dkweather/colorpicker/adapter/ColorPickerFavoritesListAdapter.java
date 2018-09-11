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
import net.darkkatrom.dkweather.colorpicker.model.ColorPickerFavoritesListItem;
import net.darkkatrom.dkweather.colorpicker.util.ColorPickerHelper;
import net.darkkatrom.dkweather.colorpicker.widget.ColorViewButton;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerFavoritesListAdapter extends
        RecyclerView.Adapter<ColorPickerFavoritesListAdapter.ViewHolder> {

    private Context mContext;
    private int mBorderColor;
    private List<ColorPickerFavoritesListItem> mColorPickerFavoritesListItems;

    private OnFavoriteItemClickedListener mOnFavoriteItemClickedListener;

    public interface OnFavoriteItemClickedListener {
        public void onFavoriteItemClicked(int color);
        public void onFavoriteItemLongClicked(int position);
    }

    public ColorPickerFavoritesListAdapter(Context context, List<ColorPickerFavoritesListItem> items) {
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
            mColorPickerFavoritesListItems = items;
        } else {
            mColorPickerFavoritesListItems = new ArrayList<ColorPickerFavoritesListItem>();
        }
    }

    @Override
    public ColorPickerFavoritesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {
        View v = LayoutInflater.from(mContext).inflate(
                R.layout.color_picker_favorites_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ColorPickerFavoritesListItem item = mColorPickerFavoritesListItems.get(position);
        String subtitle = item.getSubtitle();
        int color = item.getColor();

        holder.mTitle.setText(item.getTitle());
        holder.mSubtitle.setText(subtitle);
        holder.mPreview.setColor(color);
        holder.mPreview.setBorderColor(mBorderColor);
        if (color != 0) {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnFavoriteItemClickedListener != null) {
                        mOnFavoriteItemClickedListener.onFavoriteItemClicked(item.getColor());
                    }
                }
            });
            holder.mView.setOnLongClickListener(null);
            holder.mPreview.setShowFavoriteIcon(false);
        } else {
            holder.mView.setOnClickListener(null);
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnFavoriteItemClickedListener != null) {
                        mOnFavoriteItemClickedListener.onFavoriteItemLongClicked(position);
                    }
                    return true;
                }
            });
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
        return mColorPickerFavoritesListItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mTitle;
        public TextView mSubtitle;
        public ColorViewButton mPreview;

        public ViewHolder(View v) {
            super(v);

            mView = v;
            mTitle = (TextView) v.findViewById(R.id.color_picker_favorites_list_item_title);
            mSubtitle = (TextView) v.findViewById(R.id.color_picker_favorites_list_item_subtitle);
            mPreview = (ColorViewButton) v.findViewById(R.id.color_picker_favorites_list_item_preview);
        }
    }

    public void setOnFavoriteItemClickedListener(OnFavoriteItemClickedListener onItemClickedListener) {
        mOnFavoriteItemClickedListener = onItemClickedListener;
    }
}
