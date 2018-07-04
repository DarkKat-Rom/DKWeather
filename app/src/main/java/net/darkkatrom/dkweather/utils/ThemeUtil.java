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

    public static int getThemeOverlayAccentResId(Context context) {
        int index = Config.getIndexForAccentColor(context);

        int resId = 0;
        if (index == 1) {
            resId = R.style.Theme_Overlay_Accent_HoloBlueLight;
        } else if (index == 2) {
            resId = R.style.Theme_Overlay_Accent_MaterialBlueGrey500;
        } else if (index == 3) {
            resId = R.style.Theme_Overlay_Accent_MaterialBlue500;
        } else if (index == 4) {
            resId = R.style.Theme_Overlay_Accent_MaterialLightBlue500;
        } else if (index == 5) {
            resId = R.style.Theme_Overlay_Accent_MaterialCyan500;
        } else if (index == 6) {
            resId = R.style.Theme_Overlay_Accent_MaterialDeepTeal500;
        } else if (index == 7) {
            resId = R.style.Theme_Overlay_Accent_MaterialIndigo500;
        } else if (index == 8) {
            resId = R.style.Theme_Overlay_Accent_MaterialPurple500;
        } else if (index == 9) {
            resId = R.style.Theme_Overlay_Accent_MaterialDeepPurple500;
        } else if (index == 10) {
            resId = R.style.Theme_Overlay_Accent_MaterialPink500;
        } else if (index == 11) {
            resId = R.style.Theme_Overlay_Accent_MaterialOrange500;
        } else if (index == 12) {
            resId = R.style.Theme_Overlay_Accent_MaterialDeepOrange500;
        } else if (index == 13) {
            resId = R.style.Theme_Overlay_Accent_MaterialRed500;
        } else if (index == 14) {
            resId = R.style.Theme_Overlay_Accent_MaterialYellow500;
        } else if (index == 15) {
            resId = R.style.Theme_Overlay_Accent_MaterialAmber500;
        } else if (index == 16) {
            resId = R.style.Theme_Overlay_Accent_MaterialGreen500;
        } else if (index == 17) {
            resId = R.style.Theme_Overlay_Accent_MaterialLightGreen500;
        } else if (index == 18) {
            resId = R.style.Theme_Overlay_Accent_MaterialLime500;
        } else if (index == 19) {
            resId = R.style.Theme_Overlay_Accent_Black;
        } else if (index == 20) {
            resId = R.style.Theme_Overlay_Accent_White;
        } else if (index == 21) {
            resId = R.style.Theme_Overlay_Accent_Blue;
        } else if (index == 22) {
            resId = R.style.Theme_Overlay_Accent_Purple;
        } else if (index == 23) {
            resId = R.style.Theme_Overlay_Accent_Orange;
        } else if (index == 24) {
            resId = R.style.Theme_Overlay_Accent_Red;
        } else if (index == 25) {
            resId = R.style.Theme_Overlay_Accent_Yellow;
        } else if (index == 26) {
            resId = R.style.Theme_Overlay_Accent_Green;
        }
        return resId;
    }

    public static int getThemeOverlayLightTextResId(Context context) {
        int index = Config.getIndexForLightTextColor(context);

        int resId = 0;
        if (index == 0) {
            resId = R.style.Theme_Overlay_TextLight_AccentDarkKat;
        } else if (index == 1) {
            resId = R.style.Theme_Overlay_TextLight_HoloBlueLight;
        } else if (index == 2) {
            resId = R.style.Theme_Overlay_TextLight_MaterialBlueGrey500;
        } else if (index == 3) {
            resId = R.style.Theme_Overlay_TextLight_MaterialBlue500;
        } else if (index == 4) {
            resId = R.style.Theme_Overlay_TextLight_MaterialLightBlue500;
        } else if (index == 5) {
            resId = R.style.Theme_Overlay_TextLight_MaterialCyan500;
        } else if (index == 6) {
            resId = R.style.Theme_Overlay_TextLight_MaterialDeepTeal500;
        } else if (index == 7) {
            resId = R.style.Theme_Overlay_TextLight_MaterialIndigo500;
        } else if (index == 8) {
            resId = R.style.Theme_Overlay_TextLight_MaterialPurple500;
        } else if (index == 9) {
            resId = R.style.Theme_Overlay_TextLight_MaterialDeepPurple500;
        } else if (index == 10) {
            resId = R.style.Theme_Overlay_TextLight_MaterialPink500;
        } else if (index == 11) {
            resId = R.style.Theme_Overlay_TextLight_MaterialOrange500;
        } else if (index == 12) {
            resId = R.style.Theme_Overlay_TextLight_MaterialDeepOrange500;
        } else if (index == 13) {
            resId = R.style.Theme_Overlay_TextLight_MaterialRed500;
        } else if (index == 14) {
            resId = R.style.Theme_Overlay_TextLight_MaterialYellow500;
        } else if (index == 15) {
            resId = R.style.Theme_Overlay_TextLight_MaterialAmber500;
        } else if (index == 16) {
            resId = R.style.Theme_Overlay_TextLight_MaterialGreen500;
        } else if (index == 17) {
            resId = R.style.Theme_Overlay_TextLight_MaterialLightGreen500;
        } else if (index == 18) {
            resId = R.style.Theme_Overlay_TextLight_MaterialLime500;
        } else if (index == 20) {
            resId = R.style.Theme_Overlay_TextLight_White;
        } else if (index == 21) {
            resId = R.style.Theme_Overlay_TextLight_Blue;
        } else if (index == 22) {
            resId = R.style.Theme_Overlay_TextLight_Purple;
        } else if (index == 23) {
            resId = R.style.Theme_Overlay_TextLight_Orange;
        } else if (index == 24) {
            resId = R.style.Theme_Overlay_TextLight_Red;
        } else if (index == 25) {
            resId = R.style.Theme_Overlay_TextLight_Yellow;
        } else if (index == 26) {
            resId = R.style.Theme_Overlay_TextLight_Green;
        }
        return resId;
    }

    public static int getThemeOverlayDarkTextResId(Context context) {
        int index = Config.getIndexForDarkTextColor(context);

        int resId = 0;
        if (index == 0) {
            resId = R.style.Theme_Overlay_TextDark_AccentDarkKat;
        } else if (index == 1) {
            resId = R.style.Theme_Overlay_TextDark_HoloBlueLight;
        } else if (index == 2) {
            resId = R.style.Theme_Overlay_TextDark_MaterialBlueGrey500;
        } else if (index == 3) {
            resId = R.style.Theme_Overlay_TextDark_MaterialBlue500;
        } else if (index == 4) {
            resId = R.style.Theme_Overlay_TextDark_MaterialLightBlue500;
        } else if (index == 5) {
            resId = R.style.Theme_Overlay_TextDark_MaterialCyan500;
        } else if (index == 6) {
            resId = R.style.Theme_Overlay_TextDark_MaterialDeepTeal500;
        } else if (index == 7) {
            resId = R.style.Theme_Overlay_TextDark_MaterialIndigo500;
        } else if (index == 8) {
            resId = R.style.Theme_Overlay_TextDark_MaterialPurple500;
        } else if (index == 9) {
            resId = R.style.Theme_Overlay_TextDark_MaterialDeepPurple500;
        } else if (index == 10) {
            resId = R.style.Theme_Overlay_TextDark_MaterialPink500;
        } else if (index == 11) {
            resId = R.style.Theme_Overlay_TextDark_MaterialOrange500;
        } else if (index == 12) {
            resId = R.style.Theme_Overlay_TextDark_MaterialDeepOrange500;
        } else if (index == 13) {
            resId = R.style.Theme_Overlay_TextDark_MaterialRed500;
        } else if (index == 14) {
            resId = R.style.Theme_Overlay_TextDark_MaterialYellow500;
        } else if (index == 15) {
            resId = R.style.Theme_Overlay_TextDark_MaterialAmber500;
        } else if (index == 16) {
            resId = R.style.Theme_Overlay_TextDark_MaterialGreen500;
        } else if (index == 17) {
            resId = R.style.Theme_Overlay_TextDark_MaterialLightGreen500;
        } else if (index == 18) {
            resId = R.style.Theme_Overlay_TextDark_MaterialLime500;
        } else if (index == 19) {
            resId = R.style.Theme_Overlay_TextDark_Black;
        } else if (index == 21) {
            resId = R.style.Theme_Overlay_TextDark_Blue;
        } else if (index == 22) {
            resId = R.style.Theme_Overlay_TextDark_Purple;
        } else if (index == 23) {
            resId = R.style.Theme_Overlay_TextDark_Orange;
        } else if (index == 24) {
            resId = R.style.Theme_Overlay_TextDark_Red;
        } else if (index == 25) {
            resId = R.style.Theme_Overlay_TextDark_Yellow;
        } else if (index == 26) {
            resId = R.style.Theme_Overlay_TextDark_Green;
        }
        return resId;
    }
}
