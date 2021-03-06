package com.globalfriends.com.aroundme.protocol.fourSquare;

import android.content.Context;
import android.text.TextUtils;

import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.data.fourSquare.FourSquarePlaceInfo;
import com.globalfriends.com.aroundme.data.fourSquare.FourSquarePlaceInfoJson;
import com.globalfriends.com.aroundme.data.fourSquare.FourSquarePlaceListJson;
import com.globalfriends.com.aroundme.protocol.DefaultFeatureManager;
import com.globalfriends.com.aroundme.protocol.Listener;
import com.globalfriends.com.aroundme.protocol.OperationEnum;
import com.globalfriends.com.aroundme.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vishal on 12/20/2015.
 */
public class FourSquareManager extends DefaultFeatureManager {
    Context mContext;
    private String mPlaceNumber;
    private String mInternationalNumber;

    public FourSquareManager(final Listener listener) {
        super(listener, AroundMeApplication.getContext().getResources().getString(R.string.four_square_tag));
        mContext = AroundMeApplication.getContext();
    }

    @Override
    public void findPlaceDetails(String internationalNumber, String phoneNumber, Double latitude, Double longitude) {
        super.findPlaceDetails(internationalNumber, phoneNumber, latitude, longitude);
        mPlaceNumber = null;//Reset
        mInternationalNumber = null;//Reset

        if (!TextUtils.isEmpty(phoneNumber)) {
            mPlaceNumber = phoneNumber.replaceAll("\\D+", "");
        }

        if (!TextUtils.isEmpty(internationalNumber)) {
            mInternationalNumber = internationalNumber.replaceAll("\\D+", "");
        }

        FourSquareWebService.Builder builder =
                new FourSquareWebService.Builder().
                        setSearchType().
                        setLocation(String.valueOf(latitude), String.valueOf(longitude)).
                        setClientId(mContext.
                                getResources().getString(R.string.foursquare_client_id)).
                        setClientKey(mContext.
                                getResources().getString(R.string.foursquare_client_secret)).
                        setVersion(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        sendVolleyJsonRequest(builder.build().getUrl(), OperationEnum.OPERATION_PLACE_LIST);
    }

    @Override
    protected void dispatchJsonResponse(OperationEnum operation, JSONObject response) {
        super.dispatchJsonResponse(operation, response);
        if (response == null) {
            mListener.onError(mContext.getString(R.string.no_more_results), mModuleTag);
            return;
        }

        String venueId = null;
        Utility.generateNoteOnSD("FourSquare_list", response.toString());
        switch (operation) {
            case OPERATION_PLACE_LIST:
                try {
                    JSONObject meta = (JSONObject) response.getJSONObject("meta");
                    if (meta.has("code")) {
                        int status = meta.getInt("code");
                        if (status != 200) { // Not OK
                            mListener.onError(meta.getString("errorDetail"), mModuleTag);
                            return;
                        }

                        JSONArray places = response.getJSONObject("response").getJSONArray("venues");
                        for (int i = 0; i < places.length(); i++) {
                            JSONObject obj = (JSONObject) places.get(i);
                            FourSquarePlaceInfo place = new FourSquarePlaceInfo();
                            place.setVenueId(obj.getString("id"));
                            if (obj.has("contact")) {
                                JSONObject contact = obj.getJSONObject("contact");
                                if (contact.has("phone")) {
                                    place.setPhone(contact.getString("phone").replaceAll("\\D+", ""));
                                    if (!TextUtils.isEmpty(mPlaceNumber)) {
                                        if (mPlaceNumber.endsWith(place.getPhone())) {
                                            venueId = place.getVenueId();
                                        }
                                    }

                                    if (!TextUtils.isEmpty(mInternationalNumber) && TextUtils.isEmpty(venueId)) {
                                        if (mInternationalNumber.endsWith(place.getPhone())) {
                                            venueId = place.getVenueId();
                                        }
                                    }
                                }
                            }

                            if (!TextUtils.isEmpty(venueId)) {
                                break;
                            }
                        }
                    } else {
                        mListener.onError(AroundMeApplication.getContext().getString(R.string.invalid_response),
                                mModuleTag);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (TextUtils.isEmpty(venueId)) {
                    mListener.onError(mContext.getString(R.string.no_more_results), mModuleTag);
                    return;
                }

                FourSquareWebService.Builder builder =
                        new FourSquareWebService.Builder().
                                setPlaceId(venueId).
                                setLocation(PreferenceManager.getLatitude(), PreferenceManager.getLongitude()).
                                setClientId(mContext.
                                        getResources().getString(R.string.foursquare_client_id)).
                                setClientKey(mContext.
                                        getResources().getString(R.string.foursquare_client_secret)).
                                setVersion(new SimpleDateFormat("yyyyMMdd").format(new Date()));
                sendVolleyJsonRequest(builder.build().getUrl(), OperationEnum.OPERATION_PLACE_DETAIL);
                break;

            case OPERATION_PLACE_DETAIL:
                Utility.generateNoteOnSD("FourSquare_place_detail", response.toString());
                try {
                    JSONObject meta = (JSONObject) response.getJSONObject("meta");
                    if (meta.has("code")) {
                        int status = meta.getInt("code");
                        if (status != 200) { // Not OK
                            mListener.onError(meta.getString("errorDetail"), mModuleTag);
                            return;
                        }

                        mListener.onGetPlaceDetails(new FourSquarePlaceInfoJson(response.
                                getJSONObject("response").getJSONObject("venue")), mModuleTag);
                    } else {
                        mListener.onError(AroundMeApplication.getContext().getString(R.string.invalid_response),
                                mModuleTag);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                // Some issue
        }
    }

    private String getPlaceIdFromListResponse(final JSONObject response, final String phoneNumber) {
        FourSquarePlaceListJson placeList = new FourSquarePlaceListJson(response);
        return placeList.getVenueId(phoneNumber);
    }

    @Override
    public int getFeatureIcon() {
        return R.drawable.foursquare;
    }

    @Override
    public int getFeatureFullLogo() {
        return R.drawable.foursquare_logo;
    }

    @Override
    public boolean isCustomRating() {
        return true;
    }
}
