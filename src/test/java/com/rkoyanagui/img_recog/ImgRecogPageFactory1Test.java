package com.rkoyanagui.img_recog;

import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.collect.ImmutableList;
import com.rkoyanagui.img_recog.impl.ImgRecogMobileElement;
import com.rkoyanagui.img_recog.impl.ImgRecogWebElement;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ImgRecogPageFactory1Test
{

  // Valid img recog element lists.
  static final List<ImgRecogMobileElement> MOBILE_ELEMENT_LIST = ImmutableList.of();
  static final List<ImgRecogWebElement> WEB_ELEMENT_LIST = ImmutableList.of();
  static final List<ImgRecogElement> ELEMENT_LIST = ImmutableList.of();
  // Not a list.
  static final Collection<ImgRecogElement> ELEMENT_COLLECTION = ImmutableList.of();
  // List without a type parameter.
  static final List LIST = ImmutableList.of();
  // List of something that is not an img recog element.
  static final List<String> STRING_LIST = ImmutableList.of();

  static Field[] fields;

  @BeforeAll
  static void loadFields()
  {
    // Gets this class's fields.
    fields = ImgRecogPageFactory1Test.class.getDeclaredFields();
  }

  static Field getByName(final String name)
  {
    return Stream.of(fields)
        .filter(f -> f.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Could not find field: " + name));
  }

  @Test
  void accept_ImgRecogMobileElement_List_AsDecoratableTest()
  {
    final Field field = getByName("MOBILE_ELEMENT_LIST");
    assertThat("Field of type 'List<ImgRecogMobileElement>' should be decoratable!",
        ImgRecogPageFactory.isDecoratableList(field));
  }

  @Test
  void accept_ImgRecogWebElement_List_AsDecoratableTest()
  {
    final Field field = getByName("WEB_ELEMENT_LIST");
    assertThat("Field of type 'List<ImgRecogWebElement>' should be decoratable!",
        ImgRecogPageFactory.isDecoratableList(field));
  }

  @Test
  void accept_ImgRecogElement_List_AsDecoratableTest()
  {
    final Field field = getByName("ELEMENT_LIST");
    assertThat("Field of type 'List<ImgRecogElement>' should be decoratable!",
        ImgRecogPageFactory.isDecoratableList(field));
  }

  @Test
  void reject_Collection_AsDecoratableTest()
  {
    final Field field = getByName("ELEMENT_COLLECTION");
    assertThat("Field of type 'Collection<ImgRecogElement>' should NOT be decoratable!",
        !ImgRecogPageFactory.isDecoratableList(field));
  }

  @Test
  void reject_Unparameterised_List_AsDecoratableTest()
  {
    final Field field = getByName("LIST");
    assertThat("Field of type 'List' (without type param) should NOT be decoratable!",
        !ImgRecogPageFactory.isDecoratableList(field));
  }

  @Test
  void reject_String_List_AsDecoratableTest()
  {
    final Field field = getByName("STRING_LIST");
    assertThat("Field of type 'List<String>' should NOT be decoratable!",
        !ImgRecogPageFactory.isDecoratableList(fields[5]));
  }

}
