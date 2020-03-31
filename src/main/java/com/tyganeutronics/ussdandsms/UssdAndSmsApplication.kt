package com.tyganeutronics.ussdandsms

import android.content.Intent
import android.view.accessibility.AccessibilityNodeInfo
import com.tyganeutronics.base.BaseApplication
import org.jetbrains.annotations.Contract

abstract class UssdAndSmsApplication : BaseApplication() {

    companion object {

        @get:Contract(pure = true)
        var active: Boolean = false

        var nodeInfo: AccessibilityNodeInfo? = null
        var expectingUssd: Boolean = false
        var intent: Intent = Intent()
        var message: String = ""
    }
}
