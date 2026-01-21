package org.firstinspires.ftc.teamcode.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.pedroPathing.InheritableAuto

@Autonomous(name = "Auto Test", group = "main")
open class Test : InheritableAuto() {
    val subsystems = Subsystems()
    var shotSets = 0

    override fun loop() {
        super.loop()

        log("time", motifShot.timer)
    }

    override fun buildPathChains() {}

    override fun pathUpdate() {
        obeliskState = ObeliskStates.GPP
        if (shotSets == 0) {
            subsystems.motifShot()
            shotSets++
        }
    }



}