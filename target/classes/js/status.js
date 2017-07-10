var d = 0;
function timeSince(date) {
	if(date == 0)
		return "never.";

	var seconds = Math.floor((new Date() - date) / 1000);
	var interval = Math.floor(seconds / 31536000);

	if (interval > 1)
		return interval + " years ago.";
	interval = Math.floor(seconds / 2592000);
	if (interval > 1)
		return interval + " months ago.";
	interval = Math.floor(seconds / 86400);
	if (interval > 1)
		return interval + " days ago.";
	interval = Math.floor(seconds / 3600);
	if (interval > 1)
		return interval + " hours ago.";
	interval = Math.floor(seconds / 60);
	if (interval > 1)
		return interval + " minutes ago.";
	/*if(Math.floor(seconds) >= 5)
		return Math.floor(seconds) + " seconds";*/
	else
		return "a few seconds ago.";
}

function uptime() {
	$.getJSON("json/stats.json", function(result) {
		if(result.reload)
			setTimeout(function() { location.reload(true) }, 1000);
		for (var i = 0; i < result.tasks.length; i++) {
			$("#loading-notice").remove();
			var TableRow = $("#tasks tr#r" + i);
			var ProcessBarType;
			if (!TableRow.length) {
				$("#tasks").append(
					"<tr id=\"r" + i + "\">" +
						"<td id=\"id\"><div class=\"progress\"><div style=\"width: 100%;\" class=\"progress-bar progress-bar-warning\"><small>Loading</small></div></div></td>" +
						"<td id=\"site\">Loading...</td>" +
						"<td id=\"url\">Loading...</td>" +
						"<td id=\"count\">Loading...</td>" +
						"<td id=\"progress\"><div class=\"progress progress-striped active\"><div style=\"width: 100%;\" class=\"progress-bar progress-bar-warning\"><small>Loading...</small></div></div></td>" +
						"<td id=\"crawler\">Loading...</td>" +
					"</tr>"
				);
				TableRow = $("#tasks tr#r" + i);
			}
			TableRow = TableRow[0];
			
			if (result.tasks[i].status > 0)
				ProcessBarType = "progress-bar progress-bar-danger";
			else if (result.tasks[i].crawler)
				ProcessBarType = "progress-bar progress-bar-warning";
			else
				ProcessBarType = "progress-bar progress-bar-success";
			TableRow.children["id"].children[0].children[0].className = ProcessBarType;
			TableRow.children["id"].children[0].children[0].innerHTML = "<small>"+result.tasks[i].id+"</small>";
			TableRow.children["site"].innerHTML = result.tasks[i].site;
			TableRow.children["url"].innerHTML = result.tasks[i].url;
			TableRow.children["count"].innerHTML = result.tasks[i].count;
			TableRow.children["progress"].children[0].children[0].className = ProcessBarType;
			TableRow.children["progress"].children[0].children[0].style.width = result.tasks[i].progress.width;
			TableRow.children["progress"].children[0].children[0].innerHTML = "<small>"+result.tasks[i].progress.html+"</small>";
			TableRow.children["crawler"].innerHTML = result.tasks[i].crawler;
		}
		d = new Date(result.updated*1000);
	});
}
function updateTime() {
	$("#updated").html("Last updated: " + timeSince(d));
}
uptime();
updateTime();
setInterval(uptime, 2000);
setInterval(updateTime, 500);