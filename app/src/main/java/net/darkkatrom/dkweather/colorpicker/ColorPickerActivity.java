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

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.colorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkweather.utils.Config;

public class ColorPickerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateTheme();
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Fragment f = new ColorPickerFragment();
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                f.setArguments(extras);
            }
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, f)
                    .commit();
        }
    }

    private void updateTheme() {
        boolean useDarkTheme = Config.getThemUseDarkTheme(this);
        boolean useOptionalLightStatusBar = !useDarkTheme && Config.getThemUseLightStatusBar(this);
        boolean useOptionalLightNavigationBar = !useDarkTheme && Config.getThemUseLightNavigationBar(this);
        int themeResId = 0;

        if (useDarkTheme) {
            themeResId = R.style.AppThemeDark;
        } else if (useOptionalLightStatusBar && useOptionalLightNavigationBar) {
            themeResId = R.style.ThemeOverlay_LightStatusBar_LightNavigationBar;
        } else if (useOptionalLightStatusBar) {
            themeResId = R.style.ThemeOverlay_LightStatusBar;
        } else if (useOptionalLightNavigationBar) {
            themeResId = R.style.ThemeOverlay_LightNavigationBar;
        } else {
            themeResId = R.style.AppThemeLight;
        }
        setTheme(themeResId);

        int oldFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newFlags = oldFlags;
        if (!useOptionalLightStatusBar) {
            // Check if light status bar flag was set
            boolean isLightStatusBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (isLightStatusBar) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }
        if (!useOptionalLightNavigationBar) {
            // Check if light navigation bar flag was set
            boolean isLightNavigationBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            if (isLightNavigationBar) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        }
        if (oldFlags != newFlags) {
            getWindow().getDecorView().setSystemUiVisibility(newFlags);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
