const axios = require("axios");
const CryptoJS = require("crypto-js");

// Key & Secret
const API_KEY = ""; // put your lalamove API key here
const SECRET = ""; // put your lalamove API secret here

axios.defaults.baseURL = "https://rest.sandbox.lalamove.com"; // URL to Lalamove Sandbox API
const time = new Date().getTime().toString();
const region = "SG_SIN";
const method = "POST";
const path = "/v3/orders";

const body = JSON.stringify({
  data: {
    quotationId: "1584431938633158698", // Quotation ID from quotation response
    sender: {
      stopId: "1584431939304247307", // Stop Id of the pickup point from quotation response
      name: "test",
      phone: "+651001234567",
    },
    recipients: [
      {
        stopId: "1584431939304247308", // Stop Id of dropoff point from quotation response
        name: "dodo",
        phone: "+651001234567",
        remarks: "Do not take this order - SANDBOX CLIENT TEST"
      }
    ],
  }
});

const rawSignature = `${time}\r\n${method}\r\n${path}\r\n\r\n${body}`;
const SIGNATURE = CryptoJS.HmacSHA256(rawSignature, SECRET).toString();
const startTime = new Date().getTime();

axios
  .post(path, body, {
    headers: {
      "Content-type": "application/json; charset=utf-8",
      "Authorization": `hmac ${API_KEY}:${time}:${SIGNATURE}`,
      "Accept": "application/json",
      "Market": region,
    },
  })
  .then((result) => {
    console.log(
      "Total elapsed http request/response time in milliseconds: ",
      new Date().getTime() - startTime
    );
    console.log(
      "Authorization header: ",
      `hmac ${API_KEY}:${time}:${SIGNATURE}`
    );
    console.log("Status Code: ", result.status);
    console.log("Returned data: ", result.data);
  });