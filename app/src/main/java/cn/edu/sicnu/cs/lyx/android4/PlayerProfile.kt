package cn.edu.sicnu.cs.lyx.android4

import java.io.Serializable

/**
 * 个人信息数据，实现 Serializable 以便用 ObjectOutputStream 写入文件。
 * hobbies 用 ArrayList 保存，ArrayList 本身可序列化。
 */
class PlayerProfile : Serializable {
    var name: String = ""
    var gender: String = ""
    var studentNumber: String = ""
    var phone: String = ""
    var email: String = ""
    var hobbies: ArrayList<String> = ArrayList()
    var savedAt: String = ""
    var saveCount: Int = 0
}
