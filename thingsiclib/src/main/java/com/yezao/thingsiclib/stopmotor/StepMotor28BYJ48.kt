package com.yezao.thingsiclib.stopmotor


/**
 *  减速步进电机
 * */
class StepMotor28BYJ48(val driver: Driver) {
    /**
     * 驱动方式  1、单相四拍 A B C D
     * 2、四拍  AB BC CD DA
     * 3、8拍  A AB B BC C CD D DA
     *
     * * */
    enum class DiriverType {
        SINGLE, FOUR, EIGHT
    }
    /**
     * 驱动 接口方式 直接管脚 驱动
     *
     * */
    enum class DirverPioMode{

    }





}