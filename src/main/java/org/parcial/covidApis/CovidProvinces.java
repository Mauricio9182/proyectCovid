package org.parcial.covidApis;

import org.parcial.util.ProvinceLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Component
public class CovidProvinces {

    private static final String API_URL = "https://covid-19-statistics.p.rapidapi.com/provinces?iso=";
    private static final String API_KEY = "2505eda46amshc60713983b5e807p1da25ajsn36febcbf4a71";
    private static final String API_HOST = "covid-19-statistics.p.rapidapi.com";

    public Map<String, List<ProvinceLoader>> fetchAllRegionData(Set<String> isoSet) throws JSONException {
        Map<String, List<ProvinceLoader>> regionDataMap = new HashMap<>();
        for (String iso : isoSet) {
            JSONObject response = fetchDataByIso(iso);
            storeProvinceData(iso, response, regionDataMap);
        }
        return regionDataMap;
    }

    private JSONObject fetchDataByIso(String iso) throws JSONException {
        try {
            URL url = new URL(API_URL + iso);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-RapidAPI-Key", API_KEY);
            conn.setRequestProperty("X-RapidAPI-Host", API_HOST);

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return new JSONObject(response.toString());
            } else {
                return new JSONObject("{\"error\":\"Failed to fetch data for ISO " + iso + "\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject("{\"error\":\"Exception for ISO " + iso + "\"}");
        }
    }

    private void storeProvinceData(String iso, JSONObject response, Map<String, List<ProvinceLoader>> dataMap) throws JSONException {
        JSONArray dataArray = response.optJSONArray("data");
        if (dataArray == null) return;

        List<ProvinceLoader> provinces = new ArrayList<>();

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);

            String province = obj.optString("province", "");
            String name = obj.optString("name", "");
            double lat = obj.optDouble("lat", 0.0);
            double lng = obj.optDouble("long", 0.0);

            provinces.add(new ProvinceLoader(iso, province, name, lat, lng));
        }

        dataMap.put(iso, provinces);
    }
}
