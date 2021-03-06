package com.github.darkpiv.gstorage

import android.content.Context

/**
 * Secure, simple key-value storage for Android.
 */
object GStorage {

  var gFacade: GFacade =
    GFacade.EmptyHawkFacade()
  /**
   * Use this method to verify if Hawk is ready to be used.
   *
   * @return true if correctly initialised and built. False otherwise.
   */
  val isBuilt: Boolean
    get() = gFacade.isBuilt

  /**
   * This will init the hawk without password protection.
   *
   * @param context is used to instantiate context based objects.
   * ApplicationContext will be used
   */
  @JvmStatic
  fun init(context: Context): GBuilder {
    return GBuilder(context)
  }

  @JvmStatic
  fun build(gBuilder: GBuilder) {
    gFacade =
      DefaultHawkFacade(gBuilder)
  }

  /**
   * Saves any type including any collection, primitive values or custom objects
   *
   * @param key   is required to differentiate the given data
   * @param value is the data that is going to be encrypted and persisted
   * @return true if the operation is successful. Any failure in any step will return false
   */
  @JvmStatic
  fun <T> put(key: String, value: T): Boolean {
    return gFacade.put(key, value)
  }

  /**
   * Gets the original data along with original type by the given key.
   * This is not guaranteed operation since Hawk uses serialization. Any change in in the requested
   * data type might affect the result. It's guaranteed to return primitive types and String type
   *
   * @param key is used to get the persisted data
   * @return the original object
   */
  @JvmStatic
  operator fun <T> get(key: String): T? {
    return gFacade[key]
  }

  /**
   * Gets the saved data, if it is null, default value will be returned
   *
   * @param key          is used to get the saved data
   * @param defaultValue will be return if the response is null
   * @return the saved object
   */
  @JvmStatic
  operator fun <T> get(key: String, defaultValue: T): T {
    return gFacade[key, defaultValue]
  }

  /**
   * Size of the saved data. Each key will be counted as 1
   *
   * @return the size
   */
  @JvmStatic
  fun count(): Long {
    return gFacade.count()
  }

  /**
   * Clears the storage, note that crypto data won't be deleted such as salt key etc.
   * Use resetCrypto in order to deleteAll crypto information
   *
   * @return true if deleteAll is successful
   */
  @JvmStatic
  fun deleteAll(): Boolean {
    return gFacade.deleteAll()
  }

  /**
   * Removes the given key/value from the storage
   *
   * @param key is used for removing related data from storage
   * @return true if delete is successful
   */
  @JvmStatic
  fun delete(key: String): Boolean {
    return gFacade.delete(key)
  }

  /**
   * Checks the given key whether it exists or not
   *
   * @param key is the key to check
   * @return true if it exists in the storage
   */
  @JvmStatic
  operator fun contains(key: String): Boolean {
    return gFacade.contains(key)
  }

  @JvmStatic
  fun destroy() {
    gFacade.destroy()
  }

}
