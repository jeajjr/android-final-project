package com.almasosorio.sharethatbill;

//import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class is used to pack together information about a Bill.
 */
public class Bill {
    String billName;
    String groupName;
    Double billValue;
    Calendar billDate;
    Double billLocationLatitude = 0.0d;
    Double billLocationLongitude = 0.0d;
    boolean locationIsSet = false;
    String billPicturePath;
    //TODO
    //Bitmap billPicture;

    @Override
    public String toString() {
        return "Bill: " + billName + ", " +
                "groupName: " + groupName + ", " +
                "billValue: " + billValue + ", " +
                "billDate: " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(billDate.getTime()) + ", " +
                "Location: (" + billLocationLatitude + ", " + billLocationLongitude + "), " +
                "locationIsSet: " + locationIsSet;

    }
}