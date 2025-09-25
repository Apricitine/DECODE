package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.pedropathing.ftc.FollowerBuilder
import com.pedropathing.ftc.drivetrains.MecanumConstants
import com.pedropathing.ftc.localization.constants.ThreeWheelIMUConstants
import com.pedropathing.paths.PathConstraints
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

class Constants {
    companion object {
        private var followerConstants: FollowerConstants = FollowerConstants().mass(5.0)
        private var pathConstraints: PathConstraints = PathConstraints(0.99, 100.0, 1.0, 1.0)
        private var driveConstants: MecanumConstants =
            MecanumConstants()
                .maxPower(1.0)
                .leftFrontMotorName("leftFront")
                .leftRearMotorName("leftBack")
                .rightFrontMotorName("rightFront")
                .rightRearMotorName("rightBack")
                .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
                .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
                .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
                .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
        private var localizerConstants: ThreeWheelIMUConstants =
            ThreeWheelIMUConstants().IMU_HardwareMapName("imu").IMU_Orientation(
                RevHubOrientationOnRobot(
                    RevHubOrientationOnRobot.LogoFacingDirection.UP,
                    RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
                )
            );


        @JvmStatic
        fun createFollower(hardwareMap: HardwareMap?): Follower {
            return FollowerBuilder(followerConstants, hardwareMap)
                .threeWheelIMULocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .build()
        }
    }
}