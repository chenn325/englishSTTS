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
				$password = $_POST['password'];

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
				$password = $_POST['password'];

				//creating the query
				$stmt = $conn->prepare("SELECT id,  username, password, identity, name, myclass, gender FROM users WHERE username = ? AND password = ?");
				$stmt->bind_param("ss",$username, $password);
				$stmt->execute();
				$stmt->store_result();

				//if the user exist
				if($stmt->num_rows > 0){
					$stmt->bind_result($id, $username, $password, $identity, $name, $myclass, $gender);
					$stmt->fetch();

					$user = array(
						'id' => $id,
						'username' => $username,
						'identity' => $identity,
						'name' => $name,
						'myclass' => $myclass,
						'gender' => $gender
					);

					$response['error'] = false;
					$response['message'] = 'Login successsfull';
					$response['user'] = $user;
				}
				else{
					//if the user not found
					$response['error'] = true;
					$response['message'] = 'Invalid username or password';
				}
			}
			break;
		case 'study' :
			if(isTheseParametersAvailable(array('unit', 'class', 'category', 'type'))){
                $Unit = $_POST['unit'];
				$Classnum = $_POST['class'];
				$Category = $_POST['category'];
				$Type = $_POST['type'];

				$stmt = $conn->prepare("SELECT ch, en FROM textbook WHERE unit = ? AND class = ? AND category = ? AND type = ?");
				$stmt->bind_param("ssss", $Unit, $Classnum, $Category, $Type);
				$stmt->execute();
				$stmt->store_result();
				$count = $stmt->num_rows;

				if($count > 0){
					$response['error'] = false;
					$response['message'] = 'get topic successful';
					$response['rownum'] = $count;
					for($i=0; $i<$count; $i++){
						$stmt->bind_result($ch, $en);
						$stmt->fetch();

						$topic = array(
							'ch' => $ch,
							'en' => $en
						);
		
						$response[$i] = $topic;
					}

					$stmt = $conn->prepare("SELECT startYmd, EndYmd FROM date WHERE unit = ? AND class = ?");
					$stmt->bind_param("ss", $Unit, $Classnum);
					$stmt->execute();
					$stmt->store_result();
					$count = $stmt->num_rows;
					if($count > 0){
						$response['dateGetError'] = false;
						$response['dateGetMessage'] = 'get successful';

						$stmt->bind_result($sd, $ed);
						$stmt->fetch();
						$response['startDate'] = $sd;
						$response['endDate'] = $ed;
					}
					else{
						$response['dateGetError'] = true;
						$response['dateGetMessage'] = 'get dete error';
					}
				}
				else{
					$response['error'] = true;
					$response['message'] = 'get topic error';
					$response['rownum'] = $count;
				}
			}
			else{
				$response['error'] = true;
				$response['message'] = 'false';
			}
			break;
		case 'addTextbook':
			if(isTheseParametersAvailable(array('e', 'c', 'unit', 'class', 'category', 'type'))){
				$E = $_POST['e'];
				$C = $_POST['c'];
				$Unit = $_POST['unit'];
				$Class = $_POST['class'];
				$Category = $_POST['category'];
				$Type = $_POST['type'];

				$stmt = $conn->prepare("INSERT INTO textbook (unit, class, category, type, ch, en) VALUES (?, ?, ?, ?, ?, ?)");
				$stmt->bind_param("ssssss", $Unit, $Class, $Category, $Type, $C, $E);
				//怪怪
				$response['error'] = !($stmt->execute());
				$response['message'] = '新增成功！';
			}
			else{
				$response['error'] = true;
				$response['message'] = 'false';
			}
			break;
		case 'readStudentHistory':
			if(isTheseParametersAvailable(array('myClass','unit'))){
				$myclass = $_POST['myClass'];
				$unit = $_POST['unit'];

				//creating the query
				$stmt = $conn->prepare("SELECT users.name, history.listen_p, history.speak_p, history.listen_c, history.speak_c FROM users JOIN history ON users.id = history.user_id WHERE users.myclass = ? AND history.unit = ?");
				$stmt->bind_param("ss", $myclass, $unit);
				$stmt->execute();
				$stmt->store_result();

				if($stmt->num_rows >0){
					$response['row'] = $stmt->num_rows;
					$count = $stmt->num_rows;
					for($i=0; $i<$count; $i++){
						$stmt->bind_result($name, $listen_p, $speak_p, $listen_c, $speak_c);
						$stmt->fetch();
						$score = array(
							'name'=>$name,
							'listen_p' => $listen_p,
							'speak_p' => $speak_p,
							'listen_c' => $listen_c,
							'speak_c' => $speak_c
						);

						$response[$i] = $score;
					}
					
				}
			
				$response['error'] = false;
				$response['message'] = 'get studentHistory successful';
			}
			else{
				$response['error'] = true;
				$response['message'] = 'readStudentHistory have some problem.';
			}
			break;
		case 'changeDate':
			if(isTheseParametersAvailable(array('unit', 'class', 'date', 'select'))){
				$Unit = $_POST['unit'];
				$Class = $_POST['class'];
				$Date = $_POST['date'];
				$Select = filter_var($_POST['select'], FILTER_VALIDATE_BOOLEAN);

				if($Select){
					$stmt = $conn->prepare("UPDATE `date` SET `startYmd`=? WHERE `unit` = ? AND `class` = ?");
				}
				else{
					$stmt = $conn->prepare("UPDATE `date` SET `endYmd`=? WHERE `unit` = ? AND `class` = ?");
				}
				$stmt->bind_param("sss", $Date, $Unit, $Class);

				if($stmt->execute()){
					$response['error'] = false;
					$response['message'] = "change start date successful";
				}
				else{
					$response['error'] = true;
					$response['message'] = "change start date error";
				}
			}
			else{
				$response['error'] = true;
				$response['message'] = 'change start date parameter error';
			}
			break;
		case 'deleteTextbook':
			if(isTheseParametersAvailable(array('unit', 'class', 'e', 'c'))){
				$Unit = $_POST['unit'];
				$Class = $_POST['class'];
				$E = $_POST['e'];
				$C = $_POST['c'];

				$stmt = $conn->prepare("DELETE FROM textbook WHERE unit = ? AND class = ? AND en = ? AND ch = ?");
				$stmt->bind_param("ssss", $Unit, $Class, $E, $C);
				if($stmt->execute()){
					$response['error'] = false;
					$response['message'] = "delete textbook successful";
				}
				else{
					$response['error'] = true;
					$response['message'] = "delete textbook error";
				}
			}
			break;
		case 'testSet':
			if(isTheseParametersAvailable(array('unit', 'class'))){
				$Unit = $_POST['unit'];
				$Class = $_POST['class'];

				$stmt = $conn->prepare("SELECT unit FROM `date` WHERE unit = ? AND class = ?");
				$stmt->bind_param("ss", $Unit, $Class);
				$stmt->execute();
				$stmt->store_result();
				$count = $stmt->num_rows;

				if($count==0){
					date_default_timezone_set('PRC');
					$today = date("Y-m-d", time());
					$tomarrow = date("Y-m-d", strtotime(" 1 day"));
					$stmt = $conn->prepare("INSERT INTO `date` (unit, class, startYmd, endYmd) VALUES (?, ?, ?, ?)");
					$stmt->bind_param("ssss", $Unit, $Class, $today, $tomarrow);
					
					if($stmt->execute()){
						$response['error'] = false;
						$response['message'] = "set new textbook successful";
						$response['startDate'] = $today;
						$response['endDate'] = $tomarrow;
					}
					else{
						$response['error'] = true;
						$response['message'] = "set new textbook fail";
					}
					
				}
				else{
					//$response['test'] = "seted";
					$response['error'] = true;
					$response['message'] = "show textbook";
				}

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
