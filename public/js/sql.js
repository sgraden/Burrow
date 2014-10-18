var sql = require('sqlite3').verbose();
var db = new sql.Database(':memory:');

db.serialize(function() {
  db.run("CREATE TABLE lorem (info TEXT)");

  db.run("
  	CREATE TABLE user (
		id int NOT NULL AUTO_INCREMENT,
		username varchar(60) NOT NULL,
		fname varchar(60) NOT NULL,
		lname varchar(60) NOT NULL,
		email varchar(60) NOT NULL,
		phone varchar(60) NOT NULL,
		deviceid varchar(60) NOT NULL,
		PRIMARY KEY (id)
	);
  ");

});

db.close();

/*var user = sql.define ({
	name: 'user',
	columns: ['userid', 'fname', 'lname', 'email']
});

var house = sql.define({
	name: 'house',
	columns: ['houseid', 'adminuserid','housename', 'geo', 'ssid']
});

var user_house = sql.define({
	name: 'user_house',
	columns: ['userid', 'houseid']
});

var pass = sql.define({
	name: 'pass',
	columns: ['userid', 'pass']
});

var insertUser = function () {};

var insert = user.insert()

var query = user.select(user.star()).from(user).toQuery();
console.log(query.values);

exports.insertUser = insertUser;*/