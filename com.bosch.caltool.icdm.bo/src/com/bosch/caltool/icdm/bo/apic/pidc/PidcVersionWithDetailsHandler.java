/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;

/**
 * @author dmo5cob
 */
public class PidcVersionWithDetailsHandler {

  private final ServiceData serviceData;

  /**
   * @param serviceData ServiceData
   */
  public PidcVersionWithDetailsHandler(final ServiceData serviceData) {
    this.serviceData = serviceData;
  }


  /**
   * @param afterUpdateModel a model
   * @param beforeUpdateModel another model
   * @param inputModel ProjectAttributesUpdationModel
   * @return differences
   * @throws DataException
   */
  public ProjectAttributesUpdationModel getChanges(final PidcVersionAttributeModel afterUpdateModel,
      final PidcVersionAttributeModel beforeUpdateModel, final ProjectAttributesUpdationModel inputModel)
      throws DataException {
    ProjectAttributesUpdationModel result = new ProjectAttributesUpdationModel();

    // set pidc version objects
    result.setPidcVersion(beforeUpdateModel.getPidcVersion());
    result.setNewPidcVersion(afterUpdateModel.getPidcVersion());


    // when invisible attributes are created (via grped attributes changes dialog), get the data from loader as it wont
    // be available in pidc vers attribute map
    for (PidcVersionAttribute versAttr : inputModel.getPidcAttrsToBeCreated().values()) {
      result.getPidcAttrsToBeCreated().put(versAttr.getAttrId(),
          (null == afterUpdateModel.getPidcVersAttrMap().get(versAttr.getAttrId())
              ? new PidcVersionAttributeLoader(this.serviceData)
                  .getPidcVersionAttributeForAttr(versAttr.getPidcVersId(), versAttr.getAttrId())
              : afterUpdateModel.getPidcVersAttrMap().get(versAttr.getAttrId())));
    }


    for (PidcVersionAttribute versAttr : inputModel.getPidcAttrsToBeCreatedwithNewVal().values()) {
      result.getPidcAttrsToBeCreatedwithNewVal().put(versAttr.getAttrId(),
          (null == afterUpdateModel.getPidcVersAttrMap().get(versAttr.getAttrId())
              ? new PidcVersionAttributeLoader(this.serviceData)
                  .getPidcVersionAttributeForAttr(versAttr.getPidcVersId(), versAttr.getAttrId())
              : afterUpdateModel.getPidcVersAttrMap().get(versAttr.getAttrId())));
    }


    // when invisible attributes are updated (via grped attributes changes dialog), get the data from loader as it wont
    // be available in pidc vers attribute map
    for (PidcVersionAttribute versAttr : inputModel.getPidcAttrsToBeUpdated().values()) {
      result.getPidcAttrsAfterUpdation().put(versAttr.getAttrId(),
          (null == afterUpdateModel.getPidcVersAttrMap().get(versAttr.getAttrId())
              ? new PidcVersionAttributeLoader(this.serviceData)
                  .getPidcVersionAttributeForAttr(versAttr.getPidcVersId(), versAttr.getAttrId())
              : afterUpdateModel.getPidcVersAttrMap().get(versAttr.getAttrId())));
      result.getPidcAttrsToBeUpdated().put(versAttr.getAttrId(),
          beforeUpdateModel.getPidcVersAttr(versAttr.getAttrId()));
    }


    // add the variant attributes to be created. Use attr Id from input model, and actual data from afterUpdateModel -
    // as it contains latest data
    for (Entry<Long, Map<Long, PidcVariantAttribute>> entry : inputModel.getPidcVarAttrsToBeCreated().entrySet()) {
      Map<Long, PidcVariantAttribute> varAttrMap = new HashMap<>();
      for (Entry<Long, PidcVariantAttribute> attrEntry : entry.getValue().entrySet()) {


        varAttrMap.put(attrEntry.getKey(),
            (null == afterUpdateModel.getVariantAttributeMap(entry.getKey()).get(attrEntry.getKey()))
                ? new PidcVariantAttributeLoader(this.serviceData)
                    .getVariantAttribute(attrEntry.getValue().getVariantId(), attrEntry.getValue().getAttrId())
                : afterUpdateModel.getVariantAttributeMap(entry.getKey()).get(attrEntry.getKey()));

      }
      result.getPidcVarAttrsToBeCreated().put(entry.getKey(), varAttrMap);
    }


    // add the variant attributes to be updated. Use attr Id from input model, and actual data from afterUpdateModel -
    // as it contains latest data
    for (Entry<Long, Map<Long, PidcVariantAttribute>> entry : inputModel.getPidcVarAttrsToBeUpdated().entrySet()) {
      Map<Long, PidcVariantAttribute> varAttrMap = new HashMap<>();
      for (Entry<Long, PidcVariantAttribute> attrEntry : entry.getValue().entrySet()) {
        varAttrMap.put(attrEntry.getKey(),
            (null == afterUpdateModel.getVariantAttributeMap(entry.getKey()).get(attrEntry.getKey()))
                ? new PidcVariantAttributeLoader(this.serviceData)
                    .getVariantAttribute(attrEntry.getValue().getVariantId(), attrEntry.getValue().getAttrId())
                : afterUpdateModel.getVariantAttributeMap(entry.getKey()).get(attrEntry.getKey()));
      }
      result.getPidcVarAttrsToBeUpdated().put(entry.getKey(), varAttrMap);
    }


    // add the subvariant attributes to be created. Use attr Id from input model, and actual data from afterUpdateModel
    // -
    // as it contains latest data
    for (Entry<Long, Map<Long, PidcSubVariantAttribute>> entry : inputModel.getPidcSubVarAttrsToBeCreated()
        .entrySet()) {
      Map<Long, PidcSubVariantAttribute> subVarAttrMap = new HashMap<>();
      for (Entry<Long, PidcSubVariantAttribute> attrEntry : entry.getValue().entrySet()) {

        subVarAttrMap.put(attrEntry.getKey(),
            (null == afterUpdateModel.getSubVariantAttributeMap(entry.getKey()).get(attrEntry.getKey()))
                ? new PidcSubVariantAttributeLoader(this.serviceData)
                    .getSubVariantAttribute(attrEntry.getValue().getSubVariantId(), attrEntry.getValue().getAttrId())
                : afterUpdateModel.getSubVariantAttributeMap(entry.getKey()).get(attrEntry.getKey()));

      }
      result.getPidcSubVarAttrsToBeCreated().put(entry.getKey(), subVarAttrMap);
    }


    // add the subvariant attributes to be updated. Use attr Id from input model, and actual data from afterUpdateModel
    // -
    // as it contains latest data
    for (Entry<Long, Map<Long, PidcSubVariantAttribute>> entry : inputModel.getPidcSubVarAttrsToBeUpdated()
        .entrySet()) {
      Map<Long, PidcSubVariantAttribute> subVarAttrMap = new HashMap<>();
      for (Entry<Long, PidcSubVariantAttribute> attrEntry : entry.getValue().entrySet()) {
        subVarAttrMap.put(attrEntry.getKey(),
            (null == afterUpdateModel.getSubVariantAttributeMap(entry.getKey()).get(attrEntry.getKey()))
                ? new PidcSubVariantAttributeLoader(this.serviceData)
                    .getSubVariantAttribute(attrEntry.getValue().getSubVariantId(), attrEntry.getValue().getAttrId())
                : afterUpdateModel.getSubVariantAttributeMap(entry.getKey()).get(attrEntry.getKey()));
      }
      result.getPidcSubVarAttrsToBeUpdated().put(entry.getKey(), subVarAttrMap);
    }


    // variant and sub variant changes
    result.getPidcVarsToBeUpdated().putAll(inputModel.getPidcVarsToBeUpdated());
    inputModel.getPidcVarsToBeUpdated().entrySet().forEach(var -> result.getPidcVarsToBeUpdatedWithNewVal()
        .put(var.getKey(), afterUpdateModel.getVariantMap().get(var.getKey())));
    result.getPidcSubVarsToBeUpdated().putAll(inputModel.getPidcSubVarsToBeUpdated());
    inputModel.getPidcSubVarsToBeUpdated().entrySet().forEach(var -> result.getPidcSubVarsToBeUpdatedWithNewVal()
        .put(var.getKey(), afterUpdateModel.getSubVariantMap().get(var.getKey())));


    result.getPidcAttrsToBeUpdatedwithNewVal().putAll(inputModel.getPidcAttrsToBeUpdatedwithNewVal());
    result.getPidcVarAttrsToBeCreatedWithNewVal().putAll(inputModel.getPidcVarAttrsToBeCreatedWithNewVal());
    result.getPidcVarAttrsToBeUpdatedWithNewVal().putAll(inputModel.getPidcVarAttrsToBeUpdatedWithNewVal());
    result.getPidcSubVarAttrsToBeCreatedWithNewVal().putAll(inputModel.getPidcSubVarAttrsToBeCreatedWithNewVal());
    result.getPidcSubVarAttrsToBeUpdatedWithNewVal().putAll(inputModel.getPidcSubVarAttrsToBeUpdatedWithNewVal());
    result.getPidcVariantAttributeDeletedMap().putAll(inputModel.getPidcVariantAttributeDeletedMap());
    result.getPidcSubVariantAttributeDeletedMap().putAll(inputModel.getPidcSubVariantAttributeDeletedMap());

    handleNewlyCreatedVarAttrs(result, afterUpdateModel, beforeUpdateModel);
    handleNewlyCreatedSubVarAttrs(result, afterUpdateModel, beforeUpdateModel);
    handlePIDCAttrsVisibility(result, afterUpdateModel, beforeUpdateModel);
    handleVarAttrsVisibility(result, afterUpdateModel, beforeUpdateModel);
    handleSubVarAttrsVisibility(result, afterUpdateModel, beforeUpdateModel);
    handleDeletedSubVarAttrs(result, afterUpdateModel, beforeUpdateModel);

    return result;
  }


  /**
   * @param result
   * @param beforeUpdateModel1
   */
  private void handleNewlyCreatedSubVarAttrs(final ProjectAttributesUpdationModel result,
      final PidcVersionAttributeModel afterUpdateModel1, final PidcVersionAttributeModel beforeUpdateModel1) {
    Map<Long, Map<Long, PidcSubVariantAttribute>> diffMap1 = CommonUtils
        .getDifference(beforeUpdateModel1.getAllSubVariantAttrMap(), afterUpdateModel1.getAllSubVariantAttrMap());
    result.getPidcSubVariantAttributeMap().putAll(diffMap1);

  }

  /**
   * @param result
   * @param beforeUpdateModel1
   */
  private void handleDeletedSubVarAttrs(final ProjectAttributesUpdationModel result,
      final PidcVersionAttributeModel afterUpdateModel1, final PidcVersionAttributeModel beforeUpdateModel1) {
    Map<Long, Map<Long, PidcSubVariantAttribute>> diffMap1 = CommonUtils
        .getDifference(afterUpdateModel1.getAllSubVariantAttrMap(), beforeUpdateModel1.getAllSubVariantAttrMap());
    result.getPidcSubVariantAttributeDeletedMap().putAll(diffMap1);

  }


  /**
   *
   */
  private void handleNewlyCreatedVarAttrs(final ProjectAttributesUpdationModel updatedModel,
      final PidcVersionAttributeModel afterUpdateModel1, final PidcVersionAttributeModel beforeUpdateModel1) {

    Map<Long, Map<Long, PidcVariantAttribute>> diffMap1 = CommonUtils
        .getDifference(beforeUpdateModel1.getAllVariantAttributeMap(), afterUpdateModel1.getAllVariantAttributeMap());
    updatedModel.getPidcVariantAttributeMap().putAll(diffMap1);

  }

  /**
   * @param beforeUpdateModel
   * @param updatedModel
   * @param beforeUpdateModel1
   */
  private void handleVarAttrsVisibility(final ProjectAttributesUpdationModel updatedModel,
      final PidcVersionAttributeModel afterUpdateModel1, final PidcVersionAttributeModel beforeUpdateModel1) {

    Map<Long, Set<Long>> variantInvisbleAttributeMapAfter = new HashMap<>();

    // fill invisible attributes for pidc variant level
    for (PidcVariant variant : afterUpdateModel1.getVariantMap().values()) {
      if (!afterUpdateModel1.getVariantInvisbleAttributeSet(variant.getId()).isEmpty()) {
        variantInvisbleAttributeMapAfter.put(variant.getId(),
            afterUpdateModel1.getVariantInvisbleAttributeSet(variant.getId()));
      }
    }

    Map<Long, Set<Long>> variantInvisbleAttributeMapBefore = new HashMap<>();

    // fill invisible attributes for pidc variant level
    for (PidcVariant variant : beforeUpdateModel1.getVariantMap().values()) {
      if (!beforeUpdateModel1.getVariantInvisbleAttributeSet(variant.getId()).isEmpty()) {
        variantInvisbleAttributeMapBefore.put(variant.getId(),
            beforeUpdateModel1.getVariantInvisbleAttributeSet(variant.getId()));
      }
    }


    Map<Long, Set<Long>> diffMap1 = new HashMap<>();
    for (PidcVariant variant : afterUpdateModel1.getVariantMap().values()) {
      Set<Long> valueDiffSet = CommonUtils.getDifference(variantInvisbleAttributeMapAfter.get(variant.getId()),
          variantInvisbleAttributeMapBefore.get(variant.getId()));
      if (!valueDiffSet.isEmpty()) {
        diffMap1.put(variant.getId(), valueDiffSet);
      }
    }

    for (Entry<Long, Set<Long>> entry : diffMap1.entrySet()) {
      Set<PidcVariantAttribute> setOfVarAttrs = new HashSet<>();
      Map<Long, PidcVariantAttribute> mapOfVarAttrs = beforeUpdateModel1.getVariantAttributeMap(entry.getKey());

      for (Long id : entry.getValue()) {


        if (null != mapOfVarAttrs.get(id)) {
          setOfVarAttrs.add(mapOfVarAttrs.get(id));
        }

        // condition to check if newly created variant attribute is invisible attribute

        else if (null != updatedModel.getPidcVarAttrsToBeCreated().get(entry.getKey()).get(id)) {
          setOfVarAttrs.add(updatedModel.getPidcVarAttrsToBeCreated().get(entry.getKey()).get(id));

        }

      }
      // add to hide
      if (!setOfVarAttrs.isEmpty()) {
        updatedModel.getVariantInvisbleAttributeMap().put(entry.getKey(), setOfVarAttrs);
      }
    }


    Map<Long, Set<Long>> diffMap2 = new HashMap<>();
    for (PidcVariant variant : beforeUpdateModel1.getVariantMap().values()) {
      Set<Long> valueDiffSet = CommonUtils.getDifference(variantInvisbleAttributeMapBefore.get(variant.getId()),
          variantInvisbleAttributeMapAfter.get(variant.getId()));
      if (!valueDiffSet.isEmpty()) {
        diffMap2.put(variant.getId(), valueDiffSet);
      }
    }

    for (Entry<Long, Set<Long>> entry : diffMap2.entrySet()) {
      Set<PidcVariantAttribute> setOfVarAttrss = new HashSet<>();
      Map<Long, PidcVariantAttribute> mapOfVarAttrs = afterUpdateModel1.getVariantAttributeMap(entry.getKey());
      entry.getValue().forEach(id -> {
        if (null != mapOfVarAttrs.get(id)) {
          setOfVarAttrss.add(mapOfVarAttrs.get(id));
        }
      });

      // add to show
      if (!setOfVarAttrss.isEmpty()) {
        updatedModel.getVariantVisbleAttributeMap().put(entry.getKey(), setOfVarAttrss);
      }
    }
  }

  /**
   * @param beforeUpdateModel
   * @param updatedModel
   * @param beforeUpdateModel1
   */
  private void handlePIDCAttrsVisibility(final ProjectAttributesUpdationModel updatedModel,
      final PidcVersionAttributeModel afterUpdateModel1, final PidcVersionAttributeModel beforeUpdateModel1) {
    // add to hide
    CommonUtils
        .getDifference(afterUpdateModel1.getPidcVersInvisibleAttrSet(),
            beforeUpdateModel1.getPidcVersInvisibleAttrSet())
        .forEach(id -> updatedModel.getPidcVersInvisibleAttrSet()
            .add(null == afterUpdateModel1.getPidcVersAttrMap().get(id)
                ? new PidcVersionAttributeLoader(this.serviceData)
                    .createDataObject(afterUpdateModel1.getPidcVersion().getId(), afterUpdateModel1.getAttribute(id))
                : afterUpdateModel1.getPidcVersAttrMap().get(id)));

    // add to show
    CommonUtils
        .getDifference(beforeUpdateModel1.getPidcVersInvisibleAttrSet(),
            afterUpdateModel1.getPidcVersInvisibleAttrSet())
        .forEach(id -> updatedModel.getPidcVersVisibleAttrSet()
            .add(null == afterUpdateModel1.getPidcVersAttrMap().get(id)
                ? new PidcVersionAttributeLoader(this.serviceData)
                    .createDataObject(afterUpdateModel1.getPidcVersion().getId(), afterUpdateModel1.getAttribute(id))
                : afterUpdateModel1.getPidcVersAttrMap().get(id)));


  }

  /**
   * @param beforeUpdateModel
   * @param updatedModel
   * @param beforeUpdateModel1
   */
  private void handleSubVarAttrsVisibility(final ProjectAttributesUpdationModel updatedModel,
      final PidcVersionAttributeModel afterUpdateModel1, final PidcVersionAttributeModel beforeUpdateModel1) {


    Map<Long, Set<Long>> subvariantInvisbleAttributeMapAfter = new HashMap<>();

    // fill invisible attributes for pidc subvariant level
    for (PidcSubVariant svariant : afterUpdateModel1.getSubVariantMap().values()) {
      if (!afterUpdateModel1.getSubVariantInvisbleAttributeSet(svariant.getId()).isEmpty()) {
        subvariantInvisbleAttributeMapAfter.put(svariant.getId(),
            afterUpdateModel1.getSubVariantInvisbleAttributeSet(svariant.getId()));
      }
    }

    Map<Long, Set<Long>> subvariantInvisbleAttributeMapBefore = new HashMap<>();

    // fill invisible attributes for pidcsub variant level
    for (PidcSubVariant svar : beforeUpdateModel1.getSubVariantMap().values()) {
      if (!beforeUpdateModel1.getSubVariantInvisbleAttributeSet(svar.getId()).isEmpty()) {
        subvariantInvisbleAttributeMapBefore.put(svar.getId(),
            beforeUpdateModel1.getSubVariantInvisbleAttributeSet(svar.getId()));
      }
    }


    Map<Long, Set<Long>> diffMap1 = new HashMap<>();
    for (PidcSubVariant subVariant : afterUpdateModel1.getSubVariantMap().values()) {
      Set<Long> valueDiffSet = CommonUtils.getDifference(subvariantInvisbleAttributeMapAfter.get(subVariant.getId()),
          subvariantInvisbleAttributeMapBefore.get(subVariant.getId()));
      if (!valueDiffSet.isEmpty()) {
        diffMap1.put(subVariant.getId(), valueDiffSet);
      }
    }


    for (Entry<Long, Set<Long>> entry : diffMap1.entrySet()) {
      Set<PidcSubVariantAttribute> setOfVarAttrs = new HashSet<>();
      Map<Long, PidcSubVariantAttribute> mapOfVarAttrs = beforeUpdateModel1.getSubVariantAttributeMap(entry.getKey());
      entry.getValue().forEach(id -> {
        if (null != mapOfVarAttrs.get(id)) {
          setOfVarAttrs.add(mapOfVarAttrs.get(id));
        }
        // condition to check if newly created variant attribute is invisible attribute

        else if (null != updatedModel.getPidcSubVarAttrsToBeCreated().get(entry.getKey()).get(id)) {
          setOfVarAttrs.add(updatedModel.getPidcSubVarAttrsToBeCreated().get(entry.getKey()).get(id));

        }
      });
      // add to hide
      if (!setOfVarAttrs.isEmpty()) {
        updatedModel.getSubVariantInvisbleAttributeMap().put(entry.getKey(), setOfVarAttrs);
      }
    }

    // add attributes that have become visible
    Map<Long, Set<Long>> diffMap2 = new HashMap<>();
    for (PidcSubVariant subVariant : beforeUpdateModel1.getSubVariantMap().values()) {
      Set<Long> valueDiffSet = CommonUtils.getDifference(subvariantInvisbleAttributeMapBefore.get(subVariant.getId()),
          subvariantInvisbleAttributeMapAfter.get(subVariant.getId()));
      if (!valueDiffSet.isEmpty()) {
        diffMap2.put(subVariant.getId(), valueDiffSet);
      }
    }


    for (Entry<Long, Set<Long>> entry : diffMap2.entrySet()) {
      Set<PidcSubVariantAttribute> setOfVarAttrs = new HashSet<>();
      Map<Long, PidcSubVariantAttribute> mapOfVarAttrs = afterUpdateModel1.getSubVariantAttributeMap(entry.getKey());
      entry.getValue().forEach(id -> {
        if (null != mapOfVarAttrs.get(id)) {
          setOfVarAttrs.add(mapOfVarAttrs.get(id));
        }
      });
      // add to show
      if (!setOfVarAttrs.isEmpty()) {
        updatedModel.getSubVariantVisbleAttributeMap().put(entry.getKey(), setOfVarAttrs);
      }

    }
  }
}
