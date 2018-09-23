package com.orhanobut.hawk

interface HawkFacade {

    val isBuilt: Boolean

    fun <T> put(key: String, value: T): Boolean

    operator fun <T> get(key: String): T?

    operator fun <T> get(key: String, defaultValue: T): T

    fun count(): Long

    fun deleteAll(): Boolean

    fun delete(key: String): Boolean

    operator fun contains(key: String): Boolean

    fun destroy()

    class EmptyHawkFacade : HawkFacade {

        override val isBuilt: Boolean
            get() = false

        override fun <T> put(key: String, value: T): Boolean {
            throwValidation()
            return false
        }

        override fun <T> get(key: String): T? {
            throwValidation()
            return null
        }

        override fun <T> get(key: String, defaultValue: T): T {
            throwValidation()
            return defaultValue
        }

        override fun count(): Long {
            throwValidation()
            return 0
        }

        override fun deleteAll(): Boolean {
            throwValidation()
            return false
        }

        override fun delete(key: String): Boolean {
            throwValidation()
            return false
        }

        override fun contains(key: String): Boolean {
            throwValidation()
            return false
        }

        override fun destroy() {
            throwValidation()
        }

        private fun throwValidation() {
            throw IllegalStateException("Hawk is not built. " + "Please call build() and wait the initialisation finishes.")
        }
    }
}
