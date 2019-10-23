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
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import net.darkkatrom.dkweather2.R;
import net.darkkatrom.dkweather2.fragments.MainFragment;
import net.darkkatrom.dkweather2.fragments.SettingsFragment;
import net.darkkatrom.dkweather2.fragments.PermissionRationaleDialogFragment;
import net.darkkatrom.dkweather2.utils.Config;
import net.darkkatrom.dkweather2.utils.JobUtil;
import net.darkkatrom.dkweather2.utils.PermissionsHelper;

public class MainActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
        MainFragment.WeatherButtonClickListener,
        PermissionRationaleDialogFragment.PermissionRationaleDialogListener {

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
            case PermissionsHelper.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
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
                        dialogFragment.show(getSupportFragmentManager(), "dialog_fragment");
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
        if (!PermissionsHelper.hasLocationPermission(this)) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PermissionsHelper.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            JobUtil.startUpdate(this);
        }
    }

    @Override
    public void onWeatherDeleteButtonClick(MainFragment fragment) {
        Config.clearWeatherData(this);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        requestPermissions(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PermissionsHelper.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
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