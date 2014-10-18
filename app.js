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

// register a user
app.post('/user/register', function(req, res) {
	var userInfo = req.body.userInfo;
	console.log(userInfo);
	sqlFile.insertUser(userInfo.userName, userInfo.password, userInfo.firstName, 
		userInfo.lastName, "hello@blah", userInfo.phoneNumber, userInfo.deviceId);
	sqlFile.getUser();

	res.send({
		"success":true
	});
});

//register a home
app.post('/home/register', function(req, res) {
	var body = req.body;
	console.log(body);
	

	res.send({
		"success":true
	});
});

//connect to a home
app.post('/home/connect', function(req, res) {
	var homeInfo = req.body;
	console.log(body);

	res.send({
		"success":true
	});
});

// start the server
app.listen(/*app.get('port')*/8008, function () {
	console.log('Sean does not love boobies. Running on port: ' + app.get('port'));
});