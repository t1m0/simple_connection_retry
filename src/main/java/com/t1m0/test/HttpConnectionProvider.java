package com.t1m0.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Implements the {@link IConnectionProvider} interface to provide the required implementation to perform http calls */
public class HttpConnectionProvider implements IConnectionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionProvider.class);

    private final String strUrl;

    public HttpConnectionProvider(String url){
        this.strUrl = url;
    }

    private URL url = null;

    private URL getUrl() throws MalformedURLException {
        if(url == null){
            url = new URL(strUrl);
        }
        return url;
    }

    private HttpURLConnection openHttpURLConnection(String requestMethod) throws ConnectionFailedException {
        try {
            HttpURLConnection conn = (HttpURLConnection) getUrl().openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("Content-Type", "application/json");
            return conn;
        } catch (Exception e){
            throw new ConnectionFailedException("Error while opening connection to  '"+strUrl+"'",e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Response sendData(String data) throws ConnectionFailedException {
        String errorMessage=null;
        HttpURLConnection conn=null;
        try {
            conn = openHttpURLConnection("PUT");
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                errorMessage = "Received bad error code from server: " + conn.getResponseCode();
            }
            conn.disconnect();
            return new Response(errorMessage != null,errorMessage);
        }catch (IOException e){
            LOGGER.warn("Unable to connect to '"+strUrl+"'.",e);
            throw new ConnectionFailedException("Failed to create connection!",e);
        }finally {
            if(conn != null)
                conn.disconnect();
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean verifyConnection() {
        boolean success = false;
        HttpURLConnection conn = null;
        try{
            conn = openHttpURLConnection("PUT");
            conn.connect();
            success = true;
        }catch (ConnectionFailedException | IOException e){
            LOGGER.info("Still unable to establish connection to '"+strUrl+"'.",e);
        }finally {
            if(conn != null)
                conn.disconnect();
        }
        return success;
    }
}
