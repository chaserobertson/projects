package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import debugger.Debugger;
import model.facade.client.ClientGeneralCommandFacade;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chase on 9/25/16.
 */
public class ClientCommunicator {

    private static ClientCommunicator instance;
    public static ClientCommunicator getInstance() {
        if(instance == null) instance = new ClientCommunicator();
        return instance;
    }
    private ClientCommunicator() {
        cookies = new HashMap<>();
        playerID = -1;
        playerUsername = "";
        playerPassword = "";
        gameIndex = -1;
        //nothing
    }

    private Map<String,HttpCookie> cookies;
    private int playerID;
    private String playerUsername;
    private String playerPassword;
    private int gameIndex;

    protected int getPlayerID() {
        return playerID;
    }

    protected String getPlayerUsername() {
        return playerUsername;
    }

    protected String getPlayerPassword() { return playerPassword; }

    protected int getGameIndex() {
        return gameIndex;
    }

    protected boolean isConnected(String urlString){
        try{
            String url = urlString + "/game/listAI";
            String response=getResponse(url, null,1000,10000);
            if(response.equals("unsuccessful")||response.equals("http error")){
                Debugger.LogMessage("ClientCommunicator, failed is connected-"+response);
                return false;
            }
            return true;
        }catch (Exception e){
            Debugger.LogMessage("ClientCommunicator:IsConnected:Exception was thrown");
            return false;
        }
    }
    private String getResponse(String urlString,String postData,int connectTimeOut,int readTimeOut){
        try {

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if(postData != null) connection.setDoOutput(true);
            connection.setReadTimeout(readTimeOut/* milliseconds */);
            connection.setConnectTimeout(connectTimeOut /* milliseconds */);//15000 orig

            String cookieString = "";
            HttpCookie userCookie = cookies.get("catan.user");
            if(userCookie instanceof HttpCookie) {
                cookieString += userCookie.getName();
                cookieString += "=";
                cookieString += userCookie.getValue();
            }
            HttpCookie gameCookie = cookies.get("catan.game");
            if(gameCookie instanceof HttpCookie) {
                cookieString += "; ";
                cookieString += gameCookie.getName();
                cookieString += "=";
                cookieString += gameCookie.getValue();
            }
            connection.setRequestProperty("Cookie", cookieString);

            // Starts the query
            connection.connect();

            // Write post data to request body
            if(postData != null) {
                OutputStream requestBody = connection.getOutputStream();
                requestBody.write(postData.getBytes());
                requestBody.close();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                String headerName;
                for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
                    if (headerName.equals("Set-cookie")) {
                        String cookie = connection.getHeaderField(i);
                        int equalsIndex = cookie.indexOf('=');
                        int semicolonIndex = cookie.indexOf(';');
                        String name = cookie.substring(0,equalsIndex);
                        String value = cookie.substring(equalsIndex+1,semicolonIndex);
                        HttpCookie httpCookie = new HttpCookie(name, value);

                        if(name.equals("catan.user")) {
                            String jsonCookie = URLDecoder.decode(value);
                            Gson gsonBuilder = new GsonBuilder().create();
                            JsonObject cookieObject = gsonBuilder.fromJson(jsonCookie, JsonObject.class);
                            this.playerID = cookieObject.get("playerID").getAsInt();
                            this.playerUsername = cookieObject.get("name").getAsString();
                            this.playerPassword = cookieObject.get("password").getAsString();
                        }
                        else if(name.equals("catan.game")) {
                            String jsonCookie = URLDecoder.decode(value);
                            this.gameIndex = new Integer(jsonCookie);
                        }

                        cookies.put(httpCookie.getName(), httpCookie);
                    }
                }

                // Get response body input stream
                InputStream responseBody = connection.getInputStream();

                // Read response body bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = responseBody.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }

                // Convert response body bytes to a string
                String responseBodyData = baos.toString();

                return responseBodyData;
            } else if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST){
                return "unsuccessful";
            } else {
                return "http error";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected String getResponse(String urlString, String postData) {
        //if(true)return getResponse(urlString,postData,15000,10000);
        try {

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if(postData != null) connection.setDoOutput(true);
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);//15000 orig

            String cookieString = "";
            HttpCookie userCookie = cookies.get("catan.user");
            if(userCookie instanceof HttpCookie) {
                cookieString += userCookie.getName();
                cookieString += "=";
                cookieString += userCookie.getValue();
            }
            HttpCookie gameCookie = cookies.get("catan.game");
            if(gameCookie instanceof HttpCookie) {
                cookieString += "; ";
                cookieString += gameCookie.getName();
                cookieString += "=";
                cookieString += gameCookie.getValue();
            }
            connection.setRequestProperty("Cookie", cookieString);

            // Starts the query
            connection.connect();

            // Write post data to request body
            if(postData != null) {
                OutputStream requestBody = connection.getOutputStream();
                requestBody.write(postData.getBytes());
                requestBody.close();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                String headerName;
                for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
                    if (headerName.equals("Set-cookie")) {
                        String cookie = connection.getHeaderField(i);
                        int equalsIndex = cookie.indexOf('=');
                        int semicolonIndex = cookie.indexOf(';');
                        String name = cookie.substring(0,equalsIndex);
                        String value = cookie.substring(equalsIndex+1,semicolonIndex);
                        HttpCookie httpCookie = new HttpCookie(name, value);

                        if(name.equals("catan.user")) {
                            String jsonCookie = URLDecoder.decode(value);
                            Gson gsonBuilder = new GsonBuilder().create();
                            JsonObject cookieObject = gsonBuilder.fromJson(jsonCookie, JsonObject.class);
                            this.playerID = cookieObject.get("playerID").getAsInt();
                            this.playerUsername = cookieObject.get("name").getAsString();
                            this.playerPassword = cookieObject.get("password").getAsString();
                        }
                        else if(name.equals("catan.game")) {
                            String jsonCookie = URLDecoder.decode(value);
                            this.gameIndex = new Integer(jsonCookie);
                        }

                        cookies.put(httpCookie.getName(), httpCookie);
                    }
                }

                // Get response body input stream
                InputStream responseBody = connection.getInputStream();

                // Read response body bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = responseBody.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }

                // Convert response body bytes to a string
                String responseBodyData = baos.toString();

                return responseBodyData;
            } else if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST){
                return "unsuccessful";
            } else {
                return "http error";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
