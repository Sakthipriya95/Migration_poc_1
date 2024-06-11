/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.qnaire;

import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectHandlerInit;
import com.bosch.caltool.icdm.common.bo.qnaire.IQuesRespDataProvider;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestAttrAndValDepModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;

/**
 * @author mkl2cob
 */
public class QuesRespDataProviderClient implements IQuesRespDataProvider {

  /** The pidc data handler. */
  private final PidcDataHandler pidcDataHandler;
  /** The project handler. */
  private ProjectHandlerInit pidcLevelProjHandlerInitializer = null;
  /** The project variant handler. */
  private ProjectHandlerInit pidcVariantLevelProjHandlerInitializer = null;
  /** The project sub-variant handler. */
  private ProjectHandlerInit pidcSubVariantLevelProjHandlerInitializer = null;
  /**
   * QnaireDefBO
   */
  private final QnaireDefBO mainQnaireDefBo;

  /**
   * @param mainQnaireDefBo QnaireDefBO
   * @param pidcDataHandler2 PidcDataHandler
   * @param pidcVariant PidcVariant
   * @param pidcVersion PidcVersion
   */
  public QuesRespDataProviderClient(final QnaireDefBO mainQnaireDefBo, final PidcVersion pidcVersion,
      final PidcVariant pidcVariant, final PidcDataHandler pidcDataHandler2) {
    this.mainQnaireDefBo = mainQnaireDefBo;
    this.pidcDataHandler = pidcDataHandler2;
    this.pidcLevelProjHandlerInitializer =
        new ProjectHandlerInit(pidcVersion, pidcVariant, this.pidcDataHandler, ApicConstants.LEVEL_PIDC_VERSION);
    if (pidcVariant != null) {
      this.pidcVariantLevelProjHandlerInitializer =
          new ProjectHandlerInit(pidcVersion, pidcVariant, this.pidcDataHandler, ApicConstants.LEVEL_PIDC_VARIANT);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Question> getQuestionsSet() {
    return this.mainQnaireDefBo.getAllQuestions(true, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getQuesRespVersId() {
    return this.mainQnaireDefBo.getQnaireVersion().getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QuestAttrAndValDepModel getQuesAttrDepModel() {
    return this.mainQnaireDefBo.getAllQnAttrValDepModel();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, IProjectAttribute> getUsedProjAttrMap() {
    return this.pidcLevelProjHandlerInitializer.getProjectObjectBO().getAttributesUsed();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCleared(final AttributeValue attrVal) {
    return new AttributeValueClientBO(attrVal).isCleared();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isVisible(final IProjectAttribute projAttr) {
    AbstractProjectObjectBO projectObjectBO = null;

    if (projAttr instanceof PidcVersionAttribute) {
      projectObjectBO = this.pidcLevelProjHandlerInitializer.getProjectObjectBO();
    }
    else if (projAttr instanceof PidcVariantAttribute) {
      projectObjectBO = this.pidcVariantLevelProjHandlerInitializer.getProjectObjectBO();
    }
    else if (projAttr instanceof PidcSubVariantAttribute) {
      projectObjectBO = this.pidcSubVariantLevelProjHandlerInitializer.getProjectObjectBO();
    }
    return getProjectAttributeHandler(projAttr, projectObjectBO).isVisible();
  }

  /**
   * Gets the project attribute handler.
   *
   * @param projectAttribute the project attribute
   * @param projObjBO the project handler
   * @return the project attribute handler
   */
  public AbstractProjectAttributeBO getProjectAttributeHandler(final IProjectAttribute projectAttribute,
      final AbstractProjectObjectBO projObjBO) {
    AbstractProjectAttributeBO projectAttrHandler = null;
    // create the specific handler
    if (projObjBO instanceof PidcVersionBO) {
      projectAttrHandler =
          new PidcVersionAttributeBO((PidcVersionAttribute) projectAttribute, (PidcVersionBO) projObjBO);
    }
    else if (projObjBO instanceof PidcVariantBO) {
      projectAttrHandler =
          new PidcVariantAttributeBO((PidcVariantAttribute) projectAttribute, (PidcVariantBO) projObjBO);
    }
    else if (projObjBO instanceof PidcSubVariantBO) {
      projectAttrHandler =
          new PidcSubVariantAttributeBO((PidcSubVariantAttribute) projectAttribute, (PidcSubVariantBO) projObjBO);
    }
    return projectAttrHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcSubVariant> getSubVarMap() {
    return this.pidcVariantLevelProjHandlerInitializer.getProjectObjectBO().getPidcDataHandler().getSubVariantMap();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public PidcSubVariantAttribute getProjSubVarAttr(final Attribute depAttr, final PidcSubVariant subVariant) {
    this.pidcSubVariantLevelProjHandlerInitializer =
        new ProjectHandlerInit(this.pidcVariantLevelProjHandlerInitializer.getProjectObjectBO().getPidcVersion(),
            subVariant, this.pidcDataHandler, ApicConstants.LEVEL_PIDC_SUB_VARIANT);
    return this.pidcVariantLevelProjHandlerInitializer.getProjectObjectBO().getPidcDataHandler()
        .getSubVariantAttributeMap().get(subVariant.getId()).get(depAttr.getId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public AttributeValue getAttributeValue(final IProjectAttribute projAttr) {
    return this.pidcDataHandler.getAttributeValueMap().get(projAttr.getValueId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, IProjectAttribute> getUsedVarAttrMap() {
    return this.pidcVariantLevelProjHandlerInitializer.getProjectObjectBO().getAttributesUsed();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isQuesDepChkNeeded() {
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Question getQuestion(final Long questionId) {
    return this.mainQnaireDefBo.getQuestion(questionId);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isHeading(final long questionId) {
    return this.mainQnaireDefBo.isHeading(questionId);
  }

}
