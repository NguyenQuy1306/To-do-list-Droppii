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

    // Tạo 12 partition cho 12 tháng tiếp theo.
    @PostConstruct
    public void createPartitions() {
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
