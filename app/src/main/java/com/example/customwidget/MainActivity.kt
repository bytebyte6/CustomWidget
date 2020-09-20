package com.example.customwidget

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.widget.SleepData
import com.example.widget.SleepView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val sleepView by lazy {
        findViewById<SleepView>(R.id.sleepView)
    }

    val r = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = LogView(this)

        add.setOnClickListener {
            root.removeView(view)
            root.addView(view)
        }

        del.setOnClickListener {
            root.removeView(view)
        }

        button.setOnClickListener {

            batteryView.setPower(r.nextFloat())

            one.setStartColor(getColor(), 0)

            two.setStartColor(getColor(), 0)

            three.setStartColor(getColor(), 0)

            progress.setColor(intArrayOf(getColor(), getColor(), getColor(), getColor()))

            progress.setIndicatorColor(getColor())

            progress.setProcess(50f)

            load()
        }

        load()
    }

    private fun getColor(): Int {
        return Color.argb(r.nextInt(255), r.nextInt(255), r.nextInt(255), r.nextInt(255))
    }

    private fun load() {

        val data = mutableListOf<SleepData>()


        for (i in 0..5) {
            val d = mutableListOf<Int>()
            val q = mutableListOf<Int>()
            for (k in 0..5) {
                d.add(r.nextInt(1000))
                q.add(r.nextInt(16))
            }
            val sleepData = SleepData(d, q)
            data.add(sleepData)
        }

        for (datum in data) {
            Log.d("TAG", "load: " + datum.durations)
            Log.d("TAG", "load: " + datum.qualitys)
        }

        sleepView.addRectF(data)
    }
}