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
        private var followerConstants: FollowerConstants = FollowerConstants().mass(11.8)
        private var pathConstraints: PathConstraints = PathConstraints(0.99, 100.0, 1.0, 1.0)
        private var mecanumConstants: MecanumConstants =
            MecanumConstants()
                .maxPower(1.0)
                .leftFrontMotorName("leftFront")
                .leftRearMotorName("leftRear")
                .rightFrontMotorName("rightFront")
                .rightRearMotorName("rightRear")
                .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
                .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
                .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
                .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
        private var localizerConstants: ThreeWheelIMUConstants =
            ThreeWheelIMUConstants().IMU_HardwareMapName("imu").IMU_Orientation(
                RevHubOrientationOnRobot(
                    RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                    RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
            )

        @JvmStatic
        fun createFollower(hardwareMap: HardwareMap?): Follower {
            return FollowerBuilder(followerConstants, hardwareMap)
                .threeWheelIMULocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(mecanumConstants)
                .build()
        }
    }
}