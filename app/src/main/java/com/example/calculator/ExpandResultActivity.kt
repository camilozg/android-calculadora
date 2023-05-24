package com.example.calculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.github.ajalt.timberkt.d

class ExpandResultActivity : AppCompatActivity() {
    companion object{
        const val RESULT = "result_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expand_result)

        var result = intent.getStringExtra(RESULT)

        findViewById<TextView>(R.id.expandedResultTextView).text = result

        findViewById<Button>(R.id.shareButton).setOnClickListener{shareResult(result)}
    }

    private fun shareResult(result: String?){
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, result)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
    }
}