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
import android.text.TextUtils;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.SummaryProvider;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.snackbar.Snackbar;

import net.darkkatrom.dkweather2.R;
import net.darkkatrom.dkweather2.WeatherLocationTask;
import net.darkkatrom.dkweather2.utils.Config;
import net.darkkatrom.dkweather2.utils.JobUtil;
import net.darkkatrom.dkweather2.utils.LocationHelper;

public class WeatherSettingsFragment extends PreferenceFragmentCompat implements
        WeatherLocationTask.Callback {
    public static final String TAG = "WeatherSettingsFragmentTag";

    private Preference mWeatherUseMetricUnits;
    private Preference mWeatherUseCustomLocation;
    private EditTextPreference mWeatherCustomLocationCity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.weather_settings, rootKey);

        mWeatherUseMetricUnits = findPreference(Config.PREF_KEY_WEATHER_USE_METRIC_UNITS);
        mWeatherUseCustomLocation = findPreference(Config.PREF_KEY_WEATHER_USE_CUSTOM_LOCATION);
        mWeatherCustomLocationCity = findPreference(Config.PREF_KEY_WEATHER_CUSTOM_LOCATION_CITY);

        mWeatherUseMetricUnits.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        JobUtil.startUpdate(getActivity());
                        return true;
                    }
                });
        mWeatherCustomLocationCity.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        new WeatherLocationTask(getActivity(), (String) newValue,
                                WeatherSettingsFragment.this).execute();
                        return true;
                    }
                });
        mWeatherCustomLocationCity.setSummaryProvider(new SummaryProvider<EditTextPreference>() {
            @Override
            public CharSequence provideSummary(EditTextPreference preference) {
                String text = Config.getCustomLocationSummary(preference.getContext());
                if (text == null || TextUtils.isEmpty(text)) {
                    text = preference.getText();
                }
                if (text == null || TextUtils.isEmpty(text)) {
                    return preference.getContext().getString(R.string.not_set);
                }
                return text;
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        updateIconVisibility();
        mWeatherUseMetricUnits.setEnabled(
                LocationHelper.getLocationStatus(getActivity()) == LocationHelper.LOCATION_STATUS_OK);
    }

    private void updateIconVisibility() {
        int visibility = Config.getPreferenceIconsVisibility(getActivity());

        switch (visibility) {
            case Config.HIDE_PREFERENCE_ICONS_SPACE_RESERVED:
                mWeatherUseMetricUnits.setIconSpaceReserved(true);
                mWeatherUseCustomLocation.setIconSpaceReserved(true);
                mWeatherCustomLocationCity.setIconSpaceReserved(true);
                mWeatherUseMetricUnits.setIcon(null);
                mWeatherUseCustomLocation.setIcon(null);
                mWeatherCustomLocationCity.setIcon(null);
                break;
            case Config.HIDE_PREFERENCE_ICONS:
                mWeatherUseMetricUnits.setIconSpaceReserved(false);
                mWeatherUseCustomLocation.setIconSpaceReserved(false);
                mWeatherCustomLocationCity.setIconSpaceReserved(false);
                mWeatherUseMetricUnits.setIcon(null);
                mWeatherUseCustomLocation.setIcon(null);
                mWeatherCustomLocationCity.setIcon(null);
                break;
            default:
                mWeatherUseMetricUnits.setIcon(R.drawable.ic_units);
                mWeatherUseCustomLocation.setIcon(R.drawable.ic_gps);
                mWeatherCustomLocationCity.setIcon(R.drawable.ic_location);
                break;
        }
    }

    @Override
    public void applyLocation(String locationId, String locationCity, String summary) {
        Config.setCustomLocationSummary(getActivity(), summary);
        Config.setLocationId(getActivity(), locationId);
        Config.setLocationName(getActivity(), locationCity);
        mWeatherCustomLocationCity.setText(locationCity);
    }

    @Override
    public void showSnackbar() {
        Snackbar.make(getView(), R.string.toast_cannot_retrieve_location_title,
                Snackbar.LENGTH_SHORT).show();
    }
}