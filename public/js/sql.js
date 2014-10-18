var async = require('async');
var sql = require('sqlite3').verbose();
var db = new sql.Database(':memory:');

db.serialize(function() {
  	db.run(
  		"CREATE TABLE user (" + 
			"id INTEGER PRIMARY KEY NOT NULL," +
			"homeid INTEGER NOT NULL," +
			"username varchar(60) NOT NULL," +
			"password varchar(10) NOT NULL," +
			"fname varchar(60) NOT NULL," +
			"lname varchar(60) NOT NULL," +
			"email varchar(60) NOT NULL," +
			"phone varchar(60) NOT NULL," +
			"deviceid varchar(60) NOT NULL," + 
			"isConnected INTEGER NOT NULL);");			// This will be our primary key?

  	db.run(
  		"CREATE TABLE home (" +
			"id INTEGER PRIMARY KEY NOT NULL," +
			"name varchar(60) NOT NULL," +
			//"mac varchar(100) NULL," +
			"ssid varchar(60) NULL," +
			"adminid INTEGER NOT NULL);");
});

var insertUser = function (username, pass, fname, lname, email, phone, deviceid, homeid, isConnected, cb) {
	console.log('Inserting user ' + username + " with home ID of " + homeid + "...");
	var insert = db.prepare(
		"INSERT INTO user (homeid, username, password, fname, lname, email, phone, deviceid, isConnected)" +
		"VALUES (?,?,?,?,?,?,?,?,?);"
	);
	insert.run(homeid, username, pass, fname, lname, email, phone, deviceid, isConnected);
	console.log("User inserted");
	cb(homeid);
};

var registerHome = function (name, ssid, deviceid, cb) {
	console.log("Registering home " + name + " with SSID " + ssid + "...");
	var insert = db.prepare(
		"INSERT INTO home (name, ssid, adminid)" +
		"VALUES (?, ?, ?);"
	);
	//console.log(deviceid);
	insert.run(name, ssid, deviceid);
	db.each("SELECT * FROM home WHERE adminid like '" + deviceid + "' LIMIT 1", function(err, row) {
		var homeid = row.id;
		console.log("House " + name + " registered with ID of " + homeid);
		
		cb(err, homeid);
	});
};

var findHouseId = function(houseName, cb) {
	console.log("Finding ID of " + houseName + "...");
	db.each("SELECT * FROM home WHERE name like '" +  houseName + "' LIMIT 1", function(err, row) {
		console.log("House " + houseName + " ID is " + row.id + " with SSID of " + row.ssid);
		cb({"homeId":row.id, "ssid":row.ssid});
	});
};

var getUsers = function(homeid, cb) {
	console.log("Finding users with home ID of " + homeid + "...");
	db.all("SELECT * FROM user WHERE homeid = " + parseInt(homeid), function(err, rows) {
		// console.log(rows);
		var userInfo = [];
		var j = 0;
		for (var i = 0; i < rows.length; i++) {
			var user = rows[i];
			// console.log(user);
			if (user.isConnected == 1) { 
				console.log(user.username + " is connected");
				userInfo[j++] = {
					"userName": user.username,
					"firstName": user.fname,
					"lastName": user.lname
				};
			} else {
				console.log(user.username + " is not connected");
			}
		} 
		//console.log(userInfo);
		cb(userInfo);	
	});
};

var updateUser = function(isConnectedToHome, deviceId, cb) {
	console.log("user on " + deviceId + " is now " + (isConnectedToHome == 1 ? "connected" : "disconnected"));
	db.all("UPDATE user SET isConnected = " + isConnectedToHome + " WHERE deviceid = " + deviceId, function(err, rows) {
		cb();
	});
};

var checkDatabase = function(table, username) {
	var specify = "";
	if (username) {
		specify = " WHERE username like '" + username + "'";
	}
	db.each("SELECT * FROM " + table + specify, function(err, row) {
		console.log("Row in " + table + ": " + row.id);
	});
};

exports.insertUser = insertUser;
exports.findHouseId = findHouseId;
exports.checkDatabase = checkDatabase;
exports.registerHome = registerHome;
exports.getUsers = getUsers;
exports.updateUser = updateUser;
// exports.connectHome = connectHome;
// exports.usersInHome = usersInHome;
// exports.actuallygetUsers = actuallygetUsers;