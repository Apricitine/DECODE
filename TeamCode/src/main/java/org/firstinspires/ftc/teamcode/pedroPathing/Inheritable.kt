package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.Path
import com.pedropathing.paths.PathChain
import com.pedropathing.util.Timer
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.Subsystems

@Autonomous(name = "Blue Target Test", group = "Test")
open class BlueTarget : Subsystems() {
    lateinit var currentPose: Pose
    val runtime = ElapsedTime()
    lateinit var pathTimer: Timer
    lateinit var actionTimer: Timer
    lateinit var opModeTimer: Timer

    var pathState: Int = 0

    lateinit var scorePreload: Path

    object PathChains : PathChain() {
        lateinit var shootBalls: PathChain
        lateinit var positionToGetFirstStrike: PathChain
        lateinit var intakeFirstStrike: PathChain
        lateinit var shootStrikes: PathChain
    }

    object Poses {
        val start = Pose(18.3, 125.6, Math.toRadians(315.0))
        val camera = Pose(34.2, 109.4, Math.toRadians(45.0))
        val shootPosition = Pose(48.0, 96.0, Math.toRadians(135.0))
        val firstStrike = Pose(48.0, 84.0, Math.toRadians(180.0))
        val getFirstStrike = Pose(122.0, 84.0, Math.toRadians(180.0))
    }

    override fun init() {
        initializeSubsystems()
        initializeProcessor()

        follower = Constants.createFollower(hardwareMap)
        follower.setStartingPose(Poses.start)

        pathTimer = Timer()
        opModeTimer = Timer()
        opModeTimer.resetTimer()

    }

    override fun start() {
        opModeTimer.resetTimer()
    }

    override fun loop() {
        follower.update()
        panelsTelemetry.update()
        currentPose = follower.pose

    }


}