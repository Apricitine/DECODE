import com.pedropathing.ftc.localization.Encoder
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.ElapsedTime

@TeleOp(name = "Basic", group = "Main")
class Basic : OpMode() {
    lateinit var right: DcMotorEx
    lateinit var left: DcMotorEx
    lateinit var strafe: DcMotorEx

    override fun init() {
        right = hardwareMap.get(DcMotorEx::class.java, "leftFront")
        left = hardwareMap.get(DcMotorEx::class.java, "flywheel")
        strafe = hardwareMap.get(DcMotorEx::class.java, "rightFront")
        right.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        right.mode = DcMotor.RunMode.RUN_USING_ENCODER
        left.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        left.mode = DcMotor.RunMode.RUN_USING_ENCODER
        strafe.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        strafe.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }


    override fun loop() {
        telemetry.addData("right", right.currentPosition)
        telemetry.addData("left", left.currentPosition)
        telemetry.addData("strafe", strafe.currentPosition)
        telemetry.update()
    }
}