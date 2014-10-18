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

// get the post request and run query
app.post('/data', function(req, res) {
	console.log(req.body);
	res.send({
		"success":true
	});
	// Get the fields here
	// restaurant
	// var data = req.body;
	// var restaurant = data.resturant;
	// var filters = {item_type: 1};
	// console.log(data);
	// for (var i in stuff) {
	// 	var field = stuff[i];
	// 	console.log(field);
	// 	if (data[field] && data[field].to) {
	// 		var amount = data[field];

	// 		filters[field] = {
	// 			from: parseInt(amount.from),
	// 			to: parseInt(amount.to)
	// 		};
	// 	}
	// }
	// console.log(filters);

	// var poo = ['item_name','brand_name','item_description','nf_calories','nf_total_fat','nf_cholesterol', 'nf_sugars', 'nf_sodium','nf_total_carbohydrate','nf_dietary_fiber','nf_protein'];

	// ntr.v1_1.search.advanced({
 //    	fields: poo,
 //    	query: restaurant,
 //    	limit: 20,
 //    	offset: 0,
	//     filters: filters
	// }, function (err, results) {
	//     if (err) console.log(err);
	//     if (results.total) {
	//     	console.log(results);
	    	
	//     	var potential = results.hits.filter(function(elem) { return elem._score > 1});
	//     	console.log(potential);

	//     	res.send(potential);
	//     } else {
	//     	console.log('No results...');
	//     }
	// });
});

// start the server
app.listen(/*app.get('port')*/8008, function () {
	console.log('Sean does not love boobies. Running on port: ' + app.get('port'));
});