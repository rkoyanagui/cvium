package com.rkoyanagui.img_recog.impl;

import org.opencv.features2d.ORB;

public class ImgRecogConst
{

  protected ImgRecogConst()
  {
  }

  public static class TemplateMatching
  {

    public static final double MATCH_THRESHOLD = 0.8;
    public static final double RESIZE_FACTOR = 1.052;
    public static final int RESIZE_MAX_ATTEMPTS = 4;

    protected TemplateMatching()
    {
    }

  }

  public static class FeatureMatching
  {

    public static final int NUM_OF_QUERY_FEATURES = 400;
    public static final int NUM_OF_TRAIN_FEATURES = 2000;
    public static final int MIN_NUM_OF_MATCHES = 30;
    public static final int NUM_OF_MATCHES_TO_KEEP = 30;
    public static final float SCALE_FACTOR = 1.2f;
    public static final int N_LEVELS = 8;
    public static final int EDGE_THRESHOLD = 31;
    public static final int FIRST_LEVEL = 0;
    public static final int WTA_K = 2;
    public static final int SCORE_TYPE = ORB.HARRIS_SCORE;
    public static final int PATCH_SIZE = 31;
    public static final int FAST_THRESHOLD = 20;

    protected FeatureMatching()
    {
    }

  }

  public static class OcrMatching
  {

    public static final float MIN_SCORE = 0.8f;
    public static final int PAGE_SEGMENTATION_MODE = 6;
    public static final float NMS_THRESHOLD = 0.4f;
    public static final float IOU_THRESHOLD = 0.0001f;
    public static final double LEFT_PADDING = 0.03;
    public static final double RIGHT_PADDING = 0.0;
    public static final double TOP_PADDING = 0.0;
    public static final double BOTTOM_PADDING = 0.0;

    protected OcrMatching()
    {
    }

  }

}
