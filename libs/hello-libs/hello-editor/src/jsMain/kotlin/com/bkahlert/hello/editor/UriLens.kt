package com.bkahlert.hello.editor

import com.bkahlert.kommons.uri.Authority
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.Lens

public object UriLens : Lens<Uri?, String> {
    override val id: String get() = ""
    override fun get(parent: Uri?): String = parent?.toString().orEmpty()
    override fun set(parent: Uri?, value: String): Uri? = value.takeUnless { it.isBlank() }?.toUriOrNull()?.run {
        if (listOfNotNull(scheme, authority, path, query, fragment).size == 1 && path.contains(".")) {
            Uri("https", Authority(null, path.substringBefore("/", ""), null), path.substringAfter("/"), null, null)
        } else {
            this
        }
    }
}
