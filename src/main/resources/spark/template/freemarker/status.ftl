<!DOCTYPE html>
<html>
 <head> 
  <title>Distributed Crawler Status</title> 
  <meta charset="utf-8" /> 
  <meta http-equiv="X-UA-Compatible" content="IE=edge" /> 
  <meta name="viewport" content="width=device-width, initial-scale=1.0" /> 
  <meta name="description" content="Distributed Crawler Status" /> 
  <link rel="stylesheet" href="css/bootstrap.min.css" /> 
  <link rel="stylesheet" href="css/bootstrap-theme.min.css" /> 
  <link rel="stylesheet" href="css/light.css" title="light" /> 
  <style>
	body {
		padding-top: 70px;
		padding-bottom: 30px;
	}
  </style> 
 </head> 
 <body> 
  <div role="navigation" class="navbar navbar-inverse navbar-fixed-top"> 
   <div class="navbar-inner"> 
    <div class="container"> 
     <div class="navbar-header"> 
      <button data-target=".navbar-collapse" data-toggle="collapse" class="navbar-toggle" type="button"> <span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span> </button> 
      <a href="#" class="navbar-brand">Distributed Crawler Status</a> 
     </div> 
    </div> 
   </div> 
  </div> 
  <div class="container content"> 
   <div id="loading-notice"> 
    <noscript> 
     <div class="alert alert-danger" style="text-align: center;"> 
      <strong>Enable JavaScript</strong> you fucking autist neckbeard, it's not gonna hurt you. 
     </div> 
    </noscript> 
    <div class="progress progress-striped active"> 
     <div class="progress-bar progress-bar-warning" style="width: 100%;">
      Loading...
     </div> 
    </div> 
    <div style="text-align: center;">
      If this message doesn't disappear make sure that you have Javascript enabled.
     <br />Otherwise the status server is probably down. 
    </div> 
    <p></p> 
   </div> 
   <table class="table table-striped table-condensed table-hover"> 
    <thead> 
     <tr> 
      <th id="id" style="text-align: center;">ID</th> 
      <th id="site" style="text-align: center;">Site</th> 
      <th id="url" style="text-align: center;">Url</th> 
      <th id="count" style="text-align: center;">Count</th> 
      <th id="progress" style="text-align: center;">Reset Remaining</th> 
      <th id="crawler" style="text-align: center;">Crawler IPaddr</th> 
     </tr> 
    </thead> 
    <tbody id="tasks"> 
     <!-- Servers here \o/ --> 
    </tbody> 
   </table> 
  </div> 
  <script src="js/jquery-1.10.2.min.js"></script> 
  <script src="js/bootstrap.min.js"></script> 
  <script src="js/status.js"></script>   
 </body>
</html>