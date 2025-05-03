package org.parcial.db.dao;

import org.parcial.db.dabaBaseConnection.DatabaseConnection;
import org.parcial.db.model.ReportModel;
import org.springframework.stereotype.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReportDao {

    private static final Logger logger = LogManager.getLogger(ReportDao.class);


    public boolean save(ReportModel report) {
        String sql = "INSERT INTO covid_reports (date, confirmed, deaths, recovered, iso, region_name, province) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, report.getDate());
            stmt.setInt(2, report.getConfirmed());
            stmt.setInt(3, report.getDeaths());
            stmt.setInt(4, report.getRecovered());
            stmt.setString(5, report.getIso());
            stmt.setString(6, report.getRegionName());
            stmt.setString(7, report.getProvince());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error inserting report: {}", report, e);
            return false;
        }
    }

    /**
     * Consulta reportes por fecha e ISO, ordenados por provincia e ISO.
     */
    public List<ReportModel> getByDateAndIsoOrdered(String date, String iso) {
        List<ReportModel> reports = new ArrayList<>();
        String sql = "SELECT * FROM covid_reports WHERE date = ? AND iso = ? ORDER BY province, iso";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, date);
            stmt.setString(2, iso);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ReportModel report = new ReportModel(
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getInt("confirmed"),
                        rs.getInt("deaths"),
                        rs.getInt("recovered"),
                        rs.getString("iso"),
                        rs.getString("region_name"),
                        rs.getString("province")
                );
                reports.add(report);
            }

        } catch (Exception e) {
            logger.error("Error retrieving reports: {}", e.getMessage());
        }

        return reports;
    }

    /**
     * Verifica si ya existe un reporte con misma fecha, ISO y provincia.
     */
    public boolean existsByDateIsoAndProvince(String date, String iso, String province) {
        String sql = "SELECT COUNT(*) FROM covid_reports WHERE date = ? AND iso = ? AND province = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, date);
            stmt.setString(2, iso);
            stmt.setString(3, province);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            logger.error("Error checking for existing report: date={}, iso={}, province={}", date, iso, province, e);
        }

        return false;
    }
}
