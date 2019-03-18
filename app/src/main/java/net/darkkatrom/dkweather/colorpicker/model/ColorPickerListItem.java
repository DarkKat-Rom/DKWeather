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

import net.darkkatrom.dkweather.utils.GraphicsUtil;

    public class ColorPickerListItem {
        public static int VIEW_TYPE_HEADER_ITEM = 0;
        public static int VIEW_TYPE_COLOR_ITEM  = 1;

        protected CharSequence mTitle;
        protected CharSequence mColor;
        protected int mHeaderIndex = -1;
        protected int mColorItemsStartIndex = -1;
        protected int mColorItemsCount = 0;
        protected boolean mIsExpanded = true;
        protected boolean mIsChecked = false;
        protected int mViewType;

        public ColorPickerListItem(int headerIndex, CharSequence title, boolean isExpanded,
                int colorItemsStartIndex, int colorItemsCount, int viewType) {
            mHeaderIndex = headerIndex;
            mTitle = title;
            mColorItemsStartIndex = colorItemsStartIndex;
            mColorItemsCount = colorItemsCount;
            mIsExpanded = isExpanded;
            mViewType = viewType;
        }

        public ColorPickerListItem(CharSequence title, CharSequence color, boolean isChecked,
                int viewType) {
            mTitle = title;
            mColor = color;
            mIsChecked = isChecked;
            mViewType = viewType;
        }

        public int getHeaderIndex() {
            return mHeaderIndex;
        }

        public CharSequence getTitle() {
            return mTitle;
        }

        public String getColorString() {
            return mColor.toString();
        }

        public int getColor() {
            return GraphicsUtil.convertToColorInt(mColor.toString());
        }

        public int getColorItemsStartIndex() {
            return mColorItemsStartIndex;
        }

        public int getColorItemsCount() {
            return mColorItemsCount;
        }

        public boolean isExpanded() {
            return mIsExpanded;
        }

        public boolean isChecked() {
            return mIsChecked;
        }

        public int getViewType() {
            return mViewType;
        }

        public void toggleExpanded() {
            mIsExpanded = !mIsExpanded;
        }

        public void setChecked(boolean checked) {
            mIsChecked = checked;
        }
    }
