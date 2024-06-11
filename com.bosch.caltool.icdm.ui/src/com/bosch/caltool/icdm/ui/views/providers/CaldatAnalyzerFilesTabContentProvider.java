/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.views.providers;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultFileModel;

/**
 * @author pdh2cob
 */
public class CaldatAnalyzerFilesTabContentProvider implements IStructuredContentProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getElements(final Object arg0) {
    return ((List<CaldataAnalyzerResultFileModel>) arg0).toArray();


  }

}
