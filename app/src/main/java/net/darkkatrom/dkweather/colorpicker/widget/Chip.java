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

package net.darkkatrom.dkweather.colorpicker.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.RadioButton;
import android.util.AttributeSet;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.utils.GraphicsUtil;
import net.darkkatrom.dkweather.utils.ThemeUtil;

public class Chip extends RadioButton {

    private int mBackgroundColorChecked = 0;
    private int mBackgroundColorNormal = 0;
    private boolean mBackgroundTintListSet = false;

    public Chip(Context context) {
        this(context, null);
    }

    public Chip(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.radioButtonStyle);
    }

    public Chip(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public Chip(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);

        if (mBackgroundColorChecked == 0) {
            mBackgroundColorChecked =
                    ThemeUtil.getColorFromThemeAttribute(getContext(), R.attr.colorAccent);
        }
        if (mBackgroundColorNormal == 0) {
            mBackgroundColorNormal = ThemeUtil.getActionBarBackgroundColor(getContext());
        }
        if (mBackgroundColorChecked != 0 && mBackgroundColorNormal != 0 && !mBackgroundTintListSet) {
            int states[][] = new int[][] {
                new int[] { android.R.attr.state_checked },
                new int[]{}
            };
            int colors[] = new int[] {
                mBackgroundColorChecked,
                mBackgroundColorNormal
            };

            ColorStateList stateColors = new ColorStateList(states, colors);
            RippleDrawable bg = (RippleDrawable) getBackground();
            bg.findDrawableByLayerId(R.id.background).setTintList(stateColors);
            mBackgroundTintListSet = true;
        }
        int textColorResId = 0;
        int rippleColorResId = 0;
        if (checked) {
            if (mBackgroundColorChecked != 0) {
                textColorResId = GraphicsUtil.isColorDark(mBackgroundColorChecked)
                        ? R.color.chip_text_color_dark : R.color.chip_text_color_light;
                rippleColorResId = GraphicsUtil.isColorDark(mBackgroundColorChecked)
                        ? R.color.ripple_white : R.color.ripple_black;
            }
        } else {
            if (mBackgroundColorNormal != 0) {
                textColorResId = GraphicsUtil.isColorDark(mBackgroundColorNormal)
                        ? R.color.chip_text_color_dark : R.color.chip_text_color_light;
                rippleColorResId = GraphicsUtil.isColorDark(mBackgroundColorNormal)
                        ? R.color.ripple_white : R.color.ripple_black;
            }
        }
        if (textColorResId != 0) {
            setTextColor(getContext().getColorStateList(textColorResId));
        }
        if (rippleColorResId != 0) {
            RippleDrawable bg = (RippleDrawable) getBackground();
            int rippleColor = getContext().getColor(rippleColorResId);
            bg.setColor(ColorStateList.valueOf(rippleColor));
        }

    }
}
