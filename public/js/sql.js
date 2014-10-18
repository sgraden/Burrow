var sql = require('sqlite3').verbose();
var db = new sql.Database(':memory:');

db.serialize(function() {
  	db.run(
  		"CREATE TABLE user (" + 
			"id INTEGER PRIMARY KEY NOT NULL," +
			"username varchar(60) NOT NULL," +
			"password varchar(10) NOT NULL," +
			"fname varchar(60) NOT NULL," +
			"lname varchar(60) NOT NULL," +
			"email varchar(60) NOT NULL," +
			"phone varchar(60) NOT NULL," +
			"deviceid varchar(60) NOT NULL);" +
	  	"CREATE TABLE house (" +
			"id INTEGER PRIMARY KEY NOT NULL," +
			"name varchar(60) NOT NULL," +
			"geo varchar(100) NULL," +
			"ssid varchar(60) NULL," +
			"adminid INTEGER NOT NULL);" +
		"CREATE TABLE user_house (" +
			"userid INTEGER NOT NULL," +
			"houseid INTEGER NOT NULL);"
  );

  	/*db.run(
		"INSERT INTEGERO user (id, username, password, fname, lname, email, phone, deviceid)" +
		"VALUES (1, 'jimmy', 'a', 'steven', 'raden', '@.com', '123', '23423402034');"
	);*/

  	/*db.each("SELECT * FROM user", function(err, row) {
    	console.log(row.id + ": " + row.username);
	});*/
});

var insertUser = function (username, pass, fname, lname, email, phone, deviceid) {
	var insert = db.prepare(
		"INSERT INTO user (username, password, fname, lname, email, phone, deviceid)" +
		"VALUES (?,?,?,?,?,?,?);"
	);
	insert.run(username, pass, fname, lname, email, phone, deviceid);
};

var getUser = function(username) {
	var specify = "";
	if (username) {
		specify = " WHERE username like '" + username + "'";
	}
	db.each("SELECT * FROM user" + specify, function(err, row) {
		console.log(row.id + ": " + row.username);
	});
};

exports.insertUser = insertUser;
exports.getUser = getUser;