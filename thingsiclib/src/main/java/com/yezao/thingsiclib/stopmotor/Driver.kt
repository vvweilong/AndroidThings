package com.yezao.thingsiclib.stopmotor

import com.google.android.things.pio.Gpio

interface Driver {
    /**
     * 设置 速度 步/每秒
     * */
    fun setSpeed(stepPerSec:Int)
    /**
     * @param step 前进step 步
     * */
    fun stepForward(step: Int)

    /**
     * @param step 后退 step 步
     * */
    fun stepBackward(step: Int)

}