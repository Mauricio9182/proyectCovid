package org.parcial.db.service;

import org.parcial.db.dao.ReportDao;
import org.parcial.db.model.ReportModel;
import org.parcial.util.ReportLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

@Service
public class ReportService {

    @Autowired
    private ReportDao reportDao;

    private static final Logger logger = Logger.getLogger(ReportService.class.getName());

    public void saveAllReports(Map<String, List<ReportLoader>> covidReports) {
        for (List<ReportLoader> reports : covidReports.values()) {
            for (ReportLoader report : reports) {

                boolean exists = reportDao.existsByDateIsoAndProvince(
                        report.getDate(), report.getIso(), report.getProvince()
                );

                if (!exists) {
                    ReportModel model = new ReportModel(
                            0,
                            report.getDate(),
                            report.getConfirmed(),
                            report.getDeaths(),
                            report.getRecovered(),
                            report.getIso(),
                            report.getRegionName(),
                            report.getProvince()
                    );
                    reportDao.save(model);
                    logger.info("Report saved: " + report.getProvince() + " - " + report.getDate());
                } else {
                    logger.info("Duplicate report ignored: " + report.getProvince() + " - " + report.getDate());
                }
            }
        }
    }

    /**
     * Devuelve los reportes ordenados por clave provincia_ISO, eliminando duplicados.
     */
    public Map<String, ReportModel> getOrderedReportMap(String date, String iso) {
        List<ReportModel> reports = reportDao.getByDateAndIsoOrdered(date, iso);

        Map<String, ReportModel> orderedMap = new TreeMap<>();
        for (ReportModel report : reports) {
            String key = report.getProvince() + "_" + report.getIso();
            orderedMap.put(key, report); // Sobrescribe si hay duplicados
        }

        return orderedMap;
    }
}
