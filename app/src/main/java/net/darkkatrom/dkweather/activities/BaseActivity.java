/*
 * Copyright (C) 2017 DarkKat
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

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.utils.Config;
import net.darkkatrom.dkweather.utils.ThemeUtil;

public class BaseActivity extends Activity {

    private AppCompatDelegate mDelegate;

    private int mThemeResId = 0;
    private int mThemeOverlayAccentResId = 0;
    private int mThemeOverlayTextResId = 0;
    private int mThemeOverlayRippleResId = 0;
    private boolean mLightStatusBar = false;
    private boolean mLightNavigationBar = false;
    private boolean mCustomizeColors = false;
    private int mStatusBarColor = 0;
    private int mNavigationColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        updateTheme();

        super.onCreate(savedInstanceState);

        int layoutResid = getLayoutResId();
        if (layoutResid > 0) {
            setContentView(R.layout.main);
            setContentView(layoutResid);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (Config.getThemeCustomizeColors(this)) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                        ThemeUtil.getActionBarBackgroundColor(this)));
            }
        }
    }

    protected int getLayoutResId() {
        return 0;
    }

    protected boolean checkForThemeUpdate() {
        return false;
    }

    private void updateTheme() {
        mThemeResId = ThemeUtil.getThemeResId(this);
        if (Config.getIndexForAccentColor(this) > 0) {
            mThemeOverlayAccentResId = ThemeUtil.getThemeOverlayAccentResId(this);
        }
        mThemeOverlayTextResId = ThemeUtil.getThemeOverlayTextResId(this);
        mThemeOverlayRippleResId = ThemeUtil.getThemeOverlayRippleResId(this);
        mLightStatusBar = ThemeUtil.needsLightStatusBar(this);
        mLightNavigationBar = ThemeUtil.needsLightNavigationBar(this);
        mCustomizeColors = Config.getThemeCustomizeColors(this);
        mStatusBarColor = ThemeUtil.getStatusBarBackgroundColor(this);
        mNavigationColor = ThemeUtil.getNavigationBarBackgroundColor(this);

        setTheme(mThemeResId);
        if (mThemeOverlayAccentResId > 0) {
            getTheme().applyStyle(mThemeOverlayAccentResId, true);
        }
        if (mThemeOverlayTextResId > 0) {
            getTheme().applyStyle(mThemeOverlayTextResId, true);
        }
        if (mThemeOverlayRippleResId > 0) {
            getTheme().applyStyle(mThemeOverlayRippleResId, true);
        }

        int oldFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newFlags = oldFlags;
        if (!mLightStatusBar) {
            // Check if light status bar flag was set
            boolean isLightStatusBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (isLightStatusBar) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }
        if (!mLightNavigationBar) {
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

        if (mCustomizeColors) {
            getWindow().setStatusBarColor(mStatusBarColor);
        }
        if (mNavigationColor != 0) {
            getWindow().setNavigationBarColor(mNavigationColor);
        }
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkForThemeUpdate()) {
            int themeResId = ThemeUtil.getThemeResId(this);
            int themeOverlayAccentResId = Config.getIndexForAccentColor(this) > 0
                    ? ThemeUtil.getThemeOverlayAccentResId(this) : 0;
            int themeOverlayTextResId = Config.getThemeUseDarkTheme(this)
                    ? ThemeUtil.getThemeOverlayDarkTextResId(this)
                    : ThemeUtil.getThemeOverlayLightTextResId(this);
            int themeOverlayRippleResId = ThemeUtil.getThemeOverlayRippleResId(this);
            boolean lightStatusBar = ThemeUtil.needsLightStatusBar(this);
            boolean lightNavigationBar = ThemeUtil.needsLightNavigationBar(this);
            boolean customizeColors = Config.getThemeCustomizeColors(this);
            int statusBarColor = ThemeUtil.getStatusBarBackgroundColor(this);
            int navigationColor = ThemeUtil.getNavigationBarBackgroundColor(this);

            if (mThemeResId != themeResId
                    || mThemeOverlayAccentResId != themeOverlayAccentResId
                    || mThemeOverlayTextResId != themeOverlayTextResId
                    || mThemeOverlayRippleResId != themeOverlayRippleResId
                    || mLightStatusBar != lightStatusBar
                    || mLightNavigationBar != lightNavigationBar
                    || mCustomizeColors != customizeColors
                    || mStatusBarColor != statusBarColor
                    || mNavigationColor != navigationColor) {
                recreate();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }
}
