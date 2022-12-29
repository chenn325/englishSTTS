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
		case 'keepLogin':
			if(isTheseParametersAvailable(array('username'))){
				//getting values
				$username = $_POST['username'];

				//creating the query
				$stmt = $conn->prepare("SELECT id,  username, password, identity, name, myclass, gender, partner FROM users WHERE username = ?");
				$stmt->bind_param("s",$username);
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
					$response['message'] = 'Keep Login successsfull';
					$response['user'] = $user;
				}
				else{
					//if the user not found
					$response['error'] = true;
					$response['message'] = 'Keep Login unsuccesssfull';
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
						$stmt->bind_result($en);
						$stmt->fetch();

						$topic = array(
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
			if(isTheseParametersAvailable(array('myclass', 'category', 'type'))){
				$Myclass = $_POST['myclass'];
				$Category = $_POST['category'];
				$Type = $_POST['type'];

				$stmt = $conn->prepare("SELECT unit, startYmd, endYmd FROM `date` WHERE class = ? AND category = ? AND type = ?");
				$stmt->bind_param('sss', $Myclass, $Category, $Type);
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
				if(isTheseParametersAvailable(array('e', 'unit', 'class', 'category', 'type'))){
				$E = $_POST['e'];
				$Unit = $_POST['unit'];
				$Class = $_POST['class'];
				$Category = $_POST['category'];
				$Type = $_POST['type'];

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
			if(isTheseParametersAvailable(array('e', 'unit', 'class', 'category', 'type'))){
				$E = $_POST['e'];
				$Unit = $_POST['unit'];
				$Class = $_POST['class'];
				$Category = $_POST['category'];
				$Type = $_POST['type'];

				$stmt = $conn->prepare("DELETE FROM textbook WHERE unit = ? AND class = ? AND en = ? AND category = ? AND type = ?");
				$stmt->bind_param("sssss", $Unit, $Class, $E, $Category, $Type);
				if($stmt->execute()){
					$response['error'] = false;
					$response['message'] = "delete textbook successful";
					//可能啥都沒刪還是跟你說成功www
				}
				else{
					$response['error'] = true;
					$response['message'] = "delete textbook error";
				}
			}
			break;
		case 'testSet':
			if(isTheseParametersAvailable(array('unit', 'class', 'category', 'type'))){
                $Unit = $_POST['unit'];
				$Classnum = $_POST['class'];
				$Category = $_POST['category'];
				$Type = $_POST['type'];

				$stmt = $conn->prepare("SELECT * FROM `date` WHERE `unit` = ? AND `class` = ? AND `category` = ? AND `type` = ?");
				$stmt->bind_param("ssss", $Unit, $Classnum, $Category, $Type);

				$stmt->execute();
				$stmt->store_result();
				$count = $stmt->num_rows;

				if($count==0){
					$response['seted'] = false;
					$response['error'] = false;
					$response['message'] = "textbook not set";
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

		case 'setNewTextbook':
			if(isTheseParametersAvailable(array('unit', 'class', 'category', 'type'))){
				$Unit = $_POST['unit'];
				$Class = $_POST['class'];
				$Category = $_POST['category'];
				$Type = $_POST['type'];

				date_default_timezone_set('PRC');
				$today = date("Y-m-d", time());
				$tomarrow = date("Y-m-d", strtotime(" 1 day"));
				$stmt = $conn->prepare("INSERT INTO `date` (unit, class, startYmd, endYmd, category, type) VALUES (?, ?, ?, ?, ?, ?)");
				$stmt->bind_param("ssssss", $Unit, $Class, $today, $tomarrow, $Category, $Type);
				
				if($stmt->execute()){
					$response['error'] = false;
					$response['message'] = "建立成功";
					$response['startDate'] = $today;
					$response['endDate'] = $tomarrow;
				}
				else{
					$response['error'] = true;
					$response['message'] = "set new textbook fail";
				}
			}
			else{
				$response['error'] = true;
				$response['message'] = "new testbook set error";
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

		case 'learningTimes': //完成練習
			if(isTheseParametersAvailable(array('user_id', 'unit', 'type', 'category'))){
				$user_id = $_POST['user_id'];
				$unit = $_POST['unit'];
				$type = $_POST['type']; //vocabulary or sentence or phrase
				$category = $_POST['category']; //listen_p or listen_c ...

				switch($category){
					case 'listen_p':
						$stmt = $conn->prepare("UPDATE history SET listen_p = listen_p + 1 WHERE user_id = ? AND unit = ? AND type = ?");
						$stmt->bind_param("sss",$user_id,$unit,$type);
						$stmt->execute();
						$stmt->close();

						break;
					case 'speak_p':
						$stmt = $conn->prepare("UPDATE history SET speak_p = speak_p + 1 WHERE user_id = ? AND unit = ? AND type = ?");
						$stmt->bind_param("sss",$user_id,$unit,$type);
						$stmt->execute();
						$stmt->close();
						break;
				}
				$response['error'] = false;
				$response['message'] = 'learningTimes successful';	
			}
			else{
				$response['error'] = true;
				$response['message'] = 'learningTimes php error.';
			}
			break;

		case 'testScore':
			if(isTheseParametersAvailable(array('user_id','unit','type','category','score'))){
				$user_id = $_POST['user_id'];
				$unit = $_POST['unit'];
				$type = $_POST['type']; //vocabulary or sentence or phrase
				$category = $_POST['category'];
				$score = $_POST['score'];
				switch($category){
					case 'listen_c':
						$getPrevScore = $conn->prepare("SELECT listen_c FROM history WHERE user_id = ? AND unit = ? AND type = ?");
						$getPrevScore->bind_param("sss",$user_id,$unit,$type);
						$getPrevScore->execute();
						$getPrevScore->store_result();
						if($getPrevScore->num_rows > 0){
							$getPrevScore->bind_result($prevScore);
							$getPrevScore->fetch();
						}
						else{
							$prevScore = -1;
						}
					
						if($score > $prevScore){
							$getPrevScore->close();
							$stmt = $conn->prepare("UPDATE history SET listen_c = $score WHERE user_id = ? AND unit = ? AND type = ?");
							$stmt->bind_param("sss",$user_id,$unit,$type);
							$stmt->execute();
							$stmt->close();
						}
					
						$getCounts = $conn->prepare("SELECT id FROM personal_history WHERE user_id = ? AND unit = ? AND type = ? AND category = ?");
						$getCounts->bind_param("ssss", $user_id, $unit, $type, $category);
						$getCounts->execute();
						$getCounts->store_result();
						$myCounts = $getCounts->num_rows;
						$getCounts->close();
						//記錄每次成績
						$stmt2 = $conn->prepare("INSERT INTO `personal_history` (`id`, `user_id`, `unit`, `type`, `category`, `counts`, `score`) VALUES (NULL, ?, ?, ?, ?, ?, ?)");
						$stmt2->bind_param("ssssss",$user_id,$unit,$type,$category,$myCounts,$score);
						$stmt2->execute();
						break;

					case 'speak_c':
						$getPrevScore = $conn->prepare("SELECT listen_c FROM history WHERE user_id = ? AND unit = ? AND type = ?");
						$getPrevScore->bind_param("sss",$user_id,$unit,$type);
						$getPrevScore->execute();
						$getPrevScore->store_result();
						if($getPrevScore->num_rows > 0){
							$getPrevScore->bind_result($prevScore);
							$getPrevScore->fetch();
						}
						else{
							$prevScore = -1;
						}
					
						if($score > $prevScore){
							$getPrevScore->close();
							$stmt = $conn->prepare("UPDATE history SET speak_c = $score WHERE user_id = ? AND unit = ? AND type = ?");
							$stmt->bind_param("sss",$user_id,$unit,$type);
							$stmt->execute();
							$stmt->close();
						}

						$getCounts = $conn->prepare("SELECT id FROM personal_history WHERE user_id = ? AND unit = ? AND type = ? AND category = ?");
						$getCounts->bind_param("ssss", $user_id, $unit, $type, $category);
						$getCounts->execute();
						$getCounts->store_result();
						$myCounts = $getCounts->num_rows;
						$getCounts->close();
						//score?
						$stmt2 = $conn->prepare("INSERT INTO `personal_history` (`id`, `user_id`, `unit`, `type`, `category`, `counts`, `score`) VALUES (NULL, ?, ?, ?, ?, ?, ?)");
						$stmt2->bind_param("ssssss",$user_id,$unit,$type,$category,$myCounts,$score);
						$stmt2->execute();
						break;
				}
				$response['error'] = false;
				$response['message'] = 'testScore successful';
			}
			else{
				$response['error'] = true;
				$response['message'] = 'testScore unsuccessful';	
			}
			break;
		case 'uploadError':
			if(isTheseParametersAvailable(array('user_id', 'category', 'type', 'unit', 'en'))){
				$user_id = $_POST['user_id'];
				$category = $_POST['category'];
				$type = $_POST['type'];
				$unit = $_POST['unit'];
				$en = $_POST['en'];

				//第幾次測驗
				$getCounts = $conn->prepare("SELECT id FROM personal_history WHERE user_id = ? AND unit = ? AND type = ? AND category = ?");
				$getCounts->bind_param("ssss", $user_id, $unit, $type, $category);
				$getCounts->execute();
				$getCounts->store_result();
				$myCounts = $getCounts->num_rows;
				$myCounts = $myCounts - 1;
				$getCounts->close();

				$stmt = $conn->prepare("INSERT INTO error (user_id, category, type, unit, counts, en) VALUES (?, ?, ?, ?, ?, ?)");
				$stmt->bind_param("ssssss", $user_id, $category, $type, $unit, $myCounts, $en);
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

				$stmt = $conn->prepare("SELECT counts, en FROM error WHERE user_id = ? AND unit = ? AND category = ? AND type = ?");
				$stmt->bind_param("ssss", $user_id, $unit, $category, $type);
				$stmt->execute();
				$stmt->store_result();

				if($stmt->num_rows > 0){
					$response['row'] = $stmt->num_rows;
					$count = $stmt->num_rows;
					for($i=0; $i<$count; $i++){
						$stmt->bind_result($counts,$en);
						$stmt->fetch();
						$errorText = array(
							'counts' => $counts,
							'text' => $en
						);

						$response[$i] = $errorText;
					}

				}
				else{
					$response['row'] = 0;
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

		case 'downloadEveryScore':
			if(isTheseParametersAvailable(array('user_id', 'unit', 'category', 'type'))){
				$user_id = $_POST['user_id'];
				$unit = $_POST['unit'];
				$category = $_POST['category'];
				$type = $_POST['type'];

				$stmt = $conn->prepare("SELECT counts, score FROM personal_history WHERE user_id = ? AND unit = ? AND category = ? AND type = ?");
				$stmt->bind_param("ssss", $user_id, $unit, $category, $type);
				$stmt->execute();
				$stmt->store_result();

				if($stmt->num_rows > 0){
					$response['row'] = $stmt->num_rows;
					$count = $stmt->num_rows;
					for($i=0; $i<$count; $i++){
						$stmt->bind_result($counts,$score);
						$stmt->fetch();
						$result = array(
							'counts' => $counts,
							'score' => $score
						);

						$response[$i] = $result;
					}

				}
				else{
					$response['row'] = 0;
					$response[0] = "無";
				}

				$response['error'] = false;
				$response['message'] = 'downloadEveryScore successful';

			}
			else{

				$response['error'] = true;
				$response['message'] = 'downloadEveryScore have some problem';
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

		

		case 'initStudentHistory' :
			if(isTheseParametersAvailable(array('class', 'unit', 'type'))){
				$classnum = $_POST['class'];
				$unit = $_POST['unit'];
				$Type = $_POST['type'];
				
				$stmt = $conn->prepare("SELECT id FROM history WHERE myclass = ? AND unit = ? AND type = ?");
				$stmt->bind_param('sss',$classnum, $unit, $Type);
				$stmt->execute();
				$stmt->store_result();
				$count = $stmt->num_rows;
				if($count > 0){
					$response['error'] = false;
					$response['message'] = 'student history has init';
				}
				else{
					$stmt2 = $conn->prepare("SELECT id FROM users WHERE myclass = ? AND identity = 'student'");
					$stmt2->bind_param('s', $classnum);
					$stmt2->execute();
					// $response['t'] = 't';
					$stmt2->store_result();
					$count2 = $stmt2->num_rows;

					if($count2 > 0){
						for($i=0; $i<$count2; $i++){
							$stmt2->bind_result($stuID);
							$stmt2->fetch();
			
							$stmt3 = $conn->prepare("INSERT INTO `history` (`id`, `user_id`, `myclass`, `unit`, `listen_p`, `speak_p`, `listen_c`, `speak_c`, `type`) VALUES (NULL, ?, ?, ?, '0', '0', '-1', '-1', ?)");
							$stmt3->bind_param('ssss', $stuID, $classnum, $unit, $Type);
							$stmt3->execute();

							// //personal_history
							$stmt4 = $conn->prepare("INSERT INTO `personal_history` (`id`, `user_id`, `unit`, `type`, `category`, `counts`, `score`) VALUES (NULL, ?, ?, ?, 'listen_p', '0', '-1')");
							$stmt4->bind_param('sss', $stuID, $unit, $Type);
							$stmt4->execute();

							$stmt5 = $conn->prepare("INSERT INTO `personal_history` (`id`, `user_id`, `unit`, `type`, `category`, `counts`, `score`) VALUES (NULL, ?, ?, ?, 'listen_c', '0', '-1')");
							$stmt5->bind_param('sss', $stuID, $unit, $Type);
							$stmt5->execute();

							$stmt6 = $conn->prepare("INSERT INTO `personal_history` (`id`, `user_id`, `unit`, `type`, `category`, `counts`, `score`) VALUES (NULL, ?, ?, ?, 'speak_p', '0', '-1')");
							$stmt6->bind_param('sss', $stuID, $unit, $Type);
							$stmt6->execute();

							$stmt7 = $conn->prepare("INSERT INTO `personal_history` (`id`, `user_id`, `unit`, `type`, `category`, `counts`, `score`) VALUES (NULL, ?, ?, ?, 'speak_c', '0', '-1')");
							$stmt7->bind_param('sss', $stuID, $unit, $Type);
							$stmt7->execute();

						}
						$response['error'] = false;
						$response['message'] = 'init student history successful';
					}
					else{
						$response['error'] = true;
						$response['message'] = 'init student history get list error';
					}
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
