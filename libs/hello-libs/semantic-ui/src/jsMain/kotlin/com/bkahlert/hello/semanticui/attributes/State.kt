package com.bkahlert.hello.semanticui.attributes

public sealed class State(override vararg val classNames: String) : Modifier {
    public object Active : State("active")
    public object Hidden : State("hidden")
    public object Indeterminate : State("indeterminate")
    public object Focus : State("focus")
    public object Loading : State("loading")
    public object Disabled : State("disabled")
    public object Error : State("error")
}
