package cn.edu.sicnu.cs.lyx.android4

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvStudent: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvHobby: TextView
    private lateinit var tvHighScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerData.ensureInitialized(this)
        setContentView(R.layout.activity_main)

        tvName = findViewById(R.id.tv_name)
        tvGender = findViewById(R.id.tv_gender)
        tvStudent = findViewById(R.id.tv_student)
        tvPhone = findViewById(R.id.tv_phone)
        tvEmail = findViewById(R.id.tv_email)
        tvHobby = findViewById(R.id.tv_hobby)
        tvHighScore = findViewById(R.id.tv_high_score)

        findViewById<MaterialButton>(R.id.btn_edit_info).setOnClickListener {
            startActivity(Intent(this, InfoEditActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btn_play_game).setOnClickListener {
            startActivity(Intent(this, CardGameActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshInfo()
    }


    private fun refreshInfo() {
        tvName.text = PlayerData.name
        tvGender.text = PlayerData.gender
        tvStudent.text = PlayerData.studentNumber
        tvPhone.text = PlayerData.phone
        tvEmail.text = PlayerData.email
        tvHobby.text = PlayerData.hobbies.joinToString(getString(R.string.hobby_separator))
        tvHighScore.text = PlayerData.highScore.toString()
    }
}
