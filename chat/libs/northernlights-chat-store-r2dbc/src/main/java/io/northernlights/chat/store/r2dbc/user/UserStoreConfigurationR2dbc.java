package io.northernlights.chat.store.r2dbc.user;

import io.northernlights.chat.store.user.UserStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserStoreConfigurationR2dbc {

    @Bean
    public UserStore userStore(UserRepository userRepository) {
        return new R2dbcUserStore(userRepository);
    }
}
