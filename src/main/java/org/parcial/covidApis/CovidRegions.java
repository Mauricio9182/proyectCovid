package org.parcial.covidApis;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Component
public class CovidRegions {

    private static final Logger logger = LogManager.getLogger(CovidRegions.class);
    private static final String API_URL = "https://covid-19-statistics.p.rapidapi.com/regions";
    private static final String API_KEY = "2505eda46amshc60713983b5e807p1da25ajsn36febcbf4a71";
    private static final String API_HOST = "covid-19-statistics.p.rapidapi.com";

    // Método original: devuelve solo los ISO
    public Set<String> fetchAllIsoCodes() {
        Set<String> isoCodes = new HashSet<>();

        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-RapidAPI-Key", API_KEY);
            conn.setRequestProperty("X-RapidAPI-Host", API_HOST);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray dataArray = jsonResponse.optJSONArray("data");

                if (dataArray != null) {
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject region = dataArray.getJSONObject(i);
                        String iso = region.optString("iso");
                        if (!iso.isEmpty()) {
                            isoCodes.add(iso);
                        }
                    }
                    logger.info("✅ Total ISO codes fetched: {}", isoCodes.size());
                }
            } else {
                logger.warn("⚠️ Failed to fetch regions. HTTP code: {}", responseCode);
            }
        } catch (Exception e) {
            logger.error("❌ Error fetching region data: {}", e.getMessage());
        }

        return isoCodes;
    }

    // ✅ Método requerido por RegionLoader
    public String fetchRegionJson() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-RapidAPI-Key", API_KEY);
            conn.setRequestProperty("X-RapidAPI-Host", API_HOST);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                logger.warn("⚠️ HTTP response code: {}", responseCode);
            }
        } catch (Exception e) {
            logger.error("❌ Error in fetchRegionJson(): {}", e.getMessage());
        }
        return null;
    }
}
