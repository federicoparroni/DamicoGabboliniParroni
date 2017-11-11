<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;

require '../vendor/autoload.php';

// CONFIGURATION SETTINGS
$config['displayErrorDetails'] = true;
$config['addContentLengthHeader'] = false;
// dummy data
$config['db']['host']   = "localhost";
$config['db']['user']   = "user";
$config['db']['pass']   = "password";
$config['db']['dbname'] = "exampleapp";

$app = new \Slim\App(["settings" => $config]);


// DEPENDENCIES
$container = $app->getContainer();



// ROUTES
$app->get('/hello/{name}', function (Request $request, Response $response) {
    $name = $request->getAttribute('name');
    $response->getBody()->write("Hello, $name");

    return $response;
});

$app->post('/login', function (Request $request, Response $response) {
    $data = $request->getParsedBody();
    $email = filter_var($data['email'], FILTER_SANITIZE_STRING);
    $password = filter_var($data['password'], FILTER_SANITIZE_STRING);

    //$print = print_r($data);
    //$response->getBody()->write("$print");
    $response->getBody()->write("email: $email,\npassword: $password");
    return $response;
});

$app->run();