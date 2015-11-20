package com.globalfriends.com.aroundme.protocol;

/**
 * Created by vishal on 11/19/2015.
 */
public enum PriceRangeEnum {
    PRICE_LOW(0),
    PRICE_LOW_MEDIUM(1),
    PRICE_HIGH_MEDIUM(2),
    PRICE_HIGH(4);

    private int mPrice;

    PriceRangeEnum(final int name) {
        this.mPrice = name;
    }

    public int getPriceRange() {
        return this.mPrice;
    }
}
