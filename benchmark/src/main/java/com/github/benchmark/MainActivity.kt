package com.github.benchmark

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import com.github.darkpiv.gstorage.GStorage
import com.github.darkpiv.gstorage.LogInterceptor
import com.google.gson.Gson
import com.prashantsolanki.secureprefmanager.SecurePrefManager
import com.prashantsolanki.secureprefmanager.SecurePrefManagerInit
import com.prashantsolanki.secureprefmanager.encryptor.AESEncryptor


class MainActivity : Activity() {
  data class Test(val id: Int, val name: String)
  data class TestParent(val id: Int, val name: String, val test: Test)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val test = Test(1, "H?ello kityu")
    val testParent = TestParent(1, "Parent", test)
    val benchmark = findViewById<Button>(R.id.benchmark)
    val encryptor = AESEncryptor(this)
    SecurePrefManagerInit.Initializer(applicationContext)
      .useEncryption(true)
      .setCustomEncryption(encryptor)
      .initialize()

    benchmark.setOnClickListener {
      timeGStorageInit()
      timeGStoragePut(testParent)
      timeGStorageGet()
      timeGStorageContains()
      timeGStorageCount()
      timeGStorageDelete()
    }


  }

  private fun timeGStorageInit() {
    val startTime = System.currentTimeMillis()

    GStorage.init(this).setLogInterceptor(object : LogInterceptor {
      override fun onLog(message: String) {
        Log.d("HAWK", message)
      }
    }).build()

    val endTime = System.currentTimeMillis()
    println("GStorage.init: " + (endTime - startTime) + "ms")
  }

  private fun timeGStoragePut(testParent: TestParent) {
    val startTime = System.currentTimeMillis()

    GStorage.put<Any>("key", testParent)

    val endTime = System.currentTimeMillis()
    println("GStorage.put: " + (endTime - startTime) + "ms")

    val start = System.currentTimeMillis()
    SecurePrefManager.with(this)
      .set("ask")
      .value(Gson().toJson(testParent))
      .go()
    val end = System.currentTimeMillis()
    println("OLD.put: " + (end - start) + "ms")
    val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
    println("test old + " + sharedPref.getString("ask", ""))

  }

  private fun timeGStorageGet() {
    val startTime = System.currentTimeMillis()

    GStorage.get<Any>("key")

    val endTime = System.currentTimeMillis()
    println("GStorage.get: " + (endTime - startTime) + "ms")

    val start = System.currentTimeMillis()
    val rstul = Gson().fromJson<TestParent>(
      SecurePrefManager.with(this)
        .get("ask").defaultValue("").go(), TestParent::class.java
    )

    val end = System.currentTimeMillis()
    println("OLD.get: " + (end - start) + "ms")
  }

  private fun timeGStorageCount() {
    val startTime = System.currentTimeMillis()

    GStorage.count()

    val endTime = System.currentTimeMillis()
    println("GStorage.count: count" + (endTime - startTime) + "ms")
  }

  private fun timeGStorageContains() {
    val startTime = System.currentTimeMillis()

    GStorage.contains("key")

    val endTime = System.currentTimeMillis()
    println("GStorage.count: contains " + (endTime - startTime) + "ms")
  }

  private fun timeGStorageDelete() {
    val startTime = System.currentTimeMillis()

    GStorage.delete("key")

    val endTime = System.currentTimeMillis()
    println("GStorage.count: delete " + (endTime - startTime) + "ms")
  }
}
