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

public class ColorPickerCard {
    protected Context mContext;
    protected Resources mResources;
    protected int mColor = 0;
    protected String mTitle = "";
    protected String mSubtitle = "";

    public ColorPickerCard(Context context) {
        mContext = context;
        mResources = mContext.getResources();
    }

    public int getColor() {
        return mColor;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public void setColorResId(int resId) {
        mColor = mContext.getColor(resId);
    }

    public void setColor(int color) {
        mColor = color;
    }

    public void setTitleResId(int resId) {
        mTitle = mResources.getString(resId);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSubtitleResId(int resId) {
        mSubtitle = mResources.getString(resId);
    }

    public void setSubtitle(String subtitle) {
        mSubtitle = subtitle;
    }
}
