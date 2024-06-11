/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe.testdata;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.bosch.caltool.testframe.TFConstants;
import com.bosch.caltool.testframe.exception.TestDataReaderException;


/**
 * Parses the xml to a java data model
 * 
 * @author bne4cob
 */
public class DataReader {

  /**
   * @param type input type
   * @throws TestDataReaderException exception during initialization
   */
  public DataReader(final TFConstants.DATA_INPUT_TYPE type) throws TestDataReaderException {
    if (type != TFConstants.DATA_INPUT_TYPE.XML) {
      throw new TestDataReaderException("This type is not supported now");
    }
  }

  /**
   * Read the test data from the given input file. Returns the list of <code>Data</code> objects
   * 
   * @param file input file
   * @return list of test data
   * @throws TestDataReaderException TestDataReaderException
   */
  public List<InputData> readData(final String file) throws TestDataReaderException {

    // Create SAX Parser
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxParser;
    try {
      saxParser = factory.newSAXParser();
    }
    catch (ParserConfigurationException | SAXException exp) {
      throw new TestDataReaderException("Error occured while initialising reader - " + exp.getMessage(), exp);
    }

    // Parser handler
    XmlParserHandler handler = new XmlParserHandler();

    // Parse the input test data file
    try {
      saxParser.parse(file, handler);
    }
    catch (SAXException exp) {
      throw new TestDataReaderException("Invalid test data file - invalid XML format in file. " + exp.getMessage(), exp);
    }
    catch (IOException exp) {
      throw new TestDataReaderException("Unable to read the data file " + exp.getMessage(), exp);
    }

    return handler.getTestData();
  }
}
