//package io.northernlights.chat.store.r2dbc;
//
//public class CloudSpringUtilsConnectionFactoryBuilder implements ConnectionFactoryBuilder {
//
//    @Override
//    public ConnectionFactory buildConnectionFactory(String schema) {
//        PostgresqlConnectionConfiguration configuration = getPostgresqlConnectionConfigurationBuilder(schema)
//            .build();
//        return new PostgresqlConnectionFactory(configuration);
//    }
//
//    @Override
//    public ConnectionFactory buildSimpleConnectionFactory() {
//        PostgresqlConnectionConfiguration configuration = getPostgresqlConnectionConfigurationBuilder(null)
//            .build();
//        return new PostgresqlConnectionFactory(configuration);
//    }
//
//    protected PostgresqlConnectionConfiguration.Builder getPostgresqlConnectionConfigurationBuilder(String schema) {
//        return PostgresqlConnectionConfiguration
//            .builder()
//            .username(dbUser)
//            .password(dbPassword)
//            .host(dbHost)
//            .port(dbPort)
//            .database(dbName)
//            .schema(schema);
//    }
