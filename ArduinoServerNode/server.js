var express = require('express');
var app = express();
var http = require('http').Server(app);
var mysql = require('mysql');
var request = require('request');
var path = require('path');
var bodyParser = require('body-parser')
var fs = require('fs');
var x, y;
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));

app.configure(function(){
	app.use(express.static(path.join(__dirname, 'public')));
	app.use(express.bodyParser());
});

var server_port = process.env.OPENSHIFT_NODEJS_PORT || 8080;
var server_ip_address = process.env.OPENSHIFT_NODEJS_IP || '0.0.0.0';

app.get('/', function (req, res) {
	res.sendfile("./public/html/index.html");
});

app.get('/sendData', function(req, res){
	x = req.query.x;
	y = req.query.y;
	res.send("Response");
});

app.get('/getData', function(req, res){
	res.json({x: x, y: y});
});

http.listen(server_port, server_ip_address, function () {
	console.log("Listening on " + server_ip_address + ", server_port " + server_port);
});