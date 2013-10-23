<?php

error_reporting(E_ALL);

$api = new Api();
$api->connect();
$api->createTables();

if (!empty($_POST)) {

	$api->addAndCreate($_POST);
}

if (!empty($_GET)) {

  $action = $_GET['action'];
  
  if ($action)
    switch ($action) {
    
      case 'locations': $api->locations(); break;
      case 'like': $api->like($_GET); break;
      
      // --------------------------------------- UNSAFE
      case 'all': $api->printAll(); break;
      case 'drop': $api->drop(); break;
      // --------------------------------------- UNSAFE
    }
}

$api->disconnect();

class Api {

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
        ." `album` VARCHAR(255) NULL, "
        ." `latitude` DOUBLE NULL, "
        ." `longitude` DOUBLE NULL, "
        ." `date` DATETIME NULL, "
        ." PRIMARY KEY(`id`));";
    $this->connection->query($sql);
    
    $sql = "CREATE TABLE IF NOT EXISTS `like` "
        ."(`id` INT NOT NULL AUTO_INCREMENT, "
        ." `id_user` INT NULL, "
        ." `created` DATETIME NULL, "
        ." PRIMARY KEY(`id`));";
    $this->connection->query($sql);
  }

  function addAndCreate($post) {

    $postNameArr = array('id_user', 'artist', 'title', 'album', 'latitude', 'longitude');
    $postIdentifierArr = array();
    
    try {
    
      foreach ($postNameArr as $postName)
        if (array_key_exists($postName, $post))
          $postIdentifierArr[] = $post[$postName];
      
      if (count($postIdentifierArr) != count($postNameArr)) {
        
        $this->printError("Wrong parameters.");
      }
      else {
      
        $is_new = false;
      
        $id_user = intval($postIdentifierArr[0]);
        $lat = doubleval($postIdentifierArr[4]);
        $lon = doubleval($postIdentifierArr[5]);
        
        if ($id_user === -1) {
        
          // create new user
          $time = time();
          $stmt = $this->connection->prepare("INSERT INTO `user` VALUES(null, ?, NOW())");
          $stmt->bind_param('s', $time); // nick is unix timestamp
          $stmt->execute();
          $stmt->close();
          
          $id_user = $this->connection->insert_id;
          $is_new = true;
        }
        
        $stmt = $this->connection->prepare("INSERT INTO `song` VALUES(null, ?, ?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param('isssdd', 
          $id_user, 
          $postIdentifierArr[1], 
          $postIdentifierArr[2], 
          $postIdentifierArr[3], 
          $lat, 
          $lon);
          
        $stmt->execute();
        $stmt->close();
        
        if ($is_new === true) $this->printAdded("added", $id_user);
        else $this->printSuccess("added");
      }
    } 
    catch (Exception $e) {
    
      $this->printError($e->getMessage());
    }
  }
  
  // http://darekdev.cba.pl/?action=locations
  function locations() {

    try {
    
      $stmt = $this->connection->prepare("SELECT `id_user`, `artist`, `title`, `album`, `latitude`, `longitude`, `date` FROM `song`");
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

  // http://darekdev.cba.pl/?action=like&target_id_user=1
  function like($get) {

    if (!empty($get['target_id_user'])) {
        
      try {
      
        $target_id_user = $get['target_id_user'];
        
        $stmt = $this->connection->prepare("INSERT INTO `like` VALUES(null, ?, NOW())");
        $stmt->bind_param('i', $target_id_user);
        $stmt->execute();
        $stmt->close();
        
        $this->printSuccess("liked");
      } 
      catch (Exception $e) {
      
        $this->printError($e->getMessage());
      }
    }
    else if (!empty($get['id_user'])) {
    
      try {
      
        $id_user = $get['id_user'];
        
        $stmt = $this->connection->prepare("SELECT COUNT(id) FROM `like` WHERE `id_user` = ?");
        $stmt->bind_param('i', $id_user);
        $stmt->execute();
        
        $result = $stmt->get_result();
        $count = $result->fetch_array(MYSQLI_NUM)[0];
        
        $stmt->close();
        
        echo json_encode(array("count" => $count));
      } 
      catch (Exception $e) {
      
        $this->printError($e->getMessage());
      }
    }
  }

  function printAll() {

    //
    // user
    //
    $data = $this->connection->query("SELECT * FROM `user`");
    while ($row = $data->fetch_assoc()) echo json_encode($row) . "<br />";
    $data->close();
    
    for ($i = 0; $i < 50; $i++) echo "-";
    echo "<br />";
    
    //
    // song
    //
    $data = $this->connection->query("SELECT * FROM `song`");
    while ($row = $data->fetch_assoc()) echo json_encode($row) . "<br />";
    $data->close();
    
    for ($i = 0; $i < 50; $i++) echo "-";
    echo "<br />";
    
    //
    // like
    //
    $data = $this->connection->query("SELECT * FROM `like`");
    while ($row = $data->fetch_assoc()) echo json_encode($row) . "<br />";
    $data->close();
  }
  
  function drop() {
  
    $this->connection()->query("DROP TABLE IF EXISTS `user`");
    $this->connection()->query("DROP TABLE IF EXISTS `song`");
    $this->connection()->query("DROP TABLE IF EXISTS `like`");
    
    echo "dropped";
  }

  function printError($message) {

    echo json_encode(array("error" => $message));
  }

  function printSuccess($message) {

    echo json_encode(array("success" => $message));
  }

  function printAdded($message, $id) {

    echo json_encode(array("success" => $message, "id_user" => $id));
  }
}

?>