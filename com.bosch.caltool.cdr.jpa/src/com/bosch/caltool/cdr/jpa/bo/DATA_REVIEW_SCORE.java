/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;

/**
 * Review score
 *
 * @author bne4cob
 */
// ICDM-2307
public enum DATA_REVIEW_SCORE {


                               /**
                                * Not reviewed
                                */
                               S_0("0", DATA_REVIEW_SCORE.RATING_NOT_REVIEWED, "SCORE_0", false),
                               /**
                                * Value only for calibration, must be modified
                                */
                               S_1("1", DATA_REVIEW_SCORE.RATING_NOT_REVIEWED, "SCORE_1", false),
                               /**
                                * Value not o.k., need to be changed
                                */
                               S_2("2", DATA_REVIEW_SCORE.RATING_NOT_REVIEWED, "SCORE_2", false),
                               /**
                                * Value reviewed, calibration required
                                */
                               S_3("3", DATA_REVIEW_SCORE.RATING_PRELIM_CAL, "SCORE_3", false),
                               /**
                                * Neutral calibration, calibration required
                                */
                               S_4("4", DATA_REVIEW_SCORE.RATING_PRELIM_CAL, "SCORE_4", false),
                               /**
                                * Value calibrated, further test required
                                */
                               S_5("5", DATA_REVIEW_SCORE.RATING_CALIBRATED, "SCORE_5", false),
                               /**
                                * Defined in Start Review, ready for production
                                */
                               S_7("7", DATA_REVIEW_SCORE.RATING_CALIBRATED, "SCORE_7", false),
                               /**
                                * Automated Review, ready for production
                                */
                               S_8("8", DATA_REVIEW_SCORE.RATING_CHECKED, "SCORE_8", true),
                               /**
                                * Review completed, ready for production
                                */
                               S_9("9", DATA_REVIEW_SCORE.RATING_CHECKED, "SCORE_9", true);

  /**
   * Group name of the score description in T_MESSAGES table
   */
  private static final String MESSAGE_GROUP_NAME = "REVIEW_SCORE";


  /**
   * DB type of the score
   */
  private final String dbType;

  /**
   * Rating of the score
   */
  private final int rating;

  /**
   * Key to find the description
   */
  private final String descKey;

  /**
   * if true, the parameter with the score is considered as reviewed
   */
  private final boolean reviewed;

  /**
   * Not reviewed score rating
   */
  public static final int RATING_NOT_REVIEWED = 0;
  /**
   * Prelim calib reviewed score rating
   */
  public static final int RATING_PRELIM_CAL = 25;
  /**
   * Calibrated reviewed score rating
   */

  public static final int RATING_CALIBRATED = 50;
  /**
   * check value review score
   */
  public static final int RATING_CHECKED = 75;
  /**
   * completed review rating
   */
  public static final int RATING_COMPLETED = 100;


  /**
   * @param dbType DB type of score
   * @param rating score rating
   * @param descKey key to find the description from messages
   * @param reviewed if true, the parameter with the score is considered as reviewed
   */
  private DATA_REVIEW_SCORE(final String dbType, final int rating, final String descKey, final boolean reviewed) {
    this.dbType = dbType;
    this.rating = rating;
    this.descKey = descKey;
    this.reviewed = reviewed;
  }


  /**
   * @return the rating desc for each rating
   */
  public String getRatingDesc() {
    String ratingDesc = "";
    // For none
    if (this.rating == RATING_NOT_REVIEWED) {
      ratingDesc = "---";
    }
    // For prelimcalibrated
    if (this.rating == RATING_PRELIM_CAL) {
      ratingDesc = "prelimCalibrated";
    }
    if (this.rating == RATING_CALIBRATED) {
      ratingDesc = "calibrated";
    }

    if (this.rating == RATING_CHECKED) {
      ratingDesc = "checked";
    }

    if (this.rating == RATING_COMPLETED) {
      ratingDesc = "completed";
    }
    return ratingDesc;
  }

  /**
   * Find the score value using the db type
   *
   * @param dbType type form database
   * @return score enum value
   */
  public static DATA_REVIEW_SCORE getType(final String dbType) {
    DATA_REVIEW_SCORE score = S_0;
    for (DATA_REVIEW_SCORE scr : DATA_REVIEW_SCORE.values()) {
      if (scr.dbType.equals(dbType)) {
        score = scr;
        break;
      }
    }
    return score;
  }

  /**
   * NOTE : Use this method only in commands, to set to entity column
   *
   * @return the dbType
   */
  public String getDbType() {
    return this.dbType;
  }

  /**
   * @return the rating
   */
  public int getRating() {
    return this.rating;
  }

  /**
   * @return the score's display text. For example '8 [75%]'
   */
  public String getScoreDisplay() {
    return getDbType() + " [" + getRating() + "%]";
  }

  /**
   * @param dataProvider cdrdataprovider
   * @return the score display text in the format.For example '8 [75%] Review completed, ready for production'
   */
  public String getScoreDisplayExt(final CDRDataProvider dataProvider) {
    return getScoreDisplay() + " " + getDescription(dataProvider);
  }

  /**
   * @param dataProvider CDR Data provider
   * @return the score description in the client language
   */
  public String getDescription(final CDRDataProvider dataProvider) {
    return dataProvider.getApicDataProvider().getMessage(MESSAGE_GROUP_NAME, this.descKey);
  }

  /**
   * @return true, the parameter with the score is considered as reviewed, else false
   */
  public boolean isReviewed() {
    return this.reviewed;
  }

  /**
   * @return the list of db values which are reviewed
   */
  public static List<DATA_REVIEW_SCORE> getReviewedValues() {
    List<DATA_REVIEW_SCORE> reviewedParam = new ArrayList<>();
    for (DATA_REVIEW_SCORE data_REVIEW_SCORE : DATA_REVIEW_SCORE.values()) {
      if (data_REVIEW_SCORE.isReviewed()) {
        reviewedParam.add(data_REVIEW_SCORE);
      }
    }
    return reviewedParam;
  }

  /**
   * @param reviewType reviewType
   * @param dataProvider dataProvider
   * @param isExtendedDisplay to display comment along with score
   * @return scores list to be populated in the combo
   */
  public static List<DATA_REVIEW_SCORE> getApplicableScore(final REVIEW_TYPE reviewType) {
    List<DATA_REVIEW_SCORE> comboList = new ArrayList<DATA_REVIEW_SCORE>();
    comboList.addAll(Arrays.asList(DATA_REVIEW_SCORE.S_0, DATA_REVIEW_SCORE.S_1, DATA_REVIEW_SCORE.S_2,
        DATA_REVIEW_SCORE.S_3, DATA_REVIEW_SCORE.S_4, DATA_REVIEW_SCORE.S_5));

    if (reviewType == REVIEW_TYPE.OFFICIAL) {
      comboList.add(DATA_REVIEW_SCORE.S_9);
    }
    else if ((reviewType == REVIEW_TYPE.START) || (reviewType == REVIEW_TYPE.TEST)) {
      comboList.add(DATA_REVIEW_SCORE.S_7);
    }
    // if review type is null , all the values are displayed. eg. for filter
    else if (reviewType == null) {
      comboList.add(DATA_REVIEW_SCORE.S_7);
      comboList.add(DATA_REVIEW_SCORE.S_8);
      comboList.add(DATA_REVIEW_SCORE.S_9);
    }
    return comboList;
  }


}
