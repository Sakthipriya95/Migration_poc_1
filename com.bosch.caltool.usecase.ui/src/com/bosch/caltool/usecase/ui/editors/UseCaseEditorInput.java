package com.bosch.caltool.usecase.ui.editors;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.listeners.ILinkSelectionProvider;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UcpAttrServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;
import com.bosch.caltool.usecase.ui.Activator;

/**
 * @author adn1cob
 */
public class UseCaseEditorInput extends AbstractClientDataHandler implements IEditorInput, ILinkSelectionProvider {

  /**
   * Selected Use Case
   */
  private final UsecaseClientBO selectedUseCase;


  private final NodeAccessPageDataHandler nodeAccessBO;

  private OutLineViewDataHandler outlineDataHandler;

  private final AttrGroupModel attrGroupModel;

  /**
   * @param selectedUseCase usecase
   * @param attrGroupModel AttrGroupModel
   */
  public UseCaseEditorInput(final UsecaseClientBO selectedUseCase, final AttrGroupModel attrGroupModel) {
    this.selectedUseCase = selectedUseCase;
    this.attrGroupModel = attrGroupModel;
    this.nodeAccessBO = new NodeAccessPageDataHandler(getSelectedUseCase().getUseCase());
    this.nodeAccessBO.setNodeDeleted(getSelectedUseCase().getUseCase().isDeleted());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCns(chData -> {
      Long useCaseId = ((UseCase) CnsUtils.getModel(chData)).getId();
      return CommonUtils.isEqual(this.selectedUseCase.getId(), useCaseId);
    }, this::refreshUseCase, MODEL_TYPE.USE_CASE);
    registerCns(chData -> {
      Long useCaseId = ((UseCaseSection) CnsUtils.getModel(chData)).getUseCaseId();
      return CommonUtils.isEqual(this.selectedUseCase.getId(), useCaseId);
    }, this::refreshEditorModel, MODEL_TYPE.USE_CASE_SECT);
    registerCns(chData -> {
      Long useCaseId = ((UcpAttr) CnsUtils.getModel(chData)).getUseCaseId();
      return CommonUtils.isEqual(this.selectedUseCase.getId(), useCaseId);
    }, this::refreshUCPAInModel, MODEL_TYPE.UCP_ATTR);
    registerCnsCheckerForNodeAccess(MODEL_TYPE.USE_CASE, this::getUseCase);

    registerCns(this::refreshEditorModel, MODEL_TYPE.ATTRIBUTE, MODEL_TYPE.ATTRIB_VALUE);
  }

  /**
   * @return UseCase
   */
  private UseCase getUseCase() {
    return this.selectedUseCase.getUseCase();
  }

  /**
   * load use case again
   */
  private void refreshUCPAInModel(final Map<Long, ChangeDataInfo> chDataInfoMap) {

    for (Entry<Long, ChangeDataInfo> chData : chDataInfoMap.entrySet()) {
      ChangeDataInfo chDataInfo = chData.getValue();
      if (chDataInfo.getChangeType() == CHANGE_OPERATION.CREATE) {
        try {
          UcpAttr ucpAttr = new UcpAttrServiceClient().getById(chDataInfo.getObjId());
          this.selectedUseCase.getUsecaseEditorModel().getUcpAttr().put(ucpAttr.getId(), ucpAttr);
          Map<Long, Long> attrMap =
              this.selectedUseCase.getUsecaseEditorModel().getAttrToUcpAttrMap().get(ucpAttr.getAttrId());
          if (null == attrMap) {
            attrMap = new HashMap<Long, Long>();
            this.selectedUseCase.getUsecaseEditorModel().getAttrToUcpAttrMap().put(ucpAttr.getAttrId(), attrMap);
          }
          attrMap.put(ucpAttr.getSectionId() == null ? ucpAttr.getUseCaseId() : ucpAttr.getSectionId(),
              ucpAttr.getId());
          this.selectedUseCase.resetAttrsInCommonBo();
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.usecase.ui.Activator.PLUGIN_ID);
        }
      }
      else if (chDataInfo.getChangeType() == CHANGE_OPERATION.DELETE) {
        UcpAttr deletedUcpAttr = (UcpAttr) chDataInfo.getRemovedData();
        this.selectedUseCase.getUsecaseEditorModel().getUcpAttr().remove(deletedUcpAttr.getId());
        Map<Long, Long> attrToUcpaMap =
            this.selectedUseCase.getUsecaseEditorModel().getAttrToUcpAttrMap().get(deletedUcpAttr.getAttrId());
        this.selectedUseCase.resetAttrsInCommonBo();
        if (null != attrToUcpaMap) {
          attrToUcpaMap.remove(
              deletedUcpAttr.getSectionId() == null ? deletedUcpAttr.getUseCaseId() : deletedUcpAttr.getSectionId());
        }
      }
      this.selectedUseCase.clearChildMap();
    }
  }

  /**
   * load use case again
   */
  private void refreshEditorModel(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    UseCaseServiceClient ucServiceClient = new UseCaseServiceClient();
    try {
      UsecaseEditorModel editorModel = ucServiceClient.getUseCaseEditorData(this.selectedUseCase.getId());
      this.selectedUseCase.setUsecaseEditorModel(editorModel);
      this.selectedUseCase.clearChildMap();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * load use case again
   */
  private void refreshUseCase(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    UseCaseServiceClient ucServiceClient = new UseCaseServiceClient();
    try {
      UseCase newUsecase = ucServiceClient.getById(this.selectedUseCase.getId());
      this.selectedUseCase.setUseCase(newUsecase);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  @Override
  public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter) {
    return null;
  }

  @Override
  public boolean exists() {
    return true;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return ImageManager.getImageDescriptor(ImageKeys.UC_28X30);
  }

  @Override
  public String getName() {
    return this.selectedUseCase.getName();
  }

  @Override
  public IPersistableElement getPersistable() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return this.selectedUseCase.getName();
  }


  /**
   * @return UseCase
   */
  public UsecaseClientBO getSelectedUseCase() {
    return this.selectedUseCase;
  }

  @Override
  public int hashCode() {
    if (null != this.selectedUseCase) {
      return this.selectedUseCase.getUseCase().getId().intValue();
    }
    return 0;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final UseCaseEditorInput other = (UseCaseEditorInput) obj;
    return this.selectedUseCase.getUseCase().getId().longValue() == other.selectedUseCase.getUseCase().getId()
        .longValue();
  }

  /**
   * @return UsecaseEditorModel
   */
  public UsecaseEditorModel getUseCaseEditorModel() {
    return this.selectedUseCase.getUsecaseEditorModel();
  }

  /**
   * @return SortedSet<Attribute>
   */
  public SortedSet<AttributeClientBO> getSortedAttributes() {
    TreeSet<AttributeClientBO> sortedAttr = new TreeSet<>();
    for (Attribute attribute : this.selectedUseCase.getUsecaseEditorModel().getAttrMap().values()) {
      sortedAttr.add(new AttributeClientBO(attribute));
    }
    return sortedAttr;
  }

  /**
   * @return nodeAccessBO
   */
  public NodeAccessPageDataHandler getNodeAccessBO() {
    return this.nodeAccessBO;
  }

  /**
   * @param isUsecaseDeleted
   */
  public void isDeleted(final boolean isUsecaseDeleted) {
    this.nodeAccessBO.setNodeDeleted(isUsecaseDeleted);
  }


  /**
   * @return the outlineDataHandler
   */
  public OutLineViewDataHandler getOutlineDataHandler() {
    return this.outlineDataHandler;
  }


  /**
   * @param outlineDataHandler the outlineDataHandler to set
   */
  public void setOutlineDataHandler(final OutLineViewDataHandler outlineDataHandler) {
    this.outlineDataHandler = outlineDataHandler;
  }


  /**
   * @return the attrGroupModel
   */
  public AttrGroupModel getAttrGroupModel() {
    return this.attrGroupModel;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Object getEditorInputSelection() {
    return this.selectedUseCase;
  }

}
