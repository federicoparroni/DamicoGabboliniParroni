<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;

use Slim\Views;
use OAuth2\Storage;
use OAuth2\GrantType;
use OAuth2\Storage\Pdo as PdoStorage;
use Chadicus\Slim\OAuth2\Routes;
use Chadicus\Slim\OAuth2\Middleware;

require_once '../vendor/autoload.php';

// CONFIGURATION SETTINGS
$config['displayErrorDetails'] = true;
$config['addContentLengthHeader'] = false;
// db config
$config['db']['host']   = "localhost";
$config['db']['dbname'] = "id3544103_travlendar";
$config['db']['user']   = "id3544103_travlendar";
$config['db']['pass']   = "travlendar";

// db connection
$pdo = new PDO("mysql:host=" . $config['db']['host'] . ";dbname=" . $config['db']['dbname'], $config['db']['user'], $config['db']['pass']);
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
$pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
$storage = new Storage\Pdo($pdo);

//$config['storage'] = $storage;

// create OAuth Storage inside the database
/* $storage = new PdoStorage($pdo);
foreach (explode(';', $storage->getBuildSql()) as $statement) {
    $result = $pdo->exec($statement);
} */

// oauth server 
$server = new OAuth2\Server(
    $storage,
    [
        'access_lifetime' => 3600,
        'allow_implicit' => true
    ],
    [
        new GrantType\ClientCredentials($storage),
        new GrantType\UserCredentials($storage),    // authenticate with username and password
        //new GrantType\AuthorizationCode($storage),
    ]
);

$app = new \Slim\App(["settings" => $config]);

// DEPENDENCIES
$container = $app->getContainer();
// dummy data
$container['user_repository'] = function ($c) {
    return [ "user1", "user2", "user3" ];
};

$container['db'] = function ($c) {
    $db = $c['settings']['db'];
    $pdo = new PDO("mysql:host=" . $db['host'] . ";dbname=" . $db['dbname'], $db['user'], $db['pass']);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
    return $pdo;
};
/* $container['storage'] = function ($c) {
    $db = $c['settings']['db'];
    $pdo = new PDO("mysql:host=" . $db['host'] . ";dbname=" . $db['dbname'], $db['user'], $db['pass']);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
    return new Storage\Pdo($pdo);
}; */

// get dependency with:     $this->db


$renderer = new Views\PhpRenderer( __DIR__ . '/vendor/chadicus/slim-oauth2-routes/templates');
// add authorization methods
$app->map(['GET', 'POST'], Routes\Authorize::ROUTE, new Routes\Authorize($server, $renderer))->setName('authorize');
$app->post(Routes\Token::ROUTE, new Routes\Token($server))->setName('token');
$app->map(['GET', 'POST'], Routes\ReceiveCode::ROUTE, new Routes\ReceiveCode($renderer))->setName('receive-code');

$authorization = new Middleware\Authorization($server, $app->getContainer());






// ROUTES
$app->get('/hello/{name}', function (Request $request, Response $response) {
    $name = $request->getAttribute('name');
    $response->getBody()->write("Hello, $name");

    return $response;
});

$app->group('/api', function () use ($app) {

    $app->post('/register', function ($request, $response) {
        $data = $request->getParsedBody();
        $email = filter_var($data['email'], FILTER_SANITIZE_STRING);
        $password = filter_var($data['password'], FILTER_SANITIZE_STRING);

        if($email && $password) {
            $db = $this->db;
            $emailESC = $db->quote($email);
            // check if user already exists
            if($db->query("SELECT COUNT(*) FROM oauth_users WHERE username=$emailESC")->fetchColumn() == 0) {
                // create new user in db
                $passESC = $db->quote($password);
                
                $storage = new Storage\Pdo($db);
                $storage->setUser($email, $password);

                $statement = $db->prepare("UPDATE oauth_users SET email = :mail WHERE username = :user");
                $statement->execute(array('mail' => $email, 'user' => $email));

                /*$statement = $conn->prepare("INSERT INTO Users VALUES(:mail, :pw)");
                $statement->execute(array("mail" => $email, "pw" => $password)); */
                
                return $response->withJson(array( 'success' => true, 'message' => 'User successfully created!' ), 201);            
            } else {
                return $response->withJson(array( 'success' => false, 'message' => 'User email already existing' ), 400);
            }
        } else {
            return $response->withJson(array( 'success' => false, 'message' => 'Incorrect email or password' ), 400);
        }
    });

    $app->post('/profile', function (Request $request, Response $response) {
        $data = $request->getParsedBody();
        $email = filter_var($data['email'], FILTER_SANITIZE_STRING);
        $password = filter_var($data['password'], FILTER_SANITIZE_STRING);
        
        if($email && $password) {
            $conn = $this->db;
            $emailESC = $conn->quote($email);
            $passESC = $conn->quote($password);
            
            if($conn->query("SELECT COUNT(*) FROM Users WHERE email=$emailESC and password=$passESC")->fetchColumn() == 1) {
                //session_start();
                //$_SESSION['user_id']= $email;

                return $response->withJson(array( 'success' => true, 'message' => 'Logged in successfully' ), 200);
            } else {
                return $response->withJson(array( 'success' => false, 'message' => 'Wrong credentials' ), 401);
            }

        } else {
            return $response->withJson(array( 'success' => false, 'message' => 'Email or password missing' ), 400);
        }
    });


    $app->get('/test', function ($request, $response) {
            
        return $response->withJson(array( 'success' => true, 'message' => 'You\'re authorized to access this API!' ), 201);            
        
    });

})->add($authorization);

$app->get('/api/getUser', function ($request, $response) {
    return $response->withJson($this->user_repository, 200);
})->add($authorization);


$app->run();