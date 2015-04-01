package com.almasosorio.testing;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DBHandler {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String HOST = "jdbc:mysql://localhost/finalproj";
    private static final String DB_USER = "root";
    private static final String DB_PW = "sendubuntu";

    public DBHandler() {

        try {
            Class.forName(JDBC_DRIVER).newInstance();

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
        }
    }

    /**
     * Method for check the user credentials on the database.
     * If there are no match, returns false.
     * If matches, return true.
     *
     * @param userEmail email of an user
     * @param password password of the user
     * @return login valid (true) or not (false)
     * @throws SQLException connection not made
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
            System.out.println("Exception: " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
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
            System.out.println("Exception: " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
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
            System.out.println("Exception: " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
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
                System.out.println("Exception: " + e.getMessage());
                for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
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
            System.out.println("Exception: " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
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
                //TODO
                //this.postNotification(new Notification(sessionUserName, Notification.USER_ADDED,addedUserName), groupName);
                return true;
            } catch (SQLException e) {
                System.out.println("Exception: " + e.getMessage());
                for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
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
            System.out.println("Exception: " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
        }
        return result;
    }

    /**
     * Method to create an user account.
     * Adds the username to users table on db
     *
     * @param userName id of an user
     * @param userPassword password of an user
     * @return if creates (true) if not (false)
     */
    public boolean createUserAccount(String userName, String userPassword) {
        if (!userExists(userName)) {
            try {
                Connection connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

                String query = "INSERT INTO `users`(`email`,`password`) VALUES (?, ?)";

                PreparedStatement psmtm = connect.prepareStatement(query);
                psmtm.setString(1, userName);
                psmtm.setString(2, userPassword);
                psmtm.executeUpdate();

                connect.close();

                return true;
            } catch (SQLException e) {
                System.out.println("Exception: " + e.getMessage());
                for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
            }
        }
        return false;
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
            System.out.println("Exception: " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
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
            psmtm.setString(1, groupName + billName);
            ResultSet rs = psmtm.executeQuery();

            if (rs.next() && Integer.parseInt(rs.getString(1)) == 1) {
                result = true;
            }

            connect.close();

        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
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
            psmtm.setString(1, groupName + billName);
            ResultSet resultSet = psmtm.executeQuery();

            while (resultSet.next()) {
                b.billName = resultSet.getString(1);
                b.billValue = Float.parseFloat(resultSet.getString(2));
                try {
                    b.billDate.setTime(df.parse(resultSet.getString(3)));
                } catch (Exception e){
                    System.out.println("Exception: " + e.getMessage());
                    for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
                }
                b.billLocationLatitude = Double.parseDouble(resultSet.getString(4));
                b.billLocationLongitude = Double.parseDouble(resultSet.getString(5));
            }
            connect.close();

        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
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
            //TODO

            //if (post) {this.postNotification(new Notification(sessionUserName,Notification.BILL_CREATED,bill.billName), bill.groupName);}

            return true;
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
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
            psmtm.setString(2, groupName + billName);
            psmtm.setDouble(3, valueOwn);
            psmtm.setDouble(4, valuePaid);
            psmtm.executeUpdate();

            connect.close();

            return true;
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) System.out.println(ste);
        }

        return false;
    }

    //TODO test
    /**
     * Edit a bill
     * @param sessionUserName current username
     * @param bill bill to be edit
     */
    public void editBill(Bill bill, String oldBillName, String sessionUserName){

        // TODO
        //this.deleteBill(oldBillName, sessionUserName,bill.groupName, false);
        this.createBill(bill, sessionUserName, false);
        //this.postNotification(new Notification(sessionUserName, Notification.BILL_EDITED, bill.billName), bill.groupName);
    }
}