package org.firstinspires.ftc.teamcode.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.pedroPathing.InheritableAuto
import java.lang.Thread.sleep

@Autonomous(name = "Center Blue", group = "main")
class CenterBlue : InheritableAuto() {
    val subsystems = Subsystems()

    object Poses {
        val start = Pose(18.3, 125.6, Math.toRadians(315.0))
        val camera = Pose(34.2, 109.4, Math.toRadians(45.0))
        val firstShootPosition = Pose(48.0, 96.0, Math.toRadians(125.0))
        val shootPosition = Pose(48.0, 96.0, Math.toRadians(135.0))
        val firstStrike = Pose(41.64, 85.51, Math.toRadians(180.0))
        val getFirstStrike = Pose(18.0, 85.51, Math.toRadians(180.0))
        val finish = Pose(48.0, 120.0, Math.toRadians(90.0))
    }

    object PathChains : PathChain() {
        lateinit var shootBalls: PathChain
        lateinit var positionToGetFirstStrike: PathChain
        lateinit var intakeFirstStrike: PathChain
        lateinit var shootStrikes: PathChain
        lateinit var getTag: PathChain
        lateinit var park: PathChain
    }

    override fun loop() {
        follower.update()
        follower.setStartingPose(Poses.start)
        panelsTelemetry.update()
        currentPose = follower.pose

        pathUpdate()

        log("path state", pathState)
        log("x", follower.pose.x)
        log("y", follower.pose.y)
        log("heading", follower.pose.heading)
    }

    override fun buildPathChains() {
        PathChains.getTag = linearPathChain(Poses.start, Poses.camera)
        PathChains.shootBalls =
            linearPathChain(Poses.camera, Poses.firstShootPosition)
        PathChains.positionToGetFirstStrike =
            linearPathChain(Poses.shootPosition, Poses.firstStrike)
        PathChains.intakeFirstStrike =
            follower.pathBuilder()
                .addPath(BezierLine(Poses.firstStrike, Poses.getFirstStrike))
                .setBrakingStrength(0.25)
                .build()
        PathChains.shootStrikes = linearPathChain(Poses.getFirstStrike, Poses.shootPosition)
        PathChains.park = linearPathChain(Poses.shootPosition, Poses.finish)
    }

    override fun pathUpdate() {
        when (pathState) {
            0 -> {
                follower.followPath(PathChains.getTag)
                setAndResetPathTimer(1)
            }

            1 -> busy {
                obeliskTag()
                if (pathTimer.elapsedTimeSeconds > 2) {
                    follower.followPath(PathChains.shootBalls, true)
                    setAndResetPathTimer(2)
                }
            }

            2 -> busy {
                subsystems.flywheel(0.65)
                subsystems.motifShot()
                subsystems.flywheel(0.0)
                if (pathTimer.elapsedTimeSeconds > 4) {
                    follower.followPath(PathChains.positionToGetFirstStrike, true)
                    setAndResetPathTimer(3)
                }
            }

            3 -> busy {
                follower.followPath(PathChains.positionToGetFirstStrike, true)
                setAndResetPathTimer(4)
            }

            4 -> busy {
                subsystems.intake(1.0)
                follower.followPath(PathChains.intakeFirstStrike, true)
                setAndResetPathTimer(5)
            }

            5 -> busy {
                subsystems.intake(1.0)
                subsystems.flywheel(0.65)
                follower.followPath(PathChains.shootStrikes, true)
                setAndResetPathTimer(6)
            }

            6 -> busy {
                subsystems.flywheel(0.65)
                subsystems.colorMotifShot()
                subsystems.flywheel(0.0)
                if (pathTimer.elapsedTimeSeconds > 6) {
                    setAndResetPathTimer(7)
                }
            }
            7 -> busy {
                subsystems.intake(0.0)
                follower.followPath(PathChains.park)
                setAndResetPathTimer(-1)
            }

            else -> {}
        }
    }
}