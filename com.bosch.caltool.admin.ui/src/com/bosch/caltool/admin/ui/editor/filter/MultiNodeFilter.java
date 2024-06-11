package com.bosch.caltool.admin.ui.editor.filter;

import com.bosch.caltool.icdm.common.bo.apic.NodeType;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;


/**
 * Text Filter Class
 *
 * @author say8cob
 */
public class MultiNodeFilter extends AbstractViewerFilter {


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean selectElement(final Object element) {
    if (element instanceof NodeAccessInfo) {
      // If selected element is a BC
      final NodeAccessInfo nodeAccessElement = (NodeAccessInfo) element;

      // Filter the table
      return matchText(nodeAccessElement.getNodeName()) || matchText(nodeAccessElement.getNodeDesc()) ||
          matchText(NodeType.getNodeType(nodeAccessElement.getAccess().getNodeType()).getModelType().getTypeName());
    }
    return false;
  }

}
