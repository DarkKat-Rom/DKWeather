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
import net.darkkatrom.dkweather.colorpicker.util.ColorPickerHelper;
import net.darkkatrom.dkweather.utils.GraphicsUtil;

public class ColorPickerFavoriteCard extends ColorPickerCard {
    private int mFavoriteNumber;

    public ColorPickerFavoriteCard(Context context) {
        super(context);
    }

    public ColorPickerFavoriteCard(Context context, int favoriteNumber, int color) {
        super(context);

        mFavoriteNumber = favoriteNumber;
        setColor(color);
    }

    @Override
    public void setColor(int color) {
        super.setColor(color);

        mTitle = "";
        if (mColor == 0) {
            mTitle = mResources.getString(R.string.favorite_title) + " " + mFavoriteNumber;
            mSubtitle = "0";
        } else {
            String paletteTitle = ColorPickerHelper.getPaletteTitle(mContext, mColor);
            String colorTitle = ColorPickerHelper.getColorTitle(mContext, mColor);
            if (!paletteTitle.isEmpty()) {
                mTitle = paletteTitle;
            }
            if (!colorTitle.isEmpty()) {
                if (!mTitle.isEmpty()) {
                    mTitle += ":\n";
                }
                mTitle += colorTitle;
            }
            if (mTitle.isEmpty()) {
                mTitle = mResources.getString(R.string.favorite_title) + " " + mFavoriteNumber;
            }
            mSubtitle = GraphicsUtil.convertToARGB(color);
        }
    }
}
