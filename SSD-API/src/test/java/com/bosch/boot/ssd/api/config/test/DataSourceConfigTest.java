/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.config.test;

import static org.junit.Assert.assertNotNull;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.bosch.boot.ssd.api.config.DataSourceConfig;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;

/**
 * @author ICP1COB
 */
@RunWith(MockitoJUnitRunner.class)
public class DataSourceConfigTest {

  @InjectMocks
  private final DataSourceConfig dataSourceConfig = new DataSourceConfig();

  private String ssdLogin;

  private String ssdUser;

  private String passwordKey;

  @SuppressWarnings("javadoc")
  @Before()
  public void setUp() {
    this.ssdLogin = "SSD.DGSPRO";
    this.ssdUser = "SSDTECHUSER";
    this.passwordKey = "SSD.SSDTECHUSER ";
    ReflectionTestUtils.setField(this.dataSourceConfig, "ssdLogin", this.ssdLogin);
    ReflectionTestUtils.setField(this.dataSourceConfig, "ssdUser", this.ssdUser);
    ReflectionTestUtils.setField(this.dataSourceConfig, "passwordKey", this.passwordKey);
  }

  /**
   * To get the data source based on the properties from the yaml file
   *
   * @throws PasswordNotFoundException - if key is invalid error to be thrown while decrypting
   */
  @Test
  @Ignore
  public void getDataSourceTest() throws PasswordNotFoundException {
    DataSource dataSource = this.dataSourceConfig.getDataSource();
    assertNotNull(dataSource);
  }
}
