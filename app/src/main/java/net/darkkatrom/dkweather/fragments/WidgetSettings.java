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

package net.darkkatrom.dkweather.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.utils.Config;
import net.darkkatrom.dkweather.utils.JobUtil;

public class WidgetSettings extends SettingsBaseFragment implements
        OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updatePreferenceScreen();
    }

    @Override
    protected int getSubtitleResId() {
        return R.string.action_bar_subtitle_settings_widget;
    }

    public void updatePreferenceScreen() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.widget_settings);

        if (!Config.getWidgetShowAnyButton(getActivity())) {
            PreferenceCategory category =
                    (PreferenceCategory) findPreference("widget_settings_buttons");
            category.removePreference(findPreference(Config.PREF_KEY_WIDGET_SHOW_SETTINGS_BUTTONS_LEFT));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Config.PREF_KEY_WIDGET_SHOW_APP_SETTINGS_BUTTON.equals(key)
                || Config.PREF_KEY_WIDGET_SHOW_THEME_COLOR_BUTTON.equals(key)
                || Config.PREF_KEY_WIDGET_SHOW_SETTINGS_BUTTONS_LEFT.equals(key)) {
            if (!Config.PREF_KEY_WIDGET_SHOW_SETTINGS_BUTTONS_LEFT.equals(key)) {
                updatePreferenceScreen();
            }
            JobUtil.startWidgetUpdate(getActivity());
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
