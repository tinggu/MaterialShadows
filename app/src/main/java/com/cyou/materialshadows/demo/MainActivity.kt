package com.cyou.materialshadows.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_re_render.setOnClickListener {
            shadow_wrapper.shouldCalculateAsync = cb_calculate_async.isChecked
            shadow_wrapper.shouldShowWhenAllReady = cb_show_when_all_ready.isChecked
            shadow_wrapper.requestLayout()
        }
    }
}
