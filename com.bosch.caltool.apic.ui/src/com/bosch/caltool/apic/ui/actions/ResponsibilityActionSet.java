/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.wizards.A2lRespMergeWizard;
import com.bosch.caltool.apic.ui.wizards.A2lRespMergeWizardDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * This class contains actions from responsibility page
 *
 * @author mkl2cob
 */
public class ResponsibilityActionSet {

  /**
   * This method adds the right click option to set responsibility .
   *
   * @param menuMgr MenuManager instance
   * @param enable true if the context option can be enabled
   */
  public void setBoschResponsibleAction(final MenuManager menuMgr, final IStructuredSelection selection,
      final boolean enable) {

    List<A2lResponsibility> a2lWpRespList = selection.toList();
    final Action setResp = new Action() {

      @Override
      public void run() {
        List<A2lResponsibility> updRespList = new ArrayList<>();
        for (A2lResponsibility a2lResponsibility : a2lWpRespList) {
          // iterate through selected responsibilities
          A2lResponsibility updResp = a2lResponsibility.clone();
          // set resp tp Robert Bosch
          updResp.setRespType(WpRespType.RB.getCode());
          if (CommonUtils.isEmptyString(updResp.getLDepartment())) {
            // if dept is empty , set alias name to dept
            updResp.setLDepartment(updResp.getAliasName());
          }
          updRespList.add(updResp);
        }
        try {
          // call service to update all responsibilities
          new A2lResponsibilityServiceClient().update(updRespList);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    };
    // set text and add the action to menu manager
    setResp.setText(WpRespType.RB.getDispName());
    setResp.setEnabled(enable);
    menuMgr.add(setResp);

  }

  /**
   * This method adds the right click option to set responsibility .
   *
   * @param menuMgr MenuManager instance
   * @param enable true if the context option can be enabled
   */
  public void setCustomerResponsibleAction(final MenuManager menuMgr, final IStructuredSelection selection,
      final boolean enable) {

    List<A2lResponsibility> a2lWpRespList = selection.toList();
    final Action setResp = new Action() {

      @Override
      public void run() {
        List<A2lResponsibility> updRespList = new ArrayList<>();
        for (A2lResponsibility a2lResponsibility : a2lWpRespList) {
          // iterate through selected responsibilities
          A2lResponsibility updResp = a2lResponsibility.clone();
          // set type to customer
          updResp.setRespType(WpRespType.CUSTOMER.getCode());
          updRespList.add(updResp);
        }
        try {
          // call the service to update the list of responsibilities
          new A2lResponsibilityServiceClient().update(updRespList);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    };
    // set text and add the action to menu manager
    setResp.setText(WpRespType.CUSTOMER.getDispName());
    setResp.setEnabled(enable);
    menuMgr.add(setResp);

  }

  /**
   * This method adds the right click option to set responsibility .
   *
   * @param menuMgr MenuManager instance
   * @param enable true if the context option can be enabled
   */
  public void setOthersResponsibleAction(final MenuManager menuMgr, final IStructuredSelection selection,
      final boolean enable) {

    List<A2lResponsibility> a2lWpRespList = selection.toList();
    final Action setResp = new Action() {

      @Override
      public void run() {
        List<A2lResponsibility> updRespList = new ArrayList<>();
        for (A2lResponsibility a2lResponsibility : a2lWpRespList) {
          // iterate through selected responsibilities
          A2lResponsibility updResp = a2lResponsibility.clone();
          // set the resp type to others
          updResp.setRespType(WpRespType.OTHERS.getCode());
          updRespList.add(updResp);
        }
        try {
          // call the service to update the responsibilities
          new A2lResponsibilityServiceClient().update(updRespList);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    };
    // set text and add the action to menu manager
    setResp.setText(WpRespType.OTHERS.getDispName());
    setResp.setEnabled(enable);
    menuMgr.add(setResp);

  }

  /**
   * This method adds the right click option to set responsibility .
   *
   * @param imenumanager menu manager
   * @param a2lResponsibility a2l responsibilty
   * @param a2lWpRespList a2lWpRespList
   */
  public void setMergeA2lResponsibleAction(final IMenuManager imenumanager, final A2lResponsibility a2lResponsibility,
      final List<A2lResponsibility> a2lWpRespList) {

    final Action setResp = new Action() {

      @Override
      public void run() {
        A2lRespMergeWizard a2lRespMergeWizard = new A2lRespMergeWizard(a2lWpRespList, a2lResponsibility, false);
        A2lRespMergeWizardDialog a2lRespMergeWizardDialog =
            new A2lRespMergeWizardDialog(Display.getCurrent().getActiveShell(), a2lRespMergeWizard, false);
        a2lRespMergeWizardDialog.create();
        // open report dialog
        a2lRespMergeWizardDialog.open();
      }
    };
    // set text and add the action to menu manager
    setResp.setText(a2lResponsibility.getAliasName());
    imenumanager.add(setResp);

  }
}
