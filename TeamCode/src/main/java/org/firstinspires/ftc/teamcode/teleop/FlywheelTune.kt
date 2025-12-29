package org.firstinspires.ftc.teamcode.teleop

import com.pedropathing.control.PIDFCoefficients
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.util.Button

@TeleOp(name = "Flywheel Tuner", group = "main")
class FlywheelTune : Inheritable() {

    // ticks per second
    val highVelocity = 1920.0
    val lowVelocity = 3900.0

    var currentTargetVelocity = highVelocity

    object Coefficients {
        var P: Double = 0.0
        var F: Double = 0.0
    }

    val stepSizes: List<Double> = listOf(10.0, 1.0, 0.1, 0.001, 0.0001)
    var stepIndex = 1

    override fun start() {
        super.start()
        val pidfCoefficients = com.qualcomm.robotcore.hardware.PIDFCoefficients(
            Coefficients.P, 0.0, 0.0,
            Coefficients.F
        )

        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients)
    }

    override fun loop() {
        super.loop()

        if (a.`is`(Button.States.TAP)) {
            currentTargetVelocity = if (currentTargetVelocity == highVelocity) lowVelocity
            else highVelocity
        }
        if (b.`is`(Button.States.TAP)) {
            stepIndex = (stepIndex + 1) % stepSizes.size
        }

        if (left.`is`(Button.States.TAP)) Coefficients.F -= stepSizes[stepIndex]
        if (right.`is`(Button.States.TAP)) Coefficients.F += stepSizes[stepIndex]

        if (down.`is`(Button.States.TAP)) Coefficients.P -= stepSizes[stepIndex]
        if (up.`is`(Button.States.TAP)) Coefficients.P += stepSizes[stepIndex]

        val pidfCoefficients = com.qualcomm.robotcore.hardware.PIDFCoefficients(
            Coefficients.P,
            0.0,
            0.0,
            Coefficients.F
        )
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients)

        flywheel.velocity = currentTargetVelocity

        val error = currentTargetVelocity - flywheel.velocity

        log("target velocity", currentTargetVelocity)
        log("current velocity", flywheel.velocity)
        log("error", error)
        log("---------------------------------------------")
        log("tuning P", Coefficients.P)
        log("tuning F", Coefficients.F)
        log("step size", stepSizes[stepIndex])


    }
}