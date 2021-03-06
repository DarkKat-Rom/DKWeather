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

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.utils.Config;

public class ThemeColorsSettings extends SettingsBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.theme_colors_settings);
    }

    @Override
    protected int getSubtitleResId() {
        return R.string.action_bar_subtitle_settings_theme_colors;
    }

    @Override
    public void onResume() {
        super.onResume();
        findPreference(Config.PREF_KEY_THEME_COLORS_WIDGET).setEnabled(Config.isEnabled(getActivity()));
    }
}
