package org.parcial.db.dao;

import org.parcial.db.dabaBaseConnection.DatabaseConnection;
import org.parcial.db.model.RegionModel;
import org.springframework.stereotype.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RegionDao {

    private static final Logger logger = LogManager.getLogger(RegionDao.class);

    // Guardar una región (verificando duplicados)
    public boolean save(RegionModel region) {
        if (existsByIsoAndName(region.getIso(), region.getName())) {
            logger.info("Región duplicada ignorada: {} - {}", region.getIso(), region.getName());
            return false;
        }

        String query = "INSERT INTO regions (iso, name) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, region.getIso());
            stmt.setString(2, region.getName());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error inserting region: {}", region, e);
            return false;
        }
    }

    // Verifica si ya existe una región por iso + name
    public boolean existsByIsoAndName(String iso, String name) {
        String query = "SELECT COUNT(*) FROM regions WHERE iso = ? AND name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, iso);
            stmt.setString(2, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking region existence: iso={}, name={}", iso, name, e);
        }
        return false;
    }

    public List<RegionModel> getAll() {
        List<RegionModel> regions = new ArrayList<>();
        String query = "SELECT * FROM regions";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                RegionModel region = new RegionModel(
                        rs.getInt("id"),
                        rs.getString("iso"),
                        rs.getString("name")
                );
                regions.add(region);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all regions", e);
        }
        return regions;
    }

    public RegionModel getById(int id) {
        RegionModel region = null;
        String query = "SELECT * FROM regions WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    region = new RegionModel(
                            rs.getInt("id"),
                            rs.getString("iso"),
                            rs.getString("name")
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving region by ID: {}", id, e);
        }
        return region;
    }
}
