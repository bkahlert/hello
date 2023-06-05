package com.bkahlert.hello.widget.ssh

import com.bkahlert.hello.fritz2.mergeValidationMessages
import com.bkahlert.hello.widget.WidgetEditor
import dev.fritz2.core.RenderContext
import dev.fritz2.core.max
import dev.fritz2.core.min
import dev.fritz2.core.placeholder
import dev.fritz2.core.type
import dev.fritz2.core.values
import dev.fritz2.headless.components.inputField

public class WsSshWidgetEditor(isNew: Boolean, widget: WsSshWidget) : WidgetEditor<WsSshWidget>(isNew, widget) {
    override fun RenderContext.renderFields() {
        inputField {
            val store = map(WsSshWidget.title())
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
            value(map(WsSshWidget.server()))
            inputLabel {
                +"Server"
                inputTextfield {
                    type("url")
                    placeholder("ssh.proxy.example.com")
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            value(map(WsSshWidget.host()))
            inputLabel {
                +"Host"
                inputTextfield {
                    type("text")
                    placeholder("ssh.example.com")
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            value(map(WsSshWidget.port()))
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
            value(map(WsSshWidget.username()))
            inputLabel {
                +"Username"
                inputTextfield {
                    type("text")
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            value(map(WsSshWidget.password()))
            inputLabel {
                +"Password"
                inputTextfield {
                    type("password")
                }.also(::mergeValidationMessages)
            }
        }
    }
}
