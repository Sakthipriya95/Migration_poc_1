package com.bosch.caltool.icdm.common.ui.dialogs;

import java.text.ParseException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.bosch.caltool.icdm.common.ui.utils.ICalendarConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.DateAndNumValidator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * This class adds the Calendar dialog
 */
public class CalendarDialog implements ICalendarConstants {

  /**
   * DateTime instance for calendar
   */
  private DateTime calendar;
  /**
   * Button instance for Ok
   */
  private Button okBtn;

  /**
   * This method adds CalendarDialog
   *
   * @param composite instance
   * @param text instance
   * @param format defines date or number format
   */
  public void addCalendarDialog(final Composite composite, final Text text, final String format) {
    final Shell dialog = customiseDialog(composite);
    addOkBtnSelectionListner(text, format, dialog);
    addMouseListener(this.calendar, text, format, dialog);
    openDialog(dialog);
  }

  /**
   * @param dialog
   */
  private void openDialog(final Shell dialog) {
    dialog.setDefaultButton(this.okBtn);
    dialog.open();
    Rectangle shellBounds = dialog.getParent().getBounds();
    Point dialogSize = dialog.getShell().getSize();
    dialog.getShell().setLocation(shellBounds.x + ((shellBounds.width - dialogSize.x) / 2),
        shellBounds.y + ((shellBounds.height - dialogSize.y) / 2));
  }

  /**
   * @param composite
   * @return
   */
  private Shell customiseDialog(final Composite composite) {
    final Shell dialog = new Shell(composite.getShell(), SWT.DIALOG_TRIM);
    GridLayout gridLayout = new GridLayout();
    dialog.setLayout(gridLayout);
    dialog.setLayoutData(GridDataUtil.getInstance().getGridData());
    dialog.setText("Select Date");
    dialog.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.CALENDAR_16X16));
    // ICDM 536
    // Label to provide hint to the user
    Label hintLabel = new Label(dialog, SWT.CENTER);
    hintLabel.setText("Click on month/year to change");

    this.calendar = new DateTime(dialog, SWT.CALENDAR | SWT.BORDER);
    GridData gridData = new GridData();
    this.calendar.setLayoutData(gridData);


    this.okBtn = new Button(dialog, SWT.PUSH);
    this.okBtn.setText("Confirm");
    GridData centerGridData = new GridData();
    centerGridData.horizontalAlignment = SWT.CENTER;
    this.okBtn.setLayoutData(centerGridData);
    dialog.pack(true);
    return dialog;
  }

  /**
   * @param cal DateTime instance of calendar
   * @param text date time text in ui
   * @param format date time format
   * @param dialog CalendarDialog
   */
  private void addMouseListener(final DateTime cal, final Text text, final String format, final Shell dialog) {
    cal.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent arg0) {
        // No implementation
      }

      @Override
      public void mouseDown(final MouseEvent arg0) {
        // No implementation
      }

      @Override
      public void mouseDoubleClick(final MouseEvent arg0) {
        setDateMonth(text, format, dialog);
      }
    });
  }

  /**
   * This method add selection listener to ok button
   *
   * @param text
   * @param format
   * @param dialog
   */
  private void addOkBtnSelectionListner(final Text text, final String format, final Shell dialog) {
    this.okBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        setDateMonth(text, format, dialog);
      }
    });
  }

  /**
   * Set the date to the text field
   *
   * @param text
   * @param format
   * @param selectMonth
   * @param selectDay
   */
  private void setDate(final Text text, final String format, final String selectMonth, final String selectDay) {
    String timeDisTxt = this.calendar.getYear() + HYPHEN + selectMonth + HYPHEN + selectDay + EMPTY_SPACE + _00 +
        COLON + _00 + COLON + _00;

    if ((format == null) || format.equals(EMPTY_STRING)) {
      text.setText(timeDisTxt);
    }
    else {
      try {
        changeToDateFormat(text, format, timeDisTxt);
      }
      catch (ParseException pe) {
        CDMLogger.getInstance().error(pe.getMessage(), pe);
      }
    }
  }

  /**
   * Change time string to date format and set to the text field
   *
   * @param text text field
   * @param format format
   * @param timeDisTxt time as string
   * @throws ParseException exception
   */
  private void changeToDateFormat(final Text text, final String format, final String timeDisTxt) throws ParseException {

    String formattedDate =
        DateAndNumValidator.getInstance().getFormatedDate(YYYY_MM_DD_24_HH_MM_SS, timeDisTxt, format);
    text.setText(formattedDate);
  }

  /**
   * @param composite Composite
   * @param agreedOnText Text
   * @param dateFormat09 String
   * @param delAgreedOn Button
   */
  public void addCalendarDialogFc2Wp(final Composite composite, final Text agreedOnText, final String dateFormat09,
      final Button delAgreedOn) {
    final Shell dialog = customiseDialog(composite);
    addOkBtnSelListnerFc2Wp(agreedOnText, dateFormat09, dialog, delAgreedOn);
    openDialog(dialog);
  }


  /**
   * @param agreedOnText
   * @param dateFormat09
   * @param dialog
   */
  private void setDateMonth(final Text agreedOnText, final String dateFormat09, final Shell dialog) {
    int month = CalendarDialog.this.calendar.getMonth();
    int day = CalendarDialog.this.calendar.getDay();
    int actualMonth = month + 1;
    String selectMonth;
    if (actualMonth < 10) {
      selectMonth = _0_CONSTANT + actualMonth;
    }
    else {
      selectMonth = actualMonth + EMPTY_STRING;
    }
    String selectDay;
    if (day < 10) {
      selectDay = _0_CONSTANT + day;
    }
    else {
      selectDay = day + EMPTY_STRING;
    }
    setDate(agreedOnText, dateFormat09, selectMonth, selectDay);
    dialog.close();
  }

  /**
   * @param agreedOnText
   * @param dateFormat09
   * @param dialog
   * @param delAgreedOn
   */
  private void addOkBtnSelListnerFc2Wp(final Text agreedOnText, final String dateFormat09, final Shell dialog,
      final Button delAgreedOn) {
    this.okBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        setDateMonth(agreedOnText, dateFormat09, dialog);
        delAgreedOn.setEnabled(true);
      }
    });
  }
}
