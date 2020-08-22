import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class TwitchAPI
{
    /*TWITCH WARNING
    "
    Warning: Treat your token like a password. For example, never use access tokens in any public URL, and never display tokens on any web page without requiring a click to de-obfuscate.
    "
     */

    /*Credential needed to communicate with the twitch API

    Replace ClientID variable with your own Client ID.
    Replace ClientSecret variable with your own Client Secret ID.

    */

    private String ClientID = "";
    private String ClientSecret = "";
    private String AuthToken = "";

    public String CheckTwitchChannel() throws IOException
    {
        GetToken();
        //This is a work-a-round that I haven't yet seemed to solve. If you use this class, instead of "denisiss", put your own channel name.
        String str = "https://api.twitch.tv/helix/streams?user_login=denisiss";
        URL url = new URL( str );
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        connection.setRequestProperty ("Authorization", "Bearer " + AuthToken);
        connection.setRequestProperty("client-id", ClientID);

        BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()) );
        String parseLines;
        StringBuilder content = new StringBuilder();

        while ( (parseLines = reader.readLine()) != null)
        {
            content.append(parseLines);
            content.append("\n");
        }

        reader.close();
        connection.disconnect();

        return content.toString();
/*
        if ( )
        {
            return "Stream is offline.";
        }
        else
        {
            return "Stream is NOT offline.";
        }
 */
    }

    /*Function to get the Token from twitch

    As of the 30th of April 2020, this has made it so that you are now required to authenticate your application before requesting anything.
    This function will send the ClientID and it's secret ID as a query parameter to https://id.twitch.tv/oauth2/token .
    After the token has been received, we return to the main function of this class @CheckTwitchChannel.
    Global variable AuthToken now holds our Token.

     */
    public void GetToken() throws IOException
    {
        URL url = new URL("https://id.twitch.tv/oauth2/token");
        Gson ReturnedMessage = new Gson();

        //Put all needed data for query parameters in query_params variable.
        String query_params = String.format("client_id=%s&client_secret=%s&grant_type=client_credentials",
                ClientID, ClientSecret);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

        outputStream.writeBytes(query_params);

        outputStream.flush();
        outputStream.close();

        BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()) );
        String parseLines;
        StringBuilder content = new StringBuilder();

        while ( (parseLines = reader.readLine()) != null)
        {
            content.append(parseLines);
            content.append("\n");
        }

        reader.close();
        connection.disconnect();

        AuthApiResponse Response = ReturnedMessage.fromJson(content.toString(), AuthApiResponse.class);
        AuthToken = Response.access_token;
    }

    /*Class for all possible responses

    As of 22nd of August 2020, this class only uses access_token.

     */
    class AuthApiResponse {
        @SerializedName("access_token")
        String access_token;
        @SerializedName("refresh_token")
        String refresh_token;
        @SerializedName("expires_in")
        int expires_in;
        @SerializedName("scope")
        String[] scope;
        @SerializedName("token_type")
        String token_type;
    }
}
