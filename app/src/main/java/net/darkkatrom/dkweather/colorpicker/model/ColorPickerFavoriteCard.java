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

import net.darkkatrom.dkweather.R;

public class ColorPickerFavoriteCard extends ColorPickerCard {

    public ColorPickerFavoriteCard(Context context) {
        super(context);
    }

    public ColorPickerFavoriteCard(Context context, int number, String subtitle, int color) {
        super(context);
        mTitleResId = R.string.favorite_title;
        mTitle = context.getResources().getString(mTitleResId) + " " + number;
        mSubtitle = subtitle.isEmpty() ? context.getResources().getString(R.string.empty_title) : subtitle;
        mColor = color;
    }
}