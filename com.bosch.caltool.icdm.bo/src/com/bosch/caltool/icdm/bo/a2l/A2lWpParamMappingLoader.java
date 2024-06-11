package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.ParamProperties;


/**
 * Loader class for A2lWpParamMapping
 *
 * @author pdh2cob
 */
public class A2lWpParamMappingLoader extends AbstractBusinessObject<A2lWpParamMapping, TA2lWpParamMapping> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public A2lWpParamMappingLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.A2L_WP_PARAM_MAPPING, TA2lWpParamMapping.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected A2lWpParamMapping createDataObject(final TA2lWpParamMapping entity) throws DataException {
    A2lWpParamMapping object = new A2lWpParamMapping();

    setCommonFields(object, entity);
    // Parameter details
    object.setParamId(entity.getTParameter().getId());
    object.setName(entity.getTParameter().getName());
    object.setDescription(entity.getTParameter().getLongname());
    // set workpackage responsibility id
    object.setWpRespId(entity.getTA2lWpResponsibility().getWpRespId());
    // set wp defn version id
    object.setWpDefnVersionId(entity.getTA2lWpResponsibility().gettA2lWpDefnVersion().getWpDefnVersId());

    object
        .setParA2lRespId(entity.getTA2lResponsibility() == null ? null : entity.getTA2lResponsibility().getA2lRespId());

    // set WP name at customer
    object.setWpNameCust(entity.getWpNameCust());
    // Inherit flags
    object.setWpNameCustInherited(yOrNToBoolean(entity.getWpNameCustInheritedFlag()));
    object.setWpRespInherited(yOrNToBoolean(entity.getWpRespInheritedFlag()));
    return object;
  }

  /**
   * @param a2lWpRespId as a2lWpResp Id
   * @return Set<A2lWpParamMapping>
   * @throws DataException as exception
   */
  public Set<A2lWpParamMapping> getWpParamMappingForWPResp(Long a2lWpRespId) throws DataException{
    Set<A2lWpParamMapping> a2lWpParamMapSet = new HashSet<>();
    TA2lWpResponsibility ta2lWpResponsibility = new A2lWpResponsibilityLoader(getServiceData()).getEntityObject(a2lWpRespId);
    for (TA2lWpParamMapping paramMapping : ta2lWpResponsibility.getTA2lWpParamMappings()) {
      a2lWpParamMapSet.add(createDataObject(paramMapping));
    }
    return a2lWpParamMapSet;
  }
  
  /**
   * @param a2lWpRespId as a2lWpResp Id
   * @return Set<String>
   * @throws DataException as exception
   */
  public Set<String> getWpParamMapNameForWPResp(Long a2lWpRespId) throws DataException{
    Set<String> a2lWpParamMapSet = new HashSet<>();
    TA2lWpResponsibility ta2lWpResponsibility = new A2lWpResponsibilityLoader(getServiceData()).getEntityObject(a2lWpRespId);
    for (TA2lWpParamMapping paramMapping : ta2lWpResponsibility.getTA2lWpParamMappings()) {
      a2lWpParamMapSet.add(createDataObject(paramMapping).getName());
    }
    return a2lWpParamMapSet;
  }
  
  /**
   * Get all A2lWpParamMapping based on A2lWpDefVersion id
   *
   * @param wpDefVersId A2lWpDefVersion id
   * @return A2lWpParamMappingModel
   * @throws DataException exception
   */
  public A2lWpParamMappingModel getAllByWpDefVersId(final Long wpDefVersId) throws DataException {
    A2lWpDefnVersionLoader a2lWpDefVersLdr = new A2lWpDefnVersionLoader(getServiceData());
    a2lWpDefVersLdr.validateId(wpDefVersId);
    // Create return model
    A2lWpParamMappingModel model = new A2lWpParamMappingModel();
    model.setSelectedWpDefnVersionId(wpDefVersId);


    Map<Long, A2lWpParamMapping> retMap = new HashMap<>();

    for (TA2lWpResponsibility dbWpResp : a2lWpDefVersLdr.getEntityObject(wpDefVersId).getTA2lWpResponsibility()) {
      // Refresh the parent of A2l Wp Param mapping
      // TODO Temporary work around
      getEntMgr().refresh(dbWpResp);
      List<TA2lWpParamMapping> dbWpParamMapList = dbWpResp.getTA2lWpParamMappings();
      for (TA2lWpParamMapping entity : dbWpParamMapList) {
        A2lWpParamMapping paramMapping = createDataObject(entity);
        retMap.put(paramMapping.getId(), paramMapping);
      }
    }
    // Set mappings
    model.setA2lWpParamMapping(retMap);

    return model;
  }

  /**
   * Gets the param by function.
   *
   * @param a2lFileId the a 2 l file id
   * @param funcSet the func set
   * @return the param by function
   * @throws IcdmException
   */
  public Map<String, Set<A2lWpParamMapping>> getParamByFunction(final Long a2lFileId, final Set<String> funcSet)
      throws IcdmException {

    A2LFileInfo a2lFileInfo = new A2LFileInfoProvider(getServiceData())
        .fetchA2LFileInfo(new A2LFileInfoLoader(getServiceData()).getDataObjectByID(a2lFileId));

    Map<String, Set<A2lWpParamMapping>> ret = new HashMap<>();

    Map<String, ParamProperties> props = new ParameterLoader(getServiceData()).fetchAllA2lParamProps(a2lFileId);
    for (String funcName : funcSet) {
      ret.put(funcName, getParamSetForFunction(funcName, a2lFileInfo, props));
    }
    return ret;
  }

  /**
   * @param a2lFileInfo
   * @param function
   * @return
   */
  private Set<A2lWpParamMapping> getParamSetForFunction(final String funcName, final A2LFileInfo a2lFileInfo,
      final Map<String, ParamProperties> props) {
    Function function = a2lFileInfo.getAllModulesFunctions().get(funcName);
    Set<A2lWpParamMapping> mappingSet = new HashSet<>();
    if (function != null) {
      List<DefCharacteristic> defCharRefList = function.getDefCharRefList();
      // Get parameter names
      if (defCharRefList != null) {
        defCharRefList.stream().map(DefCharacteristic::getName).forEach(param -> {
          A2lWpParamMapping mapping = new A2lWpParamMapping();
          ParamProperties paramProp = props.get(param);
          mapping.setParamId(paramProp.getId());
          mapping.setName(param);
          mappingSet.add(mapping);
        });
      }
    }
    return mappingSet;
  }

  /**
   * Gets the mapping by wp def vers id.
   *
   * @param wpDefVersId the wp def vers id
   * @return the mapping by wp def vers id , key - param id , value - a2lwp param mapping
   * @throws DataException the data exception
   */
  public Map<Long, A2lWpParamMapping> getMappingByWpDefVersId(final Long wpDefVersId) throws DataException {

    A2lWpDefnVersionLoader a2lWpDefVersLdr = new A2lWpDefnVersionLoader(getServiceData());
    a2lWpDefVersLdr.validateId(wpDefVersId);

    Map<Long, A2lWpParamMapping> retMap = new HashMap<>();

    List<TA2lWpResponsibility> a2lWpResps = a2lWpDefVersLdr.getEntityObject(wpDefVersId).getTA2lWpResponsibility();

    if (CommonUtils.isNotEmpty(a2lWpResps)) {
      for (TA2lWpResponsibility dbWpResp : a2lWpResps) {
        List<TA2lWpParamMapping> dbWpParamMapList = dbWpResp.getTA2lWpParamMappings();
        if (dbWpParamMapList != null) {
          for (TA2lWpParamMapping entity : dbWpParamMapList) {
            A2lWpParamMapping paramMapping = createDataObject(entity);
            retMap.put(paramMapping.getId(), paramMapping);
          }
        }
      }
    }
    return retMap;

  }


  /**
   * Gets the mapping by wp def vers id. all the mapping along with Varaint group mapping
   *
   * @param wpDefVersId the wp def vers id
   * @return the mapping by wp def vers id , key - param id , value - Map of A2l wp param map id and value
   *         A2lparamMapping Object
   * @throws DataException the data exception
   */
  public Map<Long, Map<Long, A2lWpParamMapping>> getAllMappingByWpDefVersId(final Long wpDefVersId)
      throws DataException {

    A2lWpDefnVersionLoader a2lWpDefVersLdr = new A2lWpDefnVersionLoader(getServiceData());
    a2lWpDefVersLdr.validateId(wpDefVersId);

    Map<Long, Map<Long, A2lWpParamMapping>> retMap = new HashMap<>();

    List<TA2lWpResponsibility> a2lWpResps = a2lWpDefVersLdr.getEntityObject(wpDefVersId).getTA2lWpResponsibility();

    if (CommonUtils.isNotEmpty(a2lWpResps)) {
      for (TA2lWpResponsibility dbWpResp : a2lWpResps) {
        List<TA2lWpParamMapping> dbWpParamMapList = dbWpResp.getTA2lWpParamMappings();
        if (dbWpParamMapList != null) {
          for (TA2lWpParamMapping entity : dbWpParamMapList) {
            A2lWpParamMapping paramMapping = createDataObject(entity);
            Map<Long, A2lWpParamMapping> mappingMap = retMap.get(paramMapping.getParamId());
            if (mappingMap == null) {
              mappingMap = new HashMap<>();
              retMap.put(paramMapping.getParamId(), mappingMap);
            }
            mappingMap.put(paramMapping.getId(), paramMapping);
          }
        }
      }
    }
    return retMap;

  }

  /**
   * Gets the mapping by wp def vers id.
   *
   * @param wpDefVersId the wp def vers id
   * @return the mapping by wp def vers id , key - param id , value - a2lwp param mapping
   * @throws DataException the data exception
   */
  public ParamMappingRespBo getParamMapRespBo(final Long wpDefVersId) throws DataException {

    A2lWpDefnVersionLoader a2lWpDefVersLdr = new A2lWpDefnVersionLoader(getServiceData());
    a2lWpDefVersLdr.validateId(wpDefVersId);


    List<TA2lWpResponsibility> a2lWpResps = a2lWpDefVersLdr.getEntityObject(wpDefVersId).getTA2lWpResponsibility();
    ParamMappingRespBo paramWpRespBo = new ParamMappingRespBo();

    A2lWpResponsibilityLoader wpRespLoader = new A2lWpResponsibilityLoader(getServiceData());

    A2lVariantGroupLoader a2lVarGrpLoader = new A2lVariantGroupLoader(getServiceData());

    if (CommonUtils.isNotEmpty(a2lWpResps)) {
      updateParamWpRespBo(a2lWpResps, paramWpRespBo, wpRespLoader, a2lVarGrpLoader);
    }
    return paramWpRespBo;

  }

  /**
   * @param a2lWpResps
   * @param paramWpRespBo
   * @param wpRespLoader
   * @param a2lVarGrpLoader
   * @throws DataException
   */
  private void updateParamWpRespBo(List<TA2lWpResponsibility> a2lWpResps, ParamMappingRespBo paramWpRespBo,
      A2lWpResponsibilityLoader wpRespLoader, A2lVariantGroupLoader a2lVarGrpLoader) throws DataException {
    for (TA2lWpResponsibility dbWpResp : a2lWpResps) {
      A2lWpResponsibility wpResp = wpRespLoader.createDataObject(dbWpResp);

      // Get all the Wp resps
      paramWpRespBo.getWpRespMap().put(dbWpResp.getWpRespId(), wpResp);

      // Populate Varaint group map
      if ((wpResp.getVariantGrpId() != null) && (dbWpResp.getVariantGroup() != null)) {

        paramWpRespBo.getA2lVarGrpMap().put(wpResp.getVariantGrpId(),
            a2lVarGrpLoader.createDataObject(dbWpResp.getVariantGroup()));
      }


      List<TA2lWpParamMapping> dbWpParamMapList = dbWpResp.getTA2lWpParamMappings();
      if (dbWpParamMapList != null) {
        for (TA2lWpParamMapping entity : dbWpParamMapList) {
          A2lWpParamMapping paramMapping = createDataObject(entity);
          // Add mapping object
          paramWpRespBo.getWpParamMapping().put(paramMapping.getId(), paramMapping);
          List<Long> mappingIdList = paramWpRespBo.getParamAndMappingMap().get(paramMapping.getParamId());

          if (mappingIdList == null) {
            mappingIdList = new ArrayList<>();
          }
          mappingIdList.add(paramMapping.getId());
          // Add the mapping list map
          paramWpRespBo.getParamAndMappingMap().put(paramMapping.getParamId(), mappingIdList);
        }
      }
    }
  }
}
