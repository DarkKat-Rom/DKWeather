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

package net.darkkatrom.dkweather.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.content.res.ColorStateList;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.WeatherInfo;
import net.darkkatrom.dkweather.WeatherInfo.HourForecast;
import net.darkkatrom.dkweather.model.WeatherCard;
import net.darkkatrom.dkweather.utils.Config;
import net.darkkatrom.dkweather.utils.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

public class WeatherCardAdapter extends
        RecyclerView.Adapter<WeatherCardAdapter.CardViewHolder> {

    private Context mContext;
    private List<WeatherCard> mWeatherCards;
    private WeatherInfo mWeatherInfo;

    private OnCardClickedListener mOnCardClickedListener;

    public interface OnCardClickedListener {
        public void onCardExpandCollapsedClicked(int position);
        public void onCardProviderLinkClicked();
    }

    public WeatherCardAdapter(Context context, List<WeatherCard> cards, WeatherInfo weatherInfo) {
        super();

        mContext = context;
        if (cards != null) {
            mWeatherCards = cards;
        } else {
            mWeatherCards = new ArrayList<WeatherCard>();
        }
        mWeatherInfo = weatherInfo;
    }

    @Override
    public WeatherCardAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {

        View v = LayoutInflater.from(mContext).inflate(
                R.layout.weather_card, parent, false);
        CardViewHolder vh = new CardViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, final int position) {
        final WeatherCard card = mWeatherCards.get(position);
        if (card.getViewType() == WeatherCard.VIEW_TYPE_CURRENT_WEATHER) {
            holder.mCurrentContentView.setVisibility(View.VISIBLE);
            holder.mForecastContentView.setVisibility(View.GONE);
            holder.mForecastDaytempsContentView.setVisibility(View.GONE);

            if (mWeatherInfo != null) {
                holder.mCurrentContent.mProviderLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {       
                        if (mOnCardClickedListener != null) {
                            mOnCardClickedListener.onCardProviderLinkClicked();
                        }
                    }
                });
            } else {
                holder.mCurrentContent.mProviderLink.setOnClickListener(null);
                holder.mCurrentContent.mProviderLink.setClickable(false);
            }

            holder.mCurrentContent.mExpandCollapseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCardClickedListener != null) {
                        mOnCardClickedListener.onCardExpandCollapsedClicked(position);
                    }
                }
            });

            if (card.isExpanded()) {
                holder.mCurrentContent.mExpandedContent.setVisibility(View.VISIBLE);
                holder.mCurrentContent.mExpandCollapseButtonDivider.setVisibility(View.VISIBLE);
                holder.mCurrentContent.mExpandCollapseButtonIcon.setImageResource(R.drawable.ic_expand_less);
                holder.mCurrentContent.mExpandCollapseButtonText.setText(R.string.collapse_card);
            } else {
                holder.mCurrentContent.mExpandedContent.setVisibility(View.GONE);
                holder.mCurrentContent.mExpandCollapseButtonDivider.setVisibility(View.GONE);
                holder.mCurrentContent.mExpandCollapseButtonIcon.setImageResource(R.drawable.ic_expand_more);
                holder.mCurrentContent.mExpandCollapseButtonText.setText(R.string.expand_card);
            }

            updateCurrentWeather(holder.mCurrentContent);
        } else if (card.getViewType() == WeatherCard.VIEW_TYPE_FORECAST_WEATHER) {
            holder.mCurrentContentView.setVisibility(View.GONE);
            holder.mForecastContentView.setVisibility(View.VISIBLE);
            holder.mForecastDaytempsContentView.setVisibility(View.GONE);

            holder.mForecastContent.mExpandCollapseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCardClickedListener != null) {
                        mOnCardClickedListener.onCardExpandCollapsedClicked(position);
                    }
                }
            });

            if (card.isExpanded()) {
                holder.mForecastContent.mExpandedContent.setVisibility(View.VISIBLE);
                holder.mForecastContent.mExpandCollapseButtonDivider.setVisibility(View.VISIBLE);
                holder.mForecastContent.mExpandCollapseButtonIcon.setImageResource(R.drawable.ic_expand_less);
                holder.mForecastContent.mExpandCollapseButtonText.setText(R.string.collapse_card);
            } else {
                holder.mForecastContent.mExpandedContent.setVisibility(View.GONE);
                holder.mForecastContent.mExpandCollapseButtonDivider.setVisibility(View.GONE);
                holder.mForecastContent.mExpandCollapseButtonIcon.setImageResource(R.drawable.ic_expand_more);
                holder.mForecastContent.mExpandCollapseButtonText.setText(R.string.expand_card);
            }

            updateForecastWeather(holder.mForecastContent, position);
        }
    }

    @Override
    public int getItemCount() {
        return mWeatherCards.size();
    }

    public void setOnCardClickedListener(OnCardClickedListener onItemClickedListener) {
        mOnCardClickedListener = onItemClickedListener;
    }

    public void updateWeatherInfo(WeatherInfo weatherInfo) {
        mWeatherInfo = weatherInfo;
    }

    public void updateCurrentWeather(CardViewHolder.CurrentContentViewHolder holder) {
        if (mWeatherInfo == null) {
            return;
        }

        int conditionIconType = Config.getConditionIconType(mContext);
        int conditionIconColor = ThemeUtil.getConditionIconColor(mContext, conditionIconType);
        Drawable icon = mWeatherInfo.getConditionIcon(
                conditionIconType, mWeatherInfo.getConditionCode());
        final String[] tempValues = {
            mWeatherInfo.getForecasts().get(0).getFormattedMorning(),
            mWeatherInfo.getForecasts().get(0).getFormattedDay(),
            mWeatherInfo.getForecasts().get(0).getFormattedEvening(),
            mWeatherInfo.getForecasts().get(0).getFormattedNight()
        };

        holder.mTime.setText(mWeatherInfo.getTime());
        if (conditionIconColor != 0) {
            holder.mImage.setImageTintList(ColorStateList.valueOf(conditionIconColor));
        } else {
            holder.mImage.setImageTintList(null);
        }
        holder.mImage.setImageDrawable(icon);
        holder.mTemp.setText(mWeatherInfo.getFormattedTemperature());
        holder.mTempLowHight.setText(mWeatherInfo.getFormattedLow() + " | " + mWeatherInfo.getFormattedHigh());
        holder.mCondition.setText(mWeatherInfo.getCondition());
        for (int i = 0; i < holder.mDayTempsValues.length; i++) {
            holder.mDayTempsValues[i].setText(tempValues[i]);
        }
        setPrecipitation(holder.mPrecipitationValue);
        holder.mWindValue.setText(mWeatherInfo.getFormattedWind());
        holder.mSunriseValue.setText(mWeatherInfo.getSunrise());
        holder.mHumidityValue.setText(mWeatherInfo.getFormattedHumidity());
        holder.mPressureValue.setText(mWeatherInfo.getFormattedPressure());
        holder.mSunsetValue.setText(mWeatherInfo.getSunset());
    }

    public void updateForecastWeather(CardViewHolder.ForecastContentViewHolder holder, int position) {
        if (mWeatherInfo == null) {
            return;
        }

        String forecastDay = mWeatherInfo.getHourForecastDays().get(0);
        ArrayList<HourForecast> hourForecasts = mWeatherInfo.getHourForecastsDay(forecastDay);
        HourForecast h = null;
        if (hourForecasts.size() != 0) {
            h = hourForecasts.get(position - 1);
        }

        if (h == null) {
            return;
        }

        int conditionIconType = Config.getConditionIconType(mContext);
        int conditionIconColor = ThemeUtil.getConditionIconColor(mContext, conditionIconType);
        final Drawable icon = mWeatherInfo.getConditionIcon(
                conditionIconType, h.getConditionCode());
        final String rain = h.getFormattedRain();
        final String snow = h.getFormattedSnow();
        final String noPrecipitationValue = mContext.getResources().getString(
                R.string.no_precipitation_value);

        holder.mTimeValue.setText(h.getTime());
        if (conditionIconColor != 0) {
            holder.mImage.setImageTintList(ColorStateList.valueOf(conditionIconColor));
        } else {
            holder.mImage.setImageTintList(null);
        }
        holder.mImage.setImageDrawable(icon);
        holder.mTempValue.setText(h.getFormattedTemperature());
        holder.mConditionValue.setText(h.getCondition());
        if (!snow.equals(WeatherInfo.NO_VALUE)) {
            holder.mPrecipitationValue.setText(snow);
        } else if (!rain.equals(WeatherInfo.NO_VALUE)) {
            holder.mPrecipitationValue.setText(rain);
        } else {
            holder.mPrecipitationValue.setText(noPrecipitationValue);
        }
        holder.mWindValue.setText(h.getFormattedWind());
        holder.mHumidityValue.setText(h.getFormattedHumidity());
        holder.mPressureValue.setText(h.getFormattedPressure());
    }

    private void setPrecipitation(TextView tv) {
        final String rain1H = mWeatherInfo.getFormattedRain1H();
        final String rain3H = mWeatherInfo.getFormattedRain3H();
        final String snow1H = mWeatherInfo.getFormattedSnow1H();
        final String snow3H = mWeatherInfo.getFormattedSnow3H();
        final String noValue = mContext.getResources().getString(R.string.no_precipitation_value);
        if (!snow1H.equals(WeatherInfo.NO_VALUE)) {
            tv.setText(snow1H);
        } else if (!snow3H.equals(WeatherInfo.NO_VALUE)) {
            tv.setText(snow3H);
        } else if (!rain1H.equals(WeatherInfo.NO_VALUE)) {
            tv.setText(rain1H);
        } else if (!rain3H.equals(WeatherInfo.NO_VALUE)) {
            tv.setText(rain3H);
        } else {
            tv.setText(noValue);
        }
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public View mRootView;
        public CardView mCardView;
        public View mCurrentContentView;
        public View mForecastContentView;
        public View mForecastDaytempsContentView;
        public CurrentContentViewHolder mCurrentContent;
        public ForecastContentViewHolder mForecastContent;
        public ForecastDaytempsContentViewHolder mForecastDaytempsContent;

        public CardViewHolder(View v) {
            super(v);

            mRootView = v;
            mCardView = (CardView) v.findViewById(R.id.weather_card);
            mCurrentContentView = v.findViewById(R.id.current_weather_content_layout);
            mForecastContentView = v.findViewById(R.id.forecast_weather_content_layout);
            mForecastDaytempsContentView = v.findViewById(R.id.forecast_daytemps_content_layout);

            mCurrentContent = new CurrentContentViewHolder(
                    mCurrentContentView);
            mForecastContent = new ForecastContentViewHolder(
                    mForecastContentView);
            mForecastDaytempsContent = new ForecastDaytempsContentViewHolder(
                    mForecastDaytempsContentView);
        }

        public static class CurrentContentViewHolder {
            public TextView mTime;
            public ImageView mImage;
            public View mImageDivider;
            public TextView mTemp;
            public TextView mTempLowHight;
            public View mTempDivider;
            public TextView mCondition;
            public TextView[] mDayTempsValues;
            public View mExpandedContent;
            public TextView mPrecipitationTitle;
            public TextView mPrecipitationValue;
            public TextView mWindTitle;
            public TextView mWindValue;
            public TextView mSunriseTitle;
            public TextView mSunriseValue;
            public TextView mHumidityTitle;
            public TextView mHumidityValue;
            public TextView mPressureTitle;
            public TextView mPressureValue;
            public TextView mSunsetTitle;
            public TextView mSunsetValue;
            private View mExpandCollapseButtonDivider;
            private TextView mProviderLink;
            private LinearLayout mExpandCollapseButton;
            private TextView mExpandCollapseButtonText;
            private ImageView mExpandCollapseButtonIcon;

            public CurrentContentViewHolder(View v) {
                mTime = (TextView) v.findViewById(R.id.current_time);
                mImage = (ImageView) v.findViewById(R.id.current_condition_image);
                mImageDivider = v.findViewById(R.id.current_image_divider);
                mTemp = (TextView) v.findViewById(R.id.current_temp);
                mTempDivider = v.findViewById(R.id.current_temp_divider);
                mTempLowHight = (TextView) v.findViewById(R.id.current_low_high);
                mCondition = (TextView) v.findViewById(R.id.current_condition);
                TextView[] dayTempsTitles = {
                    (TextView) v.findViewById(R.id.current_temp_morning_title),
                    (TextView) v.findViewById(R.id.current_temp_day_title),
                    (TextView) v.findViewById(R.id.current_temp_evening_title),
                    (TextView) v.findViewById(R.id.current_temp_night_title)
                };
                mDayTempsValues = new TextView[] {
                        (TextView) v.findViewById(R.id.current_temp_morning_value),
                        (TextView) v.findViewById(R.id.current_temp_day_value),
                        (TextView) v.findViewById(R.id.current_temp_evening_value),
                        (TextView) v.findViewById(R.id.current_temp_night_value)
                };
                mExpandedContent = v.findViewById(R.id.current_expanded_content_layout);
                mPrecipitationTitle = (TextView) v.findViewById(R.id.current_precipitation_title);
                mPrecipitationValue = (TextView) v.findViewById(R.id.current_precipitation_value);
                mWindTitle = (TextView) v.findViewById(R.id.current_wind_title);
                mWindValue = (TextView) v.findViewById(R.id.current_wind_value);
                mSunriseTitle = (TextView) v.findViewById(R.id.current_sunrise_title);
                mSunriseValue = (TextView) v.findViewById(R.id.current_sunrise_value);
                mHumidityTitle = (TextView) v.findViewById(R.id.current_humidity_title);
                mHumidityValue = (TextView) v.findViewById(R.id.current_humidity_value);
                mPressureTitle = (TextView) v.findViewById(R.id.current_pressure_title);
                mPressureValue = (TextView) v.findViewById(R.id.current_pressure_value);
                mSunsetTitle = (TextView) v.findViewById(R.id.current_sunset_title);
                mSunsetValue = (TextView) v.findViewById(R.id.current_sunset_value);
                mExpandCollapseButtonDivider = 
                        v.findViewById(R.id.current_expand_collapse_button_divider);
                mProviderLink = 
                        (TextView) v.findViewById(R.id.current_provider_link);
                mExpandCollapseButton = 
                        (LinearLayout) v.findViewById(R.id.current_expand_collapse_button);
                mExpandCollapseButtonText = 
                        (TextView) v.findViewById(R.id.current_expand_collapse_button_text);
                mExpandCollapseButtonIcon = 
                        (ImageView) v.findViewById(R.id.current_expand_collapse_button_icon);

            }
        }

        public static class ForecastContentViewHolder {
            public View mExpandedContent;
            public TextView mTimeValue;
            public ImageView mImage;
            public TextView mTempValue;
            public TextView mConditionValue;
            public TextView mPrecipitationValue;
            public TextView mWindValue;
            public TextView mHumidityValue;
            public TextView mPressureValue;
            public View mExpandCollapseButtonDivider;
            public LinearLayout mExpandCollapseButton;
            public TextView mExpandCollapseButtonText;
            public ImageView mExpandCollapseButtonIcon;

            public ForecastContentViewHolder(View v) {
                mExpandedContent = v.findViewById(R.id.forecast_expanded_content_layout);
                mTimeValue = (TextView) v.findViewById(R.id.forecast_time);
                mImage = (ImageView) v.findViewById(R.id.forecast_condition_image);
                mTempValue = (TextView) v.findViewById(R.id.forecast_temp_value);
                mConditionValue = (TextView) v.findViewById(R.id.forecast_condition_value);
                mPrecipitationValue = 
                        (TextView) v.findViewById(R.id.forecast_precipitation_value);
                mWindValue = (TextView) v.findViewById(R.id.forecast_wind_value);
                mHumidityValue = (TextView) v.findViewById(R.id.forecast_humidity_value);
                mPressureValue = (TextView) v.findViewById(R.id.forecast_pressure_value);
                mExpandCollapseButtonDivider = 
                        v.findViewById(R.id.forecast_expand_collapse_button_divider);
                mExpandCollapseButton = 
                        (LinearLayout) v.findViewById(R.id.forecast_expand_collapse_button);
                mExpandCollapseButtonText = 
                        (TextView) v.findViewById(R.id.forecast_expand_collapse_button_text);
                mExpandCollapseButtonIcon = 
                        (ImageView) v.findViewById(R.id.forecast_expand_collapse_button_icon);
            }
        }

        public static class ForecastDaytempsContentViewHolder {

            public ForecastDaytempsContentViewHolder(View v) {

            }
        }

    }
}
