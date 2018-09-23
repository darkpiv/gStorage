package com.github.darkpiv.gstorage

import com.google.gson.reflect.TypeToken
import java.util.*

@Suppress("UNCHECKED_CAST")
/**
 * Concrete implementation of encoding and decoding.
 * List types will be encoded/decoded by parser
 * Serializable types will be encoded/decoded object stream
 * Not serializable objects will be encoded/decoded by parser
 */
class GConverter(private val parser: Parser) :
  Converter {

  override fun <T> toString(value: T): String? {
    return if (value == null) {
      null
    } else parser.toJson(value)
  }

  @Throws(Exception::class)
  override fun <T> fromString(value: String, dataInfo: DataInfo?): T? {
    if (value.isEmpty() || dataInfo == null) {
      return null
    }
    GUtils.checkNull("data info", dataInfo)

    val keyType = dataInfo.keyClazz
    val valueType = dataInfo.valueClazz

    return when (dataInfo.dataType) {
      DataInfo.TYPE_OBJECT -> toObject<T>(value, keyType)
      DataInfo.TYPE_LIST -> toList<T>(value, keyType)
      DataInfo.TYPE_MAP -> toMap<Any, Any, T>(value, keyType, valueType)
      DataInfo.TYPE_SET -> toSet<T>(value, keyType)
      else -> null
    }
  }

  @Throws(Exception::class)
  private fun <T> toObject(json: String, type: Class<*>?): T? {
    if (type == null) return null;

    return parser.fromJson<T>(json, type)
  }

  @Throws(Exception::class)
  private fun <T> toList(json: String, type: Class<*>?): T? {
    if (type == null) {
      return ArrayList<Any>() as T
    }
    val list = parser.fromJson<List<T>>(
      json,
      object : TypeToken<List<T>>() {

      }.type
    )
    if (list.isEmpty()) return list as T
    return list.map { it -> parser.fromJson<Any>(parser.toJson(it)!!, type) } as T
  }

  @Throws(Exception::class)
  private fun <T> toSet(json: String, type: Class<*>?): T {
    val resultSet = HashSet<T>()
    if (type == null) {
      return resultSet as T
    }
    val set = parser.fromJson<Set<T>>(json, object : TypeToken<Set<T>>() {

    }.type)

    for (t in set) {
      val valueJson = parser.toJson(t)
      val value = parser.fromJson<T>(valueJson!!, type)
      resultSet.add(value)
    }
    return resultSet as T
  }

  @Throws(Exception::class)
  private fun <K, V, T> toMap(json: String, keyType: Class<*>?, valueType: Class<*>?): T {
    val resultMap = HashMap<K, V>()
    if (keyType == null || valueType == null) {
      return resultMap as T
    }
    val map = parser.fromJson<Map<K, V>>(json, object : TypeToken<Map<K, V>>() {

    }.type)

    for ((key, value) in map) {
      val keyJson = parser.toJson(key)
      val k = parser.fromJson<K>(keyJson!!, keyType)

      val valueJson = parser.toJson(value)
      val v = parser.fromJson<V>(valueJson!!, valueType)
      resultMap[k] = v
    }
    return resultMap as T
  }

}
