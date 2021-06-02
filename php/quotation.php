<?php
// Put your key and secret here
$key = ""; // put your lalamove API key here
$secret = ""; // put your lalamove API secret here

$time = time() * 1000;

$baseURL = "https://rest.sandbox.lalamove.com"; // URl to Lalamove Sandbox API
$method = 'POST';
$path = '/v2/quotations';
$region = 'SG_SIN';

// Please, find information about body structure and passed values here https://developers.lalamove.com/#get-quotation
$body = '{
    "serviceType": "MOTORCYCLE",
    "specialRequests": [],
    "requesterContact": {
        "name": "test",
        "phone": "0899183138"
    },
    "stops": [
        {
            "location": {
                "lat": "1.3140113",
                "lng": "103.8807331"
            },
            "addresses": {
                "en_SG": {
                    "displayString": "Lorong 23 Geylang, Singapore Badminton Hall, Singapore",
                    "country": "'.$region.'"
                }
            }
        },
        {
            "location": {
                "lat": "1.2966147",
                "lng": "103.8485095"
            },
           "addresses": {
               "en_SG": {
                   "displayString": "Stamford Road, National Museum of Singapore, Singapore",
                   "country": "'.$region.'"
               }
           }
        }
   ],
   "deliveries": [
        {
            "toStop": 1,
            "toContact": {
                "name": "dodo",
                "phone": "+660923447537"
            },
           "remarks": "Do not take this order - SANDBOX CLIENT TEST"
        }
   ]
}';

$rawSignature = "{$time}\r\n{$method}\r\n{$path}\r\n\r\n{$body}";
$signature = hash_hmac("sha256", $rawSignature, $secret);
$startTime = microtime(true);
$token = $key.':'.$time.':'.$signature;

$curl = curl_init();
curl_setopt_array($curl, array(
    CURLOPT_URL => $baseURL.$path,
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 3,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HEADER => false, // Enable this option if you want to see what headers Lalamove API returning in response
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'POST',
    CURLOPT_POSTFIELDS => $body,
    CURLOPT_HTTPHEADER => array(
        "Content-type: application/json; charset=utf-8",
        "Authorization: hmac ".$token, // A unique Signature Hash has to be generated for EVERY API call at the time of making such call.
        "Accept: application/json",
        "X-LLM-Country: {$region}" // Please note to which city are you trying to make API call
    ),
));

$response = curl_exec($curl);
$httpCode = curl_getinfo($curl, CURLINFO_HTTP_CODE);
curl_close($curl);

echo "Total elapsed http request/response time in milliseconds: ".floor((microtime(true) - $startTime)*1000)."\r\n";
echo "Authorization: hmac ".$token."\r\n";
echo 'Status Code: '. $httpCode."\r\n";
echo 'Returned data: '.$response."\r\n";
