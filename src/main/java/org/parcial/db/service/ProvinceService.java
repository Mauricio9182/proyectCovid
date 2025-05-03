package org.parcial.db.service;

import org.parcial.db.dao.ProvinceDao;
import org.parcial.db.model.ProvinceModel;
import org.parcial.util.ProvinceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProvinceService {

    @Autowired
    private ProvinceDao provinceDao;

    public void saveAllProvinces(Map<String, List<ProvinceLoader>> allData) {
        for (List<ProvinceLoader> list : allData.values()) {
            for (ProvinceLoader info : list) {
                boolean exists = provinceDao.existsByIsoAndProvince(info.getIso(), info.getProvince());

                if (!exists) {
                    ProvinceModel province = new ProvinceModel(
                            info.getIso(),
                            info.getProvince(),
                            info.getName(),
                            info.getLat(),
                            info.getLng()
                    );
                    provinceDao.save(province);
                }
            }
        }
    }
}
