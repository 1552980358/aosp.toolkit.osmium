package aosp.toolkit.perseus

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import aosp.toolkit.perseus.base.BaseOperation.Companion.ShortToast
import aosp.toolkit.perseus.base.ViewPagerAdapter

import kotlinx.android.synthetic.main.activity_aosp.*
import kotlinx.android.synthetic.main.activity_aospselectrom.*
import kotlinx.android.synthetic.main.fragment_lineageos.*
import kotlinx.android.synthetic.main.fragment_omni.*
import kotlinx.android.synthetic.main.view_losbrand.view.*
import kotlinx.android.synthetic.main.item_losdevice.view.*
import kotlinx.android.synthetic.main.view_devicedownload.view.*
import kotlinx.android.synthetic.main.view_omnidevice.view.*

import org.jsoup.Jsoup

/*
 * @File:   AOSPActivity
 * @Author: 1552980358
 * @Time:   4:56 PM
 * @Date:   24 May 2019
 * 
 */

class AOSPActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aosp)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val tabList = listOf("LineageOS", "OMNIRom")
        val fragmentList = listOf(LineageOSFragment(), OMNIFragment())
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragmentList, tabList)
        tabLayout.setupWithViewPager(viewPager)
    }

    class AOSPSelectRomActivity : AppCompatActivity() {
        companion object {
            val head = mapOf(
                "LOS" to "https://download.lineageos.org", "OMNI" to "http://dl.omnirom.org"
            )
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_aospselectrom)

            toolbarAOSPSelection.title =
                "${intent.getStringExtra("brand")} ${intent.getStringExtra("model")} (${intent.getStringExtra(
                    "device"
                )})"

            setSupportActionBar(toolbarAOSPSelection)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbarAOSPSelection.setNavigationOnClickListener { finish() }

            Thread {
                when (intent.getStringExtra("rom")) {
                    "LOS" -> lineageos()
                    "OMNI" -> omni()
                }
            }.start()
        }

        private fun lineageos() {
            val href = intent.getStringExtra("href")

            val document =
                Jsoup.connect(head["LOS"] + href).get().getElementsByTag("tbody")[0].getElementsByTag(
                    "tr"
                )

            for (i in document) {
                val td = i.getElementsByTag("td")

                val buildType = td[0].select("td").text()   // Build Type
                val version = td[1].select("td").text()     // Version
                val a = td[2].select("td").select("a")[0]
                val fileName = a.text()                             // File Name
                val url = a.attr("href")                  // URL
                val size = td[3].select("td").text()        // Size
                val date = td[4].select("td").text()        // Date

                val deviceDownloadView =
                    DeviceDownloadView(this, url, fileName, date, buildType, version, size)
                runOnUiThread {
                    try {
                        aospSelectROMContainer.addView(deviceDownloadView)
                    } catch (e: Exception) {
                        ShortToast(this, e, false)
                    }
                }

            }
        }

        private fun omni() {
            val href = intent.getStringExtra("href")

            for (i in Jsoup.connect(head["OMNI"] + href).get().getElementsByTag("tr")) {
                if (i.getElementsByClass("fb-i").text().isEmpty() || i.getElementsByClass("fb-n").select(
                        "a"
                    ).attr("href") == ".."
                ) {
                    continue
                }
                val fbn = i.getElementsByClass("fb-n").select("a")
                val fileName = fbn.text()
                val url = fbn.attr("href")
                val date = i.getElementsByClass("fb-d").select("td").text()
                val size = i.getElementsByClass("fb-s").select("td").text()

                val deviceDownloadView = DeviceDownloadView(this, head["OMNI"] + url, fileName, date, "", "", size)
                runOnUiThread { omniRoot.addView(deviceDownloadView) }

            }

        }

        @SuppressLint("ViewConstructor", "SetTextI18n")
        class DeviceDownloadView(
            context: Context,
            url: String,
            fileName: String,
            date: String,
            type: String,
            version: String,
            size: String
        ) : LinearLayout(context) {
            init {
                LayoutInflater.from(context).inflate(R.layout.view_devicedownload, this)
                romFile.text = fileName
                romSize.text = size
                romDate.text = date
                romVersion.text = if (type.isNotEmpty()) {
                    if (version.isNotEmpty()) {
                        "$version-$type"
                    } else {
                        type
                    }
                } else {
                    romVersion.visibility = View.GONE
                    ""
                }
                romDownload.setOnClickListener {
                    Thread {
                        val path = context.externalCacheDir!!.absolutePath
                        context.startActivity(
                            Intent(context, DownloadActivity::class.java).putExtra(
                                "fileName", fileName
                            ).putExtra("url", url).putExtra("filePath", path)
                        )
                    }.start()
                }
                romLinkCopy.setOnClickListener {
                    Thread {
                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip =
                            ClipData.newPlainText("text", url)

                    }.start()
                }
            }
        }
    }

    class LineageOSFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_lineageos, container, false)
        }

        private var created = false
        override fun setUserVisibleHint(isVisibleToUser: Boolean) {
            super.setUserVisibleHint(isVisibleToUser)
            if (isVisibleToUser && !created) {
                Thread {
                    try {

                        for (i in Jsoup.connect("https://download.lineageos.org/").get().getElementsByClass(
                            "no-padding"
                        )) {
                            // Brand
                            val brand =
                                i.getElementsByClass("collapsible-header waves-effect waves-teal bold")
                                    .select("a").text()
                            val losBrandView = LOSBrandView(
                                context!!, brand
                            )

                            for (j in i.getElementsByClass("device-link")) {
                                val href = j.select("a").attr("href")     // Href
                                val info = j.getElementsByTag("div")
                                val model = info[0].select("div").text()            // Model
                                val device = info[1].select("div").text()           // Device

                                losBrandView.addView(
                                    LOSDeviceItem(
                                        context!!, brand, model, device, href
                                    )
                                )
                            }

                            activity!!.runOnUiThread {
                                try {
                                    losRoot.addView(losBrandView)
                                } catch (e: Exception) {
                                    ShortToast(activity!!, e)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        ShortToast(activity!!, e, false)
                    }
                    created = true
                }.start()
            }
        }

        @SuppressLint("ViewConstructor")
        private class LOSBrandView(context: Context, brand: String) : LinearLayout(context) {
            init {
                LayoutInflater.from(context).inflate(R.layout.view_losbrand, this)
                losViewTitle.text = brand
                losViewRoot.setOnClickListener {
                    losViewContainer.visibility = if (losViewContainer.visibility == View.VISIBLE) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                }
            }

            override fun addView(view: View) {
                losViewContainer.addView(view)
            }
        }

        @SuppressLint("ViewConstructor")
        private class LOSDeviceItem(
            context: Context, brand: String, model: String, device: String, href: String
        ) : LinearLayout(context) {
            init {
                LayoutInflater.from(context).inflate(R.layout.item_losdevice, this)
                losModel.text = model
                losdevice.text = device
                losDeviceRoot.setOnClickListener {
                    val intent = Intent().setClass(context, AOSPSelectRomActivity::class.java)
                        .putExtra("rom", "LOS").putExtra("href", href).putExtra("device", device)
                        .putExtra("model", model).putExtra("brand", brand)
                    context.startActivity(intent)
                }
            }
        }
    }

    class OMNIFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_omni, container, false)
        }

        override fun setUserVisibleHint(isVisibleToUser: Boolean) {
            super.setUserVisibleHint(isVisibleToUser)
            Thread {
                try {
                    for (i in Jsoup.connect("http://dl.omnirom.org/").get().getElementsByTag("tr")) {
                        if (i.getElementsByClass("fb-i").select("img").attr("href").isEmpty() || i.getElementsByClass(
                                "fb-i"
                            ).select("img").attr("href") == null || i.getElementsByClass("fb-s").select(
                                "img"
                            ).attr("href").isNotEmpty() || i.getElementsByClass("fb-s").select("img").attr(
                                "href"
                            ) != null
                        ) {
                            continue
                        }
                        val fbn = i.getElementsByClass("fb-n").select("a")
                        val device = fbn.attr("href")              // Device
                        val href = fbn.text()                                // Href
                        val date = i.getElementsByClass("fb-d").select("td").text()     // Date
                        val omniDeviceView = OMNIDeviceView(context!!, device, href, date)
                        activity!!.runOnUiThread {
                            try {
                                omniRoot.addView(omniDeviceView)
                            } catch (e: Exception) {
                                ShortToast(activity!!, e, false)
                            }
                        }
                    }

                } catch (e: Exception) {
                    ShortToast(activity!!, e, false)
                }
            }.start()
        }

        @SuppressLint("ViewConstructor")
        class OMNIDeviceView(context: Context, device: String, href: String, date: String) :
            LinearLayout(context) {
            init {
                LayoutInflater.from(context).inflate(R.layout.view_omnidevice, this)
                omniDevice.text = device
                omniLastUpdate.text = date
                omniViewRoot.setOnClickListener {
                    Thread {
                        val intent = Intent().setClass(context, AOSPSelectRomActivity::class.java)
                            .putExtra("rom", "OMNI").putExtra("href", href).putExtra("brand", "")
                            .putExtra("model", "").putExtra("device", device)
                        context.startActivity(intent)
                    }.start()
                }
            }
        }
    }
}