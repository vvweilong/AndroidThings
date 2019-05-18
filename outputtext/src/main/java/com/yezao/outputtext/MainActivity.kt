package com.yezao.outputtext

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import java.util.*
import kotlin.collections.ArrayList

import kotlin.concurrent.scheduleAtFixedRate

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
private val TAG = MainActivity::class.java.simpleName

class MainActivity : AppCompatActivity() , com.yezao.thingsiclib.HCSR04.ResultCallback{
    override fun onReceivedResult(distance: Float) {
        Log.i(TAG, "onReceivedResult: 距离 $distance 厘米")
    }


    lateinit var manager: PeripheralManager

    lateinit var gpio: Gpio
    lateinit var gpioList:ArrayList<MyPio>

    val BCM19 ="BCM19"
    val BCM26 ="BCM26"




    lateinit var hcsR04: com.yezao.thingsiclib.HCSR04

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = PeripheralManager.getInstance()
        val gpioNames = manager.gpioList
        gpioList = ArrayList<MyPio>()



    }


    private fun runHCSR04(){
        //使用 GPIO19 作为控制端口  GPIO26 作为接受端口
        val trig = manager.openGpio(BCM19)
        val echo = manager.openGpio(BCM26)

        trig.setActiveType(Gpio.ACTIVE_HIGH)
        echo.setActiveType(Gpio.ACTIVE_HIGH)

        trig.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        echo.setDirection(Gpio.DIRECTION_IN)

        echo.setEdgeTriggerType(Gpio.EDGE_BOTH)

        hcsR04= com.yezao.thingsiclib.HCSR04(trig, echo)
        hcsR04.distanceCallback=this

        timer.scheduleAtFixedRate(1000,2000,action = {
            hcsR04.start()
        })
    }

    val timer = Timer()


    override fun isDestroyed(): Boolean {
        gpio.close()
        return super.isDestroyed()
    }
}
