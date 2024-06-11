/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.IRuleManager;
import com.bosch.caltool.apic.jpa.bo.IAttributeMappedObject;
import com.bosch.caltool.apic.jpa.bo.NodeAccessRight;
import com.bosch.caltool.apic.jpa.caldataimport.ICalDataImporterObject;
import com.bosch.caltool.dmframework.bo.AbstractDataCommand;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.bo.caldataimport.AbstractImportRuleManager;
import com.bosch.caltool.icdm.bo.caldataimport.ICalDataImportParamDetailsLoader;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.ssd.icdm.model.SSDCase;
import com.bosch.ssd.icdm.model.SSDConfigEnums.ParameterClass;


/**
 * CDRFunction.java, This class is the business object of the Calibration data review FUNCTION
 *
 * @author adn1cob
 */
// iCDM-471
public class CDRFunction extends AbstractParameterCollection<CDRFuncParameter>
    implements Comparable<CDRFunction>, ICalDataImporterObject {

  /**
   * Function name
   */
  private final String name;

  /**
   * All versions of this function
   */
  private Map<String, CDRFuncVersion> allVersionsMap;


  /**
   * This Constructor is protected and triggered from DataCache/DataLoader only
   *
   * @param cdrDataProvider the dataprovider
   * @param funcId functin id
   * @param funcName function name
   */
  protected CDRFunction(final CDRDataProvider cdrDataProvider, final Long funcId, final String funcName) {
    super(cdrDataProvider, funcId);
    this.name = funcName;
  }

  /**
   * Get all versions of this function
   *
   * @return map of CDRFuncVersion
   */
  public Map<String, CDRFuncVersion> getVersions() {
    if (this.allVersionsMap == null) {
      this.allVersionsMap = getDataLoader().fetchFunctionVersions(this.name);
    }
    return this.allVersionsMap;
  }

  /**
   * Get all PARAMETER object for this function. This method filters the VARIANT CODED params //iCDM-605
   *
   * @return set of CDRFuncParameter of this function
   * @throws SsdInterfaceException
   */
  @Override
  public Map<String, CDRFuncParameter> getAllParameters(final boolean includeRules) throws SsdInterfaceException {
    // rules is always fetched for the function parameters
    if (this.paramsMap == null) {
      if (isBigFunction()) {
        this.paramsMap = new HashMap<>();
      }
      else {
        // iCDM-605
        // Filter variant coded params
        this.paramsMap = filterVariantCodedParams(getDataLoader().fetchParameters(this.name, null /* version */));
      }
    }

    return this.paramsMap;
  }


  /**
   * @return the param map
   */
  public Map<String, CDRFuncParameter> getParamsMap() {
    return this.paramsMap;
  }

  /**
   * Get Sorted set of CDRFuncVersion
   *
   * @return CDRFuncVersion set
   */
  public SortedSet<CDRFuncVersion> getSortedVersions() {
    final SortedSet<CDRFuncVersion> sortedVersions = new TreeSet<CDRFuncVersion>();
    if (this.allVersionsMap == null) {
      this.allVersionsMap = getVersions();
    }
    sortedVersions.addAll(this.allVersionsMap.values());

    return sortedVersions;
  }


  /**
   * @return true if the relevant name is 'Y'
   */
  public boolean isRelevant() {
    String relevantName = getEntityProvider().getDbFunction(getID()).getRelevantName();
    if (ApicConstants.YES.equals(relevantName)) {
      return true;
    }
    return false;
  }

  /**
   * @return the name
   */
  @Override
  public String getName() {
    return this.name;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.CDR_FUNCTION;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRFunction other) {
    return ApicUtil.compare(getName(), other.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // Not implemented
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbFunction(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbFunction(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFunction(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFunction(getID()).getModifiedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasVersions() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isParamPropsModifiable() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isParamMappingModifiable() {
    // List of parameters cannot be modified for a function.
    return false;
  }


  /**
   * @return true if it is a big function
   */
  public boolean isBigFunction() {
    String bigFunction = getEntityProvider().getDbFunction(getID()).getBigFunction();
    if (bigFunction == null) {
      return false;
    }
    return bigFunction.equals(ApicConstants.YES);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IRuleManager getRuleManager() {
    if (this.ruleManager == null) {
      // this.ruleManager = new FunctionRuleManager(this);
    }
    return this.ruleManager;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getObjectTypeName() {
    return ApicConstants.FUNCTION;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRulesModifiable() {
    return true;
  }

  // ICDM-1540
  /**
   * Check if the rule set is modifiable
   */
  @Override
  public boolean isModifiable() {
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    return (curUserAccRight != null) && curUserAccRight.hasWriteAccess();
  }

  /**
   * Not applicable
   * <p>
   * {@inheritDoc}
   */
  @Override
  public ICalDataImporterObject getCaldataImportObject() {
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRFunction getDataObject() {
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void enableParameterRefresh() {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getSsdNodeID() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getSsdVersNodeID() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDCase getSsdUseCase() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ParameterClass getSsdParamClass() {
    return ParameterClass.CUSTSPEC;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public Collection<IAttributeMappedObject> getAttrMappedObjects() throws SsdInterfaceException {
    Collection<IAttributeMappedObject> attrs = new TreeSet<IAttributeMappedObject>();
    attrs.addAll(getAllParameters(false).values());
    return attrs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ICalDataImportParamDetailsLoader getParamDetailsLoader() {
    return null;// new FunctionParamDetailsLoader(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean enableLabelListCreation() {
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isInputParamsChecked() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AbstractImportRuleManager getImportRuleManager() {
    return null;// new FunctionImportRuleManager(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean addParamsFromFile() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AbstractDataCommand getCreateParamsCommands(final List<String> paramsTobeAdded) {
    // Not applicable
    return null;
  }

}
