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
import android.preference.PreferenceScreen;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.colorpicker.fragment.SettingsColorPickerFragment;
import net.darkkatrom.dkweather.utils.Config;

public class ThemeColorsPickColorList extends SettingsColorPickerFragment implements
        OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updatePreferenceScreen();
    }

    @Override
    protected int getSubtitleResId() {
        return R.string.action_bar_subtitle_settings_theme_colors_pick_color_list;
    }

    public void updatePreferenceScreen() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.theme_colors_settings_pick_color_list);

        PreferenceCategory catDialogTitleBackground =
                (PreferenceCategory) findPreference(Config.PREF_KEY_PICK_COLOR_LIST_CAT_DLG_TITLE_BG);

        if (Config.getPickColorListDlgTitleBg(getActivity())
                    != Config.PICK_COLOR_LIST_DLG_TITLE_BG_PICK_COLOR) {
            catDialogTitleBackground.removePreference(findPreference(
                    Config.PREF_KEY_PICK_COLOR_LIST_DLG_TITLE_BG_CUSTOM_COLOR));
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

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        if (Config.PREF_KEY_PICK_COLOR_LIST_DLG_TITLE_BG.equals(key)) {
            updatePreferenceScreen();
        }
    }
}
