package com.rkoyanagui.img_recog;

import static com.rkoyanagui.utils.WaitUtils.await;
import static java.util.Objects.isNull;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

import com.rkoyanagui.utils.Pair;
import java.awt.Font;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import nu.pattern.OpenCV;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;

public class ImgUtils
{

  private static final String COULD_NOT_LOAD = "Could not read image from byte input stream!";

  protected ImgUtils()
  {
  }

  public static void displayInWindow(final String title,
                                     final List<byte[]> imgs,
                                     final List<String> comments)
  {
    final List<JScrollPane> scrollPanes = new ArrayList<>();
    for (final byte[] img : imgs)
    {
      final BufferedImage bufImage;
      try (final InputStream in = new ByteArrayInputStream(img))
      {
        bufImage = ImageIO.read(in);
      }
      catch (IOException x)
      {
        throw new IllegalArgumentException(COULD_NOT_LOAD, x);
      }
      final JLabel label = new JLabel(new ImageIcon(bufImage), SwingConstants.CENTER);
      final JScrollPane scrollPane = new JScrollPane(label, VERTICAL_SCROLLBAR_AS_NEEDED,
          HORIZONTAL_SCROLLBAR_AS_NEEDED);
      scrollPanes.add(scrollPane);
    }

    final JFrame frame = new JFrame(title);
    final JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    if (!comments.isEmpty())
    {
      final StringBuilder sb = new StringBuilder();
      comments.forEach(c -> sb.append(c).append("\n"));
      final JTextArea textArea = new JTextArea(sb.toString());
      textArea.setEditable(false);
      textArea.setFont(new Font("Arial", Font.PLAIN, 24));
      final JPanel textPanel = new JPanel();
      textPanel.add(textArea);
      final JScrollPane scrollPane = new JScrollPane(textPanel, VERTICAL_SCROLLBAR_AS_NEEDED,
          HORIZONTAL_SCROLLBAR_AS_NEEDED);
      mainPanel.add(scrollPane);
    }

    if (!scrollPanes.isEmpty())
    {
      scrollPanes.forEach(mainPanel::add);
    }

    frame.getContentPane().add(mainPanel);
    // Resolucao: 1920 x 1080
    frame.setSize(960, 960);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static List<Double> generateInterposedPowerSequence(final double seed,
                                                             final int maxPower)
  {
    final List<Double> s = new ArrayList<>();
    s.add(1.0);
    for (int i = 1; i <= maxPower; i++)
    {
      s.add(Math.pow(seed, i));
      s.add(Math.pow(seed, -i));
    }
    return s;
  }

  public static List<Pair<Double, Double>> generatePowerSequencePair(final double seed,
                                                                     final int maxPower)
  {
    final List<Pair<Double, Double>> s = new ArrayList<>();
    s.add(new Pair<>(1.0, 1.0));
    for (int i = 1; i <= maxPower; i++)
    {
      s.add(new Pair<>(Math.pow(seed, i), Math.pow(seed, -i)));
      s.add(new Pair<>(Math.pow(seed, -i), Math.pow(seed, i)));
    }
    return s;
  }

  public static Point getCentre(final Rectangle rect)
  {
    int x = rect.getX() + (rect.getWidth() / 2);
    int y = rect.getY() + (rect.getHeight() / 2);
    return new Point(x, y);
  }

  public static boolean isOneRectsCentreInsideTheOtherRectsBorders(final Rectangle one,
                                                                   final Rectangle theOther)
  {
    // upper left corner
    final Point rect0Anchor = new Point(theOther.x, theOther.y);
    // lower right corner
    final Point rect0Opposite =
        new Point(theOther.x + theOther.width, theOther.y + theOther.height);
    final Point rect1Centre = ImgUtils.getCentre(one);
    return rect1Centre.x >= rect0Anchor.x && rect1Centre.y >= rect0Anchor.y &&
        rect1Centre.x <= rect0Opposite.x && rect1Centre.y <= rect0Opposite.y;
  }

  /**
   * Waits until all AWT/Swing frames are no longer visible, that is, until they are closed. Useful
   * when you want those frames to stay open at the end of a test, while you analyse them, when in
   * "debug" mode (see README.md). Once you are done analysing, close all "debug" windows, and then
   * the test execution resumes.
   */
  public static void waitUntilAllFramesAreClosed()
  {
    await().unlimitedNumOfAttempts()
        .unlimitedTimeout()
        .until(() -> {
          final Frame[] frames = Frame.getFrames();
          return isNull(frames) ||
              frames.length == 0 ||
              Arrays.stream(frames).noneMatch(f -> f.isVisible());
        })
        .perform();
  }

  /**
   * Call this method to initialise OpenCV before anything else is called in your 'main' method.
   */
  public static void initOpenCv()
  {
    OpenCV.loadLocally();
  }

}
