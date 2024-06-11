/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.config;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;
import com.bosch.caltool.security.Decryptor;

/**
 * @author ICP1COB
 */
@Configuration
@PropertySource("classpath:/application.properties")
public class DataSourceConfig {

  @Value("${ssd.login}")
  private String ssdLogin;

  @Value("${ssd.user}")
  private String ssdUser;

  @Value("${ssd.passwordKey}")
  private String passwordKey;

  private final ILoggerAdapter logger = new Log4JLoggerAdapterImpl(LogManager.getLogger(DataSourceConfig.class));

  private static final String DB_CONNECTION_PREFIX = "jdbc:oracle:thin:@";

  /**
   * @return DataSource
   * @throws PasswordNotFoundException - exception
   */
  @Bean
  public DataSource getDataSource() throws PasswordNotFoundException {
    DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.url(DB_CONNECTION_PREFIX.concat(getDataSourceUrl(this.ssdLogin)));
    dataSourceBuilder.username(this.ssdUser);
    dataSourceBuilder.password(getDataSourceUrl(this.passwordKey));
    return dataSourceBuilder.build();
  }

  private String getDataSourceUrl(final String key) throws PasswordNotFoundException {
    PasswordService passwordService = new PasswordService();
    return Decryptor.INSTANCE.decrypt(passwordService.getPassword(key), this.logger);
  }
}
