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
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.WeatherInfo;
import net.darkkatrom.dkweather.WeatherInfo.HourForecast;
import net.darkkatrom.dkweather.activities.MainActivity;
import net.darkkatrom.dkweather.animator.BaseItemAnimator;
import net.darkkatrom.dkweather.animator.FragmentAnimator;
import net.darkkatrom.dkweather.adapter.WeatherCardAdapter;
import net.darkkatrom.dkweather.model.WeatherCard;

import java.util.ArrayList;
import java.util.List;

public class WeatherFragment extends Fragment implements
        WeatherCardAdapter.OnCardClickedListener {

    public static final int ANIMATE_CARDS_SWITCH  = 0;
    public static final int ANIMATE_LAYOUT_SWITCH = 1;
    public static final int NO_ANIMATION          = 2;

    private View mRootView;
    private RecyclerView mContentList;
    private View mNoWeatherDataLayout;

    private WeatherInfo mWeatherInfo;

    private WeatherCardAdapter mCardAdapter = null;
    private List<WeatherCard> mWeatherCards;

    private int mItemCount = 0;

    private int mVisibleDay = MainActivity.TODAY;

    private int mAnimationtype = -1;
    private float mTranslationX;
    private boolean mViewCreated = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.weather_fragment, container, false);
        mContentList = (RecyclerView) mRootView.findViewById(R.id.weather_fragment_list);
        mNoWeatherDataLayout = mRootView.findViewById(R.id.no_weather_data_layout);
        setupAdapterAndCardlist();
        return mRootView;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.post(new Runnable() {
            @Override
            public void run() {
                mTranslationX = view.getWidth();
                mContentList.setTranslationX(mWeatherInfo == null ? mTranslationX : 0);
                mNoWeatherDataLayout.setTranslationX(mWeatherInfo == null ? 0 : mTranslationX);
                mViewCreated = true;
                if (mAnimationtype != -1) {
                    // There was already a canceled call to 'updateContent',
                    // recall 'updateContent'
                    updateContent(mWeatherInfo, mAnimationtype);
                }
            }
        });
    }

    public void setupAdapterAndCardlist() {
        mWeatherCards = new ArrayList<WeatherCard>();
        mCardAdapter = new WeatherCardAdapter(getActivity(), mWeatherCards);
        mCardAdapter.setOnCardClickedListener(this);
        mContentList.setAdapter(mCardAdapter);
    }

    public void setVisibleDay(int visibleDay) {
        mVisibleDay = visibleDay;
    }

    public void updateContent(WeatherInfo weather, int animationType) {
        mWeatherInfo = weather;
        mAnimationtype = animationType;
        if (!mViewCreated) {
            // The view isn't created, cancel updating the content,
            // 'updateContent' will be recalled once the view was created
            return;
        }

        if (mAnimationtype == ANIMATE_LAYOUT_SWITCH) {
            FragmentAnimator animator = new FragmentAnimator();
            View oldView = mWeatherInfo == null ? mContentList : mNoWeatherDataLayout;
            View view = mWeatherInfo == null ? mNoWeatherDataLayout : mContentList;
            animator.animateSwitch(oldView, view, mTranslationX);
        }
        if (mWeatherInfo == null) {
            return;
        }

        if (mItemCount > 0) {
            mWeatherCards.clear();
            mCardAdapter.notifyItemRangeRemoved(0, mItemCount);
            mItemCount = 0;
        }

        boolean forecastStartAt1 = true;
        if (mVisibleDay == MainActivity.TODAY) {
            mWeatherCards.add(new WeatherCard(getActivity(), WeatherCard.VIEW_TYPE_CURRENT_WEATHER));
        } else {
            if (!(mWeatherInfo.getForecasts().size() < mVisibleDay + 1)) {
                mWeatherCards.add(new WeatherCard(getActivity(), WeatherCard.VIEW_TYPE_FORECAST_DAYTEMPS));
            } else {
                forecastStartAt1 = false;
            }
        }

        String forecastDay = mWeatherInfo.getHourForecastDays().get(mVisibleDay);
        ArrayList<HourForecast> hourForecasts = mWeatherInfo.getHourForecastsDay(forecastDay);
        if (hourForecasts.size() != 0) {
            for (int i = 0; i < hourForecasts.size(); i++) {
                mWeatherCards.add(new WeatherCard(getActivity(), WeatherCard.VIEW_TYPE_FORECAST_WEATHER));
            }
        }

        mCardAdapter.setVisibleDay(mVisibleDay);
        mCardAdapter.setForecastStartAt1(forecastStartAt1);
        mCardAdapter.updateWeatherInfo(mWeatherInfo);
        if (mAnimationtype == ANIMATE_CARDS_SWITCH) {
            mContentList.setItemAnimator(new BaseItemAnimator());
        } else {
            mContentList.setItemAnimator(null);
        }
        mItemCount = mWeatherCards.size();
        mCardAdapter.notifyItemRangeInserted(0, mItemCount);
        mContentList.getLayoutManager().scrollToPosition(0);
        ((AppBarLayout) getActivity().findViewById(R.id.appBarLayout)).setExpanded(true, true);
    }

    @Override
    public void onCardExpandCollapsedClicked(int position) {
        mContentList.setItemAnimator(null);
        mWeatherCards.get(position).toggleIsExpanded();
        mCardAdapter.notifyItemChanged(position);
        if (mWeatherCards.get(position).isExpanded()) {
            ((LinearLayoutManager) mContentList.getLayoutManager()).scrollToPositionWithOffset(position, 0);
        }
    }

    @Override
    public void onCardProviderLinkClicked() {
        String cityId = mWeatherInfo.getId();
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://openweathermap.org/city/" + cityId));
        getActivity().startActivity(intent);
    }
}
