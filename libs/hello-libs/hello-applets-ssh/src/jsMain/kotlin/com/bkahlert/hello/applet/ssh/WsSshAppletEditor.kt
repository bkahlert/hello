package com.bkahlert.hello.applet.ssh

import com.bkahlert.hello.applet.AppletEditor
import com.bkahlert.hello.fritz2.mergeValidationMessages
import dev.fritz2.core.RenderContext
import dev.fritz2.core.max
import dev.fritz2.core.min
import dev.fritz2.core.placeholder
import dev.fritz2.core.type
import dev.fritz2.core.values
import dev.fritz2.headless.components.inputField

public class WsSshAppletEditor(isNew: Boolean, applet: WsSshApplet) : AppletEditor<WsSshApplet>(isNew, applet) {
    override fun RenderContext.renderFields() {
        inputField {
            val store = map(WsSshApplet.title())
            value(store)
            inputLabel {
                +"Title"
                inputTextfield {
                    type("text")
                    placeholder("Workstation")
                    keyups.values() handledBy store.update
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            value(map(WsSshApplet.server()))
            inputLabel {
                +"Server"
                inputTextfield {
                    type("url")
                    placeholder("ssh.proxy.example.com")
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            value(map(WsSshApplet.host()))
            inputLabel {
                +"Host"
                inputTextfield {
                    type("text")
                    placeholder("ssh.example.com")
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            value(map(WsSshApplet.port()))
            inputLabel {
                +"Port"
                inputTextfield {
                    type("number")
                    min("1")
                    max("65535")
                    placeholder("22")
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            value(map(WsSshApplet.username()))
            inputLabel {
                +"Username"
                inputTextfield {
                    type("text")
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            value(map(WsSshApplet.password()))
            inputLabel {
                +"Password"
                inputTextfield {
                    type("password")
                }.also(::mergeValidationMessages)
            }
        }
    }
}
