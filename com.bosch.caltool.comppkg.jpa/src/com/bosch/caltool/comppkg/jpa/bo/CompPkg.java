/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.IAttributeMappedObject;
import com.bosch.caltool.apic.jpa.bo.Link;
import com.bosch.caltool.apic.jpa.bo.NodeAccessRight;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.exception.IcdmRuntimeException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBc;
import com.bosch.caltool.icdm.database.entity.comppkg.TCpRuleAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.comppkg.CompPkgType;


/**
 * Component package ICdm-949 isIdValid method removed
 */
@Deprecated
public class CompPkg extends AbstractCPObject implements Comparable<CompPkg>, IAttributeMappedObject {

  /**
   * CP name key in component pkg
   */
  private static final String CP_NAME = "CP_NAME";

  /**
   * CP english desc key in component pkg
   */
  private static final String CP_DESC_ENG = "CP_DESC_GER";

  /**
   * CP german desc key in component pkg
   */
  private static final String CP_DESC_GER = "CP_DESC_GER";

  /**
   * CP deleted flag key in component pkg
   */
  private static final String CP_DELETED_FLAG = "CP_DELETED_FLAG";

  /**
   * CP type key in component pkg
   */
  private static final String CP_TYPE = "CP_TYPE";

  /**
   * CP SSD Node ID key in component pkg
   */
  private static final String CP_SSD_NODE_ID = "CP_SSD_NODE_ID";

  /**
   * iCDM-1326 <br>
   * CP SSD Vers Node ID key in component pkg for labelList creation
   */
  private static final String CP_SSD_VERS_NODE_ID = "CP_SSD_VERS_NODE_ID";

  /**
   * CP SSD Use Case key in component pkg
   */
  private static final String CP_SSD_USE_CASE = "CP_SSD_USE_CASE";

  /**
   * CP SSD Param class key in component pkg
   */
  private static final String CP_SSD_PARAM_CLASS = "CP_SSD_PARAM_CLASS";

  /**
   * Defines map of BC's for this comp pkg
   */
  private Map<Long, CompPkgBc> bcMap;

  /**
   * Defines map of Rule attributes for this comp pkg
   */
  private Map<Long, CompPkgRuleAttr> ruleAttrMap;

  /**
   * Rule importer object
   */
  private CompPkgCaldataImporterObject caldataImpObj;

  /**
   * Param collection object
   */
  private CompPkgParamCollectionObject paramColObj;

  /**
   * Only atmost 5 BC's can be mapped for a comp package
   */
  private static final int MAX_BC_COUNT = 5;

  /**
   * Only atmost 1 BC's can be mapped for a NE type comp package
   */
  private static final int MAX_BC_COUNT_NE = 1;

  /**
   * Constructor
   *
   * @param dataProvider the data provider
   * @param objID resultID
   */
  protected CompPkg(final CPDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
  }

  /**
   * @return CompPkg name
   */
  public String getCompPkgName() {
    return CommonUtils.checkNull(getEntityProvider().getDbCompPkg(getID()).getCompPkgName());
  }

  /**
   * @return created date
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCompPkg(getID()).getCreatedDate());
  }

  /**
   * @return created user
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbCompPkg(getID()).getCreatedUser();
  }

  /**
   * @return description eng
   */
  public String getDescEng() {
    return CommonUtils.checkNull(getEntityProvider().getDbCompPkg(getID()).getDescEng());
  }

  /**
   * @return description german
   */
  public String getDescGer() {
    return CommonUtils.checkNull(getEntityProvider().getDbCompPkg(getID()).getDescGer());
  }

  /**
   * Get the description, based on language
   *
   * @return description, based on language
   */
  @Override
  public String getDescription() {

    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getDescEng(), getDescGer(),
        ApicConstants.EMPTY_STRING);
  }

  /**
   * @return modified date
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCompPkg(getID()).getModifiedDate());
  }

  /**
   * @return modified user
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbCompPkg(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbCompPkg(getID()).getVersion();
  }

  /**
   * Get the Set of BC's mapped to this Component Package
   *
   * @return Sorted BC set
   */
  public SortedSet<CompPkgBc> getCompPkgBcs() {
    return new TreeSet<CompPkgBc>(getCompPkgBcsMap().values());
  }

  /**
   * @return Map of BC's made the method as public for uasge in the Outline Filter
   */
  public Map<Long, CompPkgBc> getCompPkgBcsMap() {
    if (this.bcMap == null) {
      this.bcMap = new HashMap<Long, CompPkgBc>();
      // ICDM-933 to set the comppkg is loaded in cache
      getApicDataProvider().setObjLoaded(this);
      final List<TCompPkgBc> bcList = getEntityProvider().getDbCompPkg(getID()).getTCompPkgBcs();
      if (bcList != null) {
        createCompBC(bcList);
      }
    }
    return this.bcMap;
  }

  /**
   * @return Map of BC's made the method as public for uasge in the Outline Filter
   */
  protected Map<Long, CompPkgRuleAttr> getCompPkgRuleAttrMap() {
    if (this.ruleAttrMap == null) {
      this.ruleAttrMap = new ConcurrentHashMap<Long, CompPkgRuleAttr>();

      final List<TCpRuleAttr> dbList = getEntityProvider().getDbCompPkg(getID()).getTCpRuleAttrs();
      if (dbList != null) {
        for (TCpRuleAttr dbRuleAttr : dbList) {
          this.ruleAttrMap.put(dbRuleAttr.getCpRuleAttrId(),
              new CompPkgRuleAttr(getDataProvider(), dbRuleAttr.getCpRuleAttrId()));
        }
      }
    }
    return this.ruleAttrMap;
  }

  /**
   * Icdm-949 sonar Qube
   *
   * @param bcList
   */
  private void createCompBC(final List<TCompPkgBc> bcList) {
    for (TCompPkgBc tCompPkgBc : bcList) {
      CompPkgBc compPkgBc = getDataCache().getCompPkgBc(tCompPkgBc.getCompBcId());
      if (compPkgBc == null) { // NOPMD by adn1cob on 6/30/14 10:15 AM
        compPkgBc = new CompPkgBc(getDataProvider(), tCompPkgBc.getCompBcId());
        // add it to all bcs in data cache
        getDataCache().getAllCompPkgBcs().put(tCompPkgBc.getCompBcId(), compPkgBc);
      }
      this.bcMap.put(tCompPkgBc.getCompBcId(), compPkgBc);
    }
  }

  /**
   * Check, if the cmp pkg has been marked as deleted
   *
   * @return TRUE, if the cmp pkg has been marked as deleted
   */
  public boolean isDeleted() {
    if (getEntityProvider().getDbCompPkg(getID()) != null) {
      return ApicConstants.YES.equals(getEntityProvider().getDbCompPkg(getID()).getDeletedFlag());
    }
    return false;

  }

  /**
   * @return User access right for the node
   */
  public NodeAccessRight getCurrentUserAccessRights() {
    return getApicDataProvider().getNodeAccRight(getID());
  }


  /**
   * @return access rights
   */
  public SortedSet<NodeAccessRight> getAccessRights() {
    return getApicDataProvider().getNodeAccessRights(getID());

  }

  /**
   * Returns whether the logged in user has privilege to modify access rights to this Project ID Card.
   *
   * @return <code>true</code> if current user can modify the access rights.
   */
  public boolean canModifyAccessRights() {
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.hasGrantOption()) {
      return true;
    }
    return false;

  }

  /**
   * @return boolean if the user has the access to change the owner flag
   */
  public boolean canModifyOwnerRights() {

    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if (((curUserAccRight != null) && curUserAccRight.isOwner()) ||
        getDataCache().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.hasWriteAccess() && !isDeleted()) {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final ConcurrentMap<String, String> objDetails = new ConcurrentHashMap<>();

    objDetails.put(CP_NAME, getCompPkgName());
    objDetails.put(CP_DESC_ENG, getDescEng());
    objDetails.put(CP_DESC_GER, CommonUtils.checkNull(getDescGer()));
    objDetails.put(CP_DELETED_FLAG, String.valueOf(isDeleted()));
    objDetails.put(CP_TYPE, getType().getLiteral());

    if (getType() == CompPkgType.NE) {
      CompPkgCaldataImporterObject impObj = getCaldataImporterObject();

      objDetails.put(CP_SSD_NODE_ID, String.valueOf(impObj.getSsdNodeID()));
      objDetails.put(CP_SSD_USE_CASE, impObj.getSsdUseCaseStr());
      objDetails.put(CP_SSD_PARAM_CLASS, impObj.getSsdParamClassStr());
      objDetails.put(CP_SSD_VERS_NODE_ID, String.valueOf(impObj.getSsdVersNodeID()));
    }

    return objDetails;
  }

  /**
   * Check if any more BC's can be added to this comp package
   *
   * @return true if number of bc's is less than max limit
   */
  public boolean canAddBCs() {
    int limit = (getType() == CompPkgType.NE) ? MAX_BC_COUNT_NE : MAX_BC_COUNT;
    return getCompPkgBcsMap().size() < limit;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompPkg other) {
    return ApicUtil.compare(getCompPkgName(), other.getCompPkgName());
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
  public String getName() {
    return getCompPkgName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CPEntityType.COMP_PKG;
  }

  // ICDM-933
  /**
   * @return TRUE if the component package is loaded in data cache
   */
  public boolean isCmpPkgLoaded() {
    return getApicDataProvider().isObjLoaded(this);
  }

  // ICDM-977
  /**
   * @return SortedSet of links
   */
  public SortedSet<Link> getLinks() {
    return getApicDataProvider().getLinks(this);
  }

  /**
   * @return sorted set of CompPkgRuleAttr
   */
  public SortedSet<CompPkgRuleAttr> getRuleAttrSet() {
    return new TreeSet<>(getCompPkgRuleAttrMap().values());
  }


  /**
   * Returns the string representation of Rule Attributes. Should be used, only when the text is to be displayed
   * directly. To get rule attributes as objects, use <code>getRuleAttrSet()</code>
   *
   * @return SSD Param Class String
   */
  public String getRuleAttrsStr() {
    StringBuilder ruleAttrStr = new StringBuilder();
    int counter = 0;
    for (CompPkgRuleAttr ruleAttr : getRuleAttrSet()) {
      if (counter > 0) {
        ruleAttrStr.append("; ");
      }
      ruleAttrStr.append(ruleAttr.getName());
      counter++;
    }

    return ruleAttrStr.toString();
  }

  /**
   * @return the type of this component package
   */
  public CompPkgType getType() {
    return CompPkgType.getType(getEntityProvider().getDbCompPkg(getID()).getCompPkgType());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Attribute> getAttributes() {
    Set<Attribute> mappedAttributes = new TreeSet<>();
    for (CompPkgRuleAttr compRuleAttr : getRuleAttrSet()) {
      mappedAttributes.add(compRuleAttr.getAttribute());
    }
    return mappedAttributes;
  }

  /**
   * @return CompPkgCaldataImporterObject
   */
  public CompPkgCaldataImporterObject getCaldataImporterObject() {
    if (getType() != CompPkgType.NE) {
      // Only NE type component package can import caldata
      throw new IcdmRuntimeException("This component package is not of type 'NE'");
    }

    synchronized (this) {
      if (this.caldataImpObj == null) {
        this.caldataImpObj = new CompPkgCaldataImporterObject(this);
      }
    }

    return this.caldataImpObj;
  }

  /**
   * @return CompPkgParamCollectionObject
   */
  public CompPkgParamCollectionObject getParamCollectionObject() {
    if (getType() != CompPkgType.NE) {
      // Only NE type component packages can be opened in Rule Editor !!
      throw new IcdmRuntimeException("This component package is not of type 'NE'");
    }

    synchronized (this) {
      if (this.paramColObj == null) {
        this.paramColObj = new CompPkgParamCollectionObject(this);
      }
    }

    return this.paramColObj;
  }


}
