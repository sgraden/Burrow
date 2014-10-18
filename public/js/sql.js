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

  	/*db.run(
		"INSERT INTEGERO user (id, username, password, fname, lname, email, phone, deviceid)" +
		"VALUES (1, 'jimmy', 'a', 'steven', 'raden', '@.com', '123', '23423402034');"
	);*/

  	/*db.each("SELECT * FROM user", function(err, row) {
    	console.log(row.id + ": " + row.username);
	});*/
});

var insertUser = function (username, pass, fname, lname, email, phone, deviceid, homeid, isConnected, cb) {
	console.log('inserting user - new users homeid: ' + homeid);
	var insert = db.prepare(
		"INSERT INTO user (homeid, username, password, fname, lname, email, phone, deviceid, isConnected)" +
		"VALUES (?,?,?,?,?,?,?,?,?);"
	);
	insert.run(homeid, username, pass, fname, lname, email, phone, deviceid, isConnected);
	cb(homeid);
};

var registerHome = function (name, ssid, deviceid, cb) {
	console.log('registering home');
	var insert = db.prepare(
		"INSERT INTO home (name, ssid, adminid)" +
		"VALUES (?, ?, ?);"
	);
	insert.run(name, ssid, deviceid);
	db.each("SELECT * FROM home WHERE adminid like '" + deviceid + "' LIMIT 1", function(err, row) {
		var homeid = row.id;
		console.log("registerHome homeId: " + homeid);
		
		cb(err, homeid);
	});
};

var findHouseId = function(houseName, cb) {
	//checkDatabase('user');
	console.log("finding house housename: " + houseName);
	db.each("SELECT * FROM home WHERE name like '" +  houseName + "' LIMIT 1", function(err, row) {
		console.log("house id " + row.id);
		cb({"homeId":row.id, "ssid":row.ssid});
	});
};

var getUsers = function(homeid, cb) {
	console.log("users homeid: " + homeid);
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
	console.log("user on " + deviceId + " ");
	db.all("UPDATE user SET isConnected = " + isConnectedToHome + " WHERE deviceid = " + deviceId, function(err, rows) {
		cb();
	});
};

// //Connects user to a home
// var connectHome = function (deviceid, homename) {
// 	var userid = -1;
// 	var homeid = -1;
// 	async.series([
// 		function(cb) {
// 			//user id
// 			db.each("SELECT * FROM user WHERE deviceid like '" + deviceid + "' LIMIT 1", function(err, row) {
// 				userid = row.id;
// 				console.log("inloop user:" + userid);
// 				db.each("SELECT * FROM home WHERE name like '" + homename + "' LIMIT 1", function(err, row) {
// 					homeid = row.id;
// 					console.log("inloop: " + homeid);
// 					cb(null, [userid, homeid]);
// 				});
// 			});
// 		}
// 	],
// 		function(err, results) {
// 			//insert into user_home
// 			console.log(results);
// 			var insert = db.prepare(
// 				"INSERT INTO user_home (userid, homeid)" +
// 				"VALUES (?, ?);"
// 			);
// 			console.log("userid: " + results[0][0]);
// 			console.log("homeid: " + results[0][1]);
// 			insert.run(results[0][0], results[0][1]);
// 		});
// };

// var usersInHome = function (homename, lastResort) {
// 	//find homeid
// 	var homeid = -1;
// 	var users = [];
// 	var usersFinal = []
// 	async.series([
// 		function(cb) {
// 			db.all("SELECT * FROM home WHERE name like '" + homename + "' LIMIT 1", function(err, rows) {
// 				console.log("first q");
// 				console.log(rows);
// 				homeid = rows[0].id;
// 				console.log("HI " + homeid);
// 				db.all("SELECT * FROM user_home WHERE homeid like '" + parseInt(homeid) + "'", function(err, rows) {
// 					users = rows;
// 					console.log("U");
// 					console.log(users);
// 					cb(null, users);
// 				});
// 			});
// 		}],
// 		function(cb, results) {
// 			//find user info
// 			console.log("users ");
// 			console.log(results[0]);
// 			// var usersFinal = [];
// 			// results[0].forEach(function(item) {
// 			// 	console.log("Item");
// 			// 	console.log(item);
// 			// 	checkDatabase("user");
// 			// 	db.all("SELECT * FROM user", function(err, row) { //WHERE id like '" + parseInt(item.userid) + "' LIMIT 1", function(err, row) {
// 			// 		console.log("row");
// 			// 		console.log(row);
// 			// 		usersFinal.push({
// 			// 			"firstName" : row.fname,
// 			// 			"lastName" : row.lname,
// 			// 			"userName" : row.username
// 			// 		});
// 			// 	});
				
// 			// });
		
// 			// console.log("DONE");
// 			// console.log(usersFinal)
// 			// //res.send({
// 			// 	"success":true,
// 			// 	"users": usersFinal
// 			// });
// 			// return results[0];
// 			lastResort(results[0]);
// 		}
// 	);
// 	// console.log(users);
// /*	db.each("SELECT * FROM home LIMIT 1", function(row, err) {  //WHERE name like '" + homename + "' LIMIT 1", function(err, row) {
// 		// homeid = row.homeid;
// 		console.log(row);
// 	});

// 	//find usersid that are joined with home
// 	var usersId = [];
// 	db.each("SELECT * FROM user_home WHERE homeid = " + homeid, function(err, row) {
// 		usersId.push(row.userid);
// 		console.log("usersinhome row: " + row);
// 	});

// 	console.log("usersinhome user ID: " + usersId);

// 	//find user info
// 	var users = [];
// 	usersId.forEach(function(id) {
// 		db.each("SELECT * FROM user WHERE id = " + id + "LIMIT 1", function(err, row) {
// 			users.push(row.username);
// 		});
// 	});
// 	console.log("usersinhome users name: " + users);*/
// 	// return [];
// };

// var actuallygetUsers = function(users, cb) {
// 	var results = [];
// 	for (var i = 0; i < users.length; i++) {
// 		var user = users[i];
// 		console.log(user);
// 		db.each("SELECT * FROM user WHERE id = " + user.userid, function(err, row) {
// 			console.log("R " + row.username);
// 			results[i] = {
// 				"userName" : row.username,
// 				"firstName" : row.fname,
// 				"lastName" : row.lname
// 			};
// 		});
// 	}
// };

var checkDatabase = function(table, username) {
	var specify = "";
	if (username) {
		specify = " WHERE username like '" + username + "'";
	}
	db.each("SELECT * FROM " + table + specify, function(err, row) {
		console.log("INSERTED " + table + ": " + row.id);
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