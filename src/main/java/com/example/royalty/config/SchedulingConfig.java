package com.example.royalty.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulingConfig {

    /**
     * Pool used by the jobs in SMSScheduler.
     * <p>
     * Spring's default scheduler is single threaded, which couples the two jobs
     * together: a send that is waiting on the SMS gateway also holds up
     * handleReceivedMessages, so inbound codes stop being credited whenever
     * delivery is unwell. Keep this above 1 so the two run independently.
     */
    @Bean
    public TaskScheduler taskScheduler(@Value("${sms.scheduler.poolSize:2}") int poolSize) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize);
        scheduler.setThreadNamePrefix("royalty-sched-");
        scheduler.setWaitForTasksToCompleteOnShutdown(false);
        return scheduler;
    }
}
