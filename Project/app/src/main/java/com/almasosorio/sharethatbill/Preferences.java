package com.almasosorio.sharethatbill;

/**
 * Created by osorio.bruno on 23/04/2015.
 */
public class Preferences {

    private static Preferences mInstance = null;

    private String mUserName;
    private String mUserEmail;

    private Preferences () {

    }

    public Preferences getInstance() {

        if (mInstance == null) {
            mInstance = new Preferences();
        }

        return mInstance;
    }

    public String getUserName() {
        return mUserName;
    }

}
