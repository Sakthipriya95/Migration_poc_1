/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views;

/**
 * @author rgo7cob
 */
public interface IEditorWithStructure {

  public IStructurePageCreator getStrucurePageCreator(PIDCDetailsViewPart viewPart);

}
