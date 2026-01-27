package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.pedropathing.util.Timer
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.Subsystems
import org.firstinspires.ftc.teamcode.Utility
import kotlin.math.PI

abstract class InheritableAuto : Subsystems() {
    lateinit var currentPose: Pose
    val runtime = ElapsedTime()
    lateinit var pathTimer: Timer
    lateinit var opModeTimer: Timer

    var pathState: Int = 0

    val motifShot = TimedSequence()
    val colorMotifShot = TimedSequence()


    override fun init() {
        initializeSubsystems()
        initializeProcessor()
        rightBaseline = calibrate(rightSensor)
        leftBaseline = calibrate(leftSensor)
        frontBaseline = calibrate(frontSensor)

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
        flywheel.setPIDFCoefficients(
            DcMotor.RunMode.RUN_USING_ENCODER, PIDFCoefficients(60.0, 0.0, 0.0, 18.0)
        )

        timeSinceLastColorUpdate = ElapsedTime()
    }

    override fun loop() {
        robot.update()

        motifShot.update()
        colorMotifShot.update()
        panelsTelemetry.update()
        currentPose = robot.pose
        pathUpdate()

        log("path state", pathState)
        log("path timer", pathTimer.elapsedTimeSeconds)
        log("x", robot.pose.x)
        log("y", robot.pose.y)
        log("heading", robot.pose.heading)
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

        fun TimedSequence.plunger(): TimedSequence {
            run { plunger.position = 0.33 }
            waitFor(350)
            run { plunger.position = 0.0 }
            return this
        }

        fun TimedSequence.shoot(
            slot: CarouselStates,
            startup: Boolean = false,
            cooldown: Boolean = true
        ): TimedSequence {
            run {
                carousel.position = when (slot) {
                    CarouselStates.FRONT -> Utility.Constants.DOUBLE_ROTATION_CAROUSEL
                    CarouselStates.RIGHT -> Utility.Constants.SINGLE_ROTATION_CAROUSEL
                    CarouselStates.LEFT -> Utility.Constants.BASE
                }
            }
            waitFor(if (startup) 1000 else 500)
            plunger()
            if (cooldown) waitFor(300)
            return this
        }

        fun intake(power: Double) {
            carousel.position = Utility.Constants.BASE
            leftIntake.power = -power
            rightIntake.power = power
        }

        /**
         * Shoots three artifacts according to the detected obelisk state given a specified order
         * in which they are loaded.
         * @param loadOrder The order in which they are loaded, given in enum type ObeliskStates.
         * The load order is the front artifact, then right artifact, and then left artifact
         */
        fun motifShot(loadOrder: ObeliskStates = ObeliskStates.GPP) {
            if (obeliskState == ObeliskStates.NONE) return
            if (!motifShot.isFinished()) return
            val purple = when (loadOrder) {
                ObeliskStates.GPP -> mutableListOf(CarouselStates.RIGHT, CarouselStates.LEFT)
                ObeliskStates.PGP -> mutableListOf(CarouselStates.FRONT, CarouselStates.RIGHT)
                ObeliskStates.PPG -> mutableListOf(CarouselStates.FRONT, CarouselStates.RIGHT)
                else -> throw(Throwable("ur stupid wtf"))
            }

            require(purple.size == obeliskState.name.count { it == 'P' }) {
                "Purple count mismatch: purple=${purple.size}, detected=${obeliskState}"
            }

            val slots = obeliskState.name.map {
                if (loadOrder == ObeliskStates.GPP) {
                    if (it == 'G') CarouselStates.FRONT else purple.removeAt(0)
                } else if (loadOrder == ObeliskStates.PGP) {
                    if (it == 'G') CarouselStates.LEFT else purple.removeAt(0)
                } else {
                    if (it == 'G') CarouselStates.LEFT else purple.removeAt(0)
                }
            }

            motifShot.reset()
            slots.forEachIndexed { i, slot ->
                log("shooting", slot)
                motifShot.shoot(
                    slot = slot,
                    startup = i == 0,
                    cooldown = i != slots.size
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
            if (!colorMotifShot.isFinished()) return
            if (obeliskState == ObeliskStates.NONE) return

            val slots = mutableListOf(
                CarouselStates.FRONT to frontColor,
                CarouselStates.RIGHT to rightColor,
                CarouselStates.LEFT to leftColor
            )

            val orderedSlots =
                obeliskState.name
                    .map { if (it == 'G') COLORS.GREEN else COLORS.PURPLE }
                    .mapNotNull { color ->
                        slots.firstOrNull { it.second == color }
                            ?.also { slots.remove(it) }
                            ?.first
                    }

            if (orderedSlots.isEmpty()) return

            colorMotifShot.reset()
            orderedSlots.forEachIndexed { i, slot ->
                colorMotifShot.shoot(
                    slot = slot,
                )
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

    fun geedadee(time: Int, state: Int? = null, call: (() -> Unit)?= null) {
        if (pathTimer.elapsedTimeSeconds > time) {
            if (state != null) setAndResetPathTimer(state)
            call?.invoke()
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