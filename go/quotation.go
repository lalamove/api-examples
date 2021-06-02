package main

import (
	"bytes"
	"crypto/hmac"
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"io/ioutil"
	"net/http"
	"strconv"
	"strings"
	"time"
)

func main() {
	key := ""    // put your lalamove API key here
	secret := "" // put your lalamove API secret here

	BaseUrl := "https://rest.sandbox.lalamove.com"
	path := "/v2/quotations"
	region := "SG_SIN"
	method := "POST"
	currentTimeStamp := strconv.FormatInt(time.Now().UnixNano()/int64(time.Millisecond), 10)
	body := `{
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
						"country": "SG_SIN"
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
						"country": "SG_SIN"
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
	}`

	var buf bytes.Buffer
	buf.WriteString(currentTimeStamp)
	buf.WriteString("\r\n")
	buf.WriteString(method)
	buf.WriteString("\r\n")
	buf.WriteString(path)
	buf.WriteString("\r\n")
	buf.WriteString("\r\n")
	buf.WriteString(body)

	h := hmac.New(sha256.New, []byte(secret))
	h.Write(buf.Bytes())

	req, err := http.NewRequest(
		method,
		BaseUrl+path,
		strings.NewReader(body),
	)
	if err != nil {
		fmt.Println(err)
		return
	}
	startTime := time.Now().UTC()
	token := "hmac " + key + ":" + currentTimeStamp + ":" + hex.EncodeToString(h.Sum(nil))
	req.Header.Add("Content-Type", "application/json")
	req.Header.Add("Authorization", token)
	req.Header.Add("X-LLM-Country", region)
	client := &http.Client{}
	res, err := client.Do(req)
	if err != nil {
		fmt.Println(err)
		return
	}
	if res != nil {
		defer res.Body.Close()
	}
	var resBody []byte
	resBody, err = ioutil.ReadAll(res.Body)
	if err != nil {
		fmt.Println(err)
		return
	}

	fmt.Println("Total elapsed http request/response time in milliseconds: ", time.Now().UTC().Sub(startTime).Nanoseconds()/(int64(time.Millisecond)/int64(time.Nanosecond)))
	fmt.Println("Authorization header: " + token)
	fmt.Println("Status Code: ", res.StatusCode)
	fmt.Println("Returned data: " + string(resBody))
}
