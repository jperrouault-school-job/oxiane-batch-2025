package fr.formation.api;

import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/job")
// http://localhost:8080/api/job/start
public class JobApiController {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job stringJob;

    @Autowired
    private Job paralleleJob;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobOperator jobOperator;
   
    @GetMapping("/start")
    public void start(@RequestParam String prefix, @RequestParam(required = false) String write) throws Exception {
        JobParameters params = new JobParametersBuilder()
            .addLong("time", System.currentTimeMillis())
            .addString("prefix", prefix)
            .addString("writeCsv", write)
            .toJobParameters()
        ;

        // jobLauncher.run(stringJob, params);
        jobLauncher.run(paralleleJob, params);
    }

    @GetMapping("/explore")
    public List<JobInstance> explore() throws Exception {
        List<JobInstance> instances = jobExplorer.findJobInstancesByJobName("stringJob", 0, 100);

        for (JobInstance instance : instances) {
            List<JobExecution> executions = jobExplorer.getJobExecutions(instance);

            for (JobExecution execution : executions) {
                System.out.println("Statut : " + execution.getStatus());
                System.out.println("Paramètres : " + execution.getJobParameters());

                if (execution.getStatus() == BatchStatus.FAILED) {
                    // jobLauncher.run(stringJob, execution.getJobParameters());
                    jobOperator.restart(execution.getId());
                }
            }
        }

        return instances;
    }
}
