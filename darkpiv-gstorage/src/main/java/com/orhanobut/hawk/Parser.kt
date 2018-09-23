package com.orhanobut.hawk

import java.lang.reflect.Type

/**
 * Intermediate layer that handles serialization/deserialization for the end result.
 * This is not the same as [Serializer]. This interface is only used to convert the intermediate value
 * into String or vice-versa to be used for [Storage]
 *
 *
 *
 * Use custom implementation if built-in implementation is not enough.
 *
 * @see GsonParser
 */
interface Parser {

  /**
   * Deserialize the given text for the given type and returns it.
   *
   * @param content is the value that will be deserialized.
   * @param type    is the object type which value will be converted to.
   * @param <T>     is the expected type.
   * @return the expected type.
   * @throws Exception if the operation is not successful.
  </T> */
  @Throws(Exception::class)
  fun <T> fromJson(content: String, type: Type): T

  /**
   * Serialize the given object to String.
   *
   * @param body is the object that will be serialized.
   * @return the serialized text.
   */
  fun <T> toJson(body: T): String?

}
