package org.parcial.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parcial.covidApis.CovidProvinces;
import org.parcial.covidApis.CovidRegions;
import org.parcial.covidApis.CovidReports;
import org.parcial.db.service.RegionService;
import org.parcial.db.service.ProvinceService;
import org.parcial.db.service.ReportService;
import org.parcial.db.service.ExecutedReportService;
import org.parcial.util.RegionLoader;
import org.parcial.util.ProvinceLoader;
import org.parcial.util.ReportLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class CovidThread implements Runnable {

    @Autowired
    private ExecutedReportService executedReportService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private CovidProvinces covidProvinces;

    @Autowired
    private CovidReports covidReports;

    @Autowired
    private CovidRegions covidRegions;

    @Value("${covid.report.date}")
    private String reportDate;

    @Value("${covid.report.countries}")
    private String countryList;

    private static final Logger logger = LogManager.getLogger(CovidThread.class);

    @Override
    public void run() {
        try {
            LocalDate date = LocalDate.parse(reportDate);

            Set<String> isoSet;
            if ("ALL".equalsIgnoreCase(countryList.trim())) {
                logger.info("🌍 Fetching ALL available countries (ISO codes) from API...");
                isoSet = covidRegions.fetchAllIsoCodes();
            } else {
                isoSet = new HashSet<>();
                for (String iso : countryList.split(",")) {
                    isoSet.add(iso.trim());
                }
            }

            int totalGlobalReports = 0;

            for (String iso : isoSet) {
                if (executedReportService.isAlreadyExecuted(date, iso)) {
                    logger.warn("⚠️ Already executed for {} on {}", iso, date);
                    continue;
                }

                logger.info("📥 Loading regions for {}...", iso);
                Map<Integer, Map<String, String>> regions = RegionLoader.loadRegions(iso);
                regionService.saveRegions(regions);

                regions.forEach((id, data) -> {
                    logger.info("🌍 Region => ID: {}, ISO: {}, Name: {}", id, data.get("iso"), data.get("name"));
                });

                logger.info("🏙️ Loading provinces for {}...", iso);
                Map<String, List<ProvinceLoader>> allData = covidProvinces.fetchAllRegionData(Set.of(iso));
                provinceService.saveAllProvinces(allData);

                allData.forEach((regionIso, provinces) -> {
                    for (ProvinceLoader province : provinces) {
                        logger.info("🏙️ Province => Region ISO: {}, Name: {}", regionIso, province.getName());
                    }
                });

                logger.info("📊 Loading reports for {}...", iso);
                Map<String, List<ReportLoader>> covidReportsData = covidReports.fetchCovidDataForAllProvinces(Set.of(iso), reportDate);
                reportService.saveAllReports(covidReportsData);

                int countryReportCount = covidReportsData.values().stream()
                        .mapToInt(List::size)
                        .sum();

                logger.info("🧮 Total reports for {}: {}", iso, countryReportCount);
                totalGlobalReports += countryReportCount;

                logger.info("💾 Saving execution for {}...", iso);
                executedReportService.saveExecution(date, iso);
                logger.info("✅ Execution saved for {} on {}", iso, date);
            }

            logger.info("📦 Total reports across all countries: {}", totalGlobalReports);

        } catch (Exception e) {
            logger.error("❌ Error in CovidThread", e);
        }
    }
}
