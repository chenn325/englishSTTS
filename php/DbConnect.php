<?php
$servername = "localhost";
$username = "root";
$password = "";
$database = "english";

//creating a new connection object using mysqli
$conn = new mysqli($servername, $username, $password, $database);

if($conn->connect_error){
	die("Connection failed: ". $conn->connect_error);
}
?>