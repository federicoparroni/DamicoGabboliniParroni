package it.polimi.gabdampar.travlendarapp.controller;

/**
 * Created by Edoardo D'Amico on 15/11/2017.
 */

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.*;


public class IdentityManager {

    private string token;



    // get token for authorizing client
    public static boolean Login(string email, string password) {
        try {
            HttpPost request = new HttpPost("https://travlendar.000webhostapp.com/travlendar/public/token");
            StringEntity params = new StringEntity(json.toString());
            request.addHeader("content-type", "x-www-form-urlencoded");
            request.setEntity(params);
            httpClient.execute(request);
            // handle response here...

            token = "abcde135ahdbvi37tnf";
            return true;
        } catch (Exception ex) {
            // handle exception here
        } finally {
            httpClient.close();
        }
    }

    public static void Register() {

    }

}
