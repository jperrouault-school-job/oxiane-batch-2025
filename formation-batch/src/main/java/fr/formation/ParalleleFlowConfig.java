package fr.formation;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ParalleleFlowConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    Step step1() {
        return new StepBuilder("step1", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("Démarrage STEP 1");
                Thread.sleep(500);
                System.out.println("FIN STEP 1");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build()
        ;
    }
    
    @Bean
    Step step2() {
        return new StepBuilder("step2", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("Démarrage STEP 2");
                Thread.sleep(3_000);
                System.out.println("FIN STEP 2");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build()
        ;
    }
    
    @Bean
    Step step3() {
        return new StepBuilder("step3", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("STEP 3 FINAL");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build()
        ;
    }

    @Bean
    Flow flow1(Step step1) {
        return new FlowBuilder<Flow>("flow1")
            .start(step1)
            .build()
        ;
    }

    @Bean
    Flow flow2(Step step2) {
        return new FlowBuilder<Flow>("flow2")
            .start(step2)
            .build()
        ;
    }

    @Bean
    Flow paralleleFlow(Flow flow1, Flow flow2) {
        return new FlowBuilder<Flow>("paralleleFlow")
            .split(new SimpleAsyncTaskExecutor())
            .add(flow1, flow2)
            .build()
        ;
    }

    @Bean
    Job paralleleJob(Flow paralleleFlow, Step step3) {
        return new JobBuilder("paralleleJob", jobRepository)
            .start(paralleleFlow)
            .next(step3)
            .end()
            .build()
        ;
    }
}
