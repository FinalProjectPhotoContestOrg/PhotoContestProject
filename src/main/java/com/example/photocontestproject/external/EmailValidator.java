package com.example.photocontestproject.external;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmailValidator {
    private static final String ZERBOUNCE_URL = "https://api.zerobounce.net/v2/validate";
    private static final String API_KEY = "0e58dbc6631143039c425a8de4684db6";

    public static boolean validateEmail(String email) {
        try {
            URL url = new URL(ZERBOUNCE_URL + "?api_key=" + API_KEY + "&email=" + email);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            int status = con.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                JSONObject obj = new JSONObject(content.toString());
                return obj.getString("status").equals("valid");
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
