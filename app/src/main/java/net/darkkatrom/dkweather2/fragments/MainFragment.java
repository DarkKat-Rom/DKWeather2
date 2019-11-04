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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import net.darkkatrom.dkweather2.R;
import net.darkkatrom.dkweather2.WeatherInfo;
import net.darkkatrom.dkweather2.activities.MainActivity;
import net.darkkatrom.dkweather2.utils.Config;
import net.darkkatrom.dkweather2.utils.LocationHelper;

public class MainFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener,
        View.OnClickListener {

    public static final String TAG = "MainFragmentTag";

    private int mLocationStatus;
    private boolean mIsLocationPermissionBlocked = false;
    private WeatherInfo mWeatherInfo;

    private AppCompatButton mWeatherUpdateButton;
    private View mWeatherDeleteButton;
    private View mWeatherLayout;
    private View mNoWeatherLayout;
    private TextView mLocationText;
    private TextView mConditionText;
    private TextView mTempText;
    private TextView mTempLowHighText;
    private ImageView mLocationImage;
    private ImageView mNoWeatherImage;
    private View mNoWeatherMessageTextLayout;
    private TextView mNoWeatherMessageText;

    private WeatherButtonClickListener mListener = null;

    public interface WeatherButtonClickListener {
        void onWeatherUpdateButtonClick(MainFragment fragment);
        void onWeatherDeleteButtonClick(MainFragment fragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WeatherButtonClickListener) {
            mListener = (WeatherButtonClickListener) context;
        }
    }

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

        mLocationStatus = LocationHelper.getLocationStatus(getActivity());
        mIsLocationPermissionBlocked = Config.isLocationPermissionBlocked(getActivity());
        mWeatherInfo = Config.getWeatherData(getActivity());

        mWeatherUpdateButton = (AppCompatButton) view.findViewById(R.id.update_weather_data);
        mWeatherDeleteButton = view.findViewById(R.id.delete_weather_data);

        mWeatherLayout = view.findViewById(R.id.weather_layout);
        mNoWeatherLayout = view.findViewById(R.id.no_weather_layout);
        mLocationText = (TextView) view.findViewById(R.id.weather_location_text);
        mConditionText = (TextView) view.findViewById(R.id.weather_condition_text);
        mTempText = (TextView) view.findViewById(R.id.weather_temp_text);
        mTempLowHighText = (TextView) view.findViewById(R.id.weather_temp_low_high_text);
        mLocationImage = (ImageView) view.findViewById(R.id.weather_condition_image);
        mNoWeatherImage = (ImageView) view.findViewById(R.id.no_weather_image);
        mNoWeatherMessageTextLayout = view.findViewById(R.id.no_weather_message_text_layout);
        mNoWeatherMessageText = (TextView) view.findViewById(R.id.no_weather_message_text);

        mWeatherUpdateButton.setOnClickListener(this);
        mWeatherDeleteButton.setOnClickListener(this);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);

        updateContent();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocationStatus != LocationHelper.getLocationStatus(getActivity())
                || mIsLocationPermissionBlocked != Config.isLocationPermissionBlocked(getActivity())) {
            mLocationStatus = LocationHelper.getLocationStatus(getActivity());
            mIsLocationPermissionBlocked = Config.isLocationPermissionBlocked(getActivity());
            if (mLocationStatus < LocationHelper.LOCATION_STATUS_NO_PERMISSION && mIsLocationPermissionBlocked) {
                mIsLocationPermissionBlocked = false;
                Config.setIsLocationPermissionBlocked(getActivity(), mIsLocationPermissionBlocked);
            }
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
            if (mListener != null) {
                mListener.onWeatherUpdateButtonClick(MainFragment.this);
            }
        } else {
            if (mListener != null) {
                mListener.onWeatherDeleteButtonClick(MainFragment.this);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Config.PREF_KEY_WEATHER_DATA)) {
            if (getActivity() != null) {
                mWeatherInfo = Config.getWeatherData(getActivity());
                updateContent();
            }
        }
    }

    private void updateContent() {
        if (mWeatherInfo != null && mLocationStatus == LocationHelper.LOCATION_STATUS_OK) {
            mWeatherUpdateButton.setEnabled(true);
            mWeatherLayout.setVisibility(View.VISIBLE);
            mNoWeatherLayout.setVisibility(View.GONE);
            if (mNoWeatherMessageTextLayout != null) {
                mNoWeatherMessageTextLayout.setVisibility(View.GONE);
            }
            mLocationText.setText(mWeatherInfo.getCity());
            mConditionText.setText(mWeatherInfo.getCondition());
            mTempText.setText(mWeatherInfo.getFormattedTemperature());
            mTempLowHighText.setText(mWeatherInfo.getFormattedLow() + " | " + mWeatherInfo.getFormattedHigh());
            mLocationImage.setImageDrawable(mWeatherInfo.getConditionIcon(mWeatherInfo.getConditionCode()));
        } else {
            mWeatherLayout.setVisibility(View.GONE);
            mNoWeatherLayout.setVisibility(View.VISIBLE);
            if (mNoWeatherMessageTextLayout != null) {
                mNoWeatherMessageTextLayout.setVisibility(View.VISIBLE);
            }
            int color = getColorFromThemeAttribute(mLocationStatus == LocationHelper.LOCATION_STATUS_OK
                    ? android.R.attr.textColorPrimary : R.attr.colorError);
            mNoWeatherImage.setImageResource(mLocationStatus == LocationHelper.LOCATION_STATUS_OK
                    ? R.drawable.weather_na : R.drawable.ic_location_error);
            mNoWeatherImage.setImageTintList(ColorStateList.valueOf(color));
            mNoWeatherMessageText.setText(LocationHelper.getLocationStatusMessageResId(getActivity()));
            mNoWeatherMessageText.setTextColor(color);
        }
        int updateButtonTextResId = mWeatherInfo == null
                ? R.string.load_weather_data_title : R.string.update_weather_data_title;
        mWeatherUpdateButton.setText(updateButtonTextResId);
        mWeatherUpdateButton.setEnabled(!mIsLocationPermissionBlocked);
        mWeatherDeleteButton.setEnabled(mWeatherInfo != null);
    }

    private int getColorFromThemeAttribute(int resId) {
        TypedValue tv = new TypedValue();
        getActivity().getTheme().resolveAttribute(resId, tv, true);
        int colorRes = tv.resourceId != 0 ? tv.resourceId : tv.data;
        return getActivity().getColor(colorRes);
    }
}