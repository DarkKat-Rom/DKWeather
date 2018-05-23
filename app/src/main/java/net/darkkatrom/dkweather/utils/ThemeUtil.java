/*
 * Copyright (C) 2017 DarkKat
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
package net.darkkatrom.dkweather.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.WeatherInfo;

public class ThemeUtil {

    public static final int STATUS_BAR_DARKEN_COLOR = 0x30000000;

    public static int getConditionIconColor(Context context, int conditionIconType) {
        int conditionIconColor = 0;
        if (conditionIconType == WeatherInfo.ICON_MONOCHROME) {
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorControlNormal, tv, true);
            if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                conditionIconColor = tv.data;
            } else {
                conditionIconColor = context.getColor(tv.resourceId);
            }
        }
        return conditionIconColor;
    }

    public static boolean needsLightStatusBar(Context context) {
        if (Config.getThemeCustomizeColors(context)) {
            return !ColorUtil.isColorDark(getStatusBarBackgroundColor(context));
        } else {
            return !Config.getThemeUseDarkTheme(context) && Config.getThemeUseLightStatusBar(context);
        }
    }

    public static boolean needsLightActionBar(Context context) {
        if (Config.getThemeCustomizeColors(context)) {
            return !ColorUtil.isColorDark(getActionBarBackgroundColor(context));
        } else {
            return !Config.getThemeUseDarkTheme(context) && Config.getThemeUseLightStatusBar(context);
        }
    }

    public static boolean needsLightNavigationBar(Context context) {
        if (Config.getThemeColorizeNavigationBar(context)) {
            return !ColorUtil.isColorDark(getNavigationBarBackgroundColor(context));
        } else if (Config.getThemeUseDarkTheme(context)) {
            return false;
        } else {
            return Config.getThemeUseLightNavigationBar(context);
        }
    }

    public static int getActionBarBackgroundColor(Context context) {
        return Config.getThemePrimaryColor(context);
    }

    public static int getStatusBarBackgroundColor(Context context) {
        return ColorUtil.compositeColors(STATUS_BAR_DARKEN_COLOR, getActionBarBackgroundColor(context));
    }

    public static int getNavigationBarBackgroundColor(Context context) {
        int color = 0;
        if (Config.getThemeColorizeNavigationBar(context)) {
            color = Config.getThemeCustomizeColors(context)
                    ? Config.getThemePrimaryColor(context) : context.getColor(R.color.primary_darkkat);
        }
        return color;
    }

    public static int getThemeResId(Context context) {
        int themeResId = 0;
        if (Config.getThemeUseDarkTheme(context)) {
            if (needsLightActionBar(context) && needsLightNavigationBar(context)) {
                themeResId = needsLightStatusBar(context)
                        ? R.style.AppThemeDark_LightStatusBar_LightNavigationBar
                        : R.style.AppThemeDark_LightActionBar_LightNavigationBar;
            } else if (needsLightActionBar(context)) {
                themeResId = needsLightStatusBar(context)
                        ? R.style.AppThemeDark_LightStatusBar
                        : R.style.AppThemeDark_LightActionBar;
            } else if (needsLightNavigationBar(context)) {
                themeResId = R.style.AppThemeDark_LightNavigationBar;
            } else {
                themeResId = R.style.AppThemeDark;
            }
        } else {
            if (needsLightActionBar(context) && needsLightNavigationBar(context)) {
                themeResId = needsLightStatusBar(context)
                        ? R.style.AppThemeLight_LightStatusBar_LightNavigationBar
                        : R.style.AppThemeLight_LightActionBar_LightNavigationBar;
            } else if (needsLightActionBar(context)) {
                themeResId = needsLightStatusBar(context)
                        ? R.style.AppThemeLight_LightStatusBar
                        : R.style.AppThemeLight_LightActionBar;
            } else if (needsLightNavigationBar(context)) {
                themeResId = R.style.AppThemeLight_LightNavigationBar;
            } else {
                themeResId = R.style.AppThemeLight;
            }
        }
        return themeResId;
    }
}
