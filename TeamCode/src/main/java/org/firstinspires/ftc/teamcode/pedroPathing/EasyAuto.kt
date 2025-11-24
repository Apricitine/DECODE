/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.firstinspires.ftc.robotcontroller.external.samples

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.ElapsedTime

@Autonomous(name = "Easy Auto", group = "Robot")
class EasyAuto : LinearOpMode() {
    lateinit var leftFront: DcMotorEx
    lateinit var rightFront: DcMotorEx
    lateinit var leftRear: DcMotorEx
    lateinit var rightRear: DcMotorEx

    private val runtime = ElapsedTime()


    override fun runOpMode() {

        leftFront = hardwareMap.get(DcMotorEx::class.java, "leftFront")
        rightFront = hardwareMap.get(DcMotorEx::class.java, "rightFront")
        leftRear = hardwareMap.get(DcMotorEx::class.java, "leftRear")
        rightRear = hardwareMap.get(DcMotorEx::class.java, "rightRear")

        leftFront.direction = DcMotorSimple.Direction.REVERSE
        leftRear.direction = DcMotorSimple.Direction.REVERSE

        telemetry.addData("Status", "Ready to run")
        telemetry.update()

        waitForStart()


        leftFront.power = FORWARD_SPEED
        leftRear.power = FORWARD_SPEED
        rightFront.power = FORWARD_SPEED
        rightRear.power = FORWARD_SPEED

        runtime.reset()
        while (opModeIsActive() && (runtime.seconds() < 1)) {
            telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds())
            telemetry.update()
        }

        leftFront.power = 0.0
        leftRear.power = 0.0
        rightFront.power = 0.0
        rightRear.power = 0.0

        telemetry.addData("Path", "Complete")
        telemetry.update()
        sleep(1000)
    }

    companion object {
        const val FORWARD_SPEED: Double = 0.5
    }
}