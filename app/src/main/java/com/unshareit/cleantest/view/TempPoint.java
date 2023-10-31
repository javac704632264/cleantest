package com.unshareit.cleantest.view;

import android.os.Parcel;
import android.os.Parcelable;

public class TempPoint implements Parcelable {
    public int x;
    public int y;


    protected TempPoint(Parcel in) {
        x = in.readInt();
        y = in.readInt();
    }

    public static final Creator<TempPoint> CREATOR = new Creator<TempPoint>() {
        @Override
        public TempPoint createFromParcel(Parcel in) {
            return new TempPoint(in);
        }

        @Override
        public TempPoint[] newArray(int size) {
            return new TempPoint[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(x);
        parcel.writeInt(y);
    }
}
