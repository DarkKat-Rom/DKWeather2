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

package net.darkkatrom.dkweather2.fragments;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import net.darkkatrom.dkweather2.R;
import net.darkkatrom.dkweather2.utils.Config;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String TAG = "SettingsFragmentTag";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateIconVisibility();
    }

    private void updateIconVisibility() {
        Preference appSettings = findPreference(Config.PREF_KEY_APP_SETTINGS);
        Preference weatherSettings = findPreference(Config.PREF_KEY_WEATHER_SETTINGS);

        int visibility = Config.getPreferenceIconsVisibility(getActivity());

        switch (visibility) {
            case Config.HIDE_PREFERENCE_ICONS_SPACE_RESERVED:
                appSettings.setIconSpaceReserved(true);
                weatherSettings.setIconSpaceReserved(true);
                appSettings.setIcon(null);
                weatherSettings.setIcon(null);
                break;
            case Config.HIDE_PREFERENCE_ICONS:
                appSettings.setIconSpaceReserved(false);
                weatherSettings.setIconSpaceReserved(false);
                appSettings.setIcon(null);
                weatherSettings.setIcon(null);
                break;
            default:
                appSettings.setIcon(R.drawable.ic_app_settings);
                weatherSettings.setIcon(R.drawable.ic_weather);
                break;
        }
    }
}