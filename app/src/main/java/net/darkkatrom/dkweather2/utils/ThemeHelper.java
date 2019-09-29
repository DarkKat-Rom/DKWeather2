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

import android.app.UiModeManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {

    public static final int THEME_ERROR = -2;

    public static int getSystemTheme(Context context) {
        UiModeManager uiModeManager =
                (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager == null) {
            return THEME_ERROR;
        }

        return uiModeManager.getNightMode();
    }

    public static int getAppTheme(Context context) {
        return Config.getTheme(context);
    }

    public static void applyTheme(String theme) {
        switch (theme) {
            case Config.THEME_DAY:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case Config.THEME_NIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
    
    public static void applyTheme(int theme) {
        AppCompatDelegate.setDefaultNightMode(theme);
    }
}