package com.github.gstorage

import android.content.Context
import com.github.darkpiv.gstorage.*
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations.initMocks

class DefaultGFacadeTest {

  private lateinit var gFacade: GFacade

  @Mock
  private lateinit var converter: Converter
  @Mock
  private lateinit var encryption: Encryption
  @Mock
  private lateinit var serializer: Serializer
  @Mock
  private lateinit var storage: Storage
  @Mock
  private lateinit var context: Context

  @Before
  fun setup() {
    initMocks(this)

    val builder = GBuilder(context)
      .setConverter(converter)
      .setSerializer(serializer)
      .setEncryption(encryption)
      .setStorage(storage)

    gFacade = DefaultHawkFacade(builder)
  }

  //region PUT

  @Test
  fun putSuccess() {
    `when`(converter.toString(VALUE)).thenReturn(CONVERTED_TEXT)
    `when`(encryption.encrypt(KEY, CONVERTED_TEXT)).thenReturn(CIPHER_TEXT)
    `when`(serializer.serialize(CIPHER_TEXT, VALUE)).thenReturn(SERIALIZED_TEXT)
    `when`(storage.put(KEY, SERIALIZED_TEXT)).thenReturn(true)

    assertThat(gFacade.put(KEY, VALUE)).isTrue()

    val inOrder = inOrder(converter, encryption, serializer, storage)
    inOrder.verify(converter).toString(VALUE)
    inOrder.verify(encryption).encrypt(KEY, CONVERTED_TEXT)
    inOrder.verify(serializer).serialize(CIPHER_TEXT, VALUE)
    inOrder.verify(storage).put(KEY, SERIALIZED_TEXT)
  }

  @Test
  fun putFailsOnConvert() {
    `when`(converter.toString(VALUE)).thenReturn(null)

    assertThat(gFacade.put(KEY, VALUE)).isFalse()

    verify(converter).toString(VALUE)
    verifyNoMoreInteractions(encryption, storage, serializer)
  }

  @Test
  fun putFailsOnEncrypt() {
    `when`(converter.toString(VALUE)).thenReturn(CONVERTED_TEXT)
    `when`(encryption.encrypt(KEY, CONVERTED_TEXT)).thenReturn(null)

    assertThat(gFacade.put(KEY, VALUE)).isFalse()

    val inOrder = inOrder(converter, encryption)
    inOrder.verify(converter).toString(VALUE)
    inOrder.verify(encryption).encrypt(KEY, CONVERTED_TEXT)
    verifyNoMoreInteractions(serializer, storage)
  }

  @Test
  fun putFailsOnSerialize() {
    `when`(converter.toString(VALUE)).thenReturn(CONVERTED_TEXT)
    `when`(encryption.encrypt(KEY, CONVERTED_TEXT)).thenReturn(CIPHER_TEXT)
    `when`(serializer.serialize(CIPHER_TEXT, VALUE)).thenReturn(null)

    assertThat(gFacade.put(KEY, VALUE)).isFalse()

    val inOrder = inOrder(converter, encryption, serializer, storage)
    inOrder.verify(converter).toString(VALUE)
    inOrder.verify(encryption).encrypt(KEY, CONVERTED_TEXT)
    inOrder.verify(serializer).serialize(CIPHER_TEXT, VALUE)
    verifyZeroInteractions(storage)
  }

  @Test
  fun putFailsOnStorage() {
    `when`(converter.toString(VALUE)).thenReturn(CONVERTED_TEXT)
    `when`(encryption.encrypt(KEY, CONVERTED_TEXT)).thenReturn(CIPHER_TEXT)
    `when`(serializer.serialize(CIPHER_TEXT, VALUE)).thenReturn(SERIALIZED_TEXT)
    `when`(storage.put(KEY, SERIALIZED_TEXT)).thenReturn(false)

    assertThat(gFacade.put(KEY, VALUE)).isFalse()

    val inOrder = inOrder(converter, encryption, serializer, storage)
    inOrder.verify(converter).toString(VALUE)
    inOrder.verify(encryption).encrypt(KEY, CONVERTED_TEXT)
    inOrder.verify(serializer).serialize(CIPHER_TEXT, VALUE)
    inOrder.verify(storage).put(KEY, SERIALIZED_TEXT)
  }

  //endregion

  @Test
  fun getWithDefault() {
    `when`(gFacade.get<Any>(anyString())).thenReturn(null)

    assertThat(gFacade["key", "default"]).isEqualTo("default")
  }

  @Test
  fun getSuccess() {
    `when`(storage.get<Any>(KEY)).thenReturn(SERIALIZED_TEXT)
    `when`(serializer.deserialize(SERIALIZED_TEXT)).thenReturn(DATA_INFO)
    `when`(encryption.decrypt(KEY, CIPHER_TEXT)).thenReturn(CONVERTED_TEXT)
    `when`(converter.fromString<Any>(CONVERTED_TEXT, DATA_INFO)).thenReturn(VALUE)

    assertThat(gFacade.get<Any>(KEY)).isEqualTo(VALUE)

    val inOrder = inOrder(converter, encryption, serializer, storage)
    inOrder.verify(storage).get<Any>(KEY)
    inOrder.verify(serializer).deserialize(SERIALIZED_TEXT)
    inOrder.verify(encryption).decrypt(KEY, CIPHER_TEXT)
    inOrder.verify(converter).fromString<Any>(CONVERTED_TEXT, DATA_INFO)
  }

  @Test
  fun getFailsOnStorage() {
    `when`(storage.get<Any>(KEY)).thenReturn(null)

    assertThat(gFacade.get<Any>(KEY)).isEqualTo(null)

    verify(storage).get<Any>(KEY)
    verifyZeroInteractions(encryption, serializer, converter)
  }

  @Test
  fun getFailsOnDeserialize() {
    `when`(storage.get<Any>(KEY)).thenReturn(SERIALIZED_TEXT)
    `when`(serializer.deserialize(SERIALIZED_TEXT)).thenReturn(null)

    assertThat(gFacade.get<Any>(KEY)).isEqualTo(null)

    val inOrder = inOrder(converter, encryption, serializer, storage)
    inOrder.verify(storage).get<Any>(KEY)
    inOrder.verify(serializer).deserialize(SERIALIZED_TEXT)

    verifyZeroInteractions(encryption, converter)
  }

  @Test
  fun getFailsOnDecrypt() {
    `when`(storage.get<Any>(KEY)).thenReturn(SERIALIZED_TEXT)
    `when`(serializer.deserialize(SERIALIZED_TEXT)).thenReturn(DATA_INFO)
    `when`(encryption.decrypt(KEY, CIPHER_TEXT)).thenReturn(null)

    assertThat(gFacade.get<Any>(KEY)).isEqualTo(null)

    val inOrder = inOrder(converter, encryption, serializer, storage)
    inOrder.verify(storage).get<Any>(KEY)
    inOrder.verify(serializer).deserialize(SERIALIZED_TEXT)
    inOrder.verify(encryption).decrypt(KEY, CIPHER_TEXT)

    verifyZeroInteractions(converter)
  }

  @Test
  fun getFailsOnConvert() {
    `when`(storage.get<Any>(KEY)).thenReturn(SERIALIZED_TEXT)
    `when`(serializer.deserialize(SERIALIZED_TEXT)).thenReturn(DATA_INFO)
    `when`(encryption.decrypt(KEY, CIPHER_TEXT)).thenReturn(CONVERTED_TEXT)
    `when`(converter.fromString<Any>(CONVERTED_TEXT, DATA_INFO)).thenReturn(null)

    assertThat(gFacade.get<Any>(KEY)).isEqualTo(null)

    val inOrder = inOrder(converter, encryption, serializer, storage)
    inOrder.verify(storage).get<Any>(KEY)
    inOrder.verify(serializer).deserialize(SERIALIZED_TEXT)
    inOrder.verify(encryption).decrypt(KEY, CIPHER_TEXT)
    inOrder.verify(converter).fromString<Any>(CONVERTED_TEXT, DATA_INFO)
  }

  //endregion

  @Test
  fun count() {
    `when`(storage.count()).thenReturn(100L)

    assertThat(gFacade.count()).isEqualTo(100L)
    verifyZeroInteractions(encryption, converter, serializer)
  }

  @Test
  fun deleteAll() {
    `when`(storage.deleteAll()).thenReturn(true)

    assertThat(gFacade.deleteAll()).isTrue()
    verifyZeroInteractions(encryption, converter, serializer)
  }

  @Test
  fun delete() {
    `when`(storage.delete(KEY)).thenReturn(true)

    assertThat(gFacade.delete(KEY)).isTrue()
    verifyZeroInteractions(encryption, converter, serializer)
  }

  @Test
  fun contains() {
    `when`(storage.contains(KEY)).thenReturn(true)

    assertThat(gFacade.contains(KEY)).isTrue()
    verifyZeroInteractions(encryption, converter, serializer)
  }

  @Test
  fun isBuilt() {
    assertThat(gFacade.isBuilt).isTrue()
  }

  companion object {

    private const val KEY = "KEY"
    private const val VALUE = "VALUE"
    private const val CONVERTED_TEXT = "CONVERTED_TEXT"
    private const val CIPHER_TEXT = "CIPHER_TEXT"
    private const val SERIALIZED_TEXT = "SERIALIZED_TEXT"
    private val DATA_INFO = DataInfo(
      DataInfo.TYPE_OBJECT,
      CIPHER_TEXT,
      String::class.java,
      null
    )
  }
}