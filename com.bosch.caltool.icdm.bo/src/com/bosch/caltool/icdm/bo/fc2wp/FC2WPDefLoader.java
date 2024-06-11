/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.fc2wp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.PTTypeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefinition;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpPtTypeRelv;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.PTType;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;

/**
 * @author bne4cob
 */
public class FC2WPDefLoader extends AbstractBusinessObject<FC2WPDef, TFc2wpDefinition> {

  /**
   * @param inputData ServiceData
   */
  public FC2WPDefLoader(final ServiceData inputData) {
    super(inputData, MODEL_TYPE.FC2WP_DEF, TFc2wpDefinition.class);
  }

  /**
   * @return Set of FC2WPDef
   */
  public Set<FC2WPDef> getAllDefinitions() {
    Set<FC2WPDef> retSet = new HashSet<>();

    TypedQuery<TFc2wpDefinition> tQuery =
        getEntMgr().createNamedQuery(TFc2wpDefinition.NQ_FIND_ALL, TFc2wpDefinition.class);

    for (TFc2wpDefinition dbFcwpDef : tQuery.getResultList()) {
      retSet.add(createDataObject(dbFcwpDef));
    }

    return retSet;
  }


  @Override
  protected FC2WPDef createDataObject(final TFc2wpDefinition dbFcwpDef) {
    FC2WPDef def = new FC2WPDef();

    def.setId(dbFcwpDef.getFcwpDefId());
    def.setRelvForQnaire("Y".equals(dbFcwpDef.getRelvForQnaireFlag()));

    // Set name and description

    def.setName(dbFcwpDef.getFc2wpName());
    def.setDescription(getLangSpecTxt(dbFcwpDef.getFc2wpDescEng(), dbFcwpDef.getFc2wpDescGer()));

    def.setDescriptionEng(dbFcwpDef.getFc2wpDescEng());
    def.setDescriptionGer(dbFcwpDef.getFc2wpDescGer());

    // Set Division details
    TabvAttrValue dbValDiv = dbFcwpDef.getTabvAttrValueDiv();

    def.setDivisionName(getLangSpecTxt(dbValDiv.getTextvalueEng(), dbValDiv.getTextvalueGer()));

    def.setDivisionValId(dbValDiv.getValueId());
    def.setVersion(dbFcwpDef.getVersion());

    return def;
  }

  /**
   * Gets the FC2WP relevant PT-types.
   *
   * @param fc2wpDefID the fc2wp definition ID
   * @return the FC2WP relevant PT-types
   * @throws DataException any error while retrieving PT types
   */
  public Set<PTType> getFC2WPRelevantPTtypes(final Long fc2wpDefID) throws DataException {
    Set<PTType> relevantPtTypes = new HashSet<>();
    PTTypeLoader loader = new PTTypeLoader(getServiceData());

    TFc2wpDefinition dbfc2wpDef = getEntityObject(fc2wpDefID);
    if (dbfc2wpDef.getTFc2wpPtTypeRelvs() != null) {
      for (TFc2wpPtTypeRelv dbPtTypeRelObj : dbfc2wpDef.getTFc2wpPtTypeRelvs()) {
        relevantPtTypes.add(loader.getDataObjectByID(dbPtTypeRelObj.getTPowerTrainType().getPtTypeId()));
      }
    }
    return relevantPtTypes;
  }


  /**
   * Find FC2WP definition entity with the given name and division ID
   *
   * @param fc2wpName Name
   * @param qnaireConfigAttrValueId Division
   * @return TFc2wpDefinition entity
   * @throws IcdmException exception
   */
  protected TFc2wpDefinition getEntityObject(final String fc2wpName, final Long qnaireConfigAttrValueId)
      throws IcdmException {
    List<TFc2wpDefinition> dbDef;

    dbDef = getEntMgr().createNamedQuery(TFc2wpDefinition.NQ_GET_DEF_BY_VALUE_ID, TFc2wpDefinition.class)
        .setParameter("nameValueId", fc2wpName).setParameter("qnaireConfigValueId", qnaireConfigAttrValueId)
        .getResultList();

    // If the Assignment SW2CAL has a attribute value for which a valid FC2WP is not defined
    if ((null == dbDef) || dbDef.isEmpty()) {
      TabvAttrValue divVal = getEntMgr().createNamedQuery(TabvAttrValue.NQ_FIND_BY_VAL_ID, TabvAttrValue.class)
          .setParameter("valId", qnaireConfigAttrValueId).getResultList().get(0);

      throw new DataException(
          "WP cannot be evaluated since the '" + new AttributeLoader(getServiceData()).getQnaireConfigAttr().getName() +
              "' attribute value : " + divVal.getTextvalueEng() + " and the SW2CAL attribute value : " + fc2wpName +
              " are not associated with an FC2WP configuration");
    }

    return dbDef.get(0);
  }

}
