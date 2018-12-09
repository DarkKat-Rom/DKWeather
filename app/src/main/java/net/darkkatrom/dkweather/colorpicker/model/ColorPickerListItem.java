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

import net.darkkatrom.dkweather.colorpicker.util.ColorPickerHelper;

    public class ColorPickerListItem {
        protected CharSequence mTitle;
        protected CharSequence mColor;
        protected int mColorItemsStartIndex;
        protected int mColorItemsCount = 0;
        protected boolean mIsExpanded;
        protected boolean mIsChecked;
        protected int mViewType;

        public ColorPickerListItem(CharSequence title, boolean isExpanded, int colorItemsStartIndex, int viewType) {
            mTitle = title;
            mColorItemsStartIndex = colorItemsStartIndex;
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

        public CharSequence getTitle() {
            return mTitle;
        }

        public int getColor() {
            return ColorPickerHelper.convertToColorInt(mColor.toString());
        }

        public int getColorItemsStartIndex() {
            return mColorItemsStartIndex;
        }

        public int getColorItemsCount() {
            return 0;
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

        public void increaseColorItemsCount() {
        }

        public void toggleExpanded() {
            mIsExpanded = !mIsExpanded;
        }

        public void setChecked(boolean checked) {
            mIsChecked = checked;
        }
    }
