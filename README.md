# CVium #

Convenience wrapper for computer vision functionality:

* [Template matching](https://en.wikipedia.org/wiki/Template_matching#Template-based_approach)
* [Feature matching](https://en.wikipedia.org/wiki/Template_matching#Feature-based_approach)
* [OCR](https://en.wikipedia.org/wiki/Optical_character_recognition)

Additionally, some image processing functionality is provided (filtering, thresholding, blurring,
morphological operations, contour detection, rescaling, cropping).

## Features

All computer vision functions are imported from [OpenCV](https://opencv.org/), an open source
computer vision library.

Template matching is done with
the [normalised correlation coefficient method](https://docs.opencv.org/4.5.4/df/dfb/group__imgproc__object.html#ga586ebfb0a7fb604b35a23d85391329be)
(higher scores for regions where pixel intensity in the two images matches more closely).

Feature matching is done with the SIFT algorithm. To be changed
to [ORB](https://www.academia.edu/29806850/ORB_an_efficient_alternative_to_SIFT_or_SURF) as soon as
possible.

Optical character detection is done with the [EAST](https://github.com/argman/EAST) algorithm, using
a free trained
[Tensorflow model](https://github.com/opencv/opencv_extra/blob/master/testdata/dnn/download_models.py#L378)
. Character recognition is handled by [Tesseract](https://tesseract-ocr.github.io/).

## Technical limitations

* Handles static 2d images only. No video. No 3d.
* For now, there are wrappers for Android/iOS mobile apps only. A new feature including
  Selenium-like wrappers for web browser automation is under study.

## Is this for you?

The purpose of this module is to make a few computer vision tools available for software quality
assurance. Let me first introduce you to some of the problems we face.

If you have ever used Selenium or Appium to run E2E black-box tests on a website or a mobile app,
then you know that page/screen elements can be uncooperative at times, i.e., hard to uniquely
identify by attribute or predicate.

You may also know that dynamic pages make element location harder. Dynamic pages are pretty much
ubiquitous these days, making tests more brittle.

Also, navigating the DOM is a costly procedure in the first place, making tests last longer.

And finally, there are applications where the "traditional" DOM tree search method doesn't work at
all, because the graphics are drawn on a canvas element. That, of course, includes most gaming apps.
But other kinds of apps use canvas too. Meaning, there are many apps now, and there will be more
over time, that are completely opaque to Selenium and Appium's regular way of locating page
elements.

This is where computer vision comes along to help. It offers us methods of finding what is on screen
regardless of how the graphics are generated.

For those already accustomed to using Selenium or Appium's Java annotations, using this module
should come as an easy transition. That is because all _computer vision locating strategies_ in this
module are available via field annotations too!

# A look under the bonnet

Java reflection is used to call a given computer vision strategy whenever a method is invoked on a
field of type `com.rkoyanagui.img_recog.ImgRecogElement` or `List<ImgRecogElement>`.

Appium/Selenium is used to take a picture of the screen. That picture is then fed to the chosen computer
vision procedure, which then populates the field with location and size data.

If all you need is to assert that an element is visible, then that is enough. But if you need to
interact with an element, then we come back to Appium/Selenium.

As you may have already guessed, the only kind of interaction possible is the 'touch' kind: tapping
and dragging, that is, the same actions performed by fingers on a screen. Using the element's
location and size, we can tell Appium/Selenium to apply touch actions to it.

## Getting started

To locate an element, or a list of elements, annotate your `com.rkoyanagui.img_recog.ImgRecogElement`
or `List<ImgRecogElement>` field with one of:

* `@AndroidImageFeatureFindBy`
* `@AndroidImageTemplateFindBy`
* `@AndroidOcrFindBy`
* `@iOSImageFeatureFindBy`
* `@iOSImageTemplateFindBy`
* `@iOSOcrFindBy`

These should look like Appium's familiar `@AndroidFindBy` and `@iOSXCUITFindBy`. And just like them,
a page factory is required to initialise their respective fields. But instead of the
regular `org.openqa.selenium.support.PageFactory`, you must
use `com.rkoyanagui.img_recog.ImgRecogPageFactory`.

If you have more than one annotation on a given field for a given OS (Android or iOS), they will all
be considered equally valid alternatives. This means any of them could be the one that actually
finds the element. Not all of them will be evaluated: the locating procedure stops upon returning
the first successful match. The order of evaluation can be defined by the `order` parameter.

A longer description of the different locating strategies and their parameters does not belong here.
You can learn more by reading the Javadoc that comes with this project's source code, and trying out
what works best with your app!

## "Debug" mode

Sometimes an element may be misidentified, and you may then wish to check _what the machine sees_
with your own eyes.

For this purpose, there is a "debug" mode, so to speak. When activated, it opens a new window
whenever the computer tries to find some element on the screen. The window displays a picture of the
screen (after image-processing), and it also draws lines around each located element. It may also
show some additional information, such as coordinates, confidence level, or raw OCR output.

To activate "debug" mode, add the VM option `-Dimg_recog.debug=true`. For example:

```
java -Dimg_recog.debug=true -jar your_jar_name_here.jar
```

## Packaging

To package to a jar file:

```
mvn clean package
```

## Additional configurations

For the EAST text detection and for the _Tesseract_ character recognition to work, you need to
define two system properties: one for choosing the language, the other for the path to the directory
containing your _tessdata_ file(s) (a trained model of the desired language for Tesseract) as well
as your _pb_ file (a trained model for EAST). Read
the [documentation on trained Tesseract data files](https://tesseract-ocr.github.io/tessdoc/Data-Files.html)
and the
[documentation on the Tensorflow model](https://www.tensorflow.org/js/guide/models_and_layers)
for more information. To set the properties, the most direct way is as VM properties, as
in `java -Dkey=value -jar your_jar_name.jar` (replacing key and value as needed).

* tesseract.language (default=`por+eng`)
* tesseract.datapath (default=`tessdata/`)
* east.datapath (default=`tessdata/frozen_east_text_detection.pb`)

### Useful links

Get started by downloading these files and setting the properties as indicated above.

* [trained EAST model](https://www.dropbox.com/s/r2ingd0l3zt8hxs/frozen_east_text_detection.tar.gz?dl=1)
* [trained Tesseract models](https://github.com/tesseract-ocr/tessdata_fast) for several languages

## Licensing

**WIP - I need some help to understand how licences work...**

I think OpenCV and Tesseract are under the _Apache Licence 2.0_. And the ORB feature detection
algorithm that comes with OpenCV is under some BSD licence.

I may need to add licensing notices to the source, maybe to the binary.

And I need to figure out under what licence, if any, a new piece of software (in this case, this
project) that uses different functionalities from different libraries under different licences, is.
And what liberties and obligations that entails.

I remember reading somewhere that _modified_ software under the _Apache Licence 2.0_ can be
commercialised but must be open source. But does importing OpenCV and using its Java-wrapped C++ API
constitute a modification? No idea.
