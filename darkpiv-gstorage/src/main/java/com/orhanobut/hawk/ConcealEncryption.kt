package com.orhanobut.hawk

import android.content.Context
import android.util.Base64

import com.facebook.android.crypto.keychain.AndroidConceal
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain
import com.facebook.crypto.Crypto
import com.facebook.crypto.CryptoConfig
import com.facebook.crypto.Entity
import com.facebook.crypto.keychain.KeyChain

class ConcealEncryption constructor(private val crypto: Crypto) : Encryption {

    constructor(context: Context) : this(SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256))

    constructor(keyChain: KeyChain) : this(AndroidConceal.get().createDefaultCrypto(keyChain))

    override fun init(): Boolean {
        return crypto.isAvailable
    }

    @Throws(Exception::class)
    override fun encrypt(key: String, value: String): String {
        val entity = Entity.create(key)
        val bytes = crypto.encrypt(value.toByteArray(), entity)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    @Throws(Exception::class)
    override fun decrypt(key: String, value: String): String {
        val entity = Entity.create(key)
        val decodedBytes = Base64.decode(value, Base64.NO_WRAP)
        val bytes = crypto.decrypt(decodedBytes, entity)
        return String(bytes)
    }

}
