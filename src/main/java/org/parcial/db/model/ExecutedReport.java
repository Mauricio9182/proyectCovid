package org.parcial.db.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "executed_reports")
public class ExecutedReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "execution_date", nullable = false)
    private LocalDate executionDate;

    @Column(name = "country_iso", nullable = false)
    private String countryIso;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

    public String getCountryIso() {
        return countryIso;
    }

    public void setCountryIso(String countryIso) {
        this.countryIso = countryIso;
    }
}
