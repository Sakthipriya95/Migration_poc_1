/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.fm;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionStatus;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.FM_VERS_STATUS;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixDetailsModel;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.uc.IUseCaseItem;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author mkl2cob
 */
public class FocusMatrixVersionClientBO implements IBasicObject, Comparable<FocusMatrixVersionClientBO> {

  /**
   * Focus Matrix review Status
   */
  public static enum FM_REVIEW_STATUS {
                                       /**
                                        * Yes
                                        */
                                       YES("Y"),
                                       /**
                                        * No
                                        */
                                       NO("N"),
                                       /**
                                        * Not defined
                                        */
                                       NOT_DEFINED("");

    /**
     * Status of review
     */
    public final String status;

    FM_REVIEW_STATUS(final String type) {
      this.status = type;
    }

    /**
     * Return the FM review object for the given db type
     *
     * @param status db literal of type
     * @return the file type object
     */
    public static FM_REVIEW_STATUS getStatus(final String status) {
      for (FM_REVIEW_STATUS type : FM_REVIEW_STATUS.values()) {
        if (type.getStatusStr().equals(status)) {
          return type;
        }
      }
      return NOT_DEFINED;
    }

    /**
     * @return the type
     */
    public String getStatusStr() {
      return this.status;
    }
  }

  /**
   * FocusMatrixVersion
   */
  private final FocusMatrixVersion fmVersion;

  /**
   * Map of focus matrix version attributes, if focus matrix version is an older version.
   * <p>
   * Note : map is empty if this version is working set
   * <p>
   * Key - Attribute ID <br>
   * Value - BO
   */
  private final ConcurrentMap<Long, Set<FocusMatrixVersionAttrClientBO>> fmVersAttrMap = new ConcurrentHashMap<>();

  private final FocusMatrixDataHandler fmDataHandler;

  /**
   * Defined focus matrix items of this PIDC
   * <p>
   * Key - Attribute ID <br>
   * Value - Map of &lt;UC Item ID, Focus Matrix Definition Item&gt;
   */
  private final ConcurrentMap<Long, ConcurrentMap<Long, FocusMatrixDetails>> projFocusMatrixDefMap =
      new ConcurrentHashMap<>();

  /**
   * Key - UCItem ID <br>
   * Value - Map of Attribute ID, UCPA id;
   */
  private final ConcurrentMap<Long, ConcurrentMap<Long, Long>> ucpAttrIdMap = new ConcurrentHashMap<>();
  /**
   * If true, focus matrix children are loaded
   */
  private boolean focusMatrixDefLoaded;

  private final List<IUseCaseItemClientBO> selectedUCItemsList = new ArrayList<>();

  private User reviewedUser;


  /**
   * Constructor
   *
   * @param fmVersion FocusMatrixVersion
   * @param fmDataHandler FocusMatrixDataHandler
   */
  public FocusMatrixVersionClientBO(final FocusMatrixVersion fmVersion, final FocusMatrixDataHandler fmDataHandler) {
    this.fmVersion = fmVersion;
    this.fmDataHandler = fmDataHandler;

  }

  /**
   * @return the fmVersion
   */
  public FocusMatrixVersion getFmVersion() {
    return this.fmVersion;
  }

  /**
   * @return true if this is working set
   */
  public boolean isWorkingSet() {
    return ApicConstants.FM_VERS_STATUS.getStatus(this.fmVersion.getStatus()) == FM_VERS_STATUS.WORKING_SET;

  }


  /**
   * @return SortedSet<FocusMatrixAttribute>
   * @throws ApicWebServiceException
   */
  public SortedSet<FocusMatrixAttributeClientBO> getFocusMatrixAttrsSet() throws ApicWebServiceException {
    return isWorkingSet() ? getFmAttrsFromProjectAttrs() : getFmAttrsFromFmVersAttrs();
  }

  /**
   * @return
   * @throws ApicWebServiceException
   */
  // ICDM-2569
  private SortedSet<FocusMatrixAttributeClientBO> getFmAttrsFromFmVersAttrs() throws ApicWebServiceException {
    SortedSet<FocusMatrixAttributeClientBO> fmAttrSet = new TreeSet<>();
    for (Entry<Long, Set<FocusMatrixVersionAttrClientBO>> entry : getFocusMatrixVersionAttrMap().entrySet()) {
      Attribute attr = this.fmDataHandler.getPidcDataHandler().getAttributeMap().get(entry.getKey());
      FocusMatrixAttributeClientBO fmAttr = new FocusMatrixAttributeClientBO(this, attr, this.fmDataHandler);
      fmAttr.getFmVersAttrSet().addAll(entry.getValue());
      addUCItems(attr, fmAttr);
      fmAttrSet.add(fmAttr);
    }
    return fmAttrSet;
  }

  /**
   * Map of focus matrix version attributes, if focus matrix version is an older version.
   * <p>
   * Note : map is empty if this version is working set
   * <p>
   * Key - Attribute ID <br>
   * Value - set of BOs
   *
   * @return map
   * @throws ApicWebServiceException
   */
  Map<Long, Set<FocusMatrixVersionAttrClientBO>> getFocusMatrixVersionAttrMap() throws ApicWebServiceException {

    if (this.fmVersAttrMap.isEmpty()) {
      Set<FocusMatrixVersionAttrClientBO> fmVersAttrs =
          this.fmDataHandler.getFocusMatrixVersionAttributes(this.fmVersion.getId());
      for (FocusMatrixVersionAttrClientBO fmVersAttr : fmVersAttrs) {
        long attrID = fmVersAttr.getAttribute().getId();
        Set<FocusMatrixVersionAttrClientBO> fmVersAttrSet = this.fmVersAttrMap.get(attrID);
        if (fmVersAttrSet == null) {
          fmVersAttrSet = new HashSet<>();
          this.fmVersAttrMap.put(attrID, fmVersAttrSet);
        }
        fmVersAttrSet.add(fmVersAttr);
      }
    }
    return this.fmVersAttrMap;
  }

  /**
   * @return
   */
  // ICDM-2569
  private SortedSet<FocusMatrixAttributeClientBO> getFmAttrsFromProjectAttrs() {
    SortedSet<FocusMatrixAttributeClientBO> setFmAttrs = new TreeSet<>();

    for (Entry<PidcVersionAttribute, Attribute> fmProjAttr : this.fmDataHandler.getFocusMatrixApplicableAttrMap()
        .entrySet()) {
      this.fmDataHandler.getPidcDataHandler().getPidcVersAttrMap();
      FocusMatrixAttributeClientBO fmAttr =
          new FocusMatrixAttributeClientBO(this, fmProjAttr.getKey(), fmProjAttr.getValue(), this.fmDataHandler);
      addUCItems(fmProjAttr.getValue(), fmAttr);
      setFmAttrs.add(fmAttr);
    }
    return setFmAttrs;
  }

  /**
   * @param attribute
   * @param fmAttr
   */
  private void addUCItems(final Attribute attribute, final FocusMatrixAttributeClientBO fmAttr) {
    // if selection is not null
    if (null != getSelectedUCItemsList()) {
      // iterate over the list of selected items
      for (IUseCaseItemClientBO ucItem : getSelectedUCItemsList()) {
        FocusMatrixUseCaseItem fmUCItem = new FocusMatrixUseCaseItem(ucItem, this);

        if (null != this.ucpAttrIdMap.get(ucItem.getID())) {
          fmUCItem.getAttributeMapping().put(attribute, this.ucpAttrIdMap.get(ucItem.getID()).get(attribute.getId()));
        }

        fmAttr.getFmUseCaseItemsSet().add(fmUCItem);
      }
    }
  }

  /**
   * @return map of focus matrix definitions of this PIDC <br>
   *         Key - Attribute ID <br>
   *         Value - Map of &lt;UC Item ID, Focus Matrix Definition Item&gt;
   */
  public Map<Long, ConcurrentMap<Long, FocusMatrixDetails>> getFocusMatrixItemMap() {
    if (!this.focusMatrixDefLoaded) {

      this.projFocusMatrixDefMap.clear();

      Long attrID;
      Long ucItemID;
      ConcurrentMap<Long, FocusMatrixDetails> childMap;


      FocusMatrixDetailsModel fmDetailsModel = this.fmDataHandler.getFocusMatrixEntries(this.fmVersion.getId());
      for (FocusMatrix tFocusMatrix : fmDetailsModel.getFocusMatrixMap().values()) {

        attrID = tFocusMatrix.getAttrId();
        childMap = this.projFocusMatrixDefMap.get(attrID);
        if (childMap == null) {
          childMap = new ConcurrentHashMap<>();
          this.projFocusMatrixDefMap.put(attrID, childMap);
        }
        // Here it is assumed that primary key is unique across use case ID and uc section id.
        if (tFocusMatrix.getSectionId() == null) {
          ucItemID = tFocusMatrix.getUseCaseId();
        }
        else {
          ucItemID = tFocusMatrix.getSectionId();
        }

        FocusMatrixDetails fmDetail = new FocusMatrixDetails(tFocusMatrix, this.fmVersion, this.fmDataHandler);
        childMap.put(ucItemID, fmDetail);
      }

      // iterate through ucp attr map to populate the ucpAttrIdMap
      for (UcpAttr ucpAttr : fmDetailsModel.getUcpAttrSet()) {
        if (ucpAttr.getSectionId() == null) {
          ucItemID = ucpAttr.getUseCaseId();
        }
        else {
          ucItemID = ucpAttr.getSectionId();
        }
        ConcurrentMap<Long, Long> attrToUcpaMap = this.ucpAttrIdMap.get(ucItemID);
        if (null == attrToUcpaMap) {
          // if there is no entry for uc item id
          attrToUcpaMap = new ConcurrentHashMap<Long, Long>();
          this.ucpAttrIdMap.put(ucItemID, attrToUcpaMap);
        }
        attrToUcpaMap.put(ucpAttr.getAttrId(), ucpAttr.getId());
      }

      // load reviewed user
      this.reviewedUser = fmDetailsModel.getReviewedUser();
      this.focusMatrixDefLoaded = true;


    }

    return this.projFocusMatrixDefMap;
  }

  /**
   * @return the selectedUCItemsList
   */
  public List<IUseCaseItemClientBO> getSelectedUCItemsList() {
    return this.selectedUCItemsList;
  }

  /**
   * @return FocusMatrixDataHandler
   */
  public FocusMatrixDataHandler getFMDataHandler() {
    return this.fmDataHandler;
  }

  /**
   * @return the ucpAttrIdMap
   */
  public ConcurrentMap<Long, ConcurrentMap<Long, Long>> getUcpAttrIdMap() {
    return this.ucpAttrIdMap;
  }

  /**
   * @return boolean
   * @throws ApicWebServiceException
   */
  public boolean isModifiable() throws ApicWebServiceException {

    PidcVersion pidcVer = getFMDataHandler().getPidcVersion();
    NodeAccess curUserAccRight = new CurrentUserBO().getNodeAccessRight(pidcVer.getPidcId());

    boolean canModify = false;

    if ((curUserAccRight != null) && curUserAccRight.isWrite()) {
      canModify =
          !(pidcVer.isDeleted() || (PidcVersionStatus.getStatus(pidcVer.getPidStatus()) == PidcVersionStatus.LOCKED));
    }

    return isWorkingSet() && canModify;

  }

  /**
   * @return User
   */
  public User getReviewedUser() {
    if (!this.focusMatrixDefLoaded) {
      getFocusMatrixItemMap();
    }
    return this.reviewedUser;

  }

  /**
   * /** Checks whether focus matrix definition is available in the pidc version. The following conditions should e
   * satisfied if focus matrix deleted flag is not 'Y' if attribute associated with focus matrix, deleted flag is not
   * 'Y' if use case section and its parents use case associated with focus matrix deleted flag is not 'Y'
   *
   * @param list List<IUseCaseItemClientBO>
   * @return true, if Focus Matrix are mapped to this PIDC version
   */

  public boolean hasFocusMatrix(final List<IUseCaseItemClientBO> list) {
    // ICDM-2644

    if ((null == getFocusMatrixItemMap()) || getFocusMatrixItemMap().isEmpty()) {
      // No focus matrix definitions
      return false;
    }
    PidcVersionAttribute pidcAttr;
    IUseCaseItem useCaseItem;

    for (Entry<Long, ConcurrentMap<Long, FocusMatrixDetails>> attrFMValues : this.projFocusMatrixDefMap.entrySet()) {
      for (FocusMatrixDetails dbFm : attrFMValues.getValue().values()) {

        if (dbFm.isDeleted()) {
          // FM marked as deleted
          continue;
        }

        pidcAttr = this.fmDataHandler.getPidcDataHandler().getPidcVersAttrMap().get(attrFMValues.getKey());
        if (isFMApplicableForPIDCAttr(pidcAttr)) {
          // Condition pidcAttr is null includes deleted attributes, attributes missing due to attr dependency
          continue;
        }

        useCaseItem = dbFm.getUseCaseItem();

        if (isUCItemOrParentDeleted(useCaseItem, list)) {
          // Use case item/parent item is deleted
          continue;
        }
        return true;
      }

    }
    return false;
  }

  /**
   * @param useCaseItem
   * @param list
   * @return
   */
  private boolean isUCItemOrParentDeleted(final IUseCaseItem useCaseItem, final List<IUseCaseItemClientBO> list) {

    for (IUseCaseItemClientBO iUseCaseItemClientBO : list) {
      if (CommonUtils.isEqual(iUseCaseItemClientBO.getUcItem(), useCaseItem)) {
        return useCaseItem.isDeleted() || iUseCaseItemClientBO.isParentLevelDeleted();
      }
    }
    return false;
  }

  /**
   * @param pidcAttr
   * @return
   */
  private boolean isFMApplicableForPIDCAttr(final PidcVersionAttribute pidcAttr) {
    return (pidcAttr == null) || !pidcAttr.isFocusMatrixApplicable();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    if (isWorkingSet()) {
      return this.fmVersion.getName();
    }
    StringBuilder name = new StringBuilder();
    name.append(this.fmVersion.getRevNum()).append(". ").append(this.fmVersion.getCreatedDate());
    if (!CommonUtils.isEmptyString(this.fmVersion.getName())) {
      name.append(" - ").append(this.fmVersion.getName());
    }
    return name.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return this.fmVersion.getRemark();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) &&
        ModelUtil.isEqual(getFmVersion().getId(), ((FocusMatrixVersionClientBO) obj).getFmVersion().getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getFmVersion().getId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FocusMatrixVersionClientBO other) {
    long thisRevNo = this.fmVersion.getRevNum();
    if (!isWorkingSet()) {
      thisRevNo = 100000000 - thisRevNo;
    }

    long otherRevNo = other.getFmVersion().getRevNum();
    if (!other.isWorkingSet()) {
      otherRevNo = 100000000 - otherRevNo;
    }

    int compareResult = ApicUtil.compare(thisRevNo, otherRevNo);

    if (compareResult == 0) {
      compareResult = ApicUtil.compare(getFmVersion().getId(), other.getFmVersion().getId());
    }
    return compareResult;
  }

  /**
   * Resets focus matrix definitions loaded in this class
   */
  public void resetFocusMatrixDefinitionLoaded() {
    this.focusMatrixDefLoaded = false;
  }
}
