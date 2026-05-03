<?php

ini_set('error_reporting', E_ALL | E_STRICT);
ini_set('log_errors', 'on');
ini_set('display_errors', 'off');
//ini_set('error_log', __DIR__.'proxy-errors.log');

$CONFIG = [
    /** Regex pattern for the HOST part of the request URL that needs to be replaced */
    "redirect_from" => "/(http|https):\/\/[^\/?&]+\/([^\/?&]+)/i",
    /** Regex for the host part of the url to replace with: */
    "redirect_to" => "$1://$2",

    // optional parameters below can be replaced with null or left empty

    /**
     * Preflight response:
     * "*", "https://abc.com" or $_SERVER['HTTP_ORIGIN'],
     */
    "cors_origin" => $_SERVER['HTTP_ORIGIN'] ?? "*",
    "cors_headers" => "Origin, Content-Type, X-Auth-Token",
    "cors_methods" => "GET, POST, PATCH, PUT, DELETE, OPTIONS",

    /**
     *  Modify request to the target
     */
    "request" => function ($method, $get, $post, $body, $headers) {
        //here you can modify request to the target
        return [$method, $get, $post, $body, $headers];
    },
    /** Modify response to the client */
    "response" => function ($headers, $body, $status) {
        return [$headers, $body, $status];
    }
];

$method = $_SERVER['REQUEST_METHOD'];
$req_url = $_SERVER['SCRIPT_URI'] ?? ($_SERVER['REQUEST_SCHEME'] ?? "http") . '://' . $_SERVER["HTTP_HOST"] . $_SERVER["REQUEST_URI"];
$get = $_GET;
$post = $_POST;
$body = file_get_contents('php://input');

$headers = getRequestHeaders();

// respond to preflights
if ($CONFIG['cors_origin'] && $method == 'OPTIONS') {
    // return only the headers and not the content
    header('Access-Control-Allow-Origin: ' . $CONFIG['cors_origin']);
    header('Access-Control-Allow-Headers: ' . $CONFIG['cors_headers']);
    header('Access-Control-Allow-Methods: ' . $CONFIG['cors_methods']);
    exit;
}


$targetUrl = strtok(preg_replace($CONFIG['redirect_from'], $CONFIG['redirect_to'], $req_url), "?");

if (!preg_match("/^(http|https):\/\//", $targetUrl)) {
    response(400, "bad request");
}
if (parse_url($targetUrl, PHP_URL_HOST) != "api.clickup.com") {
    response(403, "forbidden target");
}

$targetMethod = $method;
$targetGet = $get;
$targetPost = $post;
$targetHeaders = $headers;
$targetBody = $body;
//unset($targetHeaders['Host']);

//Modify request if request config value is callable
if (is_callable($CONFIG['request'])) {
    $processedRequest = call_user_func($CONFIG['request'], $targetMethod, $targetGet, $targetPost, $targetBody, $targetHeaders);
    $targetMethod = $processedRequest[0];
    $targetGet = $processedRequest[1];
    $targetPost = $processedRequest[2];
    $targetBody = $processedRequest[3];
    $targetHeaders = $processedRequest[4];
}

$targetResponseBody = httpRequest(
    $targetUrl,
    $targetMethod,
    $targetGet,
    $targetPost,
    $targetBody,
    $targetHeaders,
    $targetResponseStatus, //out
    $targetResponseHeaders //out
);

$responseBody = $targetResponseBody;
$responseStatus = $targetResponseStatus;
$responseHeaders = $targetResponseHeaders;

if (is_callable($CONFIG['response'])) {
    $processedResponse = call_user_func($CONFIG['response'], $targetResponseHeaders, $targetResponseBody, $targetResponseStatus);
    $responseHeaders = $processedResponse[0];
    $responseBody = $processedResponse[1];
    $responseStatus = $processedResponse[2];
}

if ($responseStatus == 0) {
    $responseStatus = 500;
}
//remove transfer-encoding header that is set up automatically
unset($responseHeaders["transfer-encoding"]);

//Set headers for response to the client
foreach ($responseHeaders as $key => $header) {
    header($key . ': ' . $header);
}
//respond client
response($responseStatus, $responseBody);
exit;


function response($status, $data)
{
    http_response_code($status);
    echo $data;
    exit();
}

/**
 * @returns array [key=>value]
 */
function getRequestHeaders(): array
{
    $headers = array();
    foreach ($_SERVER as $key => $value) {
        if (substr($key, 0, 5) <> 'HTTP_') {
            continue;
        }
        $header = str_replace(' ', '-', ucwords(str_replace('_', ' ', strtolower(substr($key, 5)))));
        $headers[$header] = $value;
    }
    if (isset($_SERVER["CONTENT-TYPE"])) {
        $headers["Content-Type"] = $_SERVER["CONTENT-TYPE"];
    }
    if (isset($_SERVER["REDIRECT_HTTP_AUTHORIZATION"])) {
        $headers["Authorization"] = $_SERVER["REDIRECT_HTTP_AUTHORIZATION"];
    }
    return $headers;
}

/**
 * @param string $url
 * @param string $method - POST, GET
 * @param array $queryArray [key=>value]
 * @param array $post
 * @param null $body
 * @param array|null $headers [key=>value]
 * @param int|null &$status -  response status code
 * @param null $returnHeaders
 * @return string|null
 */
function httpRequest(string $url, string $method, array $queryArray, array $post = [],
                            $body = null, array $headers = null, int &$status = null, &$returnHeaders = null)
{
    $request = curl_init();
    try {
        switch ($method) {
            case "PUT":
            case 'DELETE':
            case 'OPTIONS':
            case 'POST':
                curl_setopt($request, CURLOPT_URL, $url . ($queryArray ? "?" . http_build_query($queryArray) : ""));
                if (count($post) > 0) {
                    curl_setopt($request, CURLOPT_POST, 1);
                    curl_setopt($request, CURLOPT_POSTFIELDS, $post);
                    file_put_contents("server_dump_request.log", "\nfuck you\n", FILE_APPEND);
                    file_put_contents("server_dump_request.log", "\n" . $headers['Content-Type'] . "\n", FILE_APPEND);
                    file_put_contents("server_dump_request.log", "\n" . $headers['Content-Length'] . "\n", FILE_APPEND);
                    file_put_contents("server_dump_request.log", "\n" . $body . "\n", FILE_APPEND);
                    // these will be auto-calculated
                    unset($headers['Content-Type']);
                    unset($headers['Content-Length']);
                } else {
                    curl_setopt($request, CURLOPT_CUSTOMREQUEST, $method);
                    curl_setopt($request, CURLOPT_POSTFIELDS, $body);
                }
                break;
            case 'GET':
            default:
                curl_setopt_array($request, array(
                    CURLOPT_URL => $url . ($queryArray ? "?" . http_build_query($queryArray) : ""),
                ));
        }


        $k_headers = [];
        if (!empty($headers)) {
            foreach ($headers as $key => $value) {
                $k_headers[] = $key . ": " . $value;
            }
            $k_headers[] = 'Content-Type: application/json';
        }
        curl_setopt($request, CURLOPT_HTTPHEADER, $k_headers);
        curl_setopt($request, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($request, CURLOPT_HEADER, true);

        //$returnHeaders = [];
        curl_setopt($request, CURLOPT_HEADERFUNCTION,
            function ($curl, $header) use (&$returnHeaders) {
                $len = strlen($header);
                $header = explode(':', $header, 2);
                if (count($header) < 2) // ignore invalid headers
                    return $len;

                $returnHeaders[strtolower(trim($header[0]))] = trim($header[1]);

                return $len;
            }
        );

        $response = curl_exec($request);
        $status = curl_getinfo($request, CURLINFO_HTTP_CODE);
        //list($returnHeaders, $body) = explode("\r\n\r\n", $response, 2);
        $header_size = curl_getinfo($request, CURLINFO_HEADER_SIZE);
        //$returnHeaders = substr($response, 0, $header_size);
        $body = substr($response, $header_size);

        curl_close($request);

        return $body;
    } catch (Exception $ex) {
        return null;
    }
}
