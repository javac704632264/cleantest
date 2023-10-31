package com.ai.subscription.hepler;

public interface BusKey {

    public static interface IStickMsg {
        boolean isEnableStick();

        boolean isConsume();

        void consume();
    }
}
