/*
 * Copyright (C) 2012 The AOKP Project
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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.text.DecimalFormat;

public class WeatherInfo {
    private static final DecimalFormat NO_DIGITS_FORMAT  = new DecimalFormat("0");
    private static final DecimalFormat ONE_DIGITS_FORMAT = new DecimalFormat("##0.#");
    private static final DecimalFormat TWO_DIGITS_FORMAT = new DecimalFormat("##0.##");

    private static final float PRECIPITATION_ITEM_THRESHOLD = 0.005f;

    public static final String NO_VALUE    = "-";

    private final Context mContext;
    private final String mPressureUnit;
    private final String mPrecipitationUnit1h;
    private final String mPrecipitationUnit3h;

    private final String mId;
    private final String mCity;
    private final String mCondition;
    private final int mConditionCode;
    private final float mTemperature;
    private final float mLow;
    private final float mHigh;
    private final String mTempUnit;
    private final float mHumidity;
    private final float mWind;
    private final int mWindDirection;
    private final String mSpeedUnit;
    private final float mPressure;
    private final float mRain1H;
    private final float mRain3H;
    private final float mSnow1H;
    private final float mSnow3H;
    private final long mTimestamp;
    private final long mSunrise;
    private final long mSunset;

    public WeatherInfo(Context context, String id, String city, String condition,
            int conditionCode, float temp, float low, float high, String tempUnit,
            float humidity, float wind, int windDir, String speedUnit, float pressure,
            float rain1H, float rain3H, float snow1H, float snow3H, long timestamp,
            long sunrise, long sunset) {
        mContext = context.getApplicationContext();
        mPressureUnit = mContext.getResources().getString(R.string.pressure_unit_title);
        mPrecipitationUnit1h = mContext.getResources().getString(R.string.precipitation_unit_1h_title);
        mPrecipitationUnit3h = mContext.getResources().getString(R.string.precipitation_unit_3h_title);

        mId = id;
        mCity = city;
        mCondition = condition;
        mConditionCode = conditionCode;
        mTemperature = temp;
        mLow = low;
        mHigh = high;
        mTempUnit = tempUnit;
        mHumidity = humidity;
        mWind = wind;
        mWindDirection = windDir;
        mSpeedUnit = speedUnit;
        mPressure = pressure;
        mRain1H = rain1H;
        mRain3H = rain3H;
        mSnow1H = snow1H;
        mSnow3H = snow3H;
        mTimestamp = timestamp;
        mSunrise = sunrise;
        mSunset = sunset;
    }

    public String getId() {
        return mId;
    }

    public String getCity() {
        return mCity;
    }

    public String getCondition() {
        return getCondition(mContext, mConditionCode, mCondition);
    }

    public int getConditionCode() {
        return mConditionCode;
    }

    public String getTemperature() {
        return getTemperature(mTemperature);
    }

    public String getFormattedTemperature() {
        return getFormattedTemperature(mTemperature, mTempUnit);
    }

    public String getLow() {
        return getTemperature(mLow);
    }

    public String getHigh() {
        return getTemperature(mHigh);
    }

    public String getFormattedLow() {
        return getFormattedTemperature(mLow, mTempUnit);
    }

    public String getFormattedHigh() {
        return getFormattedTemperature(mHigh, mTempUnit);
    }

    public String getFormattedHumidity() {
        return getFormattedHumidity(mHumidity);
    }

    public String getFormattedWind() {
        return getFormattedWind(mContext, mWind, mWindDirection, mSpeedUnit);
    }

    public String getFormattedPressure() {
        return getFormattedPressure(mPressure, mPressureUnit);
    }

    public String getFormattedRain1H() {
        return getFormattedPrecipitation(mRain1H, mPrecipitationUnit1h);
    }

    public String getFormattedRain3H() {
        return getFormattedPrecipitation(mRain3H, mPrecipitationUnit3h);
    }

    public String getFormattedSnow1H() {
        return getFormattedPrecipitation(mSnow1H, mPrecipitationUnit1h);
    }

    public String getFormattedSnow3H() {
        return getFormattedPrecipitation(mSnow3H, mPrecipitationUnit3h);
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public Date getDate() {
        return new Date(mTimestamp);
    }

    public String getTime() {
        return getTime(true);
    }

    public String getTime(boolean useLocaleTimeZone) {
        return getTime(getDate(), useLocaleTimeZone);
    }

    public String getFormattedDate() {
        return getFormattedDate(getDate(), true);
    }

    public String getSunrise() {
        return getTime(new Date(mSunrise), true);
    }

    public String getSunset() {
        return getTime(new Date(mSunset), true);
    }

    private static String getCondition(Context context, int conditionCode, String condition) {
        final Resources res = context.getResources();
        final int resId = res.getIdentifier("weather_" + conditionCode, "string", context.getPackageName());
        if (resId != 0) {
            return res.getString(resId);
        }
        return condition;
    }

    private static String getFormattedValue(float value, String unit) {
        if (Float.isNaN(value)) {
            return NO_VALUE;
        }
        String formatted = NO_DIGITS_FORMAT.format(value);
        if (formatted.equals("-0")) {
            formatted = "0";
        }
        return formatted + unit;
    }

    private static String getTemperature(float temp) {
        if (Float.isNaN(temp)) {
            return NO_VALUE;
        }
        String formatted = NO_DIGITS_FORMAT.format(temp);
        if (formatted.equals("-0")) {
            formatted = "0";
        }
        return formatted + "\u00b0";
    }

    private static String getFormattedTemperature(float temp, String unit) {
        if (Float.isNaN(temp)) {
            return NO_VALUE;
        }
        String formatted = NO_DIGITS_FORMAT.format(temp);
        if (formatted.equals("-0")) {
            formatted = "0";
        }
        return formatted + "\u00b0" + unit;
    }

    private static String getFormattedHumidity(float humidity) {
        if (Float.isNaN(humidity)) {
            return NO_VALUE;
        }
        String formatted = NO_DIGITS_FORMAT.format(humidity);
        if (formatted.equals("-0")) {
            formatted = "0";
        }
        if (formatted.equals("-1")) {
            return NO_VALUE;
        }
        return formatted + "%";
    }

    private static String getFormattedWind(Context context, float speed, int direction, String unit) {
        if (Float.isNaN(speed)) {
            return NO_VALUE;
        }
        if (NO_DIGITS_FORMAT.format(speed).equals("-0") || NO_DIGITS_FORMAT.format(speed).equals("0")) {
            return NO_VALUE;
        }

        speed *= 3.6f;

        String formattedUnitAndDirection = unit + " - " + getWindDirection(context, direction);

        return ONE_DIGITS_FORMAT.format(speed) + formattedUnitAndDirection;
    }

    private static String getWindDirection(Context context, int direction) {
        int resId;

        if (direction < 0) resId = R.string.unknown;
        else if (direction < 23) resId = R.string.weather_N;
        else if (direction < 68) resId = R.string.weather_NE;
        else if (direction < 113) resId = R.string.weather_E;
        else if (direction < 158) resId = R.string.weather_SE;
        else if (direction < 203) resId = R.string.weather_S;
        else if (direction < 248) resId = R.string.weather_SW;
        else if (direction < 293) resId = R.string.weather_W;
        else if (direction < 338) resId = R.string.weather_NW;
        else resId = R.string.weather_N;

        return context.getString(resId);
    }

    private static String getFormattedPressure(float pressure, String unit) {
        if (Float.isNaN(pressure)) {
            return NO_VALUE;
        }
        String formatted = ONE_DIGITS_FORMAT.format(pressure);
        if (formatted.equals("-0")) {
            formatted = "0";
        }
        if (formatted.equals("0")) {
            return NO_VALUE;
        }
        return formatted + unit;
    }

    private static String getFormattedPrecipitation(float precipitation, String unit) {
        if (Float.isNaN(precipitation)) {
            return NO_VALUE;
        }
        if (precipitation >= PRECIPITATION_ITEM_THRESHOLD) {
            return TWO_DIGITS_FORMAT.format(precipitation) + " " + unit;
        } else {
            return NO_VALUE;
        }
    }

    private static String getTime(Date date, boolean useLocaleTimeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm", Locale.getDefault());
        if (!useLocaleTimeZone) {
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        return sdf.format(date);
    }

    public static String getFormattedDate(Date date, boolean useLocaleTimeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.getDefault());
        if (!useLocaleTimeZone) {
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        return sdf.format(date);
    }

    public Drawable getConditionIcon(int conditionCode) {
        String iconName = "weather_";

        final int resId = mContext.getResources().getIdentifier(
                iconName + conditionCode, "drawable", mContext.getPackageName());
        if (resId != 0) {
            return mContext.getResources().getDrawable(resId);
        }

        // Use the default color set unknown icon
        return mContext.getResources().getDrawable(R.drawable.weather_na);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WeatherInfo for ");
        builder.append(getCity());
        builder.append(" (");
        builder.append(getId());
        builder.append(") @ ");
        builder.append(getDate());
        builder.append(": ");
        builder.append(getCondition());
        builder.append("(");
        builder.append(getConditionCode());
        builder.append("), temperature ");
        builder.append(getFormattedTemperature());
        builder.append(", low ");
        builder.append(getFormattedLow());
        builder.append(", high ");
        builder.append(getFormattedHigh());
        builder.append(", humidity ");
        builder.append(getFormattedHumidity());
        builder.append(", wind ");
        builder.append(getFormattedWind());
        builder.append(", pressure ");
        builder.append(getFormattedPressure());
        builder.append(", rain1H ");
        builder.append(getFormattedRain1H());
        builder.append(", rain3H ");
        builder.append(getFormattedRain3H());
        builder.append(", snow1H ");
        builder.append(getFormattedSnow1H());
        builder.append(", snow3H ");
        builder.append(getFormattedSnow3H());
        builder.append(", sunrise ");
        builder.append(getSunrise());
        builder.append(", sunset ");
        builder.append(getSunset());
        return builder.toString();
    }

    public String toSerializedString() {
        StringBuilder builder = new StringBuilder();
        builder.append(mId).append('|');
        builder.append(mCity).append('|');
        builder.append(mCondition).append('|');
        builder.append(mConditionCode).append('|');
        builder.append(mTemperature).append('|');
        builder.append(mLow).append('|');
        builder.append(mHigh).append('|');
        builder.append(mTempUnit).append('|');
        builder.append(mHumidity).append('|');
        builder.append(mWind).append('|');
        builder.append(mWindDirection).append('|');
        builder.append(mSpeedUnit).append('|');
        builder.append(mPressure).append('|');
        builder.append(mRain1H).append('|');
        builder.append(mRain3H).append('|');
        builder.append(mSnow1H).append('|');
        builder.append(mSnow3H).append('|');
        builder.append(mTimestamp).append('|');
        builder.append(mSunrise).append('|');
        builder.append(mSunset).append('|');
        return builder.toString();
    }

    public static WeatherInfo fromSerializedString(Context context, String input) {
        if (input == null) {
            return null;
        }

        String[] parts = input.split("\\|");
        if (parts == null || parts.length != 20) {
            return null;
        }

        int conditionCode, windDirection;
        long timestamp;
        long sunrise;
        long sunset;
        float temperature, low, high, humidity, wind, pressure, rain1H, rain3H,
        snow1H, snow3H;

        // Parse the core data
        try {
            conditionCode = Integer.parseInt(parts[3]);
            temperature = Float.parseFloat(parts[4]);
            low = Float.parseFloat(parts[5]);
            high = Float.parseFloat(parts[6]);
            humidity = Float.parseFloat(parts[8]);
            wind = Float.parseFloat(parts[9]);
            windDirection = Integer.parseInt(parts[10]);
            pressure = Float.parseFloat(parts[12]);
            rain1H = Float.parseFloat(parts[13]);
            rain3H = Float.parseFloat(parts[14]);
            snow1H = Float.parseFloat(parts[15]);
            snow3H = Float.parseFloat(parts[16]);
            timestamp = Long.parseLong(parts[17]);
            sunrise = Long.parseLong(parts[18]);
            sunset = Long.parseLong(parts[19]);
        } catch (NumberFormatException e) {
            return null;
        }

        return new WeatherInfo(context,
                /* id */ parts[0], /* city */ parts[1], /* condition */ parts[2],
                conditionCode, temperature, low, high, /* tempUnit */ parts[7],
                humidity, wind, windDirection, /* speedUnit */ parts[11],
                pressure, rain1H, rain3H, snow1H, snow3H, timestamp, sunrise, sunset);
    }
}