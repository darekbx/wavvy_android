<?php

$server_name="mysql.cba.pl";
$user_name='baza_dev';
$password ='qaz1234';
$db_name='darekdev_cba_pl';

$dbConnection = new mysqli($server_name, $user_name, $password, $db_name);

if ($mysqli->connect_errno != 0)
	die($mysqli->connect_error);

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
		." `longitude` VARCHAR(10) NULL, "
		." `latitude` VARCHAR(10) NULL, "
		." `date` DATETIME NULL, "
		." PRIMARY KEY(`id`));";
$dbConnection->query($sql);

if (!empty($_POST)) {

	$postNameArr = array('nick', 'artist', 'title', 'longitude', 'latitude');
    $postIdentifierArr = array();
        
	foreach ($postNameArr as $postName)
		if (array_key_exists($postName, $_POST))
			$postIdentifierArr[] = $postName;
	
	$stmt = $dbConnection->prepare("INSERT INTO `users` VALUES(null, ?, ?, ?, ?, ?, NOW())");
	
	$stmt->bind_param("sssss", 
		$postIdentifierArr[0], 
		$postIdentifierArr[1], 
		$postIdentifierArr[2], 
		$postIdentifierArr[3], 
		$postIdentifierArr[4]);
		
	$stmt->execute();
	$stmt->close();
}

$select = "SELECT * FROM `users`";
$data = @$dbConnection->query($select);

while ($row = $data->fetch_assoc()) {

	echo "id = ". $row['id'] . "<br />";
	echo "nick = ". $row['nick'] ."<br />";
	echo "artist = ". $row['artist'] ."<br />";
	echo "date =  ".$row['date'] ."<br /><br />";
}

$dbConnection -> close();

?>