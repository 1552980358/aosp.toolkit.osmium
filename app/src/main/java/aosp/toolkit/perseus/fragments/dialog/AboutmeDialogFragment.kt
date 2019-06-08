package aosp.toolkit.perseus.fragments.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import aosp.toolkit.perseus.R
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import aosp.toolkit.perseus.base.BaseIndex.QQGroupKey

/*
 * @File:   AboutmeDialogFragment
 * @Author: 1552980358
 * @Time:
 * @Date:   2 Apr 2019
 *
 */
 
@Suppress("DEPRECATION")
class AboutmeDialogFragment: DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (dialog.window)!!.setBackgroundDrawable(ContextCompat.getDrawable(context!!, android.R.color.transparent))
        val view = inflater.inflate(R.layout.dialogfragment_aboutme, container, false)

        val textView1 = view.findViewById<TextView>(R.id.coolapk)
        textView1.append(Html.fromHtml("http://www.coolapk.com/u/724354/"))
        textView1.movementMethod = LinkMovementMethod.getInstance()

        val textView2 = view.findViewById<TextView>(R.id.github)
        textView2.append(Html.fromHtml("https://github.com/1552980358"))
        textView2.movementMethod = LinkMovementMethod.getInstance()

        val textView3 = view.findViewById<TextView>(aosp.toolkit.perseus.R.id.junpQQGroup)
        textView3.setOnClickListener {
            joinQQGroup(QQGroupKey)
        }
        textView3.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        return view
    }

    /****************
     *
     * 发起添加群流程。群号：英仙 Perseus(235443368) 的 key 为： Zbl6GscS86zcFBrelPvBW2lPcOPtqzt-
     * 调用 joinQQGroup(Zbl6GscS86zcFBrelPvBW2lPcOPtqzt-) 即可发起手Q客户端申请加群 英仙 Perseus(235443368)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     */
    private fun joinQQGroup(key: String): Boolean {
        val intent = Intent()
        intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            // 未安装手Q或安装的版本不支持
            false
        }

    }


}