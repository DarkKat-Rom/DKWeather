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


import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

import net.darkkatrom.dkweather.R;

import net.darkkatrom.dkweather.colorpicker.fragment.SettingsColorPickerFragment;
import net.darkkatrom.dkweather.colorpicker.preference.ColorPickerPreference;

import net.darkkatrom.dkweather.utils.Config;
import net.darkkatrom.dkweather.utils.JobUtil;

public class WidgetSettings extends SettingsColorPickerFragment implements
        OnPreferenceChangeListener  {

    private Preference mWidgetBackground;
    private Preference mWidgetBackgroundColor;
    private Preference mWidgetFrameColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.widget_settings);

        mWidgetBackground = findPreference(Config.PREF_KEY_WIDGET_BACKGROUND);
        mWidgetBackground.setOnPreferenceChangeListener(this);

        mWidgetBackgroundColor = findPreference(Config.PREF_KEY_WIDGET_BACKGROUND_COLOR);
        mWidgetBackgroundColor.setOnPreferenceChangeListener(this);

        mWidgetFrameColor = findPreference(Config.PREF_KEY_WIDGET_FRAME_COLOR);
        mWidgetFrameColor.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mWidgetBackground
                || preference == mWidgetBackgroundColor
                || preference == mWidgetFrameColor) {
            JobUtil.startWidgetUpdate(getActivity());
            return true;
        }
        return false;
    }
}
