package com.globalfriends.com.aroundme.protocol.fourSquare;

import android.content.Context;
import android.text.TextUtils;

import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.data.fourSquare.FourSquarePlaceListJson;
import com.globalfriends.com.aroundme.protocol.DefaultFeatureManager;
import com.globalfriends.com.aroundme.protocol.Listener;
import com.globalfriends.com.aroundme.protocol.OperationEnum;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vishal on 12/20/2015.
 */
public class FourSquareManager extends DefaultFeatureManager {
    Context mContext;

    public FourSquareManager(final Listener listener) {
        super(listener, AroundMeApplication.getContext().getResources().getString(R.string.four_square_tag));
        mContext = AroundMeApplication.getContext();
    }

    @Override
    public void findPlaceDetails(String phoneNumber) {
        super.findPlaceDetails(phoneNumber);
        FourSquareWebService.Builder builder =
                new FourSquareWebService.Builder().
                        setSearchType().
                        setLocation(PreferenceManager.getLatitude(), PreferenceManager.getLongitude()).
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

        switch (operation) {
            case OPERATION_PLACE_LIST:
                final String venueId = getPlaceIdFromListResponse(response);
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
                                        getResources().getString(R.string.foursquare_client_secret));
                sendVolleyJsonRequest(builder.build().getUrl(), OperationEnum.OPERATION_PLACE_LIST);
                break;
            case OPERATION_PLACE_DETAIL:
                break;
            default:
                // Some issue
        }
    }

    private String getPlaceIdFromListResponse(final JSONObject response) {
        FourSquarePlaceListJson placeList = new FourSquarePlaceListJson(response);
        return placeList.getVenueId();
    }
}
