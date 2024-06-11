package com.bosch.caltool.icdm.bo.uc;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvUcpAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixMappingData;
import com.bosch.caltool.icdm.model.uc.UcpAttr;


/**
 * Command class for Ucp Attr
 *
 * @author MKL2COB
 */
public class UcpAttrCommand extends AbstractCommand<UcpAttr, UcpAttrLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public UcpAttrCommand(final ServiceData serviceData, final UcpAttr input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new UcpAttrLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : getCreateOrUpdateCommandMode(isUpdate));
  }


  /**
   * @param isUpdate boolean
   * @return COMMAND_MODE
   */
  private static com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE getCreateOrUpdateCommandMode(
      final boolean isUpdate) {
    return isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvUcpAttr entity = new TabvUcpAttr();

    UseCaseLoader ucLoader = new UseCaseLoader(getServiceData());
    UseCaseSectionLoader sectionLoader = new UseCaseSectionLoader(getServiceData());
    TabvUseCase tabvUseCase = ucLoader.getEntityObject(getInputData().getUseCaseId());
    entity.setTabvUseCas(tabvUseCase);

    // When the mapping is to an usecase
    if (null == tabvUseCase.getTabvUcpAttrs()) {
      // when the child usecase list is null
      List<TabvUcpAttr> childUCPAList = new ArrayList<TabvUcpAttr>();
      tabvUseCase.setTabvUcpAttrs(childUCPAList);
    }
    tabvUseCase.getTabvUcpAttrs().add(entity);

    if (null != getInputData().getSectionId()) {
      // when the mapping is to an usecase section
      TabvUseCaseSection tabvUseCaseSection = sectionLoader.getEntityObject(getInputData().getSectionId());
      entity.setTabvUseCaseSection(tabvUseCaseSection);
      if (null == tabvUseCaseSection.getTabvUcpAttrs()) {
        // when the child usecase list is null
        List<TabvUcpAttr> childUCPAList = new ArrayList<TabvUcpAttr>();
        tabvUseCaseSection.setTabvUcpAttrs(childUCPAList);
      }
      tabvUseCaseSection.getTabvUcpAttrs().add(entity);
    }
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    TabvAttribute tabvAttribute = attrLoader.getEntityObject(getInputData().getAttrId());
    entity.setTabvAttribute(tabvAttribute);

    entity.setMappingFlags(getInputData().getMappingFlags());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);

    // check for focus matrix entries
    FocusMatrixLoader fmLoader = new FocusMatrixLoader(getServiceData());
    FocusMatrixMappingData fmMappingData = new FocusMatrixMappingData();
    fmMappingData.setAttrId(entity.getTabvAttribute().getAttrId());
    fmMappingData.setUseCaseId(entity.getTabvUseCas().getUseCaseId());
    if (null != entity.getTabvUseCaseSection()) {
      fmMappingData.setUseCaseSectionId(entity.getTabvUseCaseSection().getSectionId());
    }
    if (fmLoader.isFMAvailableWhileMapping(fmMappingData)) {
      // when there are focus matrix entries
      for (FocusMatrix focusMatrix : fmLoader.getFocusMatrix(fmMappingData)) {
        focusMatrix.setUcpaId(entity.getUcpaId());
        FocusMatrixCommand fmCommand = new FocusMatrixCommand(getServiceData(), focusMatrix, true, false);
        executeChildCommand(fmCommand);
      }

    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    UcpAttrLoader loader = new UcpAttrLoader(getServiceData());
    TabvUcpAttr entity = loader.getEntityObject(getInputData().getId());

    UseCaseLoader ucLoader = new UseCaseLoader(getServiceData());
    UseCaseSectionLoader sectionLoader = new UseCaseSectionLoader(getServiceData());
    TabvUseCase tabvUseCase = ucLoader.getEntityObject(getInputData().getUseCaseId());
    entity.setTabvUseCas(tabvUseCase);
    List<TabvUcpAttr> tabvUcpAttrs;
    if (getInputData().getSectionId() != null) {
      TabvUseCaseSection tabvUseCaseSection = sectionLoader.getEntityObject(getInputData().getSectionId());
      entity.setTabvUseCaseSection(tabvUseCaseSection);
      tabvUcpAttrs = tabvUseCaseSection.getTabvUcpAttrs();
      if (null == tabvUcpAttrs) {
        tabvUcpAttrs = new ArrayList<>();
        tabvUseCaseSection.setTabvUcpAttrs(tabvUcpAttrs);
      }
      tabvUcpAttrs.add(entity);
    }

    else {
      tabvUcpAttrs = tabvUseCase.getTabvUcpAttrs();
      if (null == tabvUcpAttrs) {
        tabvUcpAttrs = new ArrayList<>();
        tabvUseCase.setTabvUcpAttrs(tabvUcpAttrs);
      }
      tabvUcpAttrs.add(entity);
    }

    entity.setMappingFlags(getInputData().getMappingFlags());

    // if there was a different section id in the old data
    if (getOldData().getSectionId() != getInputData().getSectionId()) {
      // remove from its list
      TabvUseCaseSection oldTabvSection = sectionLoader.getEntityObject(getInputData().getSectionId());
      oldTabvSection.getTabvUcpAttrs().remove(entity);
    }

    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    TabvAttribute tabvAttribute = attrLoader.getEntityObject(getInputData().getAttrId());
    entity.setTabvAttribute(tabvAttribute);


    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    UcpAttrLoader loader = new UcpAttrLoader(getServiceData());
    TabvUcpAttr entity = loader.getEntityObject(getInputData().getId());

    FocusMatrixLoader fmLoader = new FocusMatrixLoader(getServiceData());
    FocusMatrixMappingData fmMappingData = new FocusMatrixMappingData();
    fmMappingData.setAttrId(entity.getTabvAttribute().getAttrId());
    fmMappingData.setUseCaseId(entity.getTabvUseCas().getUseCaseId());
    if (entity.getTabvUseCaseSection() != null) {
      fmMappingData.setUseCaseSectionId(entity.getTabvUseCaseSection().getSectionId());
    }
    if (fmLoader.isFocusMatrixAvailableWhileUnMapping(fmMappingData)) {
      // when there are focus matrix entries
      for (FocusMatrix focusMatrix : fmLoader.getFocusMatrix(fmMappingData)) {
        focusMatrix.setUcpaId(null);
        FocusMatrixCommand fmCommand = new FocusMatrixCommand(getServiceData(), focusMatrix, true, false);
        executeChildCommand(fmCommand);
      }

    }
    entity.getTabvUseCas().getTabvUcpAttrs().remove(entity);

    if (entity.getTabvUseCaseSection() != null) {
      entity.getTabvUseCaseSection().getTabvUcpAttrs().remove(entity);
    }

    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub
  }

}
