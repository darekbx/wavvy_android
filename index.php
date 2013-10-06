<?php

error_reporting(E_ALL);

$api = new Api();
$api->connect();
$api->createTables();

if (!empty($_POST)) {

	$api->add($_POST);
}

if (!empty($_GET)) {

  $action = $_GET['action'];
  
  if ($action)
    switch ($action) {
    
      case 'nearest': $api->findNearest($_GET); break;
      case 'userlocations': $api->userLocations($_GET); break;
      case 'registernick': $api->registerNick($_GET); break;
      
      // --------------------------------------- UNSAFE
      case 'all': $api->printAll(); break;
      case 'drop': $api->drop(); break;
      // --------------------------------------- UNSAFE
    }
}

$api->disconnect();

class Api {

  private $maxNearest = 10;
  private $connection;
  
  function connect() {

    $server_name = 'mysql.cba.pl';
    $user_name = 'baza_dev';
    $password = 'qaz1234';
    $db_name = 'darekdev_cba_pl';

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

    $sql = "CREATE TABLE IF NOT EXISTS `user` "
        ."(`id` INT NOT NULL AUTO_INCREMENT, "
        ." `nick` VARCHAR(255) NULL, "
        ." `created` DATETIME NULL, "
        ." PRIMARY KEY(`id`));";
        
    $this->connection->query($sql);
    
    $sql = "CREATE TABLE IF NOT EXISTS `song` "
        ."(`id` INT NOT NULL AUTO_INCREMENT, "
        ." `id_user` INT NULL, "
        ." `artist` VARCHAR(255) NULL, "
        ." `title` VARCHAR(255) NULL, "
        ." `latitude` DOUBLE NULL, "
        ." `longitude` DOUBLE NULL, "
        ." `date` DATETIME NULL, "
        ." PRIMARY KEY(`id`));";
        
    $this->connection->query($sql);
  }

  function add($post) {

    $postNameArr = array('id_user', 'artist', 'title', 'latitude', 'longitude');
    $postIdentifierArr = array();
    
    try {
    
      foreach ($postNameArr as $postName)
        if (array_key_exists($postName, $post))
          $postIdentifierArr[] = $post[$postName];
      
      if (count($postIdentifierArr) != count($postNameArr)) {
        
        $this->printError("Wrong parameters.");
      }
      else {
      
        $stmt = $this->connection->prepare("INSERT INTO `song` VALUES(null, ?, ?, ?, ?, ?, NOW())");
        
        $id = intval($postIdentifierArr[0]);
        $lat = doubleval($postIdentifierArr[3]);
        $lon = doubleval($postIdentifierArr[4]);
        
        $stmt->bind_param('issdd', 
          $id, 
          $postIdentifierArr[1], 
          $postIdentifierArr[2], 
          $lat, 
          $lon);
          
        $stmt->execute();
        $stmt->close();
        
        $this->printSuccess("added");
      }
    } 
    catch (Exception $e) {
    
      $this->printError($e->getMessage());
    }
  }
  
  // http://darekdev.cba.pl/?action=registernick&nick=Darek
  function registerNick($get) {
  
    if (!empty($get['nick'])) {
      
      try { 
        
        $nick = $get['nick'];
        
        $stmt = $this->connection->prepare("SELECT COUNT(id) FROM `user` WHERE `nick` = ?");
        $stmt->bind_param('s', $nick);
        $stmt->execute();
        
        $result = $stmt->get_result();
        $count = $result->fetch_array(MYSQLI_NUM)[0];
        
        $stmt->close();
        
        if ($count == 0) {
          
          $this->reserveNick($nick);
        }
        else {
        
          echo json_encode(array("count" => $count));
        }
      } 
      catch (Exception $e) {
      
        $this->printError($e->getMessage());
      }
    }
  }
  
  function reserveNick($nick) {
  
    if (!empty($nick)) {
      
      try { 
        
        $stmt = $this->connection->prepare("INSERT INTO `user` VALUES(null, ?, NOW())");
        $stmt->bind_param('s', $nick);
        $stmt->execute();
        $stmt->close();
        
        echo json_encode(array("id" => $this->connection->insert_id));
      } 
      catch (Exception $e) {
      
        $this->printError($e->getMessage());
      }
    }
  }
  
  // http://darekdev.cba.pl/?action=userlocations&id_user=1
  function userLocations($get) {

    if (!empty($get['id_user'])) {
    
      try {
      
        $id_user= $get['id_user'];
        
        $stmt = $this->connection->prepare("SELECT * FROM `song` WHERE `id_user` = ?");
        $stmt->bind_param('i', $id_user);
        $stmt->execute();
        
        $result = $stmt->get_result();
        $rows = array();
        
        while ($row = $result->fetch_assoc())
          $rows[] = $row;
        
        $stmt->close();
        
        echo json_encode($rows);
        
      } 
      catch (Exception $e) {
      
        $this->printError($e->getMessage());
      }
    }
  }

  // http://darekdev.cba.pl/?action=nearest&latitude=52.11&longitude=21.56&id_user=1
  function findNearest($get) {
  
    if (is_numeric($get['latitude']) && is_numeric($get['longitude']) && !empty($get['id_user'])) {
      
      try {
        
        $lat = doubleval($get['latitude']);
        $lon = doubleval($get['longitude']);
        $id_user = $get['id_user'];
        
        $select = "SELECT id, id_user, latitude, longitude FROM `song`";
        $data = $this->connection->query($select);
        
        if ($data) {
          
          $distances = array();
        
          // loop through all songs
          while ($row = $data->fetch_assoc()) {
            
            // skip current user
            if ($id_user == $row['id_user'])
              continue;
            
            // find nearest distance
            $distance = $this->haversineGreatCircleDistance($lat, $lon, $row['latitude'], $row['longitude']);
            $add = true;
            
            // if exists, check if new distance is smaller
            if (array_key_exists($row['id_user'], $distances)) {
            
              $existing = $distances[$row['id_user']];
              $add = $existing > $distance;
            }
            
            // set user distance
            if ($add) {
            
              $distances[$row['id_user']] = array(
                "distance" => intval($distance), 
                "latitude" => $row['latitude'], 
                "longitude" => $row['longitude']
              );
            }
          }
          
          $data->close();
          
          // sort by distance
          uasort($distances, $this->build_sorter('distance'));
          
          if (count($distances) > 0) {
          
            // limit nearest users
            $distances = array_slice($distances, 0, $this->maxNearest, true);
            $ids = implode(",", array_keys($distances));
            
            // get detailed user info
            $select = "SELECT * FROM `user` WHERE `id` IN (". $ids .") ORDER BY FIELD (id, ". $ids .")";
            $data = $this->connection->query($select);
            $result = array();
            
            while ($row = $data->fetch_assoc())
              $result[] = array_merge($distances[$row['id']], $row);
            
            echo json_encode($result);
            $data->close();
          }
          else {
          
            echo json_encode(array());
          }
        }
        else {
        
          echo json_encode(array());
        }
      } 
      catch (Exception $e) {
      
        $this->printError($e->getMessage());
      } 
    }
    else {
    
      $this->printError("Invalid one of parameters: logitude, latitude, id_user.");
    }
  }

  function printAll() {

    $select = "SELECT * FROM `user`";
    $data = $this->connection->query($select);

    while ($row = $data->fetch_assoc())
      echo json_encode($row) . "<br />";
    
    $data->close();
    
    for ($i = 0; $i < 50; $i++)
      echo "-";
    echo "<br />";
    
    $select = "SELECT * FROM `song`";
    $data = $this->connection->query($select);

    while ($row = $data->fetch_assoc())
      echo json_encode($row) . "<br />";
    
    $data->close();
  }
  
  function drop() {
  
    $sql = "DROP TABLE IF EXISTS `user`";
    $this->connection()->query($sql);
    
    $sql = "DROP TABLE IF EXISTS `song`";
    $this->connection()->query($sql);
    
    echo "dropped";
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
  
  function build_sorter($key) {

    return function ($a, $b) use ($key) {

      return strnatcmp($a[$key], $b[$key]);
    };
  }
}

?>