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
import android.transition.TransitionManager;
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
    private int mVisibleDay = 0;
    private boolean mForecastStartAt1 = true;

    RecyclerView mRecyclerView;

    private OnCardClickedListener mOnCardClickedListener;

    public interface OnCardClickedListener {
        public void onCardExpandCollapsedClicked(int position);
        public void onCardProviderLinkClicked();
    }

    public WeatherCardAdapter(Context context, List<WeatherCard> cards) {
        super();

        mContext = context;
        if (cards != null) {
            mWeatherCards = cards;
        } else {
            mWeatherCards = new ArrayList<WeatherCard>();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public WeatherCardAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {

        View v = LayoutInflater.from(mContext).inflate(
                R.layout.weather_card, parent, false);
        CardViewHolder vh = null;
        if (viewType == WeatherCard.VIEW_TYPE_CURRENT_WEATHER) {
            vh = new CurrentContentViewHolder(v);
        } else if (viewType == WeatherCard.VIEW_TYPE_FORECAST_WEATHER) {
            vh = new ForecastContentViewHolder(v);
        } else {
            vh = new ForecastDayTempsContentViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, final int position) {
        final WeatherCard card = mWeatherCards.get(position);
        int viewType = card.getViewType();

        holder.mCurrentContentView.setVisibility(
                viewType == WeatherCard.VIEW_TYPE_CURRENT_WEATHER ? View.VISIBLE : View.GONE);
        holder.mForecastContentView.setVisibility(
                viewType == WeatherCard.VIEW_TYPE_FORECAST_WEATHER ? View.VISIBLE : View.GONE);
        holder.mForecastDayTempsContentView.setVisibility(
                viewType == WeatherCard.VIEW_TYPE_FORECAST_DAYTEMPS ? View.VISIBLE : View.GONE);

        if (viewType == WeatherCard.VIEW_TYPE_CURRENT_WEATHER) {
            updateCurrentWeather((CurrentContentViewHolder) holder, position, card.isExpanded());
        } else if (card.getViewType() == WeatherCard.VIEW_TYPE_FORECAST_WEATHER) {
            updateForecastWeather((ForecastContentViewHolder) holder, position, card.isExpanded());
        } else {
            updateForecastDayTemps((ForecastDayTempsContentViewHolder) holder);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mWeatherCards.get(position).getViewType();
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

    public void setVisibleDay(int visibleDay) {
        mVisibleDay = visibleDay;
    }

    public void setForecastStartAt1(boolean startAt1) {
        mForecastStartAt1 = startAt1;
    }

    private void updateCurrentWeather(CurrentContentViewHolder holder, final int position,
            boolean isExpanded) {
        if (isExpanded) {
            holder.mExpandedContent.setVisibility(View.VISIBLE);
            holder.mExpandCollapseButtonDivider.setVisibility(View.VISIBLE);
            holder.mExpandCollapseButtonIcon.setImageResource(R.drawable.ic_expand_less);
            holder.mExpandCollapseButtonText.setText(R.string.collapse_card);
        } else {
            holder.mExpandedContent.setVisibility(View.GONE);
            holder.mExpandCollapseButtonDivider.setVisibility(View.GONE);
            holder.mExpandCollapseButtonIcon.setImageResource(R.drawable.ic_expand_more);
            holder.mExpandCollapseButtonText.setText(R.string.expand_card);
        }

        holder.mExpandCollapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCardClickedListener != null) {
                    TransitionManager.beginDelayedTransition(mRecyclerView);
                    mOnCardClickedListener.onCardExpandCollapsedClicked(position);
                }
            }
        });

        if (mWeatherInfo == null) {
            holder.mProviderLink.setOnClickListener(null);
            holder.mProviderLink.setClickable(false);
        } else {
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

            holder.mSubtitle.setText(mWeatherInfo.getTime());
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

            holder.mProviderLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {       
                    if (mOnCardClickedListener != null) {
                        mOnCardClickedListener.onCardProviderLinkClicked();
                    }
                }
            });
        }
    }

    private void updateForecastWeather(ForecastContentViewHolder holder, final int position,
            boolean isExpanded) {
        if (isExpanded) {
            holder.mExpandedContent.setVisibility(View.VISIBLE);
            holder.mExpandCollapseButtonDivider.setVisibility(View.VISIBLE);
            holder.mExpandCollapseButtonIcon.setImageResource(R.drawable.ic_expand_less);
            holder.mExpandCollapseButtonText.setText(R.string.collapse_card);
        } else {
            holder.mExpandedContent.setVisibility(View.GONE);
            holder.mExpandCollapseButtonDivider.setVisibility(View.GONE);
            holder.mExpandCollapseButtonIcon.setImageResource(R.drawable.ic_expand_more);
            holder.mExpandCollapseButtonText.setText(R.string.expand_card);
        }

        holder.mExpandCollapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCardClickedListener != null) {
                    TransitionManager.beginDelayedTransition(mRecyclerView);
                    mOnCardClickedListener.onCardExpandCollapsedClicked(position);
                }
            }
        });

        if (mWeatherInfo == null) {
            return;
        }

        String forecastDay = mWeatherInfo.getHourForecastDays().get(mVisibleDay);
        ArrayList<HourForecast> hourForecasts = mWeatherInfo.getHourForecastsDay(forecastDay);
        HourForecast h = null;
        if (hourForecasts.size() != 0) {
            h = hourForecasts.get(mForecastStartAt1 ? position - 1 : position);
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

        holder.mSubtitle.setText(h.getTime());
        if (conditionIconColor != 0) {
            holder.mImage.setImageTintList(ColorStateList.valueOf(conditionIconColor));
        } else {
            holder.mImage.setImageTintList(null);
        }
        holder.mImage.setImageDrawable(icon);
        holder.mTemp.setText(h.getFormattedTemperature());
        holder.mCondition.setText(h.getCondition());
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

    private void updateForecastDayTemps(ForecastDayTempsContentViewHolder holder) {
        if (mWeatherInfo == null) {
            holder.mProviderLink.setOnClickListener(null);
            holder.mProviderLink.setClickable(false);
        } else {
            final String[] tempValues = {
                mWeatherInfo.getForecasts().get(mVisibleDay).getFormattedMorning(),
                mWeatherInfo.getForecasts().get(mVisibleDay).getFormattedDay(),
                mWeatherInfo.getForecasts().get(mVisibleDay).getFormattedEvening(),
                mWeatherInfo.getForecasts().get(mVisibleDay).getFormattedNight()
            };
            for (int i = 0; i < holder.mDayTempsValues.length; i++) {
                holder.mDayTempsValues[i].setText(tempValues[i]);
            }
            final String min =
                    mWeatherInfo.getForecasts().get(mVisibleDay).getFormattedLow();
            final String max =
                    mWeatherInfo.getForecasts().get(mVisibleDay).getFormattedHigh();
            holder.mMinValue.setText(min);
            holder.mMaxValue.setText(max);

            holder.mProviderLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {       
                    if (mOnCardClickedListener != null) {
                        mOnCardClickedListener.onCardProviderLinkClicked();
                    }
                }
            });
        }
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
        public View mForecastDayTempsContentView;

        public CardViewHolder(View v) {
            super(v);

            mRootView = v;
            mCardView = (CardView) v.findViewById(R.id.weather_card);
            mCurrentContentView = v.findViewById(R.id.current_weather_content_layout);
            mForecastContentView = v.findViewById(R.id.forecast_weather_content_layout);
            mForecastDayTempsContentView = v.findViewById(R.id.forecast_day_temps_content_layout);
        }
    }

    public static class CurrentContentViewHolder extends CardViewHolder {
        public TextView mSubtitle;
        public ImageView mImage;
        public TextView mTemp;
        public TextView mTempLowHight;
        public TextView mCondition;
        public TextView[] mDayTempsValues;
        public View mExpandedContent;
        public TextView mPrecipitationValue;
        public TextView mWindValue;
        public TextView mSunriseValue;
        public TextView mHumidityValue;
        public TextView mPressureValue;
        public TextView mSunsetValue;
        public View mExpandCollapseButtonDivider;
        public TextView mProviderLink;
        public LinearLayout mExpandCollapseButton;
        public TextView mExpandCollapseButtonText;
        public ImageView mExpandCollapseButtonIcon;

        public CurrentContentViewHolder(View v) {
            super(v);
            mSubtitle = (TextView) findViewById(R.id.weather_card_content_subtitle);
            mImage = (ImageView) findViewById(R.id.weather_card_content_condition_image);
            mTemp = (TextView) findViewById(R.id.weather_card_content_temp);
            mTempLowHight = (TextView) findViewById(R.id.current_content_low_high);
            mCondition = (TextView) findViewById(R.id.weather_card_content_condition);
            mDayTempsValues = new TextView[] {
                    (TextView) findViewById(R.id.weather_card_content_day_temps_morning_value),
                    (TextView) findViewById(R.id.weather_card_content_day_temps_day_value),
                    (TextView) findViewById(R.id.weather_card_content_day_temps_evening_value),
                    (TextView) findViewById(R.id.weather_card_content_day_temps_night_value)
            };
            mExpandedContent = findViewById(R.id.weather_card_expanded_content_layout);
            mPrecipitationValue =
                    (TextView) findViewById(R.id.weather_card_expanded_content_precipitation_value);
            mWindValue = (TextView) findViewById(R.id.weather_card_expanded_content_wind_value);
            mSunriseValue = (TextView) findViewById(R.id.current_content_sunrise_value);
            mHumidityValue =
                    (TextView) findViewById(R.id.weather_card_expanded_content_humidity_value);
            mPressureValue =
                    (TextView) findViewById(R.id.weather_card_expanded_content_pressure_value);
            mSunsetValue = (TextView) findViewById(R.id.current_content_sunset_value);
            mExpandCollapseButtonDivider = 
                    findViewById(R.id.weather_card_content_expand_collapse_button_divider);
            mProviderLink = 
                    (TextView) findViewById(R.id.weather_card_content_provider_link);
            mExpandCollapseButton = 
                    (LinearLayout) findViewById(R.id.weather_card_content_expand_collapse_button);
            mExpandCollapseButtonText = 
                    (TextView) findViewById(R.id.weather_card_content_expand_collapse_button_text);
            mExpandCollapseButtonIcon = 
                    (ImageView) findViewById(R.id.weather_card_content_expand_collapse_button_icon);
        }

        private View findViewById(int id) {
            return mCurrentContentView.findViewById(id);
        }
    }

    public static class ForecastContentViewHolder extends CardViewHolder {
        public TextView mSubtitle;
        public ImageView mImage;
        public TextView mTemp;
        public TextView mCondition;
        public View mExpandedContent;
        public TextView mPrecipitationValue;
        public TextView mWindValue;
        public TextView mHumidityValue;
        public TextView mPressureValue;
        public View mExpandCollapseButtonDivider;
        public LinearLayout mExpandCollapseButton;
        public TextView mExpandCollapseButtonText;
        public ImageView mExpandCollapseButtonIcon;

        public ForecastContentViewHolder(View v) {
            super(v);
            mSubtitle = (TextView) findViewById(R.id.weather_card_content_subtitle);
            mImage = (ImageView) findViewById(R.id.weather_card_content_condition_image);
            mTemp = (TextView) findViewById(R.id.weather_card_content_temp);
            mCondition = (TextView) findViewById(R.id.weather_card_content_condition);
            mExpandedContent = findViewById(R.id.weather_card_expanded_content_layout);
            mPrecipitationValue = 
                    (TextView) findViewById(R.id.weather_card_expanded_content_precipitation_value);
            mWindValue = (TextView) findViewById(R.id.weather_card_expanded_content_wind_value);
            mHumidityValue =
                    (TextView) findViewById(R.id.weather_card_expanded_content_humidity_value);
            mPressureValue =
                    (TextView) findViewById(R.id.weather_card_expanded_content_pressure_value);
            mExpandCollapseButtonDivider = 
                    findViewById(R.id.weather_card_content_expand_collapse_button_divider);
            mExpandCollapseButton = 
                    (LinearLayout) findViewById(R.id.weather_card_content_expand_collapse_button);
            mExpandCollapseButtonText = 
                    (TextView) findViewById(R.id.weather_card_content_expand_collapse_button_text);
            mExpandCollapseButtonIcon = 
                    (ImageView) findViewById(R.id.weather_card_content_expand_collapse_button_icon);
        }

        private View findViewById(int id) {
            return mForecastContentView.findViewById(id);
        }
    }

    public static class ForecastDayTempsContentViewHolder extends CardViewHolder {
        public TextView[] mDayTempsValues;
        public TextView mMinValue;
        public TextView mMaxValue;
        public TextView mProviderLink;

        public ForecastDayTempsContentViewHolder(View v) {
            super(v);
            mDayTempsValues = new TextView[] {
                    (TextView) findViewById(R.id.weather_card_content_day_temps_morning_value),
                    (TextView) findViewById(R.id.weather_card_content_day_temps_day_value),
                    (TextView) findViewById(R.id.weather_card_content_day_temps_evening_value),
                    (TextView) findViewById(R.id.weather_card_content_day_temps_night_value)
            };
            mMinValue = (TextView) findViewById(R.id.forecast_day_temps_content_min_value);
            mMaxValue = (TextView) findViewById(R.id.forecast_day_temps_content_max_value);
            mProviderLink = (TextView) findViewById(R.id.weather_card_content_provider_link);
        }

        private View findViewById(int id) {
            return mForecastDayTempsContentView.findViewById(id);
        }
    }
}
