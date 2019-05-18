package com.yezao.thingsiclib.stopmotor

import android.os.Handler
import com.google.android.things.pio.Gpio

/**
 * 采用 5 个 IO口直接驱动
 * */
class DiretDriver(val mode: StepMotor28BYJ48.DiriverType, val motor: StepMotor28BYJ48, val pios: ArrayList<Gpio>) :
    Driver, Runnable {

    // A B C D
    private val steps4single = arrayListOf<Array<Boolean>>(
        arrayOf(true, false, false, false),// A
        arrayOf(false, true, false, false),// B
        arrayOf(true, false, true, false),// C
        arrayOf(true, false, false, true)// d
    )
    //"AB", "BC", "CD", "DA"
    private val steps4double = arrayListOf<Array<Boolean>>(
        arrayOf(true, true, false, false),// AB
        arrayOf(false, true, true, false),// BC
        arrayOf(false, false, true, true),// CD
        arrayOf(true, false, false, true)// DA
    )
    //"A", "AB", "B", "BC", "C", "CD", "D", "DA"
    private val steps8 = arrayListOf<Array<Boolean>>(
        arrayOf(true, false, false, false),
        arrayOf(true, true, false, false),
        arrayOf(false, true, false, false),
        arrayOf(false, true, true, false),
        arrayOf(false, false, true, false),
        arrayOf(false, false, true, true),
        arrayOf(false, false, false, true),
        arrayOf(true, false, false, true)
    )
    private val handler = Handler()
    private lateinit var currentStepList: ArrayList<Array<Boolean>>

    private var running = false
    private var currentSpeed = 10
    private var stepTime = 100L //ms 每一步时间间隔 100ms
    private var stepCount = 0 //计步器
    private var stepTotal = 0 //目标步数


    init {
        when (mode) {
            StepMotor28BYJ48.DiriverType.SINGLE -> {
                currentStepList = steps4single
            }
            StepMotor28BYJ48.DiriverType.FOUR -> {
                currentStepList = steps4double
            }
            StepMotor28BYJ48.DiriverType.EIGHT -> {
                currentStepList = steps8
            }
        }
    }

    private fun stopRunning() {
        handler.removeCallbacks(this)
    }


    override fun run() {
        if (running.not()) {
            return
        }
        stepCount++
        if (stepCount == stepTotal) {
            stopRunning()
        }
        //输出pio
        val stepIndex = stepCount % currentStepList.size
        pios[0].value = currentStepList[stepIndex][0]
        pios[1].value = currentStepList[stepIndex][1]
        pios[2].value = currentStepList[stepIndex][2]
        pios[3].value = currentStepList[stepIndex][3]

        //通知进行下一次步进
        handler.postDelayed(this, stepTime)
    }

    override fun setSpeed(stepPerSec: Int) {
        currentSpeed = stepPerSec
        stepTime = 1000L.div(currentSpeed)
    }

    override fun stepForward(steps: Int) {
        if (running) {
            stopRunning()
            running = false
        }
        stepTotal = steps
        handler.post(this)
    }

    override fun stepBackward(steps: Int) {
        if (running) {
            stopRunning()
            running = false
        }
        stepTotal = steps
        handler.post(this)
    }


}