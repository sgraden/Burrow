var express = require('express'),
	path  = require('path'),
	sqlFile = require("./public/js/sql.js"),
	sql = require("sqlite3").verbose(),
	app = express();

// Uses the favicon
//app.use(express.favicon(path.join(__dirname, 'public/images/favicon.ico'))); 
// set the port I <3 boobs
app.set('port', process.env.PORT || 8000);
// share the love, make public available
app.use(express.static(path.join(__dirname, 'public')));
// Read the body of the page
app.use(express.bodyParser());

// get the homepage using basic html
app.get('/', function(req, res) {
	res.sendfile('public/views/index.html', {title:'Burrow'});
});
var ssid;
// register a user
app.post('/user/register', function(req, res) {
	var userInfo = req.body.userInfo;
	var houseInfo = req.body.houseInfo;
	console.log(userInfo);
	console.log(houseInfo);
	var completedFunction = function(homeId) {
		sqlFile.checkDatabase('user');
		sqlFile.checkDatabase('home');

		res.send({
			"success":true,
			"homeid":homeId,
			"ssid":ssid
		});
	}
	console.log("admin level: " + userInfo.isAdmin);
	if (userInfo.isAdmin == "true") {
		console.log('is admin');
		ssid = houseInfo.ssid;
		// The admin will register the home
		sqlFile.registerHome(houseInfo.homeName, houseInfo.ssid, userInfo.deviceId, function(err, homeId) {
			console.log("in callbac j " + homeId);
			// After we establish the home, lets add the user
			sqlFile.insertUser(userInfo.userName, userInfo.password, userInfo.firstName, 
				userInfo.lastName, "hello@blah", userInfo.phoneNumber, userInfo.deviceId, 
				homeId, 1, completedFunction);
		});	
	} else {
		console.log('not admin');
		sqlFile.findHouseId(houseInfo.homeName, function(result) {
			console.log("in callbac " + result.homeId);
			ssid = result.ssid;
			var isConnected = 0;
			if (houseInfo.ssid == result.ssid) {
				isConnected = 1;
			}
			sqlFile.insertUser(userInfo.userName, userInfo.password, userInfo.firstName, 
				userInfo.lastName, "hello@blah", userInfo.phoneNumber, userInfo.deviceId, 
				result.homeId, isConnected, completedFunction);
		});
	}
	// sqlFile.insertUser(userInfo.userName, userInfo.password, userInfo.firstName, 
		// userInfo.lastName, "hello@blah", userInfo.phoneNumber, userInfo.deviceId);
});

app.post('/users', function(req, res) {
	var homeId = req.body.homeId;
	sqlFile.getUsers(homeId, function(data) {
		res.send({
			"success":true,
			"userInfo":data
		});
	});
});

app.post('/user/update', function(req, res) {
	var body = req.body;
	console.log(body);
	var isConnectedToHome = 1;
	if (body.isConnectedToHome == "disconnected") {
		isConnectedToHome = 0;
	}
	sqlFile.updateUser(isConnectedToHome, body.deviceId, function() {
		res.send({
			"success":body.isConnectedToHome
		});
	});
});


// start the server
app.listen(/*app.get('port')*/8008, function () {
	console.log('Sean does not love boobies. Running on port: ' + app.get('port'));
});