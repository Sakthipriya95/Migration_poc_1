package com.bosch.caltool.icdm.bo.uc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvUcpAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixMappingData;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;


/**
 * Command class for Usecase Section
 *
 * @author MKL2COB
 */
public class UseCaseSectionCommand extends AbstractCommand<UseCaseSection, UseCaseSectionLoader> {


  /**
   *
   */
  private static final String DEFAULT_SUB_SECTION = "default sub-section";

  private static final String DEFAULT_SECTION = "default section";

  private final Set<Long> ucSectionIdSet = new HashSet<>();

  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public UseCaseSectionCommand(final ServiceData serviceData, final UseCaseSection input, final boolean isUpdate)
      throws IcdmException {
    super(serviceData, input, new UseCaseSectionLoader(serviceData),
        (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvUseCaseSection entity = new TabvUseCaseSection();

    UseCaseLoader ucLoader = new UseCaseLoader(getServiceData());
    UseCaseSectionLoader sectionLoader = new UseCaseSectionLoader(getServiceData());
    TabvUseCase tabvUseCase = ucLoader.getEntityObject(getInputData().getUseCaseId());

    entity.setTabvUseCas(tabvUseCase);
    entity.setNameEng(getInputData().getNameEng());
    entity.setNameGer(getInputData().getNameGer());
    entity.setDescEng(getInputData().getDescEng());
    entity.setDescGer(getInputData().getDescGer());

    if (null != getInputData().getParentSectionId()) {
      TabvUseCaseSection tabvUseCaseSection = sectionLoader.getEntityObject(getInputData().getParentSectionId());
      entity.setTabvUseCaseSection(tabvUseCaseSection);
    }
    entity.setDeletedFlag(getInputData().isDeleted() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    entity.setFocusMatrixRelevant(getInputData().getFocusMatrixYn() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);

    if (null == getInputData().getParentSectionId()) {
      // parent=use case
      // for first section under a use case, if no child is present create default ucs
      if (tabvUseCase.getTabvUseCaseSections().isEmpty()) {
        TabvUseCaseSection defaultUcsEntity = createDefaultSubSection(tabvUseCase, null);
        TabvUseCaseSection ucsEntity = (null != defaultUcsEntity) ? defaultUcsEntity : entity;
        mapAttributesInDB(ucsEntity, null, tabvUseCase);
        mapFocusMatrixDetails(ucsEntity, null, tabvUseCase);
        tabvUseCase.getTabvUseCaseSections().add(ucsEntity);
        ucsEntity.getTabvUcpAttrs().addAll(tabvUseCase.getTabvUcpAttrs());
        this.ucSectionIdSet.add(ucsEntity.getSectionId());
      }

      tabvUseCase.getTabvUseCaseSections().add(entity);
      this.ucSectionIdSet.add(entity.getSectionId());

    }
    else {
      // creation of sub section under a section
      TabvUseCaseSection tabvUseCaseSection = sectionLoader.getEntityObject(getInputData().getParentSectionId());
      // check if child is present under that use case section
      if (tabvUseCaseSection.getTabvUseCaseSections().isEmpty()) {
        TabvUseCaseSection defaultUcsEntity = createDefaultSubSection(tabvUseCase, tabvUseCaseSection);
        TabvUseCaseSection ucsEntity = (null != defaultUcsEntity) ? defaultUcsEntity : entity;
        mapAttributesInDB(ucsEntity, tabvUseCaseSection, null);
        mapFocusMatrixDetails(ucsEntity, tabvUseCaseSection, null);
        tabvUseCaseSection.getTabvUseCaseSections().add(ucsEntity);
        ucsEntity.getTabvUcpAttrs().addAll(tabvUseCaseSection.getTabvUcpAttrs());
        this.ucSectionIdSet.add(ucsEntity.getSectionId());
      }

      tabvUseCaseSection.getTabvUseCaseSections().add(entity);
      entity.setTabvUseCaseSection(tabvUseCaseSection);
      this.ucSectionIdSet.add(entity.getSectionId());

    }
  }


  /**
   * @param parentUC
   * @param parentUCSection
   */
  private TabvUseCaseSection createDefaultSubSection(final TabvUseCase parentUC,
      final TabvUseCaseSection parentUCSection) {
    // if parent use case/section has atleast one attribute mapping, only then default section/sub-section is created
    if (parentUC.getTabvUcpAttrs().isEmpty() ||
        ((null != parentUCSection) && parentUCSection.getTabvUcpAttrs().isEmpty())) {
      return null;
    }
    // create default section/sub-section only if use case/section has atleast one attribute mapping
    TabvUseCaseSection defaultUcsEntity = new TabvUseCaseSection();
    defaultUcsEntity.setTabvUseCas(parentUC);
    String nameDescEng = (null == parentUCSection) ? DEFAULT_SECTION : DEFAULT_SUB_SECTION;
    defaultUcsEntity.setNameEng(nameDescEng);
    defaultUcsEntity.setDescEng(nameDescEng);
    if (null != parentUCSection) {
      defaultUcsEntity.setTabvUseCaseSection(parentUCSection);
    }
    defaultUcsEntity
        .setDeletedFlag(null == parentUCSection ? parentUC.getDeletedFlag() : parentUCSection.getDeletedFlag());
    defaultUcsEntity.setFocusMatrixRelevant(
        null == parentUCSection ? parentUC.getFocusMatrixRelevant() : parentUCSection.getFocusMatrixRelevant());

    setUserDetails(COMMAND_MODE.CREATE, defaultUcsEntity);

    persistEntity(defaultUcsEntity);

    return defaultUcsEntity;
  }

  /**
   * Map the focus matrix defenitions from the Parent to the First Child in database
   *
   * @param dbUcs
   * @throws IcdmException
   * @throws DataException
   */
  private void mapFocusMatrixDetails(final TabvUseCaseSection dbUcs, final TabvUseCaseSection parentUcs,
      final TabvUseCase parentUc)
      throws DataException, IcdmException {

    if (canMapAttributes(parentUcs, parentUc)) {
      // if first child
      FocusMatrixMappingData fmData = new FocusMatrixMappingData();
      FocusMatrixLoader fmLoader = new FocusMatrixLoader(getServiceData());
      if (null == getInputData().getParentSectionId()) {
        fmData.setUseCaseId(null != parentUc ? parentUc.getUseCaseId() : null);
      }
      else {
        fmData.setUseCaseSectionId(null != parentUcs ? parentUcs.getSectionId() : null);
      }
      FocusMatrixCommand fmCommand;
      for (FocusMatrix fmEntry : fmLoader.getFocusMatrix(fmData)) {
        fmEntry.setSectionId(dbUcs.getSectionId());
        fmCommand = new FocusMatrixCommand(getServiceData(), fmEntry, true, false);
        executeChildCommand(fmCommand);
      }
    }

  }

  /**
   * Map the Attributes from the Parent to the First Child in database
   *
   * @param dbUcs
   * @throws IcdmException
   */
  private void mapAttributesInDB(final TabvUseCaseSection dbUcs, final TabvUseCaseSection parentUcs,
      final TabvUseCase parentUc)
      throws IcdmException {

    UcpAttrLoader ucpaLoader = new UcpAttrLoader(getServiceData());
    if (canMapAttributes(parentUcs, parentUc)) {
      // if first child
      if (null == getInputData().getParentSectionId()) {
        List<TabvUcpAttr> tabvUcpAttrs = (dbUcs.getTabvUseCas()).getTabvUcpAttrs();

        // setting section for the TabvUcpAttr
        for (TabvUcpAttr dbUcpAttr : tabvUcpAttrs) {
          UcpAttr ucpa = ucpaLoader.getDataObjectByID(dbUcpAttr.getUcpaId());
          ucpa.setSectionId(dbUcs.getSectionId());
          UcpAttrCommand ucpaCommand = new UcpAttrCommand(getServiceData(), ucpa, true, false);
          executeChildCommand(ucpaCommand);
        }
      }
      else {

        // setting section for the TabvUcpAttr
        for (TabvUcpAttr dbUcpAttr : dbUcs.getTabvUseCaseSection().getTabvUcpAttrs()) {

          UcpAttr ucpa = ucpaLoader.getDataObjectByID(dbUcpAttr.getUcpaId());
          ucpa.setSectionId(dbUcs.getSectionId());
          UcpAttrCommand ucpaCommand = new UcpAttrCommand(getServiceData(), ucpa, true, false);
          executeChildCommand(ucpaCommand);
        }

      }
    }

  }

  /**
   * @param parentUcs
   * @param parentUc
   * @return
   */
  private boolean canMapAttributes(final TabvUseCaseSection parentUcs, final TabvUseCase parentUc) {
    boolean canMapAttributes;
    if (null == parentUc) {
      canMapAttributes = (null == parentUcs.getTabvUseCaseSections()) || parentUcs.getTabvUseCaseSections().isEmpty();
    }
    else {
      canMapAttributes = (null == parentUc.getTabvUseCaseSections()) || parentUc.getTabvUseCaseSections().isEmpty();
    }
    return canMapAttributes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    UseCaseSectionLoader loader = new UseCaseSectionLoader(getServiceData());
    TabvUseCaseSection entity = loader.getEntityObject(getInputData().getId());

    UseCaseLoader ucLoader = new UseCaseLoader(getServiceData());
    UseCaseSectionLoader sectionLoader = new UseCaseSectionLoader(getServiceData());
    TabvUseCase tabvUseCase = ucLoader.getEntityObject(getInputData().getUseCaseId());
    entity.setTabvUseCas(tabvUseCase);
    entity.setNameEng(getInputData().getNameEng());
    entity.setNameGer(getInputData().getNameGer());
    entity.setDescEng(getInputData().getDescEng());
    entity.setDescGer(getInputData().getDescGer());

    TabvUseCaseSection tabvUseCaseSection = sectionLoader.getEntityObject(getInputData().getParentSectionId());
    entity.setTabvUseCaseSection(tabvUseCaseSection);
    entity.setDeletedFlag(getInputData().isDeleted() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    entity.setFocusMatrixRelevant(getInputData().getFocusMatrixYn() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    UseCaseSectionLoader loader = new UseCaseSectionLoader(getServiceData());
    TabvUseCaseSection entity = loader.getEntityObject(getInputData().getId());

    entity.setDeletedFlag(ApicConstants.CODE_YES);
    setUserDetails(COMMAND_MODE.DELETE, entity);
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

  /**
   * @return the ucSectionIdSet
   */
  public Set<Long> getUcSectionIdSet() {
    return this.ucSectionIdSet;
  }


}
