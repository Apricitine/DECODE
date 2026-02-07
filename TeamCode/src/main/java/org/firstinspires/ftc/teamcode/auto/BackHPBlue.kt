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
    var reset = false

    open val poses = mapOf(
        "start" to Pose(60.0, 9.0, Math.toRadians(90.0)),
        "shootPreload" to Pose(58.0, 18.0, Math.toRadians(112.0)),
        "thirdStrike" to Pose(48.0, 36.0, Math.toRadians(180.0)),
        "getThirdStrike" to Pose(18.0, 36.0, Math.toRadians(180.0)),
        "shootThirdStrike" to Pose(58.0, 18.0, Math.toRadians(112.0)),
        "getHP" to Pose(12.0, 12.0, Math.toRadians(180.0)),
        "shootHP" to Pose(58.0, 18.0, Math.toRadians(112.0)),
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
        subsystems.flywheel(1225.0)
        robot.setStartingPose(poses["start"])
    }

    override fun buildPathChains() {
        PathChains.shootPreload =
            linearPathChain(poses["start"], poses["shootPreload"])
        PathChains.thirdStrike =
            robot.pathBuilder().addPath(BezierLine(poses["shootPreload"], poses["thirdStrike"]))
                .setLinearHeadingInterpolation(
                    poses["shootPreload"]!!.heading,
                    poses["thirdStrike"]!!.heading
                ).build()
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
            0 -> busy {
                if (!reset) {
                    setAndResetPathTimer(0)
                    reset = !reset
                }
                obeliskTag()
                if (pathTimer.elapsedTimeSeconds > 4) {
                    robot.followPath(PathChains.shootPreload, true)
                    setAndResetPathTimer(1)
                }
            }

            1 -> busy {
                if (shotSets == 0) {
                    subsystems.motifShot()
                    shotSets++
                }

                if (pathTimer.elapsedTimeSeconds > 6) {
                    robot.followPath(PathChains.thirdStrike, true)
                    setAndResetPathTimer(2)
                }
            }

            2 -> busy {
                subsystems.intake(1.0)
                robot.followPath(PathChains.getThirdStrike, 0.5, true)
                if (pathTimer.elapsedTimeSeconds > 3) {
                    robot.followPath(PathChains.getThirdStrike, 0.5, true)
                    setAndResetPathTimer(3)
                }
            }

            3 -> busy {
                subsystems.intake(1.0)
                robot.followPath(PathChains.shootThirdStrike, true)
                setAndResetPathTimer(4)
            }

            4 -> busy {
                if (shotSets == 1) {
                    subsystems.motifShot(ObeliskStates.PPG)
                    shotSets++
                }

                if (pathTimer.elapsedTimeSeconds > 6) {
                    subsystems.intake(1.0)
                    robot.followPath(PathChains.getHP, 0.5, true)
                    setAndResetPathTimer(5)
                }
            }

            5 -> busy {
                subsystems.intake(1.0)
                robot.followPath(PathChains.getHP, true)
                setAndResetPathTimer(6)
            }

            6 -> busy {
                subsystems.intake(1.0)
                robot.followPath(PathChains.shootHP, true)
                setAndResetPathTimer(7)
            }

            7 -> busy {
                if (shotSets == 2) {
                    subsystems.motifShot(ObeliskStates.PGP)
                    shotSets++
                }

                if (pathTimer.elapsedTimeSeconds > 6) {
                    robot.followPath(PathChains.park, true)
                }
            }

        }
    }
}