package com.globalfriends.com.aroundme.protocol.places;

/**
 * Created by vishal on 11/19/2015.
 */
public enum PlacesRankByEnum {
    RANK_BY_PROMINENCE("prominence"), RANK_BY_DISTANCE("distance");
    private String mName;

    PlacesRankByEnum(final String name) {
        this.mName = name;
    }

    public String getDescription() {
        return this.mName;
    }
}
