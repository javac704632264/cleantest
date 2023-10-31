package com.ai.subscription.hepler;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class BusMutableLiveData<T> extends MutableLiveData<T> {

    private int currentVersion;

    private boolean isStick;

    public BusMutableLiveData(boolean isStick) {
        this.isStick = isStick;
    }

    public boolean isStick() {
        return isStick;
    }

    @Override
    public void setValue(T value) {
        currentVersion++;
        super.setValue(value);
    }

    @Override
    public void postValue(T value) {
        currentVersion++;
        super.postValue(value);
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, new ObserverWrapper<T>(observer, currentVersion, this));
    }

    private static class ObserverWrapper<T> implements Observer<T> {

        private Observer<? super T> observer;
        private int version;
        private BusMutableLiveData<T> liveData;

        public ObserverWrapper(Observer<? super T> observer, int version, BusMutableLiveData<T> liveData) {
            this.observer = observer;
            this.version = version;
            this.liveData = liveData;
        }

        @Override
        public void onChanged(T t) {

            if (t != null && t instanceof BusKey.IStickMsg) {
                BusKey.IStickMsg stickMsg = (BusKey.IStickMsg) t;
                if (stickMsg.isEnableStick()) {
                    if (!stickMsg.isConsume()) {
                        observer.onChanged(t);
                    }
                    return;
                }
            }
            if (liveData.currentVersion > version && observer != null) {
                observer.onChanged(t);
            }
        }
    }
}
