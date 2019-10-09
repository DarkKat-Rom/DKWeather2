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
package net.darkkatrom.dkweather2.providers;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import net.darkkatrom.dkweather2.WeatherInfo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class AbstractWeatherProvider {
    private static final String TAG = "DKWeather:AbstractWeatherProvider";
    private static final boolean DEBUG = false;

    protected Context mContext;

    public AbstractWeatherProvider(Context context) {
        mContext = context;
    }

    protected String retrieve(String urlString) {
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();
        boolean error = false;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } else {
                error = true;
            }
        } catch(Exception e) {
            error = true;
            Log.e(TAG, "Couldn't retrieve data from url " + urlString, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return error ? null : result.toString();
    }

    public abstract WeatherInfo getLocationWeather(Location location, boolean metric);

    protected void log(String tag, String msg) {
        if (DEBUG) Log.d("WeatherJobService:" + tag, msg);
    }
}