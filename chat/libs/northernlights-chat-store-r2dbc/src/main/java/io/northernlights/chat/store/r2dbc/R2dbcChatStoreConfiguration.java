package io.northernlights.chat.store.r2dbc;

import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataModel;
import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataTypeConverter;
import io.northernlights.store.r2dbc.converter.ClobToStringConverter;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
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

import java.util.List;

import static io.northernlights.store.r2dbc.converter.R2dbcConverters.*;

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
        log.info("ConnectionFactory uses r2dbc url: {}", url);
//        return ConnectionFactories.get(url);
        ConnectionFactoryOptions connectionFactoryOptions = ConnectionFactoryOptions.parse(url);
        PostgresqlConnectionConfiguration.Builder builder = PostgresqlConnectionConfiguration.builder()
            .codecRegistrar(EnumCodec.builder().withEnum("conversation_data_type", ConversationDataModel.ConversationDataType.class).build());

        if (connectionFactoryOptions.hasOption(ConnectionFactoryOptions.CONNECT_TIMEOUT)) {
            builder.connectTimeout(connectionFactoryOptions.getRequiredValue(ConnectionFactoryOptions.CONNECT_TIMEOUT));
        }
        if (connectionFactoryOptions.hasOption(ConnectionFactoryOptions.DATABASE)) {
            builder.database(connectionFactoryOptions.getRequiredValue(ConnectionFactoryOptions.DATABASE));
        }
//        if (parse.hasOption(ConnectionFactoryOptions.DRIVER)) {
//            builder.(parse.getValue(ConnectionFactoryOptions.DRIVER));
//        }
        if (connectionFactoryOptions.hasOption(ConnectionFactoryOptions.HOST)) {
            builder.host(connectionFactoryOptions.getRequiredValue(ConnectionFactoryOptions.HOST));
        }
        if (connectionFactoryOptions.hasOption(ConnectionFactoryOptions.PORT)) {
            builder.port(connectionFactoryOptions.getRequiredValue(ConnectionFactoryOptions.PORT));
        }
        if (connectionFactoryOptions.hasOption(ConnectionFactoryOptions.SSL) &&
            connectionFactoryOptions.getRequiredValue(ConnectionFactoryOptions.SSL)) {
            builder.enableSsl();
        }
        if (connectionFactoryOptions.hasOption(ConnectionFactoryOptions.USER)) {
            builder.username(connectionFactoryOptions.getRequiredValue(ConnectionFactoryOptions.USER));
        }
        if (connectionFactoryOptions.hasOption(ConnectionFactoryOptions.PASSWORD)) {
            builder.password(connectionFactoryOptions.getRequiredValue(ConnectionFactoryOptions.PASSWORD));
        }
        return new PostgresqlConnectionFactory(builder.build());
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
