package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap

abstract class Subsystems : OpMode() {
    lateinit var leftIntake: CRServo
    lateinit var rightIntake: CRServo
    lateinit var carousel: Servo
    lateinit var plunger: Servo
    lateinit var hood: Servo
    lateinit var leftLift: DcMotorEx
    lateinit var rightLift: DcMotorEx
    lateinit var flywheel: DcMotorEx

    fun initializeSubsystems() {
        leftIntake = hardwareMap.get(CRServo::class.java, "leftIntake")
        rightIntake = hardwareMap.get(CRServo::class.java, "rightIntake")
        carousel = hardwareMap.get(Servo::class.java, "carousel")
        plunger = hardwareMap.get(Servo::class.java, "plunger")
        hood = hardwareMap.get(Servo::class.java, "hood")

        leftLift = hardwareMap.get(DcMotorEx::class.java, "leftLift")
        rightLift = hardwareMap.get(DcMotorEx::class.java, "rightLift")
        flywheel = hardwareMap.get(DcMotorEx::class.java, "flywheel")
    }
}