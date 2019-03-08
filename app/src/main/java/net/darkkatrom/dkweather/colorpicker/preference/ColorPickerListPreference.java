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
import net.darkkatrom.dkweather.utils.GraphicsUtil;
import net.darkkatrom.dkweather.utils.ThemeUtil;

public class ColorPickerListPreference extends ListPreference implements
        ColorPickerListAdapter.OnItemClickedListener {

    public static final String TAG = "ColorPickerListPreference";

    private CharSequence[] mListEntries;
    private CharSequence[] mListEntryValues;
    private CharSequence[] mEntryColors;

    private RecyclerView mRecyclerView = null;
    private ColorPickerListAdapter mAdapter = null;
    private int mClickedDialogItem = -1;

    private View mCustomTitleLayout = null;
    private TextView mCustomTitleText = null;

    private int mSelectedColor = 0;
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

        if (mClickedDialogItem == -1) {
            mSelectedColor = convertToColorInt((String) getEntryColor());
            mClickedDialogItem = findIndexOfValue(getValue());
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
                dividerBottom, mListEntries, mEntryColors, mClickedDialogItem);
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
        ((AlertDialog) getDialog()).getButton(
                AlertDialog.BUTTON_POSITIVE).setEnabled(findIndexOfValue(getValue()) != mClickedDialogItem);
        mRecyclerView.getLayoutManager().scrollToPosition(mClickedDialogItem);
        mDialogTitleBgColor = ThemeUtil.getPickColorListDlgTitleBgColor(getContext(),
                mSelectedColor);
        mDialogTitleTextColor = ThemeUtil.getPickColorListDlgTitleTextColor(getContext(),
                mSelectedColor);
        updateDialogTitleColors(false);
    }

    @Override
    public void onItemClicked(int position, int color) {
        mClickedDialogItem = position;
        ((AlertDialog) getDialog()).getButton(
                AlertDialog.BUTTON_POSITIVE).setEnabled(findIndexOfValue(getValue()) != mClickedDialogItem);
        mSelectedColor = color;
        updateDialogTitleColors(true);
        // As CheckedTextView already animates checked state changes,
        // call 'notifyDataSetChanged()' to disable the internal animations.
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        ((ColorPickerListAdapter) mRecyclerView.getAdapter()).setOnItemClickedListener(null);
        if (positiveResult && mClickedDialogItem >= 0 && mListEntryValues != null) {
            String color = convertToARGB(mSelectedColor);
            int listIndex = findIndexOfColor(color);
            String value = mListEntryValues[listIndex].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
        if (!positiveResult) {
            mSelectedColor = convertToColorInt((String) getEntryColor());
        }
        // As the header collapsed/expanded states are not saved for now,
        // possibly the clicked dialog item has the wrong index when opening the
        // dialog again, so set the index to match the index of the current value
        mClickedDialogItem = findIndexOfValue(getValue());
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

    private void updateDialogTitleColors(boolean animate) {
        int newBgColor = ThemeUtil.getPickColorListDlgTitleBgColor(getContext(),
                mSelectedColor);
        int newTextColor = ThemeUtil.getPickColorListDlgTitleTextColor(getContext(),
                mSelectedColor);

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
        myState.clickedDialogItem = mClickedDialogItem;
        myState.selectedColor = mSelectedColor;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null) {
            super.onRestoreInstanceState(state);
            return;
        }
         
        SavedState myState = (SavedState) state;
        mClickedDialogItem = myState.clickedDialogItem;
        mSelectedColor = myState.selectedColor;
        super.onRestoreInstanceState(myState.getSuperState());
    }
    
    private static class SavedState extends BaseSavedState {
        int clickedDialogItem;
        int selectedColor;

        public SavedState(Parcel source) {
            super(source);
            clickedDialogItem = source.readInt();
            selectedColor = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(clickedDialogItem);
            dest.writeInt(selectedColor);
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
