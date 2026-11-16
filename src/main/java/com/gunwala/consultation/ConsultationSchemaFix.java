package com.gunwala.consultation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Hibernate created consultation_bookings.status as a MySQL ENUM('CANCELLED','CONFIRMED'), and
 * ddl-auto=update never changes an existing column, so saving PENDING_PAYMENT would fail.
 * Turns the column into a plain VARCHAR once; does nothing on later starts.
 */
@Slf4j
@Component
@RequiredArgsConstructor
class ConsultationSchemaFix implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<String> type = jdbc.queryForList(
                    "SELECT DATA_TYPE FROM information_schema.COLUMNS "
                            + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'consultation_bookings' AND COLUMN_NAME = 'status'",
                    String.class);
            if (!type.isEmpty() && "enum".equalsIgnoreCase(type.get(0))) {
                jdbc.execute("ALTER TABLE consultation_bookings MODIFY COLUMN status VARCHAR(20) NOT NULL");
                log.info("consultation_bookings.status changed from ENUM to VARCHAR(20)");
            }
        } catch (Exception e) {
            log.error("Could not update consultation_bookings.status; paid consultations will fail to save: {}", e.getMessage());
        }
    }
}
