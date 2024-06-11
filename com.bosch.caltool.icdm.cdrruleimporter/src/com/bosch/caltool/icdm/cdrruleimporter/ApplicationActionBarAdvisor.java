package com.bosch.caltool.icdm.cdrruleimporter;

import java.io.IOException;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.bosch.caltool.icdm.cdrruleimporter.plausibel.PlausibelImporter;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

  public ApplicationActionBarAdvisor(final IActionBarConfigurer configurer) {
    super(configurer);
  }

  @Override
  protected void makeActions(final IWorkbenchWindow window) {

    // loads a complete directory. Parameter is just the directory path

    try {
      new PlausibelImporter().startImport("C:\\Temp\\Test", false);
    }
    catch (IOException e) {
      e.printStackTrace();
    }


    // loads single file. Parameters are function name, path of the file and sheet name in Excel
    // new PlausibelImporter().startImport("AMSV","C:\\Archiv\\90_send_to_iCDM\\Labelliste_AMSV.xls","%AMSV");


  }

  @Override
  protected void fillMenuBar(final IMenuManager menuBar) {}

}
