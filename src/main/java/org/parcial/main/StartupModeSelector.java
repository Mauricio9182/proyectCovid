package org.parcial.main;

import org.parcial.thread.CovidThread;
import org.parcial.db.service.ReportService;
import org.parcial.db.model.ReportModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class StartupModeSelector implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupModeSelector.class);

    @Value("${app.start.mode}")
    private String mode;

    @Value("${covid.report.date}")
    private String reportDate;

    @Value("${covid.report.countries}")
    private String countries;

    @Autowired
    private CovidThread covidThread;

    @Autowired
    private ReportService reportService;

    @Override
    public void run(String... args) throws Exception {
        if ("thread".equalsIgnoreCase(mode)) {
            logger.info("Running in thread mode (CovidThread)...");
            Thread.sleep(15000);
            new Thread(covidThread).start();

        } else if ("query".equalsIgnoreCase(mode)) {
            logger.info("Running in query mode...");

            for (String iso : countries.split(",")) {
                iso = iso.trim();
                logger.info("📅 Fetching reports for {} on {}...", iso, reportDate);

                Map<String, ReportModel> orderedReports = reportService.getOrderedReportMap(reportDate, iso);

                if (orderedReports.isEmpty()) {
                    logger.warn("⚠️ No reports found for {}", iso);
                } else {
                    logger.info("✅ Reports found for {}:", iso);
                    orderedReports.forEach((province, report) -> {
                        logger.info("🌎 {} | Province: {} | Confirmed: {} | Deaths: {} | Recovered: {}",
                                report.getIso(),
                                report.getProvince(),
                                report.getConfirmed(),
                                report.getDeaths(),
                                report.getRecovered());
                    });
                }
            }

        } else {
            logger.error("❌ Unrecognized mode in 'app.start.mode'. Use 'thread' or 'query'.");
        }
    }
}
