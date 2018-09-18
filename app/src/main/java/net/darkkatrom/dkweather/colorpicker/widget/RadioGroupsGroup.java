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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class RadioGroupsGroup extends LinearLayout implements RadioGroup.OnCheckedChangeListener {

    private List<RadioGroup> mRadioGroups = null;

    private OnCheckedChangeListener mOnCheckedChangeListener;

    public interface OnCheckedChangeListener {
        public void onCheckedChanged(RadioGroupsGroup group, int checkedId);
    }

    public RadioGroupsGroup(Context context) {
        this(context, null);
    }

    public RadioGroupsGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioGroupsGroup(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public RadioGroupsGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof RadioGroup) {
                ((RadioGroup) child).setOnCheckedChangeListener(this);
                if (mRadioGroups == null) {
                    mRadioGroups = new ArrayList<RadioGroup>();
                }
                mRadioGroups.add((RadioGroup) child);
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId != -1) {
            for (int i = 0; i < mRadioGroups.size(); i++) {
                if (mRadioGroups.get(i).getId() != group.getId()) {
                    mRadioGroups.get(i).setOnCheckedChangeListener(null);
                    mRadioGroups.get(i).clearCheck();
                    mRadioGroups.get(i).setOnCheckedChangeListener(this);
                }
            }
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, checkedId);
            }
        }
    }

    public void check(int id) {
        for (int i = 0; i < mRadioGroups.size(); i++) {
            if (mRadioGroups.get(i).findViewById(id) != null) {
                mRadioGroups.get(i).check(id);
            }
        }
    }

    public void clearCheck() {
        for (int i = 0; i < mRadioGroups.size(); i++) {
            if (mRadioGroups.get(i).getCheckedRadioButtonId() != -1) {
                mRadioGroups.get(i).clearCheck();
            }
        }
    }

    public int getCheckedRadioButtonId() {
        int id = -1;
        for (int i = 0; i < mRadioGroups.size(); i++) {
            if (mRadioGroups.get(i).getCheckedRadioButtonId() != -1) {
                id = mRadioGroups.get(i).getCheckedRadioButtonId();
            }
        }
        return id;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }
}
