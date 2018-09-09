/*
 * Copyright (C) 2016 DarkKat
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

package net.darkkatrom.dkweather.fragments.themecolors;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.activities.SettingsActivity;
import net.darkkatrom.dkweather.colorpicker.fragment.SettingsColorPickerFragment;
import net.darkkatrom.dkweather.utils.Config;

public class ThemeColorsApp extends SettingsColorPickerFragment implements
        OnSharedPreferenceChangeListener {

    private int mPrimaryColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.theme_colors_settings_app);

        PreferenceCategory catAllThemes =
                (PreferenceCategory) findPreference(Config.PREF_KEY_CAT_ALL_COLORS);
        PreferenceCategory catLightTheme =
                (PreferenceCategory) findPreference(Config.PREF_KEY_CAT_LIGHT_COLORS);
        PreferenceCategory catDarkTheme =
                (PreferenceCategory) findPreference(Config.PREF_KEY_CAT_DARK_COLORS);

        mPrimaryColor = Config.getThemePrimaryColor(getActivity());

        if (Config.getThemeUseDarkTheme(getActivity())
                || Config.getThemeCustomizeColors(getActivity())) {
            removePreference(Config.PREF_KEY_THEME_USE_LIGHT_STATUS_BAR);
        }

        if (Config.getThemeUseDarkTheme(getActivity())
                || Config.getThemeColorizeNavigationBar(getActivity())) {
            removePreference(Config.PREF_KEY_THEME_USE_LIGHT_NAVIGATION_BAR);
        }


        if (!Config.getThemeCustomizeColors(getActivity())) {
            catAllThemes.removePreference(findPreference(Config.PREF_KEY_THEME_PRIMARY_COLOR));
            catLightTheme.removePreference(findPreference(Config.PREF_KEY_THEME_LIGHT_ACCENT_COLOR));
            catLightTheme.removePreference(findPreference(Config.PREF_KEY_THEME_LIGHT_TEXT_COLOR));
            catLightTheme.removePreference(findPreference(Config.PREF_KEY_THEME_LIGHT_RIPPLE_COLOR));
            catDarkTheme.removePreference(findPreference(Config.PREF_KEY_THEME_DARK_ACCENT_COLOR));
            catDarkTheme.removePreference(findPreference(Config.PREF_KEY_THEME_DARK_TEXT_COLOR));
            catDarkTheme.removePreference(findPreference(Config.PREF_KEY_THEME_DARK_RIPPLE_COLOR));
            removePreference(Config.PREF_KEY_CAT_ALL_COLORS);
            removePreference(Config.PREF_KEY_CAT_LIGHT_COLORS);
            removePreference(Config.PREF_KEY_CAT_DARK_COLORS);
        } else {
           if (Config.getThemeUseDarkTheme(getActivity())) {
                catLightTheme.removePreference(findPreference(Config.PREF_KEY_THEME_LIGHT_ACCENT_COLOR));
                catLightTheme.removePreference(findPreference(Config.PREF_KEY_THEME_LIGHT_TEXT_COLOR));
                catLightTheme.removePreference(findPreference(Config.PREF_KEY_THEME_LIGHT_RIPPLE_COLOR));
                removePreference(Config.PREF_KEY_CAT_LIGHT_COLORS);
            }
        }
    }

    @Override
    protected int getSubtitleResId() {
        return R.string.action_bar_subtitle_settings_theme_colors_app;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Config.PREF_KEY_THEME.equals(key)
                    || Config.PREF_KEY_THEME_LIGHT_ACCENT_COLOR.equals(key)
                    || Config.PREF_KEY_THEME_DARK_ACCENT_COLOR.equals(key)
                    || Config.PREF_KEY_THEME_LIGHT_TEXT_COLOR.equals(key)
                    || Config.PREF_KEY_THEME_DARK_TEXT_COLOR.equals(key)
                    || Config.PREF_KEY_THEME_LIGHT_RIPPLE_COLOR.equals(key)
                    || Config.PREF_KEY_THEME_DARK_RIPPLE_COLOR.equals(key)
                    || Config.PREF_KEY_THEME_USE_LIGHT_STATUS_BAR.equals(key)
                    || Config.PREF_KEY_THEME_USE_LIGHT_NAVIGATION_BAR.equals(key)
                    || Config.PREF_KEY_THEME_CUSTOMIZE_COLORS.equals(key)
                    || Config.PREF_KEY_THEME_COLORIZE_NAVIGATION_BAR.equals(key)) {
            ((SettingsActivity) getActivity()).recreateForThemeChange();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Config.getThemePrimaryColor(getActivity()) != mPrimaryColor) {
            ((SettingsActivity) getActivity()).recreateForThemeChange();
        } else {
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
