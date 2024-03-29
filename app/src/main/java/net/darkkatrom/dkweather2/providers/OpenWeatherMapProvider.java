/*
 * Copyright (C) 2013 The CyanogenMod Project
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

package net.darkkatrom.dkweather2.providers;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import net.darkkatrom.dkweather2.R;
import net.darkkatrom.dkweather2.WeatherInfo;
import net.darkkatrom.dkweather2.utils.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenWeatherMapProvider extends AbstractWeatherProvider {
    private static final String TAG = "DKWeather:OpenWeatherMapProvider";

    private static final int FORECAST_DAYS = 5;
    private static final String SELECTION_LOCATION = "lat=%f&lon=%f";
    private static final String API_KEY = "6d2f4f034d60d9680a720c12df8c7ddd";

    private static final String URL_LOCATION =
            "https://api.openweathermap.org/data/2.5/find?q=%s&mode=json&lang=%s&appid=%s";
    private static final String URL_WEATHER =
            "https://api.openweathermap.org/data/2.5/weather?%s&mode=json&units=%s&lang=%s&appid=%s";
    private static final String URL_FORECAST =
            "https://api.openweathermap.org/data/2.5/forecast/daily?" +
            "%s&mode=json&units=%s&lang=%s&cnt=" + FORECAST_DAYS + "&appid=%s";

    public OpenWeatherMapProvider(Context context) {
        super(context);
    }

    public List<WeatherInfo.WeatherLocation> getLocations(String input) {
        String url = String.format(URL_LOCATION, Uri.encode(input), getLanguageCode(), API_KEY);
        String response = retrieve(url);
        if (response == null) {
            return null;
        }

        log(TAG, "URL = " + url + " returning a response of " + response);

        try {
            JSONArray jsonResults = new JSONObject(response).getJSONArray("list");
            ArrayList<WeatherInfo.WeatherLocation> results = new ArrayList<WeatherInfo.WeatherLocation>();
            int count = jsonResults.length();

            for (int i = 0; i < count; i++) {
                JSONObject result = jsonResults.getJSONObject(i);
                WeatherInfo.WeatherLocation location = new WeatherInfo.WeatherLocation();

                location.id = result.getString("id");
                location.city = result.getString("name");
                location.countryId = result.getJSONObject("sys").getString("country");
                results.add(location);
            }

            return results;
        } catch (JSONException e) {
            Log.e(TAG, "Received malformed location data (input=" + input + ")", e);
        }

        return null;
    }

    public WeatherInfo getLocationWeather(Location location, boolean metric) {
        String selection = String.format(Locale.US, SELECTION_LOCATION,
                location.getLatitude(), location.getLongitude());
        return handleWeatherRequest(selection, metric);
    }

    private WeatherInfo handleWeatherRequest(String selection, boolean metric) {
        String units = metric ? "metric" : "imperial";
        String locale = getLanguageCode();
        String conditionUrl = String.format(Locale.US, URL_WEATHER, selection, units, locale, API_KEY);
        String conditionResponse = retrieve(conditionUrl);
        if (conditionResponse == null) {
            return null;
        }
        log(TAG, "Condition URL = " + conditionUrl + " returning a response of " + conditionResponse);

        String forecastUrl = String.format(Locale.US, URL_FORECAST, selection, units, locale, API_KEY);
        String forecastResponse = retrieve(forecastUrl);
        if (forecastResponse == null) {
            return null;
        }
        log(TAG, "Forecast URL = " + forecastUrl + " returning a response of " + forecastResponse);

        try {
            JSONObject conditions = new JSONObject(conditionResponse);
            JSONObject weather = conditions.getJSONArray("weather").getJSONObject(0);
            JSONObject conditionData = conditions.getJSONObject("main");
            JSONObject windData = conditions.getJSONObject("wind");
            JSONObject rainData = conditions.has("rain") ? conditions.getJSONObject("rain") : null;
            JSONObject snowData = conditions.has("snow") ? conditions.getJSONObject("snow") : null;
            JSONObject sysData = conditions.getJSONObject("sys");
            float[] lowhigh =
                    parseForecasts(new JSONObject(forecastResponse).getJSONArray("list"), metric);
            int tempUnitResId = metric
                    ? R.string.temp_celsius_unit_title : R.string.temp_fahrenheit_unit_title;
            int speedUnitResId = metric
                    ? R.string.speed_kph_unit_title : R.string.speed_mph_unit_title;
            String localizedCityName = conditions.getString("name");

            WeatherInfo w = new WeatherInfo(mContext, conditions.getString("id"), localizedCityName,
                    /* condition */ weather.getString("main"),
                    /* conditionCode */ mapConditionIconToCode(
                            weather.getString("icon"), weather.getInt("id")),
                    /* temperature */ sanitizeTemperature(conditionData.getDouble("temp"), metric),
                    /* low */ lowhigh[0],
                    /* high */ lowhigh[1],
                    /* tempUnit */ mContext.getString(tempUnitResId),
                    /* humidity */ (float) conditionData.getDouble("humidity"),
                    /* wind */ (float) windData.getDouble("speed"),
                    /* windDir */ windData.has("deg") ? windData.getInt("deg") : 0,
                    /* speedUnit */ mContext.getString(speedUnitResId),
                    /* pressure */ (float) conditionData.getDouble("pressure"),
                    /* rain1h */ rainData == null ? 0 : rainData.has("1h") ? (float) rainData.getDouble("1h") : 0,
                    /* rain3h */ rainData == null ? 0 : rainData.has("3h") ? (float) rainData.getDouble("3h") : 0,
                    /* snow1h */ snowData == null ? 0 : snowData.has("1h") ? (float) snowData.getDouble("1h") : 0,
                    /* snow3h */ snowData == null ? 0 : snowData.has("3h") ? (float) snowData.getDouble("3h") : 0,
                    /* timestamp */ conditions.getLong("dt") * 1000,
                    /* sunrise */ sysData.getLong("sunrise") * 1000,
                    /* sunset */ sysData.getLong("sunset") * 1000);

            log(TAG, "Weather updated: " + w);
            return w;
        } catch (JSONException e) {
            Log.e(TAG, "Received malformed weather data (selection = " + selection
                    + ", lang = " + locale + ")", e);
        }

        return null;
    }

    private float[] parseForecasts(JSONArray forecasts, boolean metric) throws JSONException {
        float result[] = new float[2];
        int count = forecasts.length();

        if (count == 0) {
            throw new JSONException("Empty forecasts array");
        }
        try {
            JSONObject forecast = forecasts.getJSONObject(0);
            JSONObject temperature = forecast.getJSONObject("temp");
            /* low */ result[0] = sanitizeTemperature(temperature.getDouble("min"), metric);
            /* high */ result[1] = sanitizeTemperature(temperature.getDouble("max"), metric);
        } catch (JSONException e) {
            Log.w(TAG, "Invalid forecast for day 0, creating dummy", e);

            /* low */ result[0] = 0;
            /* high */ result[1] = 0;
        }
        return result;
    }

    // OpenWeatherMap sometimes returns temperatures in Kelvin even if we ask it
    // for deg C or deg F. Detect this and convert accordingly.
    private static float sanitizeTemperature(double value, boolean metric) {
        // threshold chosen to work for both C and F. 170 deg F is hotter
        // than the hottest place on earth.
        if (value > 170) {
            // K -> deg C
            value -= 273.15;
            if (!metric) {
                // deg C -> deg F
                value = (value * 1.8) + 32;
            }
        }
        return (float) value;
    }
    
    private static final HashMap<String, String> LANGUAGE_CODE_MAPPING = new HashMap<String, String>();
    static {
        LANGUAGE_CODE_MAPPING.put("bg-", "bg");
        LANGUAGE_CODE_MAPPING.put("de-", "de");
        LANGUAGE_CODE_MAPPING.put("es-", "sp");
        LANGUAGE_CODE_MAPPING.put("fi-", "fi");
        LANGUAGE_CODE_MAPPING.put("fr-", "fr");
        LANGUAGE_CODE_MAPPING.put("it-", "it");
        LANGUAGE_CODE_MAPPING.put("nl-", "nl");
        LANGUAGE_CODE_MAPPING.put("pl-", "pl");
        LANGUAGE_CODE_MAPPING.put("pt-", "pt");
        LANGUAGE_CODE_MAPPING.put("ro-", "ro");
        LANGUAGE_CODE_MAPPING.put("ru-", "ru");
        LANGUAGE_CODE_MAPPING.put("se-", "se");
        LANGUAGE_CODE_MAPPING.put("tr-", "tr");
        LANGUAGE_CODE_MAPPING.put("uk-", "ua");
        LANGUAGE_CODE_MAPPING.put("zh-CN", "zh_cn");
        LANGUAGE_CODE_MAPPING.put("zh-TW", "zh_tw");
    }
    private String getLanguageCode() {
        Locale locale = mContext.getResources().getConfiguration().locale;
        String selector = locale.getLanguage() + "-" + locale.getCountry();

        for (Map.Entry<String, String> entry : LANGUAGE_CODE_MAPPING.entrySet()) {
            if (selector.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "en";
    }

    private static final HashMap<String, Integer> ICON_MAPPING = new HashMap<String, Integer>();
    static {
        ICON_MAPPING.put("01d", 32);
        ICON_MAPPING.put("01n", 31);
        ICON_MAPPING.put("02d", 30);
        ICON_MAPPING.put("02n", 29);
        ICON_MAPPING.put("03d", 26);
        ICON_MAPPING.put("03n", 26);
        ICON_MAPPING.put("04d", 28);
        ICON_MAPPING.put("04n", 27);
        ICON_MAPPING.put("09d", 12);
        ICON_MAPPING.put("09n", 11);
        ICON_MAPPING.put("10d", 40);
        ICON_MAPPING.put("10n", 45);
        ICON_MAPPING.put("11d", 4);
        ICON_MAPPING.put("11n", 4);
        ICON_MAPPING.put("13d", 16);
        ICON_MAPPING.put("13n", 16);
        ICON_MAPPING.put("50d", 21);
        ICON_MAPPING.put("50n", 20);
    }

    private int mapConditionIconToCode(String icon, int conditionId) {

        // First, use condition ID for specific cases
        switch (conditionId) {
            // Thunderstorms
            case 202:   // thunderstorm with heavy rain
            case 232:   // thunderstorm with heavy drizzle
            case 211:   // thunderstorm
                return 4;
            case 212:   // heavy thunderstorm
                return 3;
            case 221:   // ragged thunderstorm
            case 231:   // thunderstorm with drizzle
            case 201:   // thunderstorm with rain
                return 38;
            case 230:   // thunderstorm with light drizzle
            case 200:   // thunderstorm with light rain
            case 210:   // light thunderstorm
                return 37;

            // Drizzle
            case 300:    // light intensity drizzle
            case 301:    // drizzle
            case 302:    // heavy intensity drizzle
            case 310:    // light intensity drizzle rain
            case 311:    // drizzle rain
            case 312:    // heavy intensity drizzle rain
            case 313:    // shower rain and drizzle
            case 314:    // heavy shower rain and drizzle
            case 321:    // shower drizzle
                return 9;

            // Rain
            case 500:    // light rain
            case 501:    // moderate rain
            case 520:    // light intensity shower rain
            case 521:    // shower rain
            case 531:    // ragged shower rain
                return 11;
            case 502:    // heavy intensity rain
            case 503:    // very heavy rain
            case 504:    // extreme rain
            case 522:    // heavy intensity shower rain
                return 12;
            case 511:    // freezing rain
                return 10;

            // Snow
            case 600: case 620: return 14; // light snow
            case 601: case 621: return 16; // snow
            case 602: case 622: return 41; // heavy snow
            case 611: case 612: return 18; // sleet
            case 615: case 616: return 5;  // rain and snow

            // Atmosphere
            case 741:    // fog
                return 20;
            case 711:    // smoke
            case 762:    // volcanic ash
                return 22;
            case 701:    // mist
            case 721:    // haze
                return 21;
            case 731:    // sand/dust whirls
            case 751:    // sand
            case 761:    // dust
                return 19;
            case 771:    // squalls
                return 23;
            case 781:    // tornado
                return 0;

            // Extreme
            case 900: return 0;  // tornado
            case 901: return 1;  // tropical storm
            case 902: return 2;  // hurricane
            case 903: return 25; // cold
            case 904: return 36; // hot
            case 905: return 24; // windy
            case 906: return 17; // hail
        }

        // Not yet handled - Use generic icon mapping
        Integer condition = ICON_MAPPING.get(icon);
        if (condition != null) {
            return condition;
        }

        return -1;
    }
}