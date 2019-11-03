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

package net.darkkatrom.dkweather2.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import net.darkkatrom.dkweather2.R;
import net.darkkatrom.dkweather2.fragments.LocationDisabledDialogFragment;
import net.darkkatrom.dkweather2.fragments.MainFragment;
import net.darkkatrom.dkweather2.fragments.PermissionRationaleDialogFragment;
import net.darkkatrom.dkweather2.fragments.SettingsFragment;
import net.darkkatrom.dkweather2.utils.Config;
import net.darkkatrom.dkweather2.utils.JobUtil;
import net.darkkatrom.dkweather2.utils.LocationHelper;

public class MainActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
        MainFragment.WeatherButtonClickListener,
        PermissionRationaleDialogFragment.PermissionRationaleDialogListener,
        LocationDisabledDialogFragment.LocationDisabledDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        if (savedInstanceState == null) {
            showFragment(MainFragment.TAG);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LocationHelper.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Config.isLocationPermissionBlocked(this)) {
                        Config.setIsLocationPermissionBlocked(this, false);
                    }
                    JobUtil.startUpdate(this);
                } else if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if (Config.isLocationPermissionBlocked(this)) {
                            Config.setIsLocationPermissionBlocked(this, false);
                        }
                        PermissionRationaleDialogFragment dialogFragment = new PermissionRationaleDialogFragment();
                        dialogFragment.show(getSupportFragmentManager(), "permission_rationale_dialog_fragment");
                    } else {
                        if (!Config.isLocationPermissionBlocked(this)) {
                            Config.setIsLocationPermissionBlocked(this, true);
                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, fragment)
                .addToBackStack(null)
                .commit();
        return true;
    }

    @Override
    public void onWeatherUpdateButtonClick(MainFragment fragment) {
        int locationStatus = LocationHelper.getLocationStatus(this);
        if (locationStatus == LocationHelper.LOCATION_STATUS_OK) {
            JobUtil.startUpdate(this);
        } else {
            if (locationStatus == LocationHelper.LOCATION_STATUS_DISABLED) {
                LocationDisabledDialogFragment dialogFragment = new LocationDisabledDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "location_disabled_dialog_fragment");
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LocationHelper.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onWeatherDeleteButtonClick(MainFragment fragment) {
        Config.clearWeatherData(this);
    }

    @Override
    public void onRationaleDialogPositiveClick(DialogFragment dialog) {
        requestPermissions(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LocationHelper.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

    }

    @Override
    public void onRationaleDialogNegativeClick(DialogFragment dialog) {
    }

    @Override
    public void onLocationDisabledDialogPositiveClick(DialogFragment dialog) {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }

    @Override
    public void onLocationDisabledDialogNegativeClick(DialogFragment dialog) {
    }

    public void showFragment(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            switch (tag) {
                case MainFragment.TAG: {
                    fragment = new MainFragment();
                    break;
                }
                case SettingsFragment.TAG: {
                    fragment = new SettingsFragment();
                    break;
                }
                default: {
                    fragment = new MainFragment();
                    break;
                }
            }
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragment instanceof SettingsFragment) {
            ft.addToBackStack(null);
        }
        ft.replace(R.id.fragment_layout, fragment, tag)
            .commit();
    }
}