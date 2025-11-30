package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.Subsystems

@Autonomous(name = "idek", group = "Test")
class Inheritable : Subsystems() {
    lateinit var currentPose: Pose
    val runtime = ElapsedTime()

    enum class ObeliskStates {
        NONE,
        GPP,
        PGP,
        PPG
    }
    private var obeliskState: ObeliskStates = ObeliskStates.NONE

    object PathChains : PathChain() {
        lateinit var scorePreload: PathChain
    }

    object Poses {
        val startPose = Pose()
        val scorePose = Pose()
    }

    override fun init() {
        initializeSubsystems()
        initializeProcessor()

        follower = Constants.createFollower(hardwareMap)
        follower.setStartingPose(Poses.startPose)
    }

    override fun loop() {
        follower.update()
        panelsTelemetry.update()
        currentPose = follower.pose

        buildPreloadPath()
    }

    fun buildPreloadPath() {
        PathChains.scorePreload =
            follower.pathBuilder().addPath(BezierLine(Poses.startPose, Poses.scorePose))
                .setLinearHeadingInterpolation(Poses.startPose.heading, Poses.scorePose.heading)
                .build()
    }
}