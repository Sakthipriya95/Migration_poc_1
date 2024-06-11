package com.bosch.calcomp.pacoparser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.bosch.calcomp.adapter.logger.util.LoggerUtil;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.factory.ProcessorFactory;
import com.bosch.calcomp.pacoparser.tagprocessor.ITagProcessor;
import com.bosch.calcomp.pacoparser.tagprocessor.SWInstanceProcessor;
import com.bosch.calcomp.pacoparser.utility.PacoFileTagNames;
import com.bosch.calcomp.pacoparser.utility.SwFeatureCreator;
import com.bosch.calcomp.pacoparser.utility.SwUnitsCreator;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 0.1 15-May-2007 Parvathy Unnikrishnan First Draft<br>
 * 0.2 18-Jun-2007 Parvathy Unnikrishnan Added exception handling<br>
 * 0.3 19-Jun-2007 Parvathy Unnikrishnan Added logging, changed functions endElement and characters<br>
 * 0.3 28-Jun-2007 Parvathy Unnikrishnan Added resolveEntity method to set local DTD path<br>
 * 0.4 31-Dec-2007 Parvathy VCL-182 Modified resolveEntity,error, fatalError, warning<br>
 * 0.5 19-Mar-2008 Parvathy VCL-182, Modified startElement - removed clearinf objectMap<br>
 * 0.6 12-May-2008 Deepa SAC-68, modified startElement(), endElement()<br>
 * 0.7 05-Jun-2008 Deepa SAC-82, made logging mechanism independant of villalogger<br>
 * 0.8 23-Jun-2008 Madhu Samuel K SAC-82, Changed PacoParserLogger.getLogger() to <br>
 * LoggerUtil.getLogger() in all the methods. <br>
 * 0.9 08-Dec-2008 Deepa SAC-79: Added comments<br>
 * 1.0 21-Oct-2010 Jagan PACP-2,6 : Support function version (<REVISION-LABEL>) <br>
 * 1.1 03-Mar-2015 Geetha Ganesan : Plugin path included to support access from RCP apps.<br>
 */
/**
 * This class identifes the various tags and delegates them to appropriate Processors.
 *
 * @author par7kor
 */
public class PacoFileHandler extends DefaultHandler {

  /** The Constant PLUGIN_ID. */
  public static final String DTD_PATH = "com.bosch.calcomp.pacoparser/config";
  /**
   * The relative path of the location of DTD in the plugin.
   */
  private static final String DTDSYSTEMID = "/msrsw_v222_lai_iai_normalized.xml.dtd";

  /**
   * The public Id of the paco files.
   */
  private static final String DTD_PUBLIC_ID = "-//MSR//DTD MSR SOFTWARE DTD:V2.2.2:LAI:IAI:XML:MSRSW.DTD//EN";

  /**
   * ITagProcessor instance.
   */
  private ITagProcessor sInstanceProcessor;

  /**
   * Variable to store the data parsed from the paco file.
   */
  private StringBuilder dataStringBuf;

  /**
   * SwUnitsCreator instance.
   */
  private SwUnitsCreator swUnitsCreator;

  /**
   * SwFeatureCreator instance.
   */
  // PACP-2,6
  private SwFeatureCreator swFeatureCreator;
  
  private SwFeatureCreator swFeatureCreatorforVersionMap;

  /**
   * Class name used to initialize villa logger.
   */
  private static final String CLASSNAME = "com.bosch.calcomp.pacoparser.PacoFileHandler";

  /**
   * Delimiter used to log messages in log file.
   */
  private static final String LOGMSGDELIMITER = ": ";

  /**
   * Delimiter used to log messages in log file.
   */
  private static final String ERRATLINE = " at line ";

  /**
   * Delimiter used to log messages in log file.
   */
  private static final String ERRCOLUMN = ", column ";

  /**
   * Delimiter used to log messages in log file.
   */
  private static final String ERRINENTITY = " in entity ";

  private final PacoParser pacoParser;
  
  private final PacoParserObjects pacoParserObjects;

  /**
   * @param pacoParser paco parser
   * @param pacoParserObjects pacoParserObjects
   */
  public PacoFileHandler(final PacoParser pacoParser
      , final PacoParserObjects pacoParserObjects
      ) {
    this.pacoParser = pacoParser;
    this.pacoParserObjects = pacoParserObjects;
  }

  /**
   * Receive notification of character data inside an element.When a SWInstanceProcessor instance is created this method
   * invokes process method for the tags under SW-INSTANCE.
   *
   * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
   * @param ch     - array of all charaters in the XML file.
   * @param start  - The start position in the character array.
   * @param length - The number of characters to use from the character array.
   * @throws SAXException - Any SAX exception, possibly wrapping another exception.
   */
  @Override
  public final void characters(final char[] ch, final int start, final int length) throws SAXException {
    LoggerUtil.getLogger().debug(PacoFileHandler.CLASSNAME + PacoFileHandler.LOGMSGDELIMITER + "characters() started");
    if ((this.sInstanceProcessor != null) || (this.swUnitsCreator != null) || (this.swFeatureCreator != null)) {
      if (this.dataStringBuf == null) {
        this.dataStringBuf = new StringBuilder();
      }

      this.dataStringBuf.append(new String(ch, start, length));
    }
    LoggerUtil.getLogger().debug(PacoFileHandler.CLASSNAME + PacoFileHandler.LOGMSGDELIMITER + "characters() ended");
  }

  /**
   * Receive notification of the end of an element. It invokes end method of SWInstanceProcessor.
   *
   * @see org.xml.sax.helpers.DefaultHandler#endElement(String, String, String)
   * @param uri       - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace
   *                    processing is not being performed.
   * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being
   *                    performed.
   * @param qName     - The qualified name (with prefix), or the empty string if qualified names are not available.
   * @throws SAXException - Any SAX exception, possibly wrapping another exception.
   */
  @Override
  public final void endElement(final String uri, final String localName, final String qName) throws SAXException {
    LoggerUtil.getLogger().debug(PacoFileHandler.CLASSNAME + PacoFileHandler.LOGMSGDELIMITER + "endElement() started.");

    try {
      if ((this.swUnitsCreator != null) && !localName.equals(PacoFileTagNames.SW_UNITS)) {
        this.swUnitsCreator.addToUnitsMap(localName, getDataString());
      }
      // PACP-2,6
      if ((this.swFeatureCreator != null) && !localName.equals(PacoFileTagNames.SW_COMPONENTS)) {
        this.swFeatureCreator.addToVersionMap(localName, getDataString());
        this.swFeatureCreatorforVersionMap.addToVersionMap(localName, getDataString());
      }
      if (this.sInstanceProcessor != null) {
//        swFeatureCreator is passed to get the version
        this.sInstanceProcessor.process(localName, getDataString(), this.swFeatureCreatorforVersionMap);
      }
      this.dataStringBuf = null;

      // when the SW_units tags end the swUnitsCreator is made null to
      // avoid
      // any other method calls on it.
      if (PacoFileTagNames.SW_UNITS.equals(localName)) {
        this.swUnitsCreator = null;
      }

      if (PacoFileTagNames.SW_INSTANCE.equals(localName)) {
        this.sInstanceProcessor = null;
      }
      // PACP-2,6: At the end of SW-COMPONENTS, make sure to null the object swFeatureCreator
      if (PacoFileTagNames.SW_COMPONENTS.equals(localName)) {
        this.swFeatureCreator = null;
      }
    }
    catch (PacoParserException parserException) {
      LoggerUtil.getLogger().error("Error occured while parsing PACO file :  " + parserException.getMessage());
      throw new SAXException(parserException.getMessage(),parserException);
    }
    LoggerUtil.getLogger().debug(PacoFileHandler.CLASSNAME + PacoFileHandler.LOGMSGDELIMITER + "endElement() ended.");
  }

  /**
   * Receive notification of a recoverable parser error.
   *
   * @see org.xml.sax.helpers.DefaultHandler#error(SAXParseException)
   * @param e - SAXParseException.
   * @throws SAXException -
   */
  @Override
  public final void error(final SAXParseException e) throws SAXException {
    // VCL-182
    LoggerUtil.getLogger().error("Error : ", e);
    LoggerUtil.getLogger().error(PacoFileHandler.ERRATLINE + e.getLineNumber() + PacoFileHandler.ERRCOLUMN +
        e.getColumnNumber() + PacoFileHandler.ERRINENTITY + e.getSystemId());
    throw e;
  }

  /**
   * Report a fatal XML parsing error.
   *
   * @see org.xml.sax.helpers.DefaultHandler#fatalError(SAXParseException)
   * @param e - SAXParseException.
   * @throws SAXException -
   */
  @Override
  public final void fatalError(final SAXParseException e) throws SAXException {
    // VCL-182
    LoggerUtil.getLogger().fatal("Error : ", e);
    LoggerUtil.getLogger().fatal(PacoFileHandler.ERRATLINE + e.getLineNumber() + PacoFileHandler.ERRCOLUMN +
        e.getColumnNumber() + PacoFileHandler.ERRINENTITY + e.getSystemId());
    throw e;
  }

  /**
   * Checks for start element. Checks for the SW-INSTANCE and instantiates the processor.
   *
   * @see org.xml.sax.helpers.DefaultHandler#startElement(String, String, String, Attributes) *
   * @param uri        - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace
   *                     processing is not being performed.
   * @param localName  - The local name (without prefix), or the empty string if Namespace processing is not being
   *                     performed.
   * @param qName      - The qualified name (with prefix), or the empty string if qualified names are not available.
   * @param attributes - The attributes attached to the element. If there are no attributes, it shall be an empty
   *                     Attributes object.
   * @throws SAXException - Any SAX exception, possibly wrapping another exception.
   */
  @Override
  public final void startElement(final String uri, final String localName, final String qName,
      final Attributes attributes) throws SAXException {
    LoggerUtil.getLogger()
        .debug(PacoFileHandler.CLASSNAME + PacoFileHandler.LOGMSGDELIMITER + "startElement() started.");
    try {
      if (PacoFileTagNames.SW_UNITS.equals(localName.trim())) {
        this.swUnitsCreator = new SwUnitsCreator();
      }
      else if (PacoFileTagNames.SW_INSTANCE.equals(localName.trim())) {
        this.sInstanceProcessor = new ProcessorFactory(this.pacoParser
            , this.pacoParserObjects
            ).createSWInstanceProcessor();
      } // PACP-2,6
      else if (PacoFileTagNames.SW_COMPONENTS.equals(localName.trim())) {
        this.swFeatureCreator = new SwFeatureCreator();
        this.swFeatureCreatorforVersionMap = new SwFeatureCreator();
      }

      createHistoryProcess(localName, attributes);

    }
    catch (PacoParserException pacoParserException) {
      LoggerUtil.getLogger().error("Error occured in while parsing file. ", pacoParserException);
      throw new SAXException(pacoParserException);
    }
    LoggerUtil.getLogger().debug(PacoFileHandler.CLASSNAME + PacoFileHandler.LOGMSGDELIMITER + "startElement() ended.");
  }

  /**
   * @param localName
   * @param attributes
   * @throws PacoParserException
   */
  private void createHistoryProcess(final String localName, final Attributes attributes) throws PacoParserException {
    if (this.sInstanceProcessor instanceof SWInstanceProcessor) {
      SWInstanceProcessor swInstProc = (SWInstanceProcessor) this.sInstanceProcessor;
      HashMap<String, String> attrsMap = null;
      if (attributes != null) {
        attrsMap = new HashMap<>();
        for (int i = 0; i < attributes.getLength(); i++) {
          String attributeName = attributes.getQName(i);
          String attributeValue = attributes.getValue(i);
          attrsMap.put(attributeName, attributeValue);
        }
      }
      swInstProc.createProcessorForHistory(localName.trim(), attrsMap);
    }
  }

  /**
   * Receive notification of a recoverable parser error.
   *
   * @see org.xml.sax.helpers.DefaultHandler#warning(SAXParseException)
   * @param e - SAXParseException.
   * @throws SAXException -
   */
  @Override
  public final void warning(final SAXParseException e) throws SAXException {
    // VCL-182
    LoggerUtil.getLogger().warn("Warning from Paco Parser : ", e);
    LoggerUtil.getLogger().warn(PacoFileHandler.ERRATLINE + e.getLineNumber() + PacoFileHandler.ERRCOLUMN +
        e.getColumnNumber() + PacoFileHandler.ERRINENTITY + e.getSystemId());
  }

  /**
   * Gets the data from the str buffer if not null else returns empty string.
   *
   * @return String
   */
  private String getDataString() {
    String dataStr = "";
    if (this.dataStringBuf != null) {
      dataStr = this.dataStringBuf.toString();
    }
    return dataStr;
  }

  /**
   * Function which sets the local path of the common DTD for paco files.
   *
   * @see org.xml.sax.helpers.DefaultHandler#resolveEntity(String, String)
   * @param publicId - the PUBLIC id defined in the DOCTYPE definintion.
   * @param systemId - the SYSTEM id get by using the same path where the xml file is present
   * @throws SAXException, IOException
   */
  @Override
  public InputSource resolveEntity(final String publicId, final String systemId) throws IOException, SAXException {
    // VCL-182
    if (PacoFileHandler.DTD_PUBLIC_ID.equals(publicId)) {
      // ICDM 573
      // String localUri = getAbsolutePath(DTD_SYSTEM_ID)
      InputStream localUri = PacoFileHandler.class.getResourceAsStream(PacoFileHandler.DTDSYSTEMID);
      // PACP-26
      if (localUri == null) {
        localUri = PacoFileHandler.class.getResourceAsStream("/config" + PacoFileHandler.DTDSYSTEMID);
      }
      if (localUri == null) {
        URL url = new URL("platform:/plugin/" + PacoFileHandler.DTD_PATH + PacoFileHandler.DTDSYSTEMID);
        localUri = url.openConnection().getInputStream();
      }
      InputSource inputSource = new InputSource(localUri);
      inputSource.setSystemId(localUri.toString());
      return inputSource;
    }
    return null;
  }


}
