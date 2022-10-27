import javax.crypto.Mac;
import java.net.URI;
import javax.crypto.spec.SecretKeySpec;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Order {
    public static void main(String[] args) throws Exception {

        final String key = ""; // put your lalamove API key here
        final String secret = ""; // put your lalamove API secret here
        final String baseURL = "https://rest.sandbox.lalamove.com"; // URl to Lalamove Sandbox API
        long timestamp = System.currentTimeMillis();
        String method = "POST";
        String path = "/v2/orders";

        String body = "{" +
        "    \"serviceType\": \"MOTORCYCLE\"," +
        "    \"specialRequests\": []," +
        "    \"requesterContact\": {" +
        "        \"name\": \"test\"," +
        "        \"phone\": \"0899183138\"" +
        "    }," +
        "    \"stops\": [" +
        "        {" +
        "            \"location\": {" +
        "                \"lat\": \"1.3140113\"," +
        "                \"lng\": \"103.8807331\"" +
        "            }," +
        "            \"addresses\": {" +
        "                \"en_SG\": {" +
        "                    \"displayString\": \"Lorong 23 Geylang, Singapore Badminton Hall, Singapore\"," +
        "                    \"market\": \"SG_SIN\"" +
        "                }" +
        "            }" +
        "        }," +
        "        {" +
        "            \"location\": {" +
        "                \"lat\": \"1.2966147\"," +
        "                \"lng\": \"103.8485095\"" +
        "            }," +
        "            \"addresses\": {" +
        "                \"en_SG\": {" +
        "                    \"displayString\": \"Stamford Road, National Museum of Singapore, Singapore\"," +
        "                    \"market\": \"SG_SIN\"" +
        "                }" +
        "           }" +
        "        }" +
        "   ]," +
        "   \"deliveries\": [" +
        "        {" +
        "            \"toStop\": 1," +
        "            \"toContact\": {" +
        "                \"name\": \"dodo\"," +
        "                \"phone\": \"+660923447537\"" +
        "            }," +
        "           \"remarks\": \"Do not take this order - SANDBOX CLIENT TEST\"" +
        "        }" +
        "    ]," +
        "    \"quotedTotalFee\": {" +
        "        \"amount\": \"11.80\"," + // this is value from Quotation response, update it if needed
        "        \"currency\": \"SGD\"" +
        "    }" +
        "}";

        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        Mac hmac256 = Mac.getInstance("HmacSHA256");
        hmac256.init(secret_key);

        String rawSignature = timestamp + "\r\n" + method + "\r\n" + path + "\r\n\r\n" + body;
        byte[] signatureRaw = hmac256.doFinal(rawSignature.getBytes("UTF-8"));
        StringBuilder buf = new StringBuilder();
        for (byte item : signatureRaw) {
            buf.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }

        long startTime = System.currentTimeMillis();
        String token = key + ':' + timestamp + ':' + buf.toString().toLowerCase();
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .uri(URI.create(baseURL + path))
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Accept", "application/json")
            .header("Authorization", "hmac " + token) // A unique Signature Hash has to be generated for EVERY API call at the time of making such call.
            .header("X-LLM-Market", "SG_SIN") // Please note to which city are you trying to make API call
            .build();

        HttpResponse<String> response = null;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Total elapsed http request/response time in milliseconds: " + elapsedTime);
        System.out.println("Authorization header: hmac " + token);
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }
}
