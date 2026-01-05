package org.firstinspires.ftc.teamcode.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.pedroPathing.InheritableAuto

@Autonomous(name = "Center Red", group = "main")
class CenterRed : InheritableAuto() {
    val subsystems = Subsystems()

    object Poses {
        val start = Pose(144-18.3, 125.6, Math.toRadians(225.0))
        val camera = Pose(144-34.2, 109.4, Math.toRadians(135.0))
        val firstShootPosition = Pose(144-48.0, 96.0, Math.toRadians(45.0))
        val shootPosition = Pose(144-48.0, 96.0, Math.toRadians(45.0))
        val firstStrike = Pose(144-41.64, 85.51, Math.toRadians(0.0))
        val getFirstStrike = Pose(144-18.0, 85.51, Math.toRadians(0.0))
        val finish = Pose(144-48.0, 120.0, Math.toRadians(90.0))
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
        robot.update()
        robot.setStartingPose(Poses.start)
        panelsTelemetry.update()
        currentPose = robot.pose

        pathUpdate()

        log("path state", pathState)
        log("x", robot.pose.x)
        log("y", robot.pose.y)
        log("heading", robot.pose.heading)
    }

    override fun buildPathChains() {
        PathChains.getTag = linearPathChain(Poses.start, Poses.camera)
        PathChains.shootBalls =
            linearPathChain(Poses.camera, Poses.firstShootPosition)
        PathChains.positionToGetFirstStrike =
            linearPathChain(Poses.shootPosition, Poses.firstStrike)
        PathChains.intakeFirstStrike =
            robot.pathBuilder()
                .addPath(BezierLine(Poses.firstStrike, Poses.getFirstStrike))
                .setBrakingStrength(0.25)
                .build()
        PathChains.shootStrikes = linearPathChain(Poses.getFirstStrike, Poses.shootPosition)
        PathChains.park = linearPathChain(Poses.shootPosition, Poses.finish)
    }

    override fun pathUpdate() {
        when (pathState) {
            0 -> {
                robot.followPath(PathChains.getTag)
                setAndResetPathTimer(1)
            }

            1 -> busy {
                obeliskTag()
                if (pathTimer.elapsedTimeSeconds > 2) {
                    robot.followPath(PathChains.shootBalls, true)
                    setAndResetPathTimer(2)
                }
            }

            2 -> busy {
                subsystems.flywheel(0.65)
                subsystems.motifShot()
                subsystems.flywheel(0.0)

                if (pathTimer.elapsedTimeSeconds > 4) {
                    robot.followPath(PathChains.positionToGetFirstStrike, true)
                    setAndResetPathTimer(3)
                }
            }

            3 -> busy {
                robot.followPath(PathChains.positionToGetFirstStrike, true)
                setAndResetPathTimer(4)
            }

            4 -> busy {
                subsystems.intake(1.0)
                robot.followPath(PathChains.intakeFirstStrike, true)
                setAndResetPathTimer(5)
            }

            5 -> busy {
                subsystems.intake(1.0)
                subsystems.flywheel(0.65)
                robot.followPath(PathChains.shootStrikes, true)
                setAndResetPathTimer(6)
            }

            6 -> busy {
                subsystems.flywheel(0.65)
                subsystems.colorMotifShot()
                subsystems.flywheel(0.0)

                if (pathTimer.elapsedTimeSeconds > 6) setAndResetPathTimer(7)

            }
            7 -> busy {
                subsystems.intake(0.0)
                robot.followPath(PathChains.park)
                setAndResetPathTimer(-1)
            }

            else -> {}
        }
    }
}