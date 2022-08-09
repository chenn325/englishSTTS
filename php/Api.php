<?php

require_once 'DbConnect.php';

//an array to display response
$response = array();

//if it is an api call
if(isset($_GET['apicall'])){
	switch($_GET['apicall']){
		case 'signup':
			//handle the registration
			if(isTheseParametersAvailable(array('username','password'))){
				//getting the values
				$username = $_POST['username'];
				$password = md5($_POST['password']);

				//checking if the user is already exist with this username
				$stmt = $conn->prepare("SELECT id FROM users WHERE username = ?");
				$stmt->bind_param("s",$username);
				$stmt->execute();
				$stmt->store_result();

				//if the user already exist in the db
				if($stmt->num_rows > 0){
					$response['error'] = true;
					$response['message'] = 'User already registered';
					$stmt->close();
				}
				else{
					//if user is new creating
					$stmt = $conn->prepare("INSERT INTO users (username, password) VALUES (?,?)");
 					$stmt->bind_param("ss", $username, $password);

 					//if the user is successfully added to the db
 					if($stmt->execute()){
 						//fetching the user back
 						$stmt = $conn->prepare("SELECT id, id, username FROM users WHERE username = ?"); 
						$stmt->bind_param("s",$username);
						$stmt->execute();
						$stmt->bind_result($userid, $id, $username);
						$stmt->fetch();

						$user = array(
							'id'=>$id,
							'username'=>$username
						);

						$stmt->close();

						//adding the user data in response
						$response['error'] = false;
						$response['message'] = 'User registered successfully';
						$response['user'] = $user;
 					}
				}
			}
			else{
				$response['error'] = true; 
				$response['message'] = 'required parameters are not available'; 
			}
			break;
		case 'login' :
			//handle the login
			if(isTheseParametersAvailable(array('username','password'))){
				//getting values
				$username = $_POST['username'];
				$password = md5($_POST['password']);

				//creating the query
				$stmt = $conn->prepare("SELECT id, username, password FROM users WHERE username = ? AND password = ?");
				$stmt->bind_param("ss",$username, $password);
				$stmt->execute();
				$stmt->store_result();

				//if the user exist
				if($stmt->num_rows > 0){
					$stmt->bind_result($id, $username, $password);
					$stmt->fetch();

					$user = array(
						'id' => $id,
						'username' => $username
					);

					$response['error'] = false;
					$response['message'] = 'Login successsfull';
					$response['user'] = $user;
				}
				else{
					//if the user not found
					$response['error'] = true;
					$response['message'] = 'Invalid username or passwor';
				}
			}
			break;
		case 'study' :
			if(isTheseParametersAvailable(array('qnum'))){
				$Qnum = $_POST['qnum'];

				$stmt = $conn->prepare("SELECT ch, en FROM study WHERE id = ?");
				$stmt->bind_param("s", $Qnum);
				$stmt->execute();
				$stmt->store_result();

				if($stmt->num_rows() > 0){
					$stmt->bind_result($ch, $en);
					$stmt->fetch();

					$topic = array(
						'ch' => $ch,
						'en' => $en
					);

					$response['error'] = false;
					$response['message'] = 'get topic successful';
					$response['topic'] = $topic;
				}
				else{
					$response['error'] = true;
					$response['message'] = 'get topic error';
				}
			}
			else{
				$response['error'] = true;
			$response['message'] = 'false';
			}
			break;
		default:
			$response['error'] = true;
			$response['message'] = 'Invalid Operation Called';
	}
}
else{
//if it is not an api call
	$response['error'] = true;
	$response['message'] = 'Invalid API Called';
}

//display the response in json
echo json_encode($response);

//function validating all the parameters are available
function isTheseParametersAvailable($params){
	//traversing through all the parameters
	foreach($params as $param){
		//if the parameter is not available
		if(!isset($_POST[$param])){
			return false;
		}
	}
	return true;
}