package org.firstinspires.ftc.teamcode.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.pedroPathing.InheritableAuto

@Autonomous(name = "Goal Complete Blue", group = "main")
open class GoalCompleteBlue : InheritableAuto() {
    val subsystems = Subsystems()
    var shotSets = 0

    open val poses = mapOf(
        "start" to Pose(21.0, 123.0, Math.toRadians(315.0)),
        "camera" to Pose(36.0, 108.0, Math.toRadians(45.0)),
        "shoot" to Pose(48.0, 96.0, Math.toRadians(135.0)),
        "firstStrike" to Pose(48.0, 84.0, Math.toRadians(180.0)),
        "getFirstStrike" to Pose(16.0, 84.0, Math.toRadians(180.0)),
        "secondStrike" to Pose(48.0, 60.0, Math.toRadians(180.0)),
        "getSecondStrike" to Pose(16.0, 60.0, Math.toRadians(180.0)),
        "thirdStrike" to Pose(48.0, 36.0, Math.toRadians(180.0)),
        "getThirdStrike" to Pose(16.0, 36.0, Math.toRadians(180.0)),
        "park" to Pose(48.0, 120.0, Math.toRadians(90.0))
    )

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
        super.loop()
        subsystems.flywheel(1000.0)
        robot.setStartingPose(poses["start"])

        log("time", motifShot.timer)
    }

    override fun buildPathChains() {
        PathChains.getTag = linearPathChain(poses["start"], poses["camera"])
        PathChains.shootPreload =
            linearPathChain(poses["camera"], poses["shoot"])
        PathChains.firstStrike =
            linearPathChain(poses["shoot"], poses["firstStrike"])
        PathChains.getFirstStrike =
            robot.pathBuilder()
                .addPath(BezierLine(poses["firstStrike"], poses["getFirstStrike"]))
                .build()
        PathChains.shootFirstStrike = linearPathChain(poses["getFirstStrike"], poses["shoot"])
        PathChains.secondStrike =
            linearPathChain(poses["shoot"], poses["secondStrike"])
        PathChains.getSecondStrike =
            robot.pathBuilder()
                .addPath(BezierLine(poses["secondStrike"], poses["getSecondStrike"]))
                .build()
        PathChains.shootSecondStrike = linearPathChain(poses["getSecondStrike"], poses["shoot"])
        PathChains.thirdStrike =
            linearPathChain(poses["shoot"], poses["thirdStrike"])
        PathChains.getThirdStrike =
            robot.pathBuilder()
                .addPath(BezierLine(poses["thirdStrike"], poses["getThirdStrike"]))
                .build()
        PathChains.shootThirdStrike = linearPathChain(poses["getThirdStrike"], poses["shoot"])
        PathChains.park = linearPathChain(poses["shoot"], poses["park"])
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
                if (shotSets == 0) {
                    subsystems.motifShot()
                    shotSets++
                }

                if (pathTimer.elapsedTimeSeconds > 8) {
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
                robot.followPath(PathChains.shootFirstStrike, true)
                setAndResetPathTimer(6)
            }

            6 -> busy {
                if (shotSets == 1) {
                    subsystems.motifShot()
                    shotSets++
                }

                if (pathTimer.elapsedTimeSeconds > 7) {
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
                robot.followPath(PathChains.shootSecondStrike, true)
                setAndResetPathTimer(10)
            }

            10 -> busy {
                if (shotSets == 2) {
                    subsystems.motifShot()
                    shotSets++
                }
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
                robot.followPath(PathChains.shootThirdStrike, true)
                setAndResetPathTimer(14)
            }

            14 -> busy {
                subsystems.intake(0.0)
                if (shotSets == 3) {
                    subsystems.colorMotifShot()
                    shotSets++
                }
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