package org.parcial.util;

import com.google.gson.reflect.TypeToken;
import org.parcial.covidApis.CovidRegions;

import java.lang.reflect.Type;
import java.util.*;

public class RegionLoader {

    public static Map<Integer, Map<String, String>> loadRegions(String isoFilter) {
        CovidRegions api = new CovidRegions();
        String json = api.fetchRegionJson();  // ✅ Usa el método que ya tienes en CovidRegions

        if (json == null) return new HashMap<>();

        Type responseType = new TypeToken<ApiResponse>() {}.getType();
        ApiResponse response = GsonManager.getGson().fromJson(json, responseType);

        Map<Integer, Map<String, String>> regionMap = new HashMap<>();
        int index = 1;

        for (Region region : response.data) {
            if (region.iso != null && region.iso.equalsIgnoreCase(isoFilter)) {
                Map<String, String> values = new HashMap<>();
                values.put("iso", region.iso);
                values.put("name", region.name);
                regionMap.put(index++, values);
            }
        }

        return regionMap;
    }

    // Clases internas para mapear el JSON
    static class ApiResponse {
        List<Region> data;
    }

    static class Region {
        String iso;
        String name;
    }
}
