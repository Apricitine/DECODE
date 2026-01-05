package org.firstinspires.ftc.teamcode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.pedropathing.follower.Follower
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.NormalizedColorSensor
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.Utility.Constants.Companion.MIN_TICKS_PER_SECOND
import org.firstinspires.ftc.teamcode.Utility.Constants.Companion.TICKS_PER_SECOND_PER_INCH
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import java.lang.Math.toDegrees
import kotlin.math.atan2

abstract class Subsystems : OpMode() {

    lateinit var timeSinceLastColorUpdate: ElapsedTime

    protected var obeliskState: ObeliskStates = ObeliskStates.NONE

    lateinit var leftIntake: CRServo
    lateinit var rightIntake: CRServo
    lateinit var carousel: Servo
    lateinit var plunger: Servo
    lateinit var leftLift: DcMotorEx
    lateinit var rightLift: DcMotorEx
    protected lateinit var flywheel: DcMotorEx

    lateinit var panelsTelemetry: TelemetryManager
    lateinit var processor: AprilTagProcessor
    lateinit var portal: VisionPortal
    lateinit var robot: Follower

    lateinit var frontSensor: NormalizedColorSensor
    lateinit var rightSensor: NormalizedColorSensor
    lateinit var leftSensor: NormalizedColorSensor

    var frontColor = COLORS.NONE
    var rightColor = COLORS.NONE
    var leftColor = COLORS.NONE

    lateinit var rightBaseline: Baseline
    lateinit var leftBaseline: Baseline
    lateinit var frontBaseline: Baseline

    enum class COLORS {
        NONE, PURPLE, GREEN
    }

    enum class ObeliskStates {
        NONE, GPP, PGP, PPG
    }

    enum class CarouselStates {
        FRONT, RIGHT, LEFT
    }

    data class Baseline(val sum: Float, val g: Float, val b: Float)

    fun initializeSubsystems() {
        leftIntake = hardwareMap.get(CRServo::class.java, "leftIntake")
        rightIntake = hardwareMap.get(CRServo::class.java, "rightIntake")
        carousel = hardwareMap.get(Servo::class.java, "carousel")
        plunger = hardwareMap.get(Servo::class.java, "plunger")

        leftLift = hardwareMap.get(DcMotorEx::class.java, "leftLift")
        rightLift = hardwareMap.get(DcMotorEx::class.java, "rightLift")
        flywheel = hardwareMap.get(DcMotorEx::class.java, "flywheel")

        frontSensor = hardwareMap.get(NormalizedColorSensor::class.java, "frontSensor")
        rightSensor = hardwareMap.get(NormalizedColorSensor::class.java, "rightSensor")
        leftSensor = hardwareMap.get(NormalizedColorSensor::class.java, "leftSensor")

        panelsTelemetry = PanelsTelemetry.telemetry
    }

    /**
     * Initializes the AprilTag vision portal.
     * @see obeliskTag
     */
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

    fun calibrate(sensor: NormalizedColorSensor) = (1..30).mapNotNull {
        val colors = sensor.normalizedColors
        val sum = colors.red + colors.green + colors.blue
        if (sum > 0) Triple(sum, colors.green / sum, colors.blue / sum) else null
    }.let { list ->
        val n = list.size.coerceAtLeast(1)
        val avg = list.fold(Triple(0f, 0f, 0f)) { acc, t ->
            Triple(acc.first + t.first, acc.second + t.second, acc.third + t.third)
        }
        Baseline(avg.first / n, avg.second / n, avg.third / n)
    }

    fun identifyColor(sensor: NormalizedColorSensor): COLORS {
        val c = sensor.normalizedColors
        val sum = c.red + c.green + c.blue
        if (sum <= 0f) return COLORS.NONE

        val g = c.green / sum
        val b = c.blue / sum

        val base = when (sensor) {
            rightSensor -> rightBaseline
            leftSensor -> leftBaseline
            else -> frontBaseline
        }

        val brightnessChanged = kotlin.math.abs(sum - base.sum) > base.sum * 0.25f
        val colorSeparated = kotlin.math.abs(g - b) > 0.08f

        if (!brightnessChanged || !colorSeparated) return COLORS.NONE
        return if (g > b) COLORS.GREEN else COLORS.PURPLE
    }

    fun updateColors() {
        timeSinceLastColorUpdate.reset()

        frontColor = identifyColor(frontSensor)
        rightColor = identifyColor(rightSensor)
        leftColor = identifyColor(leftSensor)
    }


    /**
     * This functions runs an AprilTag detection using the camera and looks
     * for the motif on the obelisk. If it finds one, it will set obeliskState
     * to the corresponding motif.
     * @see initializeProcessor
     */
    fun obeliskTag() {
        val detections = processor.detections
        log("tags detected", detections.size)

        for (detection in detections) {
            if (detection.metadata != null) {
                if (detection.id == 21) obeliskState = ObeliskStates.GPP
                if (detection.id == 22) obeliskState = ObeliskStates.PGP
                if (detection.id == 23) obeliskState = ObeliskStates.PPG
                log("tag", obeliskState)
            }
        }
    }

    fun logGoalTagDistance() {
        for (detection in processor.detections) {
            if (detection.id !in 21..23) {
                log("y distance from tag", detection.ftcPose.y)
                log("x distance from tag", detection.ftcPose.x)
                log("yaw from tag", detection.ftcPose.yaw)
                log("angular offset to tag", toDegrees(atan2(detection.ftcPose.x, detection.ftcPose.y)))
            }
        }
    }

    fun getTicksPerSecond(call: () -> Double): Double {
        return call() * TICKS_PER_SECOND_PER_INCH + MIN_TICKS_PER_SECOND
    }
}