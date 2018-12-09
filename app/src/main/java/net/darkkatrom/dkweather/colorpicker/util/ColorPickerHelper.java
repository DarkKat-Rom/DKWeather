/*
* Copyright (C) 2018 DarkKat
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package net.darkkatrom.dkweather.colorpicker.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import net.darkkatrom.dkweather.R;

public class ColorPickerHelper {

    public static String getColorTitle(Context context, int color) {
        Resources res = context.getResources();
        TypedArray colors = res.obtainTypedArray(R.array.color_picker_all_palette);
        TypedArray titles = res.obtainTypedArray(R.array.color_picker_all_palette_titles);
        int titleResId = 0;
        for (int i = 0; i < 38; i++) {
            int resId = colors.getResourceId(i, 0);
            if (color == context.getColor(resId)) {
                titleResId = titles.getResourceId(i, 0);
                break;
            }
        }

        colors.recycle();
        titles.recycle();

        if (titleResId > 0) {
            return res.getString(titleResId);
        } else {
            return "";
        }
    }

    public static String getPaletteTitle(Context context, int color) {
        Resources res = context.getResources();
        TypedArray colors = res.obtainTypedArray(R.array.color_picker_all_palette);
        int titleResId = 0;
        for (int i = 0; i < 38; i++) {
            int resId = colors.getResourceId(i, 0);
            if (color == context.getColor(resId)) {
                if (i < 3) {
                    titleResId = R.string.darkkat_title;
                } else if (i < 20) {
                    titleResId = R.string.material_title;
                } else if (i < 30) {
                    titleResId = R.string.holo_title;
                } else {
                    titleResId = R.string.rgb_title;
                }
                break;
            }
        }

        colors.recycle();

        if (titleResId > 0) {
            return res.getString(titleResId);
        } else {
            return "";
        }
    }
}
