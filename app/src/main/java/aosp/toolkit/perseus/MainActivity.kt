package aosp.toolkit.perseus

/*
 * OsToolkit - Kotlin
 *
 * Date : 31/12/2018
 *
 * By   : 1552980358
 *
 */

/*
 * Modify
 *
 * 23/1/2019
 * 8/7/2019
 *
 */

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout

import aosp.toolkit.perseus.base.BaseManager
import aosp.toolkit.perseus.base.BaseOperation.Companion.ShortToast
import aosp.toolkit.perseus.base.BaseOperation.Companion.getPackageVersion
import aosp.toolkit.perseus.fragments.*
import aosp.toolkit.perseus.fragments.dialog.AboutmeDialogFragment
import aosp.toolkit.perseus.fragments.dialog.LicenceDialogFragment
import aosp.toolkit.perseus.fragments.dialog.UpdateDialogFragment
import aosp.toolkit.perseus.services.UpdateService

import com.topjohnwu.superuser.Shell

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener, UpdateService.UpdateServiceListener {

    /*
    // 定义fragments
    private var mainFragment: MainFragment = MainFragment()
    private var aboutFragment: Fragment? = null
    private lateinit var deviceInfoFragment: Fragment
    private lateinit var chargingFragment: Fragment
    private lateinit var coreFragment: Fragment
    private lateinit var applyYCFragment: Fragment
    private lateinit var applyPixelCatFragment: Fragment
    private lateinit var romIOFragment: Fragment
    private lateinit var otherFragment: Fragment
    */

    private val fragmentMap = mutableMapOf<String, Fragment>(FragmentTable[0] to MainFragment())

    companion object {
        val FragmentTable = arrayOf(
            "MainFragment",
            "ApplyPixelCatFragment",
            "ApplyYCFragment",
            "ChargingFragment",
            "CoreFragment",
            "DeviceFragment",
            "OtherFragment",
            "RomIOFragment"
        )
    }

    // 显示的fragment
    private var currentFragment = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread {
            // 移除上一个activity
            BaseManager.getInstance().finishActivities()
            BaseManager.instance.mainActivity = this
        }.start()

        checkUpdate()
        setContentView(R.layout.activity_main)

        Thread {
            if (getSharedPreferences("launch", Context.MODE_PRIVATE).getString(
                    "aboutAuthor", "0"
                ) != getPackageVersion(this)
            ) {
                AboutmeDialogFragment().show(
                    supportFragmentManager, "AboutmeDialogFragment().launch"
                )
                getSharedPreferences("launch", Context.MODE_PRIVATE).edit()
                    .putString("aboutAuthor", getPackageVersion(this)).apply()
            }
        }.start()

        initUI()
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayout_main, fragmentMap[FragmentTable[0]]!!).commit()
        drawer_layout.openDrawer(GravityCompat.START)
    }

    private fun initUI() {
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        drawer_layout.setScrimColor(ContextCompat.getColor(this, android.R.color.transparent))
        toggle.syncState()

        if (getSharedPreferences("ui", Context.MODE_PRIVATE).getBoolean("navBar", true)) {
            // ContextCompact通用包
            window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

            // 5.0+可过编译 6.0+弃用 无法更改颜色
            // window.navigationBarColor = resources.getColor(R.color.colorPrimaryDark)
            // 6.0加入的API 无法适配 5.0 / 5.1
            // window.navigationBarColor = resources.getColor(R.color.colorPrimary, null)
        }
    }

    fun checkUpdate() {
        startService(Intent(applicationContext, UpdateService::class.java))
    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.END) -> drawer_layout.closeDrawer(GravityCompat.END)
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(
                GravityCompat.START
            )
            else -> {
                Thread {
                    val fragmentManager = supportFragmentManager.beginTransaction()
                    for (i in supportFragmentManager.fragments) {
                        fragmentManager.remove(i)
                    }
                    fragmentManager.commit()
                }.start()

                super.onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.reb -> Shell.su("reboot").exec()
            R.id.soft -> Shell.su("killall zygote").exec()
            R.id.bl -> Shell.su("reboot bootloader").exec()
            R.id.rec -> Shell.su("reboot recovery").exec()
            R.id.re9008 -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.warn_9008_title).setMessage(R.string.warn_9008_msg)
                    .setPositiveButton(R.string.cont) { _, _ ->
                        Shell.su("reboot edl").exec()
                    }.setNegativeButton(R.string.cancel) { _, _ -> }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        when (val id = item.itemId) {
            R.id.nav_monitor -> startActivity(Intent(this, UsageActivity::class.java))
            R.id.nav_tower -> startActivity(Intent(this, DisableAppActivity::class.java))
            R.id.nav_zxing -> startActivity(Intent(this, ZXingActivity::class.java))
            R.id.nav_miui -> startActivity(Intent(this, MIUIActivity::class.java))
            R.id.nav_aosp -> startActivity(Intent(this, AOSPActivity::class.java))
            else -> exchangeFragment(id)
        }
        return true
    }

    fun showLicenceList() {
        Thread {
            LicenceDialogFragment().show(
                supportFragmentManager, "LicenceDialogFragment()"
            )
        }.start()
    }

    fun openDrawerLayoutLeft() {
        drawer_layout.openDrawer(GravityCompat.START)
    }

    fun openDrawerLayoutRight() {
        drawer_layout.openDrawer(GravityCompat.END)
    }

    fun navigationViewBottomListener(view: View) {
        view.findViewById<RelativeLayout>(R.id.nav_about).setOnClickListener(this)
        view.findViewById<RelativeLayout>(R.id.nav_aboutme).setOnClickListener(this)
    }

    override fun onUpdateChecking() {
        super.onUpdateChecking()
        ShortToast(this, R.string.update_startcheck, false)
    }

    override fun onUpdate(
        version: String, url: String, date: String, changelogZh: String, changelogEn: String
    ) {
        super.onUpdate(version, url, date, changelogZh, changelogEn)
        UpdateDialogFragment().setData(version, url, date, changelogZh, changelogEn)
            .show(supportFragmentManager, "UpdateDialogFragment()")
    }

    override fun onNewest(version: String, code: String) {
        super.onNewest(version, code)
        ShortToast(this, String.format(getString(R.string.update_alreadynew), version, code), false)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.nav_about -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                drawer_layout.openDrawer(GravityCompat.END)
            }
            R.id.nav_aboutme -> {
                @Suppress("SpellCheckingInspection") AboutmeDialogFragment().show(
                    supportFragmentManager,
                    "AboutmeDialogFragment()"
                )
            }
        }
    }

    private fun exchangeFragment(id: Int) {
        drawer_layout.closeDrawer(GravityCompat.START)
        Thread {
            val frag: Int
            var newObject = false
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val title: Int

            when (id) {
                R.id.nav_applypc -> {
                    title = R.string.nav_pc
                    if (fragmentMap[FragmentTable[1]] == null) {
                        newObject = true
                        fragmentMap[FragmentTable[1]] = ApplyYCFragment()
                    }
                    frag = 1
                }
                R.id.nav_applyyc -> {
                    title = R.string.nav_yc
                    if (fragmentMap[FragmentTable[2]] == null) {
                        newObject = true
                        fragmentMap[FragmentTable[2]] = ApplyYCFragment()
                    }
                    frag = 2
                }
                R.id.nav_charging -> {
                    title = R.string.nav_charging
                    if (fragmentMap[FragmentTable[3]] == null) {
                        newObject = true
                        fragmentMap[FragmentTable[3]] = ChargingFragment()
                    }
                    frag = 3
                }
                R.id.nav_cores -> {
                    title = R.string.nav_processor
                    if (fragmentMap[FragmentTable[4]] == null) {
                        newObject = true
                        fragmentMap[FragmentTable[4]] = CoreFragment()
                    }
                    frag = 4
                }
                R.id.nav_deviceinfo -> {
                    title = R.string.nav_deviceinfo
                    if (fragmentMap[FragmentTable[5]] == null) {
                        newObject = true
                        fragmentMap[FragmentTable[5]] = DeviceInfoFragment()
                    }
                    frag = 5
                }
                R.id.nav_others -> {
                    title = R.string.nav_other
                    if (fragmentMap[FragmentTable[6]] == null) {
                        newObject = true
                        fragmentMap[FragmentTable[6]] = OtherFragment()
                    }
                    frag = 6
                }
                R.id.nav_romio -> {
                    title = R.string.nav_romio
                    if (fragmentMap[FragmentTable[7]] == null) {
                        newObject = true
                        fragmentMap[FragmentTable[7]] = RomIOFragment()
                    }
                    frag = 7
                }
                else -> {
                    title = R.string.nav_main
                    frag = 0
                }
            }

            /*when (id) {
                R.id.nav_charging -> {
                    title = R.string.nav_charging
                    if (::chargingFragment.isInitialized) {
                        fragmentTransaction.show(chargingFragment)
                    } else {
                        chargingFragment = ChargingFragment()
                        fragmentTransaction.add(R.id.frameLayout_main, chargingFragment)
                    }
                    frag = chargingFragment
                }
                R.id.nav_cores -> {
                    title = R.string.nav_processor
                    if (::coreFragment.isInitialized) {
                        fragmentTransaction.show(coreFragment)
                    } else {
                        coreFragment = CoreFragment()
                        fragmentTransaction.add(R.id.frameLayout_main, coreFragment)
                    }
                    frag = coreFragment
                }
                R.id.nav_applyyc -> {
                    title = R.string.nav_yc
                    if (::applyYCFragment.isInitialized) {
                        fragmentTransaction.show(applyYCFragment)
                    } else {
                        applyYCFragment = ApplyYCFragment()
                        fragmentTransaction.add(R.id.frameLayout_main, applyYCFragment)
                    }
                    frag = applyYCFragment
                }
                R.id.nav_applypc -> {
                    title = R.string.nav_pc
                    if (::applyPixelCatFragment.isInitialized) {
                        fragmentTransaction.show(applyPixelCatFragment)
                    } else {
                        applyPixelCatFragment = ApplyPixelCatFragment()
                        fragmentTransaction.add(R.id.frameLayout_main, applyPixelCatFragment)
                    }
                    frag = applyPixelCatFragment
                }
                R.id.nav_romio -> {
                    title = R.string.nav_romio
                    if (::romIOFragment.isInitialized) {
                        fragmentTransaction.show(romIOFragment)
                    } else {
                        romIOFragment = RomIOFragment()
                        fragmentTransaction.add(R.id.frameLayout_main, romIOFragment)
                    }
                    frag = romIOFragment
                }
                R.id.nav_others -> {
                    title = R.string.nav_other
                    if (::otherFragment.isInitialized) {
                        fragmentTransaction.show(otherFragment)
                    } else {
                        otherFragment = OtherFragment()
                        fragmentTransaction.add(R.id.frameLayout_main, otherFragment)
                    }
                    frag = otherFragment
                }
                R.id.nav_deviceinfo -> {
                    title = R.string.nav_deviceinfo
                    if (::deviceInfoFragment.isInitialized) {
                        fragmentTransaction.show(deviceInfoFragment)
                    } else {
                        deviceInfoFragment = DeviceInfoFragment()
                        fragmentTransaction.add(R.id.frameLayout_main, deviceInfoFragment)
                    }
                    frag = deviceInfoFragment
                }
                else -> {
                    frag = mainFragment
                    fragmentTransaction.show(mainFragment)
                    title = R.string.nav_main
                }
            }*/
            /*
                        runOnUiThread { toolbar.setTitle(title) }
                        if ( != currentFragment) {
                            fragmentTransaction.hide(currentFragment).commit()
                        }
                        currentFragment = frag
                        */

            if (frag != currentFragment) {
                runOnUiThread { toolbar.setTitle(title) }
                if (!newObject) {
                    fragmentTransaction.show(fragmentMap[FragmentTable[frag]]!!)
                } else {
                    fragmentTransaction.add(
                        R.id.frameLayout_main,
                        fragmentMap[FragmentTable[frag]]!!
                    )
                }
                fragmentTransaction.hide(fragmentMap[FragmentTable[currentFragment]]!!).commit()
                currentFragment = frag
            }

        }.start()
    }
}
