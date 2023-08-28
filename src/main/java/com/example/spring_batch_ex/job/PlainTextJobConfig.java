package com.example.spring_batch_ex.job;

import com.example.spring_batch_ex.core.domain.PlainText;
import com.example.spring_batch_ex.core.repository.PlainTextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class PlainTextJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlainTextRepository plainTextRepository;
    @Bean("PlainTextJob")
    public Job plainTextJob(Step plainTextStep){
        return jobBuilderFactory.get("plainTextJob")
                .incrementer(new RunIdIncrementer())
                .start(plainTextStep)
                .build();

    }
    @JobScope
    @Bean("plainTextStep")
    public Step plainTextStep(ItemReader plainTextReader,
                              ItemProcessor itemProcessor,
                              ItemWriter itemWriter){
        return stepBuilderFactory.get("plainTextStep")
                .<PlainText, String> chunk(5)
                .reader(plainTextReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<PlainText> plainTextReader(){
        return new RepositoryItemReaderBuilder<PlainText>()
                .name("plainTextReader")
                .repository(plainTextRepository)
                .methodName("findBy")
                .pageSize(5)
                .arguments(List.of())
                .sorts(Collections.singletonMap("id", Sort.Direction.DESC))
                .build();
    }
    @StepScope
    @Bean
    public ItemProcessor<PlainText, String> plainTextProcessor(){
      return item -> "processed : " + item.getText();
    }
    @StepScope
    @Bean
    public ItemWriter<String> plainTextWriter(){
        return items -> {
            items.forEach(System.out::println);
            System.out.println("=== chunk is finished");
        };
    }
}
