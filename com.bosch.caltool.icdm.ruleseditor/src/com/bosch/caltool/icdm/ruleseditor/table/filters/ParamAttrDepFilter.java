package com.bosch.caltool.icdm.ruleseditor.table.filters;


import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;

/**
 * This class handles the common filters for the Attributes table viewer
 */
public class ParamAttrDepFilter<D extends IParameterAttribute, P extends IParameter> extends AbstractViewerFilter {


  private final ParameterDataProvider<D, P> paramDataProvider;

  /**
   * @param paramDataProvider paramDataProvider
   */
  public ParamAttrDepFilter(final ParameterDataProvider<D, P> paramDataProvider) {
    this.paramDataProvider = paramDataProvider;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    boolean flag = false;
    if (element instanceof IParameterAttribute) {
      final IParameterAttribute paramAttr = (IParameterAttribute) element;

      flag = checkText(this.paramDataProvider.getAttribute(paramAttr));
    }
    else if (element instanceof Attribute) {
      flag = checkText((Attribute) element);
    }

    return flag;

  }

  /**
   * @param attribute attribute
   * @return True if the properties are matching.
   */
  private boolean checkText(final Attribute parmAttr) {
    boolean flag = false;
    // Match the Text with the Attribute properties. If any one matches return true.
    if (matchText(parmAttr.getName())) {
      flag = true;
    }

    if (matchText(parmAttr.getDescription()) || matchText(parmAttr.getUnit())) {
      flag = true;
    }

    return flag;
  }
}