package com.globalfriends.com.aroundme.data;

/**
 * Created by vishal on 11/22/2015.
 */
public enum DistanceFormatEnum {
    MILES(0), KILOMETER(1);

    private int mType;

    DistanceFormatEnum(final int type) {
        this.mType = type;
    }

    public static DistanceFormatEnum getFormat(final int val) {
        switch (val) {
            case 0:
                return MILES;
            case 1:
                return KILOMETER;
            default:
                return MILES;
        }
    }

    public int getValue() {
        return mType;
    }
}
