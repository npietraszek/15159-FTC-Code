/*
Relatively simple teleOp. Controller 1 is responsible for the wheels of the robot.
Controller 2 is responsible for the latch and sweeper.
For gold, ratio of blue to red is 2:1.
For silver, the ratio is relatively 1:1.
This will be our way of finding the blocks
Readings for silver are over 80-90 for all.
Readings for gold are around 16-24-17 and 24-32-23
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class FinalTeleOp extends LinearOpMode {
    //private Gyroscope imu;
    //private DigitalChannel digitalTouch;
    private DcMotor motorLeft1;
    private DcMotor motorRight1;
    private CRServo servoClaw;
    private DcMotor latch;
    private DcMotor sweeper;
    @Override
    public void runOpMode() {
        double  position = 0.5;
        motorLeft1 = hardwareMap.get(DcMotor.class, "motorLeft1");
        motorRight1 = hardwareMap.get(DcMotor.class, "motorRight1");
        latch = hardwareMap.get(DcMotor.class, "latch");
        sweeper = hardwareMap.get(DcMotor.class, "sweeper");
        servoClaw = hardwareMap.get(CRServo.class, "servoClaw");
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        // run until the end of the match (driver presses STOP)
        double leftPower = 0;
        double rightPower = 0;
        while (opModeIsActive()) {
            // forwards/backwards at 1
            leftPower=-this.gamepad1.left_stick_y;
            rightPower=this.gamepad1.left_stick_y;
            motorLeft1.setPower(leftPower);
            motorRight1.setPower(rightPower);
            // right/left at 1
            leftPower=-this.gamepad1.right_stick_x;
            rightPower=-this.gamepad1.right_stick_x;
            // if forwards/backwards is 0, right/left power is halved
            //leftPower*=(double)1/(2-Math.abs(gamepad1.left_stick_y));
            //rightPower*=(double)1/(2-Math.abs(gamepad1.left_stick_y));
            latch.setPower(gamepad2.left_stick_y/2);
            position-=gamepad2.right_stick_y/100;
            position=Math.min(position,1);
            position=Math.max(position,0);

            sweeper.setPower(0);
            if (gamepad2.right_bumper){
                sweeper.setPower(-1);
            }
            else if (gamepad2.left_bumper){
                sweeper.setPower(1);
            }

           //Full Power button
            if(gamepad1.dpad_up) {
                rightPower = -1;
                leftPower = 1;
            } else if(gamepad1.dpad_down){
                rightPower = 1;
                leftPower = -1;
            }
            //Set the non encoder mode for full power
            motorLeft1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motorRight1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motorLeft1.setPower(leftPower);
            motorRight1.setPower(rightPower);
            telemetry.addData("Left Motor Power", motorLeft1.getPower());
            telemetry.addData("Right Motor Power", motorRight1.getPower());

            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}