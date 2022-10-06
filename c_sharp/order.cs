using System;
using System.Net;
using System.IO;
using System.Security.Cryptography;
using System.Text;

public class Order
{
    private static long ToUnixTimestamp(long time)
    {
        return (time - 621355968000000000) / TimeSpan.TicksPerMillisecond;
    }
    
    private static string HMACSHA256(string message, string key)
    {
        HMACSHA256 hmac = new HMACSHA256(Encoding.UTF8.GetBytes(key));
        hmac.Initialize();
        byte[] buffer = Encoding.UTF8.GetBytes(message);
        string signature = BitConverter.ToString(hmac.ComputeHash(buffer)).Replace("-", "").ToLower();
        return signature;
    }
    
    private static void PrintOutHttpResponse(WebResponse response, long startTime, string token)
    {
        HttpWebResponse httpResponse = (HttpWebResponse)response;
        Stream dataStream = response.GetResponseStream();
        StreamReader reader = new StreamReader(dataStream);
        string responseFromServer = reader.ReadToEnd();
        
        long endTime = ToUnixTimestamp(DateTime.Now.Ticks);
        long requestTime = endTime - startTime;
        
        // Get HTTP response
        Console.WriteLine("Total elapsed http request/response time in milliseconds: {0}", requestTime);
        Console.WriteLine("Authorization header: {0}", token);
        Console.WriteLine("Status Code: {0}", (int)httpResponse.StatusCode);
        Console.WriteLine("Returned data: {0}", responseFromServer);
        
        reader.Close();
        dataStream.Close();
        response.Close();
    }
    
    public static void Main(string[] args)
    {
        // Get information
        string key = "";    // put your lalamove API key here
        string secret = ""; // put your lalamove API secret here
        string baseUrl = "https://rest.sandbox.lalamove.com";
        string path = "/v2/orders";
        string region = "SG_SIN";
        string method = "POST";
        string time = ToUnixTimestamp(DateTime.Now.Ticks).ToString();
        string body = "{\"serviceType\": \"MOTORCYCLE\", \"specialRequests\": [], \"requesterContact\": {\"name\": \"test\", \"phone\": \"0899183138\"}, \"stops\": [{\"location\": {\"lat\": \"1.3140113\", \"lng\": \"103.8807331\"}, \"addresses\": {\"en_SG\": {\"displayString\": \"Lorong 23 Geylang, Singapore Badminton Hall, Singapore\", \"market\": \"SG_SIN\"}}}, {\"location\": {\"lat\": \"1.2966147\", \"lng\": \"103.8485095\"}, \"addresses\": {\"en_SG\": {\"displayString\": \"Stamford Road, National Museum of Singapore, Singapore\", \"market\": \"SG_SIN\"}}}], \"deliveries\": [{\"toStop\": 1, \"toContact\": {\"name\": \"dodo\", \"phone\": \"+660923447537\"}, \"remarks\": \"Do not take this order - SANDBOX CLIENT TEST\"}], \"quotedTotalFee\": {\"amount\": \"11.80\", \"currency\": \"SGD\"}}";
        
        // Create signature and authorization
        string rawSignature = String.Format("{0}\r\n{1}\r\n{2}\r\n\r\n{3}", time, method, path, body);
        string signature = HMACSHA256(rawSignature, secret);
        string token = string.Format("hmac {0}:{1}:{2}", key, time, signature);
        
        // Create HTTP request
        string url = baseUrl+path;
        HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
        request.Method = method;
        string postData = body;
        byte[] byteArray = Encoding.UTF8.GetBytes(postData);
        request.Accept = "application/json";
        request.ContentType = "application/json; charset=utf-8";
        request.Headers.Add("Authorization", token);
        request.Headers.Add("X-LLM-Market", region);
        
        long startTime = ToUnixTimestamp(DateTime.Now.Ticks);
        
        // Send HTTP request
        Stream dataStream = request.GetRequestStream();
        dataStream.Write(byteArray, 0, byteArray.Length);
        dataStream.Close();
        
        try 
        {
            WebResponse response = request.GetResponse();
            PrintOutHttpResponse(response, startTime, token);
        } 
        catch (WebException ex)
        {
            WebResponse response = ex.Response as WebResponse;
            PrintOutHttpResponse(response, startTime, token);
        }
    }
}