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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.ListPreference;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.colorpicker.animator.ColorPickerListAnimator;
import net.darkkatrom.dkweather.colorpicker.util.ColorPickerHelper;
import net.darkkatrom.dkweather.utils.ThemeUtil;

public class ColorPickerListPreference extends ListPreference implements
        ColorPickerListAdapter.OnItemClickedListener {

    boolean mNeedEntryColors;
    private CharSequence[] mEntryColors;

    private RecyclerView mRecyclerView = null;
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

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.ColorPickerListPreference, defStyleAttr, defStyleRes);
            mNeedEntryColors = a.getBoolean(R.styleable.ColorPickerListPreference_needEntryColors, true);
            if (mNeedEntryColors) {
                mEntryColors = a.getTextArray(R.styleable.ColorPickerListPreference_entryColors);
            } else {
                mEntryColors = getEntryValues();
            }
            a.recycle();
        }
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
        if (getEntries() == null || getEntryValues() == null) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array.");
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
        ColorPickerListAdapter adapter = new ColorPickerListAdapter(getContext(), dividerTop,
                dividerBottom, getEntries(), getEntryColors(), mClickedDialogItem);

        mCustomTitleText.setText(getDialogTitle());
        adapter.setOnItemClickedListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
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
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        ((ColorPickerListAdapter) mRecyclerView.getAdapter()).setOnItemClickedListener(null);
        if (positiveResult && mClickedDialogItem >= 0 && getEntryValues() != null) {
            String value = getEntryValues()[mClickedDialogItem].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
        if (!positiveResult) {
            mSelectedColor = convertToColorInt((String) getEntryColor());
            mClickedDialogItem = findIndexOfValue(getValue());
        }
    }

    public void setNeedEntryColors(boolean needEntryColors) {
        mNeedEntryColors = needEntryColors;
        notifyChanged();
    }

    public void setEntryColors(CharSequence[] entryColors) {
        mEntryColors = entryColors;
        notifyChanged();
    }

    public void setEntryColors(int entryColorsResId) {
        setEntryColors(getContext().getResources().getTextArray(entryColorsResId));
    }

    public CharSequence[] getEntryColors() {
        return mEntryColors;
    }

    public CharSequence getEntryColor() {
        int index = findIndexOfValue(getValue());
        return index >= 0 && mEntryColors != null ? mEntryColors[index] : null;
    }

    private void updateDialogTitleColors(boolean animate) {
        int newBgColor = ThemeUtil.getPickColorListDlgTitleBgColor(getContext(),
                mSelectedColor);
        int newTextColor = ThemeUtil.getPickColorListDlgTitleTextColor(getContext(),
                mSelectedColor);

        if (animate) {
            if (mDialogTitleBgColor != newBgColor) {
                boolean animateTextColor = mDialogTitleTextColor != newTextColor;
                ColorPickerListAnimator.animateColorTransition(mDialogTitleBgColor, newBgColor,
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
        return ColorPickerHelper.convertToARGB(color);
    }

    /**
     * Converts a aarrggbb- or rrggbb color string to a color int
     * 
     * @param argb
     * @throws NumberFormatException
     * @author Unknown
     */
    public static int convertToColorInt(String argb) {
        return ColorPickerHelper.convertToColorInt(argb);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState myState = new SavedState(superState);
        myState.clickedDialogItem = mClickedDialogItem;
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
        super.onRestoreInstanceState(myState.getSuperState());
    }
    
    private static class SavedState extends BaseSavedState {
        int clickedDialogItem;

        public SavedState(Parcel source) {
            super(source);
            clickedDialogItem = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(clickedDialogItem);
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
