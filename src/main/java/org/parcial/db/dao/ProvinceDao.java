package org.parcial.db.dao;

import org.parcial.db.dabaBaseConnection.DatabaseConnection;
import org.parcial.db.model.ProvinceModel;
import org.springframework.stereotype.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class ProvinceDao {

    private static final Logger logger = LogManager.getLogger(ProvinceDao.class);

    public boolean save(ProvinceModel province) {
        if (existsByIsoAndProvince(province.getIso(), province.getProvince())) {
            logger.info("Provincia duplicada ignorada: {} - {}", province.getIso(), province.getProvince());
            return false;
        }

        String sql = "INSERT INTO provinces (iso, province, name, lat, lng) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, province.getIso());
            stmt.setString(2, province.getProvince());
            stmt.setString(3, province.getName());
            stmt.setDouble(4, province.getLat());
            stmt.setDouble(5, province.getLng());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error inserting province: {}", province, e);
            return false;
        }
    }

    public boolean existsByIsoAndProvince(String iso, String province) {
        String sql = "SELECT COUNT(*) FROM provinces WHERE iso = ? AND province = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, iso);
            stmt.setString(2, province);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            logger.error("Error checking existing province: iso={}, province={}", iso, province, e);
        }

        return false;
    }
}
