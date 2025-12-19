package org.firstinspires.ftc.teamcode.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import org.firstinspires.ftc.teamcode.pedroPathing.InheritableAuto


class CenterBlue : InheritableAuto() {
    val subsystems = Subsystems()

    object Poses {
        val start = Pose(18.3, 125.6, Math.toRadians(315.0))
        val camera = Pose(34.2, 109.4, Math.toRadians(45.0))
        val shootPosition = Pose(48.0, 96.0, Math.toRadians(135.0))
        val firstStrike = Pose(41.64, 85.51, Math.toRadians(180.0))
        val getFirstStrike = Pose(21.70, 85.51, Math.toRadians(180.0))
    }

    object PathChains : PathChain() {
        lateinit var shootBalls: PathChain
        lateinit var positionToGetFirstStrike: PathChain
        lateinit var intakeFirstStrike: PathChain
        lateinit var shootStrikes: PathChain
        lateinit var getTag: PathChain
    }

    override fun loop() {
        follower.setStartingPose(Poses.start)
    }

    override fun buildPathChains() {
        PathChains.getTag = linearPathChain(Poses.start, Poses.camera)
        PathChains.shootBalls =
            linearPathChain(Poses.camera, Poses.shootPosition, Poses.camera, Poses.firstStrike)
        PathChains.positionToGetFirstStrike =
            linearPathChain(Poses.shootPosition, Poses.firstStrike)
        PathChains.intakeFirstStrike =
            follower.pathBuilder().addPath(BezierLine(Poses.firstStrike, Poses.getFirstStrike))
                .build()
        PathChains.shootStrikes = linearPathChain(Poses.getFirstStrike, Poses.shootPosition)
    }

    override fun pathUpdate() {
        when (pathState) {
            0 -> {
                follower.followPath(PathChains.getTag)
                pathState = -1
                pathTimer.resetTimer()
            }
            1 -> busy {
                obeliskTag()
                if (pathTimer.elapsedTimeSeconds > 5) {
                    follower.followPath(PathChains.shootBalls, true)
                    pathState = 2
                    pathTimer.resetTimer()
                }
            }
            2 -> busy {
                subsystems.flywheel(0.65)
                when (obeliskState) {
                    ObeliskStates.GPP -> {
                        subsystems.shoot(CarouselStates.FRONT)
                        subsystems.shoot(CarouselStates.RIGHT)
                        subsystems.shoot(CarouselStates.LEFT, false)
                        subsystems.flywheel(0.0)
                    }

                    ObeliskStates.PGP -> {
                        subsystems.shoot(CarouselStates.RIGHT)
                        subsystems.shoot(CarouselStates.FRONT)
                        subsystems.shoot(CarouselStates.LEFT, false)
                        subsystems.flywheel(0.0)
                    }

                    ObeliskStates.PPG -> {
                        subsystems.shoot(CarouselStates.LEFT)
                        subsystems.shoot(CarouselStates.RIGHT)
                        subsystems.shoot(CarouselStates.FRONT, false)
                        subsystems.flywheel(0.0)
                    }

                    else -> {}
                }
                if (pathTimer.elapsedTimeSeconds > 5) {
                    follower.followPath(PathChains.positionToGetFirstStrike)
                    pathState = 3
                    pathTimer.resetTimer()
                }
            }
            3 -> busy {
                follower.followPath(PathChains.positionToGetFirstStrike, true)
                pathState = 4
                pathTimer.resetTimer()
            }
            4 -> busy {
                subsystems.intake(1.0)
                follower.followPath(PathChains.intakeFirstStrike, true)
                pathState = 5
                pathTimer.resetTimer()
            }
            5 -> busy {
                subsystems.intake(1.0)
                subsystems.flywheel(0.65)
                follower.followPath(PathChains.shootStrikes, true)
                pathState = 6
                pathTimer.resetTimer()
            }
            6 -> busy {
                subsystems.flywheel(0.65)
                when (obeliskState) {
                    ObeliskStates.GPP -> {
                        subsystems.shoot(CarouselStates.FRONT)
                        subsystems.shoot(CarouselStates.RIGHT)
                        subsystems.shoot(CarouselStates.LEFT, false)
                        subsystems.flywheel(0.0)
                    }

                    ObeliskStates.PGP -> {
                        subsystems.shoot(CarouselStates.RIGHT)
                        subsystems.shoot(CarouselStates.FRONT)
                        subsystems.shoot(CarouselStates.LEFT, false)
                        subsystems.flywheel(0.0)
                    }

                    ObeliskStates.PPG -> {
                        subsystems.shoot(CarouselStates.LEFT)
                        subsystems.shoot(CarouselStates.RIGHT)
                        subsystems.shoot(CarouselStates.FRONT, false)
                        subsystems.flywheel(0.0)
                    }

                    else -> {}
                }
                if (pathTimer.elapsedTimeSeconds > 5) {
                    pathState = -1
                    pathTimer.resetTimer()
                }
            }
            7 -> busy {
                follower.followPath(PathChains.shootBalls, true)
                pathState = 8
                pathTimer.resetTimer()
            }
            8 -> busy {
                pathState = -1
                pathTimer.resetTimer()
            }
            else -> {}
        }
    }
}