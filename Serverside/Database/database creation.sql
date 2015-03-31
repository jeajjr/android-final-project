DROP TABLE IF EXISTS usersAndGroups;
DROP TABLE IF EXISTS usersAndBills;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS groupNotifications;

CREATE TABLE groups (
	name varchar(50) NOT NULL,
	PRIMARY KEY (name)
);

CREATE TABLE users (
	email varchar(50) NOT NULL,
	password varchar(50) NOT NULL,
	PRIMARY KEY (email)
);

CREATE TABLE bills (
	id varchar(50) NOT NULL,
	value float NOT NULL,
	dateOcurred date DEFAULT NULL,
	gid varchar(50) NOT NULL,
	latitude float,
	longitude float,
	picture varbinary(1024) DEFAULT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (gid) REFERENCES groups(name) ON UPDATE CASCADE
);

CREATE TABLE usersAndGroups (
	uid varchar(50) NOT NULL,
	gid varchar(50) NOT NULL,
	PRIMARY KEY (uid, gid),
	FOREIGN KEY (uid) REFERENCES users(email) ON UPDATE CASCADE,
	FOREIGN KEY (gid) REFERENCES groups(name) ON UPDATE CASCADE
);

CREATE TABLE usersAndBills (
	uid varchar(50) NOT NULL,
	bid varchar(50) NOT NULL,
	valueOwed FLOAT NOT NULL,
	valuePaid FLOAT NOT NULL,
	PRIMARY KEY (uid, bid),
	FOREIGN KEY (uid) REFERENCES users(email) ON UPDATE CASCADE,
	FOREIGN KEY (bid) REFERENCES bills(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE groupNotifications (
	nid int NOT NULL,
	gid varchar(50) NOT NULL,
	uid varchar(50) NOT NULL,
	type int NOT NULL,
	details varchar(50) NOT NULL, -- details will hold aditional information about the notification's action.
								  -- For example, if the notification is user1 created the bill bill1, details
								  -- will contain the name bill1 and uid will contain the name user1.
	time timestamp,
	PRIMARY KEY (notificationid, gid),
	FOREIGN KEY (gid) REFERENCES groups(name) ON UPDATE CASCADE
);