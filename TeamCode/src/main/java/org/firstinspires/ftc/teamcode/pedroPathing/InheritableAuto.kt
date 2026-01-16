package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.pedropathing.util.Timer
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.Subsystems
import org.firstinspires.ftc.teamcode.Utility
import java.lang.Thread.sleep
import kotlin.math.PI

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

        robot = Constants.createFollower(hardwareMap)
        buildPathChains()

        pathTimer = Timer()
        opModeTimer = Timer()
        opModeTimer.resetTimer()
        pathState = 0
        pathTimer.resetTimer()

        carousel.position = Utility.Constants.BASE
        plunger.direction = Servo.Direction.REVERSE
        plunger.position = 0.02

        flywheel.mode = DcMotor.RunMode.RUN_USING_ENCODER
        flywheel.direction = DcMotorSimple.Direction.REVERSE

        timeSinceLastColorUpdate = ElapsedTime()
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
    fun linearPathChain(
        startPose: Pose?,
        endPose: Pose?,
        interpolationStartPose: Pose? = null,
        interpolationEndPose: Pose? = null,
        brakingStrength: Double = 1.0
    ): PathChain {
        return robot.pathBuilder()
            .addPath(BezierLine(startPose, endPose))
            .setLinearHeadingInterpolation(
                interpolationStartPose?.heading ?: startPose!!.heading,
                interpolationEndPose?.heading ?: endPose!!.heading
            )
            .setBrakingStrength(brakingStrength)
            .build()
    }

    abstract fun buildPathChains()
    abstract fun pathUpdate()

    /**
     * Class representing the robot's subsystem actions. Should include all autonomous
     * specific methods.
     */
    inner class Subsystems {

        /**
         * Run the flywheel with the given amount of power.
         * @param velocity The amount of power to give to the flywheel motor.
         */
        fun flywheel(velocity: Double) {
            flywheel.velocity = velocity
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
                CarouselStates.FRONT -> carousel.position =
                    Utility.Constants.DOUBLE_ROTATION_CAROUSEL

                CarouselStates.RIGHT -> carousel.position =
                    Utility.Constants.SINGLE_ROTATION_CAROUSEL

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

        /**
         * Shoots three artifacts according to the detected obelisk state assuming
         * that they are loaded in the GPP sequence.
         */
        fun motifShot() {
            if (obeliskState == ObeliskStates.NONE) return

            val purple = mutableListOf(CarouselStates.RIGHT, CarouselStates.LEFT)

            obeliskState.name
                .map { if (it == 'G') CarouselStates.FRONT else purple.removeAt(0) }
                .forEachIndexed { index, slot ->
                    shoot(
                        slot,
                        index != 2,
                        index == 0
                    )
                }
        }

        /**
         * Parses through the sensor detections to determine the colors to be associated with
         * each carousel position. Then, according to the obelisk state, fires the loaded
         * artifacts in the motif's order.
         */
        fun colorMotifShot() {
            updateColors()
            if (obeliskState == ObeliskStates.NONE) return

            val slots = mutableListOf(
                CarouselStates.FRONT to frontColor,
                CarouselStates.RIGHT to rightColor,
                CarouselStates.LEFT to leftColor
            )

            obeliskState.name
                .map { if (it == 'G') COLORS.GREEN else COLORS.PURPLE }
                .mapNotNull { c ->
                    slots.firstOrNull { it.second == c }?.also { slots.remove(it) }?.first
                }
                .also { list ->
                    list.forEachIndexed { i, slot ->
                        shoot(slot, i != list.lastIndex, i == 0)
                    }
                }
        }
    }

    /**
     * Runs code if the robot is not busy (following a path).
     * @param call The code to be run if the robot is not busy.
     */
    fun busy(call: () -> Unit) {
        if (!robot.isBusy) {
            call()
        }
    }

    /**
     * Sets the path state to a given integer state and resets the path timer.
     * @param state The integer state to set pathState to.
     */
    fun setAndResetPathTimer(state: Int) {
        pathState = state
        pathTimer.resetTimer()
    }

    fun Pose.reflectOverX(center: Double = 72.0): Pose =
        Pose(
            2 * center - x,
            y,
            normalizeAngle(PI - heading)
        )

    private fun normalizeAngle(theta: Double): Double {
        var t = theta
        while (t <= -PI) t += 2 * PI
        while (t > PI) t -= 2 * PI
        return t
    }

    fun Map<String, Pose>.reflectOverX(center: Double = 72.0): Map<String, Pose> =
        mapValues { (_, pose) -> pose.reflectOverX(center) }
}