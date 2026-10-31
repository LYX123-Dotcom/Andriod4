package cn.edu.sicnu.cs.lyx.android4

import android.os.Bundle
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText


class InfoEditActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etStudent: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton
    private lateinit var cbReading: CheckBox
    private lateinit var cbGame: CheckBox
    private lateinit var cbShopping: CheckBox
    private lateinit var cbTravel: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerData.ensureInitialized(this)
        setContentView(R.layout.activity_info_edit)

        etName = findViewById(R.id.et_name)
        etStudent = findViewById(R.id.et_student)
        etPhone = findViewById(R.id.et_phone)
        etEmail = findViewById(R.id.et_email)
        rbMale = findViewById(R.id.rb_male)
        rbFemale = findViewById(R.id.rb_female)
        cbReading = findViewById(R.id.cb_reading)
        cbGame = findViewById(R.id.cb_game)
        cbShopping = findViewById(R.id.cb_shopping)
        cbTravel = findViewById(R.id.cb_travel)

        showCurrentInfo()

        findViewById<MaterialButton>(R.id.btn_save).setOnClickListener {
            saveInfo()
            finish()
        }
        findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener {
            finish()
        }
    }

    private fun showCurrentInfo() {
        etName.setText(PlayerData.name)
        etStudent.setText(PlayerData.studentNumber)
        etPhone.setText(PlayerData.phone)
        etEmail.setText(PlayerData.email)
        rbMale.isChecked = PlayerData.gender == getString(R.string.gender_male)
        rbFemale.isChecked = PlayerData.gender == getString(R.string.gender_female)
        cbReading.isChecked = PlayerData.hobbies.contains(getString(R.string.hobby_reading))
        cbGame.isChecked = PlayerData.hobbies.contains(getString(R.string.hobby_game))
        cbShopping.isChecked = PlayerData.hobbies.contains(getString(R.string.hobby_shopping))
        cbTravel.isChecked = PlayerData.hobbies.contains(getString(R.string.hobby_travel))
    }

    private fun saveInfo() {
        PlayerData.name = etName.text.toString().trim()
        PlayerData.studentNumber = etStudent.text.toString().trim()
        PlayerData.phone = etPhone.text.toString().trim()
        PlayerData.email = etEmail.text.toString().trim()
        PlayerData.gender = if (rbFemale.isChecked) {
            getString(R.string.gender_female)
        } else {
            getString(R.string.gender_male)
        }
        PlayerData.hobbies = mutableListOf<String>().apply {
            if (cbReading.isChecked) add(getString(R.string.hobby_reading))
            if (cbGame.isChecked) add(getString(R.string.hobby_game))
            if (cbShopping.isChecked) add(getString(R.string.hobby_shopping))
            if (cbTravel.isChecked) add(getString(R.string.hobby_travel))
        }
    }
}
