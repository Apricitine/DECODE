package org.firstinspires.ftc.teamcode.teleop

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.Path
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.Subsystems
import org.firstinspires.ftc.teamcode.Utility
import org.firstinspires.ftc.teamcode.pedroPathing.Constants.Companion.createFollower
import org.firstinspires.ftc.teamcode.util.Button
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc
import java.lang.Thread.sleep

enum class LiftStages {
    ZERO, ONE, TWO, THREE, FOUR
}

@Configurable
@TeleOp
abstract class Inheritable : Subsystems() {
    protected var carouselState: CarouselStates = CarouselStates.FRONT
    private var intakeRunning: Boolean = false
    private var intakeReverseRunning: Boolean = false
    private var flywheelSlowRunning: Boolean = false
    private var flywheelFastRunning: Boolean = false
    private var hoodUp: Boolean = false

    private var plungerBusy: Boolean = false

    private var liftState = LiftStages.ZERO

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

    override fun init() {
        initializeSubsystems()
        follower = createFollower(hardwareMap)
        follower.update()
        initializeProcessor()
    }

    override fun start() {
        follower.startTeleopDrive(true)
        carousel.position = Utility.Constants.BASE
        carouselState = CarouselStates.FRONT
        plunger.direction = Servo.Direction.REVERSE
        plunger.position = 0.02
        leftLift.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        rightLift.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        hood.position = 1.0
        hood.direction = Servo.Direction.REVERSE
        hood.position = 0.07

        leftLift.mode = DcMotor.RunMode.RUN_USING_ENCODER
        rightLift.mode = DcMotor.RunMode.RUN_USING_ENCODER

        rightLift.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
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

    fun driveSpeed(button: Button): Double {
        return if (button.`is`(Button.States.TAP)) {
            0.25
        } else {
            1.0
        }
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
//                sleep(300)
//                plungerMotion()
            } else if (rightColor == COLORS.GREEN) {
                carousel.position = Utility.Constants.SINGLE_ROTATION_CAROUSEL
//                sleep(300)
//                plungerMotion()
//                sleep(300)
                carousel.position = Utility.Constants.BASE
            } else if (leftColor == COLORS.GREEN) {
                carousel.position = Utility.Constants.DOUBLE_ROTATION_CAROUSEL
//                sleep(300)
//                plungerMotion()
//                sleep(300)
                carousel.position = Utility.Constants.BASE
            }
        }
        if (purpleButton.`is`(Button.States.TAP)) {
            updateColors()
            if (frontColor == COLORS.PURPLE) {
                carousel.position = Utility.Constants.BASE
//                sleep(300)
//                plungerMotion()
            } else if (rightColor == COLORS.PURPLE) {
                carousel.position = Utility.Constants.SINGLE_ROTATION_CAROUSEL
//                sleep(300)
//                plungerMotion()
//                sleep(300)
                carousel.position = Utility.Constants.BASE
            } else if (leftColor == COLORS.PURPLE) {
                carousel.position = Utility.Constants.DOUBLE_ROTATION_CAROUSEL
//                sleep(300)
//                plungerMotion()
//                sleep(300)
                carousel.position = Utility.Constants.BASE
            }
        }
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

    fun flywheel(buttonSlow: Button, buttonFast: Button) {
        if (buttonSlow.`is`(Button.States.TAP)) {
            flywheelSlowRunning = !flywheelSlowRunning
            if (flywheelFastRunning) flywheelFastRunning = false
        }
        if (buttonFast.`is`(Button.States.TAP)) {
            flywheelFastRunning = !flywheelFastRunning
            if (flywheelSlowRunning) flywheelSlowRunning = false
        }

        if (flywheelSlowRunning) flywheel.power = -0.65
        else if (flywheelFastRunning) flywheel.power = -1.0
        else flywheel.power = 0.0
    }

    fun quickShot(button: Button) {
        if (button.`is`(Button.States.TAP)) {
            flywheelSlowRunning = true
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
            flywheelSlowRunning = false
        }
    }

    fun align(button: Button, distance: Double? = null) {
        val tagPose: AprilTagPoseFtc = (processor.detections.firstOrNull {
            it.metadata != null && it.id !in 21..23
        } ?: return)
            .ftcPose ?: return
        val x = tagPose.x * 39.37
        val y = tagPose.y * 37.37 - (distance ?: 0.0)

        follower.followPath(
            Path(
                BezierLine(
                    Pose(0.0, 0.0, 0.0),
                    Pose(
                        x,
                        if (distance != null) y else tagPose.y * 39.37,
                        Math.toRadians(tagPose.yaw.toDouble())
                    )
                )
            )
        )
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

    }
}