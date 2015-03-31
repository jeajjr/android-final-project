-- email, password
INSERT INTO users VALUES ('user1', '1234');
INSERT INTO users VALUES ('user2', '1234');

-- name
INSERT INTO groups VALUES ('group1');

--	id, value, dateOcurred, gid, latitute, longitude, picture
INSERT INTO bills VALUES ('bgroup11', 50.00, '','group1', '', '', '');

-- uid, gid
INSERT INTO usersAndGroups VALUES ('user1', 'group1');
INSERT INTO usersAndGroups VALUES ('user2', 'group1');

--	uid, bid, valueOwed, valuePaid
INSERT INTO usersAndBills VALUES ('user1','bgroup11', 0, 50.00);
INSERT INTO usersAndBills VALUES ('user2','bgroup11', 50.00, 0);

-- notificationid, gid, uid, type, details, time
INSERT INTO groupNotifications VALUES ('1', 'group1', 'user1', '',
'user1 created time', '');
