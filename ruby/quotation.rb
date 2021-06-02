require 'uri'
require 'time'
require 'json'
require 'openssl'
require 'net/http'
require 'net/https'

key = '' # put your lalamove API key here
secret = '' # put your lalamove API secret here

path = '/v2/quotations'
region = 'SG_SIN';
method = 'POST';
timestamp = Time.now.to_i*1000
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
                    "country": region
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
                    "country": region
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
}.to_json

rawSignature = "#{timestamp}\r\n#{method}\r\n#{path}\r\n\r\n#{body}"
signature = OpenSSL::HMAC.hexdigest("SHA256", secret, rawSignature)
startTime = Time.now

uri = URI('https://rest.sandbox.lalamove.com' + path)
https = Net::HTTP.new(uri.host, uri.port)
https.use_ssl = true
req = Net::HTTP::Post.new(uri.path, initheader = {
        'Content-Type' => 'application/json; charset=utf-8',
        'Authorization' => "hmac #{key}:#{timestamp}:#{signature}",
        'Accept' => 'application/json',
        'X-LLM-Country' => region
    }
)
req.body = body
res = https.request(req)
requestTime = (Time.now - startTime)*1000

puts "Total elapsed http request/response time in milliseconds: #{requestTime.to_i}"
puts "Authorization header: hmac #{key}:#{timestamp}:#{signature}"
puts "Status Code: #{res.code}"
puts "Returned data: #{res.body}"
