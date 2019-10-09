/*
 * Copyright (C) 2019 DarkKat
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
package net.darkkatrom.dkweather2;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import net.darkkatrom.dkweather2.providers.AbstractWeatherProvider;
import net.darkkatrom.dkweather2.providers.OpenWeatherMapProvider;
import net.darkkatrom.dkweather2.utils.Config;
import net.darkkatrom.dkweather2.utils.JobUtil;

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

        handler.post(new Runnable() {
            @Override
            public void run() {
                updateWeather(jobParameters);
            }

        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    private void updateWeather(JobParameters jobParameters) {
        try {
            AbstractWeatherProvider provider = new OpenWeatherMapProvider(this);
            Location location = getCurrentLocation();
            if (location != null) {
                mWeatherInfo = provider.getLocationWeather(location, Config.isMetric(this));
            }
            if (mWeatherInfo != null) {
                Config.setWeatherData(this, mWeatherInfo);
            }
        } finally {
            if (mWeatherInfo == null) {
                Config.clearWeatherData(this);
            }
            jobFinished(jobParameters, false);
        }
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
}