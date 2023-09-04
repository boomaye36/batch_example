package com.example.spring_batch_ex.job.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobScheduler {

    private final JobLauncher jobLauncher;
    private final Job aptNotificationJob;
    private final Job aptDealInsertJob;
    @Scheduled(cron = "0 0 8 * * *") // 매일 오전 8시에 실행
    public void runAptNotificationJob(){
        try{
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("dealDate", "2023-07-14")
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(aptNotificationJob, jobParameters);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 1 0 * * *") // 매일 오전 1시에 실행
    public void runAptDealInsertJob(){
        try{
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("yearMonth", "2023-07")
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(aptDealInsertJob, jobParameters);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
