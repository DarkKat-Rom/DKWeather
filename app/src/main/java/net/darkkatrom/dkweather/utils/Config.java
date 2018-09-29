/*
 *  Copyright (C) 2015 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.darkkatrom.dkweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.providers.AbstractWeatherProvider;
import net.darkkatrom.dkweather.providers.OpenWeatherMapProvider;
import net.darkkatrom.dkweather.WeatherInfo;

public class Config {
    public static final String PREF_KEY_SETTINGS_NOTIFICATION  = "settings_notification";
    public static final String PREF_KEY_THEME_COLORS_WIDGET    = "theme_colors_widget";

    public static final String PREF_KEY_ENABLE               = "enable";
    public static final String PREF_KEY_UPDATE_INTERVAL      = "update_interval";
    public static final String PREF_KEY_OWM_API_KEY          = "owm_api_key";
    public static final String PREF_KEY_UNITS                = "units";
    public static final String PREF_KEY_CUSTOM_LOCATION      = "custom_location";
    public static final String PREF_KEY_CUSTOM_LOCATION_CITY = "custom_location_city";
    public static final String PREF_KEY_CONDITION_ICON       = "condition_icon";

    public static final String PREF_KEY_SHOW_NOTIF          = "show_notification";
    public static final String PREF_KEY_SHOW_NOTIF_ONGOING  = "show_notification_ongoing";
    public static final String PREF_KEY_NOTIF_SHOW_LOCATION = "notification_show_location";
    public static final String PREF_KEY_NOTIF_SHOW_DK_ICON  = "notification_show_dk_icon";

    public static final String PREF_KEY_WIDGET_SHOW_APP_SETTINGS_BUTTON
            = "widget_show_settings_button";
    public static final String PREF_KEY_WIDGET_SHOW_THEME_COLOR_BUTTON 
            = "widget_show_theme_colors_settings_button";
    public static final String PREF_KEY_WIDGET_SHOW_SETTINGS_BUTTONS_LEFT
            = "widget_show_settings_buttons_left";

    public static final String PREF_KEY_THEME =
            "theme";
    public static final String PREF_KEY_THEME_USE_LIGHT_STATUS_BAR =
            "theme_use_light_status_bar";
    public static final String PREF_KEY_THEME_USE_LIGHT_NAVIGATION_BAR =
            "theme_use_light_navigation_bar";

    public static final String PREF_KEY_THEME_CUSTOMIZE_COLORS =
            "theme_customize_colors";
    public static final String PREF_KEY_THEME_PRIMARY_COLOR =
            "theme_primary_color";
    public static final String PREF_KEY_THEME_ACCENT_COLOR =
            "theme_accent_color";
    public static final String PREF_KEY_THEME_LIGHT_TEXT_COLOR =
            "theme_light_text_color";
    public static final String PREF_KEY_THEME_DARK_TEXT_COLOR =
            "theme_dark_text_color";
    public static final String PREF_KEY_THEME_LIGHT_RIPPLE_COLOR =
            "theme_light_ripple_color";
    public static final String PREF_KEY_THEME_DARK_RIPPLE_COLOR =
            "theme_dark_ripple_color";
    public static final String PREF_KEY_THEME_COLORIZE_NAVIGATION_BAR =
            "theme_colorize_navigation_bar";

    public static final String PREF_KEY_WIDGET_BACKGROUND       = "widget_background";
    public static final String PREF_KEY_WIDGET_BACKGROUND_COLOR = "widget_background_color";
    public static final String PREF_KEY_WIDGET_FRAME_COLOR      = "widget_frame_color";
    public static final String PREF_KEY_WIDGET_TEXT_COLOR       = "widget_text_color";
    public static final String PREF_KEY_WIDGET_ICON_COLOR       = "widget_icon_color";

    public static final String PREF_KEY_LOCATION_ID   = "location_id";
    public static final String PREF_KEY_LOCATION_NAME = "location_name";
    public static final String PREF_KEY_WEATHER_DATA  = "weather_data";

    public static final String DARKKAT_API_KEY = "6d2f4f034d60d9680a720c12df8c7ddd";

    public static final int THEME_MATERIAL_LIGHT   = 0;
    public static final int THEME_MATERIAL_DARKKAT = 1;

    public static final int WIDGET_BACKGROUND_NONE                     = 0;
    public static final int WIDGET_BACKGROUND_BACKGROUND_ONLY          = 1;
    public static final int WIDGET_BACKGROUND_BACKGROUND_WITHOUT_FRAME = 2;
    public static final int WIDGET_BACKGROUND_BACKGROUND_WITH_FRAME    = 3;

    private static final int WIDGET_TEXT_PRIMARY_ALPHA   = 255;
    private static final int WIDGET_TEXT_SECONDARY_ALPHA = 179;
    private static final int WIDGET_ICON_ALPHA           = WIDGET_TEXT_SECONDARY_ALPHA;
    private static final int WIDGET_DIVIDER_ALPHA        = 51;

    public static boolean isEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_ENABLE, false);
    }

    public static int getUpdateInterval(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String valueString = prefs.getString(PREF_KEY_UPDATE_INTERVAL, "1");
        return Integer.valueOf(valueString);
    }

    public static String getAPIKey(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String apiKey = prefs.getString(PREF_KEY_OWM_API_KEY, DARKKAT_API_KEY);
        if (apiKey.isEmpty()) {
            apiKey = DARKKAT_API_KEY;
        }
        return apiKey;
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getString(PREF_KEY_UNITS, "0").equals("0");
    }

    public static int getUnit(Context context) {
        return isMetric(context) ? 0 : 1;
    }

    public static boolean isCustomLocation(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_CUSTOM_LOCATION, false);
    }

    public static String getLocationId(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getString(PREF_KEY_LOCATION_ID, null);
    }

    public static void setLocationId(Context context, String id) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().putString(PREF_KEY_LOCATION_ID, id).commit();
    }
    
    public static String getLocationName(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getString(PREF_KEY_LOCATION_NAME, null);
    }
    
    public static void setLocationName(Context context, String name) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().putString(PREF_KEY_LOCATION_NAME, name).commit();
    }

    public static WeatherInfo getWeatherData(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String str = prefs.getString(PREF_KEY_WEATHER_DATA, null);
        if (str != null) {
            WeatherInfo data = WeatherInfo.fromSerializedString(context, str);
            return data;
        }
        return null;
    }
    
    public static void setWeatherData(Context context, WeatherInfo data) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().putString(PREF_KEY_WEATHER_DATA, data.toSerializedString()).commit();
    }

    public static void clearWeatherData(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().remove(PREF_KEY_WEATHER_DATA).commit();
    }

    public static boolean getShowNotification(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_SHOW_NOTIF, false);
    }

    public static boolean getShowNotificationOngoing(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_SHOW_NOTIF_ONGOING, false);
    }

    public static boolean getNotificationShowLocation(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_NOTIF_SHOW_LOCATION, true);
    }

    public static boolean getNotificationShowDKIcon(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_NOTIF_SHOW_DK_ICON, true);
    }

    public static boolean getWidgetShowAppSettingsButton(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_WIDGET_SHOW_APP_SETTINGS_BUTTON, false);
    }

    public static boolean getWidgetShowThemeColorsButton(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_WIDGET_SHOW_THEME_COLOR_BUTTON, false);
    }

    public static boolean getWidgetShowAnyButton(Context context) {
        return getWidgetShowAppSettingsButton(context) || getWidgetShowThemeColorsButton(context);
    }

    public static boolean getWidgetShowSettingsButtonsLeft(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_WIDGET_SHOW_SETTINGS_BUTTONS_LEFT, false);
    }

    public static int getConditionIconType(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String valueString = prefs.getString(PREF_KEY_CONDITION_ICON, "0");
        return Integer.valueOf(valueString);
    }

    public static int getTheme(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String valueString = prefs.getString(PREF_KEY_THEME, "0");
        return Integer.valueOf(valueString);
    }

    public static boolean getThemeUseDarkTheme(Context context) {
        return getTheme(context) > 0;
    }

    public static boolean getThemeUseLightStatusBar(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_THEME_USE_LIGHT_STATUS_BAR, false);
    }

    public static boolean getThemeUseLightNavigationBar(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_THEME_USE_LIGHT_NAVIGATION_BAR, false);
    }

    public static boolean getThemeCustomizeColors(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_THEME_CUSTOMIZE_COLORS, false);
    }

    public static int getThemePrimaryColor(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        int defaultColor = context.getColor(R.color.primary_darkkat);
        return prefs.getInt(PREF_KEY_THEME_PRIMARY_COLOR, defaultColor);
    }

    public static int getIndexForAccentColor(Context context) {
        if (!getThemeCustomizeColors(context)) {
            return 0;
        } else {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            String valueString = prefs.getString(PREF_KEY_THEME_ACCENT_COLOR, "0");
            return Integer.valueOf(valueString);
        }
    }

    public static int getIndexForLightTextColor(Context context) {
        if (!getThemeCustomizeColors(context)) {
            return 19;
        } else {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            String valueString = prefs.getString(PREF_KEY_THEME_LIGHT_TEXT_COLOR, "19");
            return Integer.valueOf(valueString);
        }
    }

    public static int getIndexForDarkTextColor(Context context) {
        if (!getThemeCustomizeColors(context)) {
            return 20;
        } else {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            String valueString = prefs.getString(PREF_KEY_THEME_DARK_TEXT_COLOR, "20");
            return Integer.valueOf(valueString);
        }
    }

    public static int getIndexForRippleColor(Context context) {
        return getIndexForRippleColor(context, false);
    }

    public static int getIndexForRippleColor(Context context, boolean forceDark) {
        int defaultColorIndex = 0;
        if (forceDark) {
            defaultColorIndex = 20;
        } else {
            defaultColorIndex = getThemeUseDarkTheme(context) ? 20 : 19;
        }
        if (!getThemeCustomizeColors(context)) {
            return defaultColorIndex;
        } else {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            String valueString = "0";
            if (forceDark) {
                valueString = prefs.getString(PREF_KEY_THEME_DARK_RIPPLE_COLOR, "20");
            } else {
                valueString = getThemeUseDarkTheme(context)
                        ? prefs.getString(PREF_KEY_THEME_DARK_RIPPLE_COLOR, "20")
                        : prefs.getString(PREF_KEY_THEME_LIGHT_RIPPLE_COLOR, "19");
            }
            int colorIndex = Integer.valueOf(valueString);
            return colorIndex == defaultColorIndex ? -1 : colorIndex;
        }
    }

    public static int getIndexForDarkRippleColor(Context context) {
        int defaultColorIndex = 20;
        if (!getThemeCustomizeColors(context)) {
            return defaultColorIndex;
        } else {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            String valueString = prefs.getString(PREF_KEY_THEME_DARK_RIPPLE_COLOR, "20");
            int colorIndex = Integer.valueOf(valueString);
            return colorIndex == defaultColorIndex ? -1 : colorIndex;
        }
    }

    public static boolean getThemeColorizeNavigationBar(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_THEME_COLORIZE_NAVIGATION_BAR, false);
    }

    public static int getWidgetBackground(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String valueString = prefs.getString(PREF_KEY_WIDGET_BACKGROUND, "3");
        return Integer.valueOf(valueString);
    }

    public static int getWidgetBackgroundColor(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        int defaultColor = context.getColor(R.color.widget_default_background_color);
        return prefs.getInt(PREF_KEY_WIDGET_BACKGROUND_COLOR, defaultColor);
    }

    public static int getWidgetFrameColor(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        int defaultColor = context.getColor(R.color.widget_default_frame_color);
        return prefs.getInt(PREF_KEY_WIDGET_FRAME_COLOR, defaultColor);
    }


    public static int getWidgetTextColor(Context context, boolean isPrimary) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        int defaultColor = context.getColor(R.color.widget_default_text_color);
        int baseColor = prefs.getInt(PREF_KEY_WIDGET_TEXT_COLOR, defaultColor);
        int alpha = isPrimary ? WIDGET_TEXT_PRIMARY_ALPHA : WIDGET_TEXT_SECONDARY_ALPHA;
        return (alpha << 24) | (baseColor & 0x00ffffff);
    }

    public static int getWidgetIconColor(Context context, boolean isDivider) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        int defaultColor = context.getColor(R.color.widget_default_icon_color);
        int baseColor = prefs.getInt(PREF_KEY_WIDGET_ICON_COLOR, defaultColor);
        int alpha = isDivider ? WIDGET_DIVIDER_ALPHA : WIDGET_ICON_ALPHA;
        return (alpha << 24) | (baseColor & 0x00ffffff);
    }

    public static void resetWidgetSettings(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String defaultBackground =  "3";
        int defaultBackgroundColor = context.getColor(R.color.widget_default_background_color);
        int defaultFrameColor = context.getColor(R.color.widget_default_frame_color);
        int defaultTextColor = context.getColor(R.color.widget_default_text_color);
        int defaultIconColor = context.getColor(R.color.widget_default_icon_color);

        prefs.edit()
                .putString(PREF_KEY_WIDGET_BACKGROUND, defaultBackground)
                .putInt(PREF_KEY_WIDGET_BACKGROUND_COLOR, defaultBackgroundColor)
                .putInt(PREF_KEY_WIDGET_FRAME_COLOR, defaultFrameColor)
                .putInt(PREF_KEY_WIDGET_TEXT_COLOR, defaultTextColor)
                .putInt(PREF_KEY_WIDGET_ICON_COLOR, defaultIconColor)
                .commit();
    }

    public static AbstractWeatherProvider getProvider(Context context) {
        return new OpenWeatherMapProvider(context);
    }

    public static String getProviderId(Context context) {
        return "OpenWeatherMap";
    }
}
