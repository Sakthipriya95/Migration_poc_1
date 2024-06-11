/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;

/**
 * @author bru2cob
 */
public class A2lParamColumnDataMapper {


  /**
   * Map with UI Col Index as key and value column name
   */
  private final Map<Integer, String> columnIndexFlagMap = new TreeMap<>();

  /**
   * Map with UI Col Index as key and value a2l wp definition id
   */
  private final Map<Integer, Long> columnIndexA2lWpDefVersIdMap = new TreeMap<>();
  /**
   * Map with a2l wp definition id as key and value a2lwpinfobo corresponding to that a2l wp vers
   */
  private Map<Long, A2LWPInfoBO> a2lHandlerMap = new TreeMap<>();
  /**
   * Map with UI Col Index as key and value A2lVariantGroup
   */
  private final Map<Integer, A2lVariantGroup> columnIndexA2lVarGrpMap = new TreeMap<>();
  /**
   * Map with a2l wp def version id as key and value UI Col Index
   */
  private final Map<Long, Integer> columnA2lWpDefVersIdIndexMap = new TreeMap<>();

  /**
   * @param a2lHandlerMap
   */
  public A2lParamColumnDataMapper(final Map<Long, A2LWPInfoBO> a2lHandlerMap) {
    this.a2lHandlerMap = a2lHandlerMap;
  }

  /**
   * @param a2lWpDefVersId wp defn id
   * @param a2lVarGrp a2l var grp corresponding to the column
   */
  public void addNewA2LColumnsIndex(final Long a2lWpDefVersId, final A2lVariantGroup a2lVarGrp) {

    if (this.columnIndexFlagMap.isEmpty()) {
      // Add column index to flag map
      this.columnIndexFlagMap.put(IUIConstants.A2LCMP_FUNC_NAME_COL_INDEX, IUIConstants.FUNCTION_NAME);
      this.columnIndexFlagMap.put(IUIConstants.A2LCMP_FUNC_VERS_COL_INDEX, IUIConstants.FUNCTION_VERS);
      this.columnIndexFlagMap.put(IUIConstants.A2LCMP_BC_COL_INDEX, IUIConstants.BC);
      this.columnIndexFlagMap.put(IUIConstants.A2LCMP_WP_COL_INDEX, IUIConstants.WORK_PACKAGE);
      this.columnIndexFlagMap.put(IUIConstants.A2LCMP_RESP_COL_INDEX, IUIConstants.RESPONSIBILTY);
      this.columnIndexFlagMap.put(IUIConstants.A2LCMP_NAME_AT_CUST_COL_INDEX, IUIConstants.NAME_AT_CUSTOMER);

      // Add column index to A2L WP Defn version map
      this.columnIndexA2lWpDefVersIdMap.put(IUIConstants.A2LCMP_FUNC_NAME_COL_INDEX, a2lWpDefVersId);
      this.columnIndexA2lWpDefVersIdMap.put(IUIConstants.A2LCMP_FUNC_VERS_COL_INDEX, a2lWpDefVersId);
      this.columnIndexA2lWpDefVersIdMap.put(IUIConstants.A2LCMP_BC_COL_INDEX, a2lWpDefVersId);
      this.columnIndexA2lWpDefVersIdMap.put(IUIConstants.A2LCMP_WP_COL_INDEX, a2lWpDefVersId);
      this.columnIndexA2lWpDefVersIdMap.put(IUIConstants.A2LCMP_RESP_COL_INDEX, a2lWpDefVersId);
      this.columnIndexA2lWpDefVersIdMap.put(IUIConstants.A2LCMP_NAME_AT_CUST_COL_INDEX, a2lWpDefVersId);

      // Add column to A2L WP Defn version ID Index map
      this.columnA2lWpDefVersIdIndexMap.put(a2lWpDefVersId, IUIConstants.A2LCMP_FUNC_NAME_COL_INDEX);
      this.columnA2lWpDefVersIdIndexMap.put(a2lWpDefVersId, IUIConstants.A2LCMP_FUNC_VERS_COL_INDEX);
      this.columnA2lWpDefVersIdIndexMap.put(a2lWpDefVersId, IUIConstants.A2LCMP_BC_COL_INDEX);
      this.columnA2lWpDefVersIdIndexMap.put(a2lWpDefVersId, IUIConstants.A2LCMP_WP_COL_INDEX);
      this.columnA2lWpDefVersIdIndexMap.put(a2lWpDefVersId, IUIConstants.A2LCMP_RESP_COL_INDEX);
      this.columnA2lWpDefVersIdIndexMap.put(a2lWpDefVersId, IUIConstants.A2LCMP_NAME_AT_CUST_COL_INDEX);

      // Add column index to A2L Var Grp map
      this.columnIndexA2lVarGrpMap.put(IUIConstants.A2LCMP_FUNC_NAME_COL_INDEX, a2lVarGrp);
      this.columnIndexA2lVarGrpMap.put(IUIConstants.A2LCMP_FUNC_VERS_COL_INDEX, a2lVarGrp);
      this.columnIndexA2lVarGrpMap.put(IUIConstants.A2LCMP_BC_COL_INDEX, a2lVarGrp);
      this.columnIndexA2lVarGrpMap.put(IUIConstants.A2LCMP_WP_COL_INDEX, a2lVarGrp);
      this.columnIndexA2lVarGrpMap.put(IUIConstants.A2LCMP_RESP_COL_INDEX, a2lVarGrp);
      this.columnIndexA2lVarGrpMap.put(IUIConstants.A2LCMP_NAME_AT_CUST_COL_INDEX, a2lVarGrp);

    }
    else {
      List<Integer> keyList = new ArrayList<>(this.columnIndexFlagMap.keySet());
      Integer highestkey = keyList.get(keyList.size() - 1);

      // Add column index to flag map
      this.columnIndexFlagMap.put(highestkey + 1, IUIConstants.FUNCTION_NAME);
      this.columnIndexFlagMap.put(highestkey + 2, IUIConstants.FUNCTION_VERS);
      this.columnIndexFlagMap.put(highestkey + 3, IUIConstants.BC);
      this.columnIndexFlagMap.put(highestkey + 4, IUIConstants.WORK_PACKAGE);
      this.columnIndexFlagMap.put(highestkey + 5, IUIConstants.RESPONSIBILTY);
      this.columnIndexFlagMap.put(highestkey + 6, IUIConstants.NAME_AT_CUSTOMER);

      // Add column index to A2L WP Defn version map
      this.columnIndexA2lWpDefVersIdMap.put(highestkey + 1, a2lWpDefVersId);
      this.columnIndexA2lWpDefVersIdMap.put(highestkey + 2, a2lWpDefVersId);
      this.columnIndexA2lWpDefVersIdMap.put(highestkey + 3, a2lWpDefVersId);
      this.columnIndexA2lWpDefVersIdMap.put(highestkey + 4, a2lWpDefVersId);
      this.columnIndexA2lWpDefVersIdMap.put(highestkey + 5, a2lWpDefVersId);
      this.columnIndexA2lWpDefVersIdMap.put(highestkey + 6, a2lWpDefVersId);

      // Add column to A2L WP Defn version ID Index map
      this.columnA2lWpDefVersIdIndexMap.put(a2lWpDefVersId, highestkey + 1);
      this.columnA2lWpDefVersIdIndexMap.put(a2lWpDefVersId, highestkey + 2);
      this.columnA2lWpDefVersIdIndexMap.put(a2lWpDefVersId, highestkey + 3);
      this.columnA2lWpDefVersIdIndexMap.put(a2lWpDefVersId, highestkey + 4);
      this.columnA2lWpDefVersIdIndexMap.put(a2lWpDefVersId, highestkey + 5);
      this.columnA2lWpDefVersIdIndexMap.put(a2lWpDefVersId, highestkey + 6);

      // Add column index to A2L Var Grp map
      this.columnIndexA2lVarGrpMap.put(highestkey + 1, a2lVarGrp);
      this.columnIndexA2lVarGrpMap.put(highestkey + 2, a2lVarGrp);
      this.columnIndexA2lVarGrpMap.put(highestkey + 3, a2lVarGrp);
      this.columnIndexA2lVarGrpMap.put(highestkey + 4, a2lVarGrp);
      this.columnIndexA2lVarGrpMap.put(highestkey + 5, a2lVarGrp);
      this.columnIndexA2lVarGrpMap.put(highestkey + 6, a2lVarGrp);
    }

  }


  /**
   * @return the a2lHandlerMap
   */
  public Map<Long, A2LWPInfoBO> getA2lHandlerMap() {
    return this.a2lHandlerMap;
  }


  /**
   * @param a2lHandlerMap the a2lHandlerMap to set
   */
  public void setA2lHandlerMap(final Map<Long, A2LWPInfoBO> a2lHandlerMap) {
    this.a2lHandlerMap = a2lHandlerMap;
  }


  /**
   * @return the columnIndexFlagMap
   */
  public Map<Integer, String> getColumnIndexFlagMap() {
    return this.columnIndexFlagMap;
  }


  /**
   * @return the columnIndexA2lWpDefVersIdMap
   */
  public Map<Integer, Long> getColumnIndexA2lWpDefVersIdMap() {
    return this.columnIndexA2lWpDefVersIdMap;
  }

  /**
   * @return the columnIndexA2lVarGrpMap
   */
  public final Map<Integer, A2lVariantGroup> getColumnIndexA2lVarGrpMap() {
    return this.columnIndexA2lVarGrpMap;
  }


  /**
   * @return the columnA2lWpDefVersIdIndexMap
   */
  public final Map<Long, Integer> getColumnA2lWpDefVersIdIndexMap() {
    return this.columnA2lWpDefVersIdIndexMap;
  }
}
