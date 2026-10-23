package com.joehajas.mcp;

import io.quarkiverse.mcp.server.Prompt;
import io.quarkiverse.mcp.server.PromptArg;
import io.quarkiverse.mcp.server.PromptMessage;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PlatformPrompts {

    @Prompt(description = "Generates a structured incident triage workflow combining platform telemetry and security boundaries.")
    public List<PromptMessage> triageIncident(
            @PromptArg(description = "The failing component or service name") String component,
            @PromptArg(description = "Observed symptom or error code") String symptom) {

        String systemPrompt = """
            You are an autonomous Platform SRE Agent. 
            Investigate the reported incident using the available diagnostic tools:
            1. Query 'checkServiceHealth' for the component '%s'.
            2. Validate caller identity using 'inspectClientSecurity'.
            3. Produce a root-cause hypothesis and evaluate against Zero-Trust network rules.
            Reported Symptom: %s
            """.formatted(component, symptom);

        return List.of(PromptMessage.withUserRole(systemPrompt));
    }
}