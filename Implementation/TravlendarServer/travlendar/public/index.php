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

$container['db'] = function ($c) {
    $db = $c['settings']['db'];
    $pdo = new PDO("mysql:host=" . $db['host'] . ";dbname=" . $db['dbname'], $db['user'], $db['pass']);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
    return $pdo;
};
// get dependency with:     $this->db


$renderer = new Views\PhpRenderer( __DIR__ . '/vendor/chadicus/slim-oauth2-routes/templates');
// add authorization methods
$app->map(['GET', 'POST'], Routes\Authorize::ROUTE, new Routes\Authorize($server, $renderer))->setName('authorize');
$app->post(Routes\Token::ROUTE, new Routes\Token($server))->setName('token');
//$app->map(['GET', 'POST'], Routes\ReceiveCode::ROUTE, new Routes\ReceiveCode($renderer))->setName('receive-code');

$authorization = new Middleware\Authorization($server, $app->getContainer());





// ROUTES
$app->get('/hello/{name}', function (Request $request, Response $response) {
    $name = $request->getAttribute('name');
    $response->getBody()->write("Hello, $name");

    return $response;
});

$app->group('/api', function () use ($app) {

    $app->post('/register', function ($request, $response) {
        return \App\Controllers\AuthenticationController::Register($request, $response, $this->db);
    });

    
    $app->group('/user', function () use ($app) {

        $app->post('/profile', function ($request, $response) {
            return \App\Controllers\UserController::Profile($request, $response, $this->db);
        });

    });

})->add($authorization);


$app->run();