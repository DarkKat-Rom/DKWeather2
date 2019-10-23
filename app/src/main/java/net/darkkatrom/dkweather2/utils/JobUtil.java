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

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import net.darkkatrom.dkweather2.WeatherJobService;

public class JobUtil {
    public static final int JOB_KEY_UPDATE = 1;

    public static void startUpdate(Context context) {
        JobInfo jobInfo = JobUtil.getBuilder(context, JOB_KEY_UPDATE)
                .build();
        JobUtil.getScheduler(context).schedule(jobInfo);
    }

    private static JobInfo.Builder getBuilder(Context context, int jobKey) {
        ComponentName componentName = new ComponentName(context, WeatherJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(jobKey, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        return builder;
    }

    private static JobScheduler getScheduler(Context context) {
        return (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
    }
}