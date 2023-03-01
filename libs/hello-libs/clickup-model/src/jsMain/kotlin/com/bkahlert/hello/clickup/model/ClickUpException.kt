package com.bkahlert.hello.clickup.model

public class ClickUpException(
    public val err: String,
    public val ECODE: String,
    cause: Throwable?,
) : IllegalStateException("[$ECODE] $err", cause)
