DROP TABLE IF EXISTS usersAndGroups;
DROP TABLE IF EXISTS usersAndBills;
DROP TABLE IF EXISTS groupNotifications;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS users;

CREATE TABLE groups (
	name varchar(50) NOT NULL,
	PRIMARY KEY (name)
);

CREATE TABLE users (
	email varchar(50) NOT NULL,
	firstName varchar(50) NOT NULL,
	lastName varchar(50) NOT NULL,
	password varchar(50) NOT NULL,
	PRIMARY KEY (email)
);

CREATE TABLE bills (
	-- bill id is the concatenation between group name and bill name
	id varchar(100) NOT NULL,
	name varchar(50) NOT NULL,
	value float NOT NULL,
	dateOccurred datetime DEFAULT NULL,
	gid varchar(50) NOT NULL,
	latitude float,
	longitude float,
	picture varbinary(2048) DEFAULT NULL,
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
	time datetime,
	PRIMARY KEY (nid, gid),
	FOREIGN KEY (gid) REFERENCES groups(name) ON UPDATE CASCADE
);

-- email, password
INSERT INTO users VALUES ('user1', 'Molson', 'Lucius', '1234');
INSERT INTO users VALUES ('user2', 'Bob', 'Dilan', '1234');

-- name
INSERT INTO groups VALUES ('group1');

--	id, name, value, dateOcurred, gid, latitute, longitude, picture
INSERT INTO bills VALUES ('group1bill1', 'bill1', 50.00, '2015-04-01 02:08','group1', '0', '0', '');

-- uid, gid
INSERT INTO usersAndGroups VALUES ('user1', 'group1');
INSERT INTO usersAndGroups VALUES ('user2', 'group1');

--	uid, bid, valueOwed, valuePaid
INSERT INTO usersAndBills VALUES ('user1','group1bill1', 0, 50.00);
INSERT INTO usersAndBills VALUES ('user2','group1bill1', 50.00, 0);

-- nid, gid, uid, type, details, time
INSERT INTO groupNotifications VALUES ('1', 'group1', 'user1', '1', 'bill1', '2015-04-01 12:00:00');
