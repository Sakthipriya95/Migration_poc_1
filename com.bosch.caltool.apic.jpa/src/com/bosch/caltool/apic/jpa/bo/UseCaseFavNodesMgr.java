/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * ICDM-1040 This class has the generator methods for favourite usecase nodes
 * 
 * @author mkl2cob
 */
class UseCaseFavNodesMgr {

  /**
   * Project id card to which project usecase item belongs to
   */
  private PIDCard pidc;

  /**
   * Apic User to which the private usecase item belongs to
   */
  private ApicUser apicUser;

  /**
   * set of root nodes
   */
  private SortedSet<FavUseCaseItemNode> rootNodeSet;

  /**
   * true if this instance is a manager for project usecases
   */
  private final boolean isPidcNodeMgr;

  /**
   * Constructor for project usecases
   * 
   * @param pidc PIDCard
   */
  public UseCaseFavNodesMgr(final PIDCard pidc) {
    this.pidc = pidc;
    this.isPidcNodeMgr = true;
  }

  /**
   * Constructor for private usecases
   * 
   * @param apicUser ApicUser
   */
  public UseCaseFavNodesMgr(final ApicUser apicUser) {
    this.apicUser = apicUser;
    this.isPidcNodeMgr = false;
  }

  /**
   * Gets the root level uc fav nodes of this project ID card.
   * 
   * @return Usecase Nodes at the root level
   */
  public SortedSet<FavUseCaseItemNode> getRootVirtualNodes() {

    if (this.rootNodeSet == null) {
      // load virtual structure only when the root node is null or cleared
      loadNodes();
    }
    return this.rootNodeSet;
  }

  /**
   * Load nodes when root node set is null
   */
  private void loadNodes() {
    // map for favourite usecase items alone
    Map<Long, FavUseCaseItemNode> favUcMap = new HashMap<Long, FavUseCaseItemNode>();

    if (this.isPidcNodeMgr) {
      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("Loading usecase fav nodes for PIDC : " + this.pidc.getName() + " ...");
        getLogger().debug("Total nodes before refresh :" + getDataCache().getPidcFavUcNodes(this.pidc.getID()).size());
      }
      for (FavUseCaseItem favUC : getDataCache().getPidcUCFavMap(this.pidc.getID()).values()) {
        // get the project favorite nodes & create the virtual structure
        FavUseCaseItemNode newFavUcNode = createFavUcNode(favUC);
        favUcMap.put(newFavUcNode.getID(), newFavUcNode);
      }

    }
    else {
      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("Loading usecase fav nodes for User : " + this.apicUser.getName() + " ...");
        getLogger().debug("Total nodes before refresh :" + getDataCache().getCurrentFavUcNodes().size());
      }
      for (FavUseCaseItem favUC : getDataCache().getCurrentUserUCFavMap().values()) {
        // get the user favorite nodes & create the virtual structure
        FavUseCaseItemNode newFavUcNode = createFavUcNode(favUC);
        favUcMap.put(newFavUcNode.getID(), createFavUcNode(favUC));
      }
    }
    Map<Long, FavUseCaseItemNode> allNodeMap = new HashMap<Long, FavUseCaseItemNode>(favUcMap);
    if (this.rootNodeSet == null) {
      this.rootNodeSet = new TreeSet<FavUseCaseItemNode>();
    }

    // load the parent nodes in the map
    for (FavUseCaseItemNode node : favUcMap.values()) {
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
      for (FavUseCaseItemNode node : getDataCache().getPidcFavUcNodes(this.pidc.getID()).values()) {
        // Hides all unused nodes. Used nodes and their parents are already set to visible above
        if (!allNodeMap.containsKey(node.getID())) {
          node.setVisible(false);
        }
      }

      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug(
            "Virtual node loading completed. Total nodes after refresh :" +
                getDataCache().getPidcFavUcNodes(this.pidc.getID()).size());
      }
    }
    else {
      for (FavUseCaseItemNode node : getDataCache().getCurrentFavUcNodes().values()) {
        // Hides all unused nodes. Used nodes and their parents are already set to visible above
        if (!allNodeMap.containsKey(node.getID())) {
          node.setVisible(false);
        }
      }
      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug(
            "Virtual node loading completed. Total nodes after refresh :" +
                getDataCache().getCurrentFavUcNodes().size());
      }
    }
  }

  /**
   * Generate fav uc item node
   * 
   * @param favUC FavUseCaseItem
   * @return Map of FavUseCaseItemNode
   */
  private FavUseCaseItemNode createFavUcNode(final FavUseCaseItem favUC) {
    FavUseCaseItemNode favUcNode;
    if (this.isPidcNodeMgr) {
      favUcNode = FavUseCaseItemNode.getFavUcNode(getDataProvider(), favUC, this.pidc);
    }
    else {
      favUcNode = FavUseCaseItemNode.getFavUcNode(getDataProvider(), favUC, this.apicUser);
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
    if (this.isPidcNodeMgr) {
      return this.pidc.getDataCache().getDataProvider().getLogger();
    }
    return this.apicUser.getDataCache().getDataProvider().getLogger();
  }

  /**
   * get the data cache
   * 
   * @return DataCache
   */
  private DataCache getDataCache() {
    if (this.isPidcNodeMgr) {
      return this.pidc.getDataCache();
    }
    return this.apicUser.getDataCache();
  }

  /**
   * @return ApicDataProvider
   */
  private ApicDataProvider getDataProvider() {
    if (this.isPidcNodeMgr) {
      return this.pidc.getDataCache().getDataProvider();
    }
    return this.apicUser.getDataCache().getDataProvider();
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
  public void refreshNodes(final FavUseCaseItem favUcItem) {
    Collection<FavUseCaseItemNode> favNodes;
    // get the fav nodes from cache
    if (this.isPidcNodeMgr) {
      favNodes = getDataCache().getPidcFavUcNodes(this.pidc.getPidcId()).values();
    }
    else {
      favNodes = getDataCache().getCurrentFavUcNodes().values();
    }
    for (FavUseCaseItemNode node : favNodes) {
      if (CommonUtils.isEqual(node.getFavUcItem(), favUcItem)) {
        // if the node matches with modified item , then reset that node
        node.resetNode();
      }
    }
    // clear the root node set
    clearRootNodes();
  }
}
