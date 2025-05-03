package org.parcial.db.service;

import org.parcial.db.dao.ExecutedReportRepository;
import org.parcial.db.model.ExecutedReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExecutedReportService {

    @Autowired
    private ExecutedReportRepository executedReportRepository;

    public boolean isAlreadyExecuted(LocalDate date, String iso) {
        List<ExecutedReport> results = executedReportRepository.findByExecutionDateAndCountryIso(date, iso);
        return !results.isEmpty();
    }

    public void saveExecution(LocalDate date, String iso) {
        ExecutedReport executed = new ExecutedReport();
        executed.setExecutionDate(date);
        executed.setCountryIso(iso);
        executedReportRepository.save(executed);
    }
}
