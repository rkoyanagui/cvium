package com.rkoyanagui.img_recog;

import static org.hamcrest.MatcherAssert.assertThat;

import com.rkoyanagui.img_recog.impl.ImgRecogMobileElement;
import java.lang.reflect.Field;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ImgRecogPageFactory2Test
{

  // Not an img recog element type.
  static String string;
  // Valid img recog element type, but without any locator annotations.
  static ImgRecogMobileElement mobileElement;

  static Field[] fields;

  @BeforeAll
  static void loadFields()
  {
    // Gets this class's fields.
    fields = ImgRecogPageFactory2Test.class.getDeclaredFields();
  }

  static Field getByName(final String name)
  {
    return Stream.of(fields)
        .filter(f -> f.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Could not find field: " + name));
  }

  @Test
  void reject_fieldOfType_String_AsDecoratableTest()
  {
    final Field field = getByName("string");
    assertThat("Field of type 'String' should NOT be decoratable!",
        !ImgRecogPageFactory.isDecoratableList(field));
  }

  @Test
  void reject_Unparameterised_List_AsDecoratableTest()
  {
    final Field field = getByName("mobileElement");
    assertThat("Field with no locator annotations should NOT be decoratable!",
        !ImgRecogPageFactory.isDecoratableList(field));
  }

}
