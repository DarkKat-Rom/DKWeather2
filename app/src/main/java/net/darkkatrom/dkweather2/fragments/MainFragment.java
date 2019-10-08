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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import net.darkkatrom.dkweather2.R;
import net.darkkatrom.dkweather2.WeatherInfo;
import net.darkkatrom.dkweather2.activities.MainActivity;
import net.darkkatrom.dkweather2.utils.Config;
import net.darkkatrom.dkweather2.utils.JobUtil;

public class MainFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener,
        View.OnClickListener {

    public static final String TAG = "MainFragmentTag";

    private WeatherInfo mWeatherInfo;

    private TextView mLocationText;
    private TextView mConditionText;
    private TextView mTempText;
    private TextView mTempLowHighText;
    private ImageView mLocationImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity())
                    .getSupportActionBar().setTitle(R.string.app_name);

        mWeatherInfo = Config.getWeatherData(getActivity());

        view.findViewById(R.id.update_weather_data).setOnClickListener(this);
        view.findViewById(R.id.delete_weather_data).setOnClickListener(this);
        
        mLocationText = (TextView) view.findViewById(R.id.weather_location_text);
        mConditionText = (TextView) view.findViewById(R.id.weather_condition_text);
        mTempText = (TextView) view.findViewById(R.id.weather_temp_text);
        mTempLowHighText = (TextView) view.findViewById(R.id.weather_temp_low_high_text);
        mLocationImage = (ImageView) view.findViewById(R.id.weather_condition_image);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);

        if (mWeatherInfo == null) {
            JobUtil.startUpdate(getActivity());
        } else {
            updateContent();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            ((MainActivity) getActivity()).showFragment(SettingsFragment.TAG);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.update_weather_data) {
            JobUtil.startUpdate(getActivity());
        } else {
            Config.clearWeatherData(getActivity());
        }
        updateContent();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Config.PREF_KEY_WEATHER_DATA)) {
            if (getActivity() != null) {
                mWeatherInfo = Config.getWeatherData(getActivity());
                if (mWeatherInfo != null) {
                    updateContent();
                }
            }
        }
    }

    private void updateContent() {
        if (mWeatherInfo == null) {
            mLocationText.setText("--");
            mConditionText.setText(null);
            mTempText.setText(null);
            mTempLowHighText.setText(null);
            mLocationImage.setImageResource(R.drawable.weather_na);
        } else {
            mLocationText.setText(mWeatherInfo.getCity());
            mConditionText.setText(mWeatherInfo.getCondition());
            mTempText.setText(mWeatherInfo.getFormattedTemperature());
            mTempLowHighText.setText(mWeatherInfo.getFormattedLow() + " | " + mWeatherInfo.getFormattedHigh());
            mLocationImage.setImageDrawable(mWeatherInfo.getConditionIcon(mWeatherInfo.getConditionCode()));
        }
    }
}