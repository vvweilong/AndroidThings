package com.yezao.thingsiclib

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.things.pio.Gpio

private val TAG = HCSR04::class.java.simpleName
/**
 * 超声波测距模块
 * @param trig 控制引脚
 * @param echo 接收引脚
 * */
class HCSR04(val trig: Gpio, val echo :Gpio){

    var timerecord = 0L//记录高电平持续时间
    var runningFlag = false//计时标志位
    lateinit var handler :Handler
    lateinit var distanceCallback : ResultCallback

    init {

        handler= Handler(Looper.getMainLooper())
        echo.registerGpioCallback {it ->

            //收到 上升沿  跳变
            if (it.value&&!runningFlag) {
                timerecord =System.nanoTime()
                Log.i(TAG, "收到 上升跳变信号 :$timerecord ")
                runningFlag=true
            }
            //收到下降沿跳变
            if (!it.value&&runningFlag){
                //回调监听
                Log.i(TAG, "收到 下降沿跳变信号: ")
                distanceCallback?.onReceivedResult(calculateDistance(System.nanoTime()-timerecord))
                runningFlag=false;
            }

            true
        }
    }


    val soundSpeed  =17 //  厘米/每毫秒
    /**
     * @param nanoTime 纳秒
     * */
    private fun calculateDistance(nanoTime:Long):Float{
        return soundSpeed*((nanoTime.toFloat()).div(1000*1000))
    }
    /**
     * 测距结果监听
     * 返回测距结果
     * */
    interface ResultCallback{
        fun onReceivedResult(centimetre:Float)
    }


    /*开始一次 检测请求 对trig 开启至少10us*/
    fun start(){
        trig.value=true
        handler.postDelayed({
            trig.value = false
        },1)
    }
}