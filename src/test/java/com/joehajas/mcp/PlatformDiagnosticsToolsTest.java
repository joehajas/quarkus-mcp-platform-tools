package com.joehajas.mcp;

import com.joehajas.mcp.PlatformDiagnosticsTools.HealthReport;
import com.joehajas.mcp.PlatformDiagnosticsTools.SecurityValidation;
import com.joehajas.mcp.PlatformDiagnosticsTools.ServiceStatus;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PlatformDiagnosticsToolsTest {

    private static final Logger LOG = Logger.getLogger(PlatformDiagnosticsToolsTest.class);

    @Inject
    PlatformDiagnosticsTools diagnosticsTools;

    @Test
    @DisplayName("Should return HEALTHY status and valid latency for platform service")
    void testCheckServiceHealth() {
        LOG.info("=== [TEST] Starting testCheckServiceHealth ===");
        HealthReport report = diagnosticsTools.checkServiceHealth("billing-engine");

        assertNotNull(report);
        assertEquals("billing-engine", report.service());
        assertEquals(ServiceStatus.HEALTHY, report.status());
        assertTrue(report.latencyMs() > 0);
        assertNotNull(report.timestamp());

        LOG.infof("=== [TEST] Success: Service %s is %s with latency %d ms ===", 
                report.service(), report.status(), report.latencyMs());
    }

    @Test
    @DisplayName("Should enforce Zero-Trust policy and return valid scopes for client ID")
    void testInspectClientSecurity() {
        LOG.info("=== [TEST] Starting testInspectClientSecurity ===");
        SecurityValidation validation = diagnosticsTools.inspectClientSecurity("client-vendor-99");

        assertNotNull(validation);
        assertEquals("client-vendor-99", validation.clientId());
        assertTrue(validation.authenticated());
        assertTrue(validation.zeroTrustCompliant());

        LOG.infof("=== [TEST] Success: Client %s verified in zone %s ===", 
                validation.clientId(), validation.isolationZone());
    }
}