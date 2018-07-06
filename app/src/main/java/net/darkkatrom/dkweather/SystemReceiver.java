/*
 *  Copyright (C) 2015 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.darkkatrom.dkweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.darkkatrom.dkweather.utils.Config;
import net.darkkatrom.dkweather.utils.JobUtil;

public class SystemReceiver extends BroadcastReceiver {
    private static final String TAG = "DKWeather:SystemReceiver";
    private static final boolean DEBUG = false;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (Config.isEnabled(context)) {
            final String action = intent.getAction();
            String logMessage = null;
            if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
                // kick updates
                JobUtil.startUpdate(context, false);
                JobUtil.scheduleUpdate(context);
                logMessage = "boot completed";
            } else if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
                // kick updates
                JobUtil.startUpdate(context);
                JobUtil.scheduleUpdate(context);
                logMessage = "package replaced";
            } else if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
                // kick updates
                JobUtil.startUpdate(context);
                logMessage = "locale changed";
            }

            if (logMessage != null && DEBUG) {
                Log.d(TAG, logMessage);
            }
        }
    }
}
