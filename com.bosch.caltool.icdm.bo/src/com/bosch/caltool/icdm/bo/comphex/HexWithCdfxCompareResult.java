package com.bosch.caltool.icdm.bo.comphex;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.bosch.calcomp.caldatautils.CalDataComparism;

/**
 * Object to store the CDMStudio compare result
 *
 * @author Vau3COB
 */
public class HexWithCdfxCompareResult {

  /**
   * List holding the names of the unequal parameter
   */
  private List<String> unequalPar = new Vector<String>();

  private final HashMap<String, CalDataComparism> labelCompareResults = new HashMap<>();

  /**
   * No of Equal Parameters
   */
  private long equalParCount = 0;

  /**
   * @return no of equal paramters
   */
  public long getEqualParCount() {
    return this.equalParCount;
  }

  /**
   * @param equalParCount - Sets the no of the equal paramters
   */
  public void setEqualParCount(final long equalParCount) {
    this.equalParCount = equalParCount;
  }

  /**
   * @return List of the unequal parameter names
   */
  public List<String> getUnequalPar() {
    return this.unequalPar;
  }

  public CalDataComparism getCompareResult(final String label) {
    return this.labelCompareResults.get(label);
  }

  /**
   * @param unequalPar - List of Unequal parameter to set
   */
  protected void setUnequalPar(final List<String> unequalPar) {
    this.unequalPar = unequalPar;
  }

  public void addCompareResult(final String label, final CalDataComparism labelCompareResult) {
    this.labelCompareResults.put(label, labelCompareResult);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + (int) (this.equalParCount ^ (this.equalParCount >>> 32));
    result = (prime * result) + ((this.unequalPar == null) ? 0 : this.unequalPar.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    HexWithCdfxCompareResult other = (HexWithCdfxCompareResult) obj;
    if (this.equalParCount != other.equalParCount) {
      return false;
    }
    if (this.unequalPar == null) {
      if (other.unequalPar != null) {
        return false;
      }
    }
    else if (!this.unequalPar.equals(other.unequalPar)) {
      return false;
    }
    return true;
  }
}

