import requests
import time
import hmac
import hashlib
import json

key = ''  # put your lalamove API key here
secret = ''  # put your lalamove API key here

path = '/v2/quotations'
region = 'SG_SIN'
method = 'POST'
timestamp = int(round(time.time() * 1000))

body = {
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
                    "market": region
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
                    "market": region
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
}
rawSignature = "{timestamp}\r\n{method}\r\n{path}\r\n\r\n{body}".format(
    timestamp=timestamp, method=method, path=path, body=json.dumps(body))
signature = hmac.new(secret.encode(), rawSignature.encode(),
                     hashlib.sha256).hexdigest()
startTime = int(round(time.time() * 1000))
url = "https://rest.sandbox.lalamove.com"

headers = {
    'Content-type': 'application/json; charset=utf-8',
    'Authorization': "hmac {key}:{timestamp}:{signature}".format(key=key, timestamp=timestamp, signature=signature),
    'Accept': 'application/json',
    'X-LLM-Market': region
}
r = requests.post(url+path, data=json.dumps(body), headers=headers)

requestTime = (int(round(time.time() * 1000)) - startTime)
print("Total elapsed http request/response time in milliseconds: {}".format(requestTime))
print("Authorization header: hmac {key}:{timestamp}:{signature}".format(
    key=key, timestamp=timestamp, signature=signature))
print("Status Code: {}".format(r.status_code))
print("Returned data: {}".format(r.text))
