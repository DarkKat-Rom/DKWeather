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

package net.darkkatrom.dkweather.model;

import android.content.Context;
import android.content.res.Resources;

public class WeatherCard {
    public static final int VIEW_TYPE_CURRENT_WEATHER   = 0;
    public static final int VIEW_TYPE_FORECAST_WEATHER  = 1;
    public static final int VIEW_TYPE_FORECAST_DAYTEMPS = 2;

    private Context mContext;
    private Resources mResources;
    private int mViewType;
    private boolean mIsExpanded = false;

    public WeatherCard(Context context, int viewType) {
        mContext = context;
        mResources = mContext.getResources();
        mViewType = viewType;
    }

    public int getViewType() {
        return mViewType;
    }

    public boolean isExpanded() {
        return mIsExpanded;
    }

    public void toggleIsExpanded() {
        mIsExpanded = !mIsExpanded;
    }


}
