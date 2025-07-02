package fr.formation.listener;

import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class DemoStepListener {
    @OnSkipInProcess
    public void onSkipInProcess(String item, Exception e) {
        System.out.println("❌ Elément " + item + " zappé : " + e.getMessage());
    }
}
