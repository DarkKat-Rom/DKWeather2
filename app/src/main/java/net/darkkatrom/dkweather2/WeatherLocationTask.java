/*
 * Copyright (C) 2012 The CyanogenMod Project (DvTonder)
 *
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

package net.darkkatrom.dkweather2;

import java.util.HashSet;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import net.darkkatrom.dkweather2.providers.OpenWeatherMapProvider;
import net.darkkatrom.dkweather2.utils.Config;

public class WeatherLocationTask extends AsyncTask<Void, Void, List<WeatherInfo.WeatherLocation>> {
    private FragmentActivity mActivity;
    private String mLocation;
    private Callback mCallback;
    private ProgressDialogFragment mProgressDialogFragment;

    public interface Callback {
        void applyLocation(String locationId, String locationCity, String summary);
        void showSnackbar();
    };

    public WeatherLocationTask(FragmentActivity activity, String location, Callback callback) {
        mActivity = activity;
        mLocation = location;
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialogFragment = new ProgressDialogFragment();
        mProgressDialogFragment.show(mActivity.getSupportFragmentManager(), "progress_dialog_fragment");
    }

    @Override
    protected List<WeatherInfo.WeatherLocation> doInBackground(Void... input) {
        return new OpenWeatherMapProvider(mActivity).getLocations(mLocation);
    }

    @Override
    protected void onPostExecute(List<WeatherInfo.WeatherLocation> results) {
        super.onPostExecute(results);

        if (results == null || results.isEmpty()) {
            mCallback.showSnackbar();
        } else if (results.size() > 1) {
            handleResultDisambiguation(results);
        } else {
            mCallback.applyLocation(results.get(0).id, results.get(0).city, results.get(0).city);
        }
        mProgressDialogFragment.dismiss();
    }

    private void handleResultDisambiguation(final List<WeatherInfo.WeatherLocation> results) {
        CharSequence[] items = buildItemList(results);
        String[] locationIds = new String[results.size()];
        String[] locationCities = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            locationIds[i] = results.get(i).id;
            locationCities[i] = results.get(i).city;
        }
        Bundle args = new Bundle();
        ChoiceDialogFragment dialog = new ChoiceDialogFragment();

        args.putCharSequenceArray("choice_items", items);
        args.putStringArray("location_ids", locationIds);
        args.putStringArray("location_cities", locationCities);
        dialog.setArguments(args);
        dialog.setTargetFragment((Fragment) mCallback, 0);
        dialog.show(mActivity.getSupportFragmentManager(), "choice_dialog_fragment");
    }

    private CharSequence[] buildItemList(List<WeatherInfo.WeatherLocation> results) {
        boolean needCountry = false, needPostal = false;
        String countryId = results.get(0).countryId;
        HashSet<String> postalIds = new HashSet<String>();

        for (WeatherInfo.WeatherLocation result : results) {
            if (!TextUtils.equals(result.countryId, countryId)) {
                needCountry = true;
            }
            String postalId = result.countryId + "##" + result.city;
            if (postalIds.contains(postalId)) {
                needPostal = true;
            }
            postalIds.add(postalId);
            if (needPostal && needCountry) {
                break;
            }
        }

        int count = results.size();
        CharSequence[] items = new CharSequence[count];
        for (int i = 0; i < count; i++) {
            WeatherInfo.WeatherLocation result = results.get(i);
            StringBuilder builder = new StringBuilder();
            if (needPostal && result.postal != null) {
                builder.append(result.postal).append(" ");
            }
            builder.append(result.city);
            if (needCountry) {
                String country = result.country != null
                        ? result.country : result.countryId;
                builder.append(" (").append(country).append(")");
            }
            items[i] = builder.toString();
        }
        return items;
    }

    public static class ProgressDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            float density = getActivity().getResources().getDisplayMetrics().density;
            int paddingTopBottom = (int) (18 * density);
            int paddingLeftRight = (int) (24 * density);
            final ProgressBar progressBar = new ProgressBar(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(lp);
            progressBar.setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight,
                    paddingTopBottom);

            builder.setTitle(R.string.dialog_location_progress_title)
                    .setView(progressBar);
            return builder.create();
        }
    }

    public static class ChoiceDialogFragment extends DialogFragment {
        CharSequence[] items = null;
        String[] locationIds = null;
        String[] locationCities = null;

        Callback getCallback() {
            return (Callback) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            items = getArguments().getCharSequenceArray("choice_items");
            locationIds = getArguments().getStringArray("location_ids");
            locationCities = getArguments().getStringArray("location_cities");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.dialog_select_location_title)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getCallback().applyLocation(locationIds[which], locationCities[which],
                                    (String) items[which]);
                        }
                    })
                   .setNegativeButton(android.R.string.cancel, null);
            return builder.create();
        }
    }
}