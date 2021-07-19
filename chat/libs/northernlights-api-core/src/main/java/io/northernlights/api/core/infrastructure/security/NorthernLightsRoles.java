package io.northernlights.api.core.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NorthernLightsRoles extends ArrayList<NorthernLightsRoles.NorthernLightsRole> {
    public static final NorthernLightsRoleType ROLE_CHATTER = NorthernLightsRoleType.CHATTER;

    public NorthernLightsRoles() {
    }

    public NorthernLightsRoles(Collection<? extends NorthernLightsRole> c) {
        super(c);
    }

    public enum NorthernLightsRoleType {
        CHATTER("chatter");//, ADMIN("admin")

        public final String type;

        NorthernLightsRoleType(String type) {
            this.type = type;
        }
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class NorthernLightsRole {
        private String type;
        private List<String> permissions;
        private NorthernLightsProfile profile;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class NorthernLightsProfile {
        private String uid;
    }
}
