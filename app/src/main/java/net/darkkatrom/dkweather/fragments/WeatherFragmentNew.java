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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.WeatherInfo;
import net.darkkatrom.dkweather.adapter.WeatherCardAdapter;
import net.darkkatrom.dkweather.model.WeatherCard;

import java.util.ArrayList;
import java.util.List;

public class WeatherFragmentNew extends Fragment implements
        WeatherCardAdapter.OnCardClickedListener {

    private RecyclerView mContentList;

    private WeatherInfo mWeatherInfo;

    private WeatherCardAdapter mCardAdapter = null;
    private List<WeatherCard> mWeatherCards;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.weather_fragment, container, false);
        mContentList = (RecyclerView) v.findViewById(R.id.weather_fragment_list);
        return v;
    }

    public void updateContent(WeatherInfo weather) {
        if (getActivity() == null || weather == null) {
            return;
        }
        mWeatherInfo = weather;
        if (mWeatherCards == null) {
            mWeatherCards = new ArrayList<WeatherCard>();
            mWeatherCards.add(new WeatherCard(getActivity(), 0));
        }
        
        if (mCardAdapter == null) {
            mCardAdapter = new WeatherCardAdapter(getActivity(), mWeatherCards, mWeatherInfo);
            mContentList.setAdapter(mCardAdapter);
            mCardAdapter.setOnCardClickedListener(this);
        } else {
            mCardAdapter.updateWeatherInfo(mWeatherInfo);
            mCardAdapter.notifyItemChanged(0);
        }
    }

    @Override
    public void onCardExpandCollapsedClicked(int position) {
        mWeatherCards.get(position).toggleIsExpanded();
        mCardAdapter.notifyItemChanged(position);
    }

    @Override
    public void onCardProviderLinkClicked() {
        String cityId = mWeatherInfo.getId();
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://openweathermap.org/city/" + cityId));
        getActivity().startActivity(intent);
    }
}
