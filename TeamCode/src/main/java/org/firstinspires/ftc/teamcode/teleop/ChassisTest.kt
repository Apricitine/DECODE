package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import kotlin.math.abs
import kotlin.math.max

@TeleOp(name="Chassis Test", group="Test")
class ChassisTest : OpMode() {
    lateinit var leftFront: DcMotorEx
    lateinit var leftRear: DcMotorEx
    lateinit var rightFront: DcMotorEx
    lateinit var rightRear: DcMotorEx


    override fun init() {
        leftFront = hardwareMap.get(DcMotorEx::class.java, "leftFront")
        leftRear = hardwareMap.get(DcMotorEx::class.java, "leftRear")
        rightFront = hardwareMap.get(DcMotorEx::class.java, "rightFront")
        rightRear = hardwareMap.get(DcMotorEx::class.java, "rightRear")
    }

    override fun loop() {
        val y = -gamepad1.left_stick_y.toDouble()
        val x = gamepad1.left_stick_x * 1.1
        val rx = gamepad1.right_stick_x.toDouble()

        val denominator = max(abs(y) + abs(x) + abs(rx), 1.0)
        val leftFrontPower = (y + x + rx) / denominator
        val leftRearPower = (y - x + rx) / denominator
        val rightFrontPower = (y - x - rx) / denominator
        val rightRearPower = (y + x - rx) / denominator

        leftFront.power = leftFrontPower
        leftRear.power = leftRearPower
        rightFront.power = rightFrontPower
        rightRear.power = rightRearPower
    }

}