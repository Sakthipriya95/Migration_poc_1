/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient.ss;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;


/**
 * @author imi2si
 */
public class ParameterLoader {

  private final static ILoggerAdapter LOG = new Log4JLoggerAdapterImpl("WS");

  Properties parameFile;

  public ParameterLoader() {
    try {
      loadParamFile("Parameters.txt");
    }
    catch (IOException | NullPointerException e) {
      LOG.error("Default parameters file couldn't be loaded. Choose an own file using loadParameters.", e);
    }
  }

  public void loadParamFile(final String filename) throws IOException {
    this.parameFile = new Properties();
    try {
      this.parameFile.load(new FileReader("testdata/ss/" + filename));
    }
    catch (IOException | NullPointerException e) {
      LOG.error("Error when loading parameters", e);
      throw (e);
    }
  }

  public String[] getParameters(final String keyInParamFile) {
    String parameters = loadParam(keyInParamFile);

    parameters = parameters.replace(" ", "");

    return parameters.split(",");

  }

  private String loadParam(final String keyInParamFile) {
    if (!this.parameFile.containsKey(keyInParamFile)) {
      LOG.error("Parameter Key " + keyInParamFile + " not found in parameter file.");
      throw new IllegalArgumentException("Parameter Key " + keyInParamFile + " not found in parameter file.");
    }

    return (String) this.parameFile.get(keyInParamFile);
  }
}
