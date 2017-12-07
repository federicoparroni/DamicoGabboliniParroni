<?php

namespace App\Controllers;

use OAuth2\Storage;
use \PDO;

class UserController
{
    
    public static function Profile($request, $response, $db) {
        $data = $request->getParsedBody();
        
        if(isset($data['email'], $data['password'])) {
            $email = filter_var($data['email'], FILTER_SANITIZE_STRING);
            $password = filter_var($data['password'], FILTER_SANITIZE_STRING);

            $emailESC = $db->quote($email);
            // check if user already exists
            if($db->query("SELECT COUNT(*) FROM oauth_users WHERE username=$emailESC")->fetchColumn() == 1) {

                // return user profile
                $statement = $db->prepare("SELECT * FROM Users WHERE email = :mail");
                $statement->execute(array('mail' => $email));
                $result = $statement->fetchAll(PDO::FETCH_ASSOC)[0];

                return $response->withJson(array( 'success' => true, 'user' => json_encode($result) ), 200);            
            } else {
                return $response->withJson(array( 'success' => false, 'message' => 'User email already existing' ), 400);
            }
        } else {
            return $response->withJson(array( 'success' => false, 'message' => 'Incorrect email or password' ), 400);
        }
    }


}