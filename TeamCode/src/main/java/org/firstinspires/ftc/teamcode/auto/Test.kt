package org.firstinspires.ftc.teamcode.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.pedroPathing.InheritableAuto

@Autonomous(name = "Auto Test", group = "main")
open class Test : InheritableAuto() {
    val poses = mapOf(
        "start" to Pose(21.0, 123.0, Math.toRadians(315.0)),
        "camera" to Pose(36.0, 108.0, Math.toRadians(45.0))
    )

    lateinit var path: PathChain

    override fun loop() {
        super.loop()

        log("time", motifShot.timer)
    }

    override fun buildPathChains() {
        path = robot.pathBuilder()
            .addPath(BezierLine(poses["start"], poses["camera"]))
            .build()
    }

    override fun pathUpdate() {
        robot.followPath(path)
    }



}