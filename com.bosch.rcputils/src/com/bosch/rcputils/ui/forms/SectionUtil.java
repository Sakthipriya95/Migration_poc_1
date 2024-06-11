/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.ui.forms;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;


/**
 * @author mga1cob
 */
public final class SectionUtil {


  /**
   * SectionUtil instance
   */
  private static SectionUtil sectionUtil;

  /**
   * The private constructor
   */
  private SectionUtil() {
    // The private constructor
  }

  /**
   * This method returns SectionUtil instance
   * 
   * @return SectionUtil
   */
  public static SectionUtil getInstance() {
    if (sectionUtil == null) {
      sectionUtil = new SectionUtil();
    }
    return sectionUtil;
  }


  /**
   * This method creates the UI form section
   * 
   * @param composite instance
   * @param toolkit instance
   * @param sectionName instance
   * @return Section instance
   */
  public Section createSection(Composite composite, final FormToolkit toolkit, String sectionName) {
    Section section = toolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    section.setText(sectionName);
    section.setExpanded(true);
    return section;
  }

  /**
   * This method creates the UI form section with a style
   * 
   * @param composite instance
   * @param toolkit instance
   * @param sectionName instance
   * @param style int
   * @return Section instance
   */
  public Section createSection(Composite composite, final FormToolkit toolkit, String sectionName, int style) {
    Section section = toolkit.createSection(composite, style);
    section.setText(sectionName);
    section.setExpanded(true);
    return section;
  }

  /**
   * This method creates UI form section
   * 
   * @param composite instance
   * @param toolkit instance
   * @return Section instance
   */
  public Section createSection(Composite composite, final FormToolkit toolkit) {
    Section section = toolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    section.setExpanded(true);
    return section;
  }

  /**
   * This method creates UI form section
   * 
   * @param composite instance
   * @param toolkit instance
   * @param layoutData instance
   * @return Section instance
   */
  public Section createSection(Composite composite, final FormToolkit toolkit, GridData layoutData) {
    Section section = toolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    section.setLayoutData(layoutData);
    section.setExpanded(true);
    return section;
  }


  /**
   * This method creates UI form section
   * 
   * @param composite instance
   * @param toolkit instance
   * @param layoutData instance
   * @param sectionName instance
   * @return Section instance
   */
  public Section createSection(Composite composite, final FormToolkit toolkit, GridData layoutData, String sectionName) {
    Section section = toolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    section.setLayoutData(layoutData);
    section.setText(sectionName);
    section.setExpanded(true);
    return section;
  }

  /**
   * This method creates UI form section
   * 
   * @param composite instance
   * @param toolkit instance
   * @param layoutData instance
   * @param sectionName instance
   * @param descControlEnable defines description control of section should be enable or not
   * @return Section instance
   */
  public Section createSection(Composite composite, final FormToolkit toolkit, GridData layoutData, String sectionName,
      boolean descControlEnable) {
    Section section = toolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    section.setLayoutData(layoutData);
    section.setText(sectionName);
    section.setExpanded(true);
    section.getDescriptionControl().setEnabled(descControlEnable);
    return section;
  }
}
