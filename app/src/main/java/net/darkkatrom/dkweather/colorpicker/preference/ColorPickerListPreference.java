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

package net.darkkatrom.dkweather.colorpicker.preference;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.ListPreference;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.colorpicker.adapter.ColorPickerListAdapter;
import net.darkkatrom.dkweather.colorpicker.animator.ColorPickerDialogAnimator;
import net.darkkatrom.dkweather.colorpicker.animator.ColorPickerListItemAnimator;
import net.darkkatrom.dkweather.colorpicker.model.ColorPickerListColorItem;
import net.darkkatrom.dkweather.colorpicker.model.ColorPickerListHeaderItem;
import net.darkkatrom.dkweather.colorpicker.model.ColorPickerListItem;
import net.darkkatrom.dkweather.utils.GraphicsUtil;
import net.darkkatrom.dkweather.utils.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerListPreference extends ListPreference implements
        ColorPickerListAdapter.OnItemClickedListener {

    public static final String TAG = "ColorPickerListPreference";

    public static int HEADER_INDEX_DARKKAT  = 0;
    public static int HEADER_INDEX_MATERIAL = 1;
    public static int HEADER_INDEX_HOLO     = 2;
    public static int HEADER_INDEX_RGB      = 3;

    public static int CHECKED_COLOR_ITEM_POSITION_UNKNOWN      = -2;
    public static int CHECKED_COLOR_ITEM_POSITION_NOT_IN_LIST  = -1;

    private CharSequence[] mHeaderTitles;

    private CharSequence[] mDarkkatEntries;
    private CharSequence[] mMaterialEntries;
    private CharSequence[] mHoloEntries;
    private CharSequence[] mRGBEntries;

    private CharSequence[] mDarkkatValues;
    private CharSequence[] mMaterialValues;
    private CharSequence[] mHoloValues;
    private CharSequence[] mRGBValues;

    private CharSequence[] mDarkkatColors;
    private CharSequence[] mMaterialColors;
    private CharSequence[] mHoloColors;
    private CharSequence[] mRGBColors;

    private CharSequence[] mListEntries;
    private CharSequence[] mListEntryValues;
    private CharSequence[] mEntryColors;

    private RecyclerView mRecyclerView = null;
    private ColorPickerListAdapter mAdapter = null;

    private List<ColorPickerListItem> mColorPickerListItems;

    private View mCustomTitleLayout = null;
    private TextView mCustomTitleText = null;

    private int mCheckedColorItemPosition = CHECKED_COLOR_ITEM_POSITION_UNKNOWN;
    private int mCheckedColor = 0;

    private int mDialogTitleBgColor = 0;
    private int mDialogTitleTextColor = 0;

    public ColorPickerListPreference(Context context) {
        this(context, null);
    }

    public ColorPickerListPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.preferenceStyle);
    }

    public ColorPickerListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ColorPickerListPreference(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mHeaderTitles = context.getResources().getStringArray(R.array.theme_color_list_header_titles);

        mDarkkatEntries = context.getResources().getStringArray(R.array.theme_color_list_darkkat_entries);
        mMaterialEntries = context.getResources().getStringArray(R.array.theme_color_list_material_entries);
        mHoloEntries = context.getResources().getStringArray(R.array.theme_color_list_holo_entries);
        mRGBEntries = context.getResources().getStringArray(R.array.theme_color_list_rgb_entries);

        mDarkkatValues = context.getResources().getStringArray(R.array.theme_color_list_darkkat_values);
        mMaterialValues = context.getResources().getStringArray(R.array.theme_color_list_material_values);
        mHoloValues = context.getResources().getStringArray(R.array.theme_color_list_holo_values);
        mRGBValues = context.getResources().getStringArray(R.array.theme_color_list_rgb_values);

        mDarkkatColors = context.getResources().getStringArray(R.array.theme_color_list_darkkat_colors);
        mMaterialColors = context.getResources().getStringArray(R.array.theme_color_list_material_colors);
        mHoloColors = context.getResources().getStringArray(R.array.theme_color_list_holo_colors);
        mRGBColors = context.getResources().getStringArray(R.array.theme_color_list_rgb_colors);

        mListEntries = context.getResources().getStringArray(R.array.theme_color_list_entries);
        mListEntryValues = context.getResources().getStringArray(R.array.theme_color_list_values);
        mEntryColors = context.getResources().getStringArray(R.array.theme_color_list_entry_colors);

        setPositiveButtonText(R.string.dlg_ok);
        setNegativeButtonText(R.string.dlg_cancel);
        setLayoutResource(R.layout.preference_color_picker);
        setWidgetLayoutResource(R.layout.preference_widget_color_picker_list);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        if (view != null) {
            int entryColor = getEntryColor() != null
                    ? convertToColorInt(getEntryColor().toString()) : Color.TRANSPARENT;
            ImageView icon = (ImageView) view.findViewById(R.id.color_picker_list_widget_icon);
            TextView hex = (TextView) view.findViewById(R.id.color_picker_list_widget_hex);
            if (entryColor != Color.TRANSPARENT) {
                icon.setImageTintList(ColorStateList.valueOf(entryColor));
            }
            hex.setText(getEntryColor());
        }
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        if (mListEntries == null || mListEntryValues == null || mEntryColors == null) {
            throw new IllegalStateException(
                    "ColorPickerListPreference requires an entries array and an entryValues array and an entryColors array.");
        }

        if (mCheckedColorItemPosition == CHECKED_COLOR_ITEM_POSITION_UNKNOWN) {
            mCheckedColor = convertToColorInt((String) getEntryColor());
        }

        if (mColorPickerListItems == null) {
            buildListItems();
        }

        if (mCheckedColorItemPosition == CHECKED_COLOR_ITEM_POSITION_UNKNOWN) {
            if (isCheckedColorInList()) {
                mCheckedColorItemPosition = getCheckedColorPositionInList();
            }
        }

        mCustomTitleLayout = LayoutInflater.from(builder.getContext()).inflate(
                R.layout.color_picker_dialog_list_title, null, false);
        mCustomTitleText = (TextView) mCustomTitleLayout.findViewById(R.id.title);
        View customContent = LayoutInflater.from(builder.getContext()).inflate(
                R.layout.color_picker_dialog_list, null, false);
        View dividerTop = customContent.findViewById(R.id.color_picker_dialog_list_divider_top);
        View dividerBottom = customContent.findViewById(R.id.color_picker_dialog_list_divider_bottom);
        mRecyclerView = (RecyclerView) customContent.findViewById(R.id.color_picker_dialog_list);
        mAdapter = new ColorPickerListAdapter(getContext(), dividerTop,
                dividerBottom, mColorPickerListItems);

        mRecyclerView.setItemAnimator(new ColorPickerListItemAnimator());
        mCustomTitleText.setText(getDialogTitle());
        mAdapter.setOnItemClickedListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        builder.setCustomTitle(mCustomTitleLayout);
        builder.setView(customContent);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        int intValue = convertToColorInt((String) getEntryColor());
        boolean buttonPositiveEnabled = mCheckedColor != intValue;
        ((AlertDialog) getDialog()).getButton(
                AlertDialog.BUTTON_POSITIVE).setEnabled(buttonPositiveEnabled);
        if (isCheckedColorInList()) {
            mRecyclerView.getLayoutManager().scrollToPosition(mCheckedColorItemPosition);
        }
        mDialogTitleBgColor = ThemeUtil.getPickColorListDlgTitleBgColor(getContext(),
                mCheckedColor);
        mDialogTitleTextColor = ThemeUtil.getPickColorListDlgTitleTextColor(getContext(),
                mCheckedColor);
        updateDialogTitleColors(false);
    }

    @Override
    public void onItemClicked(ColorPickerListItem item, int position) {
        if (item.getViewType() == ColorPickerListItem.VIEW_TYPE_HEADER_ITEM) {
            int fromIndex = position + 1;
            int itemCount = item.getColorItemsCount();
            if (item.isExpanded()) {
                removeColorItems(fromIndex, itemCount);
            } else {
                CharSequence[] entries = getEntriesForHeader(item.getHeaderIndex());
                CharSequence[] colors = getColorsForHeader(item.getHeaderIndex());
                addColorItems(fromIndex, entries, colors);
            }
            item.toggleExpanded();
            mAdapter.notifyItemChanged(position);
            mCheckedColorItemPosition = getCheckedColorPositionInList();
        } else {
            if (isCheckedColorInList() && getCheckedColorPositionInList()
                    != CHECKED_COLOR_ITEM_POSITION_NOT_IN_LIST) {
                mColorPickerListItems.get(getCheckedColorPositionInList()).setChecked(false);
            }
            mColorPickerListItems.get(position).setChecked(true);
            mCheckedColor = item.getColor();
            int intValue = convertToColorInt((String) getEntryColor());
            boolean buttonPositiveEnabled = mCheckedColor != intValue;
            ((AlertDialog) getDialog()).getButton(
                    AlertDialog.BUTTON_POSITIVE).setEnabled(buttonPositiveEnabled);
            updateDialogTitleColors(true);
            // As CheckedTextView already animates checked state changes,
            // call 'notifyDataSetChanged()' to disable the internal animations.
            mCheckedColorItemPosition = position;
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        ((ColorPickerListAdapter) mRecyclerView.getAdapter()).setOnItemClickedListener(null);
        if (positiveResult && mCheckedColorItemPosition >= 0 && mListEntryValues != null) {
            String color = convertToARGB(mCheckedColor);
            int listIndex = findIndexOfColor(color);
            String value = mListEntryValues[listIndex].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
        if (!positiveResult) {
            // Reset the color item checked states
            if (mCheckedColorItemPosition != CHECKED_COLOR_ITEM_POSITION_NOT_IN_LIST) {
                mColorPickerListItems.get(mCheckedColorItemPosition).setChecked(false);
            }
            mCheckedColor = convertToColorInt((String) getEntryColor());
            mCheckedColorItemPosition = getCheckedColorPositionInList();
            if (mCheckedColorItemPosition != CHECKED_COLOR_ITEM_POSITION_NOT_IN_LIST) {
                mColorPickerListItems.get(mCheckedColorItemPosition).setChecked(true);
            }
        }
    }

    @Override
    public void setEntries(CharSequence[] entries) {
        Log.w(TAG, "Custom entries are not supported, ignoring!");
    }

    @Override
    public void setEntries(int entriesResId) {
        Log.w(TAG, "Custom entries are not supported, ignoring!");
    }

    @Override
    public CharSequence[] getEntries() {
        return mListEntries;
    }

    @Override
    public void setEntryValues(CharSequence[] entryValues) {
        Log.w(TAG, "Custom entry values are not supported, ignoring!");
    }

    @Override
    public void setEntryValues(int entryValuesResId) {
        Log.w(TAG, "Custom entry values are not supported, ignoring!");
    }

    public CharSequence[] getEntryValues() {
        return mListEntryValues;
    }

    @Override
    public CharSequence getEntry() {
        int index = getListValueIndex();
        return index >= 0 && mListEntries != null ? mListEntries[index] : null;
    }

    public CharSequence[] getEntryColors() {
        return mEntryColors;
    }

    public CharSequence getEntryColor() {
        int index = findIndexOfValue(getValue());
        return index >= 0 && mEntryColors != null ? mEntryColors[index] : null;
    }

    @Override
    public void setValueIndex(int index) {
        if (mListEntryValues != null) {
            setValue(mListEntryValues[index].toString());
        }
    }

    private int getListValueIndex() {
        return findIndexOfValue(getValue());
    }

    @Override
    public int findIndexOfValue(String value) {
        if (value != null && mListEntryValues != null) {
            for (int i = mListEntryValues.length - 1; i >= 0; i--) {
                if (mListEntryValues[i].equals(value)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int findIndexOfColor(String color) {
        if (color != null && mEntryColors != null) {
            for (int i = mEntryColors.length - 1; i >= 0; i--) {
                if (mEntryColors[i].equals(color)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void buildListItems() {
        mColorPickerListItems = new ArrayList<ColorPickerListItem>();

        for (int i = 0; i < mHeaderTitles.length; i++) {
            addHeaderItem(i, getEntriesForHeader(i));
            addColorItems(-1, getEntriesForHeader(i), getColorsForHeader(i));
        }
    }

    private void addHeaderItem(int headerIndex, CharSequence[] entries) {
        ColorPickerListItem headerItem = new ColorPickerListHeaderItem(headerIndex,
                mHeaderTitles[headerIndex], true, mColorPickerListItems.size(), entries.length,
                        ColorPickerListItem.VIEW_TYPE_HEADER_ITEM);
        mColorPickerListItems.add(headerItem);
    }

    private void addColorItems(int startIndex, CharSequence[] entries, CharSequence[] colors) {
        ColorPickerListItem colorItem = null;
        String CheckedColor = convertToARGB(mCheckedColor);
        int fromIndex = startIndex;
        int itemCount = entries.length;
        for (int i = 0; i < entries.length; i++) {
            colorItem = new ColorPickerListColorItem(entries[i], colors[i],
                    CheckedColor.equals(colors[i]), ColorPickerListItem.VIEW_TYPE_COLOR_ITEM);
            if (startIndex != -1 && mAdapter != null) {
                mColorPickerListItems.add(fromIndex + i, colorItem);
            } else {
                mColorPickerListItems.add(colorItem);
            }
        }
        if (startIndex != -1 && mAdapter != null) {
            mAdapter.notifyItemRangeInserted(fromIndex, itemCount);
            mAdapter.notifyItemRangeChanged(0, mColorPickerListItems.size());
        }
    }

    private void removeColorItems(int startIndex, int itemCount) {
        int fromIndex = startIndex;
        for (int i = 0; i < itemCount; i++) {
            mColorPickerListItems.remove(fromIndex);
        }
        mAdapter.notifyItemRangeRemoved(fromIndex, itemCount);
        mAdapter.notifyItemRangeChanged(0, mColorPickerListItems.size());
    }

    private CharSequence[] getEntriesForHeader(int headerIndex) {
        CharSequence[] entries = null;
        if (headerIndex == HEADER_INDEX_DARKKAT) {
            entries = mDarkkatEntries;
        } else if (headerIndex == HEADER_INDEX_MATERIAL) {
            entries = mMaterialEntries;
        } else if (headerIndex == HEADER_INDEX_HOLO) {
            entries = mHoloEntries;
        } else {
            entries = mRGBEntries;
        }
        return entries;
    }

    private CharSequence[] getColorsForHeader(int headerIndex) {
        CharSequence[] entries = null;
        CharSequence[] colors = null;
        if (headerIndex == HEADER_INDEX_DARKKAT) {
            colors = mDarkkatColors;
        } else if (headerIndex == HEADER_INDEX_MATERIAL) {
            colors = mMaterialColors;
        } else if (headerIndex == HEADER_INDEX_HOLO) {
            colors = mHoloColors;
        } else {
            colors = mRGBColors;
        }
        return colors;
    }

    private boolean isCheckedColorInList() {
        boolean isInList = false;
        for (int i = 0; i < mColorPickerListItems.size(); i++) {
            if (mColorPickerListItems.get(i).getViewType()
                    == ColorPickerListItem.VIEW_TYPE_COLOR_ITEM) {
                if (mColorPickerListItems.get(i).getColor() == mCheckedColor) {
                    isInList = true;
                }
            }
        }
        return isInList;
    }

    /**
     * Temporary helper methode to obtain the position of the item holding the checked color
     * in the current adapter list
     * 
     * returns the position in list if an item was found holding the checked color,
     * otherwise CHECKED_COLOR_ITEM_POSITION_NOT_IN_LIST (-1)
     */
    private int getCheckedColorPositionInList() {
        int position = CHECKED_COLOR_ITEM_POSITION_NOT_IN_LIST;
        for (int i = 0; i < mColorPickerListItems.size(); i++) {
            if (mColorPickerListItems.get(i).getViewType()
                    == ColorPickerListItem.VIEW_TYPE_COLOR_ITEM) {
                if (mColorPickerListItems.get(i).getColor() == mCheckedColor) {
                    position = i;
                }
            }
        }
        return position;
    }

    private void updateDialogTitleColors(boolean animate) {
        int newBgColor = ThemeUtil.getPickColorListDlgTitleBgColor(getContext(),
                mCheckedColor);
        int newTextColor = ThemeUtil.getPickColorListDlgTitleTextColor(getContext(),
                mCheckedColor);

        if (animate) {
            if (mDialogTitleBgColor != newBgColor) {
                boolean animateTextColor = mDialogTitleTextColor != newTextColor;
                ColorPickerDialogAnimator.animateColorTransition(mDialogTitleBgColor, newBgColor,
                        mDialogTitleTextColor, newTextColor, mCustomTitleLayout,
                        animateTextColor ? mCustomTitleText : null);
                mDialogTitleBgColor = newBgColor;
                mDialogTitleTextColor = newTextColor;
            } else {
                mDialogTitleBgColor = newBgColor;
                mDialogTitleTextColor = newTextColor;
                mCustomTitleLayout.setBackgroundColor(mDialogTitleBgColor);
                mCustomTitleText.setTextColor(mDialogTitleTextColor);
            }
        } else {
            mDialogTitleBgColor = newBgColor;
            mDialogTitleTextColor = newTextColor;
            mCustomTitleLayout.setBackgroundColor(mDialogTitleBgColor);
            mCustomTitleText.setTextColor(mDialogTitleTextColor);
        }

    }

    /**
     * For custom purposes. Not used by ColorPickerPreferrence
     * 
     * @param color
     * @author Unknown
     */
    public static String convertToARGB(int color) {
        return GraphicsUtil.convertToARGB(color);
    }

    /**
     * Converts a aarrggbb- or rrggbb color string to a color int
     * 
     * @param argb
     * @throws NumberFormatException
     * @author Unknown
     */
    public static int convertToColorInt(String argb) {
        return GraphicsUtil.convertToColorInt(argb);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState myState = new SavedState(superState);
        myState.checkedColorItemPosition = mCheckedColorItemPosition;
        myState.checkedColor = mCheckedColor;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null) {
            super.onRestoreInstanceState(state);
            return;
        }
         
        SavedState myState = (SavedState) state;
        mCheckedColorItemPosition = myState.checkedColorItemPosition;
        mCheckedColor = myState.checkedColor;
        super.onRestoreInstanceState(myState.getSuperState());
    }
    
    private static class SavedState extends BaseSavedState {
        int checkedColorItemPosition;
        int checkedColor;

        public SavedState(Parcel source) {
            super(source);
            checkedColorItemPosition = source.readInt();
            checkedColor = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(checkedColorItemPosition);
            dest.writeInt(checkedColor);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
