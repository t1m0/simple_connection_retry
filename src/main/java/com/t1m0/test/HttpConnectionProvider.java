package com.t1m0.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by timo.schoepflin on 24.08.2017.
 */
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

    private HttpURLConnection getHttpURLConnection() throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) getUrl().openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
        }catch (IOException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException("Error while opening connection to  '"+strUrl+"'",e);
        }
        return conn;
    }

    @Override
    public Response sendData(String data) throws ConnectionFailedException {
        String errorMessage=null;
        HttpURLConnection conn=null;
        try {
            conn = getHttpURLConnection();
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                errorMessage = "Received bad error code from server: " + conn.getResponseCode();
            }
            conn.disconnect();
            return new Response(errorMessage != null,errorMessage);
        }catch (IOException e){
            String execMsg = "Unable to connect to '"+strUrl+"'.";
            if(LOGGER.isDebugEnabled()){
                LOGGER.warn(execMsg,e);
            }else {
                LOGGER.warn(execMsg);
            }
            throw new ConnectionFailedException("Failed to create connection!",e);
        }finally {
            if(conn != null)
                conn.disconnect();
        }
    }

    @Override
    public boolean verifyConnection() {
        boolean success = false;
        HttpURLConnection conn = null;
        try{
            conn = getHttpURLConnection();
            conn.connect();
            success = true;
        }catch (IOException e){
            LOGGER.info("Still unable to establish connection to '"+strUrl+"'.");
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("Connection error on verify.",e);
            }
        }finally {
            if(conn != null)
                conn.disconnect();
        }
        return success;
    }
}
