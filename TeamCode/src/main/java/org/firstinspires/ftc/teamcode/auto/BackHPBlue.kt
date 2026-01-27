package org.firstinspires.ftc.teamcode.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.pedroPathing.InheritableAuto

@Autonomous(name = "Back Human Player Blue", group = "main")
open class BackHPBlue : InheritableAuto() {
    val subsystems = Subsystems()
    var shotSets = 0

    open val poses = mapOf(
        "start" to Pose(60.0, 9.0, Math.toRadians(90.0)),
        "shootPreload" to Pose(60.0, 18.0, Math.toRadians(106.0)),
        "thirdStrike" to Pose(48.0, 36.0, Math.toRadians(180.0)),
        "getThirdStrike" to Pose(12.0, 36.0, Math.toRadians(180.0)),
        "shootThirdStrike" to Pose(66.0, 12.0, Math.toRadians(106.0)),
        "getHP" to Pose(12.0, 12.0, Math.toRadians(180.0)),
        "shootHP" to Pose(66.0, 18.0, Math.toRadians(106.0)),
        "park" to Pose(36.0, 36.0, Math.toRadians(90.0))
    )

    object PathChains : PathChain() {
        lateinit var shootPreload: PathChain
        lateinit var thirdStrike: PathChain
        lateinit var getThirdStrike: PathChain
        lateinit var shootThirdStrike: PathChain

        lateinit var getHP: PathChain
        lateinit var shootHP: PathChain
        lateinit var park: PathChain
    }

    override fun loop() {
        super.loop()
        subsystems.flywheel(1100.0)
        robot.setStartingPose(poses["start"])
    }

    override fun buildPathChains() {
        PathChains.shootPreload =
            linearPathChain(poses["start"], poses["shootPreload"])
        PathChains.thirdStrike =
            linearPathChain(poses["shootPreload"], poses["thirdStrike"])
        PathChains.getThirdStrike =
            robot.pathBuilder().addPath(BezierLine(poses["thirdStrike"], poses["getThirdStrike"]))
                .build()
        PathChains.shootThirdStrike =
            linearPathChain(poses["getThirdStrike"], poses["shootThirdStrike"])
        PathChains.getHP =
            robot.pathBuilder().addPath(BezierLine(poses["shootThirdStrike"], poses["getHP"]))
                .build()
        PathChains.shootHP =
            linearPathChain(poses["getHP"], poses["shootHP"])
        PathChains.park = linearPathChain(poses["shootHP"], poses["park"])
    }

    override fun pathUpdate() {
        when (pathState) {
            0 -> {
                obeliskTag()
                geedadee(2, 1)
            }

            1 -> {
                robot.followPath(PathChains.shootPreload)
                geedadee(2, 2)
            }
            2 -> {
                subsystems.motifShot()
                geedadee(7, 3)
            }
            3 -> {
                robot.followPath(PathChains.thirdStrike)
            }
        }
    }
}