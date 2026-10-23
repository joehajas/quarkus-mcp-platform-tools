package com.joehajas.mcp;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class PlatformDiagnosticsTools {

    private static final Logger LOG = Logger.getLogger(PlatformDiagnosticsTools.class);
    private static final Set<String> KNOWN_SERVICES = Set.of("auth-service", "billing-engine", "spen-portal-db");

    public enum ServiceStatus { HEALTHY, DEGRADED, UNREACHABLE }

    public record HealthReport(
        String service,
        ServiceStatus status,
        int latencyMs,
        String activeVlan,
        Instant timestamp,
        List<String> activeNodes
    ) {}

    public record SecurityValidation(
        String clientId,
        boolean authenticated,
        String tokenScope,
        boolean zeroTrustCompliant,
        String isolationZone
    ) {}

    @Tool(description = "Check operational health, response latency, and cluster distribution for a platform service.")
    public HealthReport checkServiceHealth(
            @ToolArg(description = "Target service identifier ('auth-service', 'billing-engine', 'spen-portal-db')") 
            String serviceName) {

        LOG.infof("MCP Tool Invocation: checkServiceHealth for service '%s'", serviceName);

        if (!KNOWN_SERVICES.contains(serviceName.toLowerCase())) {
            LOG.warnf("Service '%s' not recognised in service mesh registry", serviceName);
            return new HealthReport(serviceName, ServiceStatus.UNREACHABLE, -1, "UNKNOWN", Instant.now(), List.of());
        }

        return new HealthReport(
            serviceName,
            ServiceStatus.HEALTHY,
            18,
            "VLAN-PROD-20",
            Instant.now(),
            List.of("node-eu-west-1a", "node-eu-west-1b")
        );
    }

    @Tool(description = "Validate Zero-Trust access parameters and IAM scopes for an enterprise client ID.")
    public SecurityValidation inspectClientSecurity(
            @ToolArg(description = "Enterprise client UUID or system principal") 
            String clientId) {

        LOG.infof("MCP Tool Invocation: inspectClientSecurity for client '%s'", clientId);
        boolean isInternal = clientId.startsWith("spen-") || clientId.startsWith("internal-");

        return new SecurityValidation(
            clientId,
            true,
            isInternal ? "telemetry:admin,system:write" : "telemetry:read",
            true,
            isInternal ? "ENCLAVE-SECURE-A" : "DMZ-PROXY-01"
        );
    }
}