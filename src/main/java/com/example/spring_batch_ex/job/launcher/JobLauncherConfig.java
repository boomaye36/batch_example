package com.example.spring_batch_ex.job.launcher;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobLauncherConfig {
    private Job aptNotificationJob;

    @Bean
    public JobLauncher jobLauncher(JobLauncher jobLauncher) {
        return jobLauncher;
    }


}
