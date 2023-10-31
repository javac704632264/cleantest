package com.ai.subscription.stats;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.doodlecamera.base.core.thread.TaskHelper;
import com.doodlecamera.ccm.utils.Utils;

public class AdjustCollector {

    public static final String SUB_SHOW_TOKEN = "k64w5d";
    public static final String SUB_CLICK_TOKEN = "hedob6";
    public static final String SUB_SUCCESS_TOKEN = "r3659p";

    public static void onEvent(final String eventToken) {
        TaskHelper.RunnableWithName task = new TaskHelper.RunnableWithName("Adjust-Event") {
            @Override
            public void execute() {
//                AdjustEvent adjustEvent = new AdjustEvent(eventToken);
//                Adjust.trackEvent(adjustEvent);
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
