package com.multitenant.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@MapperScan("com.multitenant.repository.mapper")
@AllArgsConstructor
public class MultiTenantConfiguration {
    private final static Logger logger = LoggerFactory.getLogger(MultiTenantConfiguration.class);

    @Bean
    @ConfigurationProperties(prefix = "tenants")
    public DataSource dataSource() throws IOException {
        logger.info("Setting up multiple tenant data sources");

        Map<Object, Object> resolvedDataSources = new HashMap<>();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:tenants/*");

        for (Resource resource : resources) {
            Properties tenantProperties = new Properties();

            logger.info("Building datasource from file {}", resource.getFilename());
            try {
                tenantProperties.load(resource.getInputStream());
                String tenantId = tenantProperties.getProperty("name");

                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(tenantProperties.getProperty("datasource.url"));
                config.setUsername(tenantProperties.getProperty("datasource.username"));
                config.setPassword(tenantProperties.getProperty("datasource.password"));
                config.setDriverClassName(tenantProperties.getProperty("datasource.driver-class-name"));

                resolvedDataSources.put(tenantId, new HikariDataSource(config));

                logger.info("Datasource for tenant {} build", tenantId);
            } catch (IOException e) {
                throw new RuntimeException("Problem in tenant datasource: " + e.getMessage());
            }
        }

        AbstractRoutingDataSource dataSource = new MultiTenantDataSource();
        dataSource.setTargetDataSources(resolvedDataSources);
        dataSource.afterPropertiesSet();

        return dataSource;
    }
}
