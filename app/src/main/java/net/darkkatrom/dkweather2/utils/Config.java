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
 
package net.darkkatrom.dkweather2.utils;
 
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import net.darkkatrom.dkweather2.WeatherInfo;

public class Config {
    public static final String PREF_KEY_CAT_THEME        = "cat_theme";
    public static final String PREF_KEY_CAT_SETTINGS     = "cat_settings";
    public static final String PREF_KEY_APP_SETTINGS     = "app_settings";
    public static final String PREF_KEY_WEATHER_SETTINGS = "weather_settings";
    public static final String PREF_KEY_APP_THEME        = "app_theme";
    public static final String PREF_KEY_PREFERENCE_ICONS = "preference_icons";
    
    public static final String THEME_DAY =           "theme_day";
    public static final String THEME_NIGHT =         "theme_night";
    public static final String THEME_FOLLOW_SYSTEM = "theme_follow_system";

    public static final String PREF_KEY_WEATHER_USE_METRIC_UNITS =
            "weather_use_metric_units";
    public static final String PREF_KEY_WEATHER_USE_CUSTOM_LOCATION =
            "weather_use_custom_location";
    public static final String PREF_KEY_WEATHER_CUSTOM_LOCATION_CITY =
            "weather_custom_location_city";
    public static final String PREF_KEY_WEATHER_CUSTOM_LOCATION_CITY_SUMMARY =
            "weather_custom_location_city_summary";

    public static final int HIDE_PREFERENCE_ICONS_SPACE_RESERVED = 0;
    public static final int HIDE_PREFERENCE_ICONS                = 1;
    public static final int SHOW_PREFERENCE_ICONS                = 2;

    public static final String PREF_KEY_LOCATION_ID   = "location_id";
    public static final String PREF_KEY_LOCATION_NAME = "location_name";
    public static final String PREF_KEY_WEATHER_DATA  = "weather_data";

    public static final String PREF_KEY_LOCATION_PERMISSION_BLOCKED =
            "location_permission_blocked";

    public static int getTheme(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String theme = prefs.getString(PREF_KEY_APP_THEME, THEME_FOLLOW_SYSTEM);
        int themeInt;
        switch (theme) {
            case THEME_DAY:
                themeInt = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case THEME_NIGHT:
                themeInt = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            default:
                themeInt = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
        }
        return themeInt;
    }

    public static int getPreferenceIconsVisibility(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String icons = prefs.getString(PREF_KEY_PREFERENCE_ICONS, "0");
        int iconsInt;
        switch (icons) {
            case "0":
                iconsInt = HIDE_PREFERENCE_ICONS_SPACE_RESERVED;
                break;
            case "1":
                iconsInt = HIDE_PREFERENCE_ICONS;
                break;
            default:
                iconsInt = SHOW_PREFERENCE_ICONS;
                break;
        }
        return iconsInt;
    }

    public static WeatherInfo getWeatherData(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String str = prefs.getString(PREF_KEY_WEATHER_DATA, null);
        if (str != null) {
            WeatherInfo data = WeatherInfo.fromSerializedString(context, str);
            return data;
        }
        return null;
    }
    
    public static void setWeatherData(Context context, WeatherInfo data) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().putString(PREF_KEY_WEATHER_DATA, data.toSerializedString()).commit();
    }

    public static void clearWeatherData(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().remove(PREF_KEY_WEATHER_DATA).commit();
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_WEATHER_USE_METRIC_UNITS, true);
    }

    public static String getLocationId(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getString(PREF_KEY_LOCATION_ID, null);
    }

    public static void setLocationId(Context context, String id) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().putString(PREF_KEY_LOCATION_ID, id).commit();
    }
    
    public static String getLocationName(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getString(PREF_KEY_LOCATION_NAME, null);
    }
    
    public static void setLocationName(Context context, String name) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().putString(PREF_KEY_LOCATION_NAME, name).commit();
    }

    public static String getCustomLocationSummary(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getString(PREF_KEY_WEATHER_CUSTOM_LOCATION_CITY_SUMMARY, null);
    }

    public static void setCustomLocationSummary(Context context, String summary) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        prefs.edit().putString(PREF_KEY_WEATHER_CUSTOM_LOCATION_CITY_SUMMARY, summary).commit();
    }

    public static boolean isLocationPermissionBlocked(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.getBoolean(PREF_KEY_LOCATION_PERMISSION_BLOCKED, false);
    }

    public static boolean setIsLocationPermissionBlocked(Context context, boolean blocked) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        return prefs.edit().putBoolean(PREF_KEY_LOCATION_PERMISSION_BLOCKED, blocked).commit();
    }
 }