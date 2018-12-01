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

package net.darkkatrom.dkweather.colorpicker.animator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.LinearInterpolator;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.colorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkweather.colorpicker.util.ColorPickerHelper;
import net.darkkatrom.dkweather.colorpicker.widget.ApplyColorView;

public class ColorPickerFragmentAnimator {
    public static final long ADD_DURATION              = 225;
    public static final long REMOVE_DURATION           = 195;
    public static final long CHANGE_DURATION           = 250;
    public static final long COLOR_TRANSITION_DURATION = 300;

    public static final int ANIMATE_TO_SHOW = 0;
    public static final int ANIMATE_TO_HIDE = 1;
    public static final int NO_ANIMATION    = 2;

    private ColorPickerFragment mFragment;
    private float mFullTranslationX;
    private boolean mHelpScreenVisible;
    private boolean mIsAnimatingHelpScreen = false;

    public ColorPickerFragmentAnimator(Context context, ColorPickerFragment fragment,
            boolean helpScreenVisible) {
        mFragment = fragment;
        mFullTranslationX = context.getResources().getDimension(
                R.dimen.color_picker_action_apply_color_translation_x);
        mHelpScreenVisible = helpScreenVisible;
    }

    public void animateShow(final View view, final int translationXHidden) {
        view.setTranslationX(translationXHidden);
        view.setVisibility(View.VISIBLE);

        final ViewPropertyAnimator animationShow = view.animate()
                .setInterpolator(new ValueAnimator().getInterpolator());
        animationShow.translationX(0).setDuration(ADD_DURATION).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                view.setTranslationX(0);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setTranslationX(0);
                animationShow.setListener(null);
            }
        }).start();
    }

    public void animateSwitch(final View oldView, final View view, final int translationXHidden) {
        view.setTranslationX(translationXHidden);
        view.setVisibility(View.VISIBLE);

        final ViewPropertyAnimator animationHide = oldView.animate()
                .setInterpolator(new ValueAnimator().getInterpolator());
        animationHide.translationX(translationXHidden)
                .setDuration(REMOVE_DURATION).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                oldView.setTranslationX(translationXHidden);
                oldView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                oldView.setTranslationX(translationXHidden);
                oldView.setVisibility(View.GONE);
                animationHide.setListener(null);
            }
        }).start();

        Runnable show = new Runnable() {
            @Override
            public void run() {
                final ViewPropertyAnimator animationShow = view.animate()
                        .setInterpolator(new ValueAnimator().getInterpolator());
                animationShow.translationX(0).setDuration(ADD_DURATION).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        view.setTranslationX(0);
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        view.setTranslationX(0);
                        animationShow.setListener(null);
                    }
                }).start();
            }
        };
        ViewCompat.postOnAnimationDelayed(oldView, show, REMOVE_DURATION);
    }



    public void animateColorTransition(final int oldColor, final int newColor, final int animationType,
            final ApplyColorView applyColorAction) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(COLOR_TRANSITION_DURATION);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float position = animation.getAnimatedFraction();
                int blended = ColorPickerHelper.getBlendColor(oldColor, newColor, position);
                applyColorAction.setColor(blended);
                if (animationType != NO_ANIMATION) {
                    final boolean animateShow = animationType == ANIMATE_TO_SHOW;
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
                    applyColorAction.setColorPreviewTranslationX(currentTranslationX);
                    if (applyAlpha) {
                        applyColorAction.applySetIconAlpha(alpha);
                    }
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animationType != NO_ANIMATION) {
                    if (animationType != ANIMATE_TO_SHOW) {
                        applyColorAction.showSetIcon(false);
                    } else {
                        applyColorAction.setOnClickListener((View.OnClickListener) mFragment);
                    }
                }
                mFragment.setOldColorValue(newColor);
                mFragment.getArguments().putInt(ColorPickerFragment.KEY_NEW_COLOR, newColor);

            }
        });
        animator.start();
    }

    public void animateShowHelpScreen(final View view) {
        view.setVisibility(View.VISIBLE);
        final ViewPropertyAnimator animationShow = view.animate()
                .setInterpolator(new ValueAnimator().getInterpolator());
        animationShow.translationX(0).setDuration(ADD_DURATION).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                mIsAnimatingHelpScreen = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                view.setTranslationX(0);
                mHelpScreenVisible = !mHelpScreenVisible;
                mFragment.getArguments().putInt(ColorPickerFragment.KEY_HELP_SCREEN_VISIBILITY,
                        ColorPickerFragment.HELP_SCREEN_VISIBILITY_VISIBLE);
                mIsAnimatingHelpScreen = false;
                mFragment.setIsHelpScreenVisible(mHelpScreenVisible);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setTranslationX(0);
                mHelpScreenVisible = !mHelpScreenVisible;
                mFragment.getArguments().putInt(ColorPickerFragment.KEY_HELP_SCREEN_VISIBILITY,
                        ColorPickerFragment.HELP_SCREEN_VISIBILITY_VISIBLE);
                animationShow.setListener(null);
                mIsAnimatingHelpScreen = false;
                mFragment.setIsHelpScreenVisible(mHelpScreenVisible);
            }
        }).start();
    }

    public void animateHideHelpScreen(final View view) {
        final float translationX = view.getWidth();
        final ViewPropertyAnimator animationShow = view.animate()
                .setInterpolator(new ValueAnimator().getInterpolator());
        animationShow.translationX(translationX).setDuration(REMOVE_DURATION)
                .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                mIsAnimatingHelpScreen = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                view.setTranslationX(translationX);
                view.setVisibility(View.GONE);
                mHelpScreenVisible = !mHelpScreenVisible;
                mFragment.getArguments().putInt(ColorPickerFragment.KEY_HELP_SCREEN_VISIBILITY,
                        ColorPickerFragment.HELP_SCREEN_VISIBILITY_GONE);
                mIsAnimatingHelpScreen = false;
                mFragment.setIsHelpScreenVisible(mHelpScreenVisible);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setTranslationX(translationX);
                view.setVisibility(View.GONE);
                mHelpScreenVisible = !mHelpScreenVisible;
                mFragment.getArguments().putInt(ColorPickerFragment.KEY_HELP_SCREEN_VISIBILITY,
                        ColorPickerFragment.HELP_SCREEN_VISIBILITY_GONE);
                animationShow.setListener(null);
                mIsAnimatingHelpScreen = false;
                mFragment.setIsHelpScreenVisible(mHelpScreenVisible);
            }
        }).start();
    }

    public boolean isAnimatingHelpScreen() {
        return mIsAnimatingHelpScreen;
    }

    public boolean isHelpScreenVisible() {
        return mHelpScreenVisible;
    }

    public void animateShowHideHelpScreen(View view) {
        if (mIsAnimatingHelpScreen) {
            return;
        }
        if (mHelpScreenVisible) {
            animateHideHelpScreen(view);
        } else {
            animateShowHelpScreen(view);
        }
    }
}
