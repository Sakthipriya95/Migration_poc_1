/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.controller.test;

/**
 * @author TUD1COB
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.bosch.boot.ssd.api.controller.CreateSSDUserController;
import com.bosch.boot.ssd.api.service.CreateSSDUserService;

class CreateSSDUserControllerTest {

  @Mock
  private CreateSSDUserService createSSDUserService;

  @InjectMocks
  private CreateSSDUserController createSSDUserController;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testCreateSSDUser() {
    this.createSSDUserController.createSSDUser();
    Mockito.verify(this.createSSDUserService).doCreateUser();
  }
}

