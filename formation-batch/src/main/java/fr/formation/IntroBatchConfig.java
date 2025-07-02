package fr.formation;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class IntroBatchConfig {
    @Bean
    ItemReader<String> stringReader() {
        List<String> valeurs = List.of("Alpha", "Bravo", "Charlie", "Delta", "Echo", "Fox-trot", "Golf", "Hotel", "India", "Juliette", "Kilo", "Lima", "Mike", "Novembre", "Oscar", "Papa", "Quebec", "Romeo", "Sierra", "Tando", "Uniform", "Victor", "Whisky", "X-Ray", "Yankee", "Zoulou");

        return new IteratorItemReader<>(valeurs);
    }

    @Bean
    ItemProcessor<String, String> stringUppercaseProcessor() {
        // return new StringUpperCaseProcessor();
        return item -> item.toUpperCase();
    }

    @Bean
    ItemWriter<String> stringPrintWriter() {
        return items -> {
            System.out.println("--- Nouveau chunk ---");

            for (String item : items) {
                System.out.println(item);
            }
        };
    }

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    Step stringStep(ItemReader<String> stringReader, ItemProcessor<String, String> stringUppercaseProcessor, ItemWriter<String> stringPrintWriter) {
        return new StepBuilder("stringStep", jobRepository)
            .<String, String>chunk(2, transactionManager)
            .reader(stringReader)
            .processor(stringUppercaseProcessor)
            .writer(stringPrintWriter)
            .build()
        ;
    }

    @Bean
    Job stringJob(Step stringStep) {
        return new JobBuilder("stringJob", jobRepository)
            .start(stringStep)
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
