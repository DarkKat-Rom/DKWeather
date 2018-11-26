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

package net.darkkatrom.dkweather.animator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewPropertyAnimator;

public class FragmentAnimator {
    public static final long ADD_DURATION    = 225;
    public static final long REMOVE_DURATION = 195;

    private boolean mIsAnimating = false;

    public void animateSwitch(final View oldView, final View view, final float translationXHidden) {
        view.setTranslationX(translationXHidden);
        view.setVisibility(View.VISIBLE);

        final ViewPropertyAnimator animationHide = oldView.animate()
                .setInterpolator(new ValueAnimator().getInterpolator());
        animationHide.translationX(translationXHidden)
                .setDuration(REMOVE_DURATION).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                mIsAnimating = true;
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
                        mIsAnimating = false;
                    }
                }).start();
            }
        };
        ViewCompat.postOnAnimationDelayed(oldView, show, REMOVE_DURATION);
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }
}
