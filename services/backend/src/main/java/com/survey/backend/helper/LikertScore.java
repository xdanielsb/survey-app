package com.survey.backend.helper;

import com.survey.backend.entity.LikertScale;

public class LikertScore {

  public static int mapLikertToScore(LikertScale scale) {
    return switch (scale) {
      case TOTALLY_DISAGREE -> 1;
      case DISAGREE -> 2;
      case NEUTRAL -> 3;
      case AGREE -> 4;
      case FULLY_AGREE -> 5;
    };
  }
}
