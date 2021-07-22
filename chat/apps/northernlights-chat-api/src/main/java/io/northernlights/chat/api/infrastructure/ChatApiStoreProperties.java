package io.northernlights.chat.api.infrastructure;

import io.northernlights.chat.store.r2dbc.StoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatApiStoreProperties extends StoreProperties {

    @NestedConfigurationProperty
    private StoreProperties conversation;
    @NestedConfigurationProperty
    private StoreProperties chatter;
//    @NestedConfigurationProperty
//    private StoreProperties client;
}
