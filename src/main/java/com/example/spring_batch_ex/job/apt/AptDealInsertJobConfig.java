package com.example.spring_batch_ex.job.apt;

import com.example.spring_batch_ex.adapter.ApartmentApiResource;
import com.example.spring_batch_ex.core.repository.LawdRepository;
import com.example.spring_batch_ex.dto.AptDealDto;
import com.example.spring_batch_ex.job.validatior.FilePathParameterValidator;
import com.example.spring_batch_ex.job.validatior.LawdCdParameterValidator;
import com.example.spring_batch_ex.job.validatior.YearMonthParameterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.AnnotationTargetKind;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.net.MalformedURLException;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AptDealInsertJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ApartmentApiResource apartmentApiResource;
    private final LawdRepository lawdRepository;

    @Bean
    public Job aptDealInsertJob(
            Step guLawdCdStep,
            Step stepDealInsertStep,
            Step contextPrintStep) {
        return jobBuilderFactory.get("aptDealInsertJob")
                .incrementer(new RunIdIncrementer())
                .validator(apiDealJobParameterValidator())
                .start(guLawdCdStep)
                .next(contextPrintStep)
                .next(stepDealInsertStep)
                .build();
    }

    private JobParametersValidator apiDealJobParameterValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(
                new YearMonthParameterValidator()
        ));
        return validator;
    }

    @JobScope
    @Bean
    public Step stepDealInsertStep(
            StaxEventItemReader<AptDealDto> aptDealDtoStaxEventItemReader,
            ItemWriter<AptDealDto> aptDealWriter
    ) {
        return stepBuilderFactory.get("aptDealInsertStep")
                .<AptDealDto, AptDealDto>chunk(10)
                .reader(aptDealDtoStaxEventItemReader)
                .writer(aptDealWriter)
                .build();
    }

    @JobScope
    @Bean
    public Step guLawdCdStep(Tasklet guLawdCdTasklet) {
        return stepBuilderFactory.get("guLawdCdStep")
                .tasklet(guLawdCdTasklet)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet guLawdCdTasklet() {
        return (contribution, chunkContext) -> {

            StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
            ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
            List<String> guLawdCds = lawdRepository.findDistinctGuLawdCd();
            executionContext.putString("guLawdCd", guLawdCds.get(0));
            return RepeatStatus.FINISHED;
        };
    }

    @JobScope
    @Bean
    public Step contextPrintStep(Tasklet contextPrintTasklet) {
        return stepBuilderFactory.get("contextPrintStep")
                .tasklet(contextPrintTasklet)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet contextPrintTasklet(
            @Value("#{jobExecutionContext['guLawdCd']}") String guLawdCd
    ) {
        return ((contribution, chunkContext) -> {
            System.out.println("[contextPrintStep] guLawdCd = " + guLawdCd);
            return RepeatStatus.FINISHED;
        });
    }

    @StepScope
    @Bean
    public StaxEventItemReader<AptDealDto> aptDealResourceReader(
            @Value("#{jobExecutionContext['guLawdCd']}") String guLawdCd,
            @Value("#{jobParameters['yearMonth']}") String yearMonth,
            Jaxb2Marshaller aptDealDtoMarshaller
    ) throws MalformedURLException {
        return new StaxEventItemReaderBuilder<AptDealDto>()
                .name("aptDealResourceReader")
                .resource(apartmentApiResource.getResource(guLawdCd, YearMonth.parse(yearMonth)))
                .addFragmentRootElements("item")
                .unmarshaller(aptDealDtoMarshaller)
                .build();
    }

    @StepScope
    @Bean
    public Jaxb2Marshaller aptDealDtoMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(AptDealDto.class); // xml에 필요한 클래스 설정
        return jaxb2Marshaller;
    }

    @StepScope
    @Bean
    public ItemWriter<AptDealDto> aptDealWriter() {
        return items -> {
            items.forEach(System.out::println);
        };
    }
}
