package com.example.android.clippingexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**  replace the default content view and set the content view to a new instance of ClippedView.
 * This will be your custom view for the clipping examples that you will create next.
 * */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ClippedView(this))
    }
}