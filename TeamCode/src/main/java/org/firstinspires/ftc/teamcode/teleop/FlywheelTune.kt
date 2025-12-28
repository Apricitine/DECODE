package org.firstinspires.ftc.teamcode.teleop

import com.pedropathing.control.PIDFCoefficients
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.util.Button

class FlywheelTune : Inheritable() {

    // rotations per minute (needs to be updated)
    val highVelocity = 1000
    val lowVelocity = 500

    var currentTargetVelocity = highVelocity

    object Coefficients {
        val P: Double = 0.0
        val F: Double = 0.0
    }

    val stepSizes = listOf(10, 1, 0.1, 0.001, 0.0001)
    val stepIndex = 1

    override fun start() {
        super.start()
        val pidfCoefficients = com.qualcomm.robotcore.hardware.PIDFCoefficients(
            Coefficients.P, 0.0, 0.0,
            Coefficients.F
        )

        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients)
    }

    override fun loop() {
        if (a.`is`(Button.States.TAP)) {

        }
    }
}