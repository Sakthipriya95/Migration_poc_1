/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.a2l;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;

/**
 * @author rgo7cob
 */
public class ParamWpRespResolver {


  // input maps
  private final Map<Long, A2lVariantGroup> a2lVarGrpMap;
  private final Map<Long, A2lWpParamMapping> a2lWParamInfoMap;
  private final Map<Long, A2lWpResponsibility> wpRespMap;


  // internal maps

  private final Map<Long, A2lWpResponsibility> pidcWpRespMap = new HashMap<>();

  /**
   * Key - variant group id and value - map of (Key - A2l Work package id and value - A2lWpResponsibility object)
   */
  private final Map<Long, Map<Long, A2lWpResponsibility>> varGrpRespMap = new HashMap<>();


  /**
   * key is the wp resp id and value is the list of a2l param mapping
   */
  private final Map<Long, List<A2lWpParamMapping>> wpRespParamMapping = new HashMap<>();


  // output maps


  /**
   * key is the wp id and value is list<resp id>
   */
  private final Map<Long, List<Long>> respListForWp = new HashMap<>();
  /**
   * key is the resp id and value is list<wp id>
   */
  private final Map<Long, List<Long>> wpListForResp = new HashMap<>();


  // below two maps to be used in Label assignment page for creating virtual records

  /**
   * key is the Variant group id and value is set<paramid> fow wchich virtual record needs to be created
   */
  private final Map<Long, Set<Long>> paramsWithRespModified = new HashMap<>();


  /**
   * key is the Variant group id and value is the map<paramid, paramwpResp>
   */
  private Map<Long, Map<Long, ParamWpResponsibility>> varGrpParamRespMap = new HashMap<>();


  /**
   * paraming mappimg map at various levels- Key vargroup id , value - Param id , A2lWpParamMapping object
   */
  private final Map<Long, Map<Long, A2lWpParamMapping>> paramMappingAtVarLevel = new HashMap<>();


  /**
   * @param a2lVarGrpMap a2lVarGrpMap
   * @param wpRespMap wpRespMap
   * @param a2lWParamInfoMap a2lWParamInfoMap
   */
  public ParamWpRespResolver(final Map<Long, A2lVariantGroup> a2lVarGrpMap,
      final Map<Long, A2lWpResponsibility> wpRespMap, final Map<Long, A2lWpParamMapping> a2lWParamInfoMap) {
    this.a2lVarGrpMap = a2lVarGrpMap;
    this.a2lWParamInfoMap = a2lWParamInfoMap;
    this.wpRespMap = wpRespMap;


    initializewpRespParamMapping();

  }


  /**
   * method to fill param mappings for given the wp resp
   */
  private void initializewpRespParamMapping() {
    for (A2lWpResponsibility wpResponsibility : this.wpRespMap.values()) {
      fillVarGrpMaps(wpResponsibility);
      fillWpRespMaps(wpResponsibility);

      for (A2lWpParamMapping a2lWpParamMapping : this.a2lWParamInfoMap.values()) {
        if (wpResponsibility.getId().equals(a2lWpParamMapping.getWpRespId())) {
          Long variantGrpId = wpResponsibility.getVariantGrpId();

          Map<Long, A2lWpParamMapping> paramMapping = this.paramMappingAtVarLevel.get(variantGrpId);
          if (paramMapping == null) {
            paramMapping = new HashMap<>();
          }
          paramMapping.put(a2lWpParamMapping.getParamId(), a2lWpParamMapping);
          this.paramMappingAtVarLevel.put(variantGrpId, paramMapping);


          List<A2lWpParamMapping> mappingList = this.wpRespParamMapping.get(wpResponsibility.getId());
          if (mappingList == null) {
            mappingList = new ArrayList<>();
          }
          mappingList.add(a2lWpParamMapping);
          this.wpRespParamMapping.put(wpResponsibility.getId(), mappingList);
        }
      }

    }

  }


  /**
   * @param wpResponsibility
   */
  private void fillWpRespMaps(final A2lWpResponsibility wpResponsibility) {
    List<Long> respList = this.respListForWp.get(wpResponsibility.getA2lWpId());

    if (respList == null) {
      respList = new ArrayList<>();
    }
    respList.add(wpResponsibility.getA2lRespId());

    this.respListForWp.put(wpResponsibility.getA2lWpId(), respList);


    List<Long> wpList = this.wpListForResp.get(wpResponsibility.getA2lRespId());

    if (wpList == null) {
      wpList = new ArrayList<>();
    }
    wpList.add(wpResponsibility.getA2lWpId());

    this.wpListForResp.put(wpResponsibility.getA2lWpId(), wpList);

  }


  /**
   * @param wpResponsibility
   */
  private void fillVarGrpMaps(final A2lWpResponsibility wpResponsibility) {
    if (wpResponsibility.getVariantGrpId() == null) {
      this.pidcWpRespMap.put(wpResponsibility.getA2lWpId(), wpResponsibility);
    }
    else {
      Map<Long, A2lWpResponsibility> respMap = this.varGrpRespMap.get(wpResponsibility.getVariantGrpId());
      if (respMap == null) {
        respMap = new HashMap<>();
      }
      respMap.put(wpResponsibility.getA2lWpId(), wpResponsibility);
      this.varGrpRespMap.put(wpResponsibility.getVariantGrpId(), respMap);

    }
  }


  /**
   * @return
   */
  public Map<Long, Map<Long, ParamWpResponsibility>> fillParamRespMaps() {
    for (A2lVariantGroup varGroup : this.a2lVarGrpMap.values()) {
      this.varGrpParamRespMap.put(varGroup.getId(), getRespForParam(varGroup.getId()));
    }

    return this.varGrpParamRespMap;
  }


  /**
   * made public for review process
   *
   * @param varGrpId varGrpId
   * @return the map with key Param id and value param resp object
   */
  public Set<A2lWPRespModel> getWPRespForVariant(final Long varGrpId) {
    Set<A2lWPRespModel> a2lWPRespModelSet = new HashSet<>();
    // Iterate through the pidc resp Map
    for (A2lWpResponsibility wpResponsibility : this.pidcWpRespMap.values()) {
      if (this.wpRespParamMapping.get(wpResponsibility.getId()) != null) {

        // Itrate the param mapping map
        for (A2lWpParamMapping a2lWpParamMapping : this.wpRespParamMapping.get(wpResponsibility.getId())) {

          A2lWPRespModel a2lWPRespModel = new A2lWPRespModel();

          if (!a2lWpParamMapping.isWpRespInherited()) {
            fetchWpRespModelForUnInherWpResp(varGrpId, wpResponsibility, a2lWpParamMapping, a2lWPRespModel);
          }
          else if (varGrpId != null) {
            fetchWpRespModelForVarGrp(varGrpId, wpResponsibility, a2lWPRespModel);
          }
          else {
            a2lWPRespModel.setA2lRespId(wpResponsibility.getA2lRespId());
          }
          a2lWPRespModel.setInheritedFlag(a2lWpParamMapping.isWpRespInherited());
          a2lWPRespModel.setA2lWpId(wpResponsibility.getA2lWpId());
          if (CommonUtils.isNull(a2lWPRespModel.getWpRespId())) {
            a2lWPRespModel.setWpRespId(wpResponsibility.getId());
          }
          a2lWPRespModelSet.add(a2lWPRespModel);
        }
      }
    }

    getWPRespForVariantGroup(varGrpId, a2lWPRespModelSet);

    return a2lWPRespModelSet;
  }


  /**
   * @param varGrpId
   * @param wpResponsibility
   * @param a2lWPRespModel
   */
  private void fetchWpRespModelForVarGrp(final Long varGrpId, final A2lWpResponsibility wpResponsibility,
      final A2lWPRespModel a2lWPRespModel) {
    Map<Long, A2lWpResponsibility> respMap = this.varGrpRespMap.get(varGrpId);
    if (respMap != null) {
      A2lWpResponsibility a2lWpResponsibility = respMap.get(wpResponsibility.getA2lWpId());

      if (a2lWpResponsibility != null) {
        a2lWPRespModel.setA2lRespId(a2lWpResponsibility.getA2lRespId());
        // setting the WPRESPID if the a2lrespid is taken from variant group
        a2lWPRespModel.setWpRespId(a2lWpResponsibility.getId());
      }
      else {
        a2lWPRespModel.setA2lRespId(wpResponsibility.getA2lRespId());
      }
    }
    else {
      a2lWPRespModel.setA2lRespId(wpResponsibility.getA2lRespId());
    }
  }


  /**
   * @param varGrpId
   * @param wpResponsibility
   * @param a2lWpParamMapping
   * @param a2lWPRespModel
   */
  private void fetchWpRespModelForUnInherWpResp(final Long varGrpId, final A2lWpResponsibility wpResponsibility,
      final A2lWpParamMapping a2lWpParamMapping, final A2lWPRespModel a2lWPRespModel) {
    a2lWPRespModel.setA2lRespId(a2lWpParamMapping.getParA2lRespId());
    // Need to add this case to make sure that Virtual records are always present even if Wp inherites is No at
    // the parent level
    if (varGrpId != null) {
      Map<Long, A2lWpResponsibility> respMap = this.varGrpRespMap.get(varGrpId);
      if (respMap != null) {
        A2lWpResponsibility a2lWpResponsibility = respMap.get(wpResponsibility.getA2lWpId());

        if (a2lWpResponsibility != null) {
          a2lWPRespModel.setA2lRespId(a2lWpResponsibility.getA2lRespId());
        }
        else {
          if (!a2lWpParamMapping.isWpRespInherited()) {
            a2lWPRespModel.setA2lRespId(a2lWpParamMapping.getParA2lRespId());
          }
          else {
            a2lWPRespModel.setA2lRespId(wpResponsibility.getA2lRespId());
          }
        }
      }
      else {
        a2lWPRespModel.setA2lRespId(wpResponsibility.getA2lRespId());
      }

    }
  }


  /**
   * @param varGrpId
   * @param a2lWPRespModelSet
   */
  private void getWPRespForVariantGroup(final Long varGrpId, final Set<A2lWPRespModel> a2lWPRespModelSet) {
    if (varGrpId != null) {
      Map<Long, A2lWpResponsibility> varRespMap = this.varGrpRespMap.get(varGrpId);
      if (varRespMap != null) {

        for (A2lWpResponsibility varResp : varRespMap.values()) {
          if (this.wpRespParamMapping.get(varResp.getId()) != null) {
            addParamMappToWPRespModelSet(a2lWPRespModelSet, varResp);
          }
        }
      }

    }
  }


  /**
   * @param a2lWPRespModelSet
   * @param varResp
   */
  private void addParamMappToWPRespModelSet(final Set<A2lWPRespModel> a2lWPRespModelSet,
      final A2lWpResponsibility varResp) {
    for (A2lWpParamMapping a2lWpParamMapping : this.wpRespParamMapping.get(varResp.getId())) {
      A2lWPRespModel paramWpResp = new A2lWPRespModel();
      paramWpResp.setA2lRespId(
          !a2lWpParamMapping.isWpRespInherited() ? a2lWpParamMapping.getParA2lRespId() : varResp.getA2lRespId());
      paramWpResp.setInheritedFlag(a2lWpParamMapping.isWpRespInherited());
      paramWpResp.setA2lWpId(varResp.getA2lWpId());
      paramWpResp.setWpRespId(varResp.getId());
      a2lWPRespModelSet.add(paramWpResp);
    }
  }


  /**
   * made public for review process
   *
   * @param varGrpId varGrpId
   * @return the map with key Param id and value param resp object
   */
  public Map<Long, ParamWpResponsibility> getRespForParam(final Long varGrpId) {


    Map<Long, ParamWpResponsibility> paramRespMap = new HashMap<>();


    // Iterate through the pidc resp Map

    for (A2lWpResponsibility wpResponsibility : this.pidcWpRespMap.values()) {

      if (this.wpRespParamMapping.get(wpResponsibility.getId()) != null) {

        // Itrate the param mapping map
        for (A2lWpParamMapping a2lWpParamMapping : this.wpRespParamMapping.get(wpResponsibility.getId())) {


          ParamWpResponsibility paramWpResp = new ParamWpResponsibility();
          paramWpResp.setParamId(a2lWpParamMapping.getParamId());
          paramWpResp.setParamName(a2lWpParamMapping.getName());

          if (!a2lWpParamMapping.isWpRespInherited()) {
            fetchParamWpRespForUnInherWpResp(varGrpId, wpResponsibility, a2lWpParamMapping, paramWpResp);
          }
          else if (varGrpId != null) {
            fetchParamWpRespForVarGrp(varGrpId, wpResponsibility, paramWpResp);
          }
          else {
            paramWpResp.setRespId(wpResponsibility.getA2lRespId());
          }
          paramWpResp.setWpId(wpResponsibility.getA2lWpId());
          paramWpResp.setWpRespId(wpResponsibility.getId());
          paramRespMap.put(paramWpResp.getParamId(), paramWpResp);

          setRespChangedParams(varGrpId, a2lWpParamMapping, paramWpResp);
        }
      }
    }

    getparamWpRespForVariantGroup(varGrpId, paramRespMap);

    return paramRespMap;
  }


  /**
   * @param varGrpId
   * @param paramRespMap
   */
  private void getparamWpRespForVariantGroup(final Long varGrpId, final Map<Long, ParamWpResponsibility> paramRespMap) {
    if (varGrpId != null) {
      Map<Long, A2lWpResponsibility> varRespMap = this.varGrpRespMap.get(varGrpId);
      if (varRespMap != null) {

        for (A2lWpResponsibility varResp : varRespMap.values()) {
          if (this.wpRespParamMapping.get(varResp.getId()) != null) {
            addParamMappToParamRespMap(varGrpId, paramRespMap, varResp);
          }
        }
      }

    }
  }


  /**
   * @param varGrpId
   * @param paramRespMap
   * @param varResp
   */
  private void addParamMappToParamRespMap(final Long varGrpId, final Map<Long, ParamWpResponsibility> paramRespMap,
      final A2lWpResponsibility varResp) {
    for (A2lWpParamMapping a2lWpParamMapping : this.wpRespParamMapping.get(varResp.getId())) {
      ParamWpResponsibility paramWpResp = new ParamWpResponsibility();
      paramWpResp.setParamId(a2lWpParamMapping.getParamId());
      paramWpResp.setParamName(a2lWpParamMapping.getName());
      if (!a2lWpParamMapping.isWpRespInherited()) {
        paramWpResp.setRespId(a2lWpParamMapping.getParA2lRespId());
      }
      else {
        paramWpResp.setRespId(varResp.getA2lRespId());
      }
      paramWpResp.setWpId(varResp.getA2lWpId());
      paramWpResp.setWpRespId(varResp.getId());
      paramRespMap.put(paramWpResp.getParamId(), paramWpResp);

      setRespChangedParams(varGrpId, a2lWpParamMapping, paramWpResp);
    }
  }


  /**
   * @param varGrpId
   * @param wpResponsibility
   * @param paramWpResp
   */
  private void fetchParamWpRespForVarGrp(final Long varGrpId, final A2lWpResponsibility wpResponsibility,
      final ParamWpResponsibility paramWpResp) {
    Map<Long, A2lWpResponsibility> respMap = this.varGrpRespMap.get(varGrpId);
    if (respMap != null) {
      A2lWpResponsibility a2lWpResponsibility = respMap.get(wpResponsibility.getA2lWpId());

      if (a2lWpResponsibility != null) {
        paramWpResp.setRespId(a2lWpResponsibility.getA2lRespId());

      }
      else {
        paramWpResp.setRespId(wpResponsibility.getA2lRespId());
      }
    }
    else {
      paramWpResp.setRespId(wpResponsibility.getA2lRespId());
    }
  }


  /**
   * @param varGrpId
   * @param wpResponsibility
   * @param a2lWpParamMapping
   * @param paramWpResp
   */
  private void fetchParamWpRespForUnInherWpResp(final Long varGrpId, final A2lWpResponsibility wpResponsibility,
      final A2lWpParamMapping a2lWpParamMapping, final ParamWpResponsibility paramWpResp) {
    // For default WP, set the parA2lRespId which is customized resp Id
    paramWpResp.setRespId(a2lWpParamMapping.getParA2lRespId());
    // Need to add this case to make sure that Virtual records are always present even if Wp inherites is No at
    // the parent level
    if (varGrpId != null) {
      Map<Long, A2lWpResponsibility> respMap = this.varGrpRespMap.get(varGrpId);
      if (respMap != null) {
        A2lWpResponsibility a2lWpResponsibility = respMap.get(wpResponsibility.getA2lWpId());

        if (a2lWpResponsibility != null) {
          paramWpResp.setRespId(a2lWpResponsibility.getA2lRespId());
        }
        else {
          if (!a2lWpParamMapping.isWpRespInherited()) {
            paramWpResp.setRespId(a2lWpParamMapping.getParA2lRespId());
          }
          else {
            paramWpResp.setRespId(wpResponsibility.getA2lRespId());
          }
        }
      }
      else {
        paramWpResp.setRespId(wpResponsibility.getA2lRespId());
      }
    }
  }


  /**
   * @param varGrpId
   * @param a2lWpParamMapping
   * @param paramWpResp
   */
  private void setRespChangedParams(final Long varGrpId, final A2lWpParamMapping a2lWpParamMapping,
      final ParamWpResponsibility paramWpResp) {

    // Remove the parameter for virtual record creation if the a2lWpParamMapping inhertied flag is N. If it is N then do
    // not add virtual record
    if (!a2lWpParamMapping.isWpRespInherited()) {
      Set<Long> paramIdSet = this.paramsWithRespModified.get(varGrpId);
      if (paramIdSet != null) {
        paramIdSet.remove(paramWpResp.getParamId());
        return;
      }
    }
    Long respInDb = a2lWpParamMapping.getParA2lRespId();
    if (respInDb == null) {
      Long wpRespId = a2lWpParamMapping.getWpRespId();
      A2lWpResponsibility a2lWpResponsibility = this.wpRespMap.get(wpRespId);
      respInDb = a2lWpResponsibility.getA2lRespId();
    }
    if (!paramWpResp.getRespId().equals(respInDb)) {
      Set<Long> paramIdSet = this.paramsWithRespModified.get(varGrpId);
      if (paramIdSet == null) {
        paramIdSet = new HashSet<>();
      }
      paramIdSet.add(paramWpResp.getParamId());
      this.paramsWithRespModified.put(varGrpId, paramIdSet);
    }
  }


  /**
   * @return the respListForWp
   */
  public Map<Long, List<Long>> getRespListForWp() {
    return this.respListForWp;
  }


  /**
   * @return the wpListForResp
   */
  public Map<Long, List<Long>> getWpListForResp() {
    return this.wpListForResp;
  }


  /**
   * @return the varGrpParamRespMap
   */
  public Map<Long, Map<Long, ParamWpResponsibility>> getVarGrpParamRespMap() {
    return this.varGrpParamRespMap;
  }


  /**
   * @param varGrpParamRespMap the varGrpParamRespMap to set
   */
  public void setVarGrpParamRespMap(final Map<Long, Map<Long, ParamWpResponsibility>> varGrpParamRespMap) {
    this.varGrpParamRespMap = varGrpParamRespMap;
  }


  /**
   * @return the paramsWithRespModified
   */
  public Map<Long, Set<Long>> getParamsWithRespModified() {
    return this.paramsWithRespModified;
  }


  /**
   * @return the paramMappingAtVarLevel
   */
  public Map<Long, Map<Long, A2lWpParamMapping>> getParamMappingAtVarLevel() {
    return this.paramMappingAtVarLevel;
  }


  /**
   * @return the varGrpRespMap
   */
  public Map<Long, Map<Long, A2lWpResponsibility>> getVarGrpRespMap() {
    return this.varGrpRespMap;
  }


  /**
   * @return the wpRespParamMapping
   */
  public Map<Long, List<A2lWpParamMapping>> getWpRespParamMapping() {
    return this.wpRespParamMapping;
  }


}
