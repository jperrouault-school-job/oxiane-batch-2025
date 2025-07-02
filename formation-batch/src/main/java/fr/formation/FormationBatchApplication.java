package fr.formation;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FormationBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(FormationBatchApplication.class, args);
    }

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job stringJob;

    @Bean
    CommandLineRunner runner() {
        return (args) -> {
            JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters()
            ;
            
            jobLauncher.run(stringJob, params);
            
            params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters()
            ;
            jobLauncher.run(stringJob, params);
        };
    }
}
