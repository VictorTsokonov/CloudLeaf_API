package com.cloudleafapi.claoudleaf.DatasourceRouting;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected DbType determineCurrentLookupKey() {
		return DbContextHolder.getDbType();
	}

}
