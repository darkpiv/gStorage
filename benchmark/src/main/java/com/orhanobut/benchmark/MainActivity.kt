package com.orhanobut.benchmark

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.orhanobut.hawk.LogInterceptor
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
      timeHawkInit()
      timeHawkPut(testParent)
      timeHawkGet()
      timeHawkContains()
      timeHawkCount()
      timeHawkDelete()
    }


  }

  private fun timeHawkInit() {
    val startTime = System.currentTimeMillis()

    Hawk.init(this).setLogInterceptor(object : LogInterceptor {
      override fun onLog(message: String) {
        Log.d("HAWK", message)
      }
    }).build()

    val endTime = System.currentTimeMillis()
    println("Hawk.init: " + (endTime - startTime) + "ms")
  }

  private fun timeHawkPut(testParent: TestParent) {
    val startTime = System.currentTimeMillis()

    Hawk.put<Any>("key", testParent)

    val endTime = System.currentTimeMillis()
    println("Hawk.put: " + (endTime - startTime) + "ms")

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

  private fun timeHawkGet() {
    val startTime = System.currentTimeMillis()

    Hawk.get<Any>("key")

    val endTime = System.currentTimeMillis()
    println("Hawk.get: " + (endTime - startTime) + "ms")

    val start = System.currentTimeMillis()
    val rstul = Gson().fromJson<TestParent>(
      SecurePrefManager.with(this)
        .get("ask").defaultValue("").go(), TestParent::class.java
    )

    val end = System.currentTimeMillis()
    println("OLD.get: " + (end - start) + "ms")
  }

  private fun timeHawkCount() {
    val startTime = System.currentTimeMillis()

    Hawk.count()

    val endTime = System.currentTimeMillis()
    println("Hawk.count: count" + (endTime - startTime) + "ms")
  }

  private fun timeHawkContains() {
    val startTime = System.currentTimeMillis()

    Hawk.contains("key")

    val endTime = System.currentTimeMillis()
    println("Hawk.count: contains " + (endTime - startTime) + "ms")
  }

  private fun timeHawkDelete() {
    val startTime = System.currentTimeMillis()

    Hawk.delete("key")

    val endTime = System.currentTimeMillis()
    println("Hawk.count: delete " + (endTime - startTime) + "ms")
  }
}
