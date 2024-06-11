package com.bosch.calcomp.caldataanalyzer.vo;

import java.io.Serializable;

/**
 * @author ram6kor
 *
 *         <pre>
 * Version 	Date			Modified by			Changes
 * ----------------------------------------------------------------------------
 * 0.2		10-Jun-2009		Parvathy		CDAGUI-12, Added serial version ID, revision history.
 *         </pre>
 */
/**
 * This class is used to store the valueIDs and the count.
 */
// CDA-38
public class ValueIDCombinationVO implements Serializable {

  /**
   * Serial Version ID.
   */
  private static final long serialVersionUID = -2102119169277137882L;
  // The list of value Ids.
  private long[] valueIDs;
  // Number of file having the same value Ids.
  private int count = 1;


  /**
   * Gets the valueIDs.
   *
   * @return valueIDs
   */
  public long[] getValueIDs() {
    return this.valueIDs;
  }

  /**
   * sets valueIDs
   *
   * @param valueIDs long[]
   */
  public void setValueIDs(final long[] valueIDs) {
  //Task 290992 : Mutable members should not be stored or returned directly
    this.valueIDs = valueIDs.clone();
  }

  /**
   * Gets the count.
   *
   * @return count
   */
  public int getCount() {
    return this.count;
  }

  /**
   * sets count
   *
   * @param count int
   */
  public void setCount(final int count) {
    this.count = count;
  }

}
