/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.rest.wadl.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.model.general.WsService;

/**
 * @author elm1cob
 */
public class WadlParser {

  /**
   * Logger Instance
   */
  private final ILoggerAdapter logger;
  /**
   * WADL file location
   */
  private final String wadlFilePath;

  private final Map<String, WsService> servicesMap = new HashMap<>();

  /**
   * New WADL parser, with WADL file path as input
   *
   * @param logger Logger
   * @param wadlFilePath WADL file path
   */
  public WadlParser(final ILoggerAdapter logger, final String wadlFilePath) {
    this.logger = logger;
    this.wadlFilePath = wadlFilePath;
  }

  /**
   * Run the parser
   *
   * @throws WadlParserException error while parsing contents
   */
  public void parse() throws WadlParserException {
    try {

      SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
      ParserHandler handler = new ParserHandler();

      saxParser.parse(new File(this.wadlFilePath), handler);
      this.servicesMap.putAll(handler.getRestWebServicesMap());

      this.logger.info("Parsing WADL file completed successfully.");

    }
    catch (ParserConfigurationException | SAXException | IOException e) {
      throw new WadlParserException("Error while parsing WADL - " + e.getMessage(), e);
    }
  }

  /**
   * Returns the services parsed from the wadl file.
   *
   * @return services
   */
  public Map<String, WsService> getServices() {
    return this.servicesMap;
  }

}
