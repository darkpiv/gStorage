package com.orhanobut.hawk;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
public class HawkFacadeIntegrationTest {

  @Before public void setUp() {
    Hawk.INSTANCE.init(RuntimeEnvironment.application).build();
  }

  @After public void tearDown() {
    if (Hawk.INSTANCE.isBuilt()) {
      Hawk.INSTANCE.deleteAll();
    }
    Hawk.INSTANCE.destroy();
  }

  @Test public void testSingleItem() {
    Hawk.INSTANCE.put("boolean", true);
    assertThat(Hawk.INSTANCE.get("boolean")).isEqualTo(true);

    Hawk.INSTANCE.put("string", "string");
    assertThat(Hawk.INSTANCE.get("string")).isEqualTo("string");

    Hawk.INSTANCE.put("float", 1.5f);
    assertThat(Hawk.INSTANCE.get("float")).isEqualTo(1.5f);

    Hawk.INSTANCE.put("integer", 10);
    assertThat(Hawk.INSTANCE.get("integer")).isEqualTo(10);

    Hawk.INSTANCE.put("char", 'A');
    assertThat(Hawk.INSTANCE.get("char")).isEqualTo('A');

    Hawk.INSTANCE.put("object", new FooBar());
    FooBar fooBar = Hawk.INSTANCE.get("object");

    assertThat(fooBar).isNotNull();
    assertThat(fooBar.getName()).isEqualTo("hawk");

    assertThat(Hawk.INSTANCE.put("innerClass", new FooBar.InnerFoo())).isTrue();
    FooBar.InnerFoo innerFoo = Hawk.INSTANCE.get("innerClass");
    assertThat(innerFoo).isNotNull();
    assertThat(innerFoo.getName()).isEqualTo("hawk");
  }

  @Test public void testSingleItemDefault() {
    boolean result = Hawk.INSTANCE.get("tag", true);
    assertThat(result).isEqualTo(true);
  }

  @Test public void testList() {
    List<String> list = new ArrayList<>();
    list.add("foo");
    list.add("bar");

    Hawk.INSTANCE.put("tag", list);

    List<String> list1 = Hawk.INSTANCE.get("tag");

    assertThat(list1).isNotNull();
    assertThat(list1.get(0)).isEqualTo("foo");
    assertThat(list1.get(1)).isEqualTo("bar");
  }

  @Test public void testEmptyList() {
    List<FooBar> list = new ArrayList<>();
    Hawk.INSTANCE.put("tag", list);

    List<FooBar> list1 = Hawk.INSTANCE.get("tag");

    assertThat(list1).isNotNull();
  }

  @Test public void testMap() {
    Map<String, String> map = new HashMap<>();
    map.put("key", "value");
    Hawk.INSTANCE.put("map", map);

    Map<String, String> map1 = Hawk.INSTANCE.get("map");

    assertThat(map1).isNotNull();
    assertThat(map1.get("key")).isEqualTo("value");
  }

  @Test public void testEmptyMap() {
    Map<String, FooBar> map = new HashMap<>();
    Hawk.INSTANCE.put("tag", map);

    Map<String, FooBar> map1 = Hawk.INSTANCE.get("tag");

    assertThat(map1).isNotNull();
  }

  @Test public void testSet() {
    Set<String> set = new HashSet<>();
    set.add("foo");
    Hawk.INSTANCE.put("set", set);

    Set<String> set1 = Hawk.INSTANCE.get("set");

    assertThat(set1).isNotNull();
    assertThat(set1.contains("foo")).isTrue();
  }

  @Test public void testEmptySet() {
    Set<FooBar> set = new HashSet<>();
    Hawk.INSTANCE.put("tag", set);

    Set<FooBar> set1 = Hawk.INSTANCE.get("tag");

    assertThat(set1).isNotNull();
  }

  @Test public void testCount() {
    Hawk.INSTANCE.deleteAll();
    String value = "test";
    Hawk.INSTANCE.put("tag", value);
    Hawk.INSTANCE.put("tag1", value);
    Hawk.INSTANCE.put("tag2", value);
    Hawk.INSTANCE.put("tag3", value);
    Hawk.INSTANCE.put("tag4", value);

    assertThat(Hawk.INSTANCE.count()).isEqualTo(5);
  }

  @Test public void testDeleteAll() {
    String value = "test";
    Hawk.INSTANCE.put("tag", value);
    Hawk.INSTANCE.put("tag1", value);
    Hawk.INSTANCE.put("tag2", value);

    Hawk.INSTANCE.deleteAll();

    assertThat(Hawk.INSTANCE.count()).isEqualTo(0);
  }

  @Test public void testDelete() {
    Hawk.INSTANCE.deleteAll();
    String value = "test";
    Hawk.INSTANCE.put("tag", value);
    Hawk.INSTANCE.put("tag1", value);
    Hawk.INSTANCE.put("tag2", value);

    Hawk.INSTANCE.delete("tag");

    String result = Hawk.INSTANCE.get("tag");

    assertThat(result).isNull();
    assertThat(Hawk.INSTANCE.count()).isEqualTo(2);
  }

  @Test public void testContains() {
    Hawk.INSTANCE.put("key", "value");

    assertThat(Hawk.INSTANCE.contains("key")).isTrue();
  }


  @Test public void testHugeData() {
    for (int i = 0; i < 100; i++) {
      Hawk.INSTANCE.put("" + i, "" + i);
    }
    assertThat(true).isTrue();
  }

}
