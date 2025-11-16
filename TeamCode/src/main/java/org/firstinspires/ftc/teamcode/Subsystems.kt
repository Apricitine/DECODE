package org.firstinspires.ftc.teamcode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.pedropathing.follower.Follower
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor

abstract class Subsystems : OpMode() {
    lateinit var leftIntake: CRServo
    lateinit var rightIntake: CRServo
    lateinit var carousel: Servo
    lateinit var plunger: Servo
    lateinit var hood: Servo
    lateinit var leftLift: DcMotorEx
    lateinit var rightLift: DcMotorEx
    lateinit var flywheel: DcMotorEx

    lateinit var panelsTelemetry: TelemetryManager
    lateinit var processor: AprilTagProcessor
    lateinit var portal: VisionPortal
    lateinit var follower: Follower

    fun initializeSubsystems() {
        leftIntake = hardwareMap.get(CRServo::class.java, "leftIntake")
        rightIntake = hardwareMap.get(CRServo::class.java, "rightIntake")
        carousel = hardwareMap.get(Servo::class.java, "carousel")
        plunger = hardwareMap.get(Servo::class.java, "plunger")
        hood = hardwareMap.get(Servo::class.java, "hood")

        leftLift = hardwareMap.get(DcMotorEx::class.java, "leftLift")
        rightLift = hardwareMap.get(DcMotorEx::class.java, "rightLift")
        flywheel = hardwareMap.get(DcMotorEx::class.java, "flywheel")

        panelsTelemetry = PanelsTelemetry.telemetry
    }

    fun initializeProcessor() {
        processor = AprilTagProcessor.Builder().build()
        val builder = VisionPortal.Builder()

        builder.setCamera(hardwareMap.get(WebcamName::class.java, "webcam"))
        builder.addProcessor(processor)
        portal = builder.build()
    }

    fun log(caption: String, vararg text: Any) {
        if (text.size == 1) {
            telemetry.addData(caption, text[0])
            panelsTelemetry.debug(caption + ": " + text[0])
        } else if (text.size >= 2) {
            val message = StringBuilder()
            for (i in text.indices) {
                message.append(text[i])
                if (i < text.size - 1) message.append(" ")
            }
            telemetry.addData(caption, message.toString())
            panelsTelemetry.debug("$caption: $message")
        }
    }
}