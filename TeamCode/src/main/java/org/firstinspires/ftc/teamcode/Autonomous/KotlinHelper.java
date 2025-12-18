package org.firstinspires.ftc.teamcode.Autonomous;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.teleop.Inheritable;
import org.firstinspires.ftc.teamcode.util.Button;

public class KotlinHelper extends Inheritable {
    public Button trueButton = new Button();
    public Button falseButton = new Button();
    Telemetry telemetry;

    @Override
    public void loop() {
        telemetry.addLine("Problem -- Shoiuld not be here!");
        telemetry.update();
    }
    public void init(){
        trueButton.update(true);
        falseButton.update(false);
    }
}
