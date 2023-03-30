package com.bkahlert.kommons.dom

import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUri
import io.ktor.http.ContentType
import io.ktor.http.charset
import io.ktor.http.withCharsetIfNeeded
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.Charsets
import kotlinx.browser.window
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

// TODO move to kommons-uri

public fun ObjectUri(mediaType: ContentType, data: String): Uri {
    val charset: Charset = mediaType.charset() ?: Charsets.UTF_8
    return blobOf(mediaType.withCharsetIfNeeded(charset), data).toObjectUri()
}

public fun Uri.download(filename: String, context: HTMLElement = window.document.body()) {
    val anchor = checkNotNull(context.ownerDocument).createElement("a") as HTMLAnchorElement
    anchor.href = toString()
    anchor.download = filename
    context.appendChild(anchor);
    anchor.click();
    context.removeChild(anchor);
}

public fun blobOf(type: ContentType?, vararg parts: dynamic): Blob = Blob(arrayOf(*parts), BlobPropertyBag(type.toString()))
public fun Blob.toObjectUri(): Uri = URL.createObjectURL(this).toUri()
