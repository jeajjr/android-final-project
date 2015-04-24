package com.almasosorio.sharethatbill;


import android.util.Log;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DBHandler {
    private static final String TAG = "DBHandler";

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String HOST = "jdbc:mysql://192.236.71.121/finalproj";
    private static final String DB_USER = "app";
    private static final String DB_PW = "app";

    public DBHandler() {
        try {
            Class.forName(JDBC_DRIVER).newInstance();

        } catch (Exception e) {
            handleException(e);
        }
    }

    void handleException(Exception e) {
        //System.out.println(e.getMessage());
        //System.out.println(e.getCause());
        Log.d(TAG, "Exception", e);
    }

    private String generateBillID(String groupName, String billName) {
        return groupName + billName;
    }

    /**
     * Method to check the user credentials on the database.
     * If there are no match, returns false.
     * If matches, return true.
     *
     * @param userEmail email of an user
     * @param password password of the user
     * @return login valid (true) or not (false)
     */
    public boolean checkLogin(String userEmail, String password) {
        boolean isValid = false;

        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, userEmail);
            psmtm.setString(2, password);
            ResultSet rs = psmtm.executeQuery();

            if (rs.next())
                isValid = true;
            else
                isValid = false;

            connect.close();

        } catch (SQLException e) {
            handleException(e);
        }
        return isValid;
    }

    /**
     * Method to get the groups of an user.
     *
     * @param userEmail email of an user
     * @return ArrayList<String> users
     */
    public ArrayList<String> getUserGroups(String userEmail) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT gid FROM usersAndGroups WHERE uid=?";

            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, userEmail);
            ResultSet rs = psmtm.executeQuery();

            while (rs.next())
                result.add(rs.getString(1));

            connect.close();
        } catch (SQLException e) {
            handleException(e);
        }
        return result;
    }

    /**
     * Method to get the group's members
     *
     * @param groupName name of the group
     * @return ArrayList<String> members
     */
    public ArrayList<String> getGroupMembers(String groupName) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT uid FROM usersAndGroups WHERE gid=?";
            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, groupName);
            ResultSet rs = psmtm.executeQuery();

            while (rs.next())
                result.add(rs.getString(1));

            connect.close();
        } catch (SQLException e) {
            handleException(e);
        }
        return result;
    }

    /**
     * Method to get the group's members
     *
     * @param groupName name of the group
     * @return ArrayList<String> members
     */
    public ArrayList<TwoStringsClass> getGroupMembersNames(String groupName) {
        ArrayList<TwoStringsClass> result = new ArrayList<>();
        ArrayList<String> emails = new ArrayList<>();
        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT uid FROM usersAndGroups WHERE gid=?";
            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, groupName);
            ResultSet rs = psmtm.executeQuery();

            while (rs.next())
                emails.add(rs.getString(1));

            connect.close();
        } catch (SQLException e) {
            handleException(e);
        }

        for (String email : emails)
            result.add(new TwoStringsClass(email, getUserNamesByEmail(email)));

        return result;
    }

    /**
     * Method to get all the bill names of a group.
     *
     * @param groupName name of the group
     * @return ArrayList<String> bills ID
     */
    public ArrayList<String> getGroupBillNames(String groupName){

        ArrayList<String> result = new ArrayList<String>();

        try {

            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT name FROM bills WHERE gid=?";
            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, groupName);
            ResultSet resultSet = psmtm.executeQuery();

            while (resultSet.next())
                result.add(resultSet.getString(1));

            connect.close();

        } catch (SQLException e) {
            handleException(e);
        }

        return result;
    }

    /**
     * Method to get all the bill names and values of a group.
     *
     * @param groupName name of the group
     * @return ArrayList<String> bills ID
     */
    public ArrayList<TwoStringsClass> getGroupBillNamesValues(String groupName){

        ArrayList<TwoStringsClass> result = new ArrayList<>();

        try {

            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT name,value FROM bills WHERE gid=?";
            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, groupName);
            ResultSet resultSet = psmtm.executeQuery();

            while (resultSet.next())
                result.add(new TwoStringsClass(resultSet.getString(1), resultSet.getString(2)));

            connect.close();

        } catch (SQLException e) {
            handleException(e);
        }

        return result;
    }

    public float getUserParticipationInBill(String groupName, String billName, String userName) {
        float result = 0f;

        try {

            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT valueOwed,valuePaid FROM usersandbills WHERE bid=? AND uid=?";
            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, generateBillID(groupName, billName));
            psmtm.setString(2, userName);
            ResultSet resultSet = psmtm.executeQuery();

            while (resultSet.next()) {
                float owes = Float.parseFloat(resultSet.getString(1));
                float paid = Float.parseFloat(resultSet.getString(2));

                result = paid - owes;
            }

            connect.close();

        } catch (SQLException e) {
            handleException(e);
        }

        return result;
    }

    /** Method to create a group. It uses groupExists method
     * then creates the group or not. Adds the group on the groups table
     *
     * @param groupName name of a group
     * @return group created (true) or not (false)
     */
    public boolean createGroup(String groupName) {

        if (!groupExists(groupName)) {

            try {
                Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

                String query = "INSERT INTO groups VALUES (?)";

                PreparedStatement psmtm = connect.prepareStatement(query);
                psmtm.setString(1, groupName);
                psmtm.executeUpdate();

                connect.close();

                return true;

            } catch (SQLException e) {
                handleException(e);
            }
        }

        return false;
    }


    /**
     * Method to check if the group already exists on the db
     *
     * @param groupName name of the group
     * @return group exists (true) or not (false)
     */
    public boolean groupExists(String groupName) {
        boolean result = false;

        try {

            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT EXISTS(SELECT 1 FROM `groups` WHERE name=?)";

            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, groupName);
            ResultSet rs = psmtm.executeQuery();

            if (rs.next() && Integer.parseInt(rs.getString(1)) == 1) {
                result = true;
            }
            connect.close();

        } catch (SQLException e) {
            handleException(e);
        }

        return result;
    }

    /**
     * Method to add a user to a group
     * @param addedUserName user to be added
     * @param groupName name of the group
     * @param sessionUserName current username
     * @return true if the user was addded, false if not
     */
    public boolean addUserToGroup(String addedUserName, String groupName, String sessionUserName) {
        if (!isUserMember(addedUserName, groupName)) {
            try {
                Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

                String query = "INSERT INTO `usersAndGroups`(`uid`, `gid`) VALUES (?, ?)";

                PreparedStatement psmtm = connect.prepareStatement(query);
                psmtm.setString(1, addedUserName);
                psmtm.setString(2, groupName);
                psmtm.executeUpdate();

                connect.close();

                this.postNotification(new Notification(groupName, sessionUserName, Notification.USER_ADDED,addedUserName));
                return true;
            } catch (SQLException e) {
                handleException(e);
            }
        }
        return false;
    }

    /**
     * Method to check if a user is member of a group
     *
     * @param userName id of an user
     * @param groupName name of the group
     * @return true if it is or false if is not
     */
    public boolean isUserMember(String userName, String groupName) {
        boolean result = false;
        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);
            String query = "SELECT EXISTS(SELECT 1 FROM usersAndGroups WHERE uid=? and gid=?)";

            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, userName);
            psmtm.setString(2, groupName);
            ResultSet rs = psmtm.executeQuery();

            while (rs.next())
                if (Integer.parseInt(rs.getString(1)) == 1) {
                    result = true;
                }

            connect.close();
        } catch (SQLException e) {
            handleException(e);
        }
        return result;
    }

    /**
     * Method to create an user account.
     * Adds the username to users table on db
     *
     * @param userEmail id of an user
     * @param firstName user's first name
     * @param lastName user's last name
     * @param userPassword password of an user
     * @return if creates (true) if not (false)
     */
    public boolean createUserAccount(String userEmail, String firstName, String lastName, String userPassword) {
        if (!userExists(userEmail)) {
            try {
                Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

                String query = "INSERT INTO users(email,firstName,lastName,password) VALUES (?, ?, ?, ?)";

                PreparedStatement psmtm = connect.prepareStatement(query);
                psmtm.setString(1, userEmail);
                psmtm.setString(2, firstName);
                psmtm.setString(3, lastName);
                psmtm.setString(4, userPassword);
                psmtm.executeUpdate();

                connect.close();

                return true;
            } catch (SQLException e) {
                handleException(e);
            }
        }
        return false;
    }

    /**
     * Method to get the users from a group and their respective balance.
     *
     * @param groupName name of the group
     * @return ArrayList<TwoStringsClass> string uses floatValue balance
     */
    public ArrayList<TwoStringsClass> getGroupUsersBalances(String groupName){
        ArrayList<TwoStringsClass> result = new ArrayList<>();
        ArrayList<String> partialMembers = new ArrayList<>();
        String query;

        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            query = "SELECT uid,SUM(valuePaid)-SUM(valueOwed) FROM bills INNER JOIN groups AS billsAndGroups " +
                    "ON bills.gid = billsAndGroups.name AND bills.gid=? " +
                    "INNER JOIN usersAndBills ON usersAndBills.bid = bills.id GROUP BY usersAndBills.uid";

            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, groupName);
            ResultSet resultSet = psmtm.executeQuery();

            while(resultSet.next()){
                result.add(new TwoStringsClass(resultSet.getString(1), resultSet.getString(2)));
                partialMembers.add(resultSet.getString(1));
            }
            connect.close();
        }
        catch (SQLException e) {
            handleException(e);
        }

        ArrayList<String> members = this.getGroupMembers(groupName);

        for (String member : members) {
            if (!partialMembers.contains(member)) {
                result.add(new TwoStringsClass(member, "0.00"));
            }
        }

        return result;
    }

    /**
     * Method to check if a user already exists in db
     *
     * @param userName id of an user
     * @return true if exists, false if not
     */
    public boolean userExists(String userName) {
        boolean result = false;

        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT EXISTS(SELECT 1 FROM users WHERE email=?)";

            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, userName);
            ResultSet rs = psmtm.executeQuery();

            if (rs.next() && Integer.parseInt(rs.getString(1)) == 1) {
                result = true;
            }

            connect.close();
        } catch (SQLException e) {
            handleException(e);
        }
        return result;
    }

    /**
     * Method to check if a bill exists
     * @param groupName group of the bill
     * @param billName name of the bill
     * @return true if exists, false if not
     */
    public boolean billExists(String groupName, String billName) {
        boolean result = false;

        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT EXISTS(SELECT 1 FROM bills WHERE id=?)";

            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, generateBillID(groupName, billName));
            ResultSet rs = psmtm.executeQuery();

            if (rs.next() && Integer.parseInt(rs.getString(1)) == 1) {
                result = true;
            }

            connect.close();

        } catch (SQLException e) {
            handleException(e);
        }
        return result;
    }

    /**
     * Method to get a bill from db.
     * @param groupName group of the bill
     * @param billName name of the bill
     * @return the Bill
     */
    public Bill getBill(String groupName, String billName){

        Bill b = new Bill();
        b.billDate = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {

            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT `name`,`value`,`dateOccurred`,`latitude`,`longitude` FROM bills WHERE id=?";

            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, generateBillID(groupName, billName));
            ResultSet resultSet = psmtm.executeQuery();

            while (resultSet.next()) {
                b.billName = resultSet.getString(1);
                b.billValue = Float.parseFloat(resultSet.getString(2));
                try {
                    b.billDate.setTime(df.parse(resultSet.getString(3)));
                } catch (Exception e){
                    handleException(e);
                }
                b.billLocationLatitude = Double.parseDouble(resultSet.getString(4));
                b.billLocationLongitude = Double.parseDouble(resultSet.getString(5));
            }
            connect.close();

        } catch (SQLException e) {
            handleException(e);
        }

        return b;
    }

    // TODO
    /**
     * Method to get a picture of a bill
     * @param bill bill name
     * @return bitmap picture
     */
    /*
    public Bitmap getBillPicture(Bill bill){
        byte[] picbytes = null;

        try {
            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "SELECT picture FROM bills WHERE id = '"+bill.billName+"'";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                picbytes = resultSet.getBytes(1);
            }

            connect.close();

        } catch (SQLException e) {
            Log.e(TAG, "getBillPicture", e);
        }

        Bitmap bitmap = null;

        if (picbytes != null)
            bitmap = BitmapFactory.decodeByteArray(picbytes, 0, picbytes.length);

        return bitmap;
    }
    */

    /**
     * Public method to call create bill
     * @param bill bill to be created
     * @param sessionUserName current username
     */
    public boolean createBill(Bill bill, String sessionUserName){
        return this.createBill(bill,sessionUserName, true);
    }

    /**
     * Method to create a bill
     * @param bill bill name
     * @param sessionUserName current username
     * @param post to post a notification
     */
    private boolean createBill(Bill bill, String sessionUserName, boolean post){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "INSERT INTO `bills`(`id`, `name`, `value`, `dateOccurred`, `gid`, `latitude`,`longitude`) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, bill.groupName + bill.billName);
            psmtm.setString(2, bill.billName);
            psmtm.setFloat(3, bill.billValue);
            psmtm.setString(4, sdf.format(bill.billDate.getTime()));
            psmtm.setString(5, bill.groupName);
            psmtm.setDouble(6, bill.billLocationLatitude);
            psmtm.setDouble(7, bill.billLocationLongitude);
            psmtm.executeUpdate();

            connect.close();

            if (post) this.postNotification(
                    new Notification(bill.groupName, sessionUserName, Notification.BILL_CREATED, bill.billName));

            return true;
        } catch (SQLException e) {
            handleException(e);
        }

        return false;
    }

    /**
     * Method to add a picture to a bill
     * @param bill bill
     */
    /*
    public boolean addPictureToBill(final Bill bill){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bill.billPicture.compress(Bitmap.CompressFormat.JPEG, 5, stream);
        byte[] byteArray = stream.toByteArray();

        Log.d(TAG, "pic size " + ", array size " + byteArray.length);


        Log.d(TAG, "saving picture");
        try
        {
            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "UPDATE bills SET picture = ? WHERE '" + bill.billName + "' = bills.id";
            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setBytes(1, byteArray);
            psmtm.executeUpdate();

            connect.close();

            Log.d(TAG, "picture added");
        } catch (SQLException e)
        {
            Log.e(TAG, "error upload picture", e);
            //do something with exception
        }
    }
    */

    /**
     * Method to create a relation between an userName and a bill and its value.
     * @param userName userName to be created a relation
     * @param billName name of the bill
     *
     */
    public boolean createUserBillRelation(String userName, String groupName, String billName, Double valueOwn, Double valuePaid){

        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "INSERT INTO `usersAndBills`(`uid`, `bid`, `valueOwed`, `valuePaid`) VALUES (?, ?, ?, ?)";

            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, userName);
            psmtm.setString(2, generateBillID(groupName, billName));
            psmtm.setDouble(3, valueOwn);
            psmtm.setDouble(4, valuePaid);
            psmtm.executeUpdate();

            connect.close();

            return true;
        } catch (SQLException e) {
            handleException(e);
        }

        return false;
    }

    //TODO test
    /**
     * Edit a updatedBill
     * @param sessionUserName current username
     * @param updatedBill updatedBill to be edit
     */
    public boolean editBill(Bill updatedBill, String oldBillName, String sessionUserName){

        if (billExists(updatedBill.groupName, oldBillName)
                && !billExists(updatedBill.groupName, updatedBill.billName)) {
            System.out.println("In editBill");

            try {
                Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

                String query = "UPDATE bills " +
                        "SET id=?, name=?, value=?, latitude=?, longitude=? " +
                        "WHERE id=?";

                PreparedStatement psmtm = connect.prepareStatement(query);
                psmtm.setString(1, generateBillID(updatedBill.groupName, updatedBill.billName));
                psmtm.setString(2, updatedBill.billName);
                psmtm.setFloat(3, updatedBill.billValue);
                psmtm.setDouble(4, updatedBill.billLocationLatitude);
                psmtm.setDouble(5, updatedBill.billLocationLongitude);
                psmtm.setString(6, generateBillID(updatedBill.groupName, oldBillName));
                System.out.println(psmtm);
                psmtm.executeUpdate();

                System.out.println(this.postNotification(new Notification(updatedBill.groupName, sessionUserName, Notification.BILL_EDITED, updatedBill.billName)));

            } catch (SQLException e) {
                handleException(e);
            }

            return true;
        }
        else
            return false;
    }

    /**
     * Method to get who not paid the bill
     * @param groupName name of the bill's group
     * @param billName name of the bill
     * @return ArrayList<TwoStringsClass> paid bills
     */
    public ArrayList<TwoStringsClass> getWhoPaidBill(String groupName, String billName){

        ArrayList<TwoStringsClass> result = new ArrayList<>();

        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT uid,valuePaid FROM usersAndBills WHERE bid = ? AND valuePaid>0";
            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, generateBillID(groupName, billName));
            ResultSet resultSet = psmtm.executeQuery();

            while (resultSet.next()) {
                result.add( new TwoStringsClass(resultSet.getString(1),resultSet.getString(2)) );
            }

            connect.close();
        } catch (SQLException e) {
            handleException(e);
        }

        return result;
    }

    public String getUserNamesByEmail(String userEmail) {
        String result = "";

        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT firstName,lastName FROM users WHERE email=?";
            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, userEmail);
            ResultSet resultSet = psmtm.executeQuery();

            if (resultSet.next()) {
                result = resultSet.getString(1) + " " + resultSet.getString(2);
            }

            connect.close();
        } catch (SQLException e) {
            handleException(e);
        }

        return result;
    }

    /**
     * Method to get users that have not paid the bill
     * @param groupName name of the bill's group
     * @param billName name of the bill
     * @return ArrayList<TwoStringsClass> owns bill
     */
    public ArrayList<TwoStringsClass> getWhoOwesBill(String groupName, String billName){

        ArrayList<TwoStringsClass> result = new ArrayList<>();

        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT uid,valueOwed FROM usersAndBills WHERE bid = ? AND valueOwed>0";
            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, generateBillID(groupName, billName));
            ResultSet resultSet = psmtm.executeQuery();

            while (resultSet.next()) {
                result.add( new TwoStringsClass(resultSet.getString(1), resultSet.getString(2)) );
            }

            connect.close();
        } catch (SQLException e) {
            handleException(e);
        }

        return result;
    }

    /**
     * public call to the delete bill method
     * @param groupName name of the group
     * @param billName name of a bill
     * @param sessionUserName current username
     */
    public boolean deleteBill(String groupName, String billName, String sessionUserName){
        return deleteBill(groupName, billName, sessionUserName, true);
    }

    /**
     * Method to delete a bill
     * @param groupName name of the group
     * @param billName bill name
     * @param sessionUserName current username
     * @param post to post a notification
     */
    private boolean deleteBill(String groupName, String billName, String sessionUserName, boolean post){
        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "DELETE FROM usersAndBills WHERE bid=?";
            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, generateBillID(groupName, billName));
            psmtm.executeUpdate();

            query = "DELETE FROM bills WHERE id=?";
            psmtm = connect.prepareStatement(query);
            psmtm.setString(1, generateBillID(groupName, billName));
            psmtm.executeUpdate();

            connect.close();

            //TODO
            if (post) this.postNotification(
                    new Notification(groupName, sessionUserName, Notification.BILL_DELETED, billName));

            return true;
        } catch (SQLException e) {
            handleException(e);
        }

        return false;
    }

    /**
     * Method to get a group notification. Its ordered by time.
     *
     * @param groupName name of the group
     * @return ArrayList<Notification>
     */
    public ArrayList<Notification> getGroupNotifications(String groupName) {
        ArrayList<Notification> result = new ArrayList<>();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "SELECT * FROM groupNotifications WHERE gid=? ORDER BY `time` DESC";

            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setString(1, groupName);
            ResultSet resultSet = psmtm.executeQuery();


            while (resultSet.next()) {
                Notification n = new Notification(groupName, resultSet.getString(3),
                        (Integer.parseInt(resultSet.getString(4))), resultSet.getString(5));
                try {
                    n.date.setTime(df.parse(resultSet.getString(6)));
                } catch (Exception e) {
                    handleException(e);
                }
                result.add(n);
            }
            connect.close();

        } catch (SQLException e) {
            handleException(e);
        }
        return result;
    }

    /**
     * Method to post a notification on the db.
     *
     * @param notification object notification created
     * @return true if posted, false if not
     */
    public boolean postNotification(Notification notification) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            String query = "INSERT INTO groupNotifications(nid, gid, uid, type, details, time) VALUES " +
                    "(?, ?, ?, ?, ?, ?)";

            PreparedStatement psmtm = connect.prepareStatement(query);
            psmtm.setInt(1, notification.hashCode());
            psmtm.setString(2, notification.groupName);
            psmtm.setString(3, notification.owner);
            psmtm.setInt(4, notification.type);
            psmtm.setString(5, notification.description);
            psmtm.setString(6, sdf.format(notification.date.getTime()));
            psmtm.executeUpdate();

            connect.close();
            return true;

        } catch (SQLException e) {
            handleException(e);
        }
        return false;
    }
}