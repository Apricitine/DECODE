package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.Subsystems
import org.openftc.apriltag.AprilTagDetection

@Autonomous(name = "idek", group = "Test")
class Inheritable : Subsystems() {
    lateinit var tag: AprilTagDetection
    lateinit var currentPose: Pose

    val runtime = ElapsedTime()

    object Poses {
        val startPose = Pose()
    }

    override fun init() {
        initializeSubsystems()
        initializeProcessor()

        follower = Constants.createFollower(hardwareMap)
        follower.setStartingPose(Poses.startPose)
    }

    override fun loop() {
        follower.update()
        panelsTelemetry.update()
        currentPose = follower.pose
    }

}