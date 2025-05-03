package org.parcial.db.service;

import org.parcial.db.dao.RegionDao;
import org.parcial.db.model.RegionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RegionService {

    @Autowired
    private RegionDao regionDao;

    public void saveRegions(Map<Integer, Map<String, String>> regions) {
        for (Map<String, String> data : regions.values()) {
            boolean exists = regionDao.existsByIsoAndName(data.get("iso"), data.get("name"));

            if (!exists) {
                RegionModel region = new RegionModel(0, data.get("iso"), data.get("name"));
                regionDao.save(region);
            }
        }
    }
}
