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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.colorpicker.model.ColorPickerListItem;
import net.darkkatrom.dkweather.colorpicker.widget.ColorPreview;
import net.darkkatrom.dkweather.utils.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerListAdapter extends RecyclerView.Adapter<ColorPickerListAdapter.ListViewHolder> {

    private Context mContext;
    private View mDividerTop;
    private View mDividerBottom;
    private boolean mDividerTopVisible = true;
    private boolean mDividerBottomVisible = false;
    private int mBorderColor;
    private List<ColorPickerListItem> mColorPickerListItems;

    private OnItemClickedListener mOnItemClickedListener;

    public interface OnItemClickedListener {
        public void onItemClicked(ColorPickerListItem item, int position);
    }

    public ColorPickerListAdapter(Context context, View dividerTop, View dividerBottom,
            List<ColorPickerListItem> items) {
        super();

        mContext = context;
        mDividerTop = dividerTop;
        mDividerBottom = dividerBottom;
        mBorderColor = ThemeUtil.getDefaultHighlightColor(mContext);
        if (items == null) {
            mColorPickerListItems = new ArrayList<ColorPickerListItem>();
        } else {
            mColorPickerListItems = items;
        }
    }

    @Override
    public ColorPickerListAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        ListViewHolder vh = null;
        if (viewType == ColorPickerListItem.VIEW_TYPE_HEADER_ITEM) {
            v = LayoutInflater.from(mContext).inflate(
                    R.layout.color_picker_dialog_list_header, parent, false);
            vh = new ListHeaderViewHolder(v);
        } else {
            v = LayoutInflater.from(mContext).inflate(
                    R.layout.color_picker_dialog_list_item, parent, false);
            vh = new ListItemViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, final int position) {
        final ColorPickerListItem item = mColorPickerListItems.get(position);
        if (item.getViewType() == ColorPickerListItem.VIEW_TYPE_HEADER_ITEM) {
            if (position == 0) {
                holder.mHeaderDivider.setVisibility(View.GONE);
            } else {
                int paddingStart = holder.mView.getPaddingStart();
                int paddingTop = mContext.getResources().getDimensionPixelOffset(
                        R.dimen.color_picker_dialog_list_header_padding_top);
                int paddingEnd = holder.mView.getPaddingEnd();
                holder.mView.setPaddingRelative(paddingStart, paddingTop, paddingEnd, 0);
            }
            holder.mHeaderTitle.setText(item.getTitle());
            ((RippleDrawable) holder.mHeaderButtonFrame.getBackground()).setColor(getMaskedRippleColor());
            holder.mHeaderButtonFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickedListener != null) {
                        mOnItemClickedListener.onItemClicked(item, position);
                    }
                }
            });
            holder.mHeaderButton.setRotationX(item.isExpanded() ? 0 : 180);
        } else {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickedListener != null) {
                        mOnItemClickedListener.onItemClicked(item, position);
                    }
                }
            });
            if (holder.mTitle.isChecked() != item.isChecked()) {
                holder.mTitle.setChecked(item.isChecked());
            }
            holder.mTitle.setText(item.getTitle());
            holder.mPreview.setColor(item.getColor());
            holder.mPreview.setBorderColor(mBorderColor);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mColorPickerListItems.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mColorPickerListItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int firstVisibleItemPosition = llm.findFirstCompletelyVisibleItemPosition();
                int lastVisibleItemPosition = llm.findLastCompletelyVisibleItemPosition();
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (firstVisibleItemPosition == 0 && !mDividerTopVisible) {
                        mDividerTop.setVisibility(View.VISIBLE);
                        mDividerTopVisible = true;
                    }

                    if (lastVisibleItemPosition == mColorPickerListItems.size() - 1
                            && mDividerBottomVisible) {
                        mDividerBottom.setVisibility(View.INVISIBLE);
                        mDividerBottomVisible = false;
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItemPosition = llm.findFirstCompletelyVisibleItemPosition();
                int lastVisibleItemPosition = llm.findLastCompletelyVisibleItemPosition();
                if (firstVisibleItemPosition == 0 && mDividerTopVisible) {
                    mDividerTop.setVisibility(View.INVISIBLE);
                    mDividerTopVisible = false;
                } else if (firstVisibleItemPosition != 0 && !mDividerTopVisible) {
                    mDividerTop.setVisibility(View.VISIBLE);
                    mDividerTopVisible = true;
                }

                if (lastVisibleItemPosition == mColorPickerListItems.size() - 1
                            && mDividerBottomVisible) {
                    mDividerBottom.setVisibility(View.INVISIBLE);
                    mDividerBottomVisible = true;
                } else if (lastVisibleItemPosition != mColorPickerListItems.size() - 1
                            && !mDividerBottomVisible) {
                    mDividerBottom.setVisibility(View.VISIBLE);
                    mDividerBottomVisible = true;
                }
            }
        });
    }

    private ColorStateList getMaskedRippleColor() {
        int defaultRippleColor = ThemeUtil.getColorFromThemeAttribute(mContext, R.attr.colorControlHighlight);
        int maskedRippleColor = (137 << 24) | (defaultRippleColor & 0x00ffffff);
        return ColorStateList.valueOf(maskedRippleColor);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recyclerView.clearOnScrollListeners();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public View mHeaderDivider;
        public TextView mHeaderTitle;
        public View mHeaderButtonFrame;
        public ImageView mHeaderButton;
        public CheckedTextView mTitle;
        public ColorPreview mPreview;

        public ListViewHolder(View v) {
            super(v);

            mView = v;
        }
    }

    public static class ListHeaderViewHolder extends ListViewHolder {

        public ListHeaderViewHolder(View v) {
            super(v);

            mHeaderDivider = v.findViewById(R.id.color_picker_dialog_list_header_divider);
            mHeaderTitle = (TextView) v.findViewById(R.id.color_picker_dialog_list_header_title);
            mHeaderButtonFrame = v.findViewById(R.id.color_picker_dialog_list_header_button_frame);
            mHeaderButton = (ImageView) v.findViewById(R.id.color_picker_dialog_list_header_button);
        }
    }

    public static class ListItemViewHolder extends ListViewHolder {

        public ListItemViewHolder(View v) {
            super(v);

            mTitle = (CheckedTextView) v.findViewById(R.id.color_picker_dialog_list_item_title);
            mPreview = (ColorPreview) v.findViewById(R.id.color_picker_dialog_list_item_preview);
        }
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        mOnItemClickedListener = onItemClickedListener;
    }
}
