/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import java.util.Set;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.layer.event.CellVisualChangeEvent;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.PIDCAttrPage;
import com.bosch.caltool.apic.ui.editors.pages.RiskEvalNatTableSection;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.client.bo.apic.PidcRMCharacterMapping;
import com.bosch.caltool.icdm.client.bo.apic.PidcRiskResultHandler;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacterExt;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcRmProjCharClient;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

/**
 * @author gge6cob
 */
public class RiskEvalUpdateCmdHandler extends AbstractLayerCommandHandler<UpdateDataCommand> {

  private final RiskEvalNatTableSection natCtrl;
  private final PidcRiskResultHandler resultHandler;
  private final CustomFilterGridLayer<PidcRMCharacterMapping> gridLayer;
  private final PidcRmProjCharClient client;
  private final PidcRmProjCharClient matrixClient;

  /**
   * @param riskEvalNattableSection
   */
  public RiskEvalUpdateCmdHandler(final RiskEvalNatTableSection riskEvalNattableSection) {
    this.natCtrl = riskEvalNattableSection;
    this.resultHandler = this.natCtrl.getResultHandler();
    this.gridLayer = this.natCtrl.getCustomFilterGridLayer();

    this.client = new PidcRmProjCharClient();
    this.matrixClient = new PidcRmProjCharClient();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<UpdateDataCommand> getCommandClass() {
    return UpdateDataCommand.class;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean doCommand(final UpdateDataCommand command) {
    if (command == null) {
      return false;
    }
    int rowPosition = command.getRowPosition();
    int columnPosition = command.getColumnPosition();

    PidcRMCharacterMapping rowObject = this.gridLayer.getBodyDataProvider().getRowObject(rowPosition);
    // Used for calling webservice update - if error revert, else synch with rowobject
    PidcRMCharacterMapping clonedRowObject = rowObject.clone();

    if (columnPosition == RiskEvalNatTableSection.RB_SW_SHARE_COLNUM) {
      final IDataProvider dataProvider = this.gridLayer.getBodyDataProvider();
      Object newValue = command.getNewValue();

      dataProvider.setDataValue(columnPosition, rowPosition, newValue);
      this.gridLayer.fireLayerEvent(new CellVisualChangeEvent(this.gridLayer, columnPosition, rowPosition));

      clonedRowObject.setRbSoftwareShare((String) newValue);
    }
    if (columnPosition == RiskEvalNatTableSection.RELEVANT_YES_COLNUM) {
      clonedRowObject.setRelevantYes(true);
    }
    if (columnPosition == RiskEvalNatTableSection.RELEVANT_NO_COLNUM) {
      clonedRowObject.setRelevantNo(true);
    }
    if (columnPosition == RiskEvalNatTableSection.RELEVANT_NA_COLNUM) {
      clonedRowObject.setRelevantNA(true);
    }
    if (columnPosition == RiskEvalNatTableSection.RB_INIT_DATA_COLNUM) {
      Object newValue = command.getNewValue();
      clonedRowObject.setInputDataByRB((String) newValue);
    }
    // Webservice call to create/update row object
    boolean isUpdateSuccess = updatePidcProjectCharacterWS(clonedRowObject, rowObject);
    this.natCtrl.updateOverallProjStatus();
    updateRiskSummaryInPidcPage();
    if (!isUpdateSuccess) {
      // Refresh nattable in case of errors
      this.natCtrl.getProjCharMappingWS(rowObject.getRiskDefId(), true);
      CDMLogger.getInstance().info("Editor is now refreshed to get latest list.", Activator.PLUGIN_ID);
    }
    return true;
  }

  /**
   *
   */
  private void updateRiskSummaryInPidcPage() {
    PIDCAttrPage pidcPage = (PIDCAttrPage) this.natCtrl.getRiskEvalPage().getEditor()
        .findPage(Messages.getString(IMessageConstants.PIDC_LABEL));
    if (null != pidcPage) {
      pidcPage.updateOverAllRiskDetails();
    }
  }

  /**
   * Update pidc project character. Based on the success of webservice call, the row object will updated or reset with
   * old data.
   *
   * @param rowObject the row object without user input changes
   * @param clonedRowObject the row object with user input changes
   */
  private boolean updatePidcProjectCharacterWS(final PidcRMCharacterMapping clonedRowObject,
      final PidcRMCharacterMapping rowObjectForUpdate) {
    PidcRmProjCharacter newObject = null;
    try {
      if (clonedRowObject.getPidcProjCharId() == 0l) {
        newObject = this.client.create(createWSObject(clonedRowObject));
      }
      else {
        newObject = this.client.update(createWSObject(clonedRowObject));
      }
    }
    catch (Exception ex) {
      CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
      return false;
    }

    try {
      /**
       * Create - Row Object to be updated iff above create call is successful. Update - Row Object to be updated even
       * if above fails to retain database state
       */
      if (newObject != null) {
        Set<PidcRmProjCharacterExt> matrixOutput =
            this.matrixClient.getPidcRmProjcharExt(clonedRowObject.getRiskDefId());
        updateRowObject(rowObjectForUpdate, newObject, matrixOutput);
      }
    }
    catch (Exception ex) {
      CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
      return false;
    }
    return true;
  }

  /**
   * Creates the WS object.
   *
   * @param rowObject the row object
   * @return the pidc rm proj character
   */
  private PidcRmProjCharacter createWSObject(final PidcRMCharacterMapping rowObject) {
    PidcRmProjCharacter projChar = new PidcRmProjCharacter();
    projChar.setId(rowObject.getPidcProjCharId());
    projChar.setProjCharId(rowObject.getProjCharId());
    projChar.setRmDefId(rowObject.getRiskDefId());
    projChar.setRelevant(rowObject.getRelevantFlag());
    projChar.setRbDataId(rowObject.getRbInputDataId());
    projChar.setRbShareId(rowObject.getRbShareId());
    projChar.setVersion(rowObject.getVersion());
    return projChar;
  }

  /**
   * Update row object.
   *
   * @param rowObject the row object
   * @param projChar the proj char
   */
  private void updateRowObject(final PidcRMCharacterMapping rowObject, final PidcRmProjCharacter updatedData,
      final Set<PidcRmProjCharacterExt> matrixOutput) {
    rowObject.setPidcProjCharId(updatedData.getId());
    rowObject.setProjCharId(updatedData.getProjCharId());
    rowObject.setRiskDefId(updatedData.getRmDefId());
    rowObject.setRelevantFlags(updatedData.getRelevant());
    rowObject.setRbInputDataId(updatedData.getRbDataId());
    rowObject.setRbShareId(updatedData.getRbShareId());
    rowObject.setVersion(updatedData.getVersion());
    for (PidcRmProjCharacterExt ouput : matrixOutput) {
      if (ouput.getPidRmChar().getProjCharId() == rowObject.getProjCharId()) {
        this.resultHandler.getRiskMatrix(rowObject, ouput);
        break;
      }
    }
  }
}
