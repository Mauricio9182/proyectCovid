package org.parcial.main;

import org.parcial.db.model.ReportModel;
import org.parcial.db.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ReportQueryRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ReportQueryRunner.class);

    @Autowired
    private ReportService reportService;

    @Value("${covid.report.date}")
    private String reportDate;

    @Value("${covid.report.countries}")
    private String countryList;

    @Override
    public void run(String... args) throws Exception {
        List<String> countries = Arrays.asList(countryList.split(","));

        for (String iso : countries) {
            iso = iso.trim();
            logger.info("📅 Searching reports for {} on date {}...", iso, reportDate);

            Map<String, ReportModel> orderedReports = reportService.getOrderedReportMap(reportDate, iso);

            if (orderedReports.isEmpty()) {
                logger.warn("⚠️ No reports found for {}", iso);
            } else {
                logger.info("✅ Reports found for {} (ordered by province):", iso);

                int totalConfirmed = 0;
                int totalDeaths = 0;
                int totalRecovered = 0;

                for (Map.Entry<String, ReportModel> entry : orderedReports.entrySet()) {
                    ReportModel report = entry.getValue();
                    logger.info("🌎 {} | Province: {} | Confirmed: {} | Deaths: {} | Recovered: {}",
                            report.getIso(),
                            report.getProvince(),
                            report.getConfirmed(),
                            report.getDeaths(),
                            report.getRecovered()
                    );

                    totalConfirmed += report.getConfirmed();
                    totalDeaths += report.getDeaths();
                    totalRecovered += report.getRecovered();
                }

                logger.info(" Summary for {}:", iso);
                logger.info(" Total Provinces: {} | Confirmed: {} | Deaths: {} | Recovered: {}",
                        orderedReports.size(),
                        totalConfirmed,
                        totalDeaths,
                        totalRecovered
                );
            }
        }
    }
}
