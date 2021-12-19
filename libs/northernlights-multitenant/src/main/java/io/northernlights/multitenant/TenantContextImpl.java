package io.northernlights.multitenant;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantContextImpl implements TenantContext {

    private Tenant tenant;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" [");
        if (this.tenant == null) {
            sb.append("Null tenant");
        }
        else {
            sb.append("Tenant=").append(this.tenant);
        }
        sb.append("]");
        return sb.toString();
    }
}
