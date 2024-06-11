package com.bosch.calcomp.pacotocaldataphy.modeladapter;

import java.util.ArrayList;
import java.util.List;

import com.bosch.calcomp.calutil.tools.A2LUtil;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.PacoParserObjects;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.modeladapter.AbstractMapCategoryAdapter;
import com.bosch.calcomp.pacoparser.pacomodelcollection.PacoModelCollection;
import com.bosch.calcomp.pacoparser.utility.PacoParserUtil;
import com.bosch.calcomp.pacoparser.utility.SwUnitsCreator;
import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.calmodel.caldataphy.CalDataAxis;
import com.bosch.calmodel.caldataphy.CalDataPhyMap;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 0.1 13-Jun-2007 Parvathy Unnikrishnan First Draft<br>
 * 0.2 18-Jun-2007 Parvathy Unnikrishnan Added Exception handling<br>
 * 0.3 19-Jun-2007 Parvathy Unnikrishnan Added logging<br>
 * 0.4 12-May-2008 Deepa SAC-68, modified setPacoData()<br>
 * 0.5 10-Jun-2008 Deepa SAC-82, made logging mechanism independant of VillaLogger<br>
 * 0.6 06-Aug-2012 Dikshita PACP-21, setAxisPtsRef to empty string
 */
/**
 * This class implements the specific code for filling of CalDataPhyMap.
 * 
 * @author par7kor
 */
public class CalDataPhyMapAdapter extends AbstractMapCategoryAdapter {

  /**
   * CalDataPhyMap instance.
   */
  private CalDataPhyMap calDataPhyMap;

  /**
   * Variable which stores unit.
   */
  private String unit = "";

  private final PacoParser parser;

  private final PacoParserObjects pacoParserObjects;

  /**
   * @param parser parser Constructor which creates CalDataPhyMap.
   */
  public CalDataPhyMapAdapter(final PacoParser parser) {
    this.parser = parser;
    this.pacoParserObjects = parser.getPacoParserObjects();
    if (calDataPhyMap == null) {
      calDataPhyMap = new CalDataPhyMap();
      calDataPhyMap.setType(A2LUtil.MAP_TEXT);
    }
  }

  /**
   * Boolean for checking whether the value format is of text.
   */
  private boolean isText;


  /**
   * Sets wert values to caldatphy.
   * 
   * @param valueList -
   * @throws PacoParserException - exception thrown by paco parser plugin.
   */
  @Override
  public final void setValues(final List valueList) throws PacoParserException {

    AtomicValuePhy[][] atomicValuePhies = new AtomicValuePhy[((ArrayList) valueList.get(0)).size()][valueList.size()];
    for (int j = 0; j < valueList.size(); j++) {
      List values = (ArrayList) valueList.get(j);
      for (int k = 0; k < values.size(); k++) {
        String value = (String) values.get(k);
        atomicValuePhies[k][j] = new AtomicValuePhy(value);
      }
    }
    calDataPhyMap.setAtomicValuePhy(atomicValuePhies);
    calDataPhyMap.setText(isText);
    calDataPhyMap.setUnit(unit);
  }

  /**
   * Sets x-axis values to caldatphy.
   * 
   * @param valueList -
   * @throws PacoParserException - exception thrown by paco parser plugin.
   */
  public final void setXValues(final List valueList) throws PacoParserException {
    CalDataAxis calDataAxisX = new CalDataAxis();
    AtomicValuePhy[] atomicValuePhy = getValue(valueList);
    calDataAxisX.setAtomicValuePhy(atomicValuePhy);
    calDataAxisX.setUnit(unit);
    // PACP-21
    calDataAxisX.setAxisPtsRef("");
    calDataAxisX.setNoOfAxisPts(valueList.size());
    calDataAxisX.setText(isText);
    calDataPhyMap.setCalDataAxisX(calDataAxisX);
  }

  /**
   * Sets y-axis values to caldatphy.
   * 
   * @param valueList -
   * @throws PacoParserException - exception thrown by paco parser plugin.
   */
  public final void setYValues(final List valueList) throws PacoParserException {
    CalDataAxis calDataAxisY = new CalDataAxis();
    AtomicValuePhy[] atomicValuePhy = getValue(valueList);
    calDataAxisY.setAtomicValuePhy(atomicValuePhy);
    calDataAxisY.setUnit(unit);
    // PACP-21
    calDataAxisY.setAxisPtsRef("");
    calDataAxisY.setNoOfAxisPts(valueList.size());
    calDataAxisY.setText(isText);
    calDataPhyMap.setCalDataAxisY(calDataAxisY);
  }

  /**
   * Sets short name.
   * 
   * @param shortName - label name.
   * @throws PacoParserException - exception thrown by paco parser plugin.
   */
  public final void setShotName(final String shortName) {
    if (calDataPhyMap != null) {
      calDataPhyMap.setName(shortName);
    }
  }

  /**
   * Sets unit.
   * 
   * @param unit -
   * @throws PacoParserException - exception thrown by paco parser plugin.
   */
  public final void setUnit(String unit, SwUnitsCreator swUnitsCreator) throws PacoParserException {
    unit = PacoParserUtil.pacoToDcmUnit(unit, swUnitsCreator);
    this.unit = unit;
  }

  /**
   * This method reads given data and returns the AtomicPhyValue.
   * 
   * @param valueList -
   * @return AtomicValuePhy[]
   */
  private AtomicValuePhy[] getValue(final List valueList) {
    AtomicValuePhy[] atomicValuePhy = new AtomicValuePhy[valueList.size()];
    for (int i = 0; i < valueList.size(); i++) {
      String value = (String) valueList.get(i);
      atomicValuePhy[i] = new AtomicValuePhy(value);
    }
    return atomicValuePhy;
  }

  /**
   * Returns a boolean to check if the value is in text format.
   * 
   * @return boolean.
   */
  public final boolean isText() {
    return isText;
  }

  /**
   * Sets text flag.
   * 
   * @param flag
   */
  public final void setTextFlag(final boolean flag) {
    isText = flag;
  }

  /**
   * Gives back the model. Adds the calDataPhyMap to PacoModelCollection.
   * 
   * @throws PacoParserException - exception thrown by paco parser plugin.
   */
  public final void setPacoData() throws PacoParserException {
    PacoModelCollection calDataPhyCollection = this.pacoParserObjects.getPacoModelCollection();
    calDataPhyCollection.put(calDataPhyMap.getName(), calDataPhyMap);

  }

}
