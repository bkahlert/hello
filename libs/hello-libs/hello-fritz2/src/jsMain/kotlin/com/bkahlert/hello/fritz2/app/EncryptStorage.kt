@file:JsModule("encrypt-storage")

package com.bkahlert.hello.fritz2.app

// Annoyingly, this library tries to be smart and converts types.
// Therefor only encryptString and decryptString are used.
public external class EncryptStorage(secret: String, options: EncryptStorageInit = definedExternally) {
    public fun encryptString(value: String): String
    public fun decryptString(value: String): String
}

public external interface EncryptStorageInit {
    /** (default: '') */
    public var prefix: String
}
