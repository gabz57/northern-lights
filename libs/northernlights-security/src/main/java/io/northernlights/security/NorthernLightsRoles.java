package io.northernlights.security;

import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

import static io.northernlights.security.NorthernLightsRoles.*;

public class NorthernLightsRoles extends ArrayList<Role> {
    public static final RoleType ROLE_CHATTER = RoleType.CHATTER;

    public NorthernLightsRoles() {
    }

    public NorthernLightsRoles(Collection<? extends Role> c) {
        super(c);
    }

    @Getter
    public enum RoleType {
        CHATTER("CHATTER");//, ADMIN("admin")

        public final String type;

        RoleType(String type) {
            this.type = type;
        }
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Role {
        private RoleType type;
//        private List<String> permissions;
//        private NorthernLightsProfile profile;
    }

//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Data
//    public static class NorthernLightsProfile {
//        private String uid;
//    }
}
