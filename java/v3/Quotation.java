import javax.crypto.Mac;
import java.net.URI;
import javax.crypto.spec.SecretKeySpec;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Quotation {
    public static void main(String[] args) throws Exception {

        final String key = ""; // put your lalamove API key here
        final String secret = ""; // put your lalamove API secret here
        final String baseURL = "https://rest.sandbox.lalamove.com"; // URl to Lalamove Sandbox API
        long timestamp = System.currentTimeMillis();
        String method = "POST";
        String path = "/v3/quotations";

        String body = "{\n" +
                "  \"data\": {\n" +
                "    \"scheduleAt\": \"2022-10-24T15:53:36.171Z\",\n" +
                "    \"serviceType\": \"MOTORCYCLE\",\n" +
                "    \"specialRequests\": [\n" +
                "\n" +
                "    ],\n" +
                "    \"language\": \"en_HK\",\n" +
                "    \"stops\": [\n" +
                "      {\n" +
                "        \"coordinates\": {\n" +
                "          \"lat\": \"22.3353139\",\n" +
                "          \"lng\": \"114.1758402\"\n" +
                "        },\n" +
                "        \"address\": \"Innocentre, 72 Tat Chee Ave, Kowloon Tong\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"coordinates\": {\n" +
                "          \"lat\": \"22.3203648\",\n" +
                "          \"lng\": \"114.169773\"\n" +
                "        },\n" +
                "        \"address\": \"13/F HOLLYWOOD PLAZA, Mong Kok\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
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
                .header("MARKET", "HK_HKG") // Please note to which city are you trying to make API call
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
