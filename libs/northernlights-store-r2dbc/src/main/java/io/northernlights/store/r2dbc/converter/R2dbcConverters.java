package io.northernlights.store.r2dbc.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext;
import org.springframework.data.relational.core.mapping.NamingStrategy;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class R2dbcConverters {

    private static final ZoneId PARIS_ZONE_ID = ZoneId.of("Europe/Paris");
    public static final ObjectMapper R2DBC_OBJECT_MAPPER = JsonMapper.builder() // or different mapper for other format
//        .addModule(new ParameterNamesModule())
//        .addModule(new Jdk8Module())
        .addModule(new JavaTimeModule())
        // and possibly other configuration, modules, then:
        .build();;

    public static MappingR2dbcConverter mappingR2dbcConverter(NamingStrategy namingStrategy, List<Object> customConverters, R2dbcDialect dialect) {
        R2dbcMappingContext mappingContext = new R2dbcMappingContext(namingStrategy);

        List<Object> converters = new ArrayList<>(dialect.getConverters());
        converters.addAll(R2dbcCustomConversions.STORE_CONVERTERS);
        R2dbcCustomConversions r2dbcCustomConversions = new R2dbcCustomConversions(CustomConversions.StoreConversions.of(dialect.getSimpleTypeHolder(), converters), customConverters);

        mappingContext.setSimpleTypeHolder(r2dbcCustomConversions.getSimpleTypeHolder());

        return new MappingR2dbcConverter(mappingContext, r2dbcCustomConversions);
    }

}
