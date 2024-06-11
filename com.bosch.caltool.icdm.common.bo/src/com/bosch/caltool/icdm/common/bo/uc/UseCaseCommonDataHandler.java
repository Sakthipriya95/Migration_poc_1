/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.uc;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseDetailsModel;

/**
 * @author dmr1cob
 */
public class UseCaseCommonDataHandler {

  private SortedSet<FavUseCaseItemNodeCommon> projfavs;


  private SortedSet<FavUseCaseItemNodeCommon> privateUsecases;

  private UsecaseDetailsModel useCaseDetailsModel;

  /**
   * Key - PIDC ID Value - Map <Node ID, Node Object>
   */
  private final ConcurrentMap<Long, ConcurrentMap<Long, FavUseCaseItemNodeCommon>> projUcFavNodeMap =
      new ConcurrentHashMap<>();

  /**
   * ICDM-1040 Manager instance for private usecases
   */
  private UseCaseFavNodesMgrCommon ucFavMgr;
  /**
   * Map <Node ID, Node Object> of all favorite usecase item nodes
   */
  private final ConcurrentMap<Long, FavUseCaseItemNodeCommon> currentUsrUcFavNodeMap = new ConcurrentHashMap<>();
  private final PidcVersion pidcversion;


  /**
   * ICDM-1040 returns virtual favourite nodes belonging to a PIDC
   *
   * @param pidcID ID of pid card
   * @return map of FavUseCaseItemNode
   */
  public ConcurrentMap<Long, FavUseCaseItemNodeCommon> getPidcFavUcNodes(final long pidcID) {
    ConcurrentMap<Long, FavUseCaseItemNodeCommon> nodeMap = this.projUcFavNodeMap.get(pidcID);

    if (nodeMap == null) {
      nodeMap = new ConcurrentHashMap<>();
      this.projUcFavNodeMap.put(pidcID, nodeMap);
    }

    return nodeMap;
  }

  /**
   * ICDM-1040
   *
   * @param pidcID id of pid card
   * @param nodeID id of fav node
   * @return FavUseCaseItemNode
   */
  public FavUseCaseItemNodeCommon getPidcFavUcNode(final long pidcID, final long nodeID) {
    return getPidcFavUcNodes(pidcID).get(nodeID);
  }

  /**
   * @param pidcVersion
   */
  public UseCaseCommonDataHandler(final PidcVersion pidcVersion) {
    this.pidcversion = pidcVersion;
  }

  /**
   * ICDM-1040
   *
   * @return Map of current user's favorite usecase nodes
   */
  public Map<Long, FavUseCaseItemNodeCommon> getCurrentFavUcNodes() {
    return this.currentUsrUcFavNodeMap;
  }

  /**
   * @return the useCaseDetailsModel
   */
  public UsecaseDetailsModel getUseCaseDetailsModel() {
    return this.useCaseDetailsModel;
  }

  /**
   * ICDM-1040
   *
   * @param nodeId id of fav node
   * @return FavUseCaseItemNode
   */
  public FavUseCaseItemNodeCommon getUserFavUcNode(final long nodeId) {
    return this.currentUsrUcFavNodeMap.get(nodeId);
  }


  /**
   * ICDM-1040 Gets the root level nodes of this user.
   *
   * @return Uc fav Nodes at the root level
   */
  public SortedSet<FavUseCaseItemNodeCommon> getRootUcFavNodes() {

    this.ucFavMgr.clearRootNodes();
    // fetch the fav uc items
    return this.ucFavMgr.getRootVirtualNodes();
  }

  /**
   * @param favUcItem FavUseCaseItem
   */
  public void refreshFavNodes(final FavUseCaseItemCommon favUcItem) {
    this.ucFavMgr.refreshNodes(favUcItem);

  }

  /**
   * reset the virtual structure by clearing the root nodes
   */
  public void resetProjFavNodes() {
    this.ucFavMgr.clearRootNodes();

  }


  /**
   * ICDM-1040 Gets the root level nodes of this project ID card.
   *
   * @return PIDC Nodes at the root level
   */
  public SortedSet<FavUseCaseItemNodeCommon> getRootProjectUcFavNodes() {
    // fetch the fav uc items
    this.ucFavMgr.clearRootNodes();
    return this.ucFavMgr.getRootVirtualNodes();
  }

  /**
   * ICDM-1040 Refresh the nodes. To be used if attribute values are changed.
   *
   * @param favUcItem FavUseCaseItem
   */
  public void refreshProjectFavNodes(final FavUseCaseItemCommon favUcItem) {
    this.ucFavMgr.refreshNodes(favUcItem);

  }

  /**
   * reset the virtual structure by clearing the root nodes
   */
  public void resetFavNodes() {
    this.ucFavMgr.clearRootNodes();
  }

  /**
   * @param ucItem IUsecaseItemCommonBO
   * @return Sorted set of childUcItems
   */
  public SortedSet<IUsecaseItemCommonBO> getChildUCItems(final IUsecaseItemCommonBO ucItem) {
    SortedSet<IUsecaseItemCommonBO> childSet = new TreeSet<>();
    if (ucItem instanceof UseCaseGroupCommonBO) {
      UseCaseGroupCommonBO grp = (UseCaseGroupCommonBO) ucItem;
      Set<Long> childGrpIds = getUseCaseDetailsModel().getChildGroupSetMap().get(grp.getID());
      if (null != childGrpIds) {
        for (Long grpId : childGrpIds) {
          UseCaseGroup ucg = getUseCaseDetailsModel().getUseCaseGroupMap().get(grpId);
          UseCaseGroup parentucg = getUseCaseDetailsModel().getUseCaseGroupMap().get(grp.getID());
          UseCaseGroupCommonBO parentUcgCommonBO = new UseCaseGroupCommonBO(parentucg, getUseCaseDetailsModel(), null);
          childSet.add(new UseCaseGroupCommonBO(ucg, getUseCaseDetailsModel(), parentUcgCommonBO));
        }
      }
      Set<Long> childUCIds = getUseCaseDetailsModel().getChildUsecaseSetMap().get(grp.getID());
      if (null != childUCIds) {
        for (Long ucId : childUCIds) {
          UseCase uc = getUseCaseDetailsModel().getUsecaseMap().get(ucId);
          UseCaseGroup ucg = getUseCaseDetailsModel().getUseCaseGroupMap().get(uc.getGroupId());
          UseCaseGroupCommonBO uceCaseCommonBO = null;
          UseCaseGroupCommonBO ucgCommonBO = new UseCaseGroupCommonBO(ucg, getUseCaseDetailsModel(), uceCaseCommonBO);
          childSet.add(new UsecaseCommonBO(uc, ucgCommonBO));
        }
      }
    }
    else if (ucItem instanceof UsecaseCommonBO) {
      UsecaseCommonBO uc = (UsecaseCommonBO) ucItem;

      Set<Long> childSectionIds =
          getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getID()).getChildSectionsMap().get(uc.getID());
      if (null != childSectionIds) {
        for (Long secId : childSectionIds) {
          UseCaseSection ucs =
              getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getID()).getUcSectionMap().get(secId);
          UseCase usc = getUseCaseDetailsModel().getUsecaseMap().get(uc.getID());
          UseCaseGroup ucg = getUseCaseDetailsModel().getUseCaseGroupMap().get(uc.getParent().getID());
          UseCaseGroupCommonBO uceCaseCommonBO = null;
          UseCaseGroupCommonBO ucgCommonBO = new UseCaseGroupCommonBO(ucg, getUseCaseDetailsModel(), uceCaseCommonBO);

          UsecaseSectionCommonBO useCaseSectionCommonBo =
              new UsecaseSectionCommonBO(ucs, getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getID()),
                  new UsecaseCommonBO(usc, ucgCommonBO));
          childSet.add(useCaseSectionCommonBo);
        }
      }
    }
    else if (ucItem instanceof UsecaseSectionCommonBO) {
      UsecaseSectionCommonBO uc = (UsecaseSectionCommonBO) ucItem;

      Set<Long> childSectionIds = getUseCaseDetailsModel().getUsecaseDetailsModelMap()
          .get(uc.getUseCaseSection().getUseCaseId()).getChildSectionsMap().get(uc.getID());
      for (Long secId : childSectionIds) {
        UseCaseSection ucs = getUseCaseDetailsModel().getUsecaseDetailsModelMap()
            .get(uc.getUseCaseSection().getUseCaseId()).getUcSectionMap().get(secId);
        UseCase usc = getUseCaseDetailsModel().getUsecaseMap().get(uc.getUseCaseSection().getUseCaseId());
        UseCaseGroup ucg = getUseCaseDetailsModel().getUseCaseGroupMap().get(usc.getGroupId());
        UseCaseGroupCommonBO ucgCommonBO = new UseCaseGroupCommonBO(ucg, getUseCaseDetailsModel(), null);

        UsecaseSectionCommonBO useCaseSectionCommonBo =
            new UsecaseSectionCommonBO(ucs, getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getID()),
                new UsecaseCommonBO(usc, ucgCommonBO));
        childSet.add(useCaseSectionCommonBo);
      }
    }

    return childSet;
  }


  /**
   * @return the pidcversion
   */
  public PidcVersion getPidcversion() {
    return this.pidcversion;
  }


  /**
   * @return the ucFavMgr
   */
  public UseCaseFavNodesMgrCommon getUcFavMgr() {
    return this.ucFavMgr;
  }


  /**
   * @param ucFavMgr the ucFavMgr to set
   */
  public void setUcFavMgr(final UseCaseFavNodesMgrCommon ucFavMgr) {
    this.ucFavMgr = ucFavMgr;
  }


  /**
   * @param attr
   * @param grp
   * @return
   */
  public boolean isUcGroupMapped(final Attribute attr, final UseCaseGroupCommonBO grp) {

    // Check whether child groups are mapped
    for (UseCaseGroupCommonBO childUcg : grp.getChildGroups(false)) {
      if (isUcGroupMapped(attr, childUcg)) {
        return true;
      }
    }

    // Check whether use cases are mapped
    for (UsecaseCommonBO ucase : grp.getUseCases(false)) {
      ucase.setUsecaseEditorModel(getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(ucase.getUseCase().getId()));
      if (isUCMapped(attr, ucase)) {
        return true;
      }
    }

    // if no mapping done, then return false
    return false;

  }


  /**
   * @param attr
   * @param uc
   * @return
   */
  public boolean isUCMapped(final Attribute attr, final UsecaseCommonBO uc) {
    if (uc.canMapAttributes()) {
      Set<Long> mappedAttrIds =
          getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getId()).getUcItemAttrMap().get(uc.getId());
      if (!CommonUtils.isNullOrEmpty(mappedAttrIds) && mappedAttrIds.contains(attr.getId())) {
        return true;
      }
    }
    else {
      for (UsecaseSectionCommonBO ucSection : uc.getUseCaseSections(false)) {
        if (isUCSMapped(attr, ucSection)) {
          return true;

        }
      }
    }

    return false;
  }

  /**
   * @param attr
   * @param ucs
   * @return
   */
  public boolean isUCSMapped(final Attribute attr, final UsecaseSectionCommonBO ucs) {
    if (ucs.canMapAttributes()) {
      Set<Long> mappedAttrIds = getUseCaseDetailsModel().getUsecaseDetailsModelMap()
          .get(ucs.getUseCaseSection().getUseCaseId()).getUcItemAttrMap().get(ucs.getId());
      if (!CommonUtils.isNullOrEmpty(mappedAttrIds) && mappedAttrIds.contains(attr.getId())) {
        return true;
      }
    }
    else {
      for (UsecaseSectionCommonBO ucsChild : ucs.getChildSections(false)) {
        if (isUCSMapped(attr, ucsChild)) {
          return true;
        }
      }
    }

    return false;
  }


  /**
   * @return the projfavs
   */
  public SortedSet<FavUseCaseItemNodeCommon> getProjfavs() {
    return this.projfavs;
  }


  /**
   * @param projfavs the projfavs to set
   */
  public void setProjfavs(final SortedSet<FavUseCaseItemNodeCommon> projfavs) {
    this.projfavs = projfavs;
  }


  /**
   * @return the privateUsecases
   */
  public SortedSet<FavUseCaseItemNodeCommon> getPrivateUsecases() {
    return this.privateUsecases;
  }


  /**
   * @param privateUsecases the privateUsecases to set
   */
  public void setPrivateUsecases(final SortedSet<FavUseCaseItemNodeCommon> privateUsecases) {
    this.privateUsecases = privateUsecases;
  }


  /**
   * @param useCaseDetailsModel the useCaseDetailsModel to set
   */
  public void setUseCaseDetailsModel(final UsecaseDetailsModel useCaseDetailsModel) {
    this.useCaseDetailsModel = useCaseDetailsModel;
  }

}
