package io.northernlights.chat.store.r2dbc;

import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataModel;
import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataTypeConverter;
import io.northernlights.store.r2dbc.converter.ClobToStringConverter;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Duration;
import java.util.List;

import static io.northernlights.store.r2dbc.converter.R2dbcConverters.*;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Slf4j
@Configuration
@EnableR2dbcRepositories(
    basePackages = "io.northernlights.chat.store.r2dbc",
    entityOperationsRef = "northernlightsChatStoreEntityOperations"
)
@EnableTransactionManagement
public class R2dbcChatStoreConfiguration extends AbstractR2dbcConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "chat.store")
    public StoreProperties chatStoreProperties() {
        return new StoreProperties();
    }
//
//    @Bean
//    public ConnectionFactory connectionFactory(StoreProperties chatStoreProperties) {
//        /*
//         * // Notice: the query string must be URL encoded
//         * ConnectionFactory connectionFactory = ConnectionFactories.get(
//         *     "r2dbcs:pool:mysql://root:database-password-in-here@127.0.0.1:3306/r2dbc?" +
//         *     "r2dbcs:mysql://root:database-password-in-here@127.0.0.1:3306/r2dbc?" +
//         *         "zeroDate=use_round&" +
//         *         "sslMode=verify_identity&" +
//         *         "useServerPrepareStatement=true&" +
//         *         "tlsVersion=TLSv1.3%2CTLSv1.2%2CTLSv1.1&" +
//         *         "sslCa=%2Fpath%2Fto%2Fmysql%2Fca.pem&" +
//         *         "sslKey=%2Fpath%2Fto%2Fmysql%2Fclient-key.pem&" +
//         *         "sslCert=%2Fpath%2Fto%2Fmysql%2Fclient-cert.pem&" +
//         *         "sslKeyPassword=key-pem-password-in-here"
//         * );
//         */
//        return ConnectionFactories.get(storeProperties.getR2dbc().getUrl());
//    }

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        String url = chatStoreProperties().getR2dbc().getUrl();
//        log.info("ConnectionFactory uses r2dbc url: {}", url);
        ConnectionFactoryOptions cfOptions = parse(url);
        PostgresqlConnectionConfiguration.Builder builder = PostgresqlConnectionConfiguration.builder()
            .codecRegistrar(EnumCodec.builder().withEnum("conversation_data_type", ConversationDataModel.ConversationDataType.class).build());

        if (cfOptions.hasOption(CONNECT_TIMEOUT)) {
            builder.connectTimeout((Duration) cfOptions.getRequiredValue(CONNECT_TIMEOUT));
        }
        if (cfOptions.hasOption(DATABASE)) {
            builder.database((String) cfOptions.getRequiredValue(DATABASE));
        }
//        if (cfOptions.hasOption(ConnectionFactoryOptions.DRIVER)) {
//            builder.(parse.getValue(ConnectionFactoryOptions.DRIVER));
//        }
        if (cfOptions.hasOption(HOST)) {
            builder.host((String) cfOptions.getRequiredValue(HOST));
        }
        if (cfOptions.hasOption(PORT)) {
            builder.port((Integer) cfOptions.getRequiredValue(PORT));
        }
        if (cfOptions.hasOption(SSL) &&
            (Boolean) cfOptions.getRequiredValue(SSL)) {
            builder.enableSsl();
        }
        if (cfOptions.hasOption(USER)) {
            builder.username((String) cfOptions.getRequiredValue(USER));
        }
        if (cfOptions.hasOption(PASSWORD)) {
            builder.password((CharSequence) cfOptions.getRequiredValue(PASSWORD));
        }

        R2dbcProperties.Pool poolProperties = chatStoreProperties().getR2dbc().getPool();
        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder(new PostgresqlConnectionFactory(builder.build()))
            .initialSize(poolProperties.getInitialSize())
            .maxSize(poolProperties.getMaxSize())
            .maxIdleTime(poolProperties.getMaxIdleTime())
            .build();
        return new ConnectionPool(poolConfiguration);
    }

    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    public R2dbcEntityOperations northernlightsChatStoreEntityOperations(ObjectProvider<NamingStrategy> namingStrategyProvider, ConnectionFactory connectionFactory) {
        R2dbcDialect dialect = PostgresDialect.INSTANCE;
        NamingStrategy namingStrategy = namingStrategyProvider.getIfAvailable(() -> NamingStrategy.INSTANCE);
        return new R2dbcEntityTemplate(
            DatabaseClient.create(connectionFactory),
            dialect,
            mappingR2dbcConverter(namingStrategy, getCustomConverters(), dialect)
        );
    }

    @Override
    public List<Object> getCustomConverters() {
        return List.of(
            // note: adding Clob -> String converter performs reading of all clob fields for any POJO
            // (which might not be desired for all cases)
            // whenever this wouldn't be acceptable, one can add specific converters for Row -> Pojo instead of this single one
            new ClobToStringConverter(),
            new ConversationDataTypeConverter()
        );
    }
}
