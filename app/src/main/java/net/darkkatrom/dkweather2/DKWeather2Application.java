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

package net.darkkatrom.dkweather2;

import android.app.Application;

import net.darkkatrom.dkweather2.utils.Config;
import net.darkkatrom.dkweather2.utils.ThemeHelper;

public class DKWeather2Application extends Application {

    public void onCreate() {
        super.onCreate();

        ThemeHelper.applyTheme(Config.getTheme(this));
    }
}