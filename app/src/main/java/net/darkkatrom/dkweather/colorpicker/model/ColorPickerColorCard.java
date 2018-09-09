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

import net.darkkatrom.dkweather.colorpicker.util.ColorPickerHelper;

public class ColorPickerColorCard extends ColorPickerCard {

    public ColorPickerColorCard(Context context) {
        super(context);
    }

    public ColorPickerColorCard(Context context, int colorResId, int titleResId) {
        super(context);

        mColor = mContext.getColor(colorResId);
        mTitle = mResources.getString(titleResId);
        mSubtitle = ColorPickerHelper.convertToARGB(mColor);
    }

    public ColorPickerColorCard(Context context, int color, String title) {
        super(context);

        mColor = color;
        mTitle = title;
        mSubtitle = ColorPickerHelper.convertToARGB(mColor);
    }
}
