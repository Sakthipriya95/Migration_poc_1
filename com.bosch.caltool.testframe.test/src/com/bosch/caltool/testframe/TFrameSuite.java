/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * @author bne4cob
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ TestEntityCreator.class, TestEntityRemover.class })
public class TFrameSuite {
  // nothing
}
