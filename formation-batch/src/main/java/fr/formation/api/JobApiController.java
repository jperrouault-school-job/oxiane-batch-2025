package fr.formation.api;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
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
   
    @GetMapping("/start")
    public void start(@RequestParam String prefix) throws Exception {
        JobParameters params = new JobParametersBuilder()
            .addLong("time", System.currentTimeMillis())
            .addString("prefix", prefix)
            .toJobParameters()
        ;

        jobLauncher.run(stringJob, params);
    }
}
