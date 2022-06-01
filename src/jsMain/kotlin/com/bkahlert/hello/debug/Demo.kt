package com.bkahlert.hello.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.api.rest.ClickUpException
import com.bkahlert.hello.clickup.api.rest.ErrorInfo
import com.bkahlert.hello.semanticui.SemanticUI
import com.bkahlert.hello.semanticui.collection.Header
import com.bkahlert.hello.semanticui.collection.LinkItem
import com.bkahlert.hello.semanticui.collection.Menu
import com.bkahlert.hello.semanticui.collection.TextMenu
import com.bkahlert.hello.semanticui.element.Header
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.kommons.SVGImage
import com.bkahlert.kommons.test.SvgFixture
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
fun Demos(
    name: String,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    SemanticUI("segments", "raised", attrs = attrs) {
        Header({
            +Attached.Top
            +Inverted
            style { property("border-bottom-width", "0") }
        }) { Text(name) }
        content?.invoke(this)
    }
}

@Composable
fun Demo(
    name: String,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    var dirty by remember { mutableStateOf(false) }
    SemanticUI("segment", attrs = attrs) {
        if (!dirty) {
            TextMenu({
                +Size.Small
                style { marginTop((-1).em); marginBottom(0.em) }
            }) {
                Header { Text(name) }
                Menu({ +Direction.Right + Size.Small }) {
                    LinkItem({
                        onClick { dirty = true }
                    }) {
                        Icon("redo", "alternate")
                        Text("Reset")
                    }
                }
            }
            content?.invoke(this)
        } else {
            Text("Resetting")
            dirty = false
        }
    }
}

val clickupException = ClickUpException(
    ErrorInfo("something went wrong", "TEST-1234"), RuntimeException("underlying problem")
)

fun <T> response(value: T) = Result.success(value)
fun <T> failedResponse(exception: Throwable = clickupException) = Result.failure<T>(exception)

@Suppress("SpellCheckingInspection", "JsonStandardCompliance")
const val SPACER = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAA" +
    "ALAAAAAABAAEAAAIBRAA7"

@Suppress("SpellCheckingInspection", "JsonStandardCompliance")
const val JOHN = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAMAA" +
    "ACdt4HsAAAC/VBMVEUAAABDQ0NCQkJCQkJFQ0IfOVBNSkf/06hBQUE6OjpCQkLC/2ZCQkJCQ" +
    "kJBQUE9PT2/+mFBQUFDQ0NDQ0NCQkKPr81BQUFCQkKPr83D/2YFN2FAQEDt65aqzaHY9H5DQ" +
    "0NDQ0PC/2aPr81BQUH/1qePr83D/2X/1KhDQ0MCAgH/2Kj/2KZBQUH/3Kv/2qn/362Pr88UE" +
    "xOPr8/D/2SokHn/06jC/2YAAAHF3GmPr83C/2YAAACn3nzC/2b/1KjD/2aRs8en2Yz/1qrG5" +
    "Wqy5YdAQECPr83/2qfD/2aPr82w7lL/2qxkXFSPr8215obC/2XE/mYAK1KPr87/1qrC/2QBA" +
    "QEfHx8+Pj7/4KD/06jC/2ZDQ0P///9mZmaPr80AAACu7FBOTk5HRkat609FRUT80KX90qdMT" +
    "EsAFipSUlJQUFAASYqw7lP//fr4x5cAPnW+/GJbW1v/7duMrMv/2bSTgG4AHDf/9ev/1q71y" +
    "6LqwpthYWFeXl639FlVVVWn5Un/8eP/6taIqMb/3r38zaDkvpjduJUAQXywknYAL1lYWFgAK" +
    "lCl40f/9/D/5cyDpMP/4MH/1av32J/z1pfWso/Rro22mn/An36ojnSeiXSPfWwDNF207liq6" +
    "ExyXksAJ0pLSkkAIkIAEB//59CQscqWu7771qPyyaHvx576ypuQkJAQWIackIW4nYMyeIA8g" +
    "n/X73vP8XOghWkAN2ij4mSAcmS89GByZ11qYVii1lVUVFSZyVAABgz/+vbR0dH/4saJrL2xs" +
    "bGNtKv4zqSxp57u35Wp2ZEASInBo4fg6obLqIUARoUqcII3fYCXhHDC+GqHdmeYfmQAM2G58" +
    "F6FsVmCa1VmXlWw4VN8ZlFaVU9WUU0ZFhPX19fLy8v848qcxLKTubCvr6//0qeZx5aax5Xj1" +
    "4idzoTj14De3HwYWXhbnHQcW3R3uXPM3W9Vkm+38G6m3W2S0m0AOWuMyWqu62SVe2K+3GCRe" +
    "GBwpl2r6lac0E2OvUhkU0JhUEBJPDDHqRSwAAAAWXRSTlMA/SHiLBQH851sNfrWvXleNQ3z2" +
    "c3Msqihd09BFQgD+fHv69DFuK6VkYd9XVRSRykkFhIM6uni4trX19fDv7Cwq6uqqqWSjIuIf" +
    "nNxbWxqY1BPOzUqKCUZC/0ofcIAAAT5SURBVFjDnZR1WFNRGMYvAoqA2N3d3d3d7d2FuR4b4" +
    "MaYc4yNqSAtCkgrCHZ3gNjd3d3dnY/n3O3eO+CeXfT3x57zne973/Od2MWQOA10wooXc3V1K" +
    "ov9D2VdHCq4uDsQAAf3JtX/We/qSOSj/j9ZFHMuXZsgJIuFQqGEcnCo7FRk/STHyg6iXKNXU" +
    "JRCKFTk5irlEtKiUvUiru9IiKK88uMvJCDlPVyLc+qdyxPyIFuxUaEM9vcyyskuPDgNipcih" +
    "F6F15fIo4ywi3IluQxcgN4YrFQEG4EySigEq4NBkEJCSIRKEbCoZP9ZlCZEClAGESn9vfxh4" +
    "xJ5cBBoA8ohFSbbO8A6RD5E8sW0WxC4FqFIAo6yLdqgFIEEuikVCgW401J2NgA4N2dBmoiwS" +
    "wvUDZDvNxDHccN02Pp1hA+yBQ/Y5w5gAAhct+26PDUp5cx0yLkr29JuMAblEH9AeIKSOTiNV" +
    "MqMV+KyxOm0RzFWg5IwtXMBziBLNgBpspSOw5K2LUYfgjvI7LBZdM75zQZy8c2fw5jZFTtBW" +
    "RPWNwCtj8moxZIyzgdSooObMyJo5zBQ5o7cwTpL0YJrvyPuaDSxehjE7Z2lWbLuV8YJq8MVU" +
    "Mf2cagM5s9Yay4nzvKE+IXguHYuOZwZds3aRBooZPtPVQBXsILqcybUQAdtHNRD5uqtuRRg4" +
    "MH2P6xN7KT05PqWhWPp4QxrMgkYlMZYKEekWku0njR+M5jxXkv2GOIMMEf6EWk8WaBbSCQIR" +
    "9aXVCkNtwK2zYqevkcXNoMR9BH6sWwBoiWzBmDgymbQGqfYzWhmLilkIJUT9TE2apVgLpEmR" +
    "O/HdGNNpxKIT2svymC+p5fEopkFA0UwHZDIOiA+7s1xCs2t26REA4PYPXsslxBHpRtj7EwpQ" +
    "TssgY3vjrUEITNIMz2dHY8hGIzTxIXM1zKRdn6InokaYChqwBa4aYUhGV6w9kSgLLngXD8MT" +
    "a0GBYqlESmGAlMda2B2aM29iZaYXVrJcAZD6tpDa9ce2mH7oW2GcdCcqY24eX/1/tWr9yvBv" +
    "5R5Apy0pHchvTyV5A9ifRT94+n65JvQIIUKl3epiXEzhnf18QNKEpgY8TXjoDVIuKoewq1v3" +
    "4nHE39JwAsR/9rM47lN4DSoygOYYjYlyGzVsoSNZl+Y6c3ZQF0eyaKFz6ZNm7YqVCoNDQWDp" +
    "xdj1JZEGc4GrJjMm+JloatIk+UbxQJqvhGHQTcehTpm+2Gc5PDZGBM97VbDrr4MzwaB+DjUH" +
    "19otp0dzb2DHKp2e8ym+OUbF/lScQ73HnrCkl3whzrL7WY1He2Cw7q1MDQ13SgDCpPAJriYw" +
    "3UPZcyLxOIfC3kofooFYsEwDM2ou0uXHbnkhjS4dGTZ0mUD0Pp29fj8gHkBB1D6hwGzZ/P5/" +
    "HFIg6EgO4/Pn/0WYbCPTPMbIg2q8O138MjSQVekwUg+ZN9ZHop7AbCgCvoWK8L8EzVKr363F" +
    "OTrtbFzioMqNuy71VcAMOW3MYnFYoHvpx59Klah9Cicfdasf/Pt49bvvhABBIi3vn/1YsMan" +
    "2oYN85HVVvST0ZmRqc/P7X+9IYNp9eferlG9SE9OnLLhfAiGfiER2dG67LzdJGZKh8fn/AtW" +
    "dlZOu/saJXqpKpIBuHpWd6QbF2k6ih0uJCp04FYlxWpGsspb9u0+1Qb8ry982zjzk0nFhD8B" +
    "SFQiZbgWV9tAAAAAElFTkSuQmCC"

object ImageFixtures {
    val JohnDoe: String get() = JOHN
    val Avatar: String get() = JOHN
    val KommonsLogo: SVGImage by lazy { SVGImage(SvgFixture.data.decodeToString()) }

    @Suppress("SpellCheckingInspection")
    val PearLogo: SVGImage = SVGImage(
        """
        <?xml version="1.0" encoding="utf-8"?>
        <svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
             x="0px" y="0px" viewBox="0 0 512 512" overflow="visible" xml:space="preserve">
        <g>
            <path fill="#953F97" d="M337.3,367.85c14.58,30.64,37.79,49.62,72.21,53.42c2.71,0.3,4.44,1.09,2.19,4.12
                c-2.22,2.98-4.31,6.04-6.46,9.06c-2.79,1.87-5.92,2.15-9.17,2.15c-85.7,0-171.41,0-257.11-0.01c-3.23,0-6.4-0.15-9.14-2.19
                c-16.15-19.34-25.48-41.68-29.28-66.46c2.71-2.8,6.26-2.2,9.57-2.2c30.06-0.06,60.12-0.03,90.17-0.04
                c42.27,0,84.54-0.02,126.81,0.03C330.6,365.75,334.31,365.16,337.3,367.85z"/>
            <path fill="#F4821F" d="M387.03,235.76c11.22,12.46,11.23,12.5-3.32,18.82c-22.12,9.61-37.01,26.24-46.74,47.92
                c-2.66,1.6-5.56,2.01-8.63,2.01c-70.79,0-141.57,0.01-212.36-0.05c-2.94,0-6.1,0.41-8.41-2.19c7.91-25.86,24.79-46.12,41.52-66.48
                c2.55-1.93,5.51-2.17,8.56-2.17c73.59-0.01,147.18-0.01,220.77-0.01C381.49,233.63,384.4,234.09,387.03,235.76z"/>
            <path fill="#E3363F" d="M107.57,302.28c76.47,0.07,152.93,0.15,229.4,0.22c-8.17,21.82-7.55,43.6,0.33,65.34
                c-78.92,0.03-157.84,0.07-236.76,0.11C97.38,345.47,100.23,323.64,107.57,302.28z"/>
            <path fill="#FCB621" d="M387.03,235.76c-79.31,0.01-158.63,0.03-237.94,0.04c15.78-20.08,28.54-41.87,37.44-65.86
                c2.54-1.38,5.26-1.8,8.13-1.8c50.13-0.01,100.27-0.02,150.4,0.03c2.8,0,5.69-0.17,7.99,1.97
                C356.69,196,370.77,216.45,387.03,235.76z"/>
            <path fill="#1A9CD7" d="M129.82,434.41c91.81,0.01,183.61,0.03,275.42,0.04c-21.13,30.3-51.46,46.74-85.89,56.9
                c-69.63,20.54-144.97-1.22-187.68-54.16C130.96,436.33,130.43,435.34,129.82,434.41z"/>
            <path fill="#5FBB48" d="M353.04,170.15c-55.51-0.07-111.01-0.13-166.52-0.2c10.44-28.75,23.03-55.35,56.77-63.58
                c8.3-2.03,12.23-0.51,11.12,8.11c-1.63,12.73,4.68,15.07,15.71,14.4c14.13-0.85,27.4-4,39.79-10.73c2.96-1.61,5.3-1.76,8.31-0.02
                C338.38,129.79,348.61,148.01,353.04,170.15z"/>
            <path fill="#5FBB48" d="M351.01,24.54c-0.35,46.91-29.8,81.48-76.35,89.88c-4.32,0.78-5.24-0.81-5.4-4.6
                c-2.01-46.93,24.28-83.15,69.02-94.92C351.11,11.52,351.11,11.52,351.01,24.54z"/>
        </g>
        </svg>
    """.trimIndent()
    )
}
