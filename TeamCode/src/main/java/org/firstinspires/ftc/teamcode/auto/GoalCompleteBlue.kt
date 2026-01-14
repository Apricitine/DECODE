package org.firstinspires.ftc.teamcode.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.pedroPathing.InheritableAuto

@Autonomous(name = "Goal Complete Blue", group = "main")
class GoalCompleteBlue : InheritableAuto() {
    val subsystems = Subsystems()

    object Poses {
        val start = Pose(18.3, 125.6, Math.toRadians(315.0))
        val camera = Pose(34.2, 109.4, Math.toRadians(45.0))
        val shoot = Pose(48.0, 96.0, Math.toRadians(135.0))
        val firstStrike = Pose(48.0, 84.0, Math.toRadians(180.0))
        val getFirstStrike = Pose(16.0, 84.0, Math.toRadians(180.0))
        val secondStrike = Pose(48.0, 60.0, Math.toRadians(180.0))
        val getSecondStrike = Pose(16.0, 60.0, Math.toRadians(180.0))
        val thirdStrike = Pose(48.0, 36.0, Math.toRadians(180.0))
        val getThirdStrike = Pose(16.0, 36.0, Math.toRadians(180.0))
        val park = Pose(48.0, 120.0, Math.toRadians(90.0))
    }

    object PathChains : PathChain() {
        lateinit var getTag: PathChain
        lateinit var shootPreload: PathChain
        lateinit var firstStrike: PathChain
        lateinit var getFirstStrike: PathChain
        lateinit var shootFirstStrike: PathChain
        lateinit var secondStrike: PathChain
        lateinit var getSecondStrike: PathChain
        lateinit var shootSecondStrike: PathChain
        lateinit var thirdStrike: PathChain
        lateinit var getThirdStrike: PathChain
        lateinit var shootThirdStrike: PathChain
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
        PathChains.shootPreload =
            linearPathChain(Poses.camera, Poses.shoot)
        PathChains.firstStrike =
            linearPathChain(Poses.shoot, Poses.firstStrike)
        PathChains.getFirstStrike =
            robot.pathBuilder()
                .addPath(BezierLine(Poses.firstStrike, Poses.getFirstStrike))
                .build()
        PathChains.shootFirstStrike = linearPathChain(Poses.getFirstStrike, Poses.shoot)
        PathChains.secondStrike =
            linearPathChain(Poses.shoot, Poses.secondStrike)
        PathChains.getSecondStrike =
            robot.pathBuilder()
                .addPath(BezierLine(Poses.secondStrike, Poses.getSecondStrike))
                .build()
        PathChains.shootSecondStrike = linearPathChain(Poses.getSecondStrike, Poses.shoot)
        PathChains.thirdStrike =
            linearPathChain(Poses.shoot, Poses.thirdStrike)
        PathChains.getThirdStrike =
            robot.pathBuilder()
                .addPath(BezierLine(Poses.thirdStrike, Poses.getThirdStrike))
                .build()
        PathChains.shootThirdStrike = linearPathChain(Poses.getThirdStrike, Poses.shoot)
        PathChains.park = linearPathChain(Poses.shoot, Poses.park)
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
                    robot.followPath(PathChains.shootPreload, true)
                    setAndResetPathTimer(2)
                }
            }

            2 -> busy {
                subsystems.flywheel(0.65)
                subsystems.motifShot()
                subsystems.flywheel(0.0)
                if (pathTimer.elapsedTimeSeconds > 4) {
                    robot.followPath(PathChains.firstStrike, true)
                    setAndResetPathTimer(3)
                }
            }

            3 -> busy {
                robot.followPath(PathChains.firstStrike, true)
                setAndResetPathTimer(4)
            }

            4 -> busy {
                subsystems.intake(1.0)
                robot.followPath(PathChains.getFirstStrike, 0.5, true)
                setAndResetPathTimer(5)
            }

            5 -> busy {
                subsystems.intake(1.0)
                subsystems.flywheel(0.65)
                robot.followPath(PathChains.shootFirstStrike, true)
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
                robot.followPath(PathChains.secondStrike, true)
                setAndResetPathTimer(8)
            }

            8 -> busy {
                subsystems.intake(1.0)
                robot.followPath(PathChains.getSecondStrike, 0.5, true)
                setAndResetPathTimer(9)
            }

            9 -> busy {
                subsystems.intake(1.0)
                subsystems.flywheel(0.65)
                robot.followPath(PathChains.shootSecondStrike, true)
                setAndResetPathTimer(10)
            }

            10 -> busy {
                subsystems.flywheel(0.65)
                subsystems.colorMotifShot()
                subsystems.flywheel(0.0)
                if (pathTimer.elapsedTimeSeconds > 6) {
                    setAndResetPathTimer(11)
                }
            }

            11 -> busy {
                robot.followPath(PathChains.thirdStrike, true)
                setAndResetPathTimer(12)
            }

            12 -> busy {
                subsystems.intake(1.0)
                robot.followPath(PathChains.getThirdStrike, 0.5, true)
                setAndResetPathTimer(13)
            }

            13 -> busy {
                subsystems.intake(1.0)
                subsystems.flywheel(0.65)
                robot.followPath(PathChains.shootThirdStrike, true)
                setAndResetPathTimer(14)
            }

            14 -> busy {
                subsystems.intake(0.0)
                subsystems.flywheel(0.65)
                subsystems.colorMotifShot()
                subsystems.flywheel(0.0)
                if (pathTimer.elapsedTimeSeconds > 6) {
                    setAndResetPathTimer(15)
                }
            }

            15 -> {
                robot.followPath(PathChains.park)
            }

            else -> {}
        }
    }
}