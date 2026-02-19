package org.firstinspires.ftc.teamcode.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.pedroPathing.InheritableAuto

@Autonomous(name = "Auto Test", group = "main")
open class Test : InheritableAuto() {
    var didIt = false

    val poses = mapOf(
        "start" to Pose(24.0, 24.0, Math.toRadians(0.0)),
        "camera" to Pose(48.0, 48.0, Math.toRadians(0.0))
    )

    lateinit var path: PathChain

    override fun loop() {
        robot.setStartingPose(poses["start"])

        super.loop()

        pathUpdate()
        log("time", motifShot.timer)
    }

    override fun buildPathChains() {
        path = robot.pathBuilder()
            .addPath(BezierLine(poses["start"], poses["camera"]))
            .build()
    }

    override fun pathUpdate() {
        if (!didIt) {
            busy { robot.followPath(path, true) }
            didIt = true
        }
    }



}