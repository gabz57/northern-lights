package io.northernlights.chat.store.r2dbc;

import io.r2dbc.spi.Clob;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Converts SQL CLOB to String
 * As Clob can be large, one may not be willing to read all CLOB this way,
 * get rid of this converter and use specialized Row -> POJO converter
 * NOTE: one may also prefer to use CharSequence in POJO to avoid adding any Converter
 */
@ReadingConverter
public class ClobToStringConverter implements Converter<Clob, String> {

    public String convert(@Nullable Clob clob) {
        return Optional.ofNullable(clob)
            .map(Clob::stream)
            .map(Mono::from)
            .map(Mono::block)
            .map(CharSequence::toString)
            .orElse(null);
    }
}
