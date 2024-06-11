/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseDetailsModel;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.uc.UsecaseFavoriteServiceClient;

/**
 * @author dmo5cob
 */
public class UseCaseDataHandler {

  private SortedSet<FavUseCaseItemNode> projfavs;


  private SortedSet<FavUseCaseItemNode> privateUsecases;

  private UsecaseDetailsModel useCaseDetailsModel;

  /**
   * Key - PIDC ID Value - Map <Node ID, Node Object>
   */
  private final ConcurrentMap<Long, ConcurrentMap<Long, FavUseCaseItemNode>> projUcFavNodeMap =
      new ConcurrentHashMap<>();

  /**
   * ICDM-1040 Manager instance for private usecases
   */
  private UseCaseFavNodesMgr ucFavMgr;
  /**
   * Map <Node ID, Node Object> of all favorite usecase item nodes
   */
  private final ConcurrentMap<Long, FavUseCaseItemNode> currentUsrUcFavNodeMap = new ConcurrentHashMap<>();
  // selected pidc version
  private final PidcVersion pidcversion;
  /**
   * To fetch usecase group parent hierarchy
   */

  private final UseCaseGrpParentBO useCaseGrpParentBO = new UseCaseGrpParentBO();


  /**
   * ICDM-1040 returns virtual favourite nodes belonging to a PIDC
   *
   * @param pidcID ID of pid card
   * @return map of FavUseCaseItemNode
   */
  public ConcurrentMap<Long, FavUseCaseItemNode> getPidcFavUcNodes(final long pidcID) {
    ConcurrentMap<Long, FavUseCaseItemNode> nodeMap = this.projUcFavNodeMap.get(pidcID);

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
  public FavUseCaseItemNode getPidcFavUcNode(final long pidcID, final long nodeID) {
    return getPidcFavUcNodes(pidcID).get(nodeID);
  }

  /**
   * @param pidcVersion
   */
  public UseCaseDataHandler(final PidcVersion pidcVersion) {
    this.pidcversion = pidcVersion;
    fetchUseCaseDetails();
  }

  /**
   * ICDM-1040
   *
   * @return Map of current user's favorite usecase nodes
   */
  public Map<Long, FavUseCaseItemNode> getCurrentFavUcNodes() {
    return this.currentUsrUcFavNodeMap;
  }

  /**
  *
  */
  private void fetchUseCaseDetails() {

    UseCaseGroupServiceClient ucgServiceClient = new UseCaseGroupServiceClient();
    try {
      this.useCaseDetailsModel = ucgServiceClient.getUseCaseDetailsModel();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }


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
  public FavUseCaseItemNode getUserFavUcNode(final long nodeId) {
    return this.currentUsrUcFavNodeMap.get(nodeId);
  }

  /**
   * ICDM-1028
   *
   * @param apicUser
   * @return Map of favourite usecase items for this user
   */
  public Map<Long, FavUseCaseItem> getFavoriteUCMap(final User apicUser) {

    UsecaseFavoriteServiceClient client = new UsecaseFavoriteServiceClient();
    Map<Long, FavUseCaseItem> favMap = new HashMap<>();
    try {
      Map<Long, UsecaseFavorite> ucMap = client.getFavoriteUseCases(apicUser.getId());
      for (Entry<Long, UsecaseFavorite> iterable_element : ucMap.entrySet()) {
        favMap.put(iterable_element.getKey(), new FavUseCaseItem(iterable_element.getValue(), this));
      }

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
    return favMap;
  }

  /**
   * @param favUcItem FavUseCaseItem
   */
  public void refreshFavNodes(final FavUseCaseItem favUcItem) {
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
  public SortedSet<FavUseCaseItemNode> getRootProjectUcFavNodes() {
    // fetch the fav uc items
    this.ucFavMgr.clearRootNodes();
    return this.ucFavMgr.getRootVirtualNodes();
  }

  /**
   * ICDM-1040 Refresh the nodes. To be used if attribute values are changed.
   *
   * @param favUcItem FavUseCaseItem
   */
  public void refreshProjectFavNodes(final FavUseCaseItem favUcItem) {
    this.ucFavMgr.refreshNodes(favUcItem);

  }

  /**
   * reset the virtual structure by clearing the root nodes
   */
  public void resetFavNodes() {
    this.ucFavMgr.clearRootNodes();
  }

  /**
   * ICDM-1027
   *
   * @param projectId id
   * @return Map of favourite usecase items for this project id card
   */
  public Map<Long, FavUseCaseItem> getFavoriteProjUCMap(final Long projectId) {

    UsecaseFavoriteServiceClient client = new UsecaseFavoriteServiceClient();
    Map<Long, FavUseCaseItem> favMap = new HashMap<>();
    try {
      Map<Long, UsecaseFavorite> ucMap = client.getProjectFavoriteUseCases(projectId);
      for (Entry<Long, UsecaseFavorite> iterable_element : ucMap.entrySet()) {
        favMap.put(iterable_element.getKey(), new FavUseCaseItem(iterable_element.getValue(), null));
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
    return favMap;
  }


  /**
   * ICDM-1092 checks if this is a valid insert
   *
   * @param ucItem IUseCaseItemClientBO
   * @param deleteChildFavItems
   * @return true if this is a valid insert
   * @throws IcdmException
   */
  public boolean isValidInsert(final IUseCaseItemClientBO ucItem, final boolean deleteChildFavItems)
      throws IcdmException {

    // Collection of favourite nodes
    Collection<FavUseCaseItemNode> favNodes;
    favNodes = getFavNodes();

    for (FavUseCaseItemNode favUcNode : favNodes) {
      if (CommonUtils.isNull(favUcNode.getFavUcItem())) {
        // check if there are fav nodes as children
        // if this is a virtual node
        if (CommonUtils.isEqual(favUcNode.getID(), ucItem.getID())) {
          // adding the new node & deleting the existing child fav items
          if (deleteChildFavItems) {
            deleteChildFavItems(favUcNode);
          }
          return true;
        }
      }
      else {
        // if this is a fav use case item
        if (CommonUtils.isEqual(favUcNode.getID(), ucItem.getID())) {
          if (favUcNode.isVisible()) {
            // Not a valid insert as the value already exists in table
            throw new IcdmException("This use case item is already added as the selected favorite type!");
          }
          return true;
        }
        if (favUcNode.isVisible()) {// do this for undeleted nodes
          // check if parent is already a fav node
          for (IUseCaseItemClientBO usecaseItem : favUcNode.getChildUCItems(this)) {
            isChildOfExistingFav(usecaseItem, ucItem);
          }
        }
      }

    }
    return true;
  }

  /**
   * ICDM-1092 delete the child fav uc items
   *
   * @param favUcNode FavUseCaseItemNode
   * @throws CommandException
   */
  private void deleteChildFavItems(final FavUseCaseItemNode favUcNode) {
    findAndDeleteFavChildItems(favUcNode);
    // store the fav uc node
  }

  /**
   * @param favUcNode FavUseCaseItemNode
   * @throws CommandException
   */
  private void findAndDeleteFavChildItems(final FavUseCaseItemNode favUcNode) {
    if (CommonUtils.isNotNull(favUcNode.getChildFavNodes())) {
      for (FavUseCaseItemNode childUcNode : favUcNode.getChildFavNodes()) {
        if (CommonUtils.isNotNull(childUcNode.getFavUcItem())) {
          UsecaseFavoriteServiceClient client = new UsecaseFavoriteServiceClient();
          try {
            UsecaseFavorite ucFav = childUcNode.getFavUcItem().getUseCaseFav();
            ucFav.setPidcVersId(this.pidcversion.getId());
            client.delete(ucFav);
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
          }
        }
        else {
          findAndDeleteFavChildItems(childUcNode);
        }
      }
    }
  }

  /**
   * @return Collection<FavUseCaseItemNode>
   */
  private Collection<FavUseCaseItemNode> getFavNodes() {
    Collection<FavUseCaseItemNode> favNodes;
    if (this.ucFavMgr.isPidcNodeMgr()) {
      favNodes = getPidcFavUcNodes(this.pidcversion.getPidcId()).values();
    }
    else {
      favNodes = getCurrentFavUcNodes().values();
    }
    return favNodes;
  }

  /**
   * ICDM-1092 checks if this child of any level to the existing favourite node
   *
   * @param ucItem
   * @param usecaseItem AbstractUseCaseItem
   * @throws IcdmException
   * @throws CommandException Invalid insert
   */
  private void isChildOfExistingFav(final IUseCaseItemClientBO ucChild, final IUseCaseItemClientBO ucItem)
      throws IcdmException {

    if (CommonUtils.isEqual(ucChild.getID(), ucItem.getID())) {
      // throws exception if this is a child of existing fav item
      throw new IcdmException("Another use case item in the parent hierarchy is already added as a favorite!");
    }
    for (IUseCaseItemClientBO usecaseItem : getChildUCItems(ucChild)) {
      // throws exception if this is a child at any level of existing fav item
      isChildOfExistingFav(usecaseItem, ucItem);
    }
  }

  /**
   * @param ucItem IUseCaseItemClientBO
   * @return Sorted set of childUcItems
   */
  public SortedSet<IUseCaseItemClientBO> getChildUCItems(final IUseCaseItemClientBO ucItem) {
    SortedSet<IUseCaseItemClientBO> childSet = new TreeSet<>();
    if (ucItem instanceof UseCaseGroupClientBO) {
      UseCaseGroupClientBO grp = (UseCaseGroupClientBO) ucItem;
      Set<Long> childGrpIds = getUseCaseDetailsModel().getChildGroupSetMap().get(grp.getID());
      if (null != childGrpIds) {
        for (Long grpId : childGrpIds) {
          UseCaseGroup ucg = getUseCaseDetailsModel().getUseCaseGroupMap().get(grpId);
          UseCaseGroup parentucg = getUseCaseDetailsModel().getUseCaseGroupMap().get(grp.getID());
          UseCaseGroupClientBO parentUcgClientBO = new UseCaseGroupClientBO(parentucg, getUseCaseDetailsModel(), null);
          childSet.add(new UseCaseGroupClientBO(ucg, getUseCaseDetailsModel(), parentUcgClientBO));
        }
      }
      Set<Long> childUCIds = getUseCaseDetailsModel().getChildUsecaseSetMap().get(grp.getID());
      if (null != childUCIds) {
        for (Long ucId : childUCIds) {
          UseCase uc = getUseCaseDetailsModel().getUsecaseMap().get(ucId);
          UseCaseGroup ucg = getUseCaseDetailsModel().getUseCaseGroupMap().get(uc.getGroupId());
          UseCaseGroupClientBO ucgClientBO = new UseCaseGroupClientBO(ucg, getUseCaseDetailsModel(), null);
          childSet.add(new UsecaseClientBO(uc, ucgClientBO));
        }
      }
    }
    else if (ucItem instanceof UsecaseClientBO) {
      UsecaseClientBO uc = (UsecaseClientBO) ucItem;

      Set<Long> childSectionIds =
          getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getID()).getChildSectionsMap().get(uc.getID());
      if (null != childSectionIds) {
        for (Long secId : childSectionIds) {
          UseCaseSection ucs =
              getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getID()).getUcSectionMap().get(secId);
          UseCase usc = getUseCaseDetailsModel().getUsecaseMap().get(uc.getID());
          UseCaseGroup ucg = getUseCaseDetailsModel().getUseCaseGroupMap().get(uc.getParent().getID());
          UseCaseGroupClientBO ucgClientBO = new UseCaseGroupClientBO(ucg, getUseCaseDetailsModel(), null);

          childSet.add(new UseCaseSectionClientBO(getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getID()),
              ucs, new UsecaseClientBO(usc, ucgClientBO)));
        }
      }
    }
    else if (ucItem instanceof UseCaseSectionClientBO) {
      UseCaseSectionClientBO uc = (UseCaseSectionClientBO) ucItem;
      Set<Long> childSectionIds = getUseCaseDetailsModel().getUsecaseDetailsModelMap()
          .get(uc.getUseCaseSection().getUseCaseId()).getChildSectionsMap().get(uc.getID());
      if (null != childSectionIds) {
        for (Long secId : childSectionIds) {
          UseCaseSection ucs = getUseCaseDetailsModel().getUsecaseDetailsModelMap()
              .get(uc.getUseCaseSection().getUseCaseId()).getUcSectionMap().get(secId);
          UseCase usc = getUseCaseDetailsModel().getUsecaseMap().get(uc.getUseCaseSection().getUseCaseId());
          UseCaseGroup ucg = getUseCaseDetailsModel().getUseCaseGroupMap().get(usc.getGroupId());
          UseCaseGroupClientBO ucgClientBO = new UseCaseGroupClientBO(ucg, getUseCaseDetailsModel(), null);

          childSet.add(new UseCaseSectionClientBO(getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getID()),
              ucs, new UsecaseClientBO(usc, ucgClientBO)));

        }
      }
    }

    return childSet;
  }


  /**
   * Refresh based on the change
   *
   * @param chDataInfoMap Change Data
   */
  public void refresh(final Map<Long, ChangeDataInfo> chDataInfoMap) {

    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      UsecaseFavorite newValue = null;
      if (changeData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {
          newValue = new UsecaseFavoriteServiceClient().getById(changeData.getObjId());
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
      else if (changeData.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        newValue = (UsecaseFavorite) changeData.getRemovedData();
      }

      if (null != newValue) {
        if (null != newValue.getUserId()) {
          refreshUserFavNodes();
        }
        else {
          refreshProjectFavNodes();
        }
      }
    }
  }

  /**
   * refresh project favorites
   */
  protected void refreshProjectFavNodes() {
    if (null != this.pidcversion) {
      UseCaseFavNodesMgr ucsFavMgr = new UseCaseFavNodesMgr(this.pidcversion, this);
      setUcFavMgr(ucsFavMgr);

      this.projUcFavNodeMap.clear();
      getProjfavs().clear();
      getProjfavs().addAll(getRootProjectUcFavNodes());
      // fetch the fav uc items
    }
  }

  /**
   * refresh user favorites
   */
  protected void refreshUserFavNodes() {
    UseCaseFavNodesMgr ucsFavMgr;
    try {

      ucsFavMgr = new UseCaseFavNodesMgr(new CurrentUserBO().getUser(), this);
      setUcFavMgr(ucsFavMgr);

      this.currentUsrUcFavNodeMap.clear();
      getPrivateUsecases().clear();
      getPrivateUsecases().addAll(getRootProjectUcFavNodes());
      // fetch the fav uc items

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
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
  public UseCaseFavNodesMgr getUcFavMgr() {
    return this.ucFavMgr;
  }


  /**
   * @param ucFavMgr the ucFavMgr to set
   */
  public void setUcFavMgr(final UseCaseFavNodesMgr ucFavMgr) {
    this.ucFavMgr = ucFavMgr;
  }


  /**
   * @param attr
   * @param grp
   * @return
   */
  public boolean isUcGroupMapped(final Attribute attr, final UseCaseGroupClientBO grp) {

    // Check whether child groups are mapped
    for (UseCaseGroupClientBO childUcg : grp.getChildGroups(false)) {
      if (isUcGroupMapped(attr, childUcg)) {
        return true;
      }
    }

    // Check whether use cases are mapped
    for (UsecaseClientBO ucase : grp.getUseCases(false)) {
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
  public boolean isUCMapped(final Attribute attr, final UsecaseClientBO uc) {
    if (uc.canMapAttributes()) {
      Set<Long> mappedAttrIds =
          getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getId()).getUcItemAttrMap().get(uc.getId());
      if (!CommonUtils.isNullOrEmpty(mappedAttrIds) && mappedAttrIds.contains(attr.getId())) {
        return true;
      }
    }
    else {
      for (UseCaseSectionClientBO ucSection : uc.getUseCaseSections(false)) {
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
  public boolean isUCSMapped(final Attribute attr, final UseCaseSectionClientBO ucs) {
    if (ucs.canMapAttributes()) {
      Set<Long> mappedAttrIds = getUseCaseDetailsModel().getUsecaseDetailsModelMap()
          .get(ucs.getUseCaseSection().getUseCaseId()).getUcItemAttrMap().get(ucs.getId());
      if (!CommonUtils.isNullOrEmpty(mappedAttrIds) && mappedAttrIds.contains(attr.getId())) {
        return true;
      }
    }
    else {
      for (UseCaseSectionClientBO ucsChild : ucs.getChildSections(false)) {
        if (isUCSMapped(attr, ucsChild)) {
          return true;
        }
      }
    }

    return false;
  }


  /**
   * Method to fetch the IUseCaseItemClientBO based on the selected usecaseItemId
   *
   * @param usecaseItemId as input
   * @return IUseCaseItemClientBO
   */
  public IUseCaseItemClientBO getIUseCaseItemClientBOObject(final Long usecaseItemId) {
    IUseCaseItemClientBO useCaseItemClientBO = null;

    UseCase useCase = getUseCaseDetailsModel().getUsecaseMap().get(usecaseItemId);
    UseCaseGroup useCaseGroup = getUseCaseDetailsModel().getUseCaseGroupMap().get(usecaseItemId);
    UseCaseSection useCaseSection = getUseCaseDetailsModel().getUcSectionMap().get(usecaseItemId);
    // Fetches if the item is usecase group
    if (useCaseGroup != null) {
      useCaseItemClientBO = getUseCaseGroupClientBO(useCaseGroup);
    } // Fetches if the item is usecase
    else if (useCase != null) {
      useCaseItemClientBO = getUsecaseClientBO(useCase);
    } // Fetches if the item is usecase sections
    else if (useCaseSection != null) {
      useCaseItemClientBO = getUseCaseSectionClientBO(useCaseSection);
    }
    return useCaseItemClientBO;
  }

  /**
   * Method to fetch the object based on tree hirarichy Lvl 1 - UsecaseGroup Lvl 2 - Usecase Lvl 3 - Usecase sections
   *
   * @param usecaseItemId as input
   * @return list of IUseCaseItemClientBO
   */
  public List<IUseCaseItemClientBO> getUsecaseObjectHirarichyList(final Long usecaseItemId) {
    List<IUseCaseItemClientBO> useCaseItemClientBOs = new ArrayList<>();
    UseCase useCase = getUseCaseDetailsModel().getUsecaseMap().get(usecaseItemId);
    UseCaseGroup useCaseGroup = getUseCaseDetailsModel().getUseCaseGroupMap().get(usecaseItemId);
    UseCaseSection useCaseSection = getUseCaseDetailsModel().getUcSectionMap().get(usecaseItemId);
    // Fetches if the item is usecase group
    if (useCaseGroup != null) {
      this.useCaseGrpParentBO.getUseCaseGroupClientBORecursive(useCaseGroup, useCaseItemClientBOs,
          getUseCaseDetailsModel());
    } // Fetches if the item is usecase
    else if (useCase != null) {
      useCaseItemClientBOs.addAll(getUsecaseClientBOHirarichy(useCase));
    } // Fetches if the item is usecase sections
    else if (useCaseSection != null) {
      useCaseItemClientBOs.addAll(getUseCaseSectionClientBOHirarichy(useCaseSection));
    }
    return useCaseItemClientBOs;
  }

  /**
   * Method that return the UseCaseSectionClientBO object if the input is useCaseSection
   */
  private UseCaseSectionClientBO getUseCaseSectionClientBO(final UseCaseSection useCaseSection) {
    UseCaseSectionClientBO useCaseSectionClientBO = null;
    UseCase useCase = getUseCaseDetailsModel().getUsecaseMap().get(useCaseSection.getUseCaseId());
    UsecaseClientBO usecaseClientBO = getUsecaseClientBO(useCase);
    if (null != usecaseClientBO) {
      useCaseSectionClientBO =
          new UseCaseSectionClientBO(getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(usecaseClientBO.getID()),
              useCaseSection, usecaseClientBO);
    }
    return useCaseSectionClientBO;
  }

  /**
   * Method that return the UsecaseclientBo object if the input is usecase
   */
  private UsecaseClientBO getUsecaseClientBO(final UseCase useCase) {
    UsecaseClientBO usecaseClientBO = null;
    if (null != useCase.getGroupId()) {
      UseCaseGroup tempUseCaseGroup = getUseCaseDetailsModel().getUseCaseGroupMap().get(useCase.getGroupId());
      UseCaseGroupClientBO useCaseGroupClientBO = getUseCaseGroupClientBO(tempUseCaseGroup);
      usecaseClientBO = new UsecaseClientBO(useCase, useCaseGroupClientBO);
    }
    return usecaseClientBO;

  }


  /**
   * Method that return the UseCaseGroupClientBO object if the input is UseCaseGroup
   */
  private UseCaseGroupClientBO getUseCaseGroupClientBO(final UseCaseGroup useCaseGroup) {
    UseCaseGroupClientBO useCaseItemClientBO;
    UseCaseGroupClientBO parentUseCaseGroupClientBO = null;
    if (null != useCaseGroup.getParentGroupId()) {
      UseCaseGroup parentUseCaseGrp =
          getUseCaseDetailsModel().getUseCaseGroupMap().get(useCaseGroup.getParentGroupId());
      parentUseCaseGroupClientBO = new UseCaseGroupClientBO(parentUseCaseGrp, getUseCaseDetailsModel(), null);
    }
    useCaseItemClientBO = new UseCaseGroupClientBO(useCaseGroup, getUseCaseDetailsModel(), parentUseCaseGroupClientBO);
    return useCaseItemClientBO;
  }

  /**
   * Method that return the List<IUseCaseItemClientBO> object if the input is useCase tree hirarichy Lvl 1 -
   * UsecaseGroup Lvl 2 - Usecase Lvl
   */
  private List<IUseCaseItemClientBO> getUsecaseClientBOHirarichy(final UseCase useCase) {
    List<IUseCaseItemClientBO> useCaseItemClientBOs = new ArrayList<>();
    if (null != useCase.getGroupId()) {
      UseCaseGroup tempUseCaseGroup = getUseCaseDetailsModel().getUseCaseGroupMap().get(useCase.getGroupId());
      this.useCaseGrpParentBO.getUseCaseGroupClientBORecursive(tempUseCaseGroup, useCaseItemClientBOs,
          getUseCaseDetailsModel());
      UseCaseGroupClientBO useCaseGroupClientBO = getUseCaseGroupClientBO(tempUseCaseGroup);
      useCaseItemClientBOs.add(new UsecaseClientBO(useCase, useCaseGroupClientBO));
    }
    return useCaseItemClientBOs;

  }

  /**
   * Method that return the List<IUseCaseItemClientBO> object if the input is useCaseSection this method will fetch
   * usecase groups ,usecase and all the parent usecase sections
   */
  private List<IUseCaseItemClientBO> getUseCaseSectionClientBOHirarichy(final UseCaseSection useCaseSection) {
    List<IUseCaseItemClientBO> useCaseItemClientBOs = new ArrayList<>();
    getParentUsecaseSections(useCaseSection, useCaseItemClientBOs);
    UseCase useCase = getUseCaseDetailsModel().getUsecaseMap().get(useCaseSection.getUseCaseId());
    UsecaseClientBO usecaseClientBO = getUsecaseClientBO(useCase);

    if (null != usecaseClientBO) {
      // add the usecase group and usecase if the list is empty
      if (useCaseItemClientBOs.isEmpty()) {
        useCaseItemClientBOs.addAll(getUsecaseClientBOHirarichy(useCase));
      }
      useCaseItemClientBOs.add(
          new UseCaseSectionClientBO(getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(usecaseClientBO.getID()),
              useCaseSection, usecaseClientBO));
    }

    return useCaseItemClientBOs;
  }


  /**
   * Recursive Method that finds and add all the parent usecase sections and its usecase and usecasegroups
   */
  private void getParentUsecaseSections(final UseCaseSection useCaseSection,
      final List<IUseCaseItemClientBO> useCaseItemClientBOs) {
    UseCaseSection parentUseCaseSection = null;
    if (null != useCaseSection.getParentSectionId()) {
      parentUseCaseSection = getUseCaseDetailsModel().getUcSectionMap().get(useCaseSection.getParentSectionId());
      getParentUsecaseSections(parentUseCaseSection, useCaseItemClientBOs);
    }
    if (parentUseCaseSection != null) {

      UseCase useCase = getUseCaseDetailsModel().getUsecaseMap().get(parentUseCaseSection.getUseCaseId());
      UsecaseClientBO usecaseClientBO = getUsecaseClientBO(useCase);
      useCaseItemClientBOs.addAll(getUsecaseClientBOHirarichy(useCase));
      useCaseItemClientBOs.add(
          new UseCaseSectionClientBO(getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(usecaseClientBO.getID()),
              parentUseCaseSection, usecaseClientBO));
    }

  }

  /**
   * @return the projfavs
   */
  public SortedSet<FavUseCaseItemNode> getProjfavs() {
    return this.projfavs;
  }


  /**
   * @param projfavs the projfavs to set
   */
  public void setProjfavs(final SortedSet<FavUseCaseItemNode> projfavs) {
    this.projfavs = projfavs;
  }


  /**
   * @return the privateUsecases
   */
  public SortedSet<FavUseCaseItemNode> getPrivateUsecases() {
    return this.privateUsecases;
  }


  /**
   * @param privateUsecases the privateUsecases to set
   */
  public void setPrivateUsecases(final SortedSet<FavUseCaseItemNode> privateUsecases) {
    this.privateUsecases = privateUsecases;
  }


  /**
   * @param useCaseDetailsModel the useCaseDetailsModel to set
   */
  public void setUseCaseDetailsModel(final UsecaseDetailsModel useCaseDetailsModel) {
    this.useCaseDetailsModel = useCaseDetailsModel;
  }


  /**
   * @return the projUcFavNodeMap
   */
  public ConcurrentMap<Long, ConcurrentMap<Long, FavUseCaseItemNode>> getProjUcFavNodeMap() {
    return this.projUcFavNodeMap;
  }

}
