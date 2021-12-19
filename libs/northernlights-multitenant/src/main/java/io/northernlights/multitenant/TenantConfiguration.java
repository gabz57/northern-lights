package io.northernlights.multitenant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantConfiguration {

    // @RefreshScope
    @ConfigurationProperties(prefix = "tenants")
    public TenantsProperties tenantsProperties() {
        return new TenantsProperties();
    }
}
