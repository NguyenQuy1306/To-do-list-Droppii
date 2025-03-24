package com.interviewproject.todolist.config;

import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class PartitionInitializer {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Tạo 12 partition cho cột duedate 12 tháng tiếp theo

    @PostConstruct
    public void createPartitions() {

        String dropPartitionSql = "DROP TABLE IF EXISTS task CASCADE;";
        jdbcTemplate.execute(dropPartitionSql);

        String createTableSql = """
                CREATE TABLE task (
                    taskId SERIAL,
                    duedate TIMESTAMP NOT NULL,
                    title VARCHAR(255) NOT NULL,
                    description VARCHAR(1000),
                    priority INT CHECK (priority BETWEEN 1 AND 5),
                    status VARCHAR(20) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT valid_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
                    PRIMARY KEY (taskId, duedate)
                ) PARTITION BY RANGE (duedate);
                """;

        jdbcTemplate.execute(createTableSql);

        YearMonth currentMonth = YearMonth.now();
        for (int i = 0; i < 12; i++) {
            YearMonth month = currentMonth.plusMonths(i);
            String partitionName = "task_" + month.getYear() + "_" + String.format("%02d", month.getMonthValue());

            String sql = "CREATE TABLE IF NOT EXISTS " + partitionName +
                    " PARTITION OF task FOR VALUES FROM ('" + month.atDay(1) + "') TO ('"
                    + month.plusMonths(1).atDay(1) + "');";
            jdbcTemplate.execute(sql);
        }
    }
}
