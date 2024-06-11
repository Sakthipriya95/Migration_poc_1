package com.bosch.calcomp.pacotocaldataphy.modeladapter;

import java.util.List;

import com.bosch.calcomp.calutil.tools.A2LUtil;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.PacoParserObjects;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.modeladapter.AbstractCurveCategoryAdapter;
import com.bosch.calcomp.pacoparser.pacomodelcollection.PacoModelCollection;
import com.bosch.calcomp.pacoparser.utility.PacoParserUtil;
import com.bosch.calcomp.pacoparser.utility.SwUnitsCreator;
import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.calmodel.caldataphy.CalDataAxis;
import com.bosch.calmodel.caldataphy.CalDataPhyCurve;

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
 * This class implements the specific code for filling of CalDataPhyCurve.
 * 
 * @author par7kor
 */
public class CalDataPhyCurveAdapter extends AbstractCurveCategoryAdapter {

  /**
   * CalDataPhyCurve instance.
   */
  private CalDataPhyCurve calDataPhyCurve;

  /**
   * Variable which stores unit.
   */
  private String unit = "";

  private final PacoParser parser;

  private final PacoParserObjects pacoParserObjects;

  /**
   * @param parser parser Constructor which creates CalDataPhyCurve.
   */
  public CalDataPhyCurveAdapter(final PacoParser parser) {
    this.parser = parser;
    this.pacoParserObjects = parser.getPacoParserObjects();
    if (calDataPhyCurve == null) {
      calDataPhyCurve = new CalDataPhyCurve();
      calDataPhyCurve.setType(A2LUtil.CURVE_TEXT);
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
    AtomicValuePhy[] atomicValuePhy = getValue(valueList);
    calDataPhyCurve.setAtomicValuePhy(atomicValuePhy);
    calDataPhyCurve.setText(isText);
    calDataPhyCurve.setUnit(unit);
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
    calDataPhyCurve.setCalDataAxisX(calDataAxisX);
  }

  /**
   * Sets short name.
   * 
   * @param shortName - label name.
   * @throws PacoParserException - exception thrown by paco parser plugin.
   */
  public final void setShotName(final String shortName) {
    if (calDataPhyCurve != null) {
      calDataPhyCurve.setName(shortName);
    }
  }

  /**
   * Sets unit.
   * 
   * @param unit -
   * @throws PacoParserException - exception thrown by paco parser plugin.
   */
  @Override
  public final void setUnit(String unit, SwUnitsCreator swUnitsCreator) throws PacoParserException {
    unit = PacoParserUtil.pacoToDcmUnit(unit, swUnitsCreator);
    this.unit = unit;
    if (this.calDataPhyCurve != null) {
      this.calDataPhyCurve.setUnit(unit);
    }
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
   */
  public final void setPacoData() throws PacoParserException {
    PacoModelCollection calDataPhyCollection = this.pacoParserObjects.getPacoModelCollection();
    calDataPhyCollection.put(calDataPhyCurve.getName(), calDataPhyCurve);

  }

}
