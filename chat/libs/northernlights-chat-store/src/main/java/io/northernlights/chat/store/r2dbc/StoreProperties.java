package io.northernlights.chat.store.r2dbc;

import lombok.Data;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;

@Data
public class StoreProperties {
    private R2dbcProperties r2dbc;
//    private MongoProperties mongo;
//    private DataSourceProperties jdbc;
}
