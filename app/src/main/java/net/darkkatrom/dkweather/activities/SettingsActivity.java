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
package net.darkkatrom.dkweather.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity implements
        PreferenceFragment.OnPreferenceStartFragmentCallback {

    public static final String KEY_SETTINGS_SCREEN = "settings_screen";

    private static final String THEME_COLORS_FRAGMENT_NAME =
            "net.darkkatrom.dkweather.fragments.ThemeColorsSettings";
    private static final String THEME_COLORS_WIDGET_FRAGMENT_NAME =
            "net.darkkatrom.dkweather.fragments.themecolors.ThemeColorsWidget";

    public static final int SETTINGS_MAIN                 = 0;
    public static final int SETTINGS_THEME_COLORS_WIDGET  = 1;

    private int mSettingsScreen = SETTINGS_MAIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_content, new SettingsFragment())
                    .commit();
        }

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getInt(KEY_SETTINGS_SCREEN) != SETTINGS_MAIN) {
                startPreferencePanel(THEME_COLORS_FRAGMENT_NAME, null, 0, null, null, 0);
                startPreferencePanel(THEME_COLORS_WIDGET_FRAGMENT_NAME, null, 0, null, null, 0);
            }
        }

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.settings;
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        startPreferencePanel(pref.getFragment(), pref.getExtras(), pref.getTitleRes(),
                pref.getTitle(), null, 0);
        return true;
    }

    public void startPreferencePanel(String fragmentClass, Bundle args, int titleRes,
        CharSequence titleText, Fragment resultTo, int resultRequestCode) {
        Fragment f = Fragment.instantiate(this, fragmentClass, args);
        if (resultTo != null) {
            f.setTargetFragment(resultTo, resultRequestCode);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, f);
        if (titleRes != 0) {
            transaction.setBreadCrumbTitle(titleRes);
        } else if (titleText != null) {
            transaction.setBreadCrumbTitle(titleText);
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    public void recreateForThemeChange() {
        recreate();
    }
}
