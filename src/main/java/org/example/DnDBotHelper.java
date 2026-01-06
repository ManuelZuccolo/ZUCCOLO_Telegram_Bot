package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DnDBotHelper {

    public static String nameToIndex(String name) {
        return name.toLowerCase().replace(" ", "-");
    }

    public static String getMonsterData(String monsterName) throws Exception {
        String index = nameToIndex(monsterName);
        String apiUrl = "https://www.dnd5eapi.co/api/2014/monsters/" + index;

        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        if (status != 200) {
            throw new Exception("Impossibile recuperare il mostro, status code " + status);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        return content.toString();
    }
}
