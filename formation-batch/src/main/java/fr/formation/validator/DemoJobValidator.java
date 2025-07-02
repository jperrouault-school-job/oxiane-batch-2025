package fr.formation.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.lang.Nullable;

public class DemoJobValidator implements JobParametersValidator {
    @Override
    public void validate(@Nullable JobParameters parameters) throws JobParametersInvalidException {
        if (parameters == null) {
            return;
        }

        String prefix = parameters.getString("prefix");

        if (prefix == null || prefix.isBlank()) {
            throw new JobParametersInvalidException("Le paramètre prefix est obligatoire !");
        }
    }
}
