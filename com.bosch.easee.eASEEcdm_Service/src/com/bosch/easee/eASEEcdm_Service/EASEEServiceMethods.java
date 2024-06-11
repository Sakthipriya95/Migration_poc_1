/**
 * 
 */
package com.bosch.easee.eASEEcdm_Service;

import java.util.List;
import java.util.Set;

import com.vector.easee.application.cdmservice.CDMWebServiceException;
import com.vector.easee.application.cdmservice.IParameterValue;
import com.vector.easee.application.cdmservice.ObjInfoEntryType;
import com.vector.easee.application.cdmservice.WSAttrMapList;
import com.vector.easee.application.cdmservice.WSProgramKey;

/**
 * Enhanced- and Delegate-Methods from CDM WebService.
 * 
 * @version 1.0 (2011/07/26)
 * @author [GPE2SI]
 */
public interface EASEEServiceMethods {

	/**
	 * Delegate-Method from CDM WebService-Interface.<br>
	 * eASEE.cdm Version 1.0.7 <br>
	 * <b>2.14.3 Product attributes and values in APRJ</b><br>
	 * 
	 * @see com.vector.easee.application.cdmservice.CDMWebService#getActivatedProductAttributeValues(String,
	 *      String, String)
	 * 
	 * @author [GPE2SI]
	 * 
	 * @param projectVersionNo
	 * @param name
	 * @return
	 */
	public WSAttrMapList getActivatedProductAttributeValues(
			String projectVersionNo, String name);

	/**
	 * Delegate-Method from CDM WebService-Interface.<br>
	 * eASEE.cdm Version 1.0.7 <br>
	 * <b>2.14.1 Attributes in domain</b><br>
	 * 
	 * @see com.vector.easee.application.cdmservice.CDMWebService#getProductAttributeValues(String,
	 *      String)
	 * 
	 * @author [GPE2SI]
	 * @param name
	 * @return
	 */
	public WSAttrMapList getProductAttributeValues(String name);

	/**
	 * Delegate-Method from CDM WebService-Interface.<br>
	 * eASEE.cdm Version 1.0.7 <br>
	 * <b>2.14.4 Product keys in APRJ</b><br>
	 * 
	 * @see com.vector.easee.application.cdmservice.CDMWebService#getProductKeyAttributeValues(String,String,
	 *      String)
	 * 
	 * @author [GPE2SI]
	 * @param name
	 * @return
	 */
	public WSAttrMapList getProductKeyAttributeValues(String projectVersionNo,
			String name);

	/**
	 * Delegate-Method from CDM WebService-Interface.<br>
	 * eASEE.cdm Version 1.0.7 <br>
	 * <b>2.14.2 Program keys in APRJ</b><br>
	 * 
	 * @see com.vector.easee.application.cdmservice.CDMWebService#getProgramKeys(String,String,
	 *      String)
	 * 
	 * @author [GPE2SI]
	 * @param projectVersionNo
	 * @param names
	 * @return
	 */
	public List<WSProgramKey> getProgramKeys(String projectVersionNo,
			List<String> names);

	/**
	 * Delegate-Method from CDM WebService-Interface.<br>
	 * eASEE.cdm Version 1.0.7 <br>
	 * <b>2.4 Search for objects</b><br>
	 * 
	 * @see com.vector.easee.application.cdmservice.CDMWebService#searchObjects(String,
	 *      String, String, String, String, String, String, Boolean)
	 *      (String,String, String)
	 * 
	 * @author [GPE2SI]
	 * @param objClass
	 * @param objName
	 * @param objVariant
	 * @param objRevision
	 * @param objType
	 * @param objDomain
	 * @param objChkInState
	 * @return
	 */
	public List<ObjInfoEntryType> searchObjects(EASEEObjClass objClass,
			String objName, String objVariant, String objRevision,
			String objType, String objDomain, Boolean objChkInState);

	/**
	 * Simple Get.<br>
	 * 
	 * @see {@link #searchObjects(EASEEObjClass, String, String, String, String, String, Boolean)}
	 * 
	 * @author [GPE2SI]
	 * 
	 * @param objClass
	 * @param objName
	 * @param objVariant
	 * @param objRevision
	 * @param checkedOut
	 * @return
	 */
	public List<ObjInfoEntryType> getObjects(EASEEObjClass objClass,
			String objName, String objVariant, Integer objRevision,
			Boolean checkedOut);
	
	/**
	 * 
	 * @param versionId
	 * @param parameterNames
	 * @return
	 * @throws CDMWebServiceException 
	 */
	public Set<IParameterValue> getDataSetValues(String versionId, String[]
		parameterNames) throws CDMWebServiceException;

	
	
}
