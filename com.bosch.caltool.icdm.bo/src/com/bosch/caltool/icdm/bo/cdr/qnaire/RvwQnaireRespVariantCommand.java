package com.bosch.caltool.icdm.bo.cdr.qnaire;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVariant;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;


/**
 * Command class for RvwQnaireRespVariant
 *
 * @author say8cob
 */
public class RvwQnaireRespVariantCommand extends AbstractCommand<RvwQnaireRespVariant, RvwQnaireRespVariantLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @param isUpdate true, if service call is to update
   * @throws IcdmException error when initializing
   */
  public RvwQnaireRespVariantCommand(final ServiceData serviceData, final RvwQnaireRespVariant input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new RvwQnaireRespVariantLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    RvwQnaireResponseLoader rvwQnaireResponseLoader = new RvwQnaireResponseLoader(getServiceData());
    Long qnaireRespId = getInputData().getQnaireRespId();
    TRvwQnaireRespVariant entity = new TRvwQnaireRespVariant();

    // qnaireRespId is Null for Simplified Qnaire
    if (CommonUtils.isNotNull(qnaireRespId)) {
      TRvwQnaireResponse tRvwQnaireResponse = rvwQnaireResponseLoader.getEntityObject(qnaireRespId);

      // Linked Qnaire resp cannot be linked to the primary variant
      if (CommonUtils.isNotEmpty(tRvwQnaireResponse.getTRvwQnaireRespVariants())) {
        for (TRvwQnaireRespVariant qnaireRespVar : tRvwQnaireResponse.getTRvwQnaireRespVariants()) {
          // Check if the Questionnaire Response is tried to be linked with
          // parent combination of Variant-Resp-Wp again
          if (checkIfPrimaryVariantLinking(qnaireRespVar)) {
            RvwQnaireResponse rvwQnaireResponse = rvwQnaireResponseLoader.createDataObject(tRvwQnaireResponse);
            throw new IcdmException("RVW_QNAIRE_RESP.LINK_VAR_NOT_ALLOWED",
                rvwQnaireResponse.getPrimaryVarRespWpName());
          }
        }
      }

      tRvwQnaireResponse.addTRvwQnaireRespVariants(entity);
    }

    TPidcVersion tPidcVersion = new PidcVersionLoader(getServiceData()).getEntityObject(getInputData().getPidcVersId());
    tPidcVersion.addTRvwQnaireRespVariants(entity);

    TA2lResponsibility tA2lResponsibility =
        new A2lResponsibilityLoader(getServiceData()).getEntityObject(getInputData().getA2lRespId());
    tA2lResponsibility.addTRvwQnaireRespVariants(entity);

    TA2lWorkPackage tA2lWorkPackage =
        new A2lWorkPackageLoader(getServiceData()).getEntityObject(getInputData().getA2lWpId());
    tA2lWorkPackage.addTRvwQnaireRespVariants(entity);

    if (CommonUtils.isNotNull(getInputData().getVariantId()) &&
        CommonUtils.isNotEqual(getInputData().getVariantId(), ApicConstants.NO_VARIANT_ID)) {
      TabvProjectVariant tabvProjectVariant =
          new PidcVariantLoader(getServiceData()).getEntityObject(getInputData().getVariantId());
      tabvProjectVariant.addTRvwQnaireRespVariants(entity);
    }

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * @param qnaireRespVar
   * @return
   */
  private boolean checkIfPrimaryVariantLinking(final TRvwQnaireRespVariant qnaireRespVar) {
    // tRvwQnaireResp is null for simplified Qnaire
    TRvwQnaireResponse tRvwQnaireResponse = qnaireRespVar.getTRvwQnaireResponse();
    return compareLinkVariant(qnaireRespVar) &&
        CommonUtils.isEqual(qnaireRespVar.gettA2lResponsibility().getA2lRespId(), getInputData().getA2lRespId()) &&
        CommonUtils.isEqual(qnaireRespVar.gettA2lWorkPackage().getA2lWpId(), getInputData().getA2lWpId()) &&
        CommonUtils.isNotNull(tRvwQnaireResponse) &&
        CommonUtils.isEqual(getInputData().getQnaireRespId(), tRvwQnaireResponse.getQnaireRespId()) &&
        CommonUtils.isEqual(getInputData().getPidcVersId(), qnaireRespVar.getTPidcVersion().getPidcVersId());
  }

  /**
   * @param qnaireRespVar
   * @return
   */
  private boolean compareLinkVariant(final TRvwQnaireRespVariant qnaireRespVar) {
    if (CommonUtils.isNull(qnaireRespVar.getTabvProjectVariant()) &&
        CommonUtils.isNull(getInputData().getVariantId())) {
      return true;
    }
    else if (CommonUtils.isNull(qnaireRespVar.getTabvProjectVariant()) ||
        CommonUtils.isNull(getInputData().getVariantId())) {
      return false;
    }
    return CommonUtils.isEqual(qnaireRespVar.getTabvProjectVariant().getVariantId(), getInputData().getVariantId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    RvwQnaireRespVariantLoader qnaireRespVarLoader = new RvwQnaireRespVariantLoader(getServiceData());
    TRvwQnaireRespVariant tQnaireRespVar = qnaireRespVarLoader.getEntityObject(getInputData().getId());

    // While Merging A2l Responsibility for Simplified Qnaire, update TRvwQnaireRespVariant record with new A2l
    // Responsibility
    if (CommonUtils.isNotEqual(tQnaireRespVar.gettA2lResponsibility().getA2lRespId(), getInputData().getA2lRespId())) {
      tQnaireRespVar.settA2lResponsibility(
          new A2lResponsibilityLoader(getServiceData()).getEntityObject(getInputData().getA2lRespId()));
    }


    if (CommonUtils.isNull(getOldData().getVariantId())) {
      tQnaireRespVar.setTabvProjectVariant(
          new PidcVariantLoader(getServiceData()).getEntityObject(getInputData().getVariantId()));
    }
    setUserDetails(COMMAND_MODE.UPDATE, tQnaireRespVar);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TRvwQnaireRespVariant entity =
        new RvwQnaireRespVariantLoader(getServiceData()).getEntityObject(getInputData().getId());
    // For No-Variant case
    if (CommonUtils.isNotNull(entity.getTabvProjectVariant())) {
      entity.getTabvProjectVariant().removeTRvwQnaireRespVariants(entity);
    }
    entity.gettA2lResponsibility().removeTRvwQnaireRespVariants(entity);
    entity.gettA2lWorkPackage().removeTRvwQnaireRespVariants(entity);

    entity.getTPidcVersion().removeTRvwQnaireRespVariant(entity);

    // tRvwQnaireResp is Null for Simplified Gnrl Qnaire resp - Empty WP/Resp Structure
    TRvwQnaireResponse tRvwQnaireResponse = entity.getTRvwQnaireResponse();
    if (CommonUtils.isNotNull(tRvwQnaireResponse)) {
      tRvwQnaireResponse.removeTRvwQnaireRespVariants(entity);
    }

    getEm().remove(entity);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Not Applicable
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
    // Not Applicable
  }

}
