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

package net.darkkatrom.dkweather.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.WeatherInfo;
import net.darkkatrom.dkweather.activities.SettingsActivity;

public class WeatherFragmentNew extends Fragment implements OnClickListener {

    View mRootView;
    private TextView mCurrentTemp;
    private TextView mTime;

    private View mNavigationButtonSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.weather_fragment, container, false);

        mCurrentTemp = (TextView) mRootView.findViewById(R.id.weather_fragment_current_temp);
        mTime = (TextView) mRootView.findViewById(R.id.weather_fragment_time);
        setupBottomNavigation();

        return mRootView;
    }

    public void updateContent(WeatherInfo weather) {
        if (getActivity() == null || weather == null) {
            return;
        }
        mCurrentTemp.setText(weather.getFormattedTemperature());
        mTime.setText(weather.getTime());
    }

    private void setupBottomNavigation() {
        mRootView.findViewById(R.id.bottom_navigation_item_previous_day).setVisibility(View.INVISIBLE);
        mNavigationButtonSettings = mRootView.findViewById(R.id.bottom_navigation_item_settings);
        mNavigationButtonSettings.setOnClickListener(this);
        mRootView.findViewById(R.id.bottom_navigation_item_next_day).setVisibility(View.INVISIBLE);

        ImageView iv =
                (ImageView) mNavigationButtonSettings.findViewById(R.id.bottom_navigation_item_icon);
        TextView tv =
                (TextView) mNavigationButtonSettings.findViewById(R.id.bottom_navigation_item_text);
        iv.setImageResource(R.drawable.ic_action_settings);
        iv.setImageTintList(getActivity().getColorStateList(
                R.color.bottom_navigation_selectable_item_text_icon_color));
        tv.setText(R.string.settings_title);
        tv.setTextColor(getActivity().getColorStateList(
                R.color.bottom_navigation_selectable_item_text_icon_color));
    }

    @Override
    public void onClick(View v) {
        if (v == mNavigationButtonSettings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }
    }
}
