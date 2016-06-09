package fr.fusoft.qbooru.network;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import fr.fusoft.qbooru.model.BooruSite;

/**
 * Created by Florent on 29/02/2016.
 */
public class ConnectionManager {

    private final String LOG_TAG = "CONN_MGR";
    private final String USER_AGENT = "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> Mobile Safari/<WebKit Rev>";

    public ConnectionManager() {

    }

    public Drawable downloadFromUrl(String url){
        HttpURLConnection con;
        Drawable d = null;
        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            //add request header
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            int responseCode = con.getResponseCode();
            Log.d(LOG_TAG, "Sending 'GET' request to URL : " + url);
            Log.d(LOG_TAG, "Response Code : " + responseCode);

            con = getRedirection(con);

            d =  Drawable.createFromStream(con.getInputStream(),"src");

        }catch(Exception e){
            System.out.println("Exception while querying " + url + "" + " : " + e.toString());
            con = null;
        }

        return d;
    }

    public String execGetRequest(String url, String urlParameters){
        HttpURLConnection con;
        String rep = "";
        try {
            url += urlParameters;
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            //add request header
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            int responseCode = con.getResponseCode();
            Log.d(LOG_TAG, "Sending 'GET' request to URL : " + url);
            Log.d(LOG_TAG, "GET parameters : " + urlParameters);
            Log.d(LOG_TAG, "Response Code : " + responseCode);

            con = getRedirection(con);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            //Log.d(LOG_TAG,response.toString());
            rep = response.toString();
        }catch(Exception e){
            System.out.println("Exception while querying " + url + "" + urlParameters + " : " + e.toString());
            con = null;
        }

        return rep;
    }

    public HttpURLConnection getRedirection(HttpURLConnection conn){
        boolean redirect = false;
        String newUrl ="";

        try {
            // normally, 3xx is redirect
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            Log.d(LOG_TAG,"Response Code ... " + status);

            if (redirect) {
                // get redirect url from "location" header field
                newUrl = conn.getHeaderField("Location");

                // get the cookie if need, for login
                String cookies = conn.getHeaderField("Set-Cookie");

                // open the new connection again

                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestProperty("Cookie", cookies);
                conn.setRequestProperty("User-Agent", "QBooru App");
                conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                Log.d(LOG_TAG, "Redirect to URL : " + newUrl);

                conn = getRedirection(conn);
            }
        }catch(Exception e){
            Log.e(LOG_TAG, "Exception while redirecting to " + newUrl + " : " + e.toString());
        }

        return conn;
    }



    public void getCookie(BooruSite site, String user, String password){
        // Get cookie manager for HttpURLConnection
        java.net.CookieStore rawCookieStore = ((java.net.CookieManager) CookieHandler.getDefault()).getCookieStore();

        // Construct URI
        java.net.URI baseUri = null;
        try {
            baseUri = new URI(site.getLoginUrl());
        } catch (URISyntaxException e) {
            // Handle invalid URI
            Log.e(LOG_TAG,"Error while getting cookie for " + site.getLoginUrl() + " " + e.getMessage());
        }

        // Copy cookies from HttpURLConnection to WebView
        List<HttpCookie> cookies = rawCookieStore.get(baseUri);
        for (HttpCookie cookie : cookies) {
            Log.d(LOG_TAG,"Cookie for " + site.getLoginUrl() + " : " + cookie.toString());
        }
    }
}
