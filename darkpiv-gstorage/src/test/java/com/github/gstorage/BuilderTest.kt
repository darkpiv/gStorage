package com.github.gstorage

import com.github.darkpiv.gstorage.*
import com.google.common.truth.Truth.assertThat
import junit.framework.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations.initMocks
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.lang.reflect.Type

@RunWith(RobolectricTestRunner::class)
class BuilderTest {

  private lateinit var builder: GBuilder

  @Before
  fun setup() {
    initMocks(this)

    builder = GBuilder(RuntimeEnvironment.application)
  }

  @Test
  fun testInit() {
    try {
      GBuilder(null)
      fail("Context should not be null")
    } catch (e: Exception) {
      assertThat(e).hasMessage("Context should not be null")
    }

  }

  @Test
  fun testStorage() {
    builder.build()
    assertThat(builder.storage).isInstanceOf(SharedPreferencesStorage::class.java)

    class MyStorage : Storage {
      override fun <T> put(key: String, value: T): Boolean {
        return false
      }

      override fun <T> get(key: String): T? {
        return null
      }

      override fun delete(key: String): Boolean {
        return false
      }

      override fun deleteAll(): Boolean {
        return false
      }

      override fun count(): Long {
        return 0
      }

      override fun contains(key: String): Boolean {
        return false
      }
    }
    builder.setStorage(MyStorage()).build()
    assertThat(builder.storage).isInstanceOf(MyStorage::class.java)
  }

  @Test
  fun testParser() {
    builder.build()
    assertThat(builder.parser).isInstanceOf(GsonParser::class.java)

    class MyParser : Parser {

      @Throws(Exception::class)
      override fun <T> fromJson(content: String, type: Type): T {
        return null as T
      }


      override fun <T> toJson(body: T): String? {
        return null
      }

    }
    builder.setParser(MyParser()).build()
    assertThat(builder.parser).isInstanceOf(MyParser::class.java)
  }

  @Test
  fun testConverter() {
    builder.build()
    assertThat(builder.converter).isInstanceOf(GConverter::class.java)

    class MyConverter : Converter {
      override fun <T> toString(value: T): String? {
        return null
      }

      @Throws(Exception::class)
      override fun <T> fromString(value: String, dataInfo: DataInfo): T? {
        return null
      }
    }

    builder.setConverter(MyConverter()).build()
    assertThat(builder.converter).isInstanceOf(MyConverter::class.java)
  }

  @Test
  fun testSerializer() {
    builder.build()
    assertThat(builder.serializer).isInstanceOf(GSerializer::class.java)

    class MySerializer : Serializer {
      override fun <T> serialize(cipherText: String, value: T): String? {
        return null
      }

      override fun deserialize(plainText: String?): DataInfo? {
        return null
      }
    }

    builder.setSerializer(MySerializer()).build()
    assertThat(builder.serializer).isInstanceOf(MySerializer::class.java)
  }

  @Test
  fun testEncryption() {
    builder.build()
    assertThat(builder.encryption).isInstanceOf(NoEncryption::class.java)

    class MyEncryption : Encryption {

      override fun init(): Boolean {
        return false
      }

      @Throws(Exception::class)
      override fun encrypt(key: String, value: String): String {
        return ""
      }

      @Throws(Exception::class)
      override fun decrypt(key: String, value: String): String {
        return ""
      }
    }
    builder.setEncryption(MyEncryption()).build()
    assertThat(builder.encryption).isInstanceOf(MyEncryption::class.java)
  }

  @Test
  fun testLogInterceptor() {
    builder.build()
    assertThat(builder.logInterceptor).isInstanceOf(LogInterceptor::class.java)

    class MyLogInterceptor : LogInterceptor {
      override fun onLog(message: String) {

      }
    }
    builder.setLogInterceptor(MyLogInterceptor()).build()
    assertThat(builder.logInterceptor).isInstanceOf(MyLogInterceptor::class.java)
  }
}
