package com.github.gstorage

import com.github.darkpiv.gstorage.*
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class GConverterTest {

  private lateinit var converter: Converter
  private lateinit var parser: Parser
  private lateinit var serializer: Serializer

  internal class Foo

  @Before fun setup() {
    parser = GsonParser(Gson())
    converter = GConverter(parser)
    serializer = GSerializer(null)
  }

  @Test fun encodeString() {
    val text = "text"
    val expected = parser.toJson(text)
    val actual = converter.toString(text)

    assertThat(actual).isEqualTo(expected)
  }

  @Test fun encodeCustomObject() {
    val data = Foo()
    val expected = parser.toJson(data)
    val actual = converter.toString(data)

    assertThat(actual).isEqualTo(expected)
  }

  @Test fun encodeList() {
    val data = ArrayList<String>()
    data.add("test")
    val expected = parser.toJson(data)
    val actual = converter.toString<List<String>>(data)

    assertThat(actual).isEqualTo(expected)
  }

  @Test fun encodeMap() {
    val data = HashMap<String, String>()
    data["key"] = "value"
    val expected = parser.toJson(data)
    val actual = converter.toString<Map<String, String>>(data)

    assertThat(actual).isEqualTo(expected)
  }

  @Test fun encodeSet() {
    val data = HashSet<String>()
    data.add("key")
    val expected = parser.toJson(data)
    val actual = converter.toString<Set<String>>(data)

    assertThat(actual).isEqualTo(expected)
  }


  @Test @Throws(Exception::class)
  fun decodeObject() {
    val clazz = "java.lang.String"
    val info = "00V"
    val cipher = "cipher"
    val dataInfo = serializer.deserialize("$clazz##$info@$cipher")
    val actual = converter.fromString<String>(cipher, dataInfo)
    assertThat(actual).isEqualTo(cipher)
  }

}
