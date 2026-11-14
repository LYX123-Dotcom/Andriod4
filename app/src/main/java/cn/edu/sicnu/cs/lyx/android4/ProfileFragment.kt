package cn.edu.sicnu.cs.lyx.android4

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 第 1 个 Fragment：个人信息。
 * 读取 openFileInput 恢复上次保存的内容；离开页面时用 openFileOutput + ObjectOutputStream
 * 把 PlayerProfile 对象序列化写回文件，实现"退出程序后再打开可以恢复"。
 */
class ProfileFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etStudent: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton
    private lateinit var cbReading: CheckBox
    private lateinit var cbGame: CheckBox
    private lateinit var cbShopping: CheckBox
    private lateinit var cbTravel: CheckBox
    private lateinit var tvStatus: TextView

    private var profile = PlayerProfile()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etName = view.findViewById(R.id.et_name)
        etStudent = view.findViewById(R.id.et_student)
        etPhone = view.findViewById(R.id.et_phone)
        etEmail = view.findViewById(R.id.et_email)
        rbMale = view.findViewById(R.id.rb_male)
        rbFemale = view.findViewById(R.id.rb_female)
        cbReading = view.findViewById(R.id.cb_reading)
        cbGame = view.findViewById(R.id.cb_game)
        cbShopping = view.findViewById(R.id.cb_shopping)
        cbTravel = view.findViewById(R.id.cb_travel)
        tvStatus = view.findViewById(R.id.tv_save_status)

        profile = loadProfile()
        bindProfile(profile)

        val btnSave = view.findViewById<MaterialButton>(R.id.btn_save_profile)
        btnSave.setOnClickListener {
            collectProfile()
            profile.saveCount += 1
            profile.savedAt = now()
            saveProfile()
            bindProfile(profile)
            Toast.makeText(requireContext(), R.string.toast_profile_saved, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::etName.isInitialized) {
            collectProfile()
            saveProfile()
        }
    }

    private fun bindProfile(p: PlayerProfile) {
        etName.setText(p.name)
        etStudent.setText(p.studentNumber)
        etPhone.setText(p.phone)
        etEmail.setText(p.email)
        if (p.gender == getString(R.string.gender_female)) {
            rbFemale.isChecked = true
        } else {
            rbMale.isChecked = true
        }
        val hobbies = p.hobbies
        cbReading.isChecked = hobbies.contains(getString(R.string.hobby_reading))
        cbGame.isChecked = hobbies.contains(getString(R.string.hobby_game))
        cbShopping.isChecked = hobbies.contains(getString(R.string.hobby_shopping))
        cbTravel.isChecked = hobbies.contains(getString(R.string.hobby_travel))
        val savedAt = if (p.savedAt.isEmpty()) {
            getString(R.string.profile_never_saved)
        } else {
            p.savedAt
        }
        tvStatus.text = getString(R.string.profile_save_status, p.saveCount, savedAt)
    }

    private fun collectProfile() {
        profile.name = etName.text.toString().trim()
        profile.gender = if (rbFemale.isChecked) {
            getString(R.string.gender_female)
        } else {
            getString(R.string.gender_male)
        }
        profile.studentNumber = etStudent.text.toString().trim()
        profile.phone = etPhone.text.toString().trim()
        profile.email = etEmail.text.toString().trim()

        val hobbies = ArrayList<String>()
        if (cbReading.isChecked) hobbies.add(getString(R.string.hobby_reading))
        if (cbGame.isChecked) hobbies.add(getString(R.string.hobby_game))
        if (cbShopping.isChecked) hobbies.add(getString(R.string.hobby_shopping))
        if (cbTravel.isChecked) hobbies.add(getString(R.string.hobby_travel))
        profile.hobbies = hobbies
    }

    private fun now(): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return fmt.format(Date())
    }

    private fun saveProfile() {
        try {
            val ctx = requireContext()
            ctx.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { fos ->
                ObjectOutputStream(fos).use { oos ->
                    oos.writeObject(profile)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadProfile(): PlayerProfile {
        return try {
            requireContext().openFileInput(FILE_NAME).use { fis ->
                ObjectInputStream(fis).use { ois ->
                    (ois.readObject() as? PlayerProfile) ?: PlayerProfile()
                }
            }
        } catch (e: Exception) {
            PlayerProfile()
        }
    }

    companion object {
        private const val FILE_NAME = "profile.dat"
    }
}
