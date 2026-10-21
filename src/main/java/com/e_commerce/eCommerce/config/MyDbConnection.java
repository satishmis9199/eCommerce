package com.e_commerce.eCommerce.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@SuppressWarnings("")
@Configuration
public class MyDbConnection{
    @Bean("getDbConnection1")
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties getDbConnection1(){
        log.info("Intializing a DataSource");
        return new DataSourceProperties();
    }
    @Bean("Db1Hikari")
    public HikariDataSource Db1Hikari(@Qualifier("getDbConnection1") DataSourceProperties sourceProperties){
        log.info("Intializing a Db1Hikari");
        HikariDataSource source=createAPollConnection(sourceProperties,"eCommerce");
        try(Connection connection=source.getConnection()){
            log.info("Connection Established");
            log.info("Database URL: {}", connection.getMetaData().getURL());
            log.info("Database Name: {}", connection.getCatalog());
            log.info("Schema Name: {}", connection.getSchema());
            log.info("Connection Valid: {}", connection.isValid(5));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return source;
    }
    @Bean("abstractShard")
    public ShardRoutingDataSource abstractShard(@Qualifier("Db1Hikari")DataSource source){
        ShardRoutingDataSource source1=new ShardRoutingDataSource();
        HashMap<Object,Object> target=new HashMap<>();
        target.put(DbRoute.SHARD1_PRIMARY,source);
    source1.setTargetDataSources(target);
    source1.setLenientFallback(false);
    return source1;
    }

    @Bean("dataSource")
    @Primary
    public DataSource dataSource(@Qualifier("abstractShard")ShardRoutingDataSource source){
        return new LazyConnectionDataSourceProxy(source);
    }
    private HikariDataSource createAPollConnection(DataSourceProperties sourceProperties, String eCommerce) {
        HikariDataSource source=sourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        return source;
    }

}