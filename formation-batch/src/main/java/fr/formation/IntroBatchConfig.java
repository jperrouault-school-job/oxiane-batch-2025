package fr.formation;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import fr.formation.listener.DemoJobListener;
import fr.formation.listener.DemoStepListener;
import fr.formation.validator.DemoJobValidator;

@Configuration
public class IntroBatchConfig {
    private boolean failure = true;


    @Bean
    @StepScope
    ItemReader<String> stringReader() {
        List<String> valeurs = List.of("Alpha", "Bravo", "Charlie", "Delta", "Echo", "Fox-trot", "Golf", "Hotel", "India", "Juliette", "Kilo", "Lima", "Mike", "Novembre", "Oscar", "Papa", "Quebec", "Romeo", "Sierra", "Tando", "Uniform", "Victor", "Whisky", "X-Ray", "Yankee", "Zoulou");

        return new IteratorItemReader<>(valeurs);
    }

    @Bean
    ItemProcessor<String, String> stringUppercaseProcessor() {
        // return new StringUpperCaseProcessor();
        return item -> {
            if (failure && "Juliette".equals(item)) {
                failure = false;
                throw new RuntimeException("Démo exception Juliette");
            }

            return item.toUpperCase();
        };
    }

    @Bean
    @StepScope
    ItemWriter<String> stringPrintWriter(@Value("#{jobParameters['prefix']}") String prefix) {
        return items -> {
            System.out.println("--- Nouveau chunk ---");

            for (String item : items) {
                System.out.println(prefix + item);
            }
        };
    }

    @Bean
    Tasklet demoTasklet() {
        return (stepContribution, chunkContext) -> {
            System.out.println("Tâche simple pour démonstration.");

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    Step demoTaskletStep(Tasklet demoTasklet) {
        return new StepBuilder("demoTaskletStep", jobRepository)
            .tasklet(demoTasklet, transactionManager)
            .build()
        ;
    }

    @Bean
    Step demoTaskletStep2() {
        return new StepBuilder("demoTaskletStep", jobRepository)
            .tasklet((stepContribution, chunkContext) -> {
                System.out.println("Tâche simple (2) pour démonstration.");

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build()
        ;
    }

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    Step stringStep(ItemReader<String> stringReader, ItemProcessor<String, String> stringUppercaseProcessor, ItemWriter<String> stringPrintWriter, DemoStepListener demoStepListener) {
        return new StepBuilder("stringStep", jobRepository)
            .<String, String>chunk(2, transactionManager)
            .reader(stringReader)
            .processor(stringUppercaseProcessor)
            .writer(stringPrintWriter)

            // Multi-threading : les chunks vont être dans des threads différents
            // .taskExecutor(new SimpleAsyncTaskExecutor())

            // Configuration la tolérence aux erreurs
            // .faultTolerant()
            // .retry(RuntimeException.class)
            // .retryLimit(3)

            // .skip(RuntimeException.class)
            // .skipLimit(1)

            .listener(demoStepListener)

            .build()
        ;
    }

    @Bean
    Flow stringFlow(Step stringStep, Step demoTaskletStep, Step demoTaskletStep2) {
        return new FlowBuilder<Flow>("stringFlow")
            .start(stringStep)
                .on("FAILED").to(demoTaskletStep)
                .from(stringStep).on("*").to(demoTaskletStep2)
            .build()
        ;
    }

    @Bean
    Job stringJob(Flow stringFlow, DemoJobListener demoJobListener) {
        return new JobBuilder("stringJob", jobRepository)
            // .start(stringStep)
            .start(stringFlow)
            .end()
            .listener(demoJobListener)
            .validator(new DemoJobValidator())
            .build()
        ;
    }

    // public static class StringUpperCaseProcessor implements ItemProcessor<String, String> {

    //     @Override
    //     @Nullable
    //     public String process(@NonNull String item) throws Exception {
    //         return item.toUpperCase();
    //     }

    // }
}
