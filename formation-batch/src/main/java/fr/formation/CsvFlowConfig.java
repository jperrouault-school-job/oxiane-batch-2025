package fr.formation;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CsvFlowConfig {
    @Bean
    ItemWriter<String> stringToCsvWriter() {
        FlatFileItemWriter<String> writer = new FlatFileItemWriter<>();

        writer.setResource(new FileSystemResource("/workspace/output.csv"));

        writer.setLineAggregator(item -> item);

        return writer;
    }

    @Bean
    JobExecutionDecider writeCsvOrNoDecider() {
        return (jobExecution, stepExecution) -> {
            String writeCsv = jobExecution.getJobParameters().getString("writeCsv");

            if ("true".equalsIgnoreCase(writeCsv)) {
                return new FlowExecutionStatus("WRITE");
            }
            
            return new FlowExecutionStatus("SKIP_WRITE");
        };
    }

    @Bean
    Step writeToCsvStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemReader<String> stringReader, ItemProcessor<String, String> stringUppercaseProcessor, ItemWriter<String> stringToCsvWriter) {
        return new StepBuilder("writeToCsvStep", jobRepository)
            .<String, String>chunk(3, transactionManager)
            .reader(stringReader)
            .processor(stringUppercaseProcessor)
            .writer(stringToCsvWriter)
            .build()
        ;
    }

    @Bean
    Flow flowToCsvOrNo(JobExecutionDecider writeCsvOrNoDecider, Step writeToCsvStep) {
        return new FlowBuilder<Flow>("flowToCsvOrNo")
            .start(writeCsvOrNoDecider)
                .on("WRITE").to(writeToCsvStep)
                .from(writeCsvOrNoDecider).on("*").end()
            .end()
        ;
    }
}
