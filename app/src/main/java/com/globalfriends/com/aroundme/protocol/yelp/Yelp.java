package com.globalfriends.com.aroundme.protocol.yelp;
/*
 Example code based on code from Nicholas Smith at http://imnes.blogspot.com/2011/01/how-to-use-yelp-v2-from-java-including.html
 For a more complete example (how to integrate with GSON, etc) see the blog post above.
 */

import android.content.Context;

import com.globalfriends.com.aroundme.R;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Example for accessing the Yelp API.
 */
public class Yelp {
    private static final String TAG = "Yelp";
    OAuthService service;
    Token accessToken;

    /**
     * Setup the Yelp API OAuth credentials.
     * <p/>
     * OAuth credentials are available from the developer site, under Manage API access (version 2 API).
     *
     * @param consumerKey    Consumer key
     * @param consumerSecret Consumer secret
     * @param token          Token
     * @param tokenSecret    Token secret
     */
    public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
    }

    public static Yelp getYelp(Context context) {
        return new Yelp(context.getString(R.string.yelp_consumer_key), context.getString(R.string.yelp_consumer_secret),
                context.getString(R.string.yelp_token), context.getString(R.string.yelp_token_secret));
    }

    /**
     * Search with term and location.
     *
     * @param term      Search term
     * @param latitude  Latitude
     * @param longitude Longitude
     * @return JSON string response
     */
    public String search(final String term, final double latitude, final double longitude, final int limit) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("ll", latitude + "," + longitude);
        request.addQuerystringParameter("limit", Integer.toString(limit));
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        String body = response.getBody();
        return body;
    }

    /**
     * @return
     */
    public String phoneSearch(final String phoneNumber, final String countryCode) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/phone_search");
        request.addQuerystringParameter("phone", phoneNumber);
        request.addQuerystringParameter("cc", countryCode);
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        String body = response.getBody();
        return body;
    }

    /**
     * Search with term string location.
     *
     * @param term
     * @param location
     * @return
     */
    public String search(final String term, final String location, final int limit) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("limit", Integer.toString(limit));
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        String body = response.getBody();
        return body;
    }
}
