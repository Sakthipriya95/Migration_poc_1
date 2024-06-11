/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import java.util.Map;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.icdm.client.bo.apic.AttrRootNode;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author dmo5cob
 */
public class OutLineViewDataHandler extends AbstractClientDataHandler {

  // instance of attr root node
  private final AttrRootNode attrRootNode;
  // instance of data handler
  private final UseCaseDataHandler ucDataHandler;
  // instance of usecase root node
  private final UseCaseRootNode useCaseRootNode;
  // instance of fav usecase root node
  private final UserFavUcRootNode userFavRootNode;
  // instance of proj fav uc root node
  private final ProjFavUcRootNode projFavUcRootNode;


  /**
   * @param pidcVersion PIDC Version
   */
  public OutLineViewDataHandler(final PidcVersion pidcVersion) {
    super();
    this.attrRootNode = new AttrRootNode(null);
    this.ucDataHandler = new UseCaseDataHandler(pidcVersion);
    this.useCaseRootNode = new UseCaseRootNode(this.ucDataHandler);
    this.userFavRootNode = new UserFavUcRootNode();
    this.projFavUcRootNode = new ProjFavUcRootNode();
    
  }
  
 
  /**
   * @param pidcVersion  pidc Version
   * @param attrGroupModel  attrGroupModel
   */
  public OutLineViewDataHandler(final PidcVersion pidcVersion, AttrGroupModel attrGroupModel) {
    super();
    this.attrRootNode = new AttrRootNode(attrGroupModel);
    this.ucDataHandler = new UseCaseDataHandler(pidcVersion);
    this.useCaseRootNode = new UseCaseRootNode(this.ucDataHandler);
    this.userFavRootNode = new UserFavUcRootNode();
    this.projFavUcRootNode = new ProjFavUcRootNode();
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    // register refresh for use case node types
    registerCnsChecker(MODEL_TYPE.SUPER_GROUP, MODEL_TYPE.GROUP, MODEL_TYPE.USE_CASE_GROUP, MODEL_TYPE.USE_CASE,
        MODEL_TYPE.USE_CASE_SECT, MODEL_TYPE.UC_FAV, MODEL_TYPE.UCP_ATTR);

    registerCnsAction(this::refreshAttrRootNode, MODEL_TYPE.SUPER_GROUP, MODEL_TYPE.GROUP);
    registerCnsAction(this::refreshUseCaseRootNode, MODEL_TYPE.USE_CASE_GROUP, MODEL_TYPE.USE_CASE,
        MODEL_TYPE.USE_CASE_SECT, MODEL_TYPE.UCP_ATTR);
    registerCnsAction(this::refreshUCDataHandler, MODEL_TYPE.UC_FAV);

  }

  private void refreshAttrRootNode(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.attrRootNode.refresh();
  }

  private void refreshUseCaseRootNode(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.useCaseRootNode.refresh();
    // in case of delete , colors should be changed in fav nodes
    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      if (changeData.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
        this.ucDataHandler.refreshProjectFavNodes();
        this.ucDataHandler.refreshUserFavNodes();
      }
    }
  }

  private void refreshUCDataHandler(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.ucDataHandler.refresh(chDataInfoMap);
  }

  /**
   * @return the attrRootNode
   */
  public AttrRootNode getAttrRootNode() {
    return this.attrRootNode;
  }


  /**
   * @return the ucDataHandler
   */
  public UseCaseDataHandler getUcDataHandler() {
    return this.ucDataHandler;
  }


  /**
   * @return the useCaseRootNode
   */
  public UseCaseRootNode getUseCaseRootNode() {
    return this.useCaseRootNode;
  }


  /**
   * @return the userFavRootNode
   */
  public UserFavUcRootNode getUserFavRootNode() {
    return this.userFavRootNode;
  }


  /**
   * @return the projFavUcRootNode
   */
  public ProjFavUcRootNode getProjFavUcRootNode() {
    return this.projFavUcRootNode;
  }


}
