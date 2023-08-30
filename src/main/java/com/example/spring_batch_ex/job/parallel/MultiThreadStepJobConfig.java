package com.example.spring_batch_ex.job.parallel;

import com.example.spring_batch_ex.dto.AmountDto;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.io.File;
import java.io.IOException;

/**
 * 단일 프로세스에서 청크 단위를 병렬 처리한다.
 */
@Configuration
@AllArgsConstructor
public class MultiThreadStepJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job multiThreadStepJob(@Qualifier("multiThreadStep") Step muliThreadStep){
        return jobBuilderFactory.get("multiThreadStepJob")
                .incrementer(new RunIdIncrementer())
                .start(muliThreadStep)
                .build();
    }

    @Bean
    @JobScope
    public Step multiThreadStep(FlatFileItemReader<AmountDto> amountDtoFlatFileItemReader,
                                ItemProcessor<AmountDto, AmountDto> amountDtoItemProcessor,
                                FlatFileItemWriter<AmountDto> amountDtoFlatFileItemWriter,
                                TaskExecutor taskExecutor){

        return stepBuilderFactory.get("multiThreadStep")
                .<AmountDto, AmountDto> chunk(10)
                .reader(amountDtoFlatFileItemReader)
                .processor(amountDtoItemProcessor)
                .writer(amountDtoFlatFileItemWriter)
                .taskExecutor(taskExecutor)
                .build();
    }

    @StepScope
    @Bean
    public FlatFileItemReader<AmountDto> amountDtoFlatFileItemReader(){
        return new FlatFileItemReaderBuilder<AmountDto>()
                .name("amountDtoFlatFileItemReader")
                .fieldSetMapper(new AmountFieldSetMapper())
                .lineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB))
                .resource(new FileSystemResource("data/input.txt"))
                .build();
    }
    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("spring-batch-task-executor");
        taskExecutor.setConcurrencyLimit(4);

        return taskExecutor;
    }
    @StepScope
    @Bean
    public ItemProcessor<AmountDto, AmountDto> amountDtoItemProcessor(){
        return item -> {
            System.out.println(item + "\tThread = " + Thread.currentThread().getName());
            item.setAmount(item.getAmount() * 100);
            return item;
        };
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<AmountDto> amountDtoFlatFileItemWriter() throws IOException {
        BeanWrapperFieldExtractor<AmountDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"index", "name", "amount"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<AmountDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setFieldExtractor(fieldExtractor);

        String filePath = "data/output.txt";
        new File(filePath).createNewFile();
        return new FlatFileItemWriterBuilder<AmountDto>()
                .name("amountDtoFlatFileItemWriter")
                .resource(new FileSystemResource(filePath))
                .lineAggregator(lineAggregator)
                .build();
    }
}
