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

public class ColorPickerColorCard {
    private int mTitleResId;
    private String mSubtitle;
    private int mColorResId;

    public ColorPickerColorCard(int titleResId, String subtitle, int colorResId) {
        mTitleResId = titleResId;
        mSubtitle = subtitle;
        mColorResId = colorResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public int getColorResId() {
        return mColorResId;
    }

    public void setTitleResId(int titleResId) {
        mTitleResId = titleResId;
    }

    public void setSubtitle(String subtitle) {
        mSubtitle = subtitle;
    }

    public void setColorResId(int colorResId) {
        mColorResId = colorResId;
    }
}
