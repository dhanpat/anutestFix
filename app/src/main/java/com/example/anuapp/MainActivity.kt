package com.example.anuapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.genai.Client
import com.google.genai.types.GenerateContentResponse
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var etInput: EditText
    private lateinit var btnSearch: Button
    private lateinit var resultContainer: LinearLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout)
        etInput = findViewById(R.id.etInput)
        btnSearch = findViewById(R.id.btnSearch)
        resultContainer = findViewById(R.id.resultContainer)
        progressBar = findViewById(R.id.progressBar)

        val navView = findViewById<NavigationView>(R.id.navigationView)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_search -> {
                    drawerLayout.closeDrawers()
                    resultContainer.scrollTo(0, 0)
                    true
                }
                else -> false
            }
        }

        // Search button click
        btnSearch.setOnClickListener {
            val query = etInput.text.toString().trim()
            if (query.isNotEmpty()) {
                performSearch(query)
            } else {
                Toast.makeText(this, "Enter a word", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performSearch(query: String) {
        // Clear previous results and show loader
        resultContainer.removeAllViews()
        progressBar.visibility = View.VISIBLE

        // Background network call
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prompt = "Translate '$query' to Hindi and English meanings"

                val client = Client.builder()
                    .apiKey("AIzaSyD3u55wGMkJmHHE0z5wVQ_0qyjUe1jg0wY") // Replace with your Gemini API key
                    .build()

                val response: GenerateContentResponse = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null
                )

                val resultText = response.text() ?: "No result"

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    val tv = TextView(this@MainActivity)
                    tv.text = resultText
                    tv.textSize = 18f
                    tv.setPadding(16, 16, 16, 16)
                    resultContainer.addView(tv)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
