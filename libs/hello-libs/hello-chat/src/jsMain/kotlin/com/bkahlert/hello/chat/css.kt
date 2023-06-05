package com.bkahlert.hello.chat

import com.bkahlert.kommons.dom.head
import kotlinx.browser.document

public val ChatCss: Unit by lazy {
    document.head().appendChild(document.createElement("style").apply {
        setAttribute("type", "text/css")
        appendChild(document.createTextNode(CSS))
    })
    Unit
}

// language=css
private const val CSS = """
.chat {
  --pf: 262 80% 43%;
  --sf: 316 70% 43%;
  --af: 175 70% 34%;
  --in: 198 93% 60%;
  --su: 158 64% 52%;
  --wa: 43 96% 56%;
  --er: 0 91% 71%;
  --inc: 198 100% 12%;
  --suc: 158 100% 10%;
  --wac: 43 100% 11%;
  --erc: 0 100% 14%;
  --rounded-box: 1rem;
  --rounded-btn: .5rem;
  --rounded-badge: 1.9rem;
  --animation-btn: .25s;
  --animation-input: .2s;
  --btn-text-case: uppercase;
  --btn-focus-scale: .95;
  --border-btn: 1px;
  --tab-border: 1px;
  --tab-radius: .5rem;
  --p: 262 80% 50%;
  --pc: 0 0% 100%;
  --s: 316 70% 50%;
  --sc: 0 0% 100%;
  --a: 175 70% 41%;
  --ac: 0 0% 100%;
  --n: 213 18% 20%;
  --nf: 212 17% 17%;
  --nc: 220 13% 69%;
  --b1: 212 18% 14%;
  --b2: 213 18% 12%;
  --b3: 213 18% 10%;
  --bc: 220 13% 69%;

  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  column-gap: .75rem;
  padding-top: .25rem;
  padding-bottom: .25rem
}

.chat-image {
  grid-row: span 2/span 2;
  align-self: flex-end
}

.chat-header {
  grid-row-start: 1;
  font-size: .875rem;
  line-height: 1.25rem
}

.chat-footer {
  grid-row-start: 3;
  font-size: .875rem;
  line-height: 1.25rem
}

.chat-bubble {
  position: relative;
  display: block;
  width: -moz-fit-content;
  width: fit-content;
  padding: .5rem 1rem;
  max-width: 90%;
  border-radius: var(--rounded-box, 1rem);
  min-height: 2.75rem;
  min-width: 2.75rem;
  --tw-bg-opacity: 1;
  background-color: hsl(var(--n)/var(--tw-bg-opacity));
  --tw-text-opacity: 1;
  color: hsl(var(--nc)/var(--tw-text-opacity))
}

.chat-bubble:before {
  position: absolute;
  bottom: 0;
  height: .75rem;
  width: .75rem;
  background-color: inherit;
  content: "";
  -webkit-mask-size: contain;
  mask-size: contain;
  -webkit-mask-repeat: no-repeat;
  mask-repeat: no-repeat;
  -webkit-mask-position: center;
  mask-position: center
}

.chat-start {
  place-items: start;
  grid-template-columns: auto 1fr;
}

.chat-start .chat-header, .chat-start .chat-footer {
  grid-column-start: 2
}

.chat-start .chat-image {
  grid-column-start: 1
}

.chat-start .chat-bubble {
  grid-column-start: 2;
  border-bottom-left-radius: 0
}

.chat-start .chat-bubble:before {
  -webkit-mask-image: url(data:image/svg+xml;base64,PHN2ZyB3aWR0aD0nMycgaGVpZ2h0PSczJyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnPjxwYXRoIGZpbGw9J2JsYWNrJyBkPSdtIDAgMyBMIDMgMyBMIDMgMCBDIDMgMSAxIDMgMCAzJy8+PC9zdmc+);
  mask-image: url(data:image/svg+xml;base64,PHN2ZyB3aWR0aD0nMycgaGVpZ2h0PSczJyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnPjxwYXRoIGZpbGw9J2JsYWNrJyBkPSdtIDAgMyBMIDMgMyBMIDMgMCBDIDMgMSAxIDMgMCAzJy8+PC9zdmc+);
  left: -.75rem
}

[dir=rtl] .chat-start .chat-bubble:before {
  -webkit-mask-image: url(data:image/svg+xml;base64,PHN2ZyB3aWR0aD0nMycgaGVpZ2h0PSczJyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnPjxwYXRoIGZpbGw9J2JsYWNrJyBkPSdtIDAgMyBMIDEgMyBMIDMgMyBDIDIgMyAwIDEgMCAwJy8+PC9zdmc+);
  mask-image: url(data:image/svg+xml;base64,PHN2ZyB3aWR0aD0nMycgaGVpZ2h0PSczJyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnPjxwYXRoIGZpbGw9J2JsYWNrJyBkPSdtIDAgMyBMIDEgMyBMIDMgMyBDIDIgMyAwIDEgMCAwJy8+PC9zdmc+)
}

.chat-end {
  place-items: end;
  grid-template-columns: 1fr auto
}

.chat-end .chat-header, .chat-end .chat-footer {
  grid-column-start: 1
}

.chat-end .chat-image {
  grid-column-start: 2
}

.chat-end .chat-bubble {
  grid-column-start: 1;
  border-bottom-right-radius: 0
}

.chat-end .chat-bubble:before {
  -webkit-mask-image: url(data:image/svg+xml;base64,PHN2ZyB3aWR0aD0nMycgaGVpZ2h0PSczJyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnPjxwYXRoIGZpbGw9J2JsYWNrJyBkPSdtIDAgMyBMIDEgMyBMIDMgMyBDIDIgMyAwIDEgMCAwJy8+PC9zdmc+);
  mask-image: url(data:image/svg+xml;base64,PHN2ZyB3aWR0aD0nMycgaGVpZ2h0PSczJyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnPjxwYXRoIGZpbGw9J2JsYWNrJyBkPSdtIDAgMyBMIDEgMyBMIDMgMyBDIDIgMyAwIDEgMCAwJy8+PC9zdmc+);
  left: 100%
}

[dir=rtl] .chat-end .chat-bubble:before {
  -webkit-mask-image: url(data:image/svg+xml;base64,PHN2ZyB3aWR0aD0nMycgaGVpZ2h0PSczJyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnPjxwYXRoIGZpbGw9J2JsYWNrJyBkPSdtIDAgMyBMIDMgMyBMIDMgMCBDIDMgMSAxIDMgMCAzJy8+PC9zdmc+);
  mask-image: url(data:image/svg+xml;base64,PHN2ZyB3aWR0aD0nMycgaGVpZ2h0PSczJyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnPjxwYXRoIGZpbGw9J2JsYWNrJyBkPSdtIDAgMyBMIDMgMyBMIDMgMCBDIDMgMSAxIDMgMCAzJy8+PC9zdmc+)
}

"""
