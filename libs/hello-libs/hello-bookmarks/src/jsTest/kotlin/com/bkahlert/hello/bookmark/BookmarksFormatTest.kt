package com.bkahlert.hello.bookmark

import com.bkahlert.kommons.uri.Uri
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.ktor.util.decodeBase64String
import kotlinx.datetime.Instant
import kotlin.test.Test

class BookmarksFormatTest {

    @Test
    fun read_netscape() {
        BookmarksFormat.NetscapeBookmarksFileFormat.read(NETSCAPE_BOOKMARKS) should {
            it shouldHaveSize 1
            it[0] shouldBe BOOKMARKS
        }
    }

    @Test
    fun write_netscape() {
        BookmarksFormat.NetscapeBookmarksFileFormat.write(listOf(BOOKMARKS)) shouldBe NETSCAPE_BOOKMARKS
    }
}

private val BOOKMARKS = BookmarkTreeNode.Folder(
    title = "Favorites Bar",
    children = listOf(
        BookmarkTreeNode.Bookmark(
            title = "OKLCH",
            url = Uri("https://oklch.com/#69.85,0.133,232.37,100"),
            icon = Uri(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADSElEQVQ4jV2TS2icZRhGn/f9vv8yk5lkZowVCwlOKnEh2oISa+qVgnZsTJvSClIXitCA4AWq4kbIxlVBIkKxRtCNSJMYRRclUXMhabAiaVEJVCKVViqtmcx9/vv3urFSPauzONtD+B9Xjx9/iDh8waA1aMgrgjwQ+ZcUB6tC7Y+3v3Nu5eaebsjkkSP2np5d7zL5xxw7soKkiSiqgciHdmK4Vgg/bkXCwcmVi7U3n5laDwGAxjDGR18uWK7SXxVcfqISlI2wb0x7k7ueLpHqdFGZ+kDIEcMq4Fwq5GrQmmvX/xr+9P2NSC1iUV7Z89x4IZV9tuK1QyKwafmqs7QXuaH9ZPfuIJXKof3zmiKtxYslKqRVfwQre2DljzP00+tfP2jBLFvGgxaPpFnlXGmX5EYeo/o3i0hqW8gfPoTm0qxszUyQSolhDoSoLYkEj2gfqVHbsZQfSqwaIW8rDUpuZIAaCz+i+uUSxDQArZE/OEwApDIzQeI6JuOyboUY1ZHK7G6bWIzHvH3fTiocuBe1hV9QPb0KleoE2EF9dhkAkD84TCKC6sxHHLAtQPoBHarUHUnQouJTd+P2Uh+uf/sryl+ch5vqBIsPSggq3Y367AokjlE4fAhs21SZmSBo6tURuzCcICYbACBghJwGcwwxBC0ACwFIg6D/aWzApEGIoCNyfteu1b8xd11MbKhv6E6QpXB1eg1iuwAI3AjRVXoc+ZFH0VhYRXV6SuyODKLEv6JDcr53becukSi5NF8mAlAcKhIA/Pn5BSRRhNtKu5EfGUBzYU2qU9+BUnnj6ERHAc7qSDpONZP4qMUdpDIsG/ObLCDpGyqSiRIktRa6R3aitrAuldOrZLldhhGQH3oxITtBADA5VhsvpLKv+s0roYVQU7PBO/Z2S9/+HgKAzaXf5NrkOUo7iWHx420u22V/872e8bdeU2MY4+SeJ+e11RrIdnT3h5EvbFNSuVgjRQbBtTouT6+Lcq2EiCjvZnXNb80FCb14yw9n5N+ZTh0T69Ye7wSb4KWMhoWwCvIb0MZDWifIKEESVCPbeCfrjfIb9384Gv3nxht89vbWw2lFz7NpDloS9FrGh228y2kJV5E0PrnvxL7lm/u/Ad5EkwNjP/GeAAAAAElFTkSuQmCC"
            ),
            dateAdded = Instant.parse("2023-02-10T11:11:06Z"),
            lastModified = null,
        ),
        BookmarkTreeNode.Folder(
            title = "Folder A",
            children = listOf(
                BookmarkTreeNode.Bookmark(
                    title = "Bookmark A",
                    url = Uri("http://localhost:8080/#widgets/bookmarks-widget"),
                    icon = Uri(
                        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACg0lEQVQ4jY2TQYhVdRTGf+f873v33ZnJGhiFyqZhhBEjEAkpUEISpihdWm1auBHcDIhEgotu5SZXbduo0KpRUFC34uykiDZFBG0itRY588bHvPvuff//OS3mvSkZF53lOZzvfOc75xNGscyJNsD7XG14SixzIsDu9ncfHdAbB2fib0vv1gACcHf3mY87oXXSceo0vHLk/lcXHUTAAZYXzr+459HqtavH39x1c/H1luZF1cThN79+MHtBATKRVyY13zep+T4lvArwGaWUsFkPremM8AadYn5t+tmXdOq5BYQ5gAzAkEFt0RwHfLCNf+ynKDYoHj9uZzE2psO2IA9gNMHFFEHBxQV1St2sHVGn1AkXdVUtqkZDMnV3NfP1LQaK4u4gIrgnoTTAAEruckvOVQYyUdWeJVPX6OqsbgEAqAiVDU1U3l6ZPfu1gLq7CDSpibOumhXVAE1JLSTMU3cbgGEpk/DypOanxnkBahJRGi8GNVkySbHBYO0JAHcHRBI2XE9V778aGt5uiUwVg8YANXcL+NqWiIwW7kgWzOy69sPcX73efN3rzWs/zCWX91wlTfUreWZjgIn048C2MxARcad7+O+LTzBY2bn0Z5xoxR0bdf75l5fjj3vnjn5y6/QfDqLjdhEBdxeRsKl+qeNHqoqi3Y5J7j8/49/v32srh19b3PPt74cEPBu1O46B4D4+X+ljBv0dLdv1cN3uHVjwSx++wwtD/SLrPboDHNVNIbyTa6a5ZqpIZwQg5cgrpCwEtBOyXPMY25oiMvqTsQY/b1j9C7gb6SeATyl9bKaYhmtJ/F7R7c5Mr3Zza3X6SfSH8ZmB/2fnnUy2bi++FW4c+9fO/wC4DzBSG8kiewAAAABJRU5ErkJggg"
                    ),
                    dateAdded = Instant.parse("2023-06-26T16:30:34Z"),
                    lastModified = null,
                )
            ),
            dateAdded = Instant.parse("2023-06-26T16:30:04Z"),
            lastModified = Instant.parse("2023-06-26T16:32:24Z"),
        ),
        BookmarkTreeNode.Folder(
            title = "Folder B",
            children = listOf(
                BookmarkTreeNode.Folder(
                    title = "Folder C",
                    children = listOf(
                        BookmarkTreeNode.Bookmark(
                            title = "Bookmark C",
                            url = Uri("javascript:window.alert('Hello%20World');"),
                            icon = null,
                            dateAdded = Instant.parse("2023-06-26T16:32:47Z"),
                            lastModified = null
                        )
                    ),
                    dateAdded = Instant.parse("2023-06-26T16:30:23Z"),
                    lastModified = Instant.parse("2023-06-26T16:32:58Z"),
                ),
                BookmarkTreeNode.Bookmark(
                    title = "Bookmark B",
                    url = Uri("edge://favorites/?id=79"),
                    icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAQAAAC1+jfqAAAAj0lEQVQoz5VQQRGAMAyrhElAAhJwAA7AARJAwhwgAQmTMAmVMAkh9OCAg3HQftYlTduIXAIKlXygQ2DWeYKiYoZ8v8kjobqDBRpEtPYe4c9QTyBRPO7fcKz30FWwgXvfXFGI/KKwPkZ8VZlf7DFCON9+G2HmuO3E9qm/RDJwINE/Eeg/DUuYMqtSQa9bHLEAbJiCvHo3gh4AAAAASUVORK5CYII"),
                    dateAdded = Instant.parse("2023-06-26T16:32:24Z"),
                    lastModified = null
                )
            ),
            dateAdded = Instant.parse("2023-06-26T16:30:15Z"),
            lastModified = Instant.parse("2023-06-26T16:32:47Z"),
        ),
    ),
    dateAdded = Instant.parse("2023-06-01T00:54:04Z"),
    lastModified = Instant.parse("2023-06-26T16:30:23Z"),
    personalToolbarFolder = true,
)

private val NETSCAPE_BOOKMARKS by lazy { NETSCAPE_BOOKMARKS_BASE64.decodeBase64String() }
private const val NETSCAPE_BOOKMARKS_BASE64 =
    "PCFET0NUWVBFIE5FVFNDQVBFLUJvb2ttYXJrLWZpbGUtMT4NCjwhLS0gVGhpcyBpcyBhbiBhdXRvbWF0aWNhbGx5IGdlbmVyYXRlZCBmaWxlLg0KICAgICBJdCB3aWxsIGJlIHJlYWQgYW5kIG92ZXJ3cml0dGVuLg0KICAgICBETyBOT1QgRURJVCEgLS0+DQo8TUVUQSBIVFRQLUVRVUlWPSJDb250ZW50LVR5cGUiIENPTlRFTlQ9InRleHQvaHRtbDsgY2hhcnNldD1VVEYtOCI+DQo8VElUTEU+Qm9va21hcmtzPC9USVRMRT4NCjxIMT5Cb29rbWFya3M8L0gxPg0KPERMPjxwPg0KICAgIDxEVD48SDMgQUREX0RBVEU9IjE2ODU1ODA4NDQiIExBU1RfTU9ESUZJRUQ9IjE2ODc3OTcwMjMiIFBFUlNPTkFMX1RPT0xCQVJfRk9MREVSPSJ0cnVlIj5GYXZvcml0ZXMgQmFyPC9IMz4NCiAgICA8REw+PHA+DQogICAgICAgIDxEVD48QSBIUkVGPSJodHRwczovL29rbGNoLmNvbS8jNjkuODUsMC4xMzMsMjMyLjM3LDEwMCIgQUREX0RBVEU9IjE2NzYwMjc0NjYiIElDT049ImRhdGE6aW1hZ2UvcG5nO2Jhc2U2NCxpVkJPUncwS0dnb0FBQUFOU1VoRVVnQUFBQkFBQUFBUUNBWUFBQUFmOC85aEFBQURTRWxFUVZRNGpWMlRTMmljWlJoR24vZjl2djh5azVsa1pvd1ZDd2xPS25FaDJvSVNhK3FWZ25ac1RKdlNDbElYaXRDQTRBV3E0a2JJeGxWQklrS3hSdENOU0pNWVJSY2xVWE1oYWJBaWFWRUpWQ0tWVmlxdG1jeDkvdnYzdXJGU1BhdXpPTnREK0I5WGp4OS9pRGg4d2FBMWFNZ3JnandRK1pjVUI2dEM3WSszdjNOdTVlYWVic2pra1NQMm5wNWQ3ekw1eHh3N3NvS2tpU2lxZ2NpSGRtSzRWZ2cvYmtYQ3djbVZpN1UzbjVsYUR3R0F4akRHUjE4dVdLN1NYeFZjZnFJU2xJMndiMHg3azd1ZUxwSHFkRkdaK2tESUVjTXE0RndxNUdyUW1tdlgveHIrOVAyTlNDMWlVVjdaODl4NElaVjl0dUsxUXlLd2FmbXFzN1FYdWFIOVpQZnVJSlhLb2Yzem1pS3R4WXNsS3FSVmZ3UXJlMkRsanpQMDArdGZQMmpCTEZ2R2d4YVBwRm5sWEdtWDVFWWVvL28zaTBocVc4Z2ZQb1RtMHF4c3pVeVFTb2xoRG9Tb0xZa0VqMmdmcVZIYnNaUWZTcXdhSVc4ckRVcHVaSUFhQ3oraSt1VVN4RFFBclpFL09Fd0FwREl6UWVJNkp1T3lib1VZMVpISzdHNmJXSXpIdkgzZlRpb2N1QmUxaFY5UVBiMEtsZW9FMkVGOWRoa0FrRDg0VENLQzZzeEhITEF0UVBvQkhhclVIVW5Rb3VKVGQrUDJVaCt1Zi9zcnlsK2NoNXZxQklzUFNnZ3EzWTM2N0Fva2psRTRmQWhzMjFTWm1TQm82dFVSdXpDY0lDWWJBQ0JnaEp3R2N3d3hCQzBBQ3dGSWc2RC9hV3pBcEVHSW9DTnlmdGV1MWI4eGQxMU1iS2h2NkU2UXBYQjFlZzFpdXdBSTNBalJWWG9jK1pGSDBWaFlSWFY2U3V5T0RLTEV2NkpEY3I1M2JlY3VrU2k1TkY4bUFsQWNLaElBL1BuNUJTUlJoTnRLdTVFZkdVQnpZVTJxVTkrQlVubmo2RVJIQWM3cVNEcE9OWlA0cU1VZHBESXNHL09iTENEcEd5cVNpUklrdFJhNlIzYWl0ckF1bGRPclpMbGRoaEdRSDNveElUdEJBREE1VmhzdnBMS3YrczByb1lWUVU3UEJPL1oyUzkvK0hnS0F6YVhmNU5ya09VbzdpV0h4NDIwdTIyVi84NzJlOGJkZVUyTVk0K1NlSitlMTFScklkblQzaDVFdmJGTlN1VmdqUlFiQnRUb3VUNitMY3EyRWlDanZablhOYjgwRkNiMTR5dzluNU4rWlRoMFQ2OVllN3dTYjRLV01ob1d3Q3ZJYjBNWkRXaWZJS0VFU1ZDUGJlQ2ZyamZJYjkzODRHdjNueGh0ODl2Yld3MmxGejdOcERsb1M5RnJHaDIyOHkya0pWNUUwUHJudnhMN2xtL3UvQWQ1RWt3TmpQL0dlQUFBQUFFbEZUa1N1UW1DQyI+T0tMQ0g8L0E+DQogICAgICAgIDxEVD48SDMgQUREX0RBVEU9IjE2ODc3OTcwMDQiIExBU1RfTU9ESUZJRUQ9IjE2ODc3OTcxNDQiPkZvbGRlciBBPC9IMz4NCiAgICAgICAgPERMPjxwPg0KICAgICAgICAgICAgPERUPjxBIEhSRUY9Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC8jd2lkZ2V0cy9ib29rbWFya3Mtd2lkZ2V0IiBBRERfREFURT0iMTY4Nzc5NzAzNCIgSUNPTj0iZGF0YTppbWFnZS9wbmc7YmFzZTY0LGlWQk9SdzBLR2dvQUFBQU5TVWhFVWdBQUFCQUFBQUFRQ0FZQUFBQWY4LzloQUFBQ2cwbEVRVlE0alkyVFFZaFZkUlRHZitmODczdjMzWm5KR2hpRnlxWmhoQkVqRUFrcFVFSVNwaWhkV20xYXVCSGNESWhFZ290dTVTWlhiZHVvMEtwUlVGQzM0dXlraURaRkJHMGl0Ulk1ODhiSHZQdnVmZi8vT1MzbXZTa1pGNTNsT1p6dmZPYzc1eE5Hc2N5Sk5zRDdYRzE0U2l4eklzRHU5bmNmSGRBYkIyZmliMHZ2MWdBQ2NIZjNtWTg3b1hYU2NlbzB2SExrL2xjWEhVVEFBWllYenIrNDU5SHF0YXZIMzl4MWMvSDFsdVpGMWNUaE43OStNSHRCQVRLUlZ5WTEzemVwK1Q0bHZBcndHYVdVc0ZrUHJlbU04QWFkWW41dCt0bVhkT3E1QllRNWdBekFrRUZ0MFJ3SGZMQ05mK3luS0RZb0hqOXVaekUycHNPMklBOWdOTUhGRkVIQnhRVjFTdDJzSFZHbjFBa1hkVlV0cWtaRE1uVjNOZlAxTFFhSzR1NGdJcmdub1RUQUFFcnVja3ZPVlFZeVVkV2VKVlBYNk9xc2JnRUFxQWlWRFUxVTNsNlpQZnUxZ0xxN0NEU3BpYk91bWhYVkFFMUpMU1RNVTNjYmdHRXBrL0R5cE9hbnhua0JhaEpSR2k4R05Wa3lTYkhCWU8wSkFIY0hSQkkyWEU5Vjc3OGFHdDV1aVV3Vmc4WUFOWGNMK05xV2lJd1c3a2dXek95NjlzUGNYNzNlZk4zcnpXcy96Q1dYOTF3bFRmVXJlV1pqZ0luMDQ4QzJNeEFSY2FkNytPK0xUekJZMmJuMFo1eG94UjBiZGY3NWw1ZmpqM3Zuam41eTYvUWZEcUxqZGhFQmR4ZVJzS2wrcWVOSHFvcWkzWTVKN2o4LzQ5L3YzMnNyaDE5YjNQUHQ3NGNFUEJ1MU80NkI0RDQrWCtsakJ2MGRMZHYxY04zdUhWandTeCsrd3d0RC9TTHJQYm9ESE5WTklieVRhNmE1WnFwSVp3UWc1Y2dycEN3RXRCT3lYUE1ZMjVvaU12cVRzUVkvYjFqOUM3Z2I2U2VBVHlsOWJLYVlobXRKL0Y3UjdjNU1yM1p6YTNYNlNmU0g4Wm1CLzJmbm5VeTJiaSsrRlc0Yys5Zk8vd0M0RHpCU0c4a2lld0FBQUFCSlJVNUVya0pnZ2c9PSI+Qm9va21hcmsgQTwvQT4NCiAgICAgICAgPC9ETD48cD4NCiAgICAgICAgPERUPjxIMyBBRERfREFURT0iMTY4Nzc5NzAxNSIgTEFTVF9NT0RJRklFRD0iMTY4Nzc5NzE2NyI+Rm9sZGVyIEI8L0gzPg0KICAgICAgICA8REw+PHA+DQogICAgICAgICAgICA8RFQ+PEgzIEFERF9EQVRFPSIxNjg3Nzk3MDIzIiBMQVNUX01PRElGSUVEPSIxNjg3Nzk3MTc4Ij5Gb2xkZXIgQzwvSDM+DQogICAgICAgICAgICA8REw+PHA+DQogICAgICAgICAgICAgICAgPERUPjxBIEhSRUY9ImphdmFzY3JpcHQ6d2luZG93LmFsZXJ0KCdIZWxsbyUyMFdvcmxkJyk7IiBBRERfREFURT0iMTY4Nzc5NzE2NyI+Qm9va21hcmsgQzwvQT4NCiAgICAgICAgICAgIDwvREw+PHA+DQogICAgICAgICAgICA8RFQ+PEEgSFJFRj0iZWRnZTovL2Zhdm9yaXRlcy8/aWQ9NzkiIEFERF9EQVRFPSIxNjg3Nzk3MTQ0IiBJQ09OPSJkYXRhOmltYWdlL3BuZztiYXNlNjQsaVZCT1J3MEtHZ29BQUFBTlNVaEVVZ0FBQUJBQUFBQVFDQVFBQUFDMStqZnFBQUFBajBsRVFWUW96NVZRUVJHQU1BeXJoRWxBQWhKd0FBN0FBUkpBd2h3Z0FRbVRNQW1WTUFraDlPQ0FnM0hRZnRZbFRkdUlYQUlLbFh5Z1EyRFdlWUtpWW9aOHY4a2pvYnFEQlJwRXRQWWU0YzlRVHlCUlBPN2ZjS3ozMEZXd2dYdmZYRkdJL0tLd1BrWjhWWmxmN0RGQ09OOStHMkhtdU8zRTlxbS9SREp3SU5FL0VlZy9EVXVZTXF0U1FhOWJITEVBYkppQ3ZIbzNnaDRBQUFBQVNVVk9SSzVDWUlJPSI+Qm9va21hcmsgQjwvQT4NCiAgICAgICAgPC9ETD48cD4NCiAgICA8L0RMPjxwPg0KPC9ETD48cD4NCg=="
