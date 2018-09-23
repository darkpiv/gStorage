package com.orhanobut.hawk

/**
 * Used to handle encoding and decoding as an intermediate layer.
 *
 * Implement this interface if a custom implementation is needed
 *
 * @see HawkConverter
 */
interface Converter {

    /**
     * Encodes the value
     *
     * @param value will be encoded
     * @return the encoded string
     */
    fun <T> toString(value: T): String?

    /**
     * Decodes
     *
     * @param value is the encoded data
     * @return the plain value
     * @throws Exception
     */
    @Throws(Exception::class)
    fun <T> fromString(value: String, dataInfo: DataInfo): T?

}
