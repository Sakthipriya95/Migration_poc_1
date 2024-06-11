package com.bosch.calcomp.pacoparser.pacomodelcollection;

import java.util.HashMap;
import java.util.Map;

import com.bosch.calcomp.adapter.logger.util.LoggerUtil;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 0.1 15-May-2007 Parvathy Unnikrishnan First Draft<br>
 * 0.2 18-Jun-2007 Parvathy Unnikrishnan Added exception handling<br>
 * 0.3 19-Jun-2007 Parvathy Unnikrishnan Added logging<br>
 * 0.4 20-May-2008 Deepa SAC-68, modified put(), added get()<br>
 * 0.5 05-Jun-2008 Deepa SAC-82, made logging mechanism independant of villalogger<br>
 * 0.6 23-Jun-2008 Madhu Samuel K SAC-82, Changed PacoParserLogger.getLogger() to <br>
 * LoggerUtil.getLogger() in all the methods. <br>
 */
/**
 * Singleton class which holds a collection of CalDataPhy objects. This class is accessed by the processor to set the
 * CalDataPhy object to the collection.
 * 
 * @author par7kor
 */
public final class PacoModelCollection {

  /**
   * Instance of PacoModelCollection whic will created once.
   */
  private static PacoModelCollection pacoModelCollection;

  /**
   * Map which holds the target object map.
   */
  private Map targetObjectMap;

  /**
   * Class name used to initialize villa logger.
   */
  private static final String CLASSNAME = "com.bosch.calcomp.pacoparser.pacoModelCollection.PacoModelCollection";

  /**
   * Puts the Target model object into the map.
   * 
   * @param object - model object .
   * @param name   - name of the model object.
   */
  public void put(final String name, final Object object) {
    LoggerUtil.getLogger().debug(CLASSNAME + ": " + "put() started.");

    if (this.targetObjectMap != null) {
      this.targetObjectMap.put(name, object);
    }
    LoggerUtil.getLogger().debug(CLASSNAME + ": " + "put() ended.");
  }

  /**
   * Gets calDataPhy collection object.
   * 
   * @throws PacoParserException - exception thrown byh paco parser plugin.
   * @return PacoModelCollection
   */
  public PacoModelCollection getPacoModelCollection() {
    return pacoModelCollection;
  }

  /**
   * Gets calDataPhyMap .
   * 
   * @return Map
   */
  public Map getTargetObjectMap() throws PacoParserException {
    return this.targetObjectMap;
  }

  /**
   * Clears the map whenever SW_INSTANE_TREE tag is found which means a new file is being parsed. This is called by
   * PacoFileHandler.
   */
  public void clearTargetObjectMap() {
    this.targetObjectMap.clear();
  }

  /**
   * 
   */
  public void initMap() {
    this.targetObjectMap = new HashMap<>();
  }

  /**
   * Returns the value for the given key.
   * 
   * @param name name
   * @return value
   */
  public Object get(String name) {
    if (this.targetObjectMap != null) {
      return this.targetObjectMap.get(name);
    }
    return null;
  }
}
