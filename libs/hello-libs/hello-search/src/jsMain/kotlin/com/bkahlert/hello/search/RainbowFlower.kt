package com.bkahlert.hello.search

import com.bkahlert.kommons.uri.DataUri
import io.ktor.http.ContentType

/** A rainbow flower an animated [SVG](https://en.wikipedia.org/wiki/SVG) used to indicate a multi-selection of various items. */
public val RainbowFlower: DataUri by lazy { DataUri(ContentType.Image.SVG, RAINBOW_FLOWER_SVG) }

// language=svg
private const val RAINBOW_FLOWER_SVG =
    """<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" aria-label="Rainbow gradient" role="img" cursor="default" width="320" height="320" viewBox="0 0 320 320" stroke-linecap="round">
    <style>
        .blue { stroke: rgb(41, 171, 226); }
        .red { stroke: rgb(194, 30, 115); }
        .yellow { stroke: rgb(235, 178, 29); }
        .green { stroke: rgb(105, 183, 69); }
        .teal { stroke: rgb(1, 129, 143); }
        .gray { stroke: rgb(128, 128, 128); }
    </style>
    <defs>
        <!--
        rgb(1, 129, 143), rgb(0, 144, 170), rgb(0, 158, 198), rgb(41, 171, 226))
        -->
        <path id="r1" class="blue">
            <animate id="p1" attributeName="d" values="m160,160l0,0 0,0;m130,110l30,-17 30,17;m130,60l30,-17 30,17;m160,20l0,0 0,0" dur="6s" repeatCount="indefinite"/>
            <animate attributeName="stroke-width" values="0;16;32;16;0" dur="6s" repeatCount="indefinite" begin="p1.begin"/>
        </path>
        <path id="r2" class="red">
            <animate attributeName="d" values="m160,160l0,0 0,0;m130,110l30,-17 30,17;m130,60l30,-17 30,17;m160,20l0,0 0,0" dur="6s" repeatCount="indefinite" begin="p1.begin+1s"/>
            <animate attributeName="stroke-width" values="0;16;32;16;0" dur="6s" repeatCount="indefinite" begin="p1.begin+1s"/>
        </path>
        <path id="r3" class="yellow">
            <animate attributeName="d" values="m160,160l0,0 0,0;m130,110l30,-17 30,17;m130,60l30,-17 30,17;m160,20l0,0 0,0" dur="6s" repeatCount="indefinite" begin="p1.begin+2s"/>
            <animate attributeName="stroke-width" values="0;16;32;16;0" dur="6s" repeatCount="indefinite" begin="p1.begin+2s"/>
        </path>
        <path id="r4" class="green">
            <animate attributeName="d" values="m160,160l0,0 0,0;m130,110l30,-17 30,17;m130,60l30,-17 30,17;m160,20l0,0 0,0" dur="6s" repeatCount="indefinite" begin="p1.begin+3s"/>
            <animate attributeName="stroke-width" values="0;16;32;16;0" dur="6s" repeatCount="indefinite" begin="p1.begin+3s"/>
        </path>
        <path id="r5" class="teal">
            <animate attributeName="d" values="m160,160l0,0 0,0;m130,110l30,-17 30,17;m130,60l30,-17 30,17;m160,20l0,0 0,0" dur="6s" repeatCount="indefinite" begin="p1.begin+4s"/>
            <animate attributeName="stroke-width" values="0;16;32;16;0" dur="6s" repeatCount="indefinite" begin="p1.begin+4s"/>
        </path>
        <path id="r6" class="gray">
            <animate attributeName="d" values="m160,160l0,0 0,0;m130,110l30,-17 30,17;m130,60l30,-17 30,17;m160,20l0,0 0,0" dur="6s" repeatCount="indefinite" begin="p1.begin+5s"/>
            <animate attributeName="stroke-width" values="0;16;32;16;0" dur="6s" repeatCount="indefinite" begin="p1.begin+5s"/>
        </path>
    </defs>
    <use xlink:href="#r1"/>
    <use xlink:href="#r1" transform="rotate(60 160 160)"/>
    <use xlink:href="#r1" transform="rotate(120 160 160)"/>
    <use xlink:href="#r1" transform="rotate(180 160 160)"/>
    <use xlink:href="#r1" transform="rotate(240 160 160)"/>
    <use xlink:href="#r1" transform="rotate(300 160 160)"/>
    <use xlink:href="#r2" transform="rotate(30 160 160)"/>
    <use xlink:href="#r2" transform="rotate(90 160 160)"/>
    <use xlink:href="#r2" transform="rotate(150 160 160)"/>
    <use xlink:href="#r2" transform="rotate(210 160 160)"/>
    <use xlink:href="#r2" transform="rotate(270 160 160)"/>
    <use xlink:href="#r2" transform="rotate(330 160 160)"/>
    <use xlink:href="#r3"/>
    <use xlink:href="#r3" transform="rotate(60 160 160)"/>
    <use xlink:href="#r3" transform="rotate(120 160 160)"/>
    <use xlink:href="#r3" transform="rotate(180 160 160)"/>
    <use xlink:href="#r3" transform="rotate(240 160 160)"/>
    <use xlink:href="#r3" transform="rotate(300 160 160)"/>
    <use xlink:href="#r4" transform="rotate(30 160 160)"/>
    <use xlink:href="#r4" transform="rotate(90 160 160)"/>
    <use xlink:href="#r4" transform="rotate(150 160 160)"/>
    <use xlink:href="#r4" transform="rotate(210 160 160)"/>
    <use xlink:href="#r4" transform="rotate(270 160 160)"/>
    <use xlink:href="#r4" transform="rotate(330 160 160)"/>
    <use xlink:href="#r5"/>
    <use xlink:href="#r5" transform="rotate(60 160 160)"/>
    <use xlink:href="#r5" transform="rotate(120 160 160)"/>
    <use xlink:href="#r5" transform="rotate(180 160 160)"/>
    <use xlink:href="#r5" transform="rotate(240 160 160)"/>
    <use xlink:href="#r5" transform="rotate(300 160 160)"/>
    <use xlink:href="#r6" transform="rotate(30 160 160)"/>
    <use xlink:href="#r6" transform="rotate(90 160 160)"/>
    <use xlink:href="#r6" transform="rotate(150 160 160)"/>
    <use xlink:href="#r6" transform="rotate(210 160 160)"/>
    <use xlink:href="#r6" transform="rotate(270 160 160)"/>
    <use xlink:href="#r6" transform="rotate(330 160 160)"/>
</svg>
"""
