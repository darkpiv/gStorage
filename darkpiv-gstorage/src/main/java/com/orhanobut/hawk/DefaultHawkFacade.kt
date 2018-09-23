package com.orhanobut.hawk

class DefaultHawkFacade(builder: HawkBuilder) : HawkFacade {

  private val storage: Storage = builder.storage
  private val converter: Converter = builder.converter
  private val encryption: Encryption = builder.encryption
  private val serializer: Serializer = builder.serializer
  private val logInterceptor: LogInterceptor = builder.logInterceptor

  init {
    logInterceptor.onLog("Hawk.init -> Encryption : " + encryption.javaClass.simpleName)
  }

  override fun <T> put(key: String, value: T): Boolean {
    // Validate
    HawkUtils.checkNull("Key", key)
    log("Hawk.put -> key: $key, value: $value")

    // If the value is null, delete it
    if (value == null) {
      log("Hawk.put -> Value is null. Any existing value will be deleted with the given key")
      return delete(key)
    }

    // 1. Convert to text
    val plainText = converter.toString<Any>(value)
    log("Hawk.put -> Converted to $plainText")
    if (plainText == null) {
      log("Hawk.put -> Converter failed")
      return false
    }

    // 2. Encrypt the text
    var cipherText: String? = null
    try {
      cipherText = encryption.encrypt(key, plainText)
      log("Hawk.put -> Encrypted to $cipherText")
    } catch (e: Exception) {
      e.printStackTrace()
    }

    if (cipherText == null) {
      log("Hawk.put -> Encryption failed")
      return false
    }

    // 3. Serialize the given object along with the cipher text
    val serializedText = serializer.serialize<Any>(cipherText, value)
    log("Hawk.put -> Serialized to $serializedText")
    if (serializedText == null) {
      log("Hawk.put -> Serialization failed")
      return false
    }

    // 4. Save to the storage
    if (storage.put<Any>(key, serializedText)) {
      log("Hawk.put -> Stored successfully")
      return true
    } else {
      log("Hawk.put -> Store operation failed")
      return false
    }
  }

  override fun <T> get(key: String): T? {
    // 1. Get serialized text from the storage
    val serializedText = storage.get<String>(key)
    log("Hawk.get -> Fetched from storage : $serializedText")
    if (serializedText == null) {
      log("Hawk.get -> Fetching from storage failed")
      return null
    }

    // 2. Deserialize
    val dataInfo = serializer.deserialize(serializedText)
    log("Hawk.get -> Deserialized")
    if (dataInfo == null) {
      log("Hawk.get -> Deserialization failed")
      return null
    }

    // 3. Decrypt
    var plainText: String? = null
    try {
      if (dataInfo.cipherText == null) return null
      plainText = encryption.decrypt(key, dataInfo.cipherText)
      log("Hawk.get -> Decrypted to : $plainText")
    } catch (e: Exception) {
      log("Hawk.get -> Decrypt failed: " + e.message)
    }

    if (plainText == null) {
      log("Hawk.get -> Decrypt failed")
      return null
    }

    // 4. Convert the text to original data along with original type
    var result: T? = null
    try {
      result = converter.fromString<T>(plainText, dataInfo)
      log("Hawk.get -> Converted to : " + result!!)
    } catch (e: Exception) {
      log("Hawk.get -> Converter failed")
    }

    return result
  }

  override fun <T> get(key: String, defaultValue: T): T {
    return get<T>(key) ?: return defaultValue
  }

  override fun count(): Long {
    return storage.count()
  }

  override fun deleteAll(): Boolean {
    return storage.deleteAll()
  }

  override fun delete(key: String): Boolean {
    return storage.delete(key)
  }

  override fun contains(key: String): Boolean {
    return storage.contains(key)
  }

  override val isBuilt: Boolean
    get() = true

  override fun destroy() {}

  private fun log(message: String) {
    logInterceptor.onLog(message)
  }
}
