/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.AttrDiffType;
import com.bosch.caltool.icdm.model.apic.pidc.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedAttrType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedSubVarType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedVariantType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsResponseType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFocusMatrixType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFocusMatrixVersType;

/**
 * @author dmr1cob
 */
public class PidcDiffsResponseCreator {

  /**
   * @param pidcDiffsResponseType {@link PidcDiffsResponseType}
   * @return {@link PidcDiffsResponseType} Copy
   */
  public PidcDiffsResponseType getPidcDiffResponseObj(final PidcDiffsResponseType pidcDiffsResponseType) {
    PidcDiffsResponseType responseType = new PidcDiffsResponseType();
    if (null != pidcDiffsResponseType) {
      responseType.setPidcId(pidcDiffsResponseType.getPidcId());
      responseType.setOldChangeNumber(pidcDiffsResponseType.getOldChangeNumber());
      responseType.setNewChangeNumber(pidcDiffsResponseType.getNewChangeNumber());
      responseType.setOldPidcVersionNumber(pidcDiffsResponseType.getOldPidcVersionNumber());
      responseType.setNewPidcVersionNumber(pidcDiffsResponseType.getNewPidcVersionNumber());
      responseType.setOldPidcStatus(pidcDiffsResponseType.getOldPidcStatus());
      responseType.setNewPidcStatus(pidcDiffsResponseType.getNewPidcStatus());
      responseType.setOldIsDeleted(pidcDiffsResponseType.getOldIsDeleted());
      responseType.setNewIsDeleted(pidcDiffsResponseType.getNewIsDeleted());
      responseType.setModifiedDate(pidcDiffsResponseType.getModifiedDate());
      responseType.setModifiedUser(pidcDiffsResponseType.getModifiedUser());
      responseType.setPidcVersion(pidcDiffsResponseType.getPidcVersion());
      responseType.setPidcChangedAttrTypeList(
          getPidcChangedAttrResponseObj(pidcDiffsResponseType.getPidcChangedAttrTypeList()));
      responseType.setPidcChangedVariantTypeList(
          getPidcChangedVarResponseObj(pidcDiffsResponseType.getPidcChangedVariantTypeList()));
      responseType
          .setPidcFocusMatrixTypeList(getPidcFmTypeResponseObj(pidcDiffsResponseType.getPidcFocusMatrixTypeList()));
      responseType.setPidcFocusMatrixVersTypeList(
          getPidcFmVersTypeResponseObj(pidcDiffsResponseType.getPidcFocusMatrixVersTypeList()));
    }
    return responseType;
  }

  /**
   * @param attrDiffType {@link AttrDiffType}
   * @return {@link AttrDiffType}
   */
  public AttrDiffType getAttrDiffType(final AttrDiffType attrDiffType) {
    AttrDiffType response = new AttrDiffType();
    if (null != attrDiffType) {
      response.setPidcId(attrDiffType.getPidcId());
      response.setVariantId(attrDiffType.getVariantId());
      response.setSubVariantId(attrDiffType.getSubVariantId());
      response.setLevel(attrDiffType.getLevel());
      response.setAttribute(getAttribute(attrDiffType.getAttribute()));
      response.setChangedItem(attrDiffType.getChangedItem());
      response.setOldAttributeValue(getAttrValue(attrDiffType.getOldAttributeValue()));
      response.setNewAttributeValue(getAttrValue(attrDiffType.getNewAttributeValue()));
      response.setOldValue(attrDiffType.getOldValue());
      response.setNewValue(attrDiffType.getNewValue());
      response.setModifiedUser(attrDiffType.getModifiedUser());
      response.setModifiedName(attrDiffType.getModifiedName());
      response.setModifiedDate(attrDiffType.getModifiedDate());
      response.setVersionId(attrDiffType.getVersionId());
      response.setPidcversion(attrDiffType.getPidcversion());
      response.setAttributeChange(attrDiffType.isAttributeChange());
      response.setPidcVersVersId(attrDiffType.getPidcVersVersId());
      response.setFocusMatrixChange(attrDiffType.isFocusMatrixChange());
      response.setUseCaseSectionId(attrDiffType.getUseCaseSectionId());
      response.setUseCaseId(attrDiffType.getUseCaseId());
    }
    return response;
  }

  private List<PidcChangedAttrType> getPidcChangedAttrResponseObj(
      final List<PidcChangedAttrType> pidcChangedAttrTypeList) {
    List<PidcChangedAttrType> changedAttrTypeList = new ArrayList<>();
    if (CommonUtils.isNotEmpty(pidcChangedAttrTypeList)) {
      for (PidcChangedAttrType pidcChangedAttrType : pidcChangedAttrTypeList) {
        PidcChangedAttrType changedAttr = new PidcChangedAttrType();
        if (null != pidcChangedAttrType) {
          changedAttr.setAttrId(pidcChangedAttrType.getAttrId());
          changedAttr.setOldValueId(pidcChangedAttrType.getOldValueId());
          changedAttr.setNewValueId(pidcChangedAttrType.getNewValueId());
          changedAttr.setOldUsed(pidcChangedAttrType.getOldUsed());
          changedAttr.setNewUsed(pidcChangedAttrType.getNewUsed());
          changedAttr.setOldPartNumber(pidcChangedAttrType.getOldPartNumber());
          changedAttr.setNewPartNumber(pidcChangedAttrType.getNewPartNumber());
          changedAttr.setOldSpecLink(pidcChangedAttrType.getOldSpecLink());
          changedAttr.setNewSpecLink(pidcChangedAttrType.getNewSpecLink());
          changedAttr.setOldDesc(pidcChangedAttrType.getOldDesc());
          changedAttr.setNewDesc(pidcChangedAttrType.getNewDesc());
          changedAttr.setOldValueIdClearingStatus(pidcChangedAttrType.getOldValueIdClearingStatus());
          changedAttr.setNewValueIdClearingStatus(pidcChangedAttrType.getNewValueIdClearingStatus());
          changedAttr.setChangeNumber(pidcChangedAttrType.getChangeNumber());
          changedAttr.setModifyDate(pidcChangedAttrType.getModifyDate());
          changedAttr.setModifyUser(pidcChangedAttrType.getModifyUser());
          changedAttr.setPidcVers(pidcChangedAttrType.getPidcVers());
          changedAttr.setOldIsVariant(pidcChangedAttrType.getOldIsVariant());
          changedAttr.setNewIsVariant(pidcChangedAttrType.getNewIsVariant());
          changedAttr.setLevel(pidcChangedAttrType.getLevel());
          changedAttr.setPidcVersChangeNum(pidcChangedAttrType.getPidcVersChangeNum());
          changedAttr.setOldFocusMatrix(pidcChangedAttrType.getOldFocusMatrix());
          changedAttr.setNewFocusMatrix(pidcChangedAttrType.getNewFocusMatrix());
          changedAttr.setOldTranferVcdm(pidcChangedAttrType.getOldTranferVcdm());
          changedAttr.setNewTranferVcdm(pidcChangedAttrType.getNewTranferVcdm());
          changedAttr.setPidcAction(pidcChangedAttrType.getPidcAction());
        }
        changedAttrTypeList.add(changedAttr);
      }
    }
    return changedAttrTypeList;
  }

  private List<PidcChangedVariantType> getPidcChangedVarResponseObj(
      final List<PidcChangedVariantType> pidcChangedVariantTypeList) {
    List<PidcChangedVariantType> variantTypeList = new ArrayList<>();
    if (CommonUtils.isNotEmpty(pidcChangedVariantTypeList)) {
      for (PidcChangedVariantType pidcChangedVariantType : pidcChangedVariantTypeList) {
        PidcChangedVariantType variantType = new PidcChangedVariantType();
        if (null != pidcChangedVariantType) {
          variantType.setVariantId(pidcChangedVariantType.getVariantId());
          variantType.setOldValueId(pidcChangedVariantType.getOldValueId());
          variantType.setNewValueId(pidcChangedVariantType.getNewValueId());
          variantType.setOldChangeNumber(pidcChangedVariantType.getOldChangeNumber());
          variantType.setNewChangeNumber(pidcChangedVariantType.getNewChangeNumber());
          variantType.setOldIsDeleted(pidcChangedVariantType.getOldIsDeleted());
          variantType.setNewIsDeleted(pidcChangedVariantType.getNewIsDeleted());
          variantType.setModifiedDate(pidcChangedVariantType.getModifiedDate());
          variantType.setModifiedUser(pidcChangedVariantType.getModifiedUser());
          variantType.setPidcVersion(pidcChangedVariantType.getPidcVersion());
          variantType.setPidcVersChangeNumber(pidcChangedVariantType.getPidcVersChangeNumber());
          variantType.setOldTextValueEng(pidcChangedVariantType.getOldTextValueEng());
          variantType.setOldTextValueGer(pidcChangedVariantType.getOldTextValueGer());
          variantType.setNewTextValueEng(pidcChangedVariantType.getNewTextValueEng());
          variantType.setNewTextValueGer(pidcChangedVariantType.getNewTextValueGer());
          variantType.setChangedAttrList(getPidcChangedAttrResponseObj(pidcChangedVariantType.getChangedAttrList()));
          variantType.setChangedSubVariantList(
              getPidcChangedSubVarResponseObj(pidcChangedVariantType.getChangedSubVariantList()));
        }
        variantTypeList.add(variantType);
      }
    }
    return variantTypeList;
  }

  private List<PidcChangedSubVarType> getPidcChangedSubVarResponseObj(
      final List<PidcChangedSubVarType> changedSubVariantList) {
    List<PidcChangedSubVarType> subVarTypeList = new ArrayList<>();
    if (CommonUtils.isNotEmpty(changedSubVariantList)) {
      for (PidcChangedSubVarType pidcChangedSubVarType : changedSubVariantList) {
        PidcChangedSubVarType subVarType = new PidcChangedSubVarType();
        if (null != pidcChangedSubVarType) {
          subVarType.setSubVariantId(pidcChangedSubVarType.getSubVariantId());
          subVarType.setOldValueId(pidcChangedSubVarType.getOldValueId());
          subVarType.setNewValueId(pidcChangedSubVarType.getNewValueId());
          subVarType.setOldChangeNumber(pidcChangedSubVarType.getOldChangeNumber());
          subVarType.setNewChangeNumber(pidcChangedSubVarType.getNewChangeNumber());
          subVarType.setOldIsdeleted(pidcChangedSubVarType.getOldIsdeleted());
          subVarType.setNewIsDeleted(pidcChangedSubVarType.getNewIsDeleted());
          subVarType.setModifiedDate(pidcChangedSubVarType.getModifiedDate());
          subVarType.setModifiedUser(pidcChangedSubVarType.getModifiedUser());
          subVarType.setPidcVersion(pidcChangedSubVarType.getPidcVersion());
          subVarType.setPidcVersChangeNumber(pidcChangedSubVarType.getPidcVersChangeNumber());
          subVarType.setOldTextValueEng(pidcChangedSubVarType.getOldTextValueEng());
          subVarType.setOldTextValueGer(pidcChangedSubVarType.getOldTextValueGer());
          subVarType.setNewTextValueEng(pidcChangedSubVarType.getNewTextValueEng());
          subVarType.setNewTextValueGer(pidcChangedSubVarType.getNewTextValueGer());
          subVarType.setChangedAttrList(getPidcChangedAttrResponseObj(pidcChangedSubVarType.getChangedAttrList()));
        }
        subVarTypeList.add(subVarType);
      }
    }
    return subVarTypeList;
  }

  private List<PidcFocusMatrixType> getPidcFmTypeResponseObj(final List<PidcFocusMatrixType> pidcFocusMatrixTypeList) {
    List<PidcFocusMatrixType> focusMatrixTypeList = new ArrayList<>();
    if (CommonUtils.isNotEmpty(pidcFocusMatrixTypeList)) {
      for (PidcFocusMatrixType pidcFocusMatrixType : pidcFocusMatrixTypeList) {
        PidcFocusMatrixType fmType = new PidcFocusMatrixType();
        if (null != pidcFocusMatrixType) {
          fmType.setFmId(pidcFocusMatrixType.getFmId());
          fmType.setFmUcpaId(pidcFocusMatrixType.getFmUcpaId());
          fmType.setOldFmColorCode(pidcFocusMatrixType.getOldFmColorCode());
          fmType.setNewFmColorCode(pidcFocusMatrixType.getNewFmColorCode());
          fmType.setOldComments(pidcFocusMatrixType.getOldComments());
          fmType.setNewComments(pidcFocusMatrixType.getNewComments());
          fmType.setCreatedUser(pidcFocusMatrixType.getCreatedUser());
          fmType.setCreatedDate(pidcFocusMatrixType.getCreatedDate());
          fmType.setModifiedUser(pidcFocusMatrixType.getModifiedUser());
          fmType.setModifiedDate(pidcFocusMatrixType.getModifiedDate());
          fmType.setFmVersion(pidcFocusMatrixType.getFmVersion());
          fmType.setOldFmLink(pidcFocusMatrixType.getOldFmLink());
          fmType.setNewFmLink(pidcFocusMatrixType.getNewFmLink());
          fmType.setUseCaseId(pidcFocusMatrixType.getUseCaseId());
          fmType.setSectionId(pidcFocusMatrixType.getSectionId());
          fmType.setAttrId(pidcFocusMatrixType.getAttrId());
          fmType.setOldDeletedFlag(pidcFocusMatrixType.getOldDeletedFlag());
          fmType.setNewDeletedFlag(pidcFocusMatrixType.getNewDeletedFlag());
          fmType.setFmVersionId(pidcFocusMatrixType.getFmVersionId());
          fmType.setPidcVersChangeNumber(pidcFocusMatrixType.getPidcVersChangeNumber());
          fmType.setPidcVersId(pidcFocusMatrixType.getPidcVersId());
          fmType.setChangeNumber(pidcFocusMatrixType.getChangeNumber());
        }
        focusMatrixTypeList.add(fmType);
      }
    }
    return focusMatrixTypeList;
  }

  private List<PidcFocusMatrixVersType> getPidcFmVersTypeResponseObj(
      final List<PidcFocusMatrixVersType> pidcFocusMatrixVersTypeList) {
    List<PidcFocusMatrixVersType> fmVersTypeList = new ArrayList<>();
    if (CommonUtils.isNotEmpty(pidcFocusMatrixVersTypeList)) {
      for (PidcFocusMatrixVersType pidcFocusMatrixVersType : pidcFocusMatrixVersTypeList) {
        PidcFocusMatrixVersType fmVersType = new PidcFocusMatrixVersType();
        if (null != pidcFocusMatrixVersType) {
          fmVersType.setFmVersId(pidcFocusMatrixVersType.getFmVersId());
          fmVersType.setOldFmVersName(pidcFocusMatrixVersType.getOldFmVersName());
          fmVersType.setNewFmVersName(pidcFocusMatrixVersType.getNewFmVersName());
          fmVersType.setFmRevNum(pidcFocusMatrixVersType.getFmRevNum());
          fmVersType.setOldFmVersStatus(pidcFocusMatrixVersType.getOldFmVersStatus());
          fmVersType.setNewFmVersStatus(pidcFocusMatrixVersType.getNewFmVersStatus());
          fmVersType.setOldFmVersRvwUser(pidcFocusMatrixVersType.getOldFmVersRvwUser());
          fmVersType.setNewFmVersRvwUser(pidcFocusMatrixVersType.getNewFmVersRvwUser());
          fmVersType.setOldFmVersLink(pidcFocusMatrixVersType.getOldFmVersLink());
          fmVersType.setNewFmVersLink(pidcFocusMatrixVersType.getNewFmVersLink());
          fmVersType.setOldRemark(pidcFocusMatrixVersType.getOldRemark());
          fmVersType.setNewRemark(pidcFocusMatrixVersType.getNewRemark());
          fmVersType.setOldFmRvwStatus(pidcFocusMatrixVersType.getOldFmRvwStatus());
          fmVersType.setNewFmRvwStatus(pidcFocusMatrixVersType.getNewFmRvwStatus());
          fmVersType.setFmVersCreatedUser(pidcFocusMatrixVersType.getFmVersCreatedUser());
          fmVersType.setFmVersCreatedDate(pidcFocusMatrixVersType.getFmVersCreatedDate());
          fmVersType.setFmVersModifiedDate(pidcFocusMatrixVersType.getFmVersModifiedDate());
          fmVersType.setFmVersModifiedUser(pidcFocusMatrixVersType.getFmVersModifiedUser());
          fmVersType.setFmVersVersion(pidcFocusMatrixVersType.getFmVersVersion());
          fmVersType.setPidcVersChangenumber(pidcFocusMatrixVersType.getPidcVersChangenumber());
          fmVersType.setPidcVersId(pidcFocusMatrixVersType.getPidcVersId());
          fmVersType.setChangeNumber(pidcFocusMatrixVersType.getChangeNumber());
          fmVersType.setOldFmVersRvwDate(pidcFocusMatrixVersType.getOldFmVersRvwDate());
          fmVersType.setNewFmVersRvwDate(pidcFocusMatrixVersType.getNewFmVersRvwDate());
        }
        fmVersTypeList.add(fmVersType);
      }
    }
    return fmVersTypeList;
  }

  private Attribute getAttribute(final Attribute attr) {
    Attribute result = new Attribute();
    if (null != attr) {
      result.setId(attr.getId());
      result.setNameEng(attr.getNameEng());
      result.setNameGer(attr.getNameGer());
      result.setDescEng(attr.getDescEng());
      result.setDescGer(attr.getDescGer());
      result.setUnit(attr.getUnit());
      result.setFormat(attr.getFormat());
      result.setNormalized(attr.isNormalized());
      result.setDeleted(attr.isDeleted());
      result.setMandatory(attr.isMandatory());
      result.setAttrLevel(attr.getAttrLevel());
      result.setGroupId(attr.getGroupId());
      result.setTypeId(attr.getTypeId());
      result.setCreateDate(attr.getCreateDate());
      result.setModifyDate(attr.getModifyDate());
      result.setCreateUser(attr.getCreateUser());
      result.setModifyUser(attr.getModifyUser());
      result.setChangeNumber(attr.getChangeNumber());
    }
    return result;
  }

  private AttributeValue getAttrValue(final AttributeValue attrVal) {
    AttributeValue result = new AttributeValue();
    if (null != attrVal) {
      result.setValueId(attrVal.getValueId());
      result.setAttrId(attrVal.getAttrId());
      result.setValueEng(attrVal.getValueEng());
      result.setValueGer(attrVal.getValueGer());
      result.setDeleted(attrVal.isDeleted());
      result.setCreatedDate(attrVal.getCreatedDate());
      result.setCreatedUser(attrVal.getCreatedUser());
      result.setModifyDate(attrVal.getModifyDate());
      result.setModifyUser(attrVal.getModifyUser());
      result.setChangeNumber(attrVal.getChangeNumber());
      result.setClearingStatus(attrVal.getClearingStatus());
      result.setCleared(attrVal.isCleared());
      result.setValueDescEng(attrVal.getValueDescEng());
      result.setValueDescGer(attrVal.getValueDescGer());
    }
    return result;
  }
}
