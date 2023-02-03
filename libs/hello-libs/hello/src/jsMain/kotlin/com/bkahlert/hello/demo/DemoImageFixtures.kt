package com.bkahlert.hello.demo

import com.bkahlert.kommons.uri.DataUri
import io.ktor.http.ContentType.Image

public object DemoImageFixtures : Iterable<DataUri> {

    /** The [Android](https://www.android.com/) robot as an [SVG](https://en.wikipedia.org/wiki/SVG). */
    public val AndroidRobot: DataUri by lazy { DataUri(Image.SVG, ANDROID_ROBOT_SVG) }

    override fun iterator(): Iterator<DataUri> = iterator {
        yield(AndroidRobot)
    }
}

@Suppress("LongLine")
private const val ANDROID_ROBOT_SVG =
    """<svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" viewBox="0 0 467.04645 250.16389"><path fill="#3DDB85" d="M293.91034,212.77477c-6.02515,0-10.92627-4.90459-10.92627-10.92982s4.90112-10.92628,10.92627-10.92628     c6.02536,0,10.92627,4.90105,10.92627,10.92628S299.9357,212.77477,293.91034,212.77477 M173.13611,212.77477     c-6.02538,0-10.92627-4.90459-10.92627-10.92982s4.90089-10.92628,10.92627-10.92628     c6.02515,0,10.92625,4.90105,10.92625,10.92628S179.16125,212.77477,173.13611,212.77477 M297.82919,146.95461l21.83826-37.82177     c1.25195-2.17392,0.50714-4.95069-1.66324-6.2061c-2.17035-1.25186-4.95068-0.50713-6.20609,1.66323l-22.11142,38.30052     c-16.909-7.71684-35.8996-12.015-56.1636-12.015c-20.26378,0-39.25438,4.29816-56.16338,12.015l-22.1114-38.30052     c-1.25543-2.17036-4.03575-2.91509-6.20612-1.66323c-2.17036,1.25541-2.91862,4.03218-1.66321,6.2061l21.83824,37.82177     c-37.49896,20.39503-63.14611,58.35866-66.89809,103.20929h262.40811     C360.97183,205.31326,335.32468,167.34964,297.82919,146.95461"/></svg>"""
