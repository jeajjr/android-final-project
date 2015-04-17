package com.almasosorio.testing;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class is used to pack information of a group notification. Notifications are created when:
 * - a bill is created
 * - a bill is edited
 * - a bill is deleted
 * - an user is added
 */
public class Notification {
    public static final int BILL_CREATED = 1;
    public static final int BILL_EDITED = 2;
    public static final int BILL_DELETED = 3;
    public static final int USER_ADDED = 4;

    String groupName;
    String owner;
    String description;
    int type;
    Calendar date;

    public Notification(String groupName, String userName, int notificationType, String description){
        this.groupName = groupName;
        this.description = description;
        this.owner = userName;
        this.type = notificationType;
        this.date = Calendar.getInstance();
    }

    @Override
    public int hashCode(){
        return owner.hashCode() * description.hashCode() * date.getTime().hashCode();
    }

    @Override
    public String toString() {
        return "groupName: " + groupName + ", " +
                "date: " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date.getTime()) + ", " +
                "description: " + description + ", " +
                "owner: " + owner + ", " +
                "type: " + type;
    }


}