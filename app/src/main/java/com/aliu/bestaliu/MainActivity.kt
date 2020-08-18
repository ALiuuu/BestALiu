package com.aliu.bestaliu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.aliu.myutils.MarketManager
import com.example.bestaliu.R

class MainActivity : FragmentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    findViewById<View>(R.id.bt_email).setOnClickListener {
      MarketManager.doFeedBackByEmail(this@MainActivity, null, "bestTitle", "bestHint")
    }
  }
}