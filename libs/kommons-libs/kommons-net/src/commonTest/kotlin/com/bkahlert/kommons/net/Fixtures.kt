package com.bkahlert.kommons.net

import io.ktor.http.Url

private const val COMPLETE_URI_STRING: String = "https://username:password@example.com:8080/poo/par?qoo=qar&qaz#foo=far&faz"
private const val EMPTY_URI_STRING: String = ""


private val COMPLETE_URI: Uri = Uri("https", Authority("username:password", "example.com", 8080), "/poo/par", "qoo=qar&qaz", "foo=far&faz")
fun Uri.Companion.completeUriString(): String = COMPLETE_URI_STRING
fun Uri.Companion.completeUri(): Uri = COMPLETE_URI

private val EMPTY_URI: Uri = Uri(null, null, "", null, null)
fun Uri.Companion.emptyUriString(): String = EMPTY_URI_STRING
fun Uri.Companion.emptyUri(): Uri = EMPTY_URI


private val COMPLETE_URL: Url = Url(COMPLETE_URI_STRING)
fun Url.Companion.completeUrlString(): String = COMPLETE_URI_STRING
fun Url.Companion.completeUrl(): Url = COMPLETE_URL

private val EMPTY_URL: Url = Url(EMPTY_URI_STRING)
fun Url.Companion.emptyUrlString(): String = EMPTY_URI_STRING
fun Url.Companion.emptyUrl(): Url = EMPTY_URL
