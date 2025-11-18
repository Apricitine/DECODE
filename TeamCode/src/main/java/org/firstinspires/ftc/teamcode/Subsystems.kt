package org.firstinspires.ftc.teamcode

import android.hardware.Sensor
import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.pedropathing.follower.Follower
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.CompassSensor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.NormalizedColorSensor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcontroller.external.samples.SensorColor
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor

abstract class Subsystems : OpMode() {
    enum class COLORS {
        NONE, PURPLE, GREEN
    }

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

    lateinit var frontSensor: NormalizedColorSensor
    lateinit var rightSensor: NormalizedColorSensor
    lateinit var leftSensor: NormalizedColorSensor

    var frontColor = COLORS.NONE
    var rightColor = COLORS.NONE
    var leftColor = COLORS.NONE

    fun initializeSubsystems() {
        leftIntake = hardwareMap.get(CRServo::class.java, "leftIntake")
        rightIntake = hardwareMap.get(CRServo::class.java, "rightIntake")
        carousel = hardwareMap.get(Servo::class.java, "carousel")
        plunger = hardwareMap.get(Servo::class.java, "plunger")
        hood = hardwareMap.get(Servo::class.java, "hood")

        leftLift = hardwareMap.get(DcMotorEx::class.java, "leftLift")
        rightLift = hardwareMap.get(DcMotorEx::class.java, "rightLift")
        flywheel = hardwareMap.get(DcMotorEx::class.java, "flywheel")

        frontSensor = hardwareMap.get(NormalizedColorSensor::class.java, "frontSensor")
        rightSensor = hardwareMap.get(NormalizedColorSensor::class.java, "rightSensor")
        leftSensor = hardwareMap.get(NormalizedColorSensor::class.java, "leftSensor")

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

    fun identifyColor(sensor: NormalizedColorSensor): COLORS {
        log("average", ((sensor.normalizedColors.red + sensor.normalizedColors.green + sensor.normalizedColors.blue) / 3))
        if (((sensor.normalizedColors.red + sensor.normalizedColors.green + sensor.normalizedColors.blue) / 3) < 0.004) {
            log("color found")
            return if (sensor.normalizedColors.green / sensor.normalizedColors.red > sensor.normalizedColors.blue / sensor.normalizedColors.red) COLORS.GREEN
            else if (sensor.normalizedColors.green / sensor.normalizedColors.red < sensor.normalizedColors.blue / sensor.normalizedColors.red) COLORS.PURPLE
            else COLORS.NONE
        } else {
            return COLORS.NONE
        }
    }
}