package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.pedropathing.ftc.FollowerBuilder
import com.pedropathing.paths.PathConstraints
import com.qualcomm.robotcore.hardware.HardwareMap

class Constants {
    companion object {
        var followerConstants: FollowerConstants = FollowerConstants()
        var pathConstraints: PathConstraints = PathConstraints(0.99, 100.0, 1.0, 1.0)

        @JvmStatic
        fun createFollower(hardwareMap: HardwareMap?): Follower {
            return FollowerBuilder(followerConstants, hardwareMap).pathConstraints(pathConstraints)
                .build()
        }
    }
}