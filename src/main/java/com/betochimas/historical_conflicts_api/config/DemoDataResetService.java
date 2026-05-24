package com.betochimas.historical_conflicts_api.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Restores the domain tables to their seeded state so visitor edits made through the shared
 * demo account don't accumulate. Runs on the schedule in {@code app.demo.reset.cron}, but only
 * when {@code app.demo.reset.enabled=true} (off everywhere except the hosted demo). The
 * {@code users} table is intentionally left untouched — see {@code db/demo/demo_reset.sql}.
 */
@Component
public class DemoDataResetService {

    private static final Logger log = LoggerFactory.getLogger(DemoDataResetService.class);
    private static final Resource SCRIPT = new ClassPathResource("db/demo/demo_reset.sql");

    private final DataSource dataSource;

    @Value("${app.demo.reset.enabled:false}")
    private boolean enabled;

    public DemoDataResetService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Scheduled(cron = "${app.demo.reset.cron:0 0 4 * * *}")
    public void scheduledReset() {
        if (!enabled) {
            return;
        }
        log.info("Demo data reset: restoring seeded domain data");
        reset();
    }

    /** Truncates and re-seeds the domain tables from {@code db/demo/demo_reset.sql}. */
    public void reset() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(SCRIPT);
        populator.execute(dataSource);
    }
}
