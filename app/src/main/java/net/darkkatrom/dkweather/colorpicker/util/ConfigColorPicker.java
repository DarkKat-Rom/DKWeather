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
package net.darkkatrom.dkweather.colorpicker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.darkkatrom.dkweather.R;

public class ConfigColorPicker {
    public static final String COLOR_PICKER_SHOW_FAVORITES   = "color_picker_show_favorites";
    public static final String COLOR_PICKER_SHOW_HELP_SCREEN = "color_picker_show_help_screen";

    public static final String COLOR_PICKER_DEFAULT_VIEW = "color_picker_settings_default_view";
    public static final String COLOR_PICKER_FAVORITE_CARD_ALLOW_DELETE_TYPE =
            "color_picker_settings_favorite_card_allow_delete_type";

    public static final String COLOR_PICKER_CHIP_CHECKED_ID = "color_picker_main_buttons_checked_id";

    public static final int COLOR_PICKER_CHIP_DEFAULT_CHECKED_ID = R.id.color_picker_chip_pick;

    public static boolean getShowFavorites(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(COLOR_PICKER_SHOW_FAVORITES, false);
    }

    public static void setShowFavorites(Context context, boolean show) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().putBoolean(COLOR_PICKER_SHOW_FAVORITES, show).commit();
    }

    public static int getShowHelpScreen(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getInt(COLOR_PICKER_SHOW_HELP_SCREEN, 1);
    }

    public static void setShowHelpScreen(Context context, int visibility) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().putInt(COLOR_PICKER_SHOW_HELP_SCREEN, visibility).commit();
    }

    public static int getFavoriteButtonValue(Context context, String buttonId) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getInt(buttonId, 0);
    }

    public static void setFavoriteButtonValue(Context context, String buttonId, int value) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().putInt(buttonId, value).commit();
    }


    public static int getFavoriteColor(Context context, String key) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getInt(key, 0);
    }

    public static void setFavoriteColor(Context context, String key, int color) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().putInt(key, color).commit();
    }

    public static String getFavoriteSubtitle(Context context, String key) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getString(key, "");
    }

    public static void setFavoriteSubtitle(Context context, String key, String subtitle) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().putString(key, subtitle).commit();
    }

    public static int getChipChededId(Context context, boolean isSavedState) {
        if (isSavedState) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            return prefs.getInt(COLOR_PICKER_CHIP_CHECKED_ID, COLOR_PICKER_CHIP_DEFAULT_CHECKED_ID);
        }
        int defaultView = ConfigColorPicker.getDefaultView(context);
        int chipChededId = COLOR_PICKER_CHIP_DEFAULT_CHECKED_ID;
        if (defaultView == 0) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            chipChededId = prefs.getInt(COLOR_PICKER_CHIP_CHECKED_ID, COLOR_PICKER_CHIP_DEFAULT_CHECKED_ID);
        } else if (defaultView == 1) {
            chipChededId = R.id.color_picker_chip_pick;
        } else if (defaultView == 2) {
            chipChededId =  R.id.color_picker_chip_favorites;
        } else if (defaultView == 3) {
            chipChededId =  R.id.color_picker_chip_darkkat;
        } else if (defaultView == 4) {
            chipChededId =  R.id.color_picker_chip_material;
        } else if (defaultView == 5) {
            chipChededId =  R.id.color_picker_chip_holo;
        } else if (defaultView == 5) {
            chipChededId =  R.id.color_picker_chip_rgb;
        }
        return chipChededId;
    }

    public static void setChipChededId(Context context, int checkedId) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().putInt(COLOR_PICKER_CHIP_CHECKED_ID, checkedId).commit();
    }

    public static int getDefaultView(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String valueString = prefs.getString(COLOR_PICKER_DEFAULT_VIEW, "0");
        return Integer.valueOf(valueString);
    }

    public static int getAllowDeleteType(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String valueString = prefs.getString(COLOR_PICKER_FAVORITE_CARD_ALLOW_DELETE_TYPE, "2");
        return Integer.valueOf(valueString);
    }
}
