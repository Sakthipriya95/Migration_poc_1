/**
 *
 */
package com.bosch.caltool.apic.ui.views.providers;

import java.util.SortedSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.apic.AttrRootNode;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.ProjFavUcRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseFavNodesMgr;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Content provider for OutLine tree
 *
 * @author dmo5cob
 */
public class OutlinePIDCTreeViewContentProvider implements ITreeContentProvider {


  /**
   * PIDCard associated with outline page
   */
  private final PidcVersion pidcVersion;
  /**
   * Instance of outline view data handler
   */
  private final OutLineViewDataHandler dataHandler;


  /**
   * @param pidcVer PidcVersion
   * @param dataHandler OutLineViewDataHandler
   */
  public OutlinePIDCTreeViewContentProvider(final PidcVersion pidcVer, final OutLineViewDataHandler dataHandler) {
    this.pidcVersion = pidcVer;
    this.dataHandler = dataHandler;
    // initialise the appropriate handler
    if (null != this.pidcVersion) {

      this.dataHandler.getUcDataHandler().setProjfavs(getProjectFavNodes());
    }
    else {
      this.dataHandler.getUcDataHandler().setProjfavs(null);
    }

    this.dataHandler.getUcDataHandler().setPrivateUsecases(getPrivateUsecases());
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface .viewers.Viewer, java.lang.Object,
   * java.lang.Object)
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang. Object)
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    // String object "ROOT" is the inidication for displaying root nodes
    if ((inputElement instanceof String) && "ROOT".equals(((String) inputElement).toString())) {
      if (this.pidcVersion != null) { // ICDM-1030
        return new Object[] {
            this.dataHandler.getAttrRootNode(),
            this.dataHandler.getUseCaseRootNode(),
            this.dataHandler.getProjFavUcRootNode(),
            this.dataHandler.getUserFavRootNode() };
      }
      return new Object[] {
          this.dataHandler.getAttrRootNode(),
          this.dataHandler.getUseCaseRootNode(),
          this.dataHandler.getUserFavRootNode() };
    }
    if (inputElement instanceof AttrRootNode) {
      return ((AttrRootNode) inputElement).getSuperGroups().toArray();
    }
    if (inputElement instanceof UseCaseRootNode) {
      return ((UseCaseRootNode) inputElement).getUseCaseGroups(false).toArray();
    }
    if (inputElement instanceof ProjFavUcRootNode) { // ICDM-1030
      return this.dataHandler.getUcDataHandler().getProjfavs().toArray();
    }
    if (inputElement instanceof UserFavUcRootNode) { // ICDM-1028
      return this.dataHandler.getUcDataHandler().getPrivateUsecases().toArray();

    }
    if (inputElement instanceof FavUseCaseItemNode) { // ICDM-1030
      if (CommonUtils.isNull(((FavUseCaseItemNode) inputElement).getChildFavNodes())) {
        return ((FavUseCaseItemNode) inputElement).getChildUCItems(this.dataHandler.getUcDataHandler()).toArray();
      }
      return ((FavUseCaseItemNode) inputElement).getChildFavNodes().toArray();
    }
    if (inputElement instanceof UseCaseGroupClientBO) {
      final SortedSet<UseCaseGroupClientBO> subGroupSet = ((UseCaseGroupClientBO) inputElement).getChildGroupSet(false);
      if (!subGroupSet.isEmpty()) {
        return subGroupSet.toArray();
      }
      return ((UseCaseGroupClientBO) inputElement).getUseCaseSet(false).toArray();
    }
    if (inputElement instanceof UsecaseClientBO) {
      UsecaseClientBO ucClientBO = (UsecaseClientBO) inputElement;
      ucClientBO.setUsecaseEditorModel(fetchUseCaseDetailsModel(ucClientBO));
      return ((UsecaseClientBO) inputElement).getUseCaseSectionSet(false).toArray();
    }
    if (inputElement instanceof UseCaseSectionClientBO) {
      return ((UseCaseSectionClientBO) inputElement).getChildSectionSet(false).toArray();
    }
    return new Object[] {};
  }


  /**
   * @return
   */
  private SortedSet<FavUseCaseItemNode> getPrivateUsecases() {
    UseCaseFavNodesMgr ucFavMgr;
    try {
      ucFavMgr = new UseCaseFavNodesMgr(new CurrentUserBO().getUser(), this.dataHandler.getUcDataHandler());
      this.dataHandler.getUcDataHandler().setUcFavMgr(ucFavMgr);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.apic.ui.Activator.PLUGIN_ID);
    }
    return this.dataHandler.getUcDataHandler().getRootProjectUcFavNodes();
  }

  /**
   * @return
   */
  private SortedSet<FavUseCaseItemNode> getProjectFavNodes() {
    UseCaseFavNodesMgr ucFavMgr = new UseCaseFavNodesMgr(this.dataHandler.getUcDataHandler().getPidcversion(),
        this.dataHandler.getUcDataHandler());
    this.dataHandler.getUcDataHandler().setUcFavMgr(ucFavMgr);

    return this.dataHandler.getUcDataHandler().getRootProjectUcFavNodes();
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang. Object)
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    // Generate children for AttrRootNode
    if (parentElement instanceof AttrRootNode) {
      return ((AttrRootNode) parentElement).getSuperGroups().toArray();
    }
    if (parentElement instanceof AttrSuperGroup) {
      AttrSuperGroup spGrp = (AttrSuperGroup) parentElement;
      return (this.dataHandler.getAttrRootNode().getGroups(spGrp)).toArray();
    }
    if (parentElement instanceof ProjFavUcRootNode) { // ICDM-1030
      return this.dataHandler.getUcDataHandler().getProjfavs().toArray();
    }
    if (parentElement instanceof UserFavUcRootNode) { // ICDM-1028
      return this.dataHandler.getUcDataHandler().getPrivateUsecases().toArray();
    }
    if (parentElement instanceof FavUseCaseItemNode) { // ICDM-1030
      if (CommonUtils.isNull(((FavUseCaseItemNode) parentElement).getChildFavNodes())) {
        SortedSet<IUseCaseItemClientBO> childUCItems =
            ((FavUseCaseItemNode) parentElement).getChildUCItems(this.dataHandler.getUcDataHandler());
        return childUCItems.toArray();
      }
      return ((FavUseCaseItemNode) parentElement).getChildFavNodes().toArray();
    }
    // Generate children for UseCaseRootNode
    if (parentElement instanceof UseCaseRootNode) {
      SortedSet<UseCaseGroupClientBO> useCaseGroups = ((UseCaseRootNode) parentElement).getUseCaseGroups(false);
      return useCaseGroups.toArray();
    }
    if (parentElement instanceof UseCaseGroupClientBO) {
      final SortedSet<UseCaseGroupClientBO> subGroupSet =
          ((UseCaseGroupClientBO) parentElement).getChildGroupSet(false);
      if (!subGroupSet.isEmpty()) {
        return subGroupSet.toArray();
      }
      SortedSet<UsecaseClientBO> useCaseSet = ((UseCaseGroupClientBO) parentElement).getUseCaseSet(false);
      return useCaseSet.toArray();
    }
    if (parentElement instanceof UsecaseClientBO) {

      UsecaseClientBO ucClientBO = (UsecaseClientBO) parentElement;
      ucClientBO.setUsecaseEditorModel(fetchUseCaseDetailsModel(ucClientBO));
      SortedSet<UseCaseSectionClientBO> useCaseSectionSet = ucClientBO.getUseCaseSectionSet(false);
      return useCaseSectionSet.toArray();
    }
    // Generate children for usecase section
    if (parentElement instanceof UseCaseSectionClientBO) {
      SortedSet<UseCaseSectionClientBO> childSectionSet =
          ((UseCaseSectionClientBO) parentElement).getChildSectionSet(false);
      return childSectionSet.toArray();
    }

    return new Object[] {};
  }


  /**
   * @param usecaseEditorModel
   * @param ucClientBO
   * @return
   */
  private UsecaseEditorModel fetchUseCaseDetailsModel(final UsecaseClientBO ucClientBO) {
    return this.dataHandler.getUcDataHandler().getUseCaseDetailsModel().getUsecaseDetailsModelMap()
        .get(ucClientBO.getUseCase().getId());
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object )
   */
  @Override
  public Object getParent(final Object element) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang. Object)
   */
  @Override
  public boolean hasChildren(final Object element) {
    // Check if node has child nodes
    if (element instanceof AttrRootNode) {
      return !((AttrRootNode) element).getSuperGroups().isEmpty();
    }
    if (element instanceof AttrSuperGroup) {
      return !(this.dataHandler.getAttrRootNode().getGroups((AttrSuperGroup) element).isEmpty());
    }
    if (element instanceof UseCaseRootNode) {
      return true;
    }
    if (element instanceof ProjFavUcRootNode) { // ICDM-1030

      return CommonUtils.isNotEmpty(this.dataHandler.getUcDataHandler().getProjfavs());
    }
    if (element instanceof UserFavUcRootNode) { // ICDM-1028
      return CommonUtils.isNotEmpty(this.dataHandler.getUcDataHandler().getPrivateUsecases());
    }
    if (element instanceof FavUseCaseItemNode) { // ICDM-1030
      FavUseCaseItemNode favUcItemNode = (FavUseCaseItemNode) element;
      return (CommonUtils.isNotEmpty(favUcItemNode.getChildFavNodes()) ||
          (CommonUtils.isNotNull(favUcItemNode.getFavUcItem()) &&
              CommonUtils.isNotEmpty(favUcItemNode.getChildUCItems(this.dataHandler.getUcDataHandler()))));
    }
    if (element instanceof UseCaseGroupClientBO) {
      if (!((UseCaseGroupClientBO) element).getChildGroupSet(false).isEmpty()) {
        return true;
      }
      return !((UseCaseGroupClientBO) element).getUseCaseSet(false).isEmpty();
    }
    if (element instanceof UsecaseClientBO) {
      UsecaseClientBO ucClientBO = (UsecaseClientBO) element;
      ucClientBO.setUsecaseEditorModel(fetchUseCaseDetailsModel(ucClientBO));
      return !(ucClientBO.getUseCaseSectionSet(false).isEmpty());
    }
    // check children for UseCaseSection
    if (element instanceof UseCaseSectionClientBO) {
      return !((UseCaseSectionClientBO) element).getChildSectionSet(false).isEmpty();
    }
    return false;
  }


  /**
   * @return the dataHandler
   */
  public OutLineViewDataHandler getDataHandler() {
    return this.dataHandler;
  }
}
