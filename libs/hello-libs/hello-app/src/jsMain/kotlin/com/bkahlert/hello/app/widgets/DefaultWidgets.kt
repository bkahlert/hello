package com.bkahlert.hello.app.widgets

import com.bkahlert.hello.bookmark.BookmarkTreeNode
import com.bkahlert.hello.bookmark.BookmarksWidget
import com.bkahlert.hello.quicklink.QuickLinks
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.Widget
import com.bkahlert.hello.widget.preview.FeaturePreview
import com.bkahlert.hello.widget.preview.FeaturePreviewWidget
import com.bkahlert.hello.widget.website.WebsiteWidget
import com.bkahlert.kommons.uri.Uri

public val DefaultWidgets: List<Widget> by lazy {
    listOf(
        BookmarksWidget(
            id = "bookmarks",
            bookmarks = listOf(
                BookmarkTreeNode.Folder(
                    title = "Nerd",
                    children = listOf(
                        BookmarkTreeNode.Bookmark(
                            title = "Impossible color",
                            url = Uri("https://en.wikipedia.org/wiki/Impossible_color"),
                            icon = Uri("https://en.wikipedia.org/favicon.ico"),
                        ),
                        BookmarkTreeNode.Folder(
                            title = "Unicode Fonts",
                            children = listOf(
                                BookmarkTreeNode.Bookmark(
                                    title = "𝕿𝖊𝖝𝖙 🎀ԲΔ₪ς¥🎀",
                                    url = Uri("https://textfancy.com/font-converter/"),
                                    icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAIAAACQkWg2AAAByUlEQVQ4jYVSPWtUURA95767b/cum0185AfYWSTIqsv+hKRKFbQRAmnszQ+xsPYTWzUqGBtJIwTcDXGR/AaLRIJEN+/du/fOWLwYtwk5MDMMw5kvDldX7xlj8B+alB3KC/7qUJKSnCmmZMuy9D7MEqIyUIQnSlElLggk57p2MLiztHRDRKzNSKMqMUmT2jKBhgZgSqpKUkPAh23s7HzWqyD/fFxfs7u7X46Ofk4mZ4PBrV5veTw+HB58X4Cs95fzmz14r+/eaPA0Rn2A93Y4PNjbGx0fn2xtPej3e/v73x49fnodce3hZvN2XyYTeflc/5wyyyCCua51zjUaDVU45wA45xYXiwKJzgHguQEkF65BxKpqSimlpKoAzlMkqAJAo8GNTYaAlPD+rcZocRnq/zeb5v4GAQXk00etyssJ9QTvZfs16gnB02RXEaZTffZEf5/SWna7NOaqlUgWBfIGswwpqaqtDwUgIjFGEQGggIpojBojYkRKF+IwdRAR51rWWuecqFKEztFaFgXIumMNW/dut91oNJ5O43h86PJcaHX4VX0pVcA0cEbOXFm5q6rGmLKsqqpqtfK86dpGX1U/Ov4swZj5ecxI/C83iCjVBVk5ngAAAABJRU5ErkJggg"),
                                ),
                                BookmarkTreeNode.Bookmark(
                                    title = "𝚄𝚗𝚒𝚌𝚘𝚍𝚎 𝕿𝖊𝖝𝖙 𝓒𝓸𝓷𝓿𝓮𝓻𝓽𝓮𝓻",
                                    url = Uri("http://qaz.wtf/u/convert.cgi?text=CUSTOM+FONTS"),
                                    icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAIAAACQkWg2AAACAklEQVQ4jU1SsW7qQBBcr31nchDFliGSU7mloEP8QKoUaSnzO5HS5RdokCx6KupIVmq7deNQuDECLvGdvbxiBXpTjfZmZld767y/vwPA8/OzUgoRjTEAAABN08RxzLzve2NMURRlWSIAvL6+RlEEAMYYz/OGw+F2u/38/MQrfN+Pomg2myVJ4r28vNzd3XVd13WdlNL3/SzLsiwDgMFg0Pf95XLhrNFoNJ1OcTgcEpG11vM8AJBSlmW5WCzgCq4z7u/vERGJCBGFEEIIY8zb21uSJACAiDeP67p93yMiuq4LAK7r3iaG/8B1Jq7rEhFaa9nKVcdxAKAsSzb4vs8EEfu+JyKvbdvbVJfLxXEcay2PJISw1rKaibXWyfOciPiN88QV1lohRNu2vCUiatsWiQgAfn9/ufX39/d6vf77+8vzfLVa7XY7AOi6johYifwJrK6qKk3TJEnSNFVKAUCapsYYFnBDlFLyOWit9/v9crmcz+dZloVhmGXZx8fH8Xg8n8/n81lrLaX0fN8fjUan04mblGXJK9psNsvlsqqqIAg4USklpUQAeHh4kFJqreM4TpKE74LNX19fxhitdRiGj4+PdV07eZ6Px2NrbV3XTdNwEhu01kyCIHh6evr5+SmKwjudTkQUhuFkMgmCoG3bw+HAUqVUGIZKKWttnuf7/R4A/gEOHSR5tB5pRwAAAABJRU5ErkJggg"),
                                ),
                                BookmarkTreeNode.Bookmark(
                                    title = "sᴍᴀʟʟ ᴛᴇxᴛ ᵍᵉⁿᵉʳᵃᵗᵒʳ",
                                    url = Uri("https://smalltext.io/"),
                                    icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABDklEQVQ4jZWTMW7CQBBFn+1IUVxEwuQQuUCoETQIDheuQMMNKClS5wChpEOWkBAURJECppg/eGUgS760mt31/J2/s9/wP2SKA+ALeLuH1FZMFbtACVTAMkbOlfSudQ/YiLwGhreIiWIREKbASvONlERRSO5BRJfd1/csbRASjTSYO76BI/ABzJVziCloAduguo8x9iJJWOEJa1oVVK6AGfAK/Gj/AdhhT7j2xBfgE3gGfkV8BBbAqHFgAux1pfO9M8lqSt3qGlF4MydYY/aKJWakW40Fanv2sSfy6n5AcY+CLrVhVphp3DB+wEVlxxCzpRN62h9LUR6r7rJLant6T9pXGQ10sF9zoHX2R+4FTmbwSY31GU41AAAAAElFTkSuQmCC"),
                                ),
                                BookmarkTreeNode.Bookmark(
                                    title = "U+0085 NEXT LINE (NEL)* – Codepoints",
                                    url = Uri("https://codepoints.net/U+0085?lang=de"),
                                    icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABTElEQVQ4jZWTMY7CMBBF/8RBFlBwCSzRcQQKDsFBaEFQbMu2NJyAGlGk4g5puUeEHM/fYicIwqKwlixbo/nP3+Mx8PfIbP0GsGvFOkeTuAZAm+tPIU3CRkQIoLbJLMs2XZC72E6N3nsdDocJQLTYW8iTeDQaxdPplKqqYoyRl8slhRDiOydt2zGEkMqy5HK51P1+ryR5PB4TgGg5L5BVYxtAyvOczjkFwOl0SlXVoigI4PE6q0a8s0ANQG2vADiZTFiWJWOMnM1mCoAiok1hTfsKEBHt9Xq8Xq8kycViQQBq9p8ADkBh1ua2AkDmnKP3Xs7nMw6HA0VESKoBcvz2xiq3QnwBcAC2RoeIZCEEppTgvZfb7fYo3prm9SWaYg4Gg1TXNauqYr/f/38viEgMIeh4PE4i0il+5+Teyp+I25C1iNAq//FnakM6v/MP/HezXmOLrOgAAAAASUVORK5CYII"),
                                ),
                                BookmarkTreeNode.Bookmark(
                                    title = "“ㅤ” non-whitespace whitespace",
                                    url = Uri("https://www.compart.com/en/unicode/U+3164"),
                                    icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABu0lEQVQ4jYWTPW8TQRCGn3duz+DIxHGCEAVIlIiKioaCJi0SFQVSKgp+Bunhv0CRgr9AkxaJ1MgSH3aCA3G42xmKuwtnvjzSSKud3Wfed7QrgOn18l5KxX4VEQBIAsMi/KriRBABUrgDGJYqX74YTDlMAC5qhU4jCAAhJSLPiM03dXqCgIC2TCnxsR6+grND8f/YSVvjI4lhC0+tQKvq/IjF4iB1JwOsW4qm1S3I30wTSUQEjcPIEkWdMvOO2MgmgBiNRne2B+kxEMfumPs+BjggPQXdaGQXog+gUZDN7K5Jz1vq8Wz+ddIdmGxt7prZzb7HPiAA3H3prgoggi/AFeAMMKFBRIQk/Q0ggKIoTFLZq9dtGmLlcid7RUHOOfrBmrB/7HsvV3r8Du0DOgtJUiGpoPFfd9Bw/WGhPwMHkM7fer70TJAj2NiejF+3jURwOyJc+tV43Uuc7EzGs4uHBBCRZSq8qh7OF98P0hrA8BxOiBi23g2EApzCLix8uJbul2X5sgocApoB+GmwcUQ9pv1N3fgGEtMqX95btAAz/RB8Es13FVIGjYzPD/B3jQFJkR2glFkxWL7fA34CDMnOyaJ5d8UAAAAASUVORK5CYII"),
                                ),
                                BookmarkTreeNode.Bookmark(
                                    title = "Wisdom/Awesome-Unicode: A curated list of delightful Unicode tidbits, packages and resources.",
                                    url = Uri("https://github.com/Wisdom/Awesome-Unicode"),
                                    icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABdklEQVQ4jY3TvWpUURQF4HUmk2QUCWJhoVUQBLHwByWx0aewsBV8mChBLRIQtNBaFBFsbIIvYCdEUJNCsRSMI5KJn4X74mUw4obD3eesvfb/TZKgpQSX8ADvMcb30u9jKfsJZrFahE5+1ulkjBUM+sQB5vHc/8vjCjjonNwp4CWuYQM72MKH0l8V9qJsVzryMn5ggrV6G+I4RnWOYa6w22X7DeeGSW4kmauKJphJstda+9hr0Se0wiZJZpIcTHI9eFuNGuNkry/9ybTujkV8Lc7rVH2wjdl9x/TH2Qw2y8GXQZKWRJJDSQ70I/+F3JKMkiwUbzhIsl34kSRLrTXVxOkShq01SZaTHK2gW8G9Smenyjj1jwxO4w32quz14Hxd7uJJ6U9xZor4sNev3Rr92c5gvYALuFULtdhzcKK3heP6rnZgt8obeIeruDiV+gife06eFWcwbfSoZ3S4hy1Un3axprZyejydfhk3Meq9zfv9F16Z5vwCz01H2UDrUX0AAAAASUVORK5CYII"),
                                ),
                            ),
                        ),
                        BookmarkTreeNode.Folder(
                            title = "GO!✺◟( ⑅❛ั ᴗ ❛ั ⑅)◞✺KAOMOJI",
                            children = listOf(
                                BookmarkTreeNode.Bookmark(
                                    title = "Kaomoji o((*^▽^*))o",
                                    url = Uri("https://cutekaomoji.com/happy/#thumbsupkaomoji"),
                                    icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAA+klEQVQ4jZ2TPU7DQBCFP1aLKbwXoOMKnMDn8AmocoBUBLngErlASmhToEgUVK7TQZUaESGIgvQo/MOyPxHkSdbY2u+NZ2d2IZAkp7xcyJvAXAPbEPK07ZlYkpoDfw7VhOb6H+ZBXSWPy9UR3lHuRJJmD3C7+horerqyXJ7/3mKOMQA3y3emlWU3swDcreMe5RjrA1ACUBbpEaQYA9BOSg+AUxObc4wZymknJUVxxrSyVBdxgixz5Ah/RinJafHSffaxuN4no8/00dG/1OOCpHbTmYan3STNtRbP9+MeFRzl3V56/ZDePpOlN3GX/t6P9GXykswPmOch/w2YWxisxGlaIgAAAABJRU5ErkJggg"),
                                ),
                                BookmarkTreeNode.Bookmark(
                                    title = "Text Faces Gallery (つ◉益◉)つ ᶘಠᴥಠᶅ",
                                    url = Uri("https://textfancy.com/text-faces/"),
                                    icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABLklEQVQ4jX3SMUoDYRAF4G83GxQxeA3BXtHGY1joHQQbsUpArDyCeAFLC0tBtLK38QhaiBCMxiRrsbPxz5o4MLzdnXmPmbcDJYaBXVUU/kYrsJtyCnxHwriBaWRJbcop0I6ETuAK8ngu0Q+se6acAr1QbeEhmg5xnJDXA+EGHwlnbvSCXAt0FjUWZg3LVeZMEoFRMn7dk6cCo4YglWFZELPYt65NUs6835VGFoS3/1ZIo0ywVBm1jDMMYvS7yMzsalPBHKeJQNnI86S3NW+CCV5U7g+jsT6cNl79mjhuCoyxiyesxfhLISqIg3ifYE9SgJ2Y4jPWWG3uGLGN66YHmeqEL5J9n3GCTWxgH1exTon3eeo5Ls0aN8ZX49sjthZMCA5wr7r7+jL7uMVRTOsHthJkjJJl7rYAAAAASUVORK5CYII"),
                                ),
                                BookmarkTreeNode.Bookmark(
                                    title = "( ͡° ͜ʖ ͡°) 【EMOTICON】 ٩( θ‿θ )۶",
                                    url = Uri("https://www.fastemoji.com/"),
                                    icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACCklEQVQ4jW2TwUtUYRTFf/e9GadSQ2FsWRASgWaCgSKSNmRtok1BRP4F/QFBi8BWLVslFQgRtGnRqkUoFEFEkhETFNbCMIsojDR1fOP35jst5s0wb/TAe9x3ud9559zvXqhBMpogYQBaYVArDDbmagjqkZmaCSApbqGXFnpTuRSBZD3F0oXuN3/2N4tI3mVEOZVLEZgpRGf3ujBsIggSyXkgn8RBmkAKet5F3WHrngIH2oZ4rJBJ1YrMDJGhQIaCGWq2QF+xdLv/o1seWJL6P7nNEwtb04XFxUOarP5JJYZVJlKZSCWGq44JkyfgyPy//PEFd2rga/yz73PlKgPKg7IAcowqZlkbeG3gFbMsx+jOLgPHPmxPd2ffX3vSNjhKO0fJcRIxToUATyUxHBLiMWZxvCDge+0WApl/SzYOaUMEOIQDfMpxNfZAjMdTwdOM86h9ffL3RZDJMaJtvmkTr028tlmSY2THoUasTj3q3JiaeVj7VomhhibWprHexEy98N58Fl/udMalIJs7Hd15eSZu+VG0fZfnVLJnCKyVOYmMGXHtXJ2ATNRVdtwwuBKa2pUJpuW6HoyNcRPHc3x9qHYbeRAywKK7r8bd/ddfVm89PVxfpjUmtMZEIn/3ZTJMgH79XZ2PytFsx/Vzi/W+GzmMXOoudlhIcDDoiNdta6aqKNlQTzEltgH/AY26/JBA2OfsAAAAAElFTkSuQmCC"),
                                ),
                                BookmarkTreeNode.Bookmark(
                                    title = "10,000+ Japanese Emoticons, Kaomoji, Text Faces & Dongers",
                                    url = Uri("http://japaneseemoticons.me/all-japanese-emoticons/"),
                                    icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAIAAACQkWg2AAACPUlEQVQ4jT2SO2tUURzEZ845d/fe3dWI+ECToE0kogaDEKKFCGKRoBaKsbKw0g8gdikFS0EULPQjaHwUURTEQhsRUkRFIhaym0ASs3nv457/WGx0qulmhvlxYfQCTCIogRAIiSQAARBIdERAQIC0ZUlBNFNIZDnoSAdAECSY4AAhQAAFUq2Weac0c0tLzDI1NpVHee/abZSKSDPIJAZ10iTrqmj0Yjh5yp48tl8/efZcGDim+rIqFXszielpFFNSjhAAazbCtevFsausVMLtW9jXzdlZyzKMjsT1dcUcdJ12ToAA73zr9aTVarFaU21O8wv68ln1ums1PYDFOhxIiXAAScRoyZUxt3OH81S7gWOHceOm6+ltPnig/v5kfFyiBBIOMJBotxxc6+Ure/tOHz9pcjIMDlpjMxk8gepv7NmN7m602gJDZzFCEKMfGma7FZ899V07XUjaMzMklJVcFNOUZgIcSVhUqex6evMP721t2R3stXrdvPPHB/P5RRYzK2daW5X3BJxINJq+r0+VbZia4tfpWJ1jltrEBLPM9x8KA0fii+esVlEoSOYAySdaXc4fPcTiInoOJJcuo1iwmR9u1y78qdv6BlZWZEaQYqAJhQTVmr5/49AwcmvfvcPlFe7dn9+/Fycm7OiAP3OaaSoZCc6PnKckEs6p0dDGBstlForYWIcZtm9Hs2HNpiuXIZAM/+FUjCwUXJpCkkWUSiCR5wwFnxShCAJAwD9+tyg2Q+dLCZ2rIMkICiDwF9z2NDJH1ChyAAAAAElFTkSuQmCC"),
                                ),
                                BookmarkTreeNode.Bookmark(
                                    title = "1 Line Art | ASCII art in one line",
                                    url = Uri("https://1lineart.kulaone.com/#/"),
                                    icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAA1ElEQVQ4jd1SURWDMAw8JNRCLAQJRUItYAEkgIRawEIsBAtISCVkHwPetrLte7v38l7aJNdrr8DPo3lcqKqXUgAApRRs21YNMDO6rmuqAgCYmTOzA7iMvu/9qyRVvRwOIbiZfSeIMToAJyI/8iNSSp8JlmU5m3POLiKVkn2vhpk5ETkAjzG6mbmIVCqI6Poq0zS9fbzX2HsB7DaqqrdtC+BuU0rpzAFgXVeM4/h0oIjc7VRVDyGc7MzsuxM4yN9ZOwyDNyGE8/M8IucMZsY8z7iq/xFunM2cvSBTdZEAAAAASUVORK5CYII"),
                                ),
                                BookmarkTreeNode.Bookmark(
                                    title = "Kaomoji by ACTIVITIES",
                                    url = Uri("http://en.emoticonfun.com/writing/"),
                                    icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACWElEQVQ4ja2TTUhUcRTFf//Xc8avhdBMWRATRO6EihYtsgFrEVb2sXAXBIrRYmwRWJAuwkZyF6MhRGgFFShtUlvIEFQgJQ4KjloKlWI1oc/RGX3zns7ztpgaikIXdXYH7rkczuHAP0JtdhCq8smvvORCO8crKrK6DR+EqnwSqK7MkE+vQSlawwsUHLtOdW2tAtA2FNdUgmVhTw1gJU3sZZNAeQEr4SD9/WEB0P9ms65rOuNsfR0AsZfRdB1NwfqqEDiaR+u9agD0UJVPLp6vwLVmQGwMlAb4pHT/QVCCNfUSAJeusNKQNNewlyxEFA8ePhJl91wSl0qTmnwFgKY03K51+t7pnDjpxxoN82atlMf9Eb4lHQqKtvEkHMm61YCsOC9Px52rAYoDxWkqr3Rz+vlOpnec4u6LjzwbmiWRs52Bt0MkEgk6OjrQXUU5JFQ+E1+WmDIcZuIOn5fSiKZjFu3hdttdIoMDKJWJxXEczOQiqVQqk8HT3hHae2aJTC8RX7ZRej7lZYe41tjEh/dj7N5VjGPv+61zXdfZov0o8NyZs/ITXq9XADlyuEyi0ah0d3XJzPSMiIgAAsjI8LAMDg7K1fp6AdD8fn82EGN+HoBgcxCXy0V0NIphGACYKytMTU6STCbxejzsLSnJhDg3N0c6nQYgZVlMjI8DkJubS3wxTl9vD47j8DUWIxaL4fF4uN/ZSU1NjQLQbzYHldfrlcDlOhYMA8Mw2OrxcKetjVBrqwIQEVldXcVxHNxuNzeamv7cQmNDg5imiW3bFBYWcqulZdOh/Rd8Bx96F61+G3M7AAAAAElFTkSuQmCC"),
                                ),
                            ),
                        ),
                    ),
                ),
                BookmarkTreeNode.Folder(
                    title = "Scrum",
                    children = listOf(
                        BookmarkTreeNode.Bookmark(
                            title = "Nine Questions You Should Ask",
                            url = Uri("https://www.mountaingoatsoftware.com/blog/nine-questions-scrum-masters-and-product-owners-should-be-asking"),
                            icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABQ0lEQVQ4ja1RLVPDQBB9uzEokgOd5Aj8gFbWUYVtHZJKZB2WXwD8gqJRtagWBwqFY9ptim/qML1FpAllaJvh483czN67fW/e7QJ/BFU1WGsDdjoAUMsZvR6labd496oM9naDC0DfHXMbDjUiOjW+H8zm8/tvCay1NXZolfEUsvDQF5HMWht4C7SUtAcgc0x1ERFKoqjnmC9Z9Qr6KV5BRkqdhQdhp88FqYrheDppUhLFWvWNwghAsEqQUvsnBmtNGaBudV8JWZ4S3myePRrfEIDjbUJS6oymk3PfmAdS3RmlaX3fmKdyC0kUjwFYACDQncI1AAoBgJmaryLDdc5cFkydola4FwW9IXfrbxJ/Mcib9HaZICSgASBztH1GvHpxzF0AmUJPcoZuRETW6DbjMLRnSRTrcia/w0EYD46s3baV/8MHrFt2XvzfY6wAAAAASUVORK5CYII"),
                        ),
                        BookmarkTreeNode.Bookmark(
                            title = "Scrum Primer",
                            url = Uri("https://scrumprimer.org/primers/de_scrumprimer20.pdf"),
                        ),
                        BookmarkTreeNode.Bookmark(
                            title = "Scrum Foundations eLearning Series Educational Videos",
                            url = Uri("https://www.scrumalliance.org/learn-about-scrum/scrum-elearning-series"),
                            icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABiUlEQVQ4jZ2SzStEURjGn/c9c2fGHUQmH83cGSRlY6esLNj4G7CyUsqCLGRzN2jm+igLKfIPWCjKf2DBwsZGUYikyAJ3LvNxXiu6U/c28qzOc973fc7vnA7BJ8klG4oc7xLicYG0liucr/furshGGSGK+I1H5mgF+hnytQaKJSMkswUzewrc7YYF/Oo919HnOdmxKiIb7ObTR5/r6Z6wOf5ZGBztLpYrZ/4i2dAgtf36ph9rEshmT8xdtQZqNoYRYOa6GIF6d5eyHf6GJ6ctIRudTWEB5Dc3dmc8mfAaozDaS6BFBk4BDIigW2uaVEr3mvMPB8EEALrs288YRbMlcL8SqieBqSArzDSnGMMCnqp5J8+xjguOVb5fT9cF1V0nc+h/qyqCt+VUSwXYB/OQNfvgBQWI6K3ih7oKDDAMzjFoT0SXwgiVEbmMJ/T+j6/6iQLZAejFbFEXYQHx1O194cZaCKvX1EcuM+LmrelAgr/o2eOT5pg+/9fphbXUoOdkJv417OatpaD9b+xZhEdtKGPJAAAAAElFTkSuQmCC"),
                        ),
                        BookmarkTreeNode.Bookmark(
                            title = "Crossfunktionale Teams - Was sind die Vorteile?",
                            url = Uri("https://agile.coach/wissen/crossfunktional-vs-komponenten-team/"),
                            icon = Uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAIAAACQkWg2AAACh0lEQVQ4jW2ST0iTcRjHn+d5/2zz37ZyKk5BxUMYSIah8xIV2GVQNw2WWFA6AgM75CokOnYLiYg0awvt2jQKE9MgBU9jLIrcrPVGOMOYuU3n3vf3dHjFS34PD8/heZ7vw/N5kJlDoeDs6zADdHrP+XwXhRCIiIhwkORIJPJh/v3Ra/cVFPNj91pajjc1NQkhJEk6sIESibjLVblS1vDd2VhVVaVpGiLyfwIAM5FPnT4z8/aNe+4hstBS621tbUIIIjIjIgohAGB/SWTmrUxm9PEjJLrS5y8uKjIriAgAdnZ2rFarOX6vQdf1Z+Nj2mocAGsbGnsvXUZEIlpYWJicnCCikpLigYHrNTW1sVhsaiosqYr07eevhvNX7UdaIrPTiZWvHk/H8vLyyMiD/n5/V1f35ubf0dEnnZ1nNU0Lh1/R51i0o+/O3K5rvlB50j/85VM0t709PT3V09Pb3u6x2+0+n6+62r24+NFut1ssFrnsULnVYumwZ5FQVmSSlHQ6nctlnU6HEELXdSLq7r5QWlqaTqcBWDZ0/dhhtdFhlSUsJp4BQADzOESkqioANDc3A8DS0hIRyQzotmFiNVEoFP7o+lY2S0TMYNJIJpOFQkGSpLq6OiJiZpkId/P5lxMvNjY2WEBqbU1RFGZhmoRCz7e2MvH4yuDgDYfDKYSQhRA2m214+C4AGIbh9/fn83nEPZOhoVuKooyPP11f/11e7mJmMtkJIZhZCINZqKpqtVpzuaxJGgBSqbWyslJmRiQyDGP/sZhB13Vm9nq9wWAwkUgw8+zsu2g02tp6IpPJGIYhV1RU7DFHRES3uyafz7e3e5LJH4HATVmWFUUJBG47HA5VVerr6/8B4mBP/PzYzQoAAAAASUVORK5CYII"),
                        ),
                        BookmarkTreeNode.Bookmark(
                            title = "The Ultimate Guide to Agile Retrospectives",
                            url = Uri("https://www.retrium.com/ultimate-guide-to-agile-retrospectives/intro"),
                            icon = Uri(
                                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACyklEQVQ4jV2TT4iVZRTGf+d87/fde713pkYnBQtxZLo0KYUUo0a1aBFBgQxhYBvbJglJVIpUA0nYokVh5LrCxagRE1lYMauUQCfGNMeplYK7yebeuX++73vf97SwpqazOIvDeXh4+PHA/8ZmcDa3db/9sm3GLm7ZBGAXx87Zz9vesal7Git/hgB31spxduteEnkN4RFqCrf74xSuydrkc/oG3q7iw3vy2PzJfzQCYOeb9yLuC+rJOKWBEunaWchfRbJZRGsUwaNa+9v3AkU8KE/9+pPaJEpedPCsoWeRjnVBhei/4+rGnbQHByliSs3VSBTSBAK7KIrSDFmJYGdHd1KvnKc0j0qCxRvF7PDudL2fkQ2354kyjZnQSI0//RXZfe1rALFvx05S15R2fhxLX2DA7afljQ2ZxFu9A1pUh0nKKtHmqOmTLMcSlRrIXUTMkeheSKDMHiLY83i/Byi4VZzQvDjNloVFbo5uImZnGKo8jJUQgWBggtiZBwuiRepJhU54lzx8hcqIvHh9ahWhzx74kVQXyeMOErkbDxjiKAwMpRULPK/g0u0UUYEpANtDIqcI5P4YS7JERZ+g7o6S+xxEFNUUlRQkw+kQa9PnyGnYx/eP2eTGYTlFsE/G3iCkH5C57/Fc4A8/R81VQFJHK04BglkbI6efpwT7Bksm2Dy0zz4aPEyIgul6nKb04xGQI3TjPjoh/ovxw+YEVX2U5Sio9ICnGUgfp+Vb9OMOMvkSlVGcJnT8ATm8cPwORhDeH2mypjpPpuANFOgaOIFuOERkHY3kdZZDjmiCxYRg23l74bICEMTo2TlaEdoRFn0PL562XaOwy5i8zFL0oBUijig/UEYVsNVlOtqcwOQQouM4gWX/LMo6BtJP6QWAS4g/Jm/9fnp1mUAEDMBe2lxlJHsTk10yef0ZO3hfjcHGNBqmmf7thFyi/K/mL2uYSMlFPCPNAAAAAElFTkSuQmCC"
                            ),
                        ),
                    ),
                ),
                BookmarkTreeNode.Bookmark(
                    title = "Der Teufelskreis von Qualität und Zeitdruck",
                    url = Uri("http://www.inf.fu-berlin.de/inst/ag-se/teaching/V-SWT-2018/49_Projektmanagement4.pdf"),
                ),
            ) + QuickLinks.DefaultLinks,
        ),
    ) + FeaturePreview.values().map {
        FeaturePreviewWidget(
            id = "feature-preview-${it.name}",
            feature = it,
            aspectRatio = AspectRatio.stretch,
        )
    } + listOf(
        WebsiteWidget(
            id = "playground",
            title = QuickLinks.DefaultLinks.first().title,
            src = QuickLinks.DefaultLinks.first().url,
            aspectRatio = AspectRatio.stretch,
        ),
        WebsiteWidget(
            id = "v1-semantic-compose-alpha",
            title = QuickLinks.DefaultLinks.drop(1).first().title,
            src = QuickLinks.DefaultLinks.drop(1).first().url,
            aspectRatio = AspectRatio.stretch,
        ),
    )
}
