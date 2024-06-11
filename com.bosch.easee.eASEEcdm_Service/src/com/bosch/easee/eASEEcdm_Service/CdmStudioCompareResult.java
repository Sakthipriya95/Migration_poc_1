/**
 * 
 */
package com.bosch.easee.eASEEcdm_Service;

import java.util.List;
import java.util.Vector;

/**
 * @author hef2fe
 *
 */
public class CdmStudioCompareResult {
	private List<String> missingPar = new Vector<String>();
	private List<String> unequalPar = new Vector<String>();
	
	private long equalParCount = 0;
	
	public long getEqualParCount() {
		return equalParCount;
	}

	protected void setEqualParCount(long equalParCount) {
		this.equalParCount = equalParCount;
	}

	public List<String> getMissingPar() {
		return missingPar;
	}

	public List<String> getUnequalPar() {
		return unequalPar;
	}
	
	protected void setMissingPar(List<String> missingPar) {
		this.missingPar = missingPar;
	}
	
	protected void setUnequalPar(List<String> unequalPar) {
		this.unequalPar = unequalPar;
	}
}

