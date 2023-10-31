package com.ai.subscription.stats;

import android.os.Bundle;

import com.doodlecamera.base.core.thread.TaskHelper;
import com.doodlecamera.base.core.utils.lang.ObjectStore;
import com.doodlecamera.tools.core.utils.Utils;
import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseCollector {

    public static final String SUB_SHOW = "sub_show";
    public static final String SUB_CLICK = "sub_click";
    public static final String SUB_SUCCESS = "sub_success";

    public static void onEvent(final String eventId, final Bundle map) {
        TaskHelper.RunnableWithName task = new TaskHelper.RunnableWithName("Firebase-Event") {
            @Override
            public void execute() {
                FirebaseAnalytics.getInstance(ObjectStore.getContext()).logEvent(eventId, map);
            }
        };
        executeStatsTask(task);
    }

    private static void executeStatsTask(TaskHelper.RunnableWithName task) {
        if (Utils.isOnMainThread()) {
            TaskHelper.execZForSDK(task);
        } else {
            task.execute();
        }
    }

}
