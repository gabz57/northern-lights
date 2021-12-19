package io.northernlights.multitenant;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

@Data
public class TenantsProperties {

    @NestedConfigurationProperty
    private TenantProperties publicTenant;
    private Map<String, TenantProperties> tenants;

    @Data
    public static class TenantProperties {
        private String id;
        private String name;
    }
}
