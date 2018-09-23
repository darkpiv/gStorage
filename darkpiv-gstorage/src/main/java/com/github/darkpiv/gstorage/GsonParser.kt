package com.github.darkpiv.gstorage

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.lang.reflect.Type

class GsonParser(private val gson: Gson) : Parser {

  @Throws(JsonSyntaxException::class)
  override fun <T> fromJson(content: String, type: Type): T {
    return gson.fromJson<T>(content, type)
  }

  override fun <T> toJson(body: T): String? {
    return gson.toJson(body)
  }

}
