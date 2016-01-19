package com.globalfriends.com.aroundme.data;

import java.util.List;

/**
 * Created by vishal on 11/29/2015.
 */
public interface IPlaceDetails {

    String getAddress();

    String getInternationalPhoneNumber();

    String getPhoneNumber();

    String getWebUrl();

    String getDistance();

    String getPlaceName();

    Double getLatitude();

    Double getLongitude();

    void updatePhotoToList(PlacePhotoMetadata photo);

    List<PlacePhotoMetadata> getPhotos();

    void updateReviewToList(PlaceReviewMetadata photo);

    List<PlaceReviewMetadata> getReviewList();

    boolean isPermanentlyClosed();

    List<String> getWeeklyTimings();

    String getPlaceHolderIcon();

    String getRatingUrl();

    int getReviewCount();

    boolean isOpenNow();

    String getRating();

    String getPlaceRatingColorCode();
}
