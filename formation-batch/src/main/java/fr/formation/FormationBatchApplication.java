package fr.formation;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
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
            // JobParameters params = new JobParametersBuilder()
            //     .addLong("time", System.currentTimeMillis())
            //     .toJobParameters()
            // ;
            
            // jobLauncher.run(stringJob, params);

            WatchService ws = FileSystems.getDefault().newWatchService();

            Path p = Paths.get("/workspace");

            p.register(ws, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {
                WatchKey key = ws.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println("Evènement = " + event.kind() + ", fichier affecté = " + event.context() + ".");
                    
                    // JobParameters params = new JobParametersBuilder()
                    //     .addLong("time", System.currentTimeMillis())
                    //     .toJobParameters()
                    // ;
                    
                    // jobLauncher.run(stringJob, params);
                }

                boolean valid = key.reset();
                
                if (!valid) {
                    System.out.println("Clé de surveillance invalide, arrêt.");
                    break;
                }
            }
        };
    }

    // @Scheduled(fixedDelay = 2000)
    // public void schedule() {
    //     JobParameters params = new JobParametersBuilder()
    //         .addLong("time", System.currentTimeMillis())
    //         .toJobParameters()
    //     ;
        
    //     try {
    //         jobLauncher.run(stringJob, params);
    //     }

    //     catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
}
