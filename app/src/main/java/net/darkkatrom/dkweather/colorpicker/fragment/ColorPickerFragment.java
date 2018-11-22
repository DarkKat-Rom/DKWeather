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

package net.darkkatrom.dkweather.colorpicker.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.utils.ColorUtil;
import net.darkkatrom.dkweather.utils.Config;
import net.darkkatrom.dkweather.utils.ThemeUtil;
import net.darkkatrom.dkweather.colorpicker.ColorPickerActivity;
import net.darkkatrom.dkweather.colorpicker.ColorPickerSettingsActivity;
import net.darkkatrom.dkweather.colorpicker.adapter.ColorPickerCardAdapter;
import net.darkkatrom.dkweather.colorpicker.animator.BaseItemAnimator;
import net.darkkatrom.dkweather.colorpicker.animator.ColorPickerCardAnimator;
import net.darkkatrom.dkweather.colorpicker.animator.ColorPickerFragmentAnimator;
import net.darkkatrom.dkweather.colorpicker.model.ColorPickerCard;
import net.darkkatrom.dkweather.colorpicker.model.ColorPickerColorCard;
import net.darkkatrom.dkweather.colorpicker.model.ColorPickerFavoriteCard;
import net.darkkatrom.dkweather.colorpicker.preference.ColorPickerPreference;
import net.darkkatrom.dkweather.colorpicker.widget.ApplyColorView;
import net.darkkatrom.dkweather.colorpicker.widget.ColorPickerView;
import net.darkkatrom.dkweather.colorpicker.widget.RadioGroupsGroup;
import net.darkkatrom.dkweather.colorpicker.util.ColorPickerHelper;
import net.darkkatrom.dkweather.colorpicker.util.ConfigColorPicker;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerFragment extends Fragment implements
        ColorPickerView.OnColorChangedListener, TextWatcher, View.OnClickListener,
        View.OnFocusChangeListener, RadioGroup.OnCheckedChangeListener,
        RadioGroupsGroup.OnCheckedChangeListener, ColorPickerCardAdapter.OnCardClickedListener {

    public static final String TAG = "ColorPickerFragment";

    public static final String KEY_TITLE                    = "picker_title";
    public static final String KEY_SUBTITLE                 = "picker_subtitle";
    public static final String KEY_ADDITIONAL_SUBTITLE      = "picker_additional_subtitle";
    public static final String KEY_INITIAL_COLOR            = "initial_color";
    public static final String KEY_NEW_COLOR                = "new_color";
    public static final String KEY_OLD_COLOR                = "old_color";
    public static final String KEY_RESET_COLOR_1            = "reset_color_1";
    public static final String KEY_RESET_COLOR_2            = "reset_color_2";
    public static final String KEY_RESET_COLOR_1_TITLE      = "reset_color_1_Title";
    public static final String KEY_RESET_COLOR_2_TITLE      = "reset_color_2_Title";
    public static final String KEY_ALPHA_SLIDER_VISIBLE     = "alpha_slider_visible";
    public static final String KEY_FAVORITE_BASE            = "color_picker_favorite_";
    public static final String KEY_ADDITION_SUBTITLE        = "_subtitle";
    public static final String KEY_HELP_SCREEN_VISIBILITY   = "help_screen_Visibility";

    private static final int HELP_SCREEN_VISIBILITY_DEFAULT = 0;
    public static final int HELP_SCREEN_VISIBILITY_VISIBLE = 1;
    public static final int HELP_SCREEN_VISIBILITY_GONE    = 2;

    public static final int NUM_MAX_FAVORITES = 10;

    private Resources mResources;

    private ApplyColorView mApplyColorAction;
    private MenuItem mShowEditHexAction;
    private EditText mEditHexValue;

    private ViewGroup mColorPickerView;
    private TextView mAdditionalSubtitleView;
    private ColorPickerView mColorPicker;

    private RadioGroup mChipsGroup;
    private RadioGroupsGroup mChipsGroupsGroup;
    private int mOldMainButtonsCheckedId = -1;
    private int mMainButtonsCheckedId = -1;

    private RecyclerView mContentList;

    private View mHelpScreen;
    private CheckedTextView mCheckShowHelpScreen;
    private Button mCloseHelpScreen;

    private CharSequence mTitle;
    private CharSequence mSubtitle;
    private CharSequence mAdditionalSubtitle;
    private String mPreferenceKey;
    private int mInitialColor;
    private int mNewColorValue;
    private int mOldColorValue;
    private int mResetColor1;
    private int mResetColor2;
    private CharSequence mResetColor1Title;
    private CharSequence mResetColor2Title;
    private boolean mShowHelpScreen;
    private boolean mHelpScreenVisible;

    private boolean mHideResetColor1 = true;
    private boolean mHideResetColor2 = true;
    private boolean mShowSubMenu = false;

    private ColorPickerFragmentAnimator mFragmentAnimator;

    private ColorPickerCardAdapter mCardAdapter = null;
    private List<ColorPickerCard> mColorPickerCards;
    private int[] mFavoriteColors;
    private int mItemCount = 0;
    private int mDisabledCardClickedItem = -1;
    private int mAllowDeleteType;

    private OnColorChangedListener mListener;

    public interface OnColorChangedListener {
        public void onColorChanged(int color);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflateAndSetupView(inflater, container, savedInstanceState);
    }

    private View inflateAndSetupView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mResources = getActivity().getResources();

        mColorPickerView = (ViewGroup) inflater.inflate(R.layout.color_picker_fragment, container, false);
        mAdditionalSubtitleView = (TextView) mColorPickerView.findViewById(R.id.color_picker_additional_subtitle);
        mColorPicker = (ColorPickerView) mColorPickerView.findViewById(R.id.color_picker_view);
        mChipsGroup = (RadioGroup) mColorPickerView.findViewById(R.id.color_picker_chips_group);
        mChipsGroupsGroup = (RadioGroupsGroup) mColorPickerView.findViewById(R.id.color_picker_chips_groups_group);
        mContentList = (RecyclerView) mColorPickerView.findViewById(R.id.color_picker_content_list);
        inflateHelpScreen();

        if (getArguments() != null) {
            mPreferenceKey = getArguments().getString(ColorPickerPreference.PREFERENCE_KEY);
            mTitle = getArguments().getCharSequence(KEY_TITLE);
            mSubtitle = getArguments().getCharSequence(KEY_SUBTITLE);
            mAdditionalSubtitle = getArguments().getCharSequence(KEY_ADDITIONAL_SUBTITLE);
            mInitialColor = getArguments().getInt(KEY_INITIAL_COLOR);
            if (getArguments().getInt(KEY_NEW_COLOR) != 0) {
                mNewColorValue = getArguments().getInt(KEY_NEW_COLOR);
            } else {
                mNewColorValue = mInitialColor;
                getArguments().putInt(KEY_NEW_COLOR, mNewColorValue);
            }
            if (getArguments().getInt(KEY_OLD_COLOR) != 0) {
                mOldColorValue = getArguments().getInt(KEY_OLD_COLOR);
            } else {
                mOldColorValue = mInitialColor;
                getArguments().putInt(KEY_OLD_COLOR, mOldColorValue);
            }
            mResetColor1 = getArguments().getInt(KEY_RESET_COLOR_1);
            mResetColor2 = getArguments().getInt(KEY_RESET_COLOR_2);
            mResetColor1Title = getArguments().getCharSequence(KEY_RESET_COLOR_1_TITLE);
            mResetColor2Title = getArguments().getCharSequence(KEY_RESET_COLOR_2_TITLE);
            mShowHelpScreen = getShowHelpScreen();
            mHelpScreenVisible = resolveHelpScreenVisibility(
                    getArguments().getInt(KEY_HELP_SCREEN_VISIBILITY));

        }

        ColorPickerActivity activity = (ColorPickerActivity) getActivity();
        if (activity.getSupportActionBar() != null) {
            if (mTitle != null) {
                activity.getSupportActionBar().setTitle(mTitle);
            }
            if (mSubtitle != null) {
                activity.getSupportActionBar().setSubtitle(mSubtitle);
            }
        }

        if (mAdditionalSubtitle != null) {
            mAdditionalSubtitleView.setVisibility(View.VISIBLE);
            mAdditionalSubtitleView.setText(mAdditionalSubtitle);
        }

        mColorPicker.setOnColorChangedListener(this);
        mColorPicker.setColor(mNewColorValue);
        mColorPicker.setBorderColor(ThemeUtil.getDefaultHighlightColor(getActivity()));

        if (mChipsGroup != null) {
            mChipsGroup.setOnCheckedChangeListener(this);
        }
        if (mChipsGroupsGroup != null) {
            mChipsGroupsGroup.setOnCheckedChangeListener(this);
        }
        if (getArguments() != null && getArguments().getBoolean(KEY_ALPHA_SLIDER_VISIBLE)) {
            mColorPicker.setAlphaSliderVisible(true);
        }

        mFragmentAnimator = new ColorPickerFragmentAnimator(getActivity(),
                ColorPickerFragment.this, mHelpScreenVisible);

        setAllFavoriteColors();
        setUpResetMenuAppearience();

        mAllowDeleteType = ConfigColorPicker.getAllowDeleteType(getActivity());

        return mColorPickerView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final boolean isSavedState = savedInstanceState != null;
        view.post(new Runnable() {
            @Override
            public void run() {
                if (!mHelpScreenVisible) {
                    float translationX = mHelpScreen.getWidth();
                    mHelpScreen.setTranslationX(translationX);
                    mHelpScreen.setVisibility(View.GONE);
                }
                mColorPicker.setTranslationX(mColorPickerView.findViewById(R.id.color_picker_content).getWidth());
                mContentList.setTranslationX(mColorPickerView.findViewById(R.id.color_picker_content).getWidth());

                setUpHelpScreen();
                setUpMainContent(isSavedState);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAllowDeleteType != ConfigColorPicker.getAllowDeleteType(getActivity())) {
            mAllowDeleteType = ConfigColorPicker.getAllowDeleteType(getActivity());
            if (mMainButtonsCheckedId == R.id.color_picker_chip_favorites) {
                if (!(mContentList.getItemAnimator() instanceof ColorPickerCardAnimator)) {
                    mContentList.setItemAnimator(new ColorPickerCardAnimator());
                }
                mCardAdapter.notifyItemRangeChanged(0, mItemCount);
            }
        }
    }

    private void inflateHelpScreen() {
        // As the help sceen always uses a dark background, create a themed context
        ContextThemeWrapper themedContext =
                new ContextThemeWrapper(getActivity(), android.R.style.Theme_Material);
        int themeOverlayAccentResId = ThemeUtil.getThemeOverlayAccentResId(getActivity(), true);
        int themeOverlayTextResId = ThemeUtil.getThemeOverlayTextResId(getActivity(), true);
        int themeOverlayRippleResId = ThemeUtil.getThemeOverlayRippleResId(getActivity(), true);
        int accentColor = ThemeUtil.getColorFromThemeAttribute(getActivity(), R.attr.colorAccent);
        int themeOverlayColoredBackground = ColorUtil.isColorDark(accentColor)
                ? R.style.ThemeOverlay_HelpScreen_ColoredBackgroundDark
                : R.style.ThemeOverlay_HelpScreen_ColoredBackgroundLight;
        int themeOverlayBackground = Config.getThemeUseDarkTheme(getActivity())
                ? R.style.ThemeOverlay_HelpScreen_Dark
                : R.style.ThemeOverlay_HelpScreen_Light;

        if (themeOverlayAccentResId > 0) {
            // Apply the (custom) accent color to the themed context
            themedContext.getTheme().applyStyle(themeOverlayAccentResId, true);
        }
        if (themeOverlayTextResId > 0) {
            // Apply the (custom) dark text color to the themed context
            themedContext.getTheme().applyStyle(themeOverlayTextResId, true);
        }
        if (themeOverlayRippleResId > 0) {
            // Apply the (custom) dark ripple effect color to the themed context
            themedContext.getTheme().applyStyle(themeOverlayRippleResId, true);
        }

        // Apply needed colors related to backgrounds using the accent color:
        // (dark on light backgrounds and vise versa)
        // - Text color
        // - Icon color
        // - Ripple effect color
        themedContext.getTheme().applyStyle(themeOverlayColoredBackground, true);

        // Apply black / translucent black background on light/dark app theme
        themedContext.getTheme().applyStyle(themeOverlayBackground, true);

        // Inflate the help screen using the themed context
        LayoutInflater inflater =
                (LayoutInflater) themedContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mHelpScreen = inflater.inflate(R.layout.color_picker_help_screen, mColorPickerView, false);
        mColorPickerView.addView(mHelpScreen);
    }

    private void setAllFavoriteColors() {
        mFavoriteColors = new int[NUM_MAX_FAVORITES];

        for (int i = 0; i < NUM_MAX_FAVORITES; i++) {
            mFavoriteColors[i] = getFavoriteColor(i + 1);
        }
    }

    private void setUpResetMenuAppearience() {
        if (mResetColor1 == Color.TRANSPARENT) {
            if (mResetColor2 != Color.TRANSPARENT) {
                mResetColor2 = Color.TRANSPARENT;
                Log.w(TAG, "Reset color 1 has not been set, ignore reset color 2 value");
            }
            if (mResetColor1Title != null) {
                mResetColor1Title = null;
                Log.w(TAG, "Reset color 1 has not been set, ignore reset color 1 title");
            }
            if (mResetColor2Title != null) {
                mResetColor2Title = null;
                Log.w(TAG, "Reset color 1 has not been set, ignore reset color 2 title");
            }
        } else if (mResetColor2 == Color.TRANSPARENT) {
            if (mResetColor2Title != null) {
                mResetColor2Title = null;
                Log.w(TAG, "Reset color 2 has not been set, ignore reset color 2 title");
            }
        }

        if (mResetColor1 != 0) {
            mHideResetColor1 = false;
            if (mResetColor2 != 0) {
                mHideResetColor2 = false;
                mShowSubMenu = true;
            }
        }
    }

    private void setUpHelpScreen() {
        WebView wv = (WebView) mColorPickerView.findViewById(R.id.help_screen_html_content);

        // As there is no difference for the accent color on light or dark themes,
        // we can resolve the accent color for the web view using the current app theme.
        int accentColor = ThemeUtil.getColorFromThemeAttribute(getActivity(), R.attr.colorAccent);
        // The help screen may uses a different primary/secondary text color,
        // so we have to resolve the primary/secondary text color for the web view using the help screen theme.
        int primaryTextColor = ThemeUtil.getColorFromThemeAttribute(
                mHelpScreen.getContext(), android.R.attr.textColorPrimary);
        // Convert 'color ints' to 'color strings'
        String accentColorString = ColorPickerHelper.convertToRGB(accentColor);
        String primaryTextColorString =
                ColorPickerHelper.convertToRGBAForWebView(primaryTextColor, "1");
        String secondaryTextColorString =
                ColorPickerHelper.convertToRGBAForWebView(primaryTextColor, "0.7");
        // Get the colorized web view 'html string' content
        String content = getString(R.string.color_picker_help_main_content, accentColorString,
                primaryTextColorString, secondaryTextColorString);

        wv.getSettings().setJavaScriptEnabled(true);
        wv.setBackgroundColor(0);
        wv.loadData(content, "text/html", "utf-8");
        mCheckShowHelpScreen = (CheckedTextView) mColorPickerView.findViewById(
                R.id.color_picker_check_show_help_screen);
        mCheckShowHelpScreen.setChecked(!mShowHelpScreen);
        mCheckShowHelpScreen.setOnClickListener(this);
        mCloseHelpScreen = (Button) mColorPickerView.findViewById(
                R.id.color_picker_help_button_ok);
        mCloseHelpScreen.setOnClickListener(this);

        mHelpScreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void setUpMainContent(boolean isSavedState) {
        if (mChipsGroup != null) {
            mChipsGroup.check(ConfigColorPicker.getChipChededId(getActivity(), isSavedState));
            final HorizontalScrollView scroller =
                    (HorizontalScrollView) mColorPickerView.findViewById(R.id.color_picker_chips_scroller);
            final View checkedView = mColorPickerView.findViewById(mMainButtonsCheckedId);
            scroller.post(new Runnable() {
                @Override
                public void run() {
                    scroller.scrollTo((int) checkedView.getX(), 0);
                }
            });
        }
        if (mChipsGroupsGroup != null) {
            mChipsGroupsGroup.check(ConfigColorPicker.getChipChededId(getActivity(), isSavedState));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.color_picker_ab_more, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (mHideResetColor1) {
            menu.removeItem(R.id.reset_colors);
            menu.removeItem(R.id.reset_color);
        } else {
            if (mShowSubMenu) {
                menu.removeItem(R.id.reset_color);
                if (mResetColor1Title != null) {
                    menu.findItem(R.id.reset_colors).getSubMenu()
                            .findItem(R.id.reset_color1).setTitle(mResetColor1Title);
                }
                if (mResetColor2Title != null) {
                    menu.findItem(R.id.reset_colors).getSubMenu()
                    .findItem(R.id.reset_color2).setTitle(mResetColor2Title);
                }
            } else {
                menu.removeItem(R.id.reset_colors);
                if (mResetColor1Title != null) {
                    menu.findItem(R.id.reset_color).setTitle(mResetColor1Title);
                }
            }
        }

        MenuItem applyColor = menu.findItem(R.id.apply_color);
        mApplyColorAction = (ApplyColorView) applyColor.getActionView();
        mShowEditHexAction = menu.findItem(R.id.edit_hex);
        LinearLayout editHexActionView = (LinearLayout) mShowEditHexAction.getActionView();
        mEditHexValue = (EditText) editHexActionView.findViewById(R.id.ab_edit_hex);
        ImageButton setHexValueButton = (ImageButton) editHexActionView.findViewById(R.id.ab_edit_hex_enter);
        MenuItem showHideHelp = menu.findItem(R.id.show_hide_help);

        boolean newColor = mNewColorValue != mInitialColor;
        int helpTitleResId = mHelpScreenVisible ? R.string.hide_help_title : R.string.show_help_title;
        float translationX = mResources.getDimension(
                R.dimen.color_picker_action_apply_color_translation_x);

        mApplyColorAction.setColor(mNewColorValue);
        mApplyColorAction.setColorPreviewTranslationX(newColor ? 0f : translationX);
        mApplyColorAction.showSetIcon(newColor ? true : false);
        mApplyColorAction.applySetIconAlpha(newColor ? 1f : 0f);
        mApplyColorAction.setOnClickListener(newColor ? this : null);

        mEditHexValue.setText(ColorPickerHelper.convertToARGB(mNewColorValue));
        mEditHexValue.setOnFocusChangeListener(this);
        setHexValueButton.setOnClickListener(this);

        showHideHelp.setTitle(mResources.getString(helpTitleResId));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        onCheckedChanged(checkedId);
    }

    @Override
    public void onCheckedChanged(RadioGroupsGroup group, int checkedId) {
        onCheckedChanged(checkedId);
    }

    private void onCheckedChanged(int checkedId) {
        if (checkedId == -1) {
            return;
        }
        if (mMainButtonsCheckedId != checkedId) {
            mMainButtonsCheckedId = checkedId;
            ConfigColorPicker.setChipChededId(getActivity(), mMainButtonsCheckedId);
            if (mMainButtonsCheckedId != mOldMainButtonsCheckedId) {
                Boolean animate = mMainButtonsCheckedId == R.id.color_picker_chip_pick
                        || mOldMainButtonsCheckedId == R.id.color_picker_chip_pick
                        || mOldMainButtonsCheckedId == -1;
                if (animate) {
                    mContentList.setItemAnimator(null);
                } else {
                    if (mContentList.getItemAnimator() instanceof ColorPickerCardAnimator
                            || mContentList.getItemAnimator() == null) {
                        mContentList.setItemAnimator(new BaseItemAnimator());
                    }
                }
                if (mMainButtonsCheckedId != R.id.color_picker_chip_pick) {
                    buildOrClearCardList();
                }
                if (animate) {
                    View view = mMainButtonsCheckedId == R.id.color_picker_chip_pick
                                ? mColorPicker : mContentList;
                    if (mOldMainButtonsCheckedId == -1) {
                        mFragmentAnimator.animateShow(view,
                                mColorPickerView.findViewById(R.id.color_picker_content).getWidth());
                    } else {
                        View oldView = mOldMainButtonsCheckedId == R.id.color_picker_chip_pick
                                ? mColorPicker : mContentList;
                        mFragmentAnimator.animateSwitch(oldView, view,
                                mColorPickerView.findViewById(R.id.color_picker_content).getWidth());
                    }
                }
                mOldMainButtonsCheckedId = mMainButtonsCheckedId;
            }
        }
    }

    public void setOldColorValue(int oldColor) {
        mOldColorValue = oldColor;
    }

    public void setIsHelpScreenVisible(boolean visible) {
        mHelpScreenVisible = visible;
        getActivity().invalidateOptionsMenu();
    }

    private void buildOrUpdateFavoriteCards() {
        for (int i = 0; i < NUM_MAX_FAVORITES; i++) {
            int favoriteNumber = i + 1;
            int color = getFavoriteColor(favoriteNumber);
            String title = color == 0
                    ? (getActivity().getResources().getString(R.string.favorite_title) + " " + favoriteNumber)
                    : ColorPickerHelper.getColorTitle(getActivity(), color);
            String subtitle = color == 0
                    ? "0"
                    : ColorPickerHelper.convertToARGB(color);

            String paletteTitle = color == 0
                    ? getActivity().getResources().getString(R.string.empty_title)
                    : ColorPickerHelper.getPaletteTitle(getActivity(), color);

            ColorPickerCard card = new ColorPickerFavoriteCard(getActivity(), title, subtitle, color, paletteTitle);
            mColorPickerCards.add(card);
        }
        mItemCount = NUM_MAX_FAVORITES;
        buildOrUpdateCardAdapter();
    }

    private void buildOrUpdateDarkKatCards() {
        TypedArray titles = mResources.obtainTypedArray(R.array.color_picker_darkkat_palette_titles);
        TypedArray colors = mResources.obtainTypedArray(R.array.color_picker_darkkat_palette);
        for (int i = 0; i < 8; i++) {
            ColorPickerCard card = new ColorPickerColorCard(getActivity(),
                    titles.getResourceId(i, 0), colors.getResourceId(i, 0));
            mColorPickerCards.add(card);
        }
        titles.recycle();
        colors.recycle();
        mItemCount = 8;
        buildOrUpdateCardAdapter();
    }

    private void buildOrUpdateMaterialCards() {
        TypedArray titles = mResources.obtainTypedArray(R.array.color_picker_material_palette_titles);
        TypedArray colors = mResources.obtainTypedArray(R.array.color_picker_material_palette);
        for (int i = 0; i < 17; i++) {
            ColorPickerCard card = new ColorPickerColorCard(getActivity(),
                    titles.getResourceId(i, 0), colors.getResourceId(i, 0));
            mColorPickerCards.add(card);
        }
        titles.recycle();
        colors.recycle();
        mItemCount = 17;
        buildOrUpdateCardAdapter();
    }

    private void buildOrUpdateHoloCards() {
        TypedArray titles = mResources.obtainTypedArray(R.array.color_picker_holo_palette_titles);
        TypedArray colors = mResources.obtainTypedArray(R.array.color_picker_holo_palette);
        for (int i = 0; i < 10; i++) {
            ColorPickerCard card = new ColorPickerColorCard(getActivity(),
                    titles.getResourceId(i, 0), colors.getResourceId(i, 0));
            mColorPickerCards.add(card);
        }
        titles.recycle();
        colors.recycle();
        mItemCount = 10;
        buildOrUpdateCardAdapter();
    }

    private void buildOrUpdateRGBCards() {
        TypedArray titles = mResources.obtainTypedArray(R.array.color_picker_rgb_palette_titles);
        TypedArray colors = mResources.obtainTypedArray(R.array.color_picker_rgb_palette);
        for (int i = 0; i < 8; i++) {
            ColorPickerCard card = new ColorPickerColorCard(getActivity(),
                    titles.getResourceId(i, 0), colors.getResourceId(i, 0));
            mColorPickerCards.add(card);
        }
        titles.recycle();
        colors.recycle();
        mItemCount = 8;
        buildOrUpdateCardAdapter();
    }

    private void buildOrClearCardList() {
        if (mColorPickerCards == null) {
            mColorPickerCards = new ArrayList<ColorPickerCard>();
        } else {
            if (mCardAdapter != null && mItemCount > 0) {
                mColorPickerCards.clear();
                mDisabledCardClickedItem = -1;
                mCardAdapter.notifyItemRangeRemoved(0, mItemCount);
            }
        }

        if (mMainButtonsCheckedId == R.id.color_picker_chip_favorites) {
            buildOrUpdateFavoriteCards();
        } else if (mMainButtonsCheckedId == R.id.color_picker_chip_darkkat) {
            buildOrUpdateDarkKatCards();
        } else if (mMainButtonsCheckedId == R.id.color_picker_chip_material) {
            buildOrUpdateMaterialCards();
        } else if (mMainButtonsCheckedId == R.id.color_picker_chip_holo) {
            buildOrUpdateHoloCards();
        } else if (mMainButtonsCheckedId == R.id.color_picker_chip_rgb) {
            buildOrUpdateRGBCards();
        }
    }

    private void buildOrUpdateCardAdapter() {
        if (mCardAdapter == null) {
            mCardAdapter = new ColorPickerCardAdapter(getActivity(), mColorPickerCards, mInitialColor,
                    mNewColorValue, mFavoriteColors);
            mCardAdapter.setOnCardClickedListener(this);
        } else {
            mCardAdapter.setNewColor(mNewColorValue);
        }
        if (mContentList.getAdapter() == null) {
            mContentList.setAdapter(mCardAdapter);
        }
        if (mItemCount > 0) {
            mCardAdapter.notifyItemRangeInserted(0, mItemCount);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.reset_color || item.getItemId() == R.id.reset_color1) {
            mColorPicker.setColor(mResetColor1, true);
            return true;
        } else if (item.getItemId() == R.id.reset_color2) {
            mColorPicker.setColor(mResetColor2, true);
            return true;
        } else if (item.getItemId() == R.id.edit_hex) {
            mEditHexValue.setText(ColorPickerHelper.convertToARGB(mNewColorValue));
            return true;
        } else if (item.getItemId() == R.id.show_hide_help) {
            showHideHelpScreen();
            return true;
        } else if (item.getItemId() == R.id.color_picker_settings) {
            Intent intent = new Intent(getActivity(), ColorPickerSettingsActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.color_picker_apply_color_action_layout) {
            Intent data = new Intent();
            data.putExtra(KEY_NEW_COLOR, mApplyColorAction.getColor());
            data.putExtra(ColorPickerPreference.PREFERENCE_KEY, mPreferenceKey);
            getActivity().setResult(Activity.RESULT_OK, data);
            getActivity().finish();
        } else if (v.getId() == R.id.ab_edit_hex_enter) {
            String text = mEditHexValue.getText().toString();
            mShowEditHexAction.collapseActionView();
            mColorPicker.setColor(ColorPickerHelper.convertToColorInt(text), true);
        } else if (v.getId() == R.id.color_picker_check_show_help_screen) {
            mCheckShowHelpScreen.toggle();
            putShowHelpScreen(!mCheckShowHelpScreen.isChecked());
        } else if (v.getId() == R.id.color_picker_help_button_ok) {
            showHideHelpScreen();
        }
    }

    @Override
    public void onColorChanged(int color) {
        int animationType = ColorPickerFragmentAnimator.NO_ANIMATION;
        if (color != mOldColorValue) {
            mNewColorValue = color;
            if (mCardAdapter != null) {
                mCardAdapter.setNewColor(mNewColorValue);
            }
            getArguments().putInt(KEY_NEW_COLOR, mNewColorValue);
            if (mNewColorValue == mInitialColor) {
                if (mOldColorValue != mInitialColor) {
                    animationType = ColorPickerFragmentAnimator.ANIMATE_TO_HIDE;
                    mApplyColorAction.setOnClickListener(null);
                    mApplyColorAction.setClickable(false);
                }
            } else if (mOldColorValue == mInitialColor) {
                animationType = ColorPickerFragmentAnimator.ANIMATE_TO_SHOW;
                mApplyColorAction.showSetIcon(true);
            }
            mFragmentAnimator.animateColorTransition(mOldColorValue, mNewColorValue, animationType,
                    mApplyColorAction);

            try {
                if (mEditHexValue != null) {
                    mEditHexValue.setText(ColorPickerHelper.convertToARGB(color));
                }
            } catch (Exception e) {}
        }

    }

    @Override
    public void onCardClicked(int color, int position) {
        try {
            if (color != mOldColorValue) {
                mColorPicker.setColor(color, true);
                if (mContentList.getAdapter() != null) {
                    if (!(mContentList.getItemAnimator() instanceof ColorPickerCardAnimator)) {
                        mContentList.setItemAnimator(new ColorPickerCardAnimator());
                    }
                    mCardAdapter.notifyItemChanged(position);
                    if (mDisabledCardClickedItem > -1) {
                        mCardAdapter.notifyItemChanged(mDisabledCardClickedItem);
                    }
                    mDisabledCardClickedItem = position;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void onCardActionApplyClicked(int color) {
        Intent data = new Intent();
        data.putExtra(KEY_NEW_COLOR, color);
        data.putExtra(ColorPickerPreference.PREFERENCE_KEY, mPreferenceKey);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }

    @Override
    public void onFavoriteCardActionFavoriteClicked(ColorPickerCard oldCard, int position, boolean addFavorite) {
        if (!(mContentList.getItemAnimator() instanceof ColorPickerCardAnimator)) {
            mContentList.setItemAnimator(new ColorPickerCardAnimator());
        }
        int favoriteNumber = position + 1;
        int color = addFavorite ? mNewColorValue : 0;
        String title = !addFavorite
                ? (getActivity().getResources().getString(R.string.favorite_title) + " " + favoriteNumber)
                : ColorPickerHelper.getColorTitle(getActivity(), color);
        String subtitle = !addFavorite
                ? "0"
                : ColorPickerHelper.convertToARGB(color);
        String paletteTitle = !addFavorite
                ? getActivity().getResources().getString(R.string.empty_title)
                : ColorPickerHelper.getPaletteTitle(getActivity(), color);

        ColorPickerCard newCard = new ColorPickerFavoriteCard(getActivity(), title, subtitle, color, paletteTitle);

        setFavoriteColor(favoriteNumber, color);
        setFavoriteSubtitle(favoriteNumber, subtitle);
        mColorPickerCards.remove(position);
        mColorPickerCards.add(position, newCard);
        mFavoriteColors[position] = color;
        mCardAdapter.notifyItemChanged(position);
    }

    @Override
    public void onColorCardActionFavoriteClicked(ColorPickerCard card, int position, boolean isFavorite) {
        if (!(mContentList.getItemAnimator() instanceof ColorPickerCardAnimator)) {
            mContentList.setItemAnimator(new ColorPickerCardAnimator());
        }
        int color = card.getColor();
        if (!isFavorite) {
            int firstEmptyFavorite = -1;
            for (int i = 0; i < NUM_MAX_FAVORITES; i++) {
                if (mFavoriteColors[i] == 0) {
                    firstEmptyFavorite = i + 1;
                    mFavoriteColors[i] = color;
                    break;
                }
            }
            if (firstEmptyFavorite != -1) {
                setFavoriteColor(firstEmptyFavorite, color);
                setFavoriteSubtitle(firstEmptyFavorite, card.getTitle());
            }
        } else {
            int favoriteNumber = -1;
            for (int i = 0; i < NUM_MAX_FAVORITES; i++) {
                if (mFavoriteColors[i] == color) {
                    favoriteNumber = i + 1;
                    mFavoriteColors[i] = 0;
                    break;
                }
            }
            if (favoriteNumber != -1) {
                setFavoriteColor(favoriteNumber, 0);
                setFavoriteSubtitle(favoriteNumber, "");
            }
        }
        mCardAdapter.notifyItemChanged(position);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            mEditHexValue.removeTextChangedListener(this);
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } else {
            mEditHexValue.addTextChangedListener(this);
        }
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        mListener = listener;
    }

    private int getColor() {
        return mColorPicker.getColor();
    }

    private void putShowHelpScreen(boolean show) {
        int visibility = show
                ? HELP_SCREEN_VISIBILITY_VISIBLE : HELP_SCREEN_VISIBILITY_GONE;
        ConfigColorPicker.setShowHelpScreen(getActivity(), visibility);
    }

    private boolean getShowHelpScreen() {
        return ConfigColorPicker.getShowHelpScreen(getActivity()) == HELP_SCREEN_VISIBILITY_VISIBLE;
    }

    private boolean resolveHelpScreenVisibility(int visibility) {
        if (visibility == HELP_SCREEN_VISIBILITY_DEFAULT) {
            getArguments().putInt(KEY_HELP_SCREEN_VISIBILITY,
                    mShowHelpScreen ? HELP_SCREEN_VISIBILITY_VISIBLE : HELP_SCREEN_VISIBILITY_GONE);
            return mShowHelpScreen;
        } else {
            return visibility == HELP_SCREEN_VISIBILITY_VISIBLE ? true : false;
        }
    }

    private int getFavoriteColor(int favoriteNumber) {
        return Integer.valueOf(ConfigColorPicker.getFavoriteColor(getActivity(),
                KEY_FAVORITE_BASE + favoriteNumber));
    }

    private void setFavoriteColor(int favoriteNumber, int color) {
        ConfigColorPicker.setFavoriteColor(getActivity(), KEY_FAVORITE_BASE + favoriteNumber, color);
    }

    private String getFavoriteSubtitle(int favoriteNumber) {
        return ConfigColorPicker.getFavoriteSubtitle(getActivity(),
               KEY_FAVORITE_BASE + favoriteNumber + KEY_ADDITION_SUBTITLE);
    }

    private void setFavoriteSubtitle(int favoriteNumber, String subtitle) {
        ConfigColorPicker.setFavoriteSubtitle(getActivity(),
                KEY_FAVORITE_BASE + favoriteNumber + KEY_ADDITION_SUBTITLE, subtitle);
    }

    public boolean isHelpScreenVisible() {
        return mFragmentAnimator.isHelpScreenVisible();
    }

    public void showHideHelpScreen() {
        mFragmentAnimator.animateShowHideHelpScreen(mHelpScreen);
    }
}
