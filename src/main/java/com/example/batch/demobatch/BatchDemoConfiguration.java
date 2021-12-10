package com.example.batch.demobatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class BatchDemoConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private TaskletStep stepTest() {
        Tasklet tasklet = (contribution, context) -> {
            log.info("This is from tasklet step with parameter -> "
                    + context.getStepContext().getJobParameters().get("message"));
            return RepeatStatus.FINISHED;
        };
        return stepBuilderFactory.get("stepTest").tasklet(tasklet).build();
    }

    // java -Dspring.batch.job.names=job1 -jar demo-batch-0.0.1-SNAPSHOT.jar message=foo
    @Bean
    public Job job1() {
        return jobBuilderFactory.get("job1")
                .incrementer(new RunIdIncrementer())
                .start(stepTest()).build();
    }
}
