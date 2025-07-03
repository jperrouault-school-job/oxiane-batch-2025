package fr.formation;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        JobLauncher jobLauncher = context.getBean(JobLauncher.class);
        Job paralleleJob = context.getBean(Job.class);

        try {
            jobLauncher.run(paralleleJob, new JobParametersBuilder().toJobParameters());
        }
        
        catch (Exception e) {
            e.printStackTrace();
        }

        context.close();
    }
}
