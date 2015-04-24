package com.almasosorio.sharethatbill;

/**
 * Created by osorio.bruno on 23/04/2015.
 */
public class Preferences {

    private static Preferences mInstance = null;

    private String mUserName;
    private String mUserEmail;
    private String mCurrentGroup;
    private int mConnectionTimeout = 10 * 1000;

    private Preferences () {

    }

    public static Preferences getInstance() {

        if (mInstance == null) {
            mInstance = new Preferences();
        }

        return mInstance;
    }

    public void setUserName(String name) { mUserName = name;}

    public void setUserEmail(String email) { mUserEmail = email;}

    public void setCurrentGroup(String group) { mCurrentGroup = group;}

    public String getUserName() {
        return mUserName;
    }

    public String getUserEmail() {
        return mUserEmail;
    }

    public String getCurrentGroup() { return mCurrentGroup;}

    public int getTimeout() {
        return mConnectionTimeout;
    }

}
