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
package net.darkkatrom.dkweather;

import android.Manifest;
import android.app.AlarmManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import net.darkkatrom.dkweather.providers.AbstractWeatherProvider;
import net.darkkatrom.dkweather.providers.WeatherContentProvider;
import net.darkkatrom.dkweather.providers.WidgetProvider;
import net.darkkatrom.dkweather.utils.Config;
import net.darkkatrom.dkweather.utils.JobUtil;
import net.darkkatrom.dkweather.utils.NotificationUtil;
import net.darkkatrom.dkweather.utils.ShortcutUtil;

import java.util.Locale;

public class WeatherJobService extends JobService {
    private static final String TAG = "DKWeather:WeatherJobService";
    private static final boolean DEBUG = false;

    public static final String ACTION_CANCEL_LOCATION_UPDATE =
            "net.darkkatrom.dkweather.CANCEL_LOCATION_UPDATE";

    private static final float LOCATION_ACCURACY_THRESHOLD_METERS = 50000;
    // request for at most 5 minutes
    public static final long LOCATION_REQUEST_TIMEOUT = 5L * 60L * 1000L;
    // 10 minutes
    private static final long OUTDATED_LOCATION_THRESHOLD_MILLIS = 10L * 60L * 1000L;

    private boolean mUpdateWidget;
    private WeatherInfo mWeatherInfo = null;

    private static final Criteria sLocationCriteria;
    static {
        sLocationCriteria = new Criteria();
        sLocationCriteria.setPowerRequirement(Criteria.POWER_LOW);
        sLocationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        sLocationCriteria.setCostAllowed(false);
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        HandlerThread handlerThread = new HandlerThread("WeatherJobService Thread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        int jobId = jobParameters.getJobId();
        final boolean updateWeather = jobId != JobUtil.JOB_KEY_WIDGET_UPDATE;
        mUpdateWidget = true;

        if (jobId == JobUtil.JOB_KEY_UPDATE) {
            PersistableBundle extras = jobParameters.getExtras();
            mUpdateWidget = extras.getBoolean(JobUtil.KEY_EXTRA_WIDGET_UPDATE);
        }

        if (!updateWeather) {
            mWeatherInfo = Config.getWeatherData(this);
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (updateWeather) {
                    updateWeather(jobParameters);
                } else if (mUpdateWidget) {
                    updateWidget(jobParameters);
                } else {
                    jobFinished(jobParameters, false);
                }
            }

        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    private void updateWeather(JobParameters jobParameters) {
        final NotificationUtil notificationUtil = new NotificationUtil(this);
        final ShortcutUtil shortcutUtil = new ShortcutUtil(this);
        try {
            AbstractWeatherProvider provider = Config.getProvider(this);
            if (!Config.isCustomLocation(this)) {
                if (checkPermissions()) {
                    Location location = getCurrentLocation();
                    if (location != null) {
                        mWeatherInfo = provider.getLocationWeather(location,
                                Config.isMetric(this));
                    }
                } else {
                    Log.w(TAG, "no location permissions");
                }
            } else if (Config.getLocationId(this) != null){
                mWeatherInfo = provider.getCustomWeather(Config.getLocationId(this),
                        Config.isMetric(this));
            } else {
                Log.w(TAG, "no valid custom location");
            }
            if (mWeatherInfo != null) {
                Config.setWeatherData(this, mWeatherInfo);
                WeatherContentProvider.updateCachedWeatherInfo(this);
                if (Config.getShowNotification(this)) {
                    notificationUtil.sendNotification();
                }
                shortcutUtil.addOrUpdateShortcuts();
            }
        } finally {
            if (mWeatherInfo == null) {
                // error
                Config.clearWeatherData(this);
                WeatherContentProvider.updateCachedWeatherInfo(this);
                notificationUtil.removeNotification(this);
                if (!mUpdateWidget) {
                    jobFinished(jobParameters, false);
                }
            } else if (mUpdateWidget) {
                updateWidget(jobParameters);
            } else {
                jobFinished(jobParameters, false);
            }
        }
    }

    private void updateWidget(JobParameters jobParameters) {
        ComponentName cn = new ComponentName(this, WidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(cn);

        if (mWeatherInfo != null) {
            for (int id : widgetIds) {
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget);

                if (Config.getWidgetBackground(this) == Config.WIDGET_BACKGROUND_NONE) {
                    remoteViews.setInt(R.id.widget_root, "setBackgroundColor", 0);
                    remoteViews.setInt(R.id.widget_container, "setBackgroundColor", 0);
                } else if (Config.getWidgetBackground(this) == Config.WIDGET_BACKGROUND_BACKGROUND_ONLY) {
                    int bgColor = Config.getWidgetBackgroundColor(this);
                    remoteViews.setInt(R.id.widget_root, "setBackgroundColor", bgColor);
                } else if (Config.getWidgetBackground(this) == Config.WIDGET_BACKGROUND_BACKGROUND_WITHOUT_FRAME) {
                    int bgColor = Config.getWidgetBackgroundColor(this);
                    remoteViews.setInt(R.id.widget_root, "setBackgroundColor", 0);
                    remoteViews.setInt(R.id.widget_container, "setBackgroundColor", bgColor);
                } else {
                    int frameColor = Config.getWidgetFrameColor(this);
                    int bgColor = Config.getWidgetBackgroundColor(this);
                    remoteViews.setInt(R.id.widget_root, "setBackgroundColor", frameColor);
                    remoteViews.setInt(R.id.widget_container, "setBackgroundColor", bgColor);
                }

                String nextAlarmText = getNextAlarmText();
                int iconResid = getResources().getIdentifier(
                        "weather_" + mWeatherInfo.getConditionCode(), "drawable", getPackageName());
                int[] dayTempsTextResId = new int[] {
                        R.id.widget_weather_temp_morning_value,
                        R.id.widget_weather_temp_day_value,
                        R.id.widget_weather_temp_evening_value,
                        R.id.widget_weather_temp_night_value
                };
                final String[] tempsText = {
                    mWeatherInfo.getForecasts().get(0).getFormattedMorning(),
                    mWeatherInfo.getForecasts().get(0).getFormattedDay(),
                    mWeatherInfo.getForecasts().get(0).getFormattedEvening(),
                    mWeatherInfo.getForecasts().get(0).getFormattedNight()
                };

                remoteViews.setViewVisibility(R.id.widget_loading_container, View.GONE);
                remoteViews.setViewVisibility(R.id.widget_clock_container, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.widget_date_alarm_container, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.widget_weather_container, View.VISIBLE);

                remoteViews.setCharSequence(R.id.widget_date_text, "setFormat12Hour",
                        getBestDatePatterns());
                remoteViews.setCharSequence(R.id.widget_date_text, "setFormat24Hour",
                        getBestDatePatterns());

                remoteViews.setViewVisibility(R.id.widget_next_alarm_icon, nextAlarmText != null
                        ? View.VISIBLE : View.GONE);
                remoteViews.setTextViewText(R.id.widget_next_alarm_text, nextAlarmText);
                remoteViews.setViewVisibility(R.id.widget_next_alarm_text, nextAlarmText != null
                        ? View.VISIBLE : View.GONE);

                remoteViews.setImageViewResource(R.id.widget_weather_condition_image, iconResid);

                remoteViews.setTextViewText(R.id.widget_weather_temp_text,
                        mWeatherInfo.getFormattedTemperature());
                remoteViews.setTextViewText(R.id.widget_weather_temp_low_high_text,
                        mWeatherInfo.getFormattedLow() + " | " + mWeatherInfo.getFormattedHigh());

                remoteViews.setTextViewText(R.id.widget_weather_location_text, mWeatherInfo.getCity());
                remoteViews.setTextViewText(R.id.widget_weather_condition_text,
                        mWeatherInfo.getCondition());

                remoteViews.setTextViewText(R.id.widget_weather_precipitation_text,
                        getPrecipitationText());
                remoteViews.setTextViewText(R.id.widget_weather_wind_text,
                        mWeatherInfo.getFormattedWind());

                for (int i = 0; i < dayTempsTextResId.length; i++) {
                    remoteViews.setTextViewText(dayTempsTextResId[i], tempsText[i]);
                }

                remoteViews.setTextViewText(R.id.widget_weather_timestamp_text, getUpdateDateTimeText());

                appWidgetManager.updateAppWidget(id, remoteViews);
            }
        }
        jobFinished(jobParameters, false);
    }

    private Location getCurrentLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.w(TAG, "network locations disabled");
            return null;
        }
        Location location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (DEBUG) Log.d(TAG, "Current location is " + location);

        if (location != null && location.getAccuracy() > LOCATION_ACCURACY_THRESHOLD_METERS) {
            Log.w(TAG, "Ignoring inaccurate location");
            location = null;
        }

        // If lastKnownLocation is not present (because none of the apps in the
        // device has requested the current location to the system yet) or outdated,
        // then try to get the current location use the provider that best matches the criteria.
        boolean needsUpdate = location == null;
        if (location != null) {
            long delta = System.currentTimeMillis() - location.getTime();
            needsUpdate = delta > OUTDATED_LOCATION_THRESHOLD_MILLIS;
        }
        if (needsUpdate) {
            if (DEBUG) Log.d(TAG, "Getting best location provider");
            String locationProvider = lm.getBestProvider(sLocationCriteria, true);
            if (TextUtils.isEmpty(locationProvider)) {
                Log.e(TAG, "No available location providers matching criteria.");
            } else {
                WeatherLocationListener.registerIfNeeded(this, locationProvider);
            }
        }

        return location;
    }

    private boolean checkPermissions() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private String getBestDatePatterns() {
        String skeleton = getResources().getString(R.string.abbrev_wday_month_day_no_year);
        return DateFormat.getBestDateTimePattern(Locale.getDefault(), skeleton);
    }



    private String getNextAlarmText() {
        String nextAlarmText = null;
        AlarmManager.AlarmClockInfo nextAlarmInfo = getAlarmInfo();

        if (nextAlarmInfo != null) {
            nextAlarmText = getNextAlarmFormattedTime(nextAlarmInfo.getTriggerTime());
        }

        return nextAlarmText;
    }

    private String getUpdateDateTimeText() {
        String dateTimeText = null;

        if (mWeatherInfo != null) {
            dateTimeText = getFormattedDateTime(mWeatherInfo.getTimestamp());
        }

        return dateTimeText;
    }

    private String getPrecipitationText() {
        final String rain1H = mWeatherInfo.getFormattedRain1H();
        final String rain3H = mWeatherInfo.getFormattedRain3H();
        final String snow1H = mWeatherInfo.getFormattedSnow1H();
        final String snow3H = mWeatherInfo.getFormattedSnow3H();
        final String noValue = getResources().getString(R.string.no_precipitation_value);
        if (!snow1H.equals(WeatherInfo.NO_VALUE)) {
            return snow1H;
        } else if (!snow3H.equals(WeatherInfo.NO_VALUE)) {
            return snow3H;
        } else if (!rain1H.equals(WeatherInfo.NO_VALUE)) {
            return rain1H;
        } else if (!rain3H.equals(WeatherInfo.NO_VALUE)) {
            return rain3H;
        } else {
            return noValue;
        }
    }

    private AlarmManager.AlarmClockInfo getAlarmInfo() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        return am.getNextAlarmClock();
    }

    private String getNextAlarmFormattedTime(long time) {
        String skeleton12 = getResources().getString(R.string.abbrev_wday_time_12_hours);
        String skeleton24 = getResources().getString(R.string.abbrev_wday_time_24_hours);
        String skeleton = DateFormat.is24HourFormat(this) ? skeleton24 : skeleton12;
        String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), skeleton);
        return (String) DateFormat.format(pattern, time);
    }

    private String getFormattedDateTime(long time) {
        String skeleton12 = getResources().getString(R.string.abbrev_wday_month_day_time_12_hours);
        String skeleton24 = getResources().getString(R.string.abbrev_wday_month_day_time_24_hours);
        String skeleton = DateFormat.is24HourFormat(this) ? skeleton24 : skeleton12;
        String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), skeleton);
        return (String) DateFormat.format(pattern, time);
    }
}
