package com.almasosorio.testing;

import java.util.Calendar;

public class Main {
	private static final String URL_BASE = "http://www.final-proj.com/";

	
	public static void main(String[] args) {
		
		DBHandler db = new DBHandler();

		/*
		System.out.println(db.checkLogin("user1", "1234"));
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

		System.out.println(db.addUserToGroup("user2", "group2", null));
		System.out.println(db.isUserMember("user2", "group2"));

		System.out.println("----/3/----");

		System.out.println(db.userExists("user1"));
		System.out.println(db.userExists("user3"));

		System.out.println(db.createUserAccount("user1", "2222"));
		System.out.println(db.createUserAccount("user3", "1234"));

		System.out.println(db.billExists("group1", "bill1"));
		System.out.println(db.billExists("group1", "bill2"));

*/
		/*

		System.out.println("----/4/----");

		System.out.println(db.getBill("group1", "bill1"));
		Bill b = new Bill();
		b.billName = "bill2";
		b.billValue = 10f;
		b.billDate = Calendar.getInstance();
		b.groupName = "group1";
		db.createBill(b, "null");

		System.out.println(db.getBill("group1", "bill2"));
*/
		//System.out.println(db.createUserBillRelation("user1", "group1", "bill2", 10d, 0d));
	}
}
