package io.northernlights.chat.api.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatApiProperties {

    @NestedConfigurationProperty
    private ChatApiStoreProperties store;
}
