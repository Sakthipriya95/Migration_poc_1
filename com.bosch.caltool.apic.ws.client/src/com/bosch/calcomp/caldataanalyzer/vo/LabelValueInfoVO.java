package com.bosch.calcomp.caldataanalyzer.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bosch.calmodel.caldataphy.CalDataPhy;

/**
 * @author par7kor
 *
 *         <pre>
 * Version 	Date			Modified by			Changes
 * ----------------------------------------------------------------------------
 * 0.1		Unknown			Deepa			First draft<br>
 * 0.2		10-Jun-2009		Parvathy		CDAGUI-12, Added serial version ID, revision history.<br>
 * 0.3		27-Jul-2009		Deepa			CDA-93, added setUsage()<br>
 *         </pre>
 */
/**
 * The LabelValueInfoVO class holds the value of a label and the list of file IDs where this label value is used.
 */
// CDA-62
public class LabelValueInfoVO implements Serializable {

  /**
   * Serial Version ID.
   */
  private static final long serialVersionUID = -6296374752070817355L;

  /**
   * Label value.
   */
  private CalDataPhy calDataPhy;

  /**
   * The list of file ids where the value id is used across datasets.
   */
  private List<Long> fileIDList = new ArrayList<>();

  /**
   * The valueId of the label.
   */
  private long valueId;

  /**
   * Usage of the label.
   */
  private int usage;

  /**
   * Gets the label value object.
   *
   * @return the label value object.
   */
  public CalDataPhy getCalDataPhy() {
    return this.calDataPhy;
  }

  /**
   * Gets the label value object.
   *
   * @param calDataPhy
   */
  public void setCalDataPhy(final CalDataPhy calDataPhy) {
    this.calDataPhy = calDataPhy;
  }

  /**
   * Gets the value id of the label.
   *
   * @return valueId
   */
  public long getValueId() {
    return this.valueId;
  }

  /**
   * Sets the value id of the label.
   *
   * @param valueId
   */
  public void setValueId(final long valueId) {
    this.valueId = valueId;
  }

  /**
   * Gets the list of fileIds.
   *
   * @return list of fileIds.
   */
  public List<Long> getFileIDList() {
    // Task 290992 : Mutable members should not be stored or returned directly
    return new ArrayList<>(this.fileIDList);
  }

  /**
   * Sets the list of fileIds.
   *
   * @param fileIDList list of fileIds.
   */
  public void setFileIDList(final List<Long> fileIDList) {
    // Task 290992
    this.fileIDList = new ArrayList<>(fileIDList);
  }

  /**
   * Sets the usage of the label.
   *
   * @param usage the usage to set
   */
  // CDA-93
  public void setUsage(final int usage) {
    this.usage = usage;
  }

  /**
   * Gets the usage of the label.
   *
   * @return the usage
   */
  public int getUsage() {
    return this.usage;
  }

}
