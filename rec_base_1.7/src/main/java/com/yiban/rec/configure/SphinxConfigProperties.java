package com.yiban.rec.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "sphinx")
@Component
public class SphinxConfigProperties {
	
	private String dbUrl;
	private String max_matches;
	
	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	public String getMax_matches() {
		return max_matches;
	}
	public void setMax_matches(String max_matches) {
		this.max_matches = max_matches;
	}
	
	/*private final String DERIVER_CLASS_NAME="com.mysql.jdbc.Driver";
	@Component
	@ConfigurationProperties(prefix = "sphinx")
	public static class Config{
	private String dbUrl;
	private String max_matches;
	
	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	public String getMax_matches() {
		return max_matches;
	}
	public void setMax_matches(String max_matches) {
		this.max_matches = max_matches;
	}
	}
	@Bean
	public BasicDataSource prodBasicDataSource(Config config){
		BasicDataSource basicDataSource = new BasicDataSource(); 
		basicDataSource.setDriverClassName(DERIVER_CLASS_NAME);
		basicDataSource.setUrl(config.getDbUrl());
		return basicDataSource;
	}
	
	@Bean
	public JdbcTemplateManager jdbcTemplate(BasicDataSource basicDataSource){
		JdbcTemplate jdbcTemplate=new JdbcTemplate(basicDataSource);
		return new JdbcTemplateManagerImpl(jdbcTemplate);
	}*/
}
