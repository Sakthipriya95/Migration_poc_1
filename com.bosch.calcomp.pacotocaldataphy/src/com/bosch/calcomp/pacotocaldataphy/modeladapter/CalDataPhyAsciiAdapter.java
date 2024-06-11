package com.bosch.calcomp.pacotocaldataphy.modeladapter;

import java.util.List;

import com.bosch.calcomp.calutil.tools.A2LUtil;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.PacoParserObjects;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.modeladapter.AbstractAsciiCategoryAdapter;
import com.bosch.calcomp.pacoparser.pacomodelcollection.PacoModelCollection;
import com.bosch.calcomp.pacoparser.utility.PacoParserUtil;
import com.bosch.calcomp.pacoparser.utility.SwUnitsCreator;
import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.calmodel.caldataphy.CalDataPhyAscii;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 0.1 14-Jun-2007 Parvathy Unnikrishnan First Draft<br>
 * 0.2 18-Jun-2007 Parvathy Unnikrishnan Added Exception handling<br>
 * 0.3 19-Jun-2007 Parvathy Unnikrishnan Added logging<br>
 * 0.4 12-May-2007 Deepa SAC-68, modified setPacoData()<br>
 * 0.5 10-Jun-2008 Deepa SAC-82, made logging mechanism independant of VillaLogger<br>
 */
/**
 * This class implements the specific code for filling of CalDataPhyAscii.
 * 
 * @author par7kor
 */
public class CalDataPhyAsciiAdapter extends AbstractAsciiCategoryAdapter {

  /**
   * CalDataPhyValBlk instance.
   */
  private CalDataPhyAscii calDataPhyAscii;

  /**
   * Boolean for checking whether the value format is of text.
   */
  private boolean isText;

  private final PacoParser parser;

  private PacoParserObjects pacoParserObjects;

  /**
   * @param parser parser Constructor which creates CalDataPhyCurve.
   */
  public CalDataPhyAsciiAdapter(final PacoParser parser) {
    this.parser = parser;
    this.pacoParserObjects = parser.getPacoParserObjects();
    if (calDataPhyAscii == null) {
      calDataPhyAscii = new CalDataPhyAscii();
      calDataPhyAscii.setType(A2LUtil.ASCII_TEXT);
    }
  }


  /**
   * Sets wert values to caldatphy.
   * 
   * @param valueList -
   * @throws PacoParserException - exception thrown by paco parser plugin.
   */
  @Override
  public final void setValues(final List valueList) throws PacoParserException {

    AtomicValuePhy[] atomicValuePhy = getValue(valueList);
    calDataPhyAscii.setNoOfCharacter(atomicValuePhy.length);
    calDataPhyAscii.setAtomicValuePhy(atomicValuePhy);
    calDataPhyAscii.setText(isText);
  }

  /**
   * Sets short name.
   * 
   * @param shortName - label name.
   * @throws PacoParserException - exception thrown by paco parser plugin.
   */
  public final void setShotName(final String shortName) {
    if (calDataPhyAscii != null) {
      calDataPhyAscii.setName(shortName);
    }
  }

  /**
   * Sets unit.
   * 
   * @param unit -
   */
  public final void setUnit(String unit, SwUnitsCreator swUnitsCreator) throws PacoParserException {
    if (calDataPhyAscii != null) {
      unit = PacoParserUtil.pacoToDcmUnit(unit, swUnitsCreator);
      calDataPhyAscii.setUnit(unit);
    }
  }

  /**
   * This method reads given data and returns the AtomicPhyValue.
   * 
   * @param valueList -
   * @return AtomicValuePhy[]
   */
  private AtomicValuePhy[] getValue(final List valueList) {
    String value = (String) valueList.get(0);
    AtomicValuePhy[] atomicValuePhy = new AtomicValuePhy[value.length()];
    for (int i = 0; i < value.length(); i++) {
      atomicValuePhy[i] = new AtomicValuePhy(value.charAt(i));
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
   * Adds the calDataPhyValue to PacoModelCollection.
   */
  public final void setPacoData() throws PacoParserException {
    PacoModelCollection calDataPhyCollection = this.pacoParserObjects.getPacoModelCollection();
    calDataPhyCollection.put(calDataPhyAscii.getName(), calDataPhyAscii);

  }

}
