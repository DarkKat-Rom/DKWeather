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

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.activities.MainActivity;
import net.darkkatrom.dkweather.utils.Config;

public class ThemeSettings extends PreferenceFragment implements
        OnPreferenceChangeListener {

    private SwitchPreference mUseDarkTheme;
    private SwitchPreference mUseLightStatusBar;
    private SwitchPreference mUseLightNavigationBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.theme_settings);

        mUseDarkTheme = (SwitchPreference) findPreference(Config.PREF_KEY_THEME_USE_DARK_THEME);
        mUseDarkTheme.setOnPreferenceChangeListener(this);

        mUseLightStatusBar = (SwitchPreference) findPreference(Config.PREF_KEY_THEME_USE_LIGHT_STATUS_BAR);
        mUseLightStatusBar.setOnPreferenceChangeListener(this);

        mUseLightNavigationBar =
                (SwitchPreference) findPreference(Config.PREF_KEY_THEME_USE_LIGHT_NAVIGATION_BAR);
        mUseLightNavigationBar.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mUseDarkTheme
                    || preference == mUseLightStatusBar
                    || preference == mUseLightNavigationBar) {
            ((MainActivity) getActivity()).recreateForThemeChange();
            return true;
        }
        return false;
    }
}
