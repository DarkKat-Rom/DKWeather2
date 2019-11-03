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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.fragment.app.FragmentActivity;

import net.darkkatrom.dkweather2.R;

public class LocationHelper {
        public static final int LOCATION_STATUS_OK                     = 0;
        public static final int LOCATION_STATUS_DISABLED               = 1;
        public static final int LOCATION_STATUS_NO_PERMISSION          = 2;
        public static final int LOCATION_STATUS_DISABLED_NO_PERMISSION = 3;
    
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    public static boolean hasLocationPermission(Context context) {
        return context.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean locationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static int getLocationStatus(Context context) {
        int status = LOCATION_STATUS_OK;
        if (!hasLocationPermission(context)) {
            status = LOCATION_STATUS_NO_PERMISSION;
        }
        if (!locationEnabled(context)) {
            if (status == LOCATION_STATUS_NO_PERMISSION) {
                status = LOCATION_STATUS_DISABLED_NO_PERMISSION;
            } else {
                status = LOCATION_STATUS_DISABLED;
            }
        }
        return status;
    }

    public static int getLocationStatusMessageResId(Context context) {
        int status = getLocationStatus(context);
        int resId = 0;
        switch (status) {
            case LOCATION_STATUS_DISABLED:
                resId = R.string.location_status_disabled_message;
                break;
            case LOCATION_STATUS_NO_PERMISSION:
                resId = R.string.location_status_no_permission_message;
                break;
            case LOCATION_STATUS_DISABLED_NO_PERMISSION:
                resId = R.string.location_status_disabled_no_permission_message;
                break;
            default:
                resId = R.string.weather_data_missing_title;
                break;
        }
        return resId;
    }
}