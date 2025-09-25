package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.abs
import kotlin.math.pow

class Drive : OpMode() {
    val leftFront = hardwareMap.dcMotor.get("leftFront")
    val leftRear = hardwareMap.dcMotor.get("leftRear")
    val rightFront = hardwareMap.dcMotor.get("rightFront")
    val rightRear = hardwareMap.dcMotor.get("rightRear")

    override fun init() {
        leftFront.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        leftRear.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rightFront.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rightRear.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        leftFront.direction = DcMotorSimple.Direction.REVERSE
        leftRear.direction = DcMotorSimple.Direction.REVERSE
        rightFront.direction = DcMotorSimple.Direction.FORWARD
        rightRear.direction = DcMotorSimple.Direction.FORWARD
    }

    override fun loop() {
        drive(1.0)
    }

    fun drive(power: Double) {
        var directionX = 0.0
        var directionY = 0.0
        var directionR = 0.0

        if (abs(gamepad1.left_stick_x.toDouble()) > 0.25) directionX =
            gamepad1.left_stick_x.pow(1).toDouble()
        if (abs(gamepad1.left_stick_y.toDouble()) > 0.25) directionY =
            -gamepad1.left_stick_y.pow(1).toDouble()
        if (abs(gamepad1.right_stick_x.toDouble()) > 0.25) directionR =
            gamepad1.right_stick_x.pow(1).toDouble()

        val leftFrontPower: Double = (directionX + directionY + directionR) * power
        val leftBackPower: Double = (-directionX + directionY + directionR) * power
        val rightFrontPower: Double = (-directionX + directionY - directionR) * power
        val rightBackPower: Double = (directionX + directionY - directionR) * power

        leftFront.power = leftFrontPower
        leftRear.power = leftBackPower
        rightFront.power = rightFrontPower
        rightRear.power = rightBackPower
    }
}