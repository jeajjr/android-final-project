package com.almasosorio.sharethatbill;

/**
 * Created by osorio.bruno on 23/04/2015.
 */
public class Preferences {

    private static Preferences mInstance = null;

    private String mUserName;
    private String mUserEmail;
    private int mConnectionTimeout = 10 * 1000;

    private Preferences () {

    }

    public static Preferences getInstance() {

        if (mInstance == null) {
            mInstance = new Preferences();
        }

        return mInstance;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getUserEmail() {
        return mUserEmail;
    }

    public int getTimeout() {
        return mConnectionTimeout;
    }

}
