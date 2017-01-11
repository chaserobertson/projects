package com.example.chaserobertson.familymap;

import com.example.chaserobertson.familymap.model.Model;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by chaserobertson on 6/2/16.
 */
public class HttpClient {

    public String getUrlString(String urlString, String auth, String postData) {
        try {

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set HTTP request headers, if necessary
            if(Model.SINGLETON.user != null) auth = Model.SINGLETON.user.getAuthorization();

            connection.addRequestProperty("Authorization", auth);

            connection.connect();

            // Write post data to request body
            OutputStream requestBody = connection.getOutputStream();
            requestBody.write(postData.getBytes());
            requestBody.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get HTTP response headers, if necessary
                // Map<String, List<String>> headers = connection.getHeaderFields();

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

                // TODO: Process response body data
                Model.SINGLETON.responseData = responseBodyData;
                return responseBodyData;
            } else {
                // SERVER RETURNED AN HTTP ERROR
                Model.SINGLETON.responseData = null;
            }
        } catch (IOException e) {
            // IO ERROR
            e.printStackTrace();
        }

        return null;
    }
}