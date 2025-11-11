package org.firstinspires.ftc.teamcode.teleop

import android.annotation.SuppressLint
import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.HeadingInterpolator
import com.pedropathing.paths.Path
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.Utility
import org.firstinspires.ftc.teamcode.pedroPathing.Constants.Companion.createFollower
import org.firstinspires.ftc.teamcode.util.Button
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import java.lang.Thread.sleep
import java.util.function.Supplier
import kotlin.text.*

enum class ObeliskStates {
    GPP,
    PGP,
    PPG
}

enum class CarouselStates {
    ONE,
    TWO,
    THREE
}

enum class LiftStages {
    ZERO,
    ONE,
    TWO,
    THREE,
    FOUR
}

@Configurable
@TeleOp
abstract class Inheritable : OpMode() {
    protected var follower: Follower? = null
    protected var automatedDrive = false
    private var pathChain: Supplier<PathChain>? = null
    lateinit var panelsTelemetry: TelemetryManager
    private var slowMode = false
    private var slowModeMultiplier = 0.5

    private lateinit var leftIntake: CRServo
    private lateinit var rightIntake: CRServo
    lateinit var carousel: Servo
    lateinit var plunger: Servo
    lateinit var hood: Servo

    lateinit var leftLift: DcMotorEx
    lateinit var rightLift: DcMotorEx
    private lateinit var flywheel: DcMotorEx

    protected lateinit var carouselState: CarouselStates

    private var intakeRunning: Boolean = false
    private var flywheelRunning: Boolean = false
    private var hoodUp: Boolean = false

    private lateinit var processor: AprilTagProcessor
    private lateinit var portal: VisionPortal

    private var liftState = LiftStages.ZERO

    val a = Button()
    val b = Button()
    val y = Button()
    val x = Button()
    val up = Button()
    val rightBumper = Button()
    val rightTrigger = Button()
    val leftBumper = Button()

    lateinit var obeliskState: ObeliskStates

    override fun init() {
        leftIntake = hardwareMap.get(CRServo::class.java, "leftIntake")
        rightIntake = hardwareMap.get(CRServo::class.java, "rightIntake")
        carousel = hardwareMap.get(Servo::class.java, "carousel")
        plunger = hardwareMap.get(Servo::class.java, "plunger")
        hood = hardwareMap.get(Servo::class.java, "hood")

        leftLift = hardwareMap.get(DcMotorEx::class.java, "leftLift")
        rightLift = hardwareMap.get(DcMotorEx::class.java, "rightLift")
        flywheel = hardwareMap.get(DcMotorEx::class.java, "flywheel")

        carouselState = CarouselStates.ONE

        follower = createFollower(hardwareMap)
        follower!!.setStartingPose(if (startingPose == null) Pose() else startingPose)
        follower!!.update()
        initializeProcessor()
        panelsTelemetry = PanelsTelemetry.telemetry
        pathChain = Supplier {
            follower!!.pathBuilder()
                .addPath(Path(BezierLine({ follower!!.pose }, Pose(45.0, 98.0))))
                .setHeadingInterpolation(
                    HeadingInterpolator.linearFromPoint(
                        { follower!!.heading },
                        Math.toRadians(45.0),
                        0.8
                    )
                )
                .build()
        }
    }

    override fun start() {
        follower!!.startTeleopDrive(true)
        carousel.position = 0.005
        carouselState = CarouselStates.ONE
        plunger.direction = Servo.Direction.REVERSE
        plunger.position = 0.02
        leftLift.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        rightLift.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        hood.position = 1.0
        hood.direction = Servo.Direction.REVERSE

        leftLift.mode = DcMotor.RunMode.RUN_USING_ENCODER
        rightLift.mode = DcMotor.RunMode.RUN_USING_ENCODER

        rightLift.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    companion object {
        var startingPose: Pose? = null
    }

    fun drive() {
        if (!slowMode) follower!!.setTeleOpDrive(
            -gamepad1.left_stick_y.toDouble(),
            -gamepad1.left_stick_x.toDouble(),
            -gamepad1.right_stick_x.toDouble(),
            true
        )
        else follower!!.setTeleOpDrive(
            -gamepad1.left_stick_y * slowModeMultiplier,
            -gamepad1.left_stick_x * slowModeMultiplier,
            -gamepad1.right_stick_x * slowModeMultiplier,
            true
        )
    }

    fun intake(button: Button) {
        if (button.`is`(Button.States.TAP)) {
            if (!intakeRunning) carousel.position = 0.02
            intakeRunning = !intakeRunning
        }
        if (intakeRunning) {
            leftIntake.power = -1.0
            rightIntake.power = 1.0
        } else {
            leftIntake.power = 0.0
            rightIntake.power = 0.0
        }
    }

    fun hood(button: Button) {
        if (button.`is`(Button.States.TAP)) {
            if (!hoodUp) {
                hood.position = 0.07
                hoodUp = true
            } else {
                hood.position = 0.0
                hoodUp = false
            }
        }
    }

    fun carousel(primary: Button, secondary: Button, tertiary: Button) {
        if (primary.`is`(Button.States.TAP)) {
            carousel.position = Utility.Constants.DOUBLE_ROTATION_CAROUSEL
            carouselState = CarouselStates.THREE
        }
        if (secondary.`is`(Button.States.TAP)) {
            carousel.position = Utility.Constants.SINGLE_ROTATION_CAROUSEL
            carouselState = CarouselStates.TWO
        }
        if (tertiary.`is`(Button.States.TAP)) {
            carousel.position = 0.02
            carouselState = CarouselStates.ONE
        }
    }

    fun plunger(button: Button) {
        if (button.`is`(Button.States.TAP)) {
            plungerMotion()
        }
    }

    private fun plungerMotion() {
        plunger.position = 0.33
        sleep(350)
        plunger.position = 0.0
    }

    fun lift() {
        log("lift state", liftState)
        if (liftState == LiftStages.ZERO) {
            if (leftLift.currentPosition >= -Utility.Constants.LIFT_STAGE_ONE) leftLift.power =
                gamepad2.left_stick_y.toDouble()
            else leftLift.power = 0.0
            if (rightLift.currentPosition <= Utility.Constants.LIFT_STAGE_ONE) rightLift.power =
                -gamepad2.left_stick_y.toDouble()
            else rightLift.power = 0.0
            if (leftLift.currentPosition <= -Utility.Constants.LIFT_STAGE_ONE && rightLift.currentPosition >= Utility.Constants.LIFT_STAGE_ONE) liftState =
                LiftStages.ONE
        } else if (liftState == LiftStages.ONE) {
            if (leftLift.currentPosition >= -Utility.Constants.LIFT_STAGE_TWO) leftLift.power =
                gamepad2.left_stick_y.toDouble()
            else leftLift.power = 0.0
            if (rightLift.currentPosition <= Utility.Constants.LIFT_STAGE_TWO) rightLift.power =
                -gamepad2.left_stick_y.toDouble()
            else rightLift.power = 0.0
            if (leftLift.currentPosition <= -Utility.Constants.LIFT_STAGE_TWO && rightLift.currentPosition >= Utility.Constants.LIFT_STAGE_TWO) liftState =
                LiftStages.TWO
        } else if (liftState == LiftStages.TWO) {
            if (leftLift.currentPosition >= -Utility.Constants.LIFT_STAGE_THREE) leftLift.power =
                gamepad2.left_stick_y.toDouble()
            else leftLift.power = 0.0
            if (rightLift.currentPosition <= Utility.Constants.LIFT_STAGE_THREE) rightLift.power =
                -gamepad2.left_stick_y.toDouble()
            else rightLift.power = 0.0
            if (leftLift.currentPosition <= -Utility.Constants.LIFT_STAGE_THREE && rightLift.currentPosition >= Utility.Constants.LIFT_STAGE_THREE) liftState =
                LiftStages.THREE
        } else if (liftState == LiftStages.THREE) {
            if (leftLift.currentPosition >= -Utility.Constants.LIFT_STAGE_FOUR) leftLift.power =
                gamepad2.left_stick_y.toDouble()
            else leftLift.power = 0.0
            if (rightLift.currentPosition <= Utility.Constants.LIFT_STAGE_FOUR) rightLift.power =
                -gamepad2.left_stick_y.toDouble()
            else rightLift.power = 0.0
            if (leftLift.currentPosition <= -Utility.Constants.LIFT_STAGE_FOUR && rightLift.currentPosition >= Utility.Constants.LIFT_STAGE_FOUR) liftState =
                LiftStages.FOUR
        }
    }

    fun flywheel(button: Button) {
        if (button.`is`(Button.States.TAP)) flywheelRunning = !flywheelRunning

        if (flywheelRunning) flywheel.power = -0.65
        else flywheel.power = 0.0
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

    fun fullCycle(button: Button) {
        if (button.`is`(Button.States.TAP)) {
            flywheelRunning = true
            plungerMotion()
            sleep(300)
            carousel.position = Utility.Constants.SINGLE_ROTATION_CAROUSEL
            sleep(200)
            plungerMotion()
            sleep(300)
            carousel.position = Utility.Constants.DOUBLE_ROTATION_CAROUSEL
            sleep(200)
            plungerMotion()
            sleep(300)
            carousel.position = 0.02
            flywheelRunning = false
        }
    }

    fun updateButtons() {
        a.update(gamepad2.a)
        b.update(gamepad2.b)
        x.update(gamepad2.x)
        y.update(gamepad2.y)
        up.update(gamepad2.dpad_up)
        rightBumper.update(gamepad2.right_bumper)
        rightTrigger.update(gamepad2.right_trigger > 0.1)
        leftBumper.update(gamepad2.left_bumper)
    }

    private fun initializeProcessor() {
        processor = AprilTagProcessor.Builder().build()
        val builder = VisionPortal.Builder()

        builder.setCamera(hardwareMap.get(WebcamName::class.java, "webcam"))
        builder.addProcessor(processor)
        portal = builder.build()
    }

    @SuppressLint("DefaultLocale")
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
}