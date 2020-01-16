package com.example.greencitylife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_choose_garden.*

class chooseGarden : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_garden)
        a_choose_garden_tv_welcome.text = "Hello New Gardener! Please choose a nickname and\n" +
                                          "create a new garden or enter an existing one."

    }
}
