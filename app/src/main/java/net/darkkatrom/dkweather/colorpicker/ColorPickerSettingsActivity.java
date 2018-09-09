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
package net.darkkatrom.dkweather.colorpicker;

import android.os.Bundle;
import android.preference.Preference;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.activities.BaseActivity;
import net.darkkatrom.dkweather.colorpicker.fragment.ColorPickerSettingsFragment;

public class ColorPickerSettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_content, new ColorPickerSettingsFragment())
                    .commit();
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.settings;
    }
}
