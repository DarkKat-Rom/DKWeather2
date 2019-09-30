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
 
public class Config {
    public static final String PREF_KEY_CAT_THEME        = "cat_theme";
    public static final String PREF_KEY_CAT_SETTINGS     = "cat_settings";
    public static final String PREF_KEY_APP_THEME        = "app_theme";
    public static final String PREF_KEY_PREFERENCE_ICONS = "preference_icons";
    
    public static final String THEME_DAY =           "theme_day";
    public static final String THEME_NIGHT =         "theme_night";
    public static final String THEME_FOLLOW_SYSTEM = "theme_follow_system";

    public static final int HIDE_PREFERENCE_ICONS_SPACE_RESERVED = 0;
    public static final int HIDE_PREFERENCE_ICONS                = 1;
    public static final int SHOW_PREFERENCE_ICONS                = 2;

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
 }