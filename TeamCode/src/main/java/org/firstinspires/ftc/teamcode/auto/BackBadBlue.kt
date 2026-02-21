package org.firstinspires.ftc.teamcode.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.pedroPathing.InheritableAuto

@Autonomous(name = "Back Bad Blue", group = "main")
open class BackBadBlue : InheritableAuto() {
    val subsystems = Subsystems()
    var shotSets = 0
    var reset = false

    lateinit var path: PathChain

    override fun loop() {
        super.loop()
        subsystems.flywheel(1260.0)
    }

    override fun buildPathChains() {
        path = linearPathChain(Pose(60.0, 12.0, 0.0), Pose(30.0, 24.0, 0.0))
    }

    override fun pathUpdate() {
        when (pathState) {
            0 -> busy {
                obeliskTag()
                if (pathTimer.elapsedTimeSeconds > 6) {
                    setAndResetPathTimer(1)
                }
            }

            1 -> busy {
                if (obeliskState == ObeliskStates.NONE) obeliskState = ObeliskStates.GPP
                if (shotSets == 0) {
                    subsystems.motifShot()
                    shotSets++
                }
                if (pathTimer.elapsedTimeSeconds > 4) setAndResetPathTimer(2)
            }

        }
    }
}