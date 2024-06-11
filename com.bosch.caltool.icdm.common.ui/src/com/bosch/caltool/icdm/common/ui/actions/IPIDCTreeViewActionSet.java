/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.actions;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;

import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;


/**
 * @author dmo5cob
 */
public interface IPIDCTreeViewActionSet {

  /**
   * @return IDoubleClickListener
   */
  IDoubleClickListener getDoubleClickListener();

  /**
   * @param selectedObject selected object
   * @param keyCode Key Code
   * @param stateMask State Mask if anything is pressed
   * @param viewer TreeViewer
   */
  void getKeyListenerToViewer(Object selectedObject, int keyCode, int stateMask, TreeViewer viewer);

  /**
   * @param funcList list of functions
   * @param paramName parameter name
   */
  void openFunctionSelectionDialog(List<Function> funcList, String paramName);

  /**
   * Creates rule in rule editor using the checked value
   *
   * @param menuManagr menuManagr
   * @param selectedObj selectedObj
   */
  void createRuleAction(final IMenuManager menuManagr, final Object selectedObj);

  /**
   * @param manager
   * @param pidVersion
   * @param viewer
   * @param pidcTreeNode
   */
  void onRightClickOfPidcTreeNode(IMenuManager manager, TreeViewer viewer, PidcTreeNode pidcTreeNode);

  /**
   * Open A 2 L file.
   *
   * @param pidcA2lId the pidc A 2 l id
   */
  void openA2LFile(Long pidcA2lId);

  /**
   * @param cdrResult cdrResult
   * @param paramName paramName
   * @param variantId
   */
  void openReviewResult(final CDRReviewResult cdrResult, final String paramName, Long variantId);

  /**
   * @param rvwResult rvwResult
   * @param paramName paramName
   */
  void openReviewResult(final RvwVariant rvwResult, final String paramName);

  /**
   * @param pidCards pidCards
   */
  void onRightClickOfAPRJnew(final Set<PidcVersion> pidCards);


  /**
   * @param manager
   * @param pidcVersion
   */
  void openComparePidcEditor(final IMenuManager manager, final List<IProjectObject> pidcVersion);
}
