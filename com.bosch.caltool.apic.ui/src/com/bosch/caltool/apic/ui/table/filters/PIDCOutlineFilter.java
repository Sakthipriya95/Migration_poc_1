/**
 *
 */
package com.bosch.caltool.apic.ui.table.filters;

import java.util.SortedSet;

import com.bosch.caltool.apic.ui.editors.compare.PidcNattableRowObject;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.OutLineUseCaseFilterHandler;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * OutlineFilter-This class handles the filters based on Outline view selection
 *
 * @author adn1cob
 */
public class PIDCOutlineFilter extends AbstractViewerFilter {


  // members to indicate if group filer or superGroup filter
  private boolean superGroup;

  private boolean group;
  private boolean common;

  private String selectedNode;

  private IUseCaseItemClientBO ucItem;

  /**
   * Icdm-1030 FavUseCaseItemNode instance
   */
  private com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode favUCItemNode;

  /**
   * Icdm-1030 true if selection is FavUseCaseItemNode
   */
  private boolean isFavUcItem;

  private boolean isUcItemSelected;

  private final PidcDataHandler pidcDataHandler;

  private final OutLineViewDataHandler outlineDataHandler;


  // ICDM-1865


  // ICDM-1865
  /**
   * @param pidcDataHandler PidcDataHandler
   * @param outlineDataHandler OutLineViewDataHandler
   */
  public PIDCOutlineFilter(final PidcDataHandler pidcDataHandler, final OutLineViewDataHandler outlineDataHandler) {
    this.pidcDataHandler = pidcDataHandler;
    this.outlineDataHandler = outlineDataHandler;
  }

  /**
   * @return the isSuperGroup
   */
  public boolean isSuperGroup() {
    return this.superGroup;
  }

  /**
   * @param isSuperGroup the isSuperGroup to set
   */
  public void setSuperGroup(final boolean isSuperGroup) {
    this.superGroup = isSuperGroup;
  }

  /**
   * @return the isGroup
   */
  public boolean isGroup() {
    return this.group;
  }

  /**
   * @param isGroup the isGroup to set
   */
  public void setGroup(final boolean isGroup) {
    this.group = isGroup;
  }

  /**
   * @return the isCommon
   */
  public boolean isCommon() {
    return this.common;
  }

  /**
   * @param isCommon the isCommon to set
   */
  public void setCommon(final boolean isCommon) {
    this.common = isCommon;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setFilterText(final String filterText) {
    // The filter text is same as the selected node in the Outline tree.
    setFilterText(filterText, false);
    this.selectedNode = filterText;
  }

  // Icdm-296
  private void clearAllFlags() {
    this.superGroup = false;
    this.common = false;
    this.group = false;
    this.isUcItemSelected = false;
    this.isFavUcItem = false;
  }

  /**
   * @param ucItem use case item
   */
  public void setUseCaseItem(final IUseCaseItemClientBO ucItem) {
    this.ucItem = ucItem;
    clearAllFlags();
    this.isUcItemSelected = true;
    setFilterText(ucItem.getName());
  }

  /**
   * icdm-1030
   *
   * @param favUcItem favourite use case item
   */
  public void setFavUseCaseItem(final FavUseCaseItemNode favUcItem) {
    this.favUCItemNode = favUcItem;
    clearAllFlags();
    this.isFavUcItem = true;
    setFilterText(favUcItem.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // Check if Attribute object
    if (element instanceof PidcNattableRowObject) {
      PidcNattableRowObject pidcAttr = (PidcNattableRowObject) element;
      Attribute attribute = pidcAttr.getAttribute();
      // if SuperGroup selected, apply filter to super group
      if (this.superGroup) {
        AttrSuperGroup attrSuperGroup = this.pidcDataHandler.getAttributeSuperGroupMap()
            .get(this.pidcDataHandler.getAttributeGroupMap()
                .get(this.pidcDataHandler.getAttributeMap().get(pidcAttr.getAttribute().getId()).getAttrGrpId())
                .getSuperGrpId());
        if (attrSuperGroup.getName().equals(this.selectedNode)) {
          return true;
        }
      }
      // if Group selected, apply filter to group
      else if (this.group) {
        AttrGroup attrGroup = this.pidcDataHandler.getAttributeGroupMap().get(attribute.getAttrGrpId());
        if (attrGroup.getName().equals(this.selectedNode)) {
          return true;
        }
      }
      else if (this.common) {
        // ICDM-1865
        if (this.selectedNode.equalsIgnoreCase(ApicUiConstants.SYS_VIEW_ROOT_NODE)) {
          return true;
        }
        else if (this.selectedNode.equalsIgnoreCase(ApicUiConstants.UC_ROOT_NODE)) {

          SortedSet<UseCaseGroupClientBO> useCasesGrps =
              this.outlineDataHandler.getUseCaseRootNode().getUseCaseGroups(false);

          for (UseCaseGroupClientBO usecaseGrp : useCasesGrps) {
            OutLineUseCaseFilterHandler outLineUseCaseFilterHandler =
                new OutLineUseCaseFilterHandler(usecaseGrp, this.outlineDataHandler.getUcDataHandler());
            if (outLineUseCaseFilterHandler.isMapped(attribute)) {
              return true;
            }
          }
        }
        else if (this.selectedNode.equalsIgnoreCase(ApicUiConstants.PROJECT_UC_ROOT_NODE)) {
          SortedSet<FavUseCaseItemNode> projectUcNodes = this.outlineDataHandler.getUcDataHandler().getProjfavs();
          if (null != projectUcNodes) {
            for (FavUseCaseItemNode ucNode : projectUcNodes) {
              if (ucNode.isMapped(attribute)) {
                return true;
              }
            }
          }
          // Story 234664 - including mandatory attr for the outling
          // filter Project Use Cases
          if (pidcAttr.getProjectAttributeHandler().isMandatory()) {
            return true;
          }
        }
        else if (this.selectedNode.equalsIgnoreCase(ApicUiConstants.PRIVATE_UC_ROOT_NODE)) {
          SortedSet<FavUseCaseItemNode> privateUcNodes =
              this.outlineDataHandler.getUcDataHandler().getPrivateUsecases();
          if (null != privateUcNodes) {
            for (FavUseCaseItemNode ucNode : privateUcNodes) {
              if (ucNode.isMapped(attribute)) {
                return true;
              }
            }
          }
        }
      }
      // Icdm-296
      else if (this.isUcItemSelected) {
        return new OutLineUseCaseFilterHandler(this.ucItem, this.outlineDataHandler.getUcDataHandler())
            .isMapped(attribute);

      }
      else if (this.isFavUcItem) {
        return this.favUCItemNode.isMapped(attribute);
      }
    }
    return false;
  }

  /**
   * @return the ucItem
   */
  public IUseCaseItemClientBO getUcItem() {
    return this.ucItem;
  }

  /**
   * @return Matcher
   */
  public Matcher getUcOutlineMatcher() {

    return new PIDCNatInputMatcher<IProjectAttribute>();
  }

  private class PIDCNatInputMatcher<E> implements Matcher<E> {

    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof PidcNattableRowObject) {
        return selectElement(element);
      }
      return false;
    }
  }


  /**
   * @return the selectedNode
   */
  public String getSelectedNode() {
    return this.selectedNode;
  }


  /**
   * @param selectedNode the selectedNode to set
   */
  public void setSelectedNode(final String selectedNode) {
    this.selectedNode = selectedNode;
  }


  /**
   * @return the favUCItemNode
   */
  public com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode getFavUCItemNode() {
    return this.favUCItemNode;
  }


  /**
   * @param favUCItemNode the favUCItemNode to set
   */
  public void setFavUCItemNode(final com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode favUCItemNode) {
    this.favUCItemNode = favUCItemNode;
  }


  /**
   * @return the isFavUcItem
   */
  public boolean isFavUcItem() {
    return this.isFavUcItem;
  }


  /**
   * @param isFavUcItem the isFavUcItem to set
   */
  public void setFavUcItem(final boolean isFavUcItem) {
    this.isFavUcItem = isFavUcItem;
  }


  /**
   * @return the isUcItem
   */
  public boolean isUcItemSelected() {
    return this.isUcItemSelected;
  }


  /**
   * @return the pidcDataHandler
   */
  public PidcDataHandler getPidcDataHandler() {
    return this.pidcDataHandler;
  }


  /**
   * @return the outlineDataHandler
   */
  public OutLineViewDataHandler getOutlineDataHandler() {
    return this.outlineDataHandler;
  }

}