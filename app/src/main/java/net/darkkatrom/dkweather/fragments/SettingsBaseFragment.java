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

import android.preference.Preference;
import android.preference.PreferenceFragment;

import net.darkkatrom.dkweather.activities.BaseActivity;

public class SettingsBaseFragment extends PreferenceFragment {

    @Override
    public void onResume() {
        super.onResume();
        BaseActivity activity = (BaseActivity) getActivity();
        if (activity.getSupportActionBar() != null && getSubtitleResId() > 0) {
            activity.getSupportActionBar().setSubtitle(getSubtitleResId());
        }
    }

    protected int getSubtitleResId() {
        return 0;
    }

    protected void removePreference(String key) {
        Preference pref = findPreference(key);
        if (pref != null) {
            getPreferenceScreen().removePreference(pref);
        }
    }
}
