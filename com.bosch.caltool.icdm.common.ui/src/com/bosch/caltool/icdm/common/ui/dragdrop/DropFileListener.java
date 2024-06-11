/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dragdrop;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author say8cob
 */
public class DropFileListener {

  private Text target;

  private Table tableTarget;

  private List listTarget;

  private final String[] supportedFileExtension;


  private final FileTransfer fileTransfer = FileTransfer.getInstance();

  Transfer[] types = new Transfer[] { this.fileTransfer };

  private String dropFilePath;

  private boolean isEditable;

  DropTarget dropTarget = null;

  /**
   * @param target as text , List and table items
   * @param supportedFileExtension supported extentions
   */
  public DropFileListener(final Object target, final String[] supportedFileExtension) {
    initializeDropTarget(target);
    this.supportedFileExtension = supportedFileExtension;
  }

  /**
   * Method to add DropTarget Listener
   *
   * @param isFileName to set file name in text item
   * @param b
   */
  public void addDropFileListener(final boolean isFileName) {

    // to receive data in file format
    this.dropTarget.setTransfer(this.types);
    // DropTargetEventListener
    this.dropTarget.addDropListener(new DropTargetAdapter() {

      @Override
      public void drop(final DropTargetEvent event) {
        if (DropFileListener.this.isEditable) {
          boolean isInvalidFile = false;
          if (DropFileListener.this.fileTransfer.isSupportedType(event.currentDataType)) {
            String[] files = (String[]) event.data;
            if (DropFileListener.this.supportedFileExtension != null) {
              String extension = FilenameUtils.getExtension(files[0]).toLowerCase();
              for (String fileExt : DropFileListener.this.supportedFileExtension) {
                if (extension.equalsIgnoreCase(fileExt) || ("*." + extension).equalsIgnoreCase(fileExt)) {
                  setTargetValue(files[0], isFileName);
                  isInvalidFile = false;
                  break;
                }
                isInvalidFile = true;
              }
            }
            else {
              setTargetValue(files[0], isFileName);
            }
            if (isInvalidFile) {
              CDMLogger.getInstance().errorDialog("The dropped file format is not allowed.", Activator.PLUGIN_ID);
            }
          }
        }
      }
    });
  }

  private void setTargetValue(final String filePath, final boolean isFileName) {
    if (this.target != null) {
      DropFileListener.this.dropFilePath = filePath;
      DropFileListener.this.target.setText(isFileName ? FilenameUtils.getName(filePath) : filePath);
    }
    else if (this.tableTarget != null) {
      DropFileListener.this.dropFilePath = filePath;
      TableItem tableItem = new TableItem(this.tableTarget, SWT.NONE);
      tableItem.setText(isFileName ? FilenameUtils.getName(filePath) : filePath);
    }
    else if (this.listTarget != null) {
      DropFileListener.this.dropFilePath = filePath;
      this.listTarget.add(isFileName ? FilenameUtils.getName(filePath) : filePath);
    }
  }

  private void initializeDropTarget(final Object targetObj) {
    if (targetObj instanceof Text) {
      this.target = (Text) targetObj;
      this.dropTarget = new DropTarget(this.target, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
    }
    else if (targetObj instanceof Table) {
      this.tableTarget = (Table) targetObj;
      this.dropTarget = new DropTarget(this.tableTarget, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
    }
    else if (targetObj instanceof List) {
      this.listTarget = (List) targetObj;
      this.dropTarget = new DropTarget(this.listTarget, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
    }
  }

  /**
   * @return the dropFilePath
   */
  public String getDropFilePath() {
    return this.dropFilePath;
  }


  /**
   * @param dropFilePath the dropFilePath to set
   */
  public void setDropFilePath(final String dropFilePath) {
    this.dropFilePath = dropFilePath;
  }

  /**
   * @return the isEditable
   */
  public boolean isEditable() {
    return this.isEditable;
  }

  /**
   * @param isEditable the isEditable to set
   */
  public void setEditable(final boolean isEditable) {
    this.isEditable = isEditable;
  }


}

