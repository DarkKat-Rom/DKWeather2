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

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import net.darkkatrom.dkweather2.R;
import net.darkkatrom.dkweather2.utils.Config;
import net.darkkatrom.dkweather2.utils.ThemeHelper;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String TAG = "SettingsFragmentTag";

    private Preference mCatTheme;
    private Preference mCatSettings;
    private Preference mAppTheme;
    private Preference mIconVisibility;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        mCatTheme = findPreference(Config.PREF_KEY_CAT_THEME);
        mCatSettings = findPreference(Config.PREF_KEY_CAT_SETTINGS);
        mAppTheme = findPreference(Config.PREF_KEY_APP_THEME);
        mIconVisibility = findPreference(Config.PREF_KEY_PREFERENCE_ICONS);

        mAppTheme.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        ThemeHelper.applyTheme((String) newValue);
                        return true;
                    }
                });

        mIconVisibility.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        updateIconVisibility(Integer.valueOf((String) newValue));
                        return true;
                    }
                });

        updateIconVisibility(Config.getPreferenceIconsVisibility(getActivity()));
    }

    @Override
    public void onResume() {
        ((AppCompatActivity) getActivity())
                    .getSupportActionBar().setTitle(R.string.toolbar_settings_subtitle);
        super.onResume();
    }

    private void updateIconVisibility(int visibility) {
        switch (visibility) {
            case Config.HIDE_PREFERENCE_ICONS_SPACE_RESERVED:
                mCatTheme.setIconSpaceReserved(true);
                mCatSettings.setIconSpaceReserved(true);
                mAppTheme.setIconSpaceReserved(true);
                mIconVisibility.setIconSpaceReserved(true);
                mAppTheme.setIcon(null);
                mIconVisibility.setIcon(null);
                break;
            case Config.HIDE_PREFERENCE_ICONS:
                mCatTheme.setIconSpaceReserved(false);
                mCatSettings.setIconSpaceReserved(false);
                mAppTheme.setIconSpaceReserved(false);
                mIconVisibility.setIconSpaceReserved(false);
                mAppTheme.setIcon(null);
                mIconVisibility.setIcon(null);
                break;
            default:
                mCatTheme.setIconSpaceReserved(true);
                mCatSettings.setIconSpaceReserved(true);
                mAppTheme.setIcon(R.drawable.ic_theme);
                mIconVisibility.setIcon(R.drawable.ic_show);
                break;
        }
    }
}