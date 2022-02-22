package com.rkoyanagui.utils;

import io.appium.java_client.HasSessionDetails;
import org.openqa.selenium.WebDriver;

public enum MobilePlatform
{

  ANDROID("Android"),
  IOS("iOS");

  public final String description;

  MobilePlatform(final String description)
  {
    this.description = description;
  }

  public boolean matches(final WebDriver driver)
  {
    final String driverPlatform;
    if (driver instanceof HasSessionDetails)
    {
      driverPlatform = ((HasSessionDetails) driver).getPlatformName();
      return this.description.equalsIgnoreCase(driverPlatform);
    }
    else
    {
      return false;
    }
  }

}
