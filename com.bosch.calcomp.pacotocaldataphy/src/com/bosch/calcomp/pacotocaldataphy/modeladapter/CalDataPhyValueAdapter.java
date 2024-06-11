package com.bosch.calcomp.pacotocaldataphy.modeladapter;

import java.util.List;

import com.bosch.calcomp.calutil.tools.A2LUtil;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.PacoParserObjects;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.modeladapter.AbstractValueCategoryAdapter;
import com.bosch.calcomp.pacoparser.pacomodelcollection.PacoModelCollection;
import com.bosch.calcomp.pacoparser.utility.PacoParserUtil;
import com.bosch.calcomp.pacoparser.utility.SwUnitsCreator;
import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.calmodel.caldataphy.CalDataPhyValue;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 0.1 14-Jun-2007 Parvathy Unnikrishnan First Draft<br>
 * 0.2 18-Jun-2007 Parvathy Unnikrishnan Added Exception handling<br>
 * 0.3 19-Jun-2007 Parvathy Unnikrishnan Added logging<br>
 * 0.4 12-May-2008 Deepa SAC-68, modified setPacoData()<br>
 * 0.5 10-Jun-2008 Deepa SAC-82, made logging mechanism independant of VillaLogger<br>
 */
/**
 * This class implements the specific code for filling of CalDataPhyValue.
 * 
 * @author par7kor
 */
public class CalDataPhyValueAdapter extends AbstractValueCategoryAdapter {

  /**
   * CalDataPhyValue instance.
   */
  private CalDataPhyValue calDataPhyValue;
  
  /**
   * Variable which stores unit.
   */
  private String unit = "";

  /**
   * Boolean for checking whether the value format is of text.
   */
  private boolean isText;

  private final PacoParser parser;

  private final PacoParserObjects pacoParserObjects;

  /**
   * @param parser parser Constructor which creates CalDataPhyCurve.
   */
  public CalDataPhyValueAdapter(final PacoParser parser) {
    this.parser = parser;
    this.pacoParserObjects = parser.getPacoParserObjects();
    if (calDataPhyValue == null) {
      calDataPhyValue = new CalDataPhyValue();
      calDataPhyValue.setType(A2LUtil.VALUE_TEXT);
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
    calDataPhyValue.setAtomicValuePhy(new AtomicValuePhy((String) valueList.get(0)));
    calDataPhyValue.setText(isText);
  }

  /**
   * Sets short name.
   * 
   * @param shortName - label name.
   * @throws PacoParserException - exception thrown by paco parser plugin.
   */
  public final void setShotName(final String shortName) {
    if (calDataPhyValue != null) {
      calDataPhyValue.setName(shortName);
    }
  }

  /**
   * Sets unit.
   * 
   * @param unit -
   * @throws PacoParserException - exception thrown by paco parser plugin.
   */
  public final void setUnit(String unit,  SwUnitsCreator swUnitsCreator) throws PacoParserException {
    unit = PacoParserUtil.pacoToDcmUnit(unit, swUnitsCreator);
    this.unit = unit;
    if (calDataPhyValue != null) {
      calDataPhyValue.setUnit(unit);
    }
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
   * 
   * @throws PacoParserException - exception thrown by paco parser plugin.
   */
  public final void setPacoData() throws PacoParserException {
    PacoModelCollection calDataPhyCollection = this.pacoParserObjects.getPacoModelCollection();
    calDataPhyCollection.put(calDataPhyValue.getName(), calDataPhyValue);
  }

}
