package com.bkahlert.hello.clickup.model

public class ClickUpException(
    err: String,
    ECODE: String,
    cause: Throwable?,
) : IllegalStateException("[$ECODE] $err", cause)
