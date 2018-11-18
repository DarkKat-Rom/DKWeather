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

package net.darkkatrom.dkweather.colorpicker.model;

import android.content.Context;
import android.content.res.Resources;

import net.darkkatrom.dkweather.colorpicker.util.ColorPickerHelper;
import net.darkkatrom.dkweather.utils.ColorUtil;

public class ColorPickerCard {
    protected Context mContext;
    protected Resources mResources;
    protected int mTitleResId = 0;
    protected String mTitle = "";
    protected int mSubtitleResId = 0;
    protected String mSubtitle = "";
    protected int mColorResId = 0;
    protected int mColor = 0;
    protected int mPaletteTitleResId = 0;
    protected String mPaletteTitle = "";

    public ColorPickerCard(Context context) {
        mContext = context;
        mResources = mContext.getResources();
    }

    public ColorPickerCard(Context context, int titleResId, int colorResId) {
        this(context);
        mTitleResId = titleResId;
        mTitle = mResources.getString(titleResId);
        mSubtitle = ColorPickerHelper.convertToARGB(context.getColor(colorResId));
        mColorResId = colorResId;
        mColor = mContext.getColor(mColorResId);
    }

    public ColorPickerCard(Context context, String title, String subtitle, int color,
            String paletteTitle) {
        this(context);
        mTitle = color != 0 ? paletteTitle + ":\n" + title : title + " (" + paletteTitle + ")";
        mSubtitle = subtitle;
        mColor = color;
        mPaletteTitle = paletteTitle;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getSubtitleResId() {
        return mSubtitleResId;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public int getColorResId() {
        return mColorResId;
    }

    public int getColor() {
        return mColor;
    }

    public int getPaletteTitleResId() {
        return mPaletteTitleResId;
    }

    public String getPaletteTitle() {
        return mPaletteTitle;
    }

    public void setTitleResId(int resId) {
        mTitleResId = resId;
        mTitle = mResources.getString(resId);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSubtitleResId(int resId) {
        mSubtitleResId = resId;
        mSubtitle = mResources.getString(resId);
    }

    public void setSubtitle(String subtitle) {
        mSubtitle = subtitle;
    }

    public void setColorResId(int resId) {
        mColorResId = resId;
        setColor(mContext.getColor(mColorResId));
    }

    public void setColor(int color) {
        mColor = color;
    }

    public void setPaletteTitleResId(int resId) {
        mPaletteTitleResId = resId;
        mPaletteTitle = mResources.getString(resId);
    }

    public void setPaletteTitle(String paletteTitle) {
        mPaletteTitle = paletteTitle;
    }

    public boolean needLightTheme() {
        return !ColorUtil.isColorDark(getColor());
    }
}
