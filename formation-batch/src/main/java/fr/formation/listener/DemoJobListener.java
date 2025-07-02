package fr.formation.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class DemoJobListener implements JobExecutionListener {
    @Override
    public void beforeJob(@NonNull JobExecution jobExecution) {
        System.out.println("📥 Avant l'exécution du Job");
    }

    @Override
    public void afterJob(@NonNull JobExecution jobExecution) {
        System.out.println("✅ Après l'exécution du Job");
    }
}
