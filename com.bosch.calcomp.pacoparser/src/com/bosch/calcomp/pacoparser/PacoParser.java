package com.bosch.calcomp.pacoparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.utility.PacoParserUtil;


/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 0.1 11-May-2007 Parvathy Unnikrishnan First Draft<br>
 * 0.2 18-Jun-2007 Parvathy Unnikrishnan Added exception handling<br>
 * 0.3 19-Jun-2007 Parvathy Unnikrishnan Added logging<br>
 * 0.4 31-Dec-2007 Parvathy VCL-182 Modified createSaxParser, parse<br>
 * 0.5 19-Mar-2008 Parvathy VCL-182, Modified parser - added clearing of objectMap<br>
 * 0.6 12-May-2008 Deepa SAC-68, added setTargetModelClassName()<br>
 * 0.7 05-Jun-2008 Deepa SAC-82, made logging mechanism independant of villalogger<br>
 * added setLogger() methods<br>
 * 0.8 12-Jun-2008 Deepa SAC-84, changed setLogger(file) to setLogFile(file)<br>
 * 0.9 02-Sep-2008 Parvathy SAC-113 <br>
 * 1.0 08-Dec-2008 Deepa SAC-79: Added comments<br>
 */

/**
 * Class parses a given paco file. It is based on the adapter factory design pattern. Hence, the target class has to be
 * set in the paco parser and extend the paco parser adapter classes. The parser return a map which contains the objects
 * of the target model adapter factory. For example, if the target adapter is paco to CalDataPhy class, then the map
 * contains the CalDataPhy objects.
 * <p>
 * PacoParser pacoParser = new PacoParser();<br>
 * pacoParser.setTargetModelClassName(CalDataPhyModelAdapterFactory.class.getName());<br>
 * pacoParser.setTargetModelClassLoader(CalDataPhyModelAdapterFactory.class.getClassLoader());<br>
 * HashMap calDataPhyHashMap = (HashMap)pacoParser.parse(pacoFile);<br>
 * <p>
 *
 * @author par7kor
 */
public class PacoParser {

  /**
   * Class loader of the target model adpater factory.
   */
  private static ClassLoader targetModelAdapterFactoryClassLoader;

  /**
   * The default handler of the JAXP.
   */
  private DefaultHandler handler;

  /**
   * SAX parser instance.
   */
  private SAXParser saxParser;

  /**
   * Class name used to initialize villa logger.
   */
  private static final String CLASSNAME = "com.bosch.calcomp.pacoparser.PacoParser";

  private static final String LOGMSGDELIMITER = ": ";
  /**
   * boolean to switch off DTD/schema validation.
   */
  // PACP-10
  private boolean isValidationOff;
  /**
   * Holds the class name of the target model.
   */
  private static String targetModelClassName;

  private InputStream fileStream;

  // logger to be used for all LOG messages
  private ILoggerAdapter logger = null;

  private final PacoParserObjects pacoParserObjects = new PacoParserObjects(this);

  /**
   * @return the pacoParserObjects
   */
  public PacoParserObjects getPacoParserObjects() {
    return pacoParserObjects;
  }

  /**
   * @param logger Logger
   */
  public PacoParser(final ILoggerAdapter logger) {
    this.logger = logger;
  }

  /**
   * @param logger   Logger
   * @param fileName the PaCo file to parse
   */
  public PacoParser(final ILoggerAdapter logger, final String fileName) {

    this.logger = logger;
    try {
      setFileName(fileName);
    }
    catch (PacoParserException e) {
      this.logger.error(PacoParser.CLASSNAME + PacoParser.LOGMSGDELIMITER +
          "Errors found when parsing the PaCo file. Input File is not an PaCo file.", e);

    }
  }


  /**
   * ALM - 320177
   *
   * @return Input Stream
   */
  public InputStream getFileStream() {
    return this.fileStream;
  }


  /**
   * ALM - 320177 Set the Input Stream to global variable
   *
   * @param fileStream the fileStream to set
   */
  public void setFileStream(final InputStream fileStream) {
    this.fileStream = fileStream;
  }


  /**
   * ALM - 320177 Convert the File to Input Stream and then set the Input Stream to global variable
   *
   * @param fileName fileName
   * @throws PacoParserException ex
   */
  public void setFileName(final String fileName) throws PacoParserException {

    if (fileName != null) {
      try {
        File file = new File(fileName);
        if (file.length() == 0) {
          this.logger.error(PacoParser.CLASSNAME + PacoParser.LOGMSGDELIMITER +
              "Errors found when parsing PaCo file.Input File is Empty");
          throw new PacoParserException("Errors found when parsing the PaCo file.Input File is Empty");
        }
        PacoParserUtil.validateFilePath(fileName);
        this.fileStream = new FileInputStream(fileName);
      }
      catch (FileNotFoundException e) {
        this.logger.error(PacoParser.CLASSNAME + PacoParser.LOGMSGDELIMITER +
            "Errors found when parsing the PaCo file.Input File is not an PaCo file.",e);
        throw new PacoParserException("Errors found when parsing the PaCo file.Input File is not an PaCo file.");
      }
    }
    else {
      this.logger.error(PacoParser.CLASSNAME + PacoParser.LOGMSGDELIMITER +
          "Errors found when parsing the PaCo file.There is no Input File");
      throw new PacoParserException("Errors found when parsing the PaCo file.There is no Input File");
    }
  }


  /**
   * Creates the SAX parser.
   *
   * @throws PacoParserException - exception thrown byh paco parser plugin.
   */
  private void createSaxParser() throws PacoParserException {
    this.logger.debug(PacoParser.CLASSNAME + PacoParser.LOGMSGDELIMITER + "createSaxParser() started");
    try {
      this.handler = new PacoFileHandler(this
          , pacoParserObjects
          );
      // Obtain a new instance of a SAXParserFactory.
      SAXParserFactory factory = SAXParserFactory.newInstance();
      // Specifies that the parser produced by this code will provide
      // support for XML namespaces.
      factory.setNamespaceAware(true);
      // Specifies that the parser produced by this code will validate
      // documents as they are parsed.
      if (this.isValidationOff) {// PACP-10
        this.logger.info("DTD validation is suppressed.");
        factory.setValidating(false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      }
      else {
        factory.setValidating(true);
      }
      // Creates a new instance of a SAXParser using the currently
      // configured factory parameters.
      this.saxParser = factory.newSAXParser();

    }
    catch (ParserConfigurationException pce) {
      this.logger.error("Configuration error occured in Sax parser : " + pce.getMessage(),pce);
      // VCL-182
      throw new PacoParserException(PacoParserException.SAX_EXCEPTION, pce.getMessage());
    }
    catch (SAXException saxException) {
      this.logger.error("Error occured in Sax parser : " + saxException.getMessage(),saxException);
      // VCL-182
      throw new PacoParserException(PacoParserException.SAX_EXCEPTION, saxException.getMessage());
    }
    this.logger.debug(PacoParser.CLASSNAME + PacoParser.LOGMSGDELIMITER + "createSaxParser() ended");
  }

  /**
   * Creates the parser and parses the stream. Returns a map which contains the objects of the target model adapter
   * factory.
   *
   * @throws PacoParserException - exception thrown byh paco parser plugin.
   * @return PacoModelCollection
   */
  public final Map parse() throws PacoParserException {

    this.logger.info(PacoParser.CLASSNAME + PacoParser.LOGMSGDELIMITER + "Parsing of PaCo file started");
    Map pacoMap = null;
    try {

      // PacoParserUtil.validateFilePath(uri)
      createSaxParser();
      /*
       * Clearing the contents in map before adding objects from the new file to be parsed.VCL-182
       */
      this.pacoParserObjects.getPacoModelCollection().initMap();
      // this.saxParser.parse(new java.io.File(uri),handler)
      InputSource is = new InputSource(this.fileStream);
      this.saxParser.parse(is, this.handler);
      pacoMap = this.pacoParserObjects.getPacoModelCollection().getTargetObjectMap();

    }
    catch (SAXException saxException) {
      this.logger.error("Error occured in Sax parser : " + saxException.getMessage(),saxException);
      if ((saxException.getException() instanceof PacoParserException)) {
        PacoParserException pacoParserException = (PacoParserException) saxException.getException();
        throw new PacoParserException(pacoParserException.getErrorCode(), pacoParserException);
      }
      // VCL-182
      throw new PacoParserException(PacoParserException.SAX_EXCEPTION, saxException.getLocalizedMessage(),
          saxException);
    }
    catch (IOException ioe) {
      this.logger.error("IO error occured in paco parser.", ioe);
      // VCL-182
      throw new PacoParserException(PacoParserException.SAX_EXCEPTION,
          "IO error occured in PaCo parser : " + ioe.getLocalizedMessage(), ioe);
    }
    finally {
      try {
        if (getFileStream() != null) {
          getFileStream().close();
        }
      }
      catch (IOException e) {
        this.logger
            .error(PacoParser.CLASSNAME + PacoParser.LOGMSGDELIMITER + "Errors found when parsing the PaCo file.",e);
        e.getMessage();
      }
    }

    this.logger.info(PacoParser.CLASSNAME + PacoParser.LOGMSGDELIMITER + "Parsing of PaCo file ended ");

    return pacoMap;
  }

  /**
   * Sets the class loader of the model factory which implements paco parser model adapter.
   *
   * @param classLoader -
   */
  public final void setTargetModelClassLoader(final ClassLoader classLoader) {
    setTargetModelAdapterFactoryClassLoader(classLoader);
  }

  /**
   * Sets the class name of the target model. Class name used to instantiate target model adapter factory. The target
   * model has to set its class name using this method.
   *
   * @param className className
   */
  public static void setTargetModelClassName(final String className) {
    PacoParser.targetModelClassName = className;
  }


  /**
   * Sets boolean to switch off DTD/schema validation.
   * <p>
   * If DTD validation is suppressed and if any of the expected fields are missing in the file given, the parser may
   * behave in unexpected manner. But since the files from eASEE.cdm and INCA are not always in conformance with the DTD
   * this feature is implemented though it has this risk. This feature is added for easy testing of PacoReportGenerator
   * tool using files from eASEE.cdm and INCA. Not recommended for general parsing.
   *
   * @param isValidationOff isValidationOff
   */
  // PACP-10
  public void setValidationOff(final boolean isValidationOff) {
    this.isValidationOff = isValidationOff;
  }

  /**
   * @return the targetModelAdapterFactoryClassLoader
   */
  public static ClassLoader getTargetModelAdapterFactoryClassLoader() {
    return targetModelAdapterFactoryClassLoader;
  }

  /**
   * @param targetModelAdapterFactoryClassLoader the targetModelAdapterFactoryClassLoader to set
   */
  public static void setTargetModelAdapterFactoryClassLoader(ClassLoader targetModelAdapterFactoryClassLoader) {
    PacoParser.targetModelAdapterFactoryClassLoader = targetModelAdapterFactoryClassLoader;
  }

  /**
   * @return the targetModelClassName
   */
  public static String getTargetModelClassName() {
    return targetModelClassName;
  }


}
