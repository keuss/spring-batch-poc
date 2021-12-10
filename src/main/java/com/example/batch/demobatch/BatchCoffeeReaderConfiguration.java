package com.example.batch.demobatch;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class BatchCoffeeReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public CoffeeRowMapper customerCofferMapper() {
        return new CoffeeRowMapper();
    }

    @Bean
    public JdbcPagingItemReader<Coffee> itemReader(DataSource customDataSource, PagingQueryProvider queryProvider) {
        log.info("itemReader build");
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("characteristics", "Strong");

        return new JdbcPagingItemReaderBuilder<Coffee>()
                .name("itemReader")
                .dataSource(customDataSource)
                .queryProvider(queryProvider)
                .parameterValues(parameterValues)
                .rowMapper(customerCofferMapper())
                .pageSize(1)
                .build();
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean queryProvider(DataSource customDataSource) {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();

        provider.setDataSource(customDataSource);
        provider.setSelectClause("select brand, origin, characteristics");
        provider.setFromClause("from coffee");
        provider.setWhereClause("where characteristics=:characteristics");
        provider.setSortKey("brand");

        log.info("queryProvider build");
        return provider;
    }

    @Bean
    public Step step1readCoffeeJob(JdbcPagingItemReader<Coffee> reader, CoffeeItemProcessor processor, CoffeeItemWriter customWriter) {

        log.info("step1readCoffeeJob build");
        return stepBuilderFactory.get("step1readCoffeeJob")
                .<Coffee, Coffee>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(customWriter)
                .build();
    }


    @Bean
    public Job readCoffeeJob(Step step1readCoffeeJob) {
        log.info("readCoffeeJob build");
        return jobBuilderFactory.get("readCoffeeJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1readCoffeeJob)
                .end()
                .build();
    }
}
