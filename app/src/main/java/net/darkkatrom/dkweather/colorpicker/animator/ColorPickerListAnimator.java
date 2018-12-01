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

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import net.darkkatrom.dkweather.colorpicker.util.ColorPickerHelper;

public class ColorPickerListAnimator {
    public static final long COLOR_TRANSITION_DURATION = 300;

    public static void animateColorTransition(final int oldBgColor, final int newBgColor,
            final int oldTextColor, final int newTextColor, final View v, final TextView tv) {
        final boolean animateTextColor = tv != null;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(COLOR_TRANSITION_DURATION);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float position = animation.getAnimatedFraction();
                int blendedBg = ColorPickerHelper.getBlendColor(oldBgColor, newBgColor, position);
                v.setBackgroundColor(blendedBg);
                if (animateTextColor) {
                    int blendedText = ColorPickerHelper.getBlendColor(oldTextColor, newTextColor, position);
                    tv.setTextColor(blendedText);
                }
            }
        });
        animator.start();
    }
}
