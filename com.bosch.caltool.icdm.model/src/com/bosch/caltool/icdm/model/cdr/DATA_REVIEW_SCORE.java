/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

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
                               S_0("0", DATA_REVIEW_SCORE.RATING_NOT_REVIEWED, "SCORE_0", false, false,DATA_REVIEW_SCORE.RATING_NOT_REVIEWED),
                               /**
                                * Value only for calibration, must be modified
                                */
                               S_1("1", DATA_REVIEW_SCORE.RATING_NOT_REVIEWED, "SCORE_1", false, true,DATA_REVIEW_SCORE.RATING_NOT_REVIEWED),
                               /**
                                * Value not o.k., need to be changed
                                */
                               S_2("2", DATA_REVIEW_SCORE.RATING_NOT_REVIEWED, "SCORE_2", false, true,DATA_REVIEW_SCORE.RATING_NOT_REVIEWED),
                               /**
                                * Value reviewed, calibration required
                                */
                               S_3("3", DATA_REVIEW_SCORE.RATING_PRELIM_CAL, "SCORE_3", false, true,DATA_REVIEW_SCORE.RATING_PRELIM_CAL),
                               /**
                                * Neutral calibration, calibration required
                                */
                               S_4("4", DATA_REVIEW_SCORE.RATING_PRELIM_CAL, "SCORE_4", false, true,DATA_REVIEW_SCORE.RATING_PRELIM_CAL),
                               /**
                                * Value calibrated, further test required
                                */
                               S_5("5", DATA_REVIEW_SCORE.RATING_CALIBRATED, "SCORE_5", false, true,DATA_REVIEW_SCORE.RATING_CALIBRATED),
                               /**
                                * Value calibrated, further test required
                                */
                               S_6("6", DATA_REVIEW_SCORE.RATING_CALIBRATED, "SCORE_6", false, true,DATA_REVIEW_SCORE.RATING_CALIBRATED),
                               /**
                                * Defined in Start Review, ready for production
                                */
                               S_7("7", DATA_REVIEW_SCORE.RATING_CALIBRATED, "SCORE_7", false, true,DATA_REVIEW_SCORE.RATING_CALIBRATED),
                               /**
                                * Automated Review, ready for production
                                */
                               S_8("8", DATA_REVIEW_SCORE.RATING_CHECKED, "SCORE_8", true, true,DATA_REVIEW_SCORE.RATING_COMPLETED),
                               /**
                                * Review completed, ready for production
                                */
                               S_9("9", DATA_REVIEW_SCORE.RATING_CHECKED, "SCORE_9", true, true,DATA_REVIEW_SCORE.RATING_COMPLETED);
  

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
   * if true, the parameter with the score = 8 or 9
   */
  private final boolean checked;

  /**
   * if true, the parameter with the score >0
   */
  private final boolean reviewed;
  
  /**
   * Rating of the 100% score
   */
  private final int hundredPercRating;

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
   * @param checked is true for parameter being checked in the review , with score 8 and 9
   * @param reviewed is true for parameter being reviewed in the review ,with the score > 0
   */
  private DATA_REVIEW_SCORE(final String dbType, final int rating, final String descKey, final boolean checked,
      final boolean reviewed,final int hundredPercRating ) {
    this.dbType = dbType;
    this.rating = rating;
    this.descKey = descKey;
    this.checked = checked;
    this.reviewed = reviewed;
    this.hundredPercRating = hundredPercRating;
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
   * @return the Hundred score's display text. For example '8 [100%]' and '9 [100%]'
   */
  public String getHundredPercScoreDisplay() {
    return getDbType() + " [" + getHundredPercRating() + "%]";
  }

  /**
   * @return true, the parameter with the score 8 & 9 is considered as checked, else false
   */
  public boolean isChecked() {
    return this.checked;
  }

  /**
   * @return the list of db values which are reviewed
   */
  public static List<DATA_REVIEW_SCORE> getCheckedValues() {
    List<DATA_REVIEW_SCORE> reviewedParam = new ArrayList<>();
    for (DATA_REVIEW_SCORE data_REVIEW_SCORE : DATA_REVIEW_SCORE.values()) {
      if (data_REVIEW_SCORE.isChecked()) {
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
    List<DATA_REVIEW_SCORE> reviewScoreList = new ArrayList<>();
    reviewScoreList.addAll(Arrays.asList(DATA_REVIEW_SCORE.S_0, DATA_REVIEW_SCORE.S_1, DATA_REVIEW_SCORE.S_2,
        DATA_REVIEW_SCORE.S_3, DATA_REVIEW_SCORE.S_4, DATA_REVIEW_SCORE.S_5, DATA_REVIEW_SCORE.S_6));

    if (reviewType == REVIEW_TYPE.OFFICIAL) {
      reviewScoreList.add(DATA_REVIEW_SCORE.S_9);
    }
    else if ((reviewType == REVIEW_TYPE.START) || (reviewType == REVIEW_TYPE.TEST)) {
      reviewScoreList.add(DATA_REVIEW_SCORE.S_7);
    }
    // if review type is null , all the values are displayed. eg. for filter
    else if (reviewType == null) {
      reviewScoreList.add(DATA_REVIEW_SCORE.S_7);
      reviewScoreList.add(DATA_REVIEW_SCORE.S_8);
      reviewScoreList.add(DATA_REVIEW_SCORE.S_9);
    }
    return reviewScoreList;
  }


  /**
   * @return the messageGroupName
   */
  public static String getMessageGroupName() {
    return MESSAGE_GROUP_NAME;
  }


  /**
   * @return the descKey
   */
  public String getDescKey() {
    return this.descKey;
  }


  /**
   * @return the newReviewed
   */
  public boolean isReviewed() {
    return this.reviewed;
  }


  
  /**
   * @return the hundredPercRating
   */
  public int getHundredPercRating() {
    return hundredPercRating;
  }


}
