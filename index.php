<?php

error_reporting(E_ALL);

$api = new Api();
$api->connect();

// drop
if (false) {
	
	$sql = "DROP TABLE IF EXISTS `users`";
	$api->connection()->query($sql);
}

// delete 
if (false) {

	$sql = "DELETE FROM `users` WHERE id > 2";
	$api->connection()->query($sql);
}

$api->createTables();

if (!empty($_POST)) {

	$api->add($_POST);
}

if (!empty($_GET)) {

  $action = $_GET['action'];
  
  if ($action)
    switch ($action) {
    
      case 'nearest':
        $api->findNearest($_GET);
        break;
       
      case 'all':
        $api->printAll();
        break;
    }
}

$api->disconnect();

class Api {

  private $connection;
  
  function connect() {

    $server_name="mysql.cba.pl";
    $user_name='baza_dev';
    $password ='qaz1234';
    $db_name='darekdev_cba_pl';

    $this->connection = new mysqli($server_name, $user_name, $password, $db_name);

    if ($this->connection->connect_errno != 0) {
      
      printError($this->connection->connect_error);
      die();
    }
  }
  
  function connection() {
  
    return $this->connection;
  }
  
  function disconnect() {
  
    $this->connection->close();
  }
    
  function createTables() {

    $sql = "CREATE TABLE IF NOT EXISTS `users` "
        ."(`id` INT NOT NULL AUTO_INCREMENT, "
        ." `nick` VARCHAR(255) NULL, "
        ." `artist` VARCHAR(255) NULL, "
        ." `title` VARCHAR(255) NULL, "
        ." `latitude` DOUBLE NULL, "
        ." `longitude` DOUBLE NULL, "
        ." `date` DATETIME NULL, "
        ." PRIMARY KEY(`id`));";
        
    $this->connection->query($sql);
  }

  function add($post) {

    $postNameArr = array('nick', 'artist', 'title', 'longitude', 'latitude');
    $postIdentifierArr = array();
    
    foreach ($postNameArr as $postName)
      if (array_key_exists($postName, $post))
        $postIdentifierArr[] = $postName;
    
    if (count($postIdentifierArr) != count($postNameArr)) {
      
      $this->printError("Wrong parameters.");
    }
    else {
    
      $stmt = $this->connection->prepare("INSERT INTO `users` VALUES(null, ?, ?, ?, ?, ?, NOW())");
      
      $lat = doubleval($postIdentifierArr[3]);
      $lon = doubleval($postIdentifierArr[4]);
      
      $stmt->bind_param('sssdd', 
        $postIdentifierArr[0], 
        $postIdentifierArr[1], 
        $postIdentifierArr[2], 
        $lat, 
        $lon);
        
      $stmt->execute();
      $stmt->close();
      
      $this->printSuccess("added");
    }
  }

  function findNearest($get) {

    // http://darekdev.cba.pl/?action=nearest&latitude=52.11&longitude=21.56
    // http://darekdev.cba.pl/?action=nearest&latitude=52.165&longitude=22.27
    
    if (is_numeric($get['latitude']) && is_numeric($get['longitude'])) {
      
      $lat = doubleval($get['latitude']);
      $lon = doubleval($get['longitude']);
      
      $select = "SELECT id, latitude, longitude FROM `users`";
      $data = $this->connection->query($select);
      $result = array();
      
      while ($row = $data->fetch_assoc()) {

        // find nearest user
        $distance = $this->haversineGreatCircleDistance($lat, $lon, $row['latitude'], $row['longitude']); 
        $nick = $row['id'];
        $result[$nick] = $distance;
      }
      
      asort($result);
      
      list($key, $value) = each($result);

      // get detailed user info
      $select = "SELECT * FROM `users` WHERE `id` = ". $key;
      $data = $this->connection->query($select);

      echo json_encode($data->fetch_assoc());
    }
    else {
    
      $this->printError("Invalid logitude or latitude.");
    }
  }

  function printAll() {

    $select = "SELECT * FROM `users`";
    $data = $this->connection->query($select);

    while ($row = $data->fetch_assoc())
      echo json_encode($row) . "<br />";
  }

  function haversineGreatCircleDistance($latitudeFrom, $longitudeFrom, $latitudeTo, $longitudeTo) {

    $earthRadius = 6371000;
    
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

  function printError($message) {

    echo json_encode(array("error" => $message));
  }

  function printSuccess($message) {

    echo json_encode(array("success" => $message));
  }
}

?>