package com.cloudleafapi.claoudleaf.DatasourceRouting;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

	@Primary
	@Bean(name = "routingDataSource")
	public DataSource dataSource() {
		RoutingDataSource routingDataSource = new RoutingDataSource();

		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put(DbType.PRIMARY, primaryDataSource());
		targetDataSources.put(DbType.REPLICA, replicaDataSource());

		routingDataSource.setTargetDataSources(targetDataSources);
		routingDataSource.setDefaultTargetDataSource(primaryDataSource());

		return routingDataSource;
	}

	@Bean(name = "primaryDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-primary")
	public DataSource primaryDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "replicaDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-replica")
	public DataSource replicaDataSource() {
		return DataSourceBuilder.create().build();
	}

}