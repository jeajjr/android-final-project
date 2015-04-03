package com.almasosorio.testing;

import java.util.Calendar;

public class Main {
	private static final String URL_BASE = "http://www.final-proj.com/";

	
	public static void main(String[] args) {
		
		DBHandler db = new DBHandler();

		//createUserBillRelation
/*
		System.out.println(db.checkLogin("user1", "1234"));
		System.out.println(db.checkLogin("user1", "1221"));
		System.out.println(db.checkLogin("user3", "1234"));
		System.out.println(db.getUserGroups("user1"));
		System.out.println(db.getGroupMembers("group1"));

		System.out.println("----/1/----");

		System.out.println(db.groupExists("group1"));
		System.out.println(db.groupExists("group2"));

		System.out.println(db.createGroup("group2"));
		System.out.println(db.groupExists("group2"));


		System.out.println("----/2/----");

		System.out.println(db.isUserMember("user1", "group1"));
		System.out.println(db.isUserMember("user1", "group2"));

		System.out.println(db.addUserToGroup("user1", "group2", "user1"));
		System.out.println(db.isUserMember("user1", "group2"));


		System.out.println("----/3/----");

		System.out.println(db.userExists("user1"));
		System.out.println(db.userExists("user3"));

		System.out.println(db.createUserAccount("user1", "2222"));
		System.out.println(db.createUserAccount("user3", "1234"));

		System.out.println(db.billExists("group1", "bill1"));
		System.out.println(db.billExists("group1", "bill2"));

		System.out.println("----/4/----");

		System.out.println(db.getBill("group1", "bill1"));
		Bill b = new Bill();
		b.billName = "bill2";
		b.billValue = 10f;
		b.billDate = Calendar.getInstance();
		b.groupName = "group1";
		System.out.println(db.createBill(b, "null"));

		System.out.println(db.getBill("group1", "bill2"));

		System.out.println(db.createUserBillRelation("user1", "group1", "bill2", 10d, 0d));
		System.out.println(db.createUserBillRelation("user2", "group1", "bill2", 0d, 10d));

		System.out.println(db.getWhoPaidBill("group1", "bill1"));
		System.out.println(db.getWhoOwesBill("group1", "bill1"));

		System.out.println(db.deleteBill("group1", "bill2", "user1"));

		//Notification n = new Notification("group1", "user1", Notification.BILL_CREATED, "flango");
		//System.out.println(db.postNotification(n));


		System.out.println(db.getGroupNotifications("group1"));
*/
		/*
		db.deleteBill("group1", "bill2", "user1");
		db.deleteBill("group1", "bill1a", "user1");

		System.out.println(db.getGroupBillNames("group1"));

		Bill b2 = new Bill();
		b2.billName = "bill2";
		b2.billValue = 50f;
		b2.billDate = Calendar.getInstance();
		b2.groupName = "group1";
		System.out.println("create: " + db.createBill(b2, "user1"));

		System.out.println(db.getGroupBillNames("group1"));

		b2.billName = "bill1a";
		b2.billValue = 55f;
		System.out.println("edit: " + db.editBill(b2, "bill2", "user2"));

		System.out.println(db.getGroupBillNames("group1"));
*/
		//System.out.println(db.getGroupUsersBalances("group1"));
/*
		System.out.println(db.getGroupBillNames("group1"));
		System.out.println(db.deleteBill("group1", "bill2", "user1"));
		System.out.println(db.getGroupBillNames("group1"));
*/
		//db.testing();
	}
}
