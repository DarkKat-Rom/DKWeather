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

public class ColorPickerFavoritesListItem {
    private String mTitle;
    private String mSubtitle;
    private int mColor;

    public ColorPickerFavoritesListItem(String title, String subtitle, int color) {
        mTitle = title;
        mSubtitle = subtitle;
        mColor = color;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public int getColor() {
        return mColor;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSubtitle(String subtitle) {
        mSubtitle = subtitle;
    }

    public void setColor(int color) {
        mColor = color;
    }
}
