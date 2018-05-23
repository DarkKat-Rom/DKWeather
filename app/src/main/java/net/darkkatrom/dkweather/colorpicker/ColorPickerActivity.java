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
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.colorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkweather.utils.Config;
import net.darkkatrom.dkweather.utils.ThemeUtil;

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
        setTheme(ThemeUtil.getThemeResId(this));

        int oldFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newFlags = oldFlags;
        if (!ThemeUtil.needsLightStatusBar(this)) {
            // Check if light status bar flag was set
            boolean isLightStatusBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (isLightStatusBar) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }
        if (!ThemeUtil.needsLightNavigationBar(this)) {
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

        if (Config.getThemeCustomizeColors(this)) {
            getWindow().setStatusBarColor(ThemeUtil.getStatusBarBackgroundColor(this));
            getActionBar().setBackgroundDrawable(new ColorDrawable(
                    ThemeUtil.getActionBarBackgroundColor(this)));
        }
        int customNavigationBarColor = ThemeUtil.getNavigationBarBackgroundColor(this);
        if (customNavigationBarColor != 0) {
            getWindow().setNavigationBarColor(customNavigationBarColor);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
