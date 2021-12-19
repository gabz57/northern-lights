package io.northernlights.multitenant;

public interface TenantContext {

    Tenant getTenant();

    void setTenant(Tenant tenant);
}
