/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.bosch.calcomp.externallink.creation.LinkCreator;
import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author elm1cob
 */
public class PIDCShortcutAction extends Action {

  /**
   * desktop constant
   */
  private final String WINDOWS_DESKTOP = "Desktop";
  /**
   * instance of pidc version
   */
  private final PidcVersion pidcVer;

  /**
   * @param pidcVersn PidcVersion
   */
  public PIDCShortcutAction(final PidcVersion pidcVersn) {
    this.pidcVer = pidcVersn;
    setText("Create Desktop Shortcut");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PIDC_16X16);
    setImageDescriptor(imageDesc);
  }

  /**
   * Method returns the windows full path
   *
   * @return String
   */
  public String getWindowsFullPath() {
    // return the current user desktop path
    return System.getenv("userprofile") + "\\" + this.WINDOWS_DESKTOP + "\\" + this.pidcVer.getName() + ".URL";
  }

  /**
   * This method creates the pidc url shortcut
   *
   * @param target String
   * @throws IOException
   */
  public void createPidcUrlShortcut(final String target) throws IOException {
    // check if file already exists
    if (new File(getWindowsFullPath()).exists()) {
      MessageDialogUtils.getInfoMessageDialog("Info", "Shortcut already exists");
    }
    else {
      // create the file shortcut
      try (FileWriter fw = new FileWriter(getWindowsFullPath())) {
        fw.write("[InternetShortcut]\n");
        fw.write("URL=" + target + "\n");
        fw.write("IconFile=" + "\"%APPDATA%\\iCDM\\icons\\pidc.ico\"" + "\n");
        fw.write("IconIndex=0\n");
        fw.flush();
        MessageDialogUtils.getInfoMessageDialog("Info", "'" + this.pidcVer.getName() + "' shortcut created");
      }
    }
  }

  @Override
  public void run() {
    // initialise the link creator for the pidc version
    LinkCreator pidcLnk = new LinkCreator(this.pidcVer);
    // get the target url , to create the shorcut
    String target = pidcLnk.getUrl();
    try {
      createPidcUrlShortcut(target);
    }
    catch (IOException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }
}
