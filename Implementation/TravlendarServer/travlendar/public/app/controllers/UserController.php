<?php

namespace App\Controllers;

use OAuth2\Storage;
use \PDO;

class UserController
{
    
    public static function Profile($request, $response, $db) {
        $user = UserController::CheckUserCredentials($request, $response, $db);
        
        if($user) {
            return $response->withJson(array(   'success' => true, 
                                                'data' => $user
                ), 200);
        } else {
            return $response->withJson(array( 'success' => false, 'message' => 'Incorrect email or password' ), 400);
        }
    }

    public static function CheckUserCredentials($request, $response, $db) {
        $data = $request->getParsedBody();
        
        if(isset($data['email'], $data['password'])) {
            $email = filter_var($data['email'], FILTER_SANITIZE_STRING);
            $password = filter_var($data['password'], FILTER_SANITIZE_STRING);

            $emailESC = $db->quote($email);
            // check if user exists
            $result = $db->query("SELECT * FROM Users WHERE email=$emailESC");
            if($result->rowCount() == 1) {
                $row = $result->fetch(\PDO::FETCH_OBJ);
                return $row;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    
}