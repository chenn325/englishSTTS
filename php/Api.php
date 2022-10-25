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
				$stmt = $conn->prepare("SELECT id,  username, password, identity, name, myclass, gender, partner FROM users WHERE username = ? AND password = ?");
				$stmt->bind_param("ss",$username, $password);
				$stmt->execute();
				$stmt->store_result();

				//if the user exist
				if($stmt->num_rows > 0){
					$stmt->bind_result($id, $username, $password, $identity, $name, $myclass, $gender, $partner);
					$stmt->fetch();

					$user = array(
						'id' => $id,
						'username' => $username,
						'identity' => $identity,
						'name' => $name,
						'myclass' => $myclass,
						'gender' => $gender,
						'partner' => $partner
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
		
		case 'changePartner':
			if(isTheseParametersAvailable(array('id','partner'))){
				$Id = $_POST['id'];
				$Partner = $_POST['partner'];

				$stmt = $conn->prepare("UPDATE users SET partner = ? WHERE id = ?");
				$stmt->bind_param("ss", $Partner, $Id);
				$stmt->execute();

				$response['error'] = false;
				$response['message'] = 'update partner successsfull';

			}
			else{
				$response['error'] = true;
				$response['message'] = 'update partner wrong';
			}

			break;
		
		case 'study' :
			if(isTheseParametersAvailable(array('unit', 'class', 'category', 'type'))){
                $Unit = $_POST['unit'];
				$Classnum = $_POST['class'];
				$Category = $_POST['category'];
				$Type = $_POST['type'];

				// $stmt = $conn->prepare("SELECT ch, en FROM textbook WHERE unit = ? AND class = ? AND category = ? AND type = ?");
				$stmt = $conn->prepare("SELECT en FROM textbook WHERE unit = ? AND class = ? AND category = ? AND type = ?");
				$stmt->bind_param("ssss", $Unit, $Classnum, $Category, $Type);
				$stmt->execute();
				$stmt->store_result();
				$count = $stmt->num_rows;

				if($count > 0){
					$response['error'] = false;
					$response['message'] = 'get topic successful';
					$response['rownum'] = $count;
					for($i=0; $i<$count; $i++){
						// $stmt->bind_result($ch, $en);
						$stmt->bind_result($en);
						$stmt->fetch();

						$topic = array(
							// 'ch' => $ch,
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

		case 'setSchedule':
			if(isTheseParametersAvailable(array('myclass'))){
				$Myclass = $_POST['myclass'];

				$stmt = $conn->prepare("SELECT unit, startYmd, endYmd FROM `date` WHERE class = ?");
				$stmt->bind_param('s', $Myclass);
				$stmt->execute();
				$stmt->store_result();

				if($stmt->num_rows > 0){
					$response['row'] = $stmt->num_rows;
					$count = $stmt->num_rows;
					for($i=0; $i<$count; $i++){
						$stmt->bind_result($unit, $startYmd, $endYmd);
						$stmt->fetch();
						$schedule = array(
							'unit' => $unit,
							'startYmd' => $startYmd,
							'endYmd' => $endYmd
						);

						$response[$i] = $schedule;
					}
					
				}
				$response['error'] = false;
				$response['message'] = 'setSchedule successsfull';
			}
			else{
				$response['error'] = true;
				$response['message'] = 'setSchedule unsuccesssfull';
			}

			break;
		case 'addTextbook':
			// if(isTheseParametersAvailable(array('e', 'c', 'unit', 'class', 'category', 'type'))){
				if(isTheseParametersAvailable(array('e', 'unit', 'class', 'category', 'type'))){
				$E = $_POST['e'];
				// $C = $_POST['c'];
				$Unit = $_POST['unit'];
				$Class = $_POST['class'];
				$Category = $_POST['category'];
				$Type = $_POST['type'];

				// $stmt = $conn->prepare("INSERT INTO textbook (unit, class, category, type, ch, en) VALUES (?, ?, ?, ?, ?, ?)");
				$stmt = $conn->prepare("INSERT INTO textbook (unit, class, category, type, en) VALUES (?, ?, ?, ?, ?)");
				$stmt->bind_param("sssss", $Unit, $Class, $Category, $Type, $E);
				$response['error'] = !($stmt->execute());
				$response['message'] = '新增成功！';
			}
			else{
				$response['error'] = true;
				$response['message'] = 'false';
			}
			break;
		case 'readStudentHistory':
			if(isTheseParametersAvailable(array('myClass','unit','type'))){
				$myclass = $_POST['myClass'];
				$unit = $_POST['unit'];
				$type = $_POST['type'];

				//creating the query
				$stmt = $conn->prepare("SELECT users.id, users.name, history.listen_p, history.speak_p, history.listen_c, history.speak_c FROM users JOIN history ON users.id = history.user_id WHERE users.myclass = ? AND history.unit = ? AND history.type = ? ");
				

				$stmt->bind_param("sss", $myclass, $unit, $type);
				$stmt->execute();
				$stmt->store_result();

				if($stmt->num_rows >0){
					$response['row'] = $stmt->num_rows;
					$count = $stmt->num_rows;
					for($i=0; $i<$count; $i++){
						$stmt->bind_result($user_id, $name, $listen_p, $speak_p, $listen_c, $speak_c);
						$stmt->fetch();
						$score = array(
							'user_id'=>$user_id,
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
			// if(isTheseParametersAvailable(array('unit', 'class', 'e', 'c'))){
				if(isTheseParametersAvailable(array('unit', 'class', 'e'))){
				$Unit = $_POST['unit'];
				$Class = $_POST['class'];
				$E = $_POST['e'];
				// $C = $_POST['c'];

				// $stmt = $conn->prepare("DELETE FROM textbook WHERE unit = ? AND class = ? AND en = ? AND ch = ?");
				$stmt = $conn->prepare("DELETE FROM textbook WHERE unit = ? AND class = ? AND en = ?");
				// $stmt->bind_param("ssss", $Unit, $Class, $E, $C);
				$stmt->bind_param("sss", $Unit, $Class, $E);
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
					$response['seted'] = false;
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
					$response['seted'] = true;
					$response['error'] = false;
					$response['message'] = "show textbook";
				}

			}
			else{
				$response['error'] = true;
				$response['message'] = "test set error";
			}
			break;
		case 'ListenLearning':
			if(isTheseParametersAvailable(array('unit','myclass','category','type'))){
				$unit = $_POST['unit'];
				$myclass = $_POST['myclass'];
				$category = $_POST['category'];
				$type = $_POST['type'];

				//creating the query
				$stmt = $conn->prepare("SELECT en FROM textbook WHERE unit = ? AND class = ? AND category = ? AND type = ?");
				$stmt->bind_param("ssss", $unit, $myclass, $category, $type);
				$stmt->execute();
				$stmt->store_result();

				if($stmt->num_rows >0){
					$response['row'] = $stmt->num_rows;
					$count = $stmt->num_rows;
					for($i=0; $i<$count; $i++){
						// $stmt->bind_result($ch, $en);
						$stmt->bind_result($en);
						$stmt->fetch();
						$ListenText = array(
							// 'ch' => $ch,
							'en' => $en
						);

						$response[$i] = $ListenText;
					}
				}
			
				$response['error'] = false;
				$response['message'] = 'get ListenLearning successful';
			}
			else{
				$response['error'] = true;
				$response['message'] = 'ListenLearning have some problem.';
			}
			break;
		case 'history_LP': //listen練習次數+1
			if(isTheseParametersAvailable(array('user_id','unit'))){
					$user_id = $_POST['user_id'];
					$unit = $_POST['unit'];
					//creating the query
					$stmt = $conn->prepare("UPDATE history SET listen_p = listen_p + 1 WHERE user_id = ? AND unit = ?");
					$stmt->bind_param("ss", $user_id, $unit);
					$stmt->execute();
					$stmt->store_result();

					
					$response['error'] = false;
					$response['message'] = 'LP plus 1 successful';
				}
				else{
					$response['error'] = true;
					$response['message'] = 'LP have some problem.';
				}
				break;
		case 'history_LC': //listen測驗成績
			if(isTheseParametersAvailable(array('user_id','unit','score'))){
					$user_id = $_POST['user_id'];
					$unit = $_POST['unit'];
					$score = $_POST['score'];

					//creating the query
					$stmt = $conn->prepare("UPDATE history SET listen_c = $score WHERE user_id = ? AND unit = ?");
					$stmt->bind_param("ss", $user_id, $unit);
					$stmt->execute();
					$stmt->store_result();

					
					$response['error'] = false;
					$response['message'] = 'LC successful';
				}
				else{
					$response['error'] = true;
					$response['message'] = 'LC have some problem.';
				}
				break;		
		case 'todayText':
			if(isTheseParametersAvailable(array('id'))){
					$id = $_POST['id'];

					//creating the query
					$stmt = $conn->prepare("SELECT text FROM todaywords WHERE id = ? ");
					$stmt->bind_param("s", $id);
					$stmt->execute();
					$stmt->store_result();

					if($stmt->num_rows > 0){
						$response['row'] = $stmt->num_rows;
						
						$stmt->bind_result($text);
						$stmt->fetch();
						$todayText = array(
							'text' => $text
						);

						$response['text'] = $todayText;
					}
				
					$response['error'] = false;
					$response['message'] = 'get todayText successful';
				}
				else{
					$response['error'] = true;
					$response['message'] = 'todayText have some problem.';
				}
				break;

		case 'uploadError':
			if(isTheseParametersAvailable(array('user_id', 'category', 'type', 'unit', 'en'))){
				$user_id = $_POST['user_id'];
				$category = $_POST['category'];
				$type = $_POST['type'];
				$unit = $_POST['unit'];
				$en = $_POST['en'];

				$stmt = $conn->prepare("INSERT INTO error (user_id, category, type, unit, en) VALUES (?, ?, ?, ?, ?)");
				$stmt->bind_param("sssss", $user_id, $category, $type, $unit, $en);
				$stmt->execute();

				$response['error'] = false;
				$response['message'] = 'uploadError successful';
			}
			else{
				$response['error'] = true;
				$response['message'] = 'uploadError have some problem';
			}
			break;
		case 'downloadError':
			if(isTheseParametersAvailable(array('user_id', 'unit', 'category', 'type'))){
				$user_id = $_POST['user_id'];
				$unit = $_POST['unit'];
				$category = $_POST['category'];
				$type = $_POST['type'];
				

				$stmt = $conn->prepare("SELECT en FROM error WHERE user_id = ? AND unit = ? AND category = ? AND type = ?");
				$stmt->bind_param("ssss", $user_id, $unit, $category, $type);
				$stmt->execute();
				$stmt->store_result();

				if($stmt->num_rows > 0){
					$response['row'] = $stmt->num_rows;
					$count = $stmt->num_rows;
					for($i=0; $i<$count; $i++){
						$stmt->bind_result($en);
						$stmt->fetch();
						$errorText = array(
							'text' => $en
						);

						$response[$i] = $errorText;
					}

				}
				else{
					$response[0] = "無";
				}
				

				$response['error'] = false;
				$response['message'] = 'downloadError successful';
			}
			else{
				$response['error'] = true;
				$response['message'] = 'downloadError have some problem';
			}
			break;

		case 'initStudentHistory' :
			if(isTheseParametersAvailable(array('class', 'unit'))){
				$classnum = $_POST['class'];
				$unit = $_POST['unit'];

				$stmt = $conn->prepare("SELECT id FROM users WHERE myclass = ?");
				$stmt->bind_param('s', $classnum);
				$stmt->execute();
				$stmt->store_result();
				$count = $stmt->num_rows;

				if($count > 0){
					$response['rownum'] = $count;
					for($i=0; $i<$count; $i++){
						// $stmt->bind_result($ch, $en);
						$stmt->bind_result($stuID);
						$stmt->fetch();
		
						// $response[$i] = $stuID;

						$stmt2 = $conn->prepare("INSERT INTO `history` (`id`, `user_id`, `unit`, `listen_p`, `speak_p`, `listen_c`, `speak_c`, `type`) VALUES (NULL, ?, ?, '0', '0', '-1', '-1', 'vocabulary')");
						$stmt2->bind_param('ss', $stuID, $unit);
						$stmt2->execute();
						// $stmt2->store_result();
					}
					$response['error'] = false;
					$response['message'] = 'get student list successful';
				}
				else{
					$response['error'] = true;
					$response['message'] = 'init student history get list error';
					$response['rownum'] = $count;
				}
			}
			else{
				$response['error'] = true;
				$response['message'] = 'init student history error';
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
