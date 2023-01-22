package com.bkahlert.kommons.binding

/** A development session to hold contextual information. */
public interface DevSession {
    /** Disposes this session, including all created resources. */
    public fun dispose()
}
