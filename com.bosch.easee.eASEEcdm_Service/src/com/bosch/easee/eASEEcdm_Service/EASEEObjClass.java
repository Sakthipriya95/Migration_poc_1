/**
 * 
 */
package com.bosch.easee.eASEEcdm_Service;

/**
 * eASEE Object-Classes.<br>
 * Now Typesafe.<br>
 * Used in
 * {@link EASEEServiceMethods#searchObjects(EASEEObjClass, String, String, String, String, String, Boolean)}
 * 
 * <b>Not complete enumerated.</b> Open for extensions.
 * 
 * @author gpe2si 2011/08/12
 * 
 */
public enum EASEEObjClass {
	/**
	 * ObjClass of Hex.
	 */
	HEX("Hex File"),
	/**
	 * ObjClass of A2L.
	 */
	A2L("ASAP2 File"),
	/**
	 * ObjClass of DST.
	 */
	DST("Data Set"),
	/**
	 * ObjClass of Comp.
	 */
	COMP("Component"),
	/**
	 * ObjClass of APRJ.
	 */
	APRJ("Application Project"),
	/**
	 * ObjClass of DOCU.
	 */
	DOCU("Documents"),
	/**
	 * ObjClass of Log.
	 */
	LOG("Log File"),
	/**
	 * ObjClass of PVD.
	 */
	PVD("Project View Definition"),
	/**
	 * ObjClass of PRD.
	 */
	PRD("Project Rights Definition"),
	/**
	 * ObjClass of FPAR.
	 */
	FPAR("Function Parameter"),
	/**
	 * ObjClass of PAR.
	 */
	PAR("Parameter Set"),
	/**
	 * ObjClass of EPAR.
	 */
	EPAR("Preliminary Parameter"),
	/**
	 * ObjClass of ZFPAR.
	 */
	ZFPAR("Additional Function Parameter"),
	/**
	 * ObjClass of CPAR.
	 */
	CPAR("Complete Parameter Set"),
	/**
	 * ObjClass of EXD.
	 */
	EXD("External Data Objects"),

	/**
	 * Obj class of PST
	 */
	PST("Program Set"),

	/**
	 * Obj class for A2LEXT
	 */
	A2LEXT("External Customer File"),
	
	/**
	 * Obj Class for Cust_Hex
	 */
	CUST_HEX("Customer Hex File"),
	
	/**
	 * Obj Class for ARCH
	 */
	ARCH("Archived Customer File"),
	
	/**
	 * Obj Class for CSCONF
	 */
	CSCONF("Checksum Configuration File"),
	
	/**
	 * Obj Class for LAB
	 */
	LAB("Parameter Label File"),
	
	/**
	 * Obj Class for MAKECONF
	 */
	MAKECONF("Configuration file of SW-Build/Make"),
	
	/**
	 * Obj Class for MAKEOUT
	 */
	MAKEOUT("Outputs from SW-Build/Make"),
	
	/**
	 * Obj Class for SSD
	 */
	SSD("Config file for CheckSSD"),
	
	/**
	 * Obj Class for SSDDAT
	 */
	SSDDAT("SSD File Collection"),
	
	/**
	 * Obj Class for SSDEXE
	 */
	SSDEXE("SSD Application"),
	
	/**
	 * Obj Class for SSDLOG
	 */
	SSDLOG("Check SSD Result Files"),
	
	/**
	 * Obj Class for VIEW
	 */
	VIEW("View on a set of Parameters"),
	
	/**
	 *Obj Class for Monitor Rules 
	 */
	MON("Monitor Rules");

	/**
	 * Simple description.
	 */
	private final String description;

	/**
	 * Std. Constructor.
	 * 
	 * @param description
	 */
	EASEEObjClass(String description) {
		this.description = description;
	}

	/**
	 * Returns the description.
	 * 
	 * @return
	 */
	public final String getDescription() {
		return this.description;
	}

}
