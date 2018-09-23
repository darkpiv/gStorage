package com.github.gstorage;

import static com.google.common.truth.Truth.assertThat;

import com.github.darkpiv.gstorage.GStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class GFacadeIntegrationTest {

  @Before public void setUp() {
    GStorage.INSTANCE.init(RuntimeEnvironment.application).build();
  }

  @After public void tearDown() {
    if (GStorage.INSTANCE.isBuilt()) {
      GStorage.INSTANCE.deleteAll();
    }
    GStorage.INSTANCE.destroy();
  }

  @Test public void testSingleItem() {
    GStorage.INSTANCE.put("boolean", true);
    assertThat(GStorage.INSTANCE.get("boolean")).isEqualTo(true);

    GStorage.INSTANCE.put("string", "string");
    assertThat(GStorage.INSTANCE.get("string")).isEqualTo("string");

    GStorage.INSTANCE.put("float", 1.5f);
    assertThat(GStorage.INSTANCE.get("float")).isEqualTo(1.5f);

    GStorage.INSTANCE.put("integer", 10);
    assertThat(GStorage.INSTANCE.get("integer")).isEqualTo(10);

    GStorage.INSTANCE.put("char", 'A');
    assertThat(GStorage.INSTANCE.get("char")).isEqualTo('A');

    GStorage.INSTANCE.put("object", new FooBar());
    FooBar fooBar = GStorage.INSTANCE.get("object");

    assertThat(fooBar).isNotNull();
    assertThat(fooBar.getName()).isEqualTo("hawk");

    assertThat(GStorage.INSTANCE.put("innerClass", new FooBar.InnerFoo())).isTrue();
    FooBar.InnerFoo innerFoo = GStorage.INSTANCE.get("innerClass");
    assertThat(innerFoo).isNotNull();
    assertThat(innerFoo.getName()).isEqualTo("hawk");
  }

  @Test public void testSingleItemDefault() {
    boolean result = GStorage.INSTANCE.get("tag", true);
    assertThat(result).isEqualTo(true);
  }

  @Test public void testList() {
    List<String> list = new ArrayList<>();
    list.add("foo");
    list.add("bar");

    GStorage.INSTANCE.put("tag", list);

    List<String> list1 = GStorage.INSTANCE.get("tag");

    assertThat(list1).isNotNull();
    assertThat(list1.get(0)).isEqualTo("foo");
    assertThat(list1.get(1)).isEqualTo("bar");
  }

  @Test public void testEmptyList() {
    List<FooBar> list = new ArrayList<>();
    GStorage.INSTANCE.put("tag", list);

    List<FooBar> list1 = GStorage.INSTANCE.get("tag");

    assertThat(list1).isNotNull();
  }

  @Test public void testMap() {
    Map<String, String> map = new HashMap<>();
    map.put("key", "value");
    GStorage.INSTANCE.put("map", map);

    Map<String, String> map1 = GStorage.INSTANCE.get("map");

    assertThat(map1).isNotNull();
    assertThat(map1.get("key")).isEqualTo("value");
  }

  @Test public void testEmptyMap() {
    Map<String, FooBar> map = new HashMap<>();
    GStorage.INSTANCE.put("tag", map);

    Map<String, FooBar> map1 = GStorage.INSTANCE.get("tag");

    assertThat(map1).isNotNull();
  }

  @Test public void testSet() {
    Set<String> set = new HashSet<>();
    set.add("foo");
    GStorage.INSTANCE.put("set", set);

    Set<String> set1 = GStorage.INSTANCE.get("set");

    assertThat(set1).isNotNull();
    assertThat(set1.contains("foo")).isTrue();
  }

  @Test public void testEmptySet() {
    Set<FooBar> set = new HashSet<>();
    GStorage.INSTANCE.put("tag", set);

    Set<FooBar> set1 = GStorage.INSTANCE.get("tag");

    assertThat(set1).isNotNull();
  }

  @Test public void testCount() {
    GStorage.INSTANCE.deleteAll();
    String value = "test";
    GStorage.INSTANCE.put("tag", value);
    GStorage.INSTANCE.put("tag1", value);
    GStorage.INSTANCE.put("tag2", value);
    GStorage.INSTANCE.put("tag3", value);
    GStorage.INSTANCE.put("tag4", value);

    assertThat(GStorage.INSTANCE.count()).isEqualTo(5);
  }

  @Test public void testDeleteAll() {
    String value = "test";
    GStorage.INSTANCE.put("tag", value);
    GStorage.INSTANCE.put("tag1", value);
    GStorage.INSTANCE.put("tag2", value);

    GStorage.INSTANCE.deleteAll();

    assertThat(GStorage.INSTANCE.count()).isEqualTo(0);
  }

  @Test public void testDelete() {
    GStorage.INSTANCE.deleteAll();
    String value = "test";
    GStorage.INSTANCE.put("tag", value);
    GStorage.INSTANCE.put("tag1", value);
    GStorage.INSTANCE.put("tag2", value);

    GStorage.INSTANCE.delete("tag");

    String result = GStorage.INSTANCE.get("tag");

    assertThat(result).isNull();
    assertThat(GStorage.INSTANCE.count()).isEqualTo(2);
  }

  @Test public void testContains() {
    GStorage.INSTANCE.put("key", "value");

    assertThat(GStorage.INSTANCE.contains("key")).isTrue();
  }


  @Test public void testHugeData() {
    for (int i = 0; i < 100; i++) {
      GStorage.INSTANCE.put("" + i, "" + i);
    }
    assertThat(true).isTrue();
  }

}
