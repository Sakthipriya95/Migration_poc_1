package com.bosch.caltool.nattable;


/**
 * The <code>NatInputToColumnConverter</code> is used to convert row object (which is given as input to nattable viewer)
 * to the respective column values
 * 
 * @author jvi6cob
 */
public abstract class AbstractNatInputToColumnConverter {

  /**
   * Method which converts passed in row object (which is given as input to nattable viewer) to the respective column
   * values
   * 
   * @param evaluateObj Row object
   * @param colIndex column index to be converted
   * @return converted column value
   */
  public abstract Object getColumnValue(Object evaluateObj, final int colIndex);

}
