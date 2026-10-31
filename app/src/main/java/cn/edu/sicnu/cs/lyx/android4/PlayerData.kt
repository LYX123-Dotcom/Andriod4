package cn.edu.sicnu.cs.lyx.android4

import android.content.Context

/**
 * 玩家数据的内存单例。
 * 按实验要求，修改后的个人信息无需保存到文件中，这里仅在应用运行期间保持。
 * 初始值全部为空，不预置任何真实个人信息，由用户在"信息修改"界面自行填写。
 */
object PlayerData {
    var name = ""
    var gender = ""
    var studentNumber = ""
    var phone = ""
    var email = ""
    var hobbies = mutableListOf<String>()
    var highScore = 0

    private var initialized = false

    /** 初始化为空值（性别默认"男"），幂等，多个界面均可安全调用 */
    fun ensureInitialized(context: Context) {
        if (initialized) return
        name = ""
        gender = context.getString(R.string.gender_male)
        studentNumber = ""
        phone = ""
        email = ""
        hobbies = mutableListOf()
        initialized = true
    }
}
