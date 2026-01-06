package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DnDBotHelper {

    // Funzione per trasformare il nome in index
    public static String nameToIndex(String name) {
        // Trasforma tutto in minuscolo e sostituisce gli spazi con trattini
        return name.toLowerCase().replace(" ", "-");
    }

    // Funzione per fare la richiesta GET all'API
    public static String getMonsterData(String monsterName) {
        String index = nameToIndex(monsterName);
        String apiUrl = "https://www.dnd5eapi.co/api/2014/monsters/" + index;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            if (status != 200) {
                return "Errore: impossibile recuperare il mostro, status code " + status;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            con.disconnect();

            return content.toString(); // qui ricevi il JSON dell'API
        } catch (Exception e) {
            return "Errore durante la connessione all'API: " + e.getMessage();
        }
    }

    // Esempio di test
    /*public static void main(String[] args) {
        String nomeMostro = "Ancient Red Dragon";
        String json = getMonsterData(nomeMostro);
        System.out.println(json);
    }*/
}
