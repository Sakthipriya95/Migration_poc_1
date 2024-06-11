/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.oss.display;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.bosch.calcomp.adapter.logger.util.LoggerUtil;




/**
 *
 */
public class OSSInfoDialog extends Dialog {

  private String filePath;
  



  /**
   * @param parentShell OSSInfoDialog parent shell
   */
  public OSSInfoDialog(final Shell parentShell,String filePath) {
    super(parentShell);
    this.filePath=filePath;

  }

  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("OSS Components Information");
  }

  @Override
  protected Control createDialogArea(final Composite parent) {
    Composite container = (Composite) super.createDialogArea(parent);
    container.setLayout(new GridLayout(1, false));

    // Create a table
    Table table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // Populate the table with data from the properties file
    populateTable(table);

    // Pack the columns
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    table.getColumn(0).setWidth(60);
    table.getColumn(1).setWidth(400);
    table.getColumn(2).setWidth(200);
    table.getColumn(3).setWidth(400);

    return container;
  }


  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // Override this method to create buttons
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
  }

  private void populateTable(final Table table) {
    List<String> lines = readLinesFromFile();
    String[] columnHeaders;

    // Take the headers from the first line
    if (!lines.isEmpty()) {
      columnHeaders = lines.get(0).split("@@@|=");
      createTableColumns(table, columnHeaders);

      // Skip the first line and populate the table
      for (int i = 1; i < lines.size(); i++) {
        TableItem item = new TableItem(table, SWT.NONE);
        item.setText(lines.get(i).replace("^^^", ", ").split("@@@|="));
      }
    }
  }

  private void createTableColumns(final Table table, final String[] headers) {
    for (String header : headers) {
      TableColumn column = new TableColumn(table, SWT.NONE);
      column.setText(header.trim());
    }
  }


  private List<String> readLinesFromFile() {
   
   
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line.trim());
      }
    }
    catch (IOException e) {
      LoggerUtil.getLogger().error("Exception while reading from properties file--" + e);
    }
    return lines;
  }


}
