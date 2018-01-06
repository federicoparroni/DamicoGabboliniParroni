<?php

namespace App\Controllers;

use OAuth2\Storage;

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

            if (filter_var($email, FILTER_VALIDATE_EMAIL)) {
                if(trim($password) != '') {

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
                    // invalid password
                    return $response->withJson(array( 'success' => false, 'message' => 'Incorrect password' ), 400);
                }
            } else {
                // invalid username
                return $response->withJson(array( 'success' => false, 'message' => 'Incorrect email' ), 400);
            }
        } else {
            return $response->withJson(array( 'success' => false, 'message' => 'Incorrect email or password' ), 400);
        }
    }

    
}