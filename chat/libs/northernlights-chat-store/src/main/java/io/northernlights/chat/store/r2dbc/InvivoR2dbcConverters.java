package io.northernlights.chat.store.r2dbc;

import org.springframework.data.convert.CustomConversions;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.util.Assert;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvivoR2dbcConverters {

    private static final ZoneId PARIS_ZONE_ID = ZoneId.of("Europe/Paris");

    public static MappingR2dbcConverter mappingR2dbcConverter(Optional<NamingStrategy> namingStrategy, List<Object> customConverters) {
        Assert.notNull(namingStrategy, "NamingStrategy must not be null!");

        R2dbcMappingContext mappingContext = new R2dbcMappingContext(namingStrategy.orElse(NamingStrategy.INSTANCE));

        R2dbcDialect dialect = MySqlDialect.INSTANCE;
        List<Object> converters = new ArrayList<>(dialect.getConverters());
        converters.addAll(R2dbcCustomConversions.STORE_CONVERTERS);
        R2dbcCustomConversions r2dbcCustomConversions = new R2dbcCustomConversions(CustomConversions.StoreConversions.of(dialect.getSimpleTypeHolder(), converters), customConverters);

        mappingContext.setSimpleTypeHolder(r2dbcCustomConversions.getSimpleTypeHolder());

        return new MappingR2dbcConverter(mappingContext, r2dbcCustomConversions);
    }

    public static List<Object> getCustomConverters() {
        // note: adding Clob -> String converter performs reading of all clob fields for any POJO
        // (which might not be desired for all cases)
        // whenever this wouldn't be acceptable, one can add specific converters for Row -> Pojo instead of this single one
        return List.of(new ClobToStringConverter());
    }

}
