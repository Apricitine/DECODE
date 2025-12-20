package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.Path
import com.pedropathing.paths.PathChain
import com.pedropathing.util.Timer
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.Subsystems
import org.firstinspires.ftc.teamcode.Utility
import java.lang.Thread.sleep
import kotlin.reflect.KFunction

abstract class InheritableAuto : Subsystems() {
    lateinit var currentPose: Pose
    val runtime = ElapsedTime()
    lateinit var pathTimer: Timer
    lateinit var actionTimer: Timer
    lateinit var opModeTimer: Timer

    var pathState: Int = 0

    override fun init() {
        initializeSubsystems()
        initializeProcessor()

        follower = Constants.createFollower(hardwareMap)
        buildPathChains()

        pathTimer = Timer()
        opModeTimer = Timer()
        opModeTimer.resetTimer()
        pathState = 0
        pathTimer.resetTimer()

        carousel.position = Utility.Constants.BASE
        plunger.direction = Servo.Direction.REVERSE
        plunger.position = 0.02
        hood.position = 1.0
        hood.direction = Servo.Direction.REVERSE
        hood.position = 0.07
    }

    override fun start() {

    }

    /**
     * This function creates a PathChain with a set path and a linear heading interpolation,
     * shortening the syntax for the repetitive PathChain assignments.
     * @param startPose The initial position of the robot on the path.
     * @param endPose The final position of the robot on the path.
     * @param interpolationStartPose Optional. In case the linear heading interpolation
     * requires a different Pose than that of the startPose, use this. Defaults to startPose
     * if not set.
     * @param interpolationEndPose Optional. In case the linear heading interpolation
     * requires a different Pose than that of the endPose, use this. Defaults to endPose if
     * not set.
     * @return A PathChain with the specified Poses integrated.
     */
    fun linearPathChain(startPose: Pose, endPose: Pose, interpolationStartPose: Pose? = null, interpolationEndPose: Pose? = null, brakingStrength: Double = 1.0): PathChain {
        return follower.pathBuilder()
            .addPath(BezierLine(startPose, endPose))
            .setLinearHeadingInterpolation(
                interpolationStartPose?.heading ?: startPose.heading,
                interpolationEndPose?.heading ?: endPose.heading
            )
            .setBrakingStrength(brakingStrength)
            .build()
    }

    abstract fun buildPathChains()
    abstract fun pathUpdate()

    inner class Subsystems {
        fun flywheel(power: Double) {
            flywheel.power = -power
        }

        /**
         * Shoot an artifact in the designated slot.
         * @param slot The artifact slot to fire from.
         * @param cooldown Optional. Defaults to true. Specifies whether
         * to add a 300ms cooldown after the plunger motion.
         */
        fun shoot(slot: CarouselStates, cooldown: Boolean = true, startup: Boolean = false) {
            if (startup) sleep(2000)
            when (slot) {
                CarouselStates.FRONT -> carousel.position = Utility.Constants.DOUBLE_ROTATION_CAROUSEL
                CarouselStates.RIGHT -> carousel.position = Utility.Constants.SINGLE_ROTATION_CAROUSEL
                CarouselStates.LEFT -> carousel.position = Utility.Constants.BASE
            }
            sleep(500)
            plunger()
            if (cooldown) sleep(300)
        }

        fun plunger() {
            plunger.position = 0.33
            sleep(350)
            plunger.position = 0.0
        }

        fun intake(power: Double) {
            carousel.position = Utility.Constants.BASE
            leftIntake.power = -power
            rightIntake.power = power
        }
    }

    fun busy(call: () -> Unit) {
        if (!follower.isBusy) {
            call()
        }
    }
}