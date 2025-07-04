package fr.formation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@SpringBatchTest
public class IntroBatchConfigTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job stringJob;

    @Autowired
    private ItemProcessor<String, String> stringUppercaseProcessor;

    @Test
    void shouldJobComplete() throws Exception {
        jobLauncherTestUtils.setJob(stringJob);

        JobParameters params = new JobParametersBuilder()
            .addString("prefix", "theprefix")
            .toJobParameters()
        ;
        
        JobExecution execution = jobLauncherTestUtils.launchJob(params);
        
        Assertions.assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
    }
    
    @Test
    void shouldStringStepFailed() {
        jobLauncherTestUtils.setJob(stringJob);
        
        JobParameters params = new JobParametersBuilder()
            .addString("prefix", "theprefix")
            .toJobParameters()
        ;

        JobExecution execution = jobLauncherTestUtils.launchStep("stringStep", params);

        Assertions.assertEquals(BatchStatus.FAILED, execution.getStatus());
    }

    @Test
    void shouldStringProcessorUppercase() throws Exception {
        String value = "Jeremy";
        String expected = "JEREMY";

        String result = stringUppercaseProcessor.process(value);

        Assertions.assertEquals(expected, result);
    }
}
