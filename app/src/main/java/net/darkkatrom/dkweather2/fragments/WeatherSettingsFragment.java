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
import net.darkkatrom.dkweather2.utils.JobUtil;
import net.darkkatrom.dkweather2.utils.PermissionsHelper;

public class WeatherSettingsFragment extends PreferenceFragmentCompat {
    public static final String TAG = "WeatherSettingsFragmentTag";

    private Preference mWeatherUseMetricUnits;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.weather_settings, rootKey);

        mWeatherUseMetricUnits = findPreference(Config.PREF_KEY_WEATHER_USE_METRIC_UNITS);
        
        mWeatherUseMetricUnits.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        JobUtil.startUpdate(getActivity());
                        return true;
                    }
                });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        updateIconVisibility();
        mWeatherUseMetricUnits.setEnabled(
                PermissionsHelper.hasLocationPermission(getActivity()));
    }

    private void updateIconVisibility() {
        int visibility = Config.getPreferenceIconsVisibility(getActivity());

        switch (visibility) {
            case Config.HIDE_PREFERENCE_ICONS_SPACE_RESERVED:
                mWeatherUseMetricUnits.setIconSpaceReserved(true);
                mWeatherUseMetricUnits.setIcon(null);
                break;
            case Config.HIDE_PREFERENCE_ICONS:
                mWeatherUseMetricUnits.setIconSpaceReserved(false);
                mWeatherUseMetricUnits.setIcon(null);
                break;
            default:
                mWeatherUseMetricUnits.setIcon(R.drawable.ic_units);
                break;
        }
    }
}