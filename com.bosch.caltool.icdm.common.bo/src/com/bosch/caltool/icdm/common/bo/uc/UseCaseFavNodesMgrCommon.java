/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.uc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.model.user.User;

/**
 * @author dmr1cob
 */
public class UseCaseFavNodesMgrCommon {

  /**
   * Apic User to which the private usecase item belongs to
   */
  private final User apicUser;

  /**
   * set of root nodes
   */
  private SortedSet<FavUseCaseItemNodeCommon> rootNodeSet;

  /**
   * true if this instance is a manager for project usecases
   */
  private final boolean isPidcNodeMgr;

  private final Map<Long, FavUseCaseItemCommon> ucFavMap;
  /**
   * Map <Node ID, Node Object> of all favorite usecase item nodes
   */
  private final ConcurrentMap<Long, FavUseCaseItemNodeCommon> currentUsrUcFavNodeMap = new ConcurrentHashMap<>();

  private final UseCaseCommonDataHandler useCaseDataHandler;

  private final Map<Long, FavUseCaseItemCommon> projFavMap;

  private final Long pidcId;


  /**
   * @return the ucFavMap
   */
  public Map<Long, FavUseCaseItemCommon> getUcFavMap() {
    return this.ucFavMap;
  }


  /**
   * Constructor for private usecases
   *
   * @param apicUser ApicUser
   * @param useCaseDataHandler UseCaseDataHandler
   */
  public UseCaseFavNodesMgrCommon(final User apicUser, final UseCaseCommonDataHandler useCaseDataHandler,
      final Map<Long, UsecaseFavorite> ucMap) {
    this.apicUser = apicUser;
    this.useCaseDataHandler = useCaseDataHandler;
    this.isPidcNodeMgr = false;
    this.ucFavMap = fillFavUcMap(ucMap);
    this.pidcId = null;
    this.projFavMap = null;


  }

  /**
   * @param pidcVrsn
   * @param useCaseDataHandler
   */
  public UseCaseFavNodesMgrCommon(final PidcVersion pidcVrsn, final UseCaseCommonDataHandler useCaseDataHandler,
      final Map<Long, UsecaseFavorite> ucMap) {
    this.apicUser = null;
    this.useCaseDataHandler = useCaseDataHandler;
    this.isPidcNodeMgr = true;
    this.ucFavMap = null;
    this.pidcId = pidcVrsn.getPidcId();
    this.projFavMap = fillProjFavMap(ucMap);
  }

  private Map<Long, FavUseCaseItemCommon> fillFavUcMap(final Map<Long, UsecaseFavorite> ucMap) {
    Map<Long, FavUseCaseItemCommon> favMap = new HashMap<>();
    for (Entry<Long, UsecaseFavorite> iterable_element : ucMap.entrySet()) {
      favMap.put(iterable_element.getKey(),
          new FavUseCaseItemCommon(iterable_element.getValue(), this.useCaseDataHandler));
    }
    return favMap;
  }

  private Map<Long, FavUseCaseItemCommon> fillProjFavMap(final Map<Long, UsecaseFavorite> ucMap) {
    Map<Long, FavUseCaseItemCommon> favMap = new HashMap<>();
    for (Entry<Long, UsecaseFavorite> iterable_element : ucMap.entrySet()) {
      favMap.put(iterable_element.getKey(),
          new FavUseCaseItemCommon(iterable_element.getValue(), this.useCaseDataHandler));
    }
    return favMap;
  }

  /**
   * Gets the root level uc fav nodes of this project ID card.
   *
   * @return Usecase Nodes at the root level
   */
  public SortedSet<FavUseCaseItemNodeCommon> getRootVirtualNodes() {

    if (this.rootNodeSet == null) {
      // load virtual structure only when the root node is null or cleared
      loadNodes();
    }
    return this.rootNodeSet;
  }

  /**
   * Load nodes when root node set is null
   */
  protected void loadNodes() {
    // map for favourite usecase items alone
    Map<Long, FavUseCaseItemNodeCommon> favUcMap = new HashMap<Long, FavUseCaseItemNodeCommon>();

    if (this.isPidcNodeMgr) {
      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("Loading usecase fav nodes for PIDC :  ...");

      }
      for (FavUseCaseItemCommon favUC : this.projFavMap.values()) {
        // get the project favorite nodes & create the virtual structure
        FavUseCaseItemNodeCommon newFavUcNode = createFavUcNode(favUC);
        favUcMap.put(newFavUcNode.getID(), newFavUcNode);
      }

    }
    else {
      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("Loading usecase fav nodes for User : " + this.apicUser.getDescription() + " ...");
        getLogger().debug("Total nodes before refresh :" + this.useCaseDataHandler.getCurrentFavUcNodes().size());
      }
      for (FavUseCaseItemCommon favUC : this.ucFavMap.values()) {
        // get the user favorite nodes & create the virtual structure
        FavUseCaseItemNodeCommon newFavUcNode = createFavUcNode(favUC);
        favUcMap.put(newFavUcNode.getID(), createFavUcNode(favUC));
      }
    }
    Map<Long, FavUseCaseItemNodeCommon> allNodeMap = new HashMap<Long, FavUseCaseItemNodeCommon>(favUcMap);
    if (this.rootNodeSet == null) {
      this.rootNodeSet = new TreeSet<FavUseCaseItemNodeCommon>();
    }

    // load the parent nodes in the map
    for (FavUseCaseItemNodeCommon node : favUcMap.values()) {
      if (CommonUtils.isNull(node.getTopNode())) {
        // if the node is top node of its own, then add the node to the root set
        this.rootNodeSet.add(node);
      }
      else {
        // or add the top node to the root set & add its parents to allnodemap
        allNodeMap.putAll(node.getParentNodes());
        this.rootNodeSet.add(node.getTopNode());
      }
    }


    if (this.isPidcNodeMgr) {
      for (FavUseCaseItemNodeCommon node : this.useCaseDataHandler.getPidcFavUcNodes(this.pidcId).values()) {
        // Hides all unused nodes. Used nodes and their parents are already set to visible above
        if (!allNodeMap.containsKey(node.getID())) {
          node.setVisible(false);
        }
      }

      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("Virtual node loading completed. Total nodes after refresh :" +
            this.useCaseDataHandler.getPidcFavUcNodes(this.pidcId).size());
      }
    }
    else {
      for (FavUseCaseItemNodeCommon node : this.useCaseDataHandler.getCurrentFavUcNodes().values()) {
        // Hides all unused nodes. Used nodes and their parents are already set to visible above
        if (!allNodeMap.containsKey(node.getID())) {
          node.setVisible(false);
        }
      }
      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("Virtual node loading completed. Total nodes after refresh :" +
            this.useCaseDataHandler.getCurrentFavUcNodes().size());
      }
    }
  }

  /**
   * Generate fav uc item node
   *
   * @param favUC FavUseCaseItem
   * @return Map of FavUseCaseItemNode
   */
  private FavUseCaseItemNodeCommon createFavUcNode(final FavUseCaseItemCommon favUC) {
    FavUseCaseItemNodeCommon favUcNode;
    if (this.isPidcNodeMgr) {
      favUcNode = FavUseCaseItemNodeCommon.getFavUcNode(favUC, this.pidcId, this.useCaseDataHandler);
    }
    else {
      favUcNode = FavUseCaseItemNodeCommon.getFavUcNode(favUC, this.apicUser, this.useCaseDataHandler);
    }
    // set visibiltiy of favorite node & its parents to true
    favUcNode.setVisible(true);
    return favUcNode;
  }

  /**
   * get the logger
   *
   * @return ILoggerAdapter
   */
  private ILoggerAdapter getLogger() {
    return CDMLogger.getInstance();
  }


  /**
   * clear root nodes during deletion & insertion to recreate the virtual structure
   */
  public void clearRootNodes() {
    this.rootNodeSet = null;
  }


  /**
   * refresh the nodes
   *
   * @param favUcItem FavUseCaseItem
   */
  public void refreshNodes(final FavUseCaseItemCommon favUcItem) {
    Collection<FavUseCaseItemNodeCommon> favNodes;
    // get the fav nodes from cache
    if (this.isPidcNodeMgr) {
      favNodes = this.useCaseDataHandler.getPidcFavUcNodes(this.pidcId).values();
    }
    else {
      favNodes = this.currentUsrUcFavNodeMap.values();
    }
    for (FavUseCaseItemNodeCommon node : favNodes) {
      if (CommonUtils.isEqual(node.getFavUcItem(), favUcItem)) {
        // if the node matches with modified item , then reset that node
        node.resetNode();
      }
    }
    // clear the root node set
    clearRootNodes();
  }


  /**
   * @return the isPidcNodeMgr
   */
  public boolean isPidcNodeMgr() {
    return this.isPidcNodeMgr;
  }


}
