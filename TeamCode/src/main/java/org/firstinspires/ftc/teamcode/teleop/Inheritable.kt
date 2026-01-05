package org.firstinspires.ftc.teamcode.teleop

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.Subsystems
import org.firstinspires.ftc.teamcode.Utility
import org.firstinspires.ftc.teamcode.Utility.Constants.Companion.LIFT_STAGE_FOUR
import org.firstinspires.ftc.teamcode.Utility.Constants.Companion.LIFT_STAGE_ONE
import org.firstinspires.ftc.teamcode.Utility.Constants.Companion.LIFT_STAGE_THREE
import org.firstinspires.ftc.teamcode.Utility.Constants.Companion.LIFT_STAGE_TWO
import org.firstinspires.ftc.teamcode.pedroPathing.Constants.Companion.createFollower
import org.firstinspires.ftc.teamcode.util.Button
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc
import java.lang.Thread.sleep
import kotlin.math.atan2

enum class LiftStates {
    ZERO, ONE, TWO, THREE
}

@Configurable
@TeleOp
abstract class Inheritable : Subsystems() {
    protected var carouselState: CarouselStates = CarouselStates.FRONT
    private var intakeRunning: Boolean = false
    private var intakeReverseRunning: Boolean = false
    private var plungerBusy: Boolean = false
    private var flywheelRunning: Boolean = false
    private var liftState = LiftStates.ZERO
    private var lifting = false

    private var goalTagPose: AprilTagPoseFtc? = null

    val a = Button()
    val b = Button()
    val y = Button()
    val x = Button()
    val up = Button()
    val right = Button()
    val left = Button()
    val down = Button()
    val rightBumper = Button()
    val rightTrigger = Button()
    val leftBumper = Button()
    val leftTrigger = Button()
    val leftStick = Button()
    val rightStick = Button()

    override fun init() {
        initializeSubsystems()
        follower = createFollower(hardwareMap)
        follower.update()
        initializeProcessor()

        rightBaseline = calibrate(rightSensor)
        leftBaseline = calibrate(leftSensor)
        frontBaseline = calibrate(frontSensor)
    }

    override fun start() {
        follower.startTeleopDrive(true)
        carousel.position = Utility.Constants.BASE
        carouselState = CarouselStates.FRONT
        plunger.direction = Servo.Direction.REVERSE
        plunger.position = 0.02
        leftLift.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        rightLift.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

        rightLift.direction = DcMotorSimple.Direction.REVERSE

        leftLift.mode = DcMotor.RunMode.RUN_TO_POSITION
        rightLift.mode = DcMotor.RunMode.RUN_TO_POSITION

        rightLift.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        flywheel.setPIDFCoefficients(
            DcMotor.RunMode.RUN_USING_ENCODER, PIDFCoefficients(40.0, 0.0, 0.0, 15.0)
        )

        flywheel.mode = DcMotor.RunMode.RUN_USING_ENCODER
        flywheel.direction = DcMotorSimple.Direction.REVERSE
    }

    override fun loop() {
        updateTagPose()
        updateButtons()
        follower.update()
        panelsTelemetry.update()
    }

    fun drive(power: Double) {
        follower.setTeleOpDrive(
            -gamepad1.left_stick_y * power,
            -gamepad1.left_stick_x * power,
            -gamepad1.right_stick_x * power,
            true
        )
    }

    fun intake(button: Button, reverseButton: Button) {
        if (button.`is`(Button.States.TAP)) {
            carousel.position = Utility.Constants.BASE
            intakeRunning = !intakeRunning
            intakeReverseRunning = false
        }

        if (reverseButton.`is`(Button.States.TAP)) {
            carousel.position = Utility.Constants.BASE
            intakeReverseRunning = !intakeReverseRunning
            intakeRunning = false
        }

        if (intakeRunning) {
            leftIntake.power = -1.0
            rightIntake.power = 1.0
        } else if (intakeReverseRunning) {
            leftIntake.power = 1.0
            rightIntake.power = -1.0
        } else {
            leftIntake.power = 0.0
            rightIntake.power = 0.0
        }
    }
    fun carousel(front: Button, right: Button, left: Button) {
        if (!plungerBusy) {
            if (front.`is`(Button.States.TAP)) {
                carousel.position = Utility.Constants.DOUBLE_ROTATION_CAROUSEL
                carouselState = CarouselStates.LEFT
            }
            if (right.`is`(Button.States.TAP)) {
                carousel.position = Utility.Constants.SINGLE_ROTATION_CAROUSEL
                carouselState = CarouselStates.RIGHT
            }
            if (left.`is`(Button.States.TAP)) {
                carousel.position = 0.02
                carouselState = CarouselStates.FRONT
            }
        }
    }
    fun plunger(button: Button) {
        if (button.`is`(Button.States.TAP)) {
            plungerMotion()
        }
    }
    fun lift(button: Button) {
        if (button.`is`(Button.States.TAP)) lifting = true
        if (!lifting) return

        val targets = intArrayOf(
            LIFT_STAGE_ONE,
            LIFT_STAGE_TWO,
            LIFT_STAGE_THREE,
            LIFT_STAGE_FOUR
        )

        leftLift.targetPosition = targets[liftState.ordinal]
        rightLift.targetPosition = targets[liftState.ordinal]
        leftLift.power = 0.4
        rightLift.power = 0.4

        if (liftState.ordinal < targets.lastIndex && !leftLift.isBusy && !rightLift.isBusy)
            liftState = LiftStates.entries.toTypedArray()[liftState.ordinal + 1]
    }
    fun flywheel(button: Button) {
        if (button.`is`(Button.States.TAP)) flywheelRunning = !flywheelRunning

        if (flywheelRunning) goalTagPose?.let {
            flywheel.velocity = getTicksPerSecond { it.y - 2.0 }
        }
        else flywheel.velocity = 0.0
    }

    private fun plungerMotion() {
        plungerBusy = true
        plunger.position = 0.33
        sleep(350)
        plunger.position = 0.0
        plungerBusy = false
    }

    fun colorShot(greenButton: Button, purpleButton: Button) {
        if (greenButton.`is`(Button.States.TAP)) {
            updateColors()
            if (frontColor == COLORS.GREEN) {
                carousel.position = Utility.Constants.BASE
                sleep(300)
                plungerMotion()
            } else if (rightColor == COLORS.GREEN) {
                carousel.position = Utility.Constants.SINGLE_ROTATION_CAROUSEL
                sleep(300)
                plungerMotion()
                sleep(300)
                carousel.position = Utility.Constants.BASE
            } else if (leftColor == COLORS.GREEN) {
                carousel.position = Utility.Constants.DOUBLE_ROTATION_CAROUSEL
                sleep(300)
                plungerMotion()
                sleep(300)
                carousel.position = Utility.Constants.BASE
            }
        }
        if (purpleButton.`is`(Button.States.TAP)) {
            updateColors()
            if (frontColor == COLORS.PURPLE) {
                carousel.position = Utility.Constants.BASE
                sleep(300)
                plungerMotion()
            } else if (rightColor == COLORS.PURPLE) {
                carousel.position = Utility.Constants.SINGLE_ROTATION_CAROUSEL
                sleep(300)
                plungerMotion()
                sleep(300)
                carousel.position = Utility.Constants.BASE
            } else if (leftColor == COLORS.PURPLE) {
                carousel.position = Utility.Constants.DOUBLE_ROTATION_CAROUSEL
                sleep(300)
                plungerMotion()
                sleep(300)
                carousel.position = Utility.Constants.BASE
            }
        }
    }
    fun quickShot(button: Button) {
        if (button.`is`(Button.States.TAP)) {
            // flywheel
            plungerMotion()
            sleep(300)
            carousel.position = Utility.Constants.SINGLE_ROTATION_CAROUSEL
            sleep(300)
            plungerMotion()
            sleep(300)
            carousel.position = Utility.Constants.DOUBLE_ROTATION_CAROUSEL
            sleep(300)
            plungerMotion()
            sleep(300)
            carousel.position = 0.02
            // flywheel
        }
    }
    fun align(button: Button) {
        if (!follower.isBusy) follower.startTeleopDrive()
        if (button.`is`(Button.States.TAP)) {
            goalTagPose?.let { follower.turn(atan2(it.x, it.y), false) }
        }
    }

    fun updateTagPose() {
        goalTagPose = processor.detections.firstOrNull {
            it.metadata != null && it.id !in 21..23
        }?.ftcPose
    }
    fun updateButtons() {
        a.update(gamepad2.a)
        b.update(gamepad2.b)
        x.update(gamepad2.x)
        y.update(gamepad2.y)
        up.update(gamepad2.dpad_up)
        right.update(gamepad2.dpad_right)
        left.update(gamepad2.dpad_left)
        down.update(gamepad2.dpad_down)
        rightBumper.update(gamepad2.right_bumper)

        rightTrigger.update(gamepad2.right_trigger > 0.1)
        leftBumper.update(gamepad2.left_bumper)
        leftTrigger.update(gamepad2.left_trigger > 0.1)
        leftStick.update(gamepad2.left_stick_button)
        rightStick.update(gamepad2.right_stick_button)

    }
}