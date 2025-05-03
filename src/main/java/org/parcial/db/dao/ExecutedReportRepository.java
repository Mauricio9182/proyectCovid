package org.parcial.db.dao;

import org.parcial.db.model.ExecutedReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExecutedReportRepository extends JpaRepository<ExecutedReport, Long> {
    List<ExecutedReport> findByExecutionDateAndCountryIso(LocalDate executionDate, String countryIso);
}
