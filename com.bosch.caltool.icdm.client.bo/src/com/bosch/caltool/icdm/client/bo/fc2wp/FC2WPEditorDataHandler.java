/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.fc2wp;

import java.util.Map;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPMappingServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 */
public class FC2WPEditorDataHandler extends AbstractClientDataHandler {

  private final FC2WPDefBO fc2wpDefBo;

  /**
   * @param fc2wpDefbo FC2WPDefBO
   */
  public FC2WPEditorDataHandler(final FC2WPDefBO fc2wpDefbo) {
    this.fc2wpDefBo = fc2wpDefbo;
  }

  @Override
  protected void registerForCns() {
    registerCnsChecker(MODEL_TYPE.FC2WP_MAPPING, chData -> {
      Long fc2wpVerId = ((FC2WPMapping) CnsUtils.getModel(chData)).getFcwpVerId();
      // To distinguish between compare editor and normal editor,in case of normal editor check for the version id
      if (this.fc2wpDefBo.getFC2WPMappingWithDetailsList().size() == 1) {
        return fc2wpVerId.equals(this.fc2wpDefBo.getFc2wpVersion().getId());
      }
      return true;
    });
    registerCnsChecker(MODEL_TYPE.FC2WP_DEF_VERS, chData -> {
      Long fc2wpDefId = ((FC2WPVersion) CnsUtils.getModel(chData)).getFcwpDefId();
      return fc2wpDefId.equals(this.fc2wpDefBo.getFc2wpVersion().getFcwpDefId());
    });
    registerNodeAccessChange();
    registerCnsChecker(MODEL_TYPE.FC2WP_RELV_PT_TYPE, chData -> {
      Long fc2wpDefId = ((FC2WPRelvPTType) CnsUtils.getModel(chData)).getFcwpDefId();
      return fc2wpDefId.equals(this.fc2wpDefBo.getFc2wpVersion().getFcwpDefId());
    });
    registerCnsAction(this::refreshFC2WPDefVersion, MODEL_TYPE.FC2WP_DEF_VERS);
    registerCnsAction(this::refreshFC2WPMapping, MODEL_TYPE.FC2WP_MAPPING);
    registerCnsAction(this::refreshFC2WPRelvPTType, MODEL_TYPE.FC2WP_RELV_PT_TYPE);
    registerCnsActionLocal(this::refreshFC2WPMappingLocal, MODEL_TYPE.FC2WP_MAPPING);

  }

  /**
  *
  */
  private void registerNodeAccessChange() {
    registerCnsChecker(MODEL_TYPE.NODE_ACCESS, chData -> {
      Long nodeId = ((NodeAccess) CnsUtils.getModel(chData)).getNodeId();
      return nodeId.equals(this.fc2wpDefBo.getFc2wpVersion().getFcwpDefId());
    });
  }

  private void refreshFC2WPMappingLocal(final ChangeData<?> chData) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
        chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      FC2WPMapping fc2wpMapping = (FC2WPMapping) chData.getNewData();
      Long objId = chData.getObjId();
      Long fc2wpVerId = fc2wpMapping.getFcwpVerId();
      FC2WPMappingServiceClient servClient = new FC2WPMappingServiceClient();
      try {
        FC2WPMappingWithDetails editedfc2wpMappingWithDetails = servClient.getFC2WPMappingById(objId);
        if ((this.fc2wpDefBo.getFC2WPMappingWithDetailsList().size() == 1) ||
            this.fc2wpDefBo.getFc2wpVersion().getId().equals(fc2wpVerId)) {
          this.fc2wpDefBo.getFc2wpMappingWithDetails().getUserMap().putAll(editedfc2wpMappingWithDetails.getUserMap());
          this.fc2wpDefBo.getFc2wpMappingWithDetails().getWpDetMap()
              .putAll(editedfc2wpMappingWithDetails.getWpDetMap());
          this.fc2wpDefBo.getFc2wpMappingWithDetails().getPtTypeMap()
              .putAll(editedfc2wpMappingWithDetails.getPtTypeMap());
          this.fc2wpDefBo.getFc2wpMappingWithDetails().getFc2wpMappingMap()
              .putAll(editedfc2wpMappingWithDetails.getFc2wpMappingMap());
          this.fc2wpDefBo.getFc2wpMappingWithDetails().getBcMap().putAll(editedfc2wpMappingWithDetails.getBcMap());
        }
        // refresh for compare editor
        else {
          this.fc2wpDefBo.getFc2wpVerMappingForCompEditor().put(fc2wpVerId, editedfc2wpMappingWithDetails);
        }
      }
      catch (ApicWebServiceException ex) {
        CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * @param chDataInfoMap Map<Long, ChangeDataInfo>
   */
  public void refreshFC2WPDefVersion(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {
          FC2WPVersionServiceClient servClient = new FC2WPVersionServiceClient();
          this.fc2wpDefBo.getAllVersions().clear();
          this.fc2wpDefBo.getAllVersions().addAll(servClient.getVersionsByDefID(this.fc2wpDefBo.getFC2WPDefId()));
        }
        catch (ApicWebServiceException ex) {
          CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
        }
      }
    }
  }

  /**
   * @param chDataInfoMap Map<Long, ChangeDataInfo>
   */
  public void refreshFC2WPMapping(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {
          FC2WPMappingServiceClient servClient = new FC2WPMappingServiceClient();
          Long objId = data.getObjId();
          // The service returns FC2WPMappingDetails object for given FC2WPMapping(created/edited in details page)
          FC2WPMappingWithDetails editedfc2wpMappingWithDetails = servClient.getFC2WPMappingById(objId);
          Object[] obj = editedfc2wpMappingWithDetails.getFc2wpMappingMap().values().toArray();
          Long fc2wpVerId = ((FC2WPMapping) obj[0]).getFcwpVerId();
          // refresh FC2WP Details for particular version
          if ((this.fc2wpDefBo.getFC2WPMappingWithDetailsList().size() == 1) ||
              this.fc2wpDefBo.getFc2wpVersion().getId().equals(fc2wpVerId)) {
            this.fc2wpDefBo.getFc2wpMappingWithDetails().getUserMap()
                .putAll(editedfc2wpMappingWithDetails.getUserMap());
            this.fc2wpDefBo.getFc2wpMappingWithDetails().getWpDetMap()
                .putAll(editedfc2wpMappingWithDetails.getWpDetMap());
            this.fc2wpDefBo.getFc2wpMappingWithDetails().getPtTypeMap()
                .putAll(editedfc2wpMappingWithDetails.getPtTypeMap());
            this.fc2wpDefBo.getFc2wpMappingWithDetails().getFc2wpMappingMap()
                .putAll(editedfc2wpMappingWithDetails.getFc2wpMappingMap());
            this.fc2wpDefBo.getFc2wpMappingWithDetails().getBcMap().putAll(editedfc2wpMappingWithDetails.getBcMap());
          }
          // refresh for compare editor
          else {
            this.fc2wpDefBo.getFc2wpVerMappingForCompEditor().put(fc2wpVerId, editedfc2wpMappingWithDetails);
          }
        }

        catch (ApicWebServiceException ex) {
          CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
        }
      }

    }
  }

  private void refreshFC2WPRelvPTType(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.CREATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        this.fc2wpDefBo.loadRelevantPTTypeData(this.fc2wpDefBo.getFC2WPDefId());
      }
    }
  }
}
