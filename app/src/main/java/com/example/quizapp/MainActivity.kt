package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gridLayout = findViewById<GridLayout>(R.id.quizGrid)
        val dbHelper = QuizDbHelper(this)

        // Create 12 buttons dynamically for quizzes 1 to 12
        for (i in 1..12) {
            val button = Button(this)
            val quizNumber = i.toString()
            val score = dbHelper.getQuizResult(quizNumber)
            button.text = if (score != null) "$quizNumber ($score)" else quizNumber
            button.textSize = 24f
            button.setBackgroundResource(R.drawable.quiz_button_background)
            button.setOnClickListener {
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra("quizNumber", i)
                startActivity(intent)
            }
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.setMargins(16, 16, 16, 16)
            button.layoutParams = params
            gridLayout.addView(button)
        }
    }
}
