/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa;

import java.util.SortedSet;

import com.bosch.caltool.a2l.jpa.bo.A2LResponsibility;
import com.bosch.caltool.a2l.jpa.bo.A2LWpResponsibility;
import com.bosch.caltool.apic.jpa.bo.A2LFile;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.PIDCA2l;
import com.bosch.caltool.apic.jpa.bo.PIDCAttribute;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author rgo7cob
 */
@Deprecated
public class A2lWPRespResolver {


  private final A2LEditorDataProvider a2lEditorDp;
  private final A2LFile a2lFile;
  private final ApicDataProvider apicDataProvider;
  private final A2LDataProvider a2lDataProvider;
  private Long rootId;


  private Long typeID;

  /**
   * constant for error message when creating A2l Resp.
   */
  public static final String ERROR_CREATING_A2L_RESP = "Error during creation of responsibilities for A2L file";

  /**
   * @param apicDataProvider apicDataProvider
   * @param a2lDataProvider a2lDataProvider
   */
  public A2lWPRespResolver(final A2LEditorDataProvider a2lEditorDp, final A2LFile a2lFile,
      final ApicDataProvider apicDataProvider, final A2LDataProvider a2lDataProvider) {
    this.a2lEditorDp = a2lEditorDp;
    this.a2lFile = a2lFile;
    this.a2lDataProvider = a2lDataProvider;
    this.apicDataProvider = apicDataProvider;
  }


  /**
   * resolve the wp resp
   *
   * @return
   */
  private A2LResponsibility resolveWpResp() {

    if (this.a2lDataProvider.getDataCache().getWpRespMap().isEmpty()) {
      this.a2lDataProvider.fetchAllWpResp();
    }

    // get the Pidc a2l
    PIDCA2l pidcA2l = this.a2lFile.getPidcA2l();
    // version
    PIDCVersion pidcVersion = pidcA2l.getPidcVersion();
    // group or Fcwp.
    PIDCAttribute pidcWpAttr = this.a2lDataProvider.getPidcWpAttr(pidcVersion);
    if ((pidcWpAttr != null) && (pidcWpAttr.getAttributeValue() != null)) {
      return fetchA2lWpResp(pidcA2l, pidcVersion, pidcWpAttr);
    }
    return null;
  }


  /**
   * @param pidcA2l
   * @param pidcVersion
   * @param pidcWpAttr
   * @return
   */
  private A2LResponsibility fetchA2lWpResp(final PIDCA2l pidcA2l, final PIDCVersion pidcVersion,
      final PIDCAttribute pidcWpAttr) {
    this.typeID = pidcWpAttr.getAttributeValue().getValueID();
    // root Group Value id
    PIDCAttribute rootGrpPidcAttr =
        this.a2lEditorDp.getRootGrpPidcAttr(this.apicDataProvider, pidcVersion.getAttributes(false));
    this.rootId = 0l;
    if (rootGrpPidcAttr != null) {
      AttributeValue attributeValue = rootGrpPidcAttr.getAttributeValue();

      if (attributeValue != null) {
        this.rootId = attributeValue.getID();
      }
    }
    // get A2l responsibility
    A2LResponsibility a2lResp = this.a2lDataProvider.getA2lResp(pidcA2l.getID(), this.typeID, this.rootId);
    if (a2lResp != null) {
      setWPRespinEditorDp(a2lResp, pidcA2l);
    }
    return a2lResp;
  }


  /**
   * @param a2lResp
   * @param pidcA2l
   */
  private void setWPRespinEditorDp(final A2LResponsibility a2lResp, final PIDCA2l pidcA2l) {
    // fetch the a2l groups corresponding
    if (CommonUtils.isEqual(this.a2lEditorDp.getMappingSourceID(),
        Long.valueOf(this.apicDataProvider.getParameterValue(ApicConstants.GROUP_MAPPING_ID)))) {
      this.a2lDataProvider.fetchA2lGrp(pidcA2l.getA2LId());
    }
    if (CommonUtils.isNullOrEmpty(this.a2lDataProvider.getDataCache().getA2lWpRespMap().get(a2lResp.getID()))) {
      this.a2lEditorDp.setA2lResp(a2lResp);
      SortedSet<A2LWpResponsibility> a2lWpRespSet = this.a2lDataProvider.getA2lWpResp(a2lResp.getID());
      this.a2lEditorDp.setA2lWpRespSet(a2lWpRespSet);
      setWpGrpRespMap(a2lWpRespSet);
      this.a2lEditorDp.setLabelMapForA2lRespWp();
    }
    else {
      this.a2lEditorDp.setA2lResp(a2lResp);
      this.a2lEditorDp.setA2lWpRespSet(this.a2lDataProvider.getDataCache().getA2lWpRespMap().get(a2lResp.getID()));
      setWpGrpRespMap(this.a2lEditorDp.getA2lWpRespSet());
    }

  }

  /**
   * @return the rootId
   */
  public Long getRootId() {
    return this.rootId;
  }


  /**
   * @return the typeID
   */
  public Long getTypeID() {
    return this.typeID;
  }

  /**
   * @param a2lWpRespSet a2lWpRespSet return the wp Grp Resp map
   */
  private void setWpGrpRespMap(final SortedSet<A2LWpResponsibility> a2lWpRespSet) {
    for (A2LWpResponsibility a2lWpResponsibility : a2lWpRespSet) {
      this.a2lEditorDp.getWpGrpRespMap().put(a2lWpResponsibility.getName(), a2lWpResponsibility.getResponbilityEnum());
    }
  }


  /**
   * @return true if the a2l Group Resp Params are created successfully or already exiting
   */
  public boolean createA2lRespGrpParams() {
    A2LResponsibility a2lResp = resolveWpResp();
    if ((a2lResp == null) &&
        CommonUtils.isEqual(this.a2lEditorDp.getMappingSourceID(),
            Long.valueOf(this.apicDataProvider.getParameterValue(ApicConstants.GROUP_MAPPING_ID))) &&
        CommonUtils.isNotEmpty(this.a2lEditorDp.getA2LGroupList())) {
      // ICDM-2602

      A2LRespGroupParamsInserter a2lRespGroupInserter =
          new A2LRespGroupParamsInserter(this.a2lFile.getPidcA2l().getA2LId(), getRootId(), this.a2lEditorDp,
              this.a2lFile.getPidcA2l(), getTypeID(), this, this.a2lDataProvider);

      // call web service
      if (!a2lRespGroupInserter.callWebServiceForCreation()) {
        return false;
      }

      // set in A2l editor Dp.
      a2lResp = this.a2lDataProvider.getA2lResp(this.a2lFile.getPidcA2l().getID(), getTypeID(), getRootId());
      setWPRespinEditorDp(a2lResp, this.a2lFile.getPidcA2l());
    }
    return true;
  }


}
