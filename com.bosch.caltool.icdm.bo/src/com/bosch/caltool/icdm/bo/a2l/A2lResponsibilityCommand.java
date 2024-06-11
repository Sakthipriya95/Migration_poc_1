package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.general.UserDetailsLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.bo.user.UserDetails;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.bo.a2l.A2lObjectIdentifierValidator;
import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwWpResp;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDelvryWpResp;
import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;
import com.bosch.caltool.icdm.model.a2l.A2lRespMaintenanceData;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.WpRespType;


/**
 * Command class for A2lResponsibility
 *
 * @author pdh2cob
 */
public class A2lResponsibilityCommand extends AbstractCommand<A2lResponsibility, A2lResponsibilityLoader> {

  private A2lRespMaintenanceData a2lRespUpdationData;
  private List<A2lRespBoschGroupUserCommand> a2lBoschGroupUserCreateCmndList;


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public A2lResponsibilityCommand(final ServiceData serviceData, final A2lResponsibility input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new A2lResponsibilityLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @param a2lResp input to update/create
   * @throws IcdmException error when initializing
   */
  public A2lResponsibilityCommand(final ServiceData serviceData, final A2lRespMaintenanceData input)
      throws IcdmException {
    super(serviceData, getInput(input), new A2lResponsibilityLoader(serviceData), getCommandMode(input));
    this.a2lRespUpdationData = input;
  }

  private static A2lResponsibility getInput(final A2lRespMaintenanceData input) {
    if (input.getA2lRespToCreate() != null) {
      return input.getA2lRespToCreate();
    }
    return input.getA2lRespToUpdate();
  }

  private static COMMAND_MODE getCommandMode(final A2lRespMaintenanceData input) {
    if (input.getA2lRespToCreate() != null) {
      return COMMAND_MODE.CREATE;
    }
    return COMMAND_MODE.UPDATE;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TA2lResponsibility entity = new TA2lResponsibility();

    setValuesToEntity(entity);
    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);

    if (this.a2lRespUpdationData != null) {
      createAndDeleteBoschGrpUsers();
    }
  }

  /**
   * @param a2lRespUpdationData
   * @throws IcdmException
   * @throws UnAuthorizedAccessException
   * @throws DataException
   */
  private void createAndDeleteBoschGrpUsers() throws IcdmException {
    if (CommonUtils.isNotEmpty(this.a2lRespUpdationData.getBoschUsrsCreationList())) {
      this.a2lBoschGroupUserCreateCmndList = new ArrayList<>();
      for (A2lRespBoschGroupUser a2lRespBoschGroupUser : this.a2lRespUpdationData.getBoschUsrsCreationList()) {
        a2lRespBoschGroupUser.setA2lRespId(getNewData().getId());
        A2lRespBoschGroupUserCommand createCmd =
            new A2lRespBoschGroupUserCommand(getServiceData(), a2lRespBoschGroupUser, false);
        executeChildCommand(createCmd);
        this.a2lBoschGroupUserCreateCmndList.add(createCmd);
      }
    }

    if (CommonUtils.isNotEmpty(this.a2lRespUpdationData.getBoschUsrsDeletionList())) {
      for (A2lRespBoschGroupUser a2lRespBoschGroupUser : this.a2lRespUpdationData.getBoschUsrsDeletionList()) {
        A2lRespBoschGroupUserCommand deleteCmd =
            new A2lRespBoschGroupUserCommand(getServiceData(), a2lRespBoschGroupUser, true);
        executeChildCommand(deleteCmd);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    if (!isEditNotApplicable(getOldData())) {
      A2lResponsibilityLoader loader = new A2lResponsibilityLoader(getServiceData());
      TA2lResponsibility entity = loader.getEntityObject(getInputData().getId());
      setValuesToEntity(entity);
      updateReferencingEntities(entity);
      setUserDetails(COMMAND_MODE.UPDATE, entity);
      if (this.a2lRespUpdationData != null) {
        createAndDeleteBoschGrpUsers();
      }
    }
  }

  private void setValuesToEntity(final TA2lResponsibility entity) throws DataException {
    TabvProjectidcard pidcEntity = new PidcLoader(getServiceData()).getEntityObject(getInputData().getProjectId());
    entity.setTabvProjectidcard(pidcEntity);

    entity.setLLastName(getInputData().getLLastName());
    entity.setLFirstName(getInputData().getLFirstName());
    entity.setLDepartment(getInputData().getLDepartment());

    entity.setDeletedFlag(booleanToYorN(getInputData().isDeleted()));
    if (getInputData().getUserId() == null) {
      if (WpRespType.getType(getInputData().getRespType()) == WpRespType.RB) {
        // if the resp type is RB and there is no user id , then null the first and last names
        entity.setLLastName(null);
        entity.setLFirstName(null);
      }
    }
    else {
      TabvApicUser userEntity = new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId());
      entity.setTabvApicUser(userEntity);
    }

    entity.setAliasName(getInputData().getAliasName());
    if (entity.getAliasName() == null) {
      resolveAliasName(getInputData(), entity);
    }
    entity.setRespType(getInputData().getRespType());
    pidcEntity.getTA2lResponsibilityList().add(entity);

  }

  private boolean isAliasNamePresent(final String aliasName) throws DataException {
    A2lResponsibilityModel a2lResponsibilityModel =
        new A2lResponsibilityLoader(getServiceData()).getByPidc(getInputData().getProjectId());
    for (A2lResponsibility a2lResponsibility : a2lResponsibilityModel.getA2lResponsibilityMap().values()) {
      if (a2lResponsibility.getAliasName().equals(aliasName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param object
   * @param entity
   * @throws DataException
   */
  private void resolveAliasName(final A2lResponsibility object, final TA2lResponsibility entity) throws DataException {

    String aliasNameResolved;
    if (object.getUserId() == null) {
      StringBuilder aliasName = new StringBuilder();
      aliasName.append(WpRespType.getType(object.getRespType()).getAliasBase());
      if (CommonUtils.isNotEmptyString(object.getLLastName())) {
        String lastName = object.getLLastName().replace(' ', '_');
        aliasName.append("_").append(lastName);
      }
      if (CommonUtils.isNotEmptyString(object.getLFirstName())) {
        String firstName = object.getLFirstName().replace(' ', '_');
        aliasName.append("_").append(firstName);
      }
      if (CommonUtils.isNotEmptyString(object.getLDepartment())) {
        String departmentName = object.getLDepartment().replace(' ', '_');
        aliasName.append("_").append(departmentName);
      }
      aliasNameResolved = aliasName.toString();
    }
    else {
      UserDetails userDetails =
          new UserDetailsLoader(getServiceData()).getDataObjectByID(entity.getTabvApicUser().getUserId());
      if (userDetails.getEmail() == null) {
        throw new InvalidInputException("The selected user is deleted. Please select a different user!");
      }
      aliasNameResolved = userDetails.getEmail();
    }
    if (isAliasNamePresent(aliasNameResolved)) {
      throw new InvalidInputException("The generated alias name for the given responsibility inputs " +
          aliasNameResolved + " already exists in the PIDC Version");
    }
    // 494776 - Handling special characters in Alias names of responsibilities
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    char[] charToExclude = { '@' };
    BitSet validationResult = validator.isValidName(aliasNameResolved, charToExclude);
    if (validationResult.cardinality() != 0) {
      throw new InvalidInputException(
          "The generated alias name for the given responsibility inputs '" + aliasNameResolved +
              "' does not comply with A2L specification\n" + validator.createErrorMsg(validationResult, charToExclude));
    }
    entity.setAliasName(aliasNameResolved);
  }

  /**
   * @param entity
   */
  private void updateReferencingEntities(final TA2lResponsibility entity) {
    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    TabvProjectidcard pidcEntity = pidcLoader.getEntityObject(getInputData().getProjectId());
    pidcEntity.getTA2lResponsibilityList().remove(entity);
    pidcEntity.getTA2lResponsibilityList().add(entity);

    if (CommonUtils.isNotNull(getInputData().getUserId())) {
      UserLoader userLoader = new UserLoader(getServiceData());
      TabvApicUser userEntity = userLoader.getEntityObject(getInputData().getUserId());
      userEntity.getTA2lResponsibilityList().remove(entity);
      userEntity.getTA2lResponsibilityList().add(entity);
    }

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    A2lResponsibilityLoader a2lResponsibilityLoader = new A2lResponsibilityLoader(getServiceData());
    TA2lResponsibility tA2lResp = a2lResponsibilityLoader.getEntityObject(getInputData().getId());
    // removeFromTA2lWpResp(tA2lResp);
    // removeFromTA2lWpParamMapping(tA2lResp);
    // removeFromTRvwWpResp(tA2lResp);
    // removeFromTCDFxDelWpResp(tA2lResp);
    // removeFromTRvwQnaireRespVariants(tA2lResp);
    removeFromTabvProjectIdCard(tA2lResp);
    removeFromTabvApicUser(tA2lResp);
    getEm().remove(tA2lResp);
  }

  /**
   * @param tA2lResp
   */
  private void removeFromTabvApicUser(final TA2lResponsibility tA2lResp) {
    if (null != tA2lResp.getTabvApicUser()) {
      tA2lResp.getTabvApicUser().getTA2lResponsibilityList().remove(tA2lResp);
    }
  }

  /**
   * @param tA2lResp
   */
  private void removeFromTabvProjectIdCard(final TA2lResponsibility tA2lResp) {
    if (null != tA2lResp.getTabvProjectidcard()) {
      tA2lResp.getTabvProjectidcard().getTA2lResponsibilityList().remove(tA2lResp);
    }
  }

  /**
   * @param tA2lResp
   */
  private void removeFromTRvwQnaireRespVariants(final TA2lResponsibility tA2lResp) {
    if (null != tA2lResp.gettRvwQnaireRespVariant()) {
      for (TRvwQnaireRespVariant tRvwQnaireRespvariant : tA2lResp.gettRvwQnaireRespVariant()) {
        tRvwQnaireRespvariant.settA2lResponsibility(null);
      }
    }
  }

  /**
   * @param tA2lResp
   */
  private void removeFromTCDFxDelWpResp(final TA2lResponsibility tA2lResp) {
    if (null != tA2lResp.gettCDFxDelWpRespList()) {
      for (TCDFxDelvryWpResp tCDFxDelvryWpResp : tA2lResp.gettCDFxDelWpRespList()) {
        tCDFxDelvryWpResp.setResp(null);
      }
    }
  }

  /**
   * @param tA2lResp
   */
  private void removeFromTRvwWpResp(final TA2lResponsibility tA2lResp) {
    if (null != tA2lResp.gettRvwWpResps()) {
      for (TRvwWpResp tRvwWpResp : tA2lResp.gettRvwWpResps()) {
        tRvwWpResp.settA2lResponsibility(null);
      }
    }
  }

  /**
   * @param tA2lResp
   */
  private void removeFromTA2lWpParamMapping(final TA2lResponsibility tA2lResp) {
    if (null != tA2lResp.getWpParamMappingList()) {
      for (TA2lWpParamMapping tA2lWpParamMapping : tA2lResp.getWpParamMappingList()) {
        tA2lWpParamMapping.setTA2lResponsibility(null);
      }
    }
  }

  /**
   * @param tA2lResp
   */
  private void removeFromTA2lWpResp(final TA2lResponsibility tA2lResp) {
    if (null != tA2lResp.getWpRespPalList()) {
      for (TA2lWpResponsibility tA2lWpResponsibility : tA2lResp.getWpRespPalList()) {
        tA2lWpResponsibility.setA2lResponsibility(null);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
//NA
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
    NodeAccessLoader nodeAccessLoader = new NodeAccessLoader(getServiceData());
    if (null != getInputData().getProjectId()) {
      return nodeAccessLoader.isCurrentUserOwner(getInputData().getProjectId());
    }
    return true;
  }

  /**
   * disable edit option if chosen responsibility is a default responsibility or is of Bosch Type with user details
   *
   * @param selA2lResp A2lResponsibility
   * @return boolean
   */
  public boolean isEditNotApplicable(final A2lResponsibility selA2lResp) {
    return A2lResponsibilityCommon.isDefaultResponsibility(selA2lResp) ||
        (CommonUtils.isEqual(WpRespType.RB.getCode(), selA2lResp.getRespType()) && (null != selA2lResp.getUserId()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // Auto-generated method stub
  }

  /**
   * @return A2lResponsibility
   */
  public A2lResponsibility getInputA2lRespData() {
    return getInputData();
  }


  /**
   * @return the a2lBoschGroupUserCreateCmndList
   */
  public List<A2lRespBoschGroupUserCommand> getA2lBoschGroupUserCreateCmndList() {
    return this.a2lBoschGroupUserCreateCmndList;
  }


}
