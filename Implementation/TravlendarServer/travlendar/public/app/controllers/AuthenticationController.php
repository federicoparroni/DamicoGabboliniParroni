<?php

namespace App\Controllers;

class AuthenticationController
{
    /**
     * It's only a validation example!
     * You should search user (on your database) by authorization token
     */

    /* public function getUserByToken($token)
    {
        if ($token != 'usertokensecret') {
            
            //The throwable class must implement UnauthorizedExceptionInterface
             
            throw new UnauthorizedException('Invalid Token');
        }
        $user = [
            'name' => 'Dyorg',
            'id' => 1,
            'permisssion' => 'admin'
        ];
        return $user;
    } */


    public static function Register($request, $response, $db) {
        $data = $request->getParsedBody();
        
        if(isset($data['email'], $data['password'])) {
            $email = filter_var($data['email'], FILTER_SANITIZE_STRING);
            $password = filter_var($data['password'], FILTER_SANITIZE_STRING);

            $emailESC = $db->quote($email);
            // check if user already exists
            if($db->query("SELECT COUNT(*) FROM oauth_users WHERE username=$emailESC")->fetchColumn() == 0) {
                // create new user in db
                $passESC = $db->quote($password);
                
                $storage = new Storage\Pdo($db);
                $storage->setUser($email, $password);

                $statement = $db->prepare("UPDATE oauth_users SET email = :mail WHERE username = :user");
                $statement->execute(array('mail' => $email, 'user' => $email));
                
                return $response->withJson(array( 'success' => true, 'message' => 'User successfully created!' ), 201);            
            } else {
                return $response->withJson(array( 'success' => false, 'message' => 'User email already existing' ), 400);
            }
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
            }
        } else {
            return null;
        }
    }

    public static function Profile($request, $response, $db) {
        $user = AuthenticationController::CheckUserCredentials($request, $response, $db);
        
        if($user) {
            return $response->withJson(array(   'success' => true, 
                                                'data' => $user
            ), 200);
        } else {
            return $response->withJson(array( 'success' => false, 'message' => 'Wrong credentials' ), 400);
        }
    }

    public static function Logout($request, $response, $args) {
        // do stuff & return $response
    }
}