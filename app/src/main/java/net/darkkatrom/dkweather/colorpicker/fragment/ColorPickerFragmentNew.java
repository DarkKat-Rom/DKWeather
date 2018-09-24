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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.utils.ThemeUtil;
import net.darkkatrom.dkweather.colorpicker.ColorPickerActivityNew;
import net.darkkatrom.dkweather.colorpicker.adapter.ColorPickerCardAdapter;
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

public class ColorPickerFragmentNew extends Fragment implements
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
    private static final int HELP_SCREEN_VISIBILITY_VISIBLE = 1;
    private static final int HELP_SCREEN_VISIBILITY_GONE    = 2;

    private static final int ANIMATE_TO_SHOW = 0;
    private static final int ANIMATE_TO_HIDE = 1;
    private static final int NO_ANIMATION    = 2;

    private static final int ANIMATE_COLOR_TRANSITION       = 0;
    private static final int ANIMATE_HELP_SCREEN_VISIBILITY = 2;

    public static final int NUM_MAX_FAVORITES = 10;

    private Resources mResources;

    private ApplyColorView mApplyColorAction;
    private MenuItem mShowEditHexAction;
    private EditText mEditHexValue;

    private View mColorPickerView;
    private TextView mAdditionalSubtitleView;
    private ColorPickerView mColorPicker;

    private RadioGroup mChipsGroup;
    private RadioGroupsGroup mChipsGroupsGroup;

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

    private float mFullTranslationX;
    private int mHelpScreenHeight = 0;

    private Animator mAnimator;
    private int mApplyColorIconAnimationType;
    private int mAnimationType;

    private ColorPickerCardAdapter mCardAdapter = null;
    private List<ColorPickerCard> mColorPickerCards;
    private int[] mFavoriteColors;

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

        mColorPickerView = inflater.inflate(R.layout.color_picker_fragment_new, container, false);
        mAdditionalSubtitleView = (TextView) mColorPickerView.findViewById(R.id.color_picker_additional_subtitle);
        mColorPicker = (ColorPickerView) mColorPickerView.findViewById(R.id.color_picker_view);
        mChipsGroup = (RadioGroup) mColorPickerView.findViewById(R.id.color_picker_chips_group);
        mChipsGroupsGroup = (RadioGroupsGroup) mColorPickerView.findViewById(R.id.color_picker_chips_groups_group);
        mContentList = (RecyclerView) mColorPickerView.findViewById(R.id.color_picker_content_list);
        mHelpScreen = mColorPickerView.findViewById(R.id.color_picker_help_screen);

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

        ColorPickerActivityNew activity = (ColorPickerActivityNew) getActivity();
        if (activity.getSupportActionBar() != null) {
            if (mTitle != null) {
                activity.getSupportActionBar().setTitle(mTitle);
            }
            if (mSubtitle != null) {
                activity.getSupportActionBar().setSubtitle(mSubtitle);
            }
        }

        mFullTranslationX = mResources.getDimension(
                R.dimen.color_picker_action_apply_color_translation_x);

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

        if (mAnimator == null) {
            mAnimator = createAnimator();
        }

        setAllFavoriteColors();
        setUpResetMenuAppearience();
        setUpMainContent();
        setUpHelpScreen();

        return mColorPickerView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.color_picker_ab_more_new, menu);
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

        mApplyColorAction.setColor(mNewColorValue);
        mApplyColorAction.setColorPreviewTranslationX(newColor ? 0f : mFullTranslationX);
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
        if (checkedId != -1) {
            ConfigColorPicker.setChipChededId(getActivity(), checkedId);
        }
        if (checkedId == R.id.color_picker_chip_pick) {
            mColorPicker.setVisibility(View.VISIBLE);
            mContentList.setVisibility(View.GONE);
        }
        if (checkedId == R.id.color_picker_chip_favorites) {
            mColorPicker.setVisibility(View.GONE);
            mContentList.setVisibility(View.VISIBLE);
            buildOrUpdateFavoriteCards();
        }
        if (checkedId == R.id.color_picker_chip_darkkat) {
            mColorPicker.setVisibility(View.GONE);
            mContentList.setVisibility(View.VISIBLE);
            buildOrUpdateDarkKatCards();
        }
        if (checkedId == R.id.color_picker_chip_material) {
            mColorPicker.setVisibility(View.GONE);
            mContentList.setVisibility(View.VISIBLE);
            buildOrUpdateMaterialCards();
        }
        if (checkedId == R.id.color_picker_chip_holo) {
            mColorPicker.setVisibility(View.GONE);
            mContentList.setVisibility(View.VISIBLE);
            buildOrUpdateHoloCards();
        }
        if (checkedId == R.id.color_picker_chip_rgb) {
            mColorPicker.setVisibility(View.GONE);
            mContentList.setVisibility(View.VISIBLE);
            buildOrUpdateRGBCards();
        }
    }

    private ValueAnimator createAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                float position = animation.getAnimatedFraction();
                if (mAnimationType == ANIMATE_COLOR_TRANSITION) {
                    int blended = ColorPickerHelper.getBlendColor(mOldColorValue, mNewColorValue, position);
                    mApplyColorAction.setColor(blended);
                    if (mApplyColorIconAnimationType != NO_ANIMATION) {
                        final boolean animateShow = mApplyColorIconAnimationType == ANIMATE_TO_SHOW;
                        float currentTranslationX = animateShow ? mFullTranslationX : 0f;
                        float alpha = animateShow ? 0f : 1f;
                        boolean applyAlpha = false;

                        if (animateShow) {
                            currentTranslationX = mFullTranslationX * (1f - position);
                            if (position > 0.5f) {
                                alpha = (position - 0.5f) * 2;
                                applyAlpha = true;
                            }
                        } else {
                            currentTranslationX = mFullTranslationX * position;
                            if (position <= 0.5f && position > 0f) {
                                alpha = 1f - position * 2;
                                applyAlpha = true;
                            }
                        }
                        mApplyColorAction.setColorPreviewTranslationX(currentTranslationX);
                        if (applyAlpha) {
                            mApplyColorAction.applySetIconAlpha(alpha);
                        }
                    }
                } else {
                    mHelpScreen.setTranslationY(
                            mHelpScreenHeight * (mHelpScreenVisible ? position  : 1f - position));
                    mHelpScreen.setAlpha(mHelpScreenVisible ? 1f - position : position);
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mAnimationType != ANIMATE_COLOR_TRANSITION) {
                    if (!mHelpScreenVisible) {
                        mHelpScreen.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mAnimationType == ANIMATE_COLOR_TRANSITION) {
                    if (mApplyColorIconAnimationType != NO_ANIMATION) {
                        if (mApplyColorIconAnimationType != ANIMATE_TO_SHOW) {
                            mApplyColorAction.showSetIcon(false);
                        } else {
                            mApplyColorAction.setOnClickListener(getFragmentOnClickListener());
                        }
                    }
                    mOldColorValue = mNewColorValue;
                    getArguments().putInt(KEY_NEW_COLOR, mOldColorValue);
                } else {
                    animation.setInterpolator(null);
                    if (mHelpScreenVisible) {
                        mHelpScreen.setVisibility(View.GONE);
                    }
                    mHelpScreenVisible = !mHelpScreenVisible;
                    getArguments().putInt(KEY_HELP_SCREEN_VISIBILITY, mHelpScreenVisible
                            ? HELP_SCREEN_VISIBILITY_VISIBLE : HELP_SCREEN_VISIBILITY_GONE);
                }
            }
        });
        return animator;
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

    private void setUpMainButtons() {

    }

    private void setUpMainContent() {
        int mainButtonsCheckedId = ConfigColorPicker.getChipChededId(getActivity());
        if (mChipsGroup != null) {
            mChipsGroup.check(mainButtonsCheckedId);
        }
        if (mChipsGroupsGroup != null) {
            mChipsGroupsGroup.check(mainButtonsCheckedId);
        }
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        if (mainButtonsCheckedId == R.id.color_picker_chip_pick) {
            mColorPicker.setVisibility(View.VISIBLE);
        } else if (mainButtonsCheckedId == R.id.color_picker_chip_favorites) {
            mContentList.setVisibility(View.VISIBLE);
            buildOrUpdateFavoriteCards();
        } else if (mainButtonsCheckedId == R.id.color_picker_chip_darkkat) {
            mContentList.setVisibility(View.VISIBLE);
            buildOrUpdateDarkKatCards();
        } else if (mainButtonsCheckedId == R.id.color_picker_chip_material) {
            mContentList.setVisibility(View.VISIBLE);
            buildOrUpdateMaterialCards();
        } else if (mainButtonsCheckedId == R.id.color_picker_chip_holo) {
            mContentList.setVisibility(View.VISIBLE);
            buildOrUpdateHoloCards();
        } else if (mainButtonsCheckedId == R.id.color_picker_chip_rgb) {
            mContentList.setVisibility(View.VISIBLE);
            buildOrUpdateRGBCards();
        }
    }

    private void buildOrUpdateFavoriteCards() {
        buildOrClearCardList();
        for (int i = 0; i < NUM_MAX_FAVORITES; i++) {
            int favoriteNumber = i + 1;
            ColorPickerCard card = new ColorPickerFavoriteCard(getActivity(),
                    favoriteNumber, getFavoriteSubtitle(favoriteNumber),
                    getFavoriteColor(favoriteNumber));
            mColorPickerCards.add(card);
        }
        buildOrUpdateCardAdapter();
    }

    private void buildOrUpdateDarkKatCards() {
        buildOrClearCardList();
        TypedArray titles = mResources.obtainTypedArray(R.array.color_picker_darkkat_palette_titles);
        TypedArray colors = mResources.obtainTypedArray(R.array.color_picker_darkkat_palette);
        for (int i = 0; i < 8; i++) {
            ColorPickerCard card = new ColorPickerColorCard(getActivity(),
                    titles.getResourceId(i, 0), colors.getResourceId(i, 0));
            mColorPickerCards.add(card);
        }
        titles.recycle();
        colors.recycle();
        buildOrUpdateCardAdapter();
    }

    private void buildOrUpdateMaterialCards() {
        buildOrClearCardList();
        TypedArray titles = mResources.obtainTypedArray(R.array.color_picker_material_palette_titles);
        TypedArray colors = mResources.obtainTypedArray(R.array.color_picker_material_palette);
        for (int i = 0; i < 17; i++) {
            ColorPickerCard card = new ColorPickerColorCard(getActivity(),
                    titles.getResourceId(i, 0), colors.getResourceId(i, 0));
            mColorPickerCards.add(card);
        }
        titles.recycle();
        colors.recycle();
        buildOrUpdateCardAdapter();
    }

    private void buildOrUpdateHoloCards() {
        buildOrClearCardList();
        TypedArray titles = mResources.obtainTypedArray(R.array.color_picker_holo_palette_titles);
        TypedArray colors = mResources.obtainTypedArray(R.array.color_picker_holo_palette);
        for (int i = 0; i < 10; i++) {
            ColorPickerCard card = new ColorPickerColorCard(getActivity(),
                    titles.getResourceId(i, 0), colors.getResourceId(i, 0));
            mColorPickerCards.add(card);
        }
        titles.recycle();
        colors.recycle();
        buildOrUpdateCardAdapter();
    }

    private void buildOrUpdateRGBCards() {
        buildOrClearCardList();
        TypedArray titles = mResources.obtainTypedArray(R.array.color_picker_rgb_palette_titles);
        TypedArray colors = mResources.obtainTypedArray(R.array.color_picker_rgb_palette);
        for (int i = 0; i < 8; i++) {
            ColorPickerCard card = new ColorPickerColorCard(getActivity(),
                    titles.getResourceId(i, 0), colors.getResourceId(i, 0));
            mColorPickerCards.add(card);
        }
        titles.recycle();
        colors.recycle();
        buildOrUpdateCardAdapter();
    }

    private void buildOrClearCardList() {
        if (mColorPickerCards == null) {
            mColorPickerCards = new ArrayList<ColorPickerCard>();
        } else {
            mColorPickerCards.clear();
        }
    }

    private void buildOrUpdateCardAdapter() {
        if (mCardAdapter == null) {
            mCardAdapter = new ColorPickerCardAdapter(getActivity(), mColorPickerCards,
                    mNewColorValue, mFavoriteColors);
            mCardAdapter.setOnCardClickedListener(this);
        } else {
            mCardAdapter.setNewColor(mNewColorValue);
        }
        if (mContentList.getAdapter() == null) {
            mContentList.setAdapter(mCardAdapter);
        } else {
            mCardAdapter.notifyDataSetChanged();
        }
    }

    private void setUpHelpScreen() {
        WebView wv = (WebView) mColorPickerView.findViewById(R.id.help_screen_html_content);

        String content = getString(R.string.color_picker_help_main_content,
                ColorPickerHelper.convertToRGB(ThemeUtil.getAccentColor(getActivity())));

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

        mHelpScreen.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHelpScreenHeight = mHelpScreen.getHeight();
                mHelpScreen.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (!mHelpScreenVisible) {
                    mHelpScreen.setTranslationY(0);
                    mHelpScreen.setAlpha(0f);
                    mHelpScreen.setVisibility(View.GONE);
                }
            }
        });
        mHelpScreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
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
            mAnimationType = ANIMATE_HELP_SCREEN_VISIBILITY;
            mAnimator.setInterpolator(new FastOutSlowInInterpolator());
            mAnimator.setDuration(225);
            mAnimator.start();
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
            mAnimationType = ANIMATE_HELP_SCREEN_VISIBILITY;
            mAnimator.setInterpolator(new FastOutSlowInInterpolator());
            mAnimator.setDuration(195);
            mAnimator.start();
        }
    }

    @Override
    public void onColorChanged(int color) {
        mApplyColorIconAnimationType = NO_ANIMATION;
        if (color != mOldColorValue) {
            mNewColorValue = color;
            if (mCardAdapter != null) {
                mCardAdapter.setNewColor(mNewColorValue);
            }
            int mainButtonsCheckedId = ConfigColorPicker.getChipChededId(getActivity());
            if (mainButtonsCheckedId != R.id.color_picker_chip_pick) {
                if (mContentList.getAdapter() != null) {
                    mCardAdapter.notifyDataSetChanged();
                }
            }
            getArguments().putInt(KEY_NEW_COLOR, mNewColorValue);
            if (mNewColorValue == mInitialColor) {
                if (mOldColorValue != mInitialColor) {
                    mApplyColorIconAnimationType = ANIMATE_TO_HIDE;
                    mApplyColorAction.setOnClickListener(null);
                    mApplyColorAction.setClickable(false);
                }
            } else if (mOldColorValue == mInitialColor) {
                mApplyColorIconAnimationType = ANIMATE_TO_SHOW;
                mApplyColorAction.showSetIcon(true);
            }
            mAnimationType = ANIMATE_COLOR_TRANSITION;
            mAnimator.setDuration(300);
            mAnimator.start();

            try {
                if (mEditHexValue != null) {
                    mEditHexValue.setText(ColorPickerHelper.convertToARGB(color));
                }
            } catch (Exception e) {}
        }

    }

    @Override
    public void onCardActionSetClicked(int color) {
        try {
            if (color != mOldColorValue) {
                mColorPicker.setColor(color, true);
            }
        } catch (Exception e) {}
    }

    @Override
    public void onFavoriteCardActionFavoriteClicked(int position) {
        int favoriteNumber = position + 1;
        setFavoriteColor(favoriteNumber, mNewColorValue);
        mColorPickerCards.get(position).setColor(getFavoriteColor(favoriteNumber));
        mFavoriteColors[position] = mNewColorValue;
        mCardAdapter.notifyDataSetChanged();
    }

    @Override
    public void onColorCardActionFavoriteClicked(ColorPickerCard card, boolean isFavorite) {
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
        mCardAdapter.notifyDataSetChanged();
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

    private View.OnClickListener getFragmentOnClickListener() {
        return this;
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
}
