package com.example.batch.demobatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class BatchCoffeeConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Value("${file.input}")
    private String fileInput;

    //  T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException;
    // java -Dspring.batch.job.names=importCoffeeJob -jar demo-batch-0.0.1-SNAPSHOT.jar
    @Bean
    public FlatFileItemReader<Coffee> reader() {
        return new FlatFileItemReaderBuilder<Coffee>().name("coffeeItemReader")
                // or ClassPathResource
                .resource(new ClassPathResource(fileInput))
                .delimited()
                .names("brand", "origin", "characteristics")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Coffee>() {{
                    setTargetType(Coffee.class);
                }})
                .build();
    }

    // O process(@NonNull I var1) throws Exception;
    @Bean
    public CoffeeItemProcessor processor() {
        return new CoffeeItemProcessor();
    }

    // void write(List<? extends T> var1) throws Exception;
    @Bean
    public CoffeeItemWriter customWriter() {
        return new CoffeeItemWriter();
    }

    // void write(List<? extends T> var1) throws Exception;
    @Bean
    public JdbcBatchItemWriter<Coffee> writerJdbc(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Coffee>().itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO coffee (brand, origin, characteristics) VALUES (:brand, :origin, :characteristics)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importCoffeeJob(JobCompletionNotificationListener listener, Step step1importCoffeeJob) {
        return jobBuilderFactory.get("importCoffeeJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1importCoffeeJob)
                .end()
                .build();
    }

    @Bean
    public CompositeItemWriter<Coffee> compositeItemWriter(JdbcBatchItemWriter<Coffee> writerJdbc) {
        CompositeItemWriter<Coffee> writer = new CompositeItemWriter<>();
        writer.setDelegates(Arrays.asList(writerJdbc, customWriter()));
        return writer;
    }

    // tester ça : https://www.desynit.com/dev-zone/java/spring-batch-log-the-record-count-during-processing/
    @Bean
    public Step step1importCoffeeJob(CompositeItemWriter<Coffee> compositeItemWriter) {
        return stepBuilderFactory.get("step1importCoffeeJob")
                .<Coffee, Coffee>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(compositeItemWriter)
                .build();
    }
}
