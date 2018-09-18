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
import android.widget.RadioButton;
import android.util.AttributeSet;
import android.util.TypedValue;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.utils.ColorUtil;
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
            TypedValue tv = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.colorAccent, tv, true);
            if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                mBackgroundColorChecked = tv.data;
            } else {
                mBackgroundColorChecked = getContext().getColor(tv.resourceId);
            }
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
            setBackgroundTintList(stateColors);
            mBackgroundTintListSet = true;
        }
        int textColorResId = 0;
        if (checked) {
            if (mBackgroundColorChecked != 0) {
                textColorResId = ColorUtil.isColorDark(mBackgroundColorChecked)
                        ? R.color.chip_text_color_dark : R.color.chip_text_color_light;
            }
        } else {
            if (mBackgroundColorNormal != 0) {
                textColorResId = ColorUtil.isColorDark(mBackgroundColorNormal)
                        ? R.color.chip_text_color_dark : R.color.chip_text_color_light;
            }
        }
        if (textColorResId != 0) {
            setTextColor(getContext().getColorStateList(textColorResId));
        }
    }
}
