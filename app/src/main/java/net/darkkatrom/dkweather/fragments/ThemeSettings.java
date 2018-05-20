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

package net.darkkatrom.dkweather.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.activities.MainActivity;
import net.darkkatrom.dkweather.utils.Config;

public class ThemeSettings extends SettingsBaseFragment implements
        OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.theme_settings);

        if (Config.getThemUseDarkTheme(getActivity())) {
            removePreference(Config.PREF_KEY_THEME_USE_LIGHT_STATUS_BAR);
            removePreference(Config.PREF_KEY_THEME_USE_LIGHT_NAVIGATION_BAR);
        }
    }

    @Override
    protected int getSubtitleResId() {
        return R.string.action_bar_subtitle_settings_theme;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Config.PREF_KEY_THEME_USE_DARK_THEME.equals(key)
                    || Config.PREF_KEY_THEME_USE_LIGHT_STATUS_BAR.equals(key)
                    || Config.PREF_KEY_THEME_USE_LIGHT_NAVIGATION_BAR.equals(key)) {
            ((MainActivity) getActivity()).recreateForThemeChange();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
