package com.rkoyanagui.img_recog.impl;

import static java.util.Objects.nonNull;

import com.rkoyanagui.img_recog.Extensions;
import com.rkoyanagui.img_recog.FeatureMatchingBy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;

class OpenCvFeatureMatcher
{

  protected static final int THICKNESS_1 = 1;
  protected static final int THICKNESS_3 = 3;
  protected static final String VISUALISE_MSG = "DEBUG Img Recog Feature Match";

  protected OpenCvFeatureMatcher()
  {
  }

  /**
   * Matches features in common between two pictures using the
   * <a href="https://ieeexplore.ieee.org/document/6126544">ORB</a> algorithm (see this
   * <a href="https://docs.opencv.org/3.4/d1/d89/tutorial_py_orb.html">tutorial</a>), which is
   * faster than SURF and SIFT, and is freely licensed. The algorithm is scale-invariant and
   * rotation-invariant.
   *
   * @param queryImg  the first picture
   * @param trainImg  the second picture
   * @param params    parameters for the algorithm
   * @param visualise to activate "debug" mode and see how the pictures were processed
   * @return a {@link FeatureMatchResult}, if the actual number of matches is greater than or equal
   * to {@code minNumOfMatches} in {@link FeatureMatchingBy}. Otherwise, returns nothing.
   */
  protected static Optional<FeatureMatchResult> matchFeatures(final Mat queryImg,
                                                              final Mat trainImg,
                                                              final FeatureMatchingBy params,
                                                              final boolean visualise)
  {

    final Mat iQueryImg = OpenCvImgUtils.accFilters(params.getFilters()).apply(queryImg.clone());
    final Mat iTrainImg = OpenCvImgUtils.accFilters(params.getFilters()).apply(trainImg.clone());
    final int qFeatures = params.getQueryImgFeatures();
    final int tFeatures = params.getTrainImgFeatures();
    final int minNumOfMatches = params.getMinNumOfMatches();
    final int numOfMatchesToKeep = params.getNumOfMatchesToKeep();

    final float scaleFactor = params.getScaleFactor();
    final int nlevels = params.getNlevels();
    final int edgeThreshold = params.getEdgeThreshold();
    final int firstLevel = params.getFirstLevel();
    final int wtaK = params.getWtaK();
    final int scoreType = params.getScoreType();
    final int patchSize = params.getPatchSize();
    final int fastThreshold = params.getFastThreshold();
    final int normType;
    if (wtaK == 2)
    {
      normType = Core.NORM_HAMMING;
    }
    else
    {
      normType = Core.NORM_HAMMING2;
    }

    // 1) Detects keypoints using ORB Detector. Computes the descriptors.
    final Feature2D qDetector = ORB.create(qFeatures, scaleFactor, nlevels, edgeThreshold,
        firstLevel, wtaK, scoreType, patchSize, fastThreshold);
    final Feature2D tDetector = ORB.create(tFeatures, scaleFactor, nlevels, edgeThreshold,
        firstLevel, wtaK, scoreType, patchSize, fastThreshold);

    final MatOfKeyPoint qKeyPoints = new MatOfKeyPoint();
    final Mat qDescriptors = new Mat(1, 1, CvType.CV_32F);

    final MatOfKeyPoint tKeyPoints = new MatOfKeyPoint();
    final Mat tDescriptors = new Mat(1, 1, CvType.CV_32F);

    qDetector.detectAndCompute(iQueryImg, new Mat(), qKeyPoints, qDescriptors);
    tDetector.detectAndCompute(iTrainImg, new Mat(), tKeyPoints, tDescriptors);

    if (qKeyPoints.empty() || tKeyPoints.empty())
    {
      return Optional.empty();
    }

    // 2) Matches descriptor vectors with NORM_HAMMING, since ORB is a binary descriptor.
    final DescriptorMatcher matcher = BFMatcher.create(normType, true);
    final MatOfDMatch matches = new MatOfDMatch();
    matcher.match(qDescriptors, tDescriptors, matches);

    // 3) Sorts by metric (Hamming) distance.
    // The less distance between two matched descriptors, the better.
    final List<DMatch> matchesList = matches.toList();
    matchesList.sort(Comparator.comparing(m -> m.distance));

    // 4) Picks the first N best matches.
    final List<DMatch> bestMatches;
    final int numOfMatches = matchesList.size();
    final boolean isAboveThreshold = numOfMatches >= minNumOfMatches;
    if (numOfMatches > numOfMatchesToKeep)
    {
      bestMatches = matchesList.subList(0, numOfMatchesToKeep);
    }
    else
    {
      bestMatches = matchesList;
    }

    if (isAboveThreshold)
    {
      final Size tSize = trainImg.size();
      final Dimension tDimension = new Dimension((int) tSize.width, (int) tSize.height);
      final FeatureMatchResult mr =
          calculateRectangle(iQueryImg, iTrainImg, qKeyPoints, tKeyPoints, bestMatches)
              .withMinNumOfMatches(minNumOfMatches)
              .withActualNumOfMatches(numOfMatches)
              .withSrcImgDimension(tDimension);
      if (visualise)
      {
        visualise(params, mr);
      }
      return Optional.of(mr);
    }
    else
    {
      if (visualise)
      {
        visualiseKeypoints(params, iQueryImg, iTrainImg, qKeyPoints, tKeyPoints, numOfMatches);
      }
      return Optional.empty();
    }

  }

  protected static FeatureMatchResult calculateRectangle(final Mat queryImg,
                                                         final Mat trainImg,
                                                         final MatOfKeyPoint qKeyPoints,
                                                         final MatOfKeyPoint tKeyPoints,
                                                         final List<DMatch> bestMatches)
  {
    // query image keypoint array
    final KeyPoint[] qkp = qKeyPoints.toArray();
    // train image keypoint array
    final KeyPoint[] tkp = tKeyPoints.toArray();
    // Finds the coordinates of every keypoint corresponding to the best matches.
    final List<Point> qps = bestMatches.stream()
        .map(m -> qkp[m.queryIdx].pt)
        .collect(Collectors.toList());
    final List<Point> tps = bestMatches.stream()
        .map(m -> tkp[m.trainIdx].pt)
        .collect(Collectors.toList());
    // matrix of query image points
    final MatOfPoint2f mqps = new MatOfPoint2f();
    mqps.fromList(qps);
    // matrix of train image points
    final MatOfPoint2f mtps = new MatOfPoint2f();
    mtps.fromList(tps);
    final Mat mask = new Mat();

    // Calculates a transformation matrix between the plane of the query image and the plane of
    // the train image. So the query image can be located inside the train image regardless of any
    // rotation, flipping, or perspective distortions. The matrix is estimated from the keypoints.
    final Mat homography = Calib3d.findHomography(mqps, mtps, Calib3d.RANSAC, 5.0, mask);

    // Get the corners from the query image
    final Mat qCorners = new Mat(4, 1, CvType.CV_32FC2);
    final Mat tCorners = new Mat();
    final int qCols = queryImg.cols();
    float[] qCornersData = new float[(int) (qCorners.total() * qCorners.channels())];
    qCorners.get(0, 0, qCornersData);
    qCornersData[0] = 0;
    qCornersData[1] = 0;
    qCornersData[2] = qCols;
    qCornersData[3] = 0;
    qCornersData[4] = qCols;
    qCornersData[5] = queryImg.rows();
    qCornersData[6] = 0;
    qCornersData[7] = queryImg.rows();
    qCorners.put(0, 0, qCornersData);

    // Applies the homography matrix to find the query image corners in the train image.
    Core.perspectiveTransform(qCorners, tCorners, homography);
    float[] tCornersData = new float[(int) (tCorners.total() * tCorners.channels())];
    tCorners.get(0, 0, tCornersData);

    // points for each of the four corners
    final Point p0 = new Point(tCornersData[0], tCornersData[1]); // upper left
    final Point p1 = new Point(tCornersData[2], tCornersData[3]); // upper right
    final Point p2 = new Point(tCornersData[4], tCornersData[5]); // lower right
    final Point p3 = new Point(tCornersData[6], tCornersData[7]); // lower left

    // Builds a rectangle anchored in the upper left corner. Works well as an approximation when
    // there are only four corners roughly box-shaped.
    final Rectangle rect = new Rectangle(
        (int) p0.x,
        (int) p0.y,
        (int) (p3.y - p0.y - 1),
        (int) (p1.x - p0.x - 1))
    {
      @Override
      public String toString()
      {
        return String.format("Rectangle(x=%d, y=%d, width=%d, height=%d)", x, y, width, height);
      }
    };

    // rectangle points
    final Point rp0 = new Point(rect.x, rect.y);
    final Point rp1 = new Point((double) rect.x + rect.width, rect.y);
    final Point rp2 = new Point((double) rect.x + rect.width, (double) rect.y + rect.height);
    final Point rp3 = new Point(rect.x, (double) rect.y + rect.height);

    // drawing points (corner.x + qCols)
    final Point dp0 = new Point(p0.x + qCols, p0.y);
    final Point dp1 = new Point(p1.x + qCols, p1.y);
    final Point dp2 = new Point(p2.x + qCols, p2.y);
    final Point dp3 = new Point(p3.x + qCols, p3.y);

    // drawing rectangle points (corner.x + qCols)
    final Point drp0 = new Point(rp0.x + qCols, rp0.y);
    final Point drp1 = new Point(rp1.x + qCols, rp1.y);
    final Point drp2 = new Point(rp2.x + qCols, rp2.y);
    final Point drp3 = new Point(rp3.x + qCols, rp3.y);

    final MatOfDMatch bestMatOfDMatch = new MatOfDMatch();
    bestMatOfDMatch.fromList(bestMatches);
    final Mat drawnMatch = new Mat();
    Features2d.drawMatches(queryImg, qKeyPoints, trainImg, tKeyPoints, bestMatOfDMatch,
        drawnMatch, OpenCvImgUtils.GREEN, OpenCvImgUtils.GREEN, new MatOfByte(mask),
        Features2d.DrawMatchesFlags_NOT_DRAW_SINGLE_POINTS);

    // Draw lines between the corners (the mapped query image in the train image)
    final Mat drawnMatch1 = drawPolygon(drawnMatch, OpenCvImgUtils.BLUE, THICKNESS_3,
        dp0, dp1, dp2, dp3);

    // Draw the corrected rectangle (anchored in the upper left corner)
    final Mat drawnMatch2 = drawPolygon(drawnMatch1, OpenCvImgUtils.RED, THICKNESS_1,
        drp0, drp1, drp2, drp3);

    return new FeatureMatchResult(rect, drawnMatch2);
  }

  protected static Mat drawPolygon(final Mat canvas,
                                   final Scalar colour,
                                   final int thickness,
                                   final Point... pts)
  {
    final Mat drawing = canvas.clone();
    if (nonNull(pts) && pts.length > 1)
    {
      for (int i = 1; i < pts.length; i++)
      {
        Imgproc.line(drawing, pts[i - 1], pts[i], colour, thickness);
      }
      Imgproc.line(drawing, pts[pts.length - 1], pts[0], colour, thickness);
    }
    return drawing;
  }

  protected static void visualiseKeypoints(final FeatureMatchingBy params,
                                           final Mat queryImg,
                                           final Mat trainImg,
                                           final MatOfKeyPoint qKeyPoints,
                                           final MatOfKeyPoint tKeyPoints,
                                           final Integer actualNumOfMatches)
  {
    final Mat queryImgKp = new Mat();
    final Mat trainImgKp = new Mat();
    Features2d.drawKeypoints(queryImg, qKeyPoints, queryImgKp);
    Features2d.drawKeypoints(trainImg, tKeyPoints, trainImgKp);
    final List<String> comments = new ArrayList<>();
    comments.add(params.toString());
    comments.add("actualNumOfMatches: " + actualNumOfMatches);
    comments.add("isAboveThreshold: false");
    OpenCvImgUtils.displayInWindow(VISUALISE_MSG,
        trainImgKp, queryImgKp, Extensions.PNG, comments);
  }

  protected static void visualise(final FeatureMatchingBy params,
                                  final FeatureMatchResult mr,
                                  final String... extraComments)
  {
    final List<String> comments = new ArrayList<>();
    comments.add(params.toString());
    comments.add(mr.rectangle.toString());
    comments.add("actualNumOfMatches: " + mr.actualNumOfMatches);
    comments.add("isAboveThreshold: true");
    if (nonNull(extraComments) && extraComments.length > 0)
    {comments.addAll(Arrays.asList(extraComments));}
    OpenCvImgUtils.displayInWindow(VISUALISE_MSG, mr.drawnMatch, Extensions.PNG, comments);
  }

}
