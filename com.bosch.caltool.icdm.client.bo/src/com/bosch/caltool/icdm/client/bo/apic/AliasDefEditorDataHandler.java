/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.apic.AliasDefServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AliasDetailServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class AliasDefEditorDataHandler extends AbstractClientDataHandler {

  private final AliasDef aliasDef;

  private final ConcurrentMap<Long, Attribute> allAttrMap = new ConcurrentHashMap<>();
  private final ConcurrentMap<Long, Map<Long, AttributeValue>> attrValueMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, AliasDetail> attrAliasMap = new ConcurrentHashMap<>();
  private final ConcurrentMap<Long, AliasDetail> valueAliasMap = new ConcurrentHashMap<>();

  /**
   * Constructor
   *
   * @param aliasDef Alias Definition
   */
  public AliasDefEditorDataHandler(final AliasDef aliasDef) {
    super();
    this.aliasDef = aliasDef;
    initializeData();
  }

  @Override
  protected void registerForCns() {

    registerCns(this::loadAttributes, MODEL_TYPE.ATTRIBUTE);

    // CNS action : Just clear the map. Data load will happen with the next selection
    registerCns(data -> this.attrValueMap.get(((AttributeValue) CnsUtils.getModel(data)).getAttributeId()) != null,
        this::clearAttrValues, MODEL_TYPE.ATTRIB_VALUE);

    registerCns(this::fetchAliasDef, MODEL_TYPE.ALIAS_DEFINITION, this::getAliasDef);

    registerCns(
        data -> ((AliasDetail) CnsUtils.getModel(data)).getAdId().longValue() == getAliasDef().getId().longValue(),
        this::loadAliasDetail, MODEL_TYPE.ALIAS_DETAIL);

    registerCnsCheckerForNodeAccess(MODEL_TYPE.ALIAS_DEFINITION, this::getAliasDef);
  }

  private void clearAttrValues(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.attrValueMap.clear();
  }

  /**
   *
   */
  private void initializeData() {
    loadAttributes(null);
    loadAliasDetail(null);
  }

  /**
  *
  */
  private void loadAttributes(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    try {
      this.allAttrMap.putAll(new AttributeServiceClient().getAll(false));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  private void loadAliasDetail(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.attrAliasMap.clear();
    this.valueAliasMap.clear();

    try {

      new AliasDetailServiceClient().getByAdId(this.aliasDef.getId()).values().forEach(det -> {
        if (det.getAttrId() == null) {
          this.valueAliasMap.put(det.getValueId(), det);
        }
        else {
          this.attrAliasMap.put(det.getAttrId(), det);
        }
      });
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @return attributes
   */
  public SortedSet<Attribute> getAttributes() {
    return new TreeSet<>(this.allAttrMap.values());
  }

  /**
   * @param attrId attribute Id
   * @return attribute values
   */
  public SortedSet<AttributeValue> getAttributeValues(final Long attrId) {
    SortedSet<AttributeValue> retSet = new TreeSet<>();

    Map<Long, AttributeValue> valMap = this.attrValueMap.get(attrId);
    if (valMap == null) {
      valMap = new ConcurrentHashMap<>();
      try {
        valMap.putAll(new AttributeValueServiceClient().getValuesByAttribute(attrId).get(attrId));
        this.attrValueMap.put(attrId, valMap);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }

    }

    retSet.addAll(valMap.values());

    return retSet;
  }

  /**
   * @param attrId attribute Id
   * @return Alias Detail
   */
  public AliasDetail getAttrAlias(final Long attrId) {
    return this.attrAliasMap.get(attrId);
  }

  /**
   * @param attrId attribute Id
   * @return alias name
   */
  public String getAttrAliasName(final Long attrId) {
    AliasDetail det = getAttrAlias(attrId);
    return det == null ? "" : det.getAliasName();
  }

  /**
   * @param valueId attribute value Id
   * @return Alias Detail
   */
  public AliasDetail getValueAlias(final Long valueId) {
    return this.valueAliasMap.get(valueId);
  }

  /**
   * @param valueId attribute value Id
   * @return alias name
   */
  public String getValueAliasName(final Long valueId) {
    AliasDetail det = getValueAlias(valueId);
    return det == null ? "" : det.getAliasName();
  }


  /**
   * @return Alias Definition
   */
  public AliasDef getAliasDef() {
    return this.aliasDef;
  }

  /**
   * @return true if this alias definition is modifiable
   */
  public boolean isModifiable() {
    boolean ret = false;
    CurrentUserBO user = new CurrentUserBO();
    try {
      ret = user.hasApicWriteAccess() || user.hasNodeWriteAccess(this.aliasDef.getId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return ret;
  }

  /**
   *
   */
  private void fetchAliasDef(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    AliasDef def;
    try {
      def = new AliasDefServiceClient().get(this.aliasDef.getId());
      CommonUtils.shallowCopy(this.aliasDef, def);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }

}
