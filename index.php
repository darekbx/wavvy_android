<?php

error_reporting(E_ALL);

$server_name="mysql.cba.pl";
$user_name='baza_dev';
$password ='qaz1234';
$db_name='darekdev_cba_pl';

$dbConnection = new mysqli($server_name, $user_name, $password, $db_name);

if ($dbConnection->connect_errno != 0)
	die($dbConnection->connect_error);

// drop
if (false) {
	
	$sql = "DROP TABLE IF EXISTS `users`";
	$dbConnection->query($sql);
}

// create structure
$sql = "CREATE TABLE IF NOT EXISTS `users` "
		."(`id` INT NOT NULL AUTO_INCREMENT, "
		." `nick` VARCHAR(255) NULL, "
		." `artist` VARCHAR(255) NULL, "
		." `title` VARCHAR(255) NULL, "
		." `latitude` DOUBLE NULL, "
		." `longitude` DOUBLE NULL, "
		." `date` DATETIME NULL, "
		." PRIMARY KEY(`id`));";
$dbConnection->query($sql);

if (!empty($_POST)) {

	$postNameArr = array('nick', 'artist', 'title', 'longitude', 'latitude');
    $postIdentifierArr = array();
        
	foreach ($postNameArr as $postName)
		if (array_key_exists($postName, $_POST))
			$postIdentifierArr[] = $postName;
	
	if (count($postIdentifierArr) != count($postNameArr)) {
		
    $dbConnection -> close();
    die();
  }
	
	$stmt = $dbConnection->prepare("INSERT INTO `users` VALUES(null, ?, ?, ?, ?, ?, NOW())");
	
	$stmt->bind_param('sssdd', 
		$postIdentifierArr[0], 
		$postIdentifierArr[1], 
		$postIdentifierArr[2], 
		doubleval($postIdentifierArr[3]), 
		doubleval($postIdentifierArr[4]));
		
	$stmt->execute();
	$stmt->close();
}

if (!empty($_GET)) {

  $action = $_GET['action'];
  
  if ($action)
    switch ($action) {
    
      case 'nearest':
        
        // http://darekdev.cba.pl/?action=nearest&latitude=52.11&longitude=21.56
        // http://darekdev.cba.pl/?action=nearest&latitude=52.165&longitude=22.27
		// http://darekdev.cba.pl/?action=nearest&latitude=52.12&longitude=23.12 7726 -> 13323
      
        $latitude = doubleval($_GET['latitude']);
        $longitude = doubleval($_GET['longitude']);
        
        $select = "SELECT * FROM `users`";
        $data = $dbConnection->query($select);
  
        $result = array();
        
        while ($row = $data->fetch_assoc()) {

          $distance = haversineGreatCircleDistance($latitude, $longitude, $row['latitude'], $row['longitude']); 
          $nick = $row['nick'];
          $result[$nick] = $distance;
        }
        
        asort($result);
        print_r(reset($result));
      
        break;
       
      // to remove -------------------------------------------------------------------------
      case 'all':

        $select = "SELECT * FROM `users`";
        $data = $dbConnection->query($select);

        while ($row = $data->fetch_assoc()) {

          echo "id = ". $row['id'] . "<br />";
          echo "nick = ". $row['nick'] ."<br />";
          echo "artist = ". $row['artist'] ."<br />";
          echo "latitude = ". $row['latitude'] ."<br />";
          echo "longitude = ". $row['longitude'] ."<br />";
          echo "date =  ".$row['date'] ."<br /><br />";
        }

        break;
    }
}

function haversineGreatCircleDistance($latitudeFrom, $longitudeFrom, $latitudeTo, $longitudeTo, $earthRadius = 6371000) {

  // convert from degrees to radians
  $latFrom = deg2rad($latitudeFrom);
  $lonFrom = deg2rad($longitudeFrom);
  $latTo = deg2rad($latitudeTo);
  $lonTo = deg2rad($longitudeTo);

  $latDelta = $latTo - $latFrom;
  $lonDelta = $lonTo - $lonFrom;

  $angle = 2 * asin(sqrt(pow(sin($latDelta / 2), 2) +
    cos($latFrom) * cos($latTo) * pow(sin($lonDelta / 2), 2)));
  return $angle * $earthRadius;
}

$dbConnection -> close();

?>