package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.control.FilteredPIDFCoefficients
import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.pedropathing.ftc.FollowerBuilder
import com.pedropathing.ftc.drivetrains.MecanumConstants
import com.pedropathing.ftc.localization.Encoder
import com.pedropathing.ftc.localization.constants.ThreeWheelConstants
import com.pedropathing.ftc.localization.constants.ThreeWheelIMUConstants
import com.pedropathing.ftc.localization.constants.TwoWheelConstants
import com.pedropathing.paths.PathConstraints
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.PIDFCoefficients

class Constants {
    companion object {
        private var followerConstants: FollowerConstants =
            FollowerConstants().mass(13.51)
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
        var localizerConstants: ThreeWheelIMUConstants =
            ThreeWheelIMUConstants()
                .leftPodY(4.6875)
                .rightPodY(-4.6875)
                .strafePodX(-3.5)
                .leftEncoder_HardwareMapName("flywheel")
                .rightEncoder_HardwareMapName("rightRear")
                .strafeEncoder_HardwareMapName("rightFront")
                .leftEncoderDirection(Encoder.FORWARD)
                .rightEncoderDirection(Encoder.FORWARD)
                .strafeEncoderDirection(Encoder.FORWARD)
                .IMU_HardwareMapName("imu")
                .IMU_Orientation(
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