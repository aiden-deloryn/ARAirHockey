var canvas = document.getElementById("canvas");
var ctx = canvas.getContext("2d");

canvas.width = 800;
canvas.height = 600;

var player = {
	x: canvas.width/2-50,
	y: canvas.height/2-50,
	width: 100,
	height: 100
};


setInterval(function(){ 
	$.get("/getData", function(data){
		var dataX = parseFloat(data.x);
		var dataY = parseFloat(data.y);
		player.x += dataX * 10;
		player.y += dataY * 10;
	});
	draw();
}, 1);

function draw(){
	ctx.clearRect(0, 0, canvas.width, canvas.height);
	ctx.fillStyle = 'red';
	ctx.fillRect(player.x, player.y, player.width, player.height);
}

