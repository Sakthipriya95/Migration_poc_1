package com.bosch.caltool.icdm.bo.a2l;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lVariantGroup;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;


/**
 * Loader class for A2lVariantGroup
 *
 * @author pdh2cob
 */
public class A2lVariantGroupLoader extends AbstractBusinessObject<A2lVariantGroup, TA2lVariantGroup> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public A2lVariantGroupLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.A2L_VARIANT_GROUP, TA2lVariantGroup.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected A2lVariantGroup createDataObject(final TA2lVariantGroup entity) throws DataException {
    A2lVariantGroup object = new A2lVariantGroup();

    setCommonFields(object, entity);

    object.setName(entity.getGroupName());
    object.setDescription(entity.getGroupDesc());
    // set workpackage defn version id
    object.setWpDefnVersId(
        entity.gettA2lWpDefnVersion() == null ? null : entity.gettA2lWpDefnVersion().getWpDefnVersId());

    return object;
  }


  /**
   * Get all A2lVariantGroup records in system
   *
   * @param wpDefVersId WP Definition version ID
   * @return Map. Key - id, Value - A2lVariantGroup object
   * @throws DataException error while retrieving data
   */
  public Map<Long, A2lVariantGroup> getA2LVarGrps(final Long wpDefVersId) throws DataException {
    A2lWpDefnVersionLoader defVersLdr = new A2lWpDefnVersionLoader(getServiceData());
    defVersLdr.validateId(wpDefVersId);
    Set<TA2lVariantGroup> varGrpList = defVersLdr.getEntityObject(wpDefVersId).getTA2lVariantGroups();
    Map<Long, A2lVariantGroup> retMap = new ConcurrentHashMap<>();
    if (CommonUtils.isNotEmpty(varGrpList)) {
      for (TA2lVariantGroup entity : varGrpList) {
        retMap.put(entity.getA2lVarGrpId(), createDataObject(entity));
      }
    }
    return retMap;
  }

  /**
   * @param wpDefVersId wp defn vers id
   * @param variantId pidc var id
   * @return var grp object
   * @throws DataException exception
   */
  public A2lVariantGroup getVariantGroup(final Long wpDefVersId, final Long variantId) throws DataException {

    Map<Long, A2lVariantGroup> varGrpMap = new A2lVariantGroupLoader(getServiceData()).getA2LVarGrps(wpDefVersId);
    for (A2lVariantGroup a2lVariantGroup : varGrpMap.values()) {
      Map<Long, A2lVarGrpVariantMapping> varGrpmapping =
          new A2lVarGrpVariantMappingLoader(getServiceData()).getA2LVarGrps(a2lVariantGroup.getId());
      for (A2lVarGrpVariantMapping mapping : varGrpmapping.values()) {
        if (mapping.getVariantId().equals(variantId)) {
          return a2lVariantGroup;
        }
      }
    }
    return null;
  }

}
