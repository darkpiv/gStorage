package com.github.darkpiv.gstorage

class DataInfo constructor(
  val dataType: Char,
  val cipherText: String?,
  val keyClazz: Class<*>?,
  val valueClazz: Class<*>?
) {

  companion object {

    const val TYPE_OBJECT = '0'
    const val TYPE_LIST = '1'
    const val TYPE_MAP = '2'
    const val TYPE_SET = '3'
  }
}
