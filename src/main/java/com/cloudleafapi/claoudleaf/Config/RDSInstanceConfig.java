package com.cloudleafapi.claoudleaf.Config;

import org.springframework.cloud.aws.jdbc.config.annotation.RdsInstanceConfigurer;
import org.springframework.cloud.aws.jdbc.datasource.TomcatJdbcDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RDSInstanceConfig {

	@Bean
	public RdsInstanceConfigurer instanceConfigurer() {
		return () -> {
			TomcatJdbcDataSourceFactory dataSourceFactory = new TomcatJdbcDataSourceFactory();
			dataSourceFactory.setInitialSize(10);
			dataSourceFactory.setValidationQuery("SELECT 1 FROM DUAL");
			return dataSourceFactory;
		};
	}

}