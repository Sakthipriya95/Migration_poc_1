package com.bosch.caltool.icdm.ruleseditor.listeners;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseClickAction;
import org.eclipse.swt.events.MouseEvent;

import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleActionSet;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;

/**
 * @author dmo5cob
 */
public class TableDoubleClickListeners<D extends IParameterAttribute, P extends IParameter>
    implements IMouseClickAction {

  /**
   * AttributesInfoViewPart instance
   */
  private final AbstractFormPage pageInstance;
  /**
   * Parameter data provider instance
   */
  private ParameterDataProvider<D, P> parameterDataProvider;

  /**
   * The parameterized constructor
   *
   * @param page instance
   */
  public TableDoubleClickListeners(final AbstractFormPage page,
      final ParameterDataProvider<D, P> parameterDataProvider) {
    this.parameterDataProvider = parameterDataProvider;
    this.pageInstance = page;
  }


  /**
   * This method listens double click on tableviewer
   */
  public void addSummaryPageDoubleClickListener() {
    if (this.pageInstance instanceof ListPage) {
      final ListPage listPage = (ListPage) this.pageInstance;
      // Double click action for Attributes table viewer
      final IStructuredSelection selection =
          (IStructuredSelection) listPage.getParamTabSec().getParamTab().getSelectionProvider().getSelection();
      if ((selection != null) && !selection.isEmpty()) {
        final Object selectedElement = selection.getFirstElement();
        ReviewRuleActionSet paramActionSet = new ReviewRuleActionSet();
        // ICDM-1190
        // open Edit rule dialog
        openEditRuleDialog(listPage, selectedElement, paramActionSet);
      }
    }

  }

  /**
   * Open edit rule dialog
   * 
   * @param listPage ListPage
   * @param selectedElement Object
   * @param paramActionSet ReviewRuleActionSet
   * @param param IParameter<?>
   */
  private void openEditRuleDialog(final ListPage listPage, final Object selectedElement,
      final ReviewRuleActionSet paramActionSet) {
    this.parameterDataProvider = listPage.getEditor().getEditorInput().getParamDataProvider();
    ParamCollectionDataProvider dataProvider = listPage.getEditor().getEditorInput().getDataProvider();
    IParameter param = null;
    // fetch param based on selection
    if (selectedElement instanceof DefaultRuleDefinition) {
      param = getParamForRule();

    }
    else if (selectedElement instanceof ReviewRule) {
      param = getParamForRule();
    }
    else if (selectedElement instanceof IParameter) {
      param = (IParameter) selectedElement;
    }
    if (((null != param) && !this.parameterDataProvider.hasDependency(param)) ||
        (selectedElement instanceof DefaultRuleDefinition) || (selectedElement instanceof ReviewRule)) {
      paramActionSet.openEditRuleDialog(selectedElement, listPage.getCdrFunction(), listPage.getEditor(), null,
          !(dataProvider.isModifiable(listPage.getCdrFunction()) &&
              dataProvider.isRulesModifiable(listPage.getCdrFunction())),
          null, null, null, dataProvider, this.parameterDataProvider);
    }
  }

  /**
   * @return parameter instance
   */
  private IParameter getParamForRule() {
    // TODO
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(final NatTable arg0, final MouseEvent arg1) {
    addSummaryPageDoubleClickListener();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isExclusive() {
    // TODO Auto-generated method stub
    return false;
  }
}
