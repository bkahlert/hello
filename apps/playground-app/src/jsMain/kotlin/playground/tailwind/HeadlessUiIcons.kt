package playground.tailwind

import com.bkahlert.kommons.uri.DataUri
import io.ktor.http.ContentType.Image
import kotlin.reflect.KProperty

/**
 * Pattern definitions from the fantastic [heropatterns](https://heropatterns.com)
 */
@Suppress("LongLine")
object HeadlessUiIcons {

    private operator fun String.provideDelegate(thisRef: Any?, property: KProperty<*>): Lazy<DataUri> = lazy {
        DataUri(Image.SVG, """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">$this</svg>""")
    }

    // @formatter:off
    /** [Headless UI](https://headlessui.com) */ val headless_ui : DataUri by """<path style="fill-rule:evenodd;clip-rule:evenodd;fill:url(#a);" d="M0.7,13.8C0,9.4-0.3,7.2,0.4,5.4c0.4-0.9,1-1.7,1.7-2.4c1.4-1.3,3.6-1.6,8-2.3c4.4-0.7,6.6-1,8.4-0.3c0.9,0.4,1.7,1,2.4,1.7 c1.3,1.4,1.6,3.6,2.3,8c0.7,4.4,1,6.6,0.3,8.4c-0.4,0.9-1,1.7-1.7,2.4c-1.4,1.3-3.6,1.6-8,2.3c-4.4,0.7-6.6,1-8.4,0.3 c-0.9-0.4-1.7-1-2.4-1.7C1.8,20.4,1.4,18.2,0.7,13.8L0.7,13.8z M8.5,21.6c1.2-0.1,2.7-0.3,4.9-0.7c2.2-0.4,3.8-0.6,4.9-0.9 c1.1-0.3,1.6-0.6,1.9-0.9c0.5-0.4,0.9-1,1.1-1.5c0.2-0.4,0.3-1,0.2-2.1c-0.1-1.2-0.3-2.7-0.7-4.9S20.3,6.8,20,5.7 c-0.3-1.1-0.6-1.6-0.9-1.9c-0.4-0.5-1-0.9-1.5-1.1c-0.4-0.2-1-0.3-2.1-0.2c-1.2,0.1-2.7,0.3-4.9,0.7C8.3,3.4,6.8,3.7,5.7,4 C4.6,4.2,4.1,4.5,3.7,4.8c-0.5,0.4-0.9,1-1.1,1.5c-0.2,0.4-0.3,1-0.2,2.1c0.1,1.2,0.3,2.7,0.7,4.9c0.4,2.2,0.6,3.8,0.9,4.9 c0.3,1.1,0.6,1.6,0.9,1.9c0.4,0.5,1,0.9,1.5,1.1C6.8,21.6,7.3,21.7,8.5,21.6L8.5,21.6z"/><path fill="url(#b)" d="M4.8,14.9L19.4,10c-0.3-1.8-0.5-3.1-0.7-4c-0.3-1-0.5-1.3-0.5-1.4c-0.3-0.3-0.6-0.6-1-0.7 c-0.1,0-0.4-0.2-1.5-0.1c-1.1,0.1-2.5,0.3-4.8,0.6C8.5,4.8,7.1,5.1,6,5.3C5,5.6,4.8,5.8,4.7,5.9c-0.3,0.3-0.6,0.6-0.7,1 c0,0.1-0.2,0.4-0.1,1.5c0.1,1.1,0.3,2.5,0.6,4.8C4.6,13.8,4.7,14.4,4.8,14.9z"/><defs><linearGradient id="a" gradientUnits="userSpaceOnUse" x1="10.0623" y1="34.1059" x2="13.8731" y2="10.2962" gradientTransform="matrix(1 0 0 -1 0 34)"><stop offset="0" style="stop-color:#66E3FF"/><stop offset="1" style="stop-color:#7064F9"/></linearGradient><linearGradient id="b" gradientUnits="userSpaceOnUse" x1="10.062" y1="34.1058" x2="13.8728" y2="10.2962" gradientTransform="matrix(1 0 0 -1 0 34)"><stop offset="0" style="stop-color:#66E3FF"/><stop offset="1" style="stop-color:#7064F9"/></linearGradient></defs>"""
    /** [Menu (Dropdown)](https://headlessui.com/react/menu) */ val menu : DataUri by """<rect width="24" height="24" rx="6" fill="url(#a)"/><rect x="13" y="5" width="7" height="3" rx="1.5" fill="#5B21B6"/><g filter="url(#b)"><rect x="5" y="9" width="15" height="10" rx="2" fill="#F5F3FF"/></g><defs><linearGradient id="a" x1="0" y1="0" x2="24.053" y2=".053" gradientUnits="userSpaceOnUse"><stop stop-color="#A855F7"/><stop offset="1" stop-color="#6366F1"/></linearGradient><filter id="b" x="2" y="7" width="21" height="16" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB"><feFlood flood-opacity="0" result="BackgroundImageFix"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.06 0"/><feBlend in2="BackgroundImageFix" result="effect1_dropShadow"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1.5"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.1 0"/><feBlend in2="effect1_dropShadow" result="effect2_dropShadow"/><feBlend in="SourceGraphic" in2="effect2_dropShadow" result="shape"/></filter></defs>"""
    /** [Listbox (Select)](https://headlessui.com/react/listbox) */ val listbox : DataUri by """<rect width="24" height="24" rx="6" fill="url(#a)"/><rect x="5" y="5" width="14" height="3" rx="1.5" fill="#FFFBEB"/><g filter="url(#b)"><rect x="5" y="9" width="14" height="10" rx="2" fill="#FFFBEB"/></g><path fill="#FCD34D" d="M5 11h14v2H5z"/><defs><linearGradient id="a" x1="0" y1="0" x2="24.053" y2=".053" gradientUnits="userSpaceOnUse"><stop stop-color="#FACC15"/><stop offset="1" stop-color="#F97316"/></linearGradient><filter id="b" x="2" y="7" width="20" height="16" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB"><feFlood flood-opacity="0" result="BackgroundImageFix"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.06 0"/><feBlend in2="BackgroundImageFix" result="effect1_dropShadow"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1.5"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.1 0"/><feBlend in2="effect1_dropShadow" result="effect2_dropShadow"/><feBlend in="SourceGraphic" in2="effect2_dropShadow" result="shape"/></filter></defs>"""
    /** [Combobox (Autocomplete)](https://headlessui.com/react/combobox) */ val combobox : DataUri by """<rect width="24" height="24" rx="6" fill="url(#a)"/><mask id="c" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="24" height="24"><rect width="24" height="24" rx="6" fill="url(#b)"/></mask><g mask="url(#c)"><g filter="url(#d)"><path d="M4 7a1 1 0 0 1 1-1h14a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V7z" fill="#fff"/><path d="M5 5.5A1.5 1.5 0 0 0 3.5 7v3A1.5 1.5 0 0 0 5 11.5h14a1.5 1.5 0 0 0 1.5-1.5V7A1.5 1.5 0 0 0 19 5.5H5z" stroke="#000" stroke-opacity=".04"/></g><path d="M6.5 8.5h4" stroke="#64748B" stroke-linecap="round"/><path opacity=".2" d="M16.5 8.5h1" stroke="#0F172A" stroke-linecap="round"/><g filter="url(#e)"><path d="M4 14a1 1 0 0 1 1-1h14a1 1 0 0 1 1 1v11a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V14z" fill="#fff"/><path d="M5 12.5A1.5 1.5 0 0 0 3.5 14v11A1.5 1.5 0 0 0 5 26.5h14a1.5 1.5 0 0 0 1.5-1.5V14a1.5 1.5 0 0 0-1.5-1.5H5z" stroke="#000" stroke-opacity=".04"/></g><path opacity=".3" d="M6.5 15.5h7m-7 3h6m-6 3h3" stroke="#0F172A" stroke-linecap="round"/></g><defs><linearGradient id="a" x1="0" y1="0" x2="24.053" y2=".053" gradientUnits="userSpaceOnUse"><stop stop-color="#AAF0F4"/><stop offset="1" stop-color="#10B981"/></linearGradient><linearGradient id="b" x1="0" y1="0" x2="24.053" y2=".053" gradientUnits="userSpaceOnUse"><stop stop-color="#EC4899"/><stop offset="1" stop-color="#F43F5E"/></linearGradient><filter id="d" x="0" y="3" width="24" height="13" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB"><feFlood flood-opacity="0" result="BackgroundImageFix"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.06 0"/><feBlend in2="BackgroundImageFix" result="effect1_dropShadow_903_6"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1.5"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.1 0"/><feBlend in2="effect1_dropShadow_903_6" result="effect2_dropShadow_903_6"/><feBlend in="SourceGraphic" in2="effect2_dropShadow_903_6" result="shape"/></filter><filter id="e" x="0" y="10" width="24" height="21" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB"><feFlood flood-opacity="0" result="BackgroundImageFix"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.06 0"/><feBlend in2="BackgroundImageFix" result="effect1_dropShadow_903_6"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1.5"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.1 0"/><feBlend in2="effect1_dropShadow_903_6" result="effect2_dropShadow_903_6"/><feBlend in="SourceGraphic" in2="effect2_dropShadow_903_6" result="shape"/></filter></defs>"""
    /** [Switch (Toggle)](https://headlessui.com/react/switch) */ val switch : DataUri by """<rect width="24" height="24" rx="6" fill="url(#a)"/><rect x="4" y="7" width="16" height="10" rx="5" fill="#0F766E"/><rect x="11" y="8" width="8" height="8" rx="4" fill="#F0FDFA"/><defs><linearGradient id="a" x1="0" y1="0" x2="24.053" y2=".053" gradientUnits="userSpaceOnUse"><stop stop-color="#4ADE80"/><stop offset="1" stop-color="#14B4C6"/></linearGradient></defs>"""
    /** [Disclosure](https://headlessui.com/react/disclosure) */ val disclosure : DataUri by """<rect width="24" height="24" rx="6" fill="url(#a)"/><g filter="url(#b)"><rect x="5" y="5" width="14" height="14" rx="2" fill="#FAF5FF"/></g><path d="m9 11 3 3 3-3" stroke="#A855F7" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><defs><linearGradient id="a" x1="0" y1="0" x2="24.053" y2=".053" gradientUnits="userSpaceOnUse"><stop stop-color="#D946EF"/><stop offset="1" stop-color="#9333EA"/></linearGradient><filter id="b" x="2" y="3" width="20" height="20" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB"><feFlood flood-opacity="0" result="BackgroundImageFix"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.06 0"/><feBlend in2="BackgroundImageFix" result="effect1_dropShadow"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1.5"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.1 0"/><feBlend in2="effect1_dropShadow" result="effect2_dropShadow"/><feBlend in="SourceGraphic" in2="effect2_dropShadow" result="shape"/></filter></defs>"""
    /** [Dialog (Modal)](https://headlessui.com/react/dialog) */ val dialog : DataUri by """<rect width="24" height="24" rx="6" fill="url(#a)"/><g filter="url(#b)"><rect x="5" y="6" width="14" height="12" rx="2" fill="#EFF6FF"/></g><defs><linearGradient id="a" x1="0" y1="0" x2="24.053" y2=".053" gradientUnits="userSpaceOnUse"><stop stop-color="#38BDF8"/><stop offset="1" stop-color="#6366F1"/></linearGradient><filter id="b" x="2" y="4" width="20" height="18" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB"><feFlood flood-opacity="0" result="BackgroundImageFix"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.06 0"/><feBlend in2="BackgroundImageFix" result="effect1_dropShadow"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1.5"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.1 0"/><feBlend in2="effect1_dropShadow" result="effect2_dropShadow"/><feBlend in="SourceGraphic" in2="effect2_dropShadow" result="shape"/></filter></defs>"""
    /** [Popover](https://headlessui.com/react/popover) */ val popover : DataUri by """<rect width="24" height="24" rx="6" fill="url(#a)"/><g filter="url(#b)"><path d="M5 9a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v6a2 2 0 0 1-2 2h-2.586a1 1 0 0 0-.707.293L12 19l-1.707-1.707A1 1 0 0 0 9.586 17H7a2 2 0 0 1-2-2V9z" fill="#FFF7ED"/></g><defs><linearGradient id="a" x1="0" y1="0" x2="24.053" y2=".053" gradientUnits="userSpaceOnUse"><stop stop-color="#FB923C"/><stop offset="1" stop-color="#DB2777"/></linearGradient><filter id="b" x="2" y="5" width="20" height="18" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB"><feFlood flood-opacity="0" result="BackgroundImageFix"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.06 0"/><feBlend in2="BackgroundImageFix" result="effect1_dropShadow"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1.5"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.1 0"/><feBlend in2="effect1_dropShadow" result="effect2_dropShadow"/><feBlend in="SourceGraphic" in2="effect2_dropShadow" result="shape"/></filter></defs>"""
    /** [Radio Group](https://headlessui.com/react/radio-group) */ val radio_group : DataUri by """<rect width="24" height="24" rx="6" fill="url(#a)"/><circle cx="12" cy="12" r="7" fill="#155E75"/><circle cx="12" cy="12" r="3" fill="#ECFEFF"/><defs><linearGradient id="a" x1="0" y1="0" x2="24.053" y2=".053" gradientUnits="userSpaceOnUse"><stop stop-color="#39D0DD"/><stop offset="1" stop-color="#0EA5E9"/></linearGradient></defs>"""
    /** [Tabs](https://headlessui.com/react/tabs) */ val tabs : DataUri by """<rect width="24" height="24" rx="6" fill="url(#a)"/><rect x="2" y="9" width="20" height="6" rx="1" fill="#1E3A8A" fill-opacity=".5"/><path d="M2 10a1 1 0 0 1 1-1h9v6H3a1 1 0 0 1-1-1v-4z" fill="#EFF6FF"/><defs><linearGradient id="a" x1="0" y1="0" x2="24.053" y2=".053" gradientUnits="userSpaceOnUse"><stop stop-color="#38BDF8"/><stop offset="1" stop-color="#2563EB"/></linearGradient></defs>"""
    /** [Transition](https://headlessui.com/react/transition) */ val transition : DataUri by """<rect width="24" height="24" rx="6" fill="url(#a)"/><circle opacity=".5" cx="9" cy="12" r="5" fill="#FFF1F2"/><g filter="url(#b)"><circle cx="15" cy="12" r="5" fill="#FFF1F2"/></g><defs><linearGradient id="a" x1="0" y1="0" x2="24.053" y2=".053" gradientUnits="userSpaceOnUse"><stop stop-color="#EC4899"/><stop offset="1" stop-color="#F43F5E"/></linearGradient><filter id="b" x="7" y="5" width="16" height="16" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB"><feFlood flood-opacity="0" result="BackgroundImageFix"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.06 0"/><feBlend in2="BackgroundImageFix" result="effect1_dropShadow"/><feColorMatrix in="SourceAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"/><feOffset dy="1"/><feGaussianBlur stdDeviation="1.5"/><feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.1 0"/><feBlend in2="effect1_dropShadow" result="effect2_dropShadow"/><feBlend in="SourceGraphic" in2="effect2_dropShadow" result="shape"/></filter></defs>"""
    // @formatter:on
}
