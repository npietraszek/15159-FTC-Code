package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class MyFIRSTJavaTeleOpMode extends LinearOpMode {
    //private Gyroscope imu;
    //private DigitalChannel digitalTouch;
    //private DistanceSefensor sensorColorRange;
    private DcMotor motorLeft;
    private DcMotor motorRight;
    private CRServo servoClaw;
    private DcMotor motorArm;
    private DcMotor sweeper;
    @Override
    public void runOpMode() {
        double  position = 0.5;
        //imu = hardwareMap.get(Gyroscope.class, "imu");
        //digitalTouch = hardwareMap.get(DigitalChannel.class, "digitalTouch");
        //sensorColorRange = hardwareMap.get(DistanceSensor.class, "sensorColorRange");
        motorLeft = hardwareMap.get(DcMotor.class, "motorLeft");
        motorRight = hardwareMap.get(DcMotor.class, "motorRight");
        motorArm = hardwareMap.get(DcMotor.class, "latch");
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
            motorLeft.setPower(leftPower);
            motorRight.setPower(rightPower);
            // right/left at 1
            leftPower=-this.gamepad1.right_stick_x;
            rightPower=-this.gamepad1.right_stick_x;
            // if forwards/backwards is 0, right/left power is halved
            //leftPower*=(double)1/(2-Math.abs(gamepad1.left_stick_y));
            //rightPower*=(double)1/(2-Math.abs(gamepad1.left_stick_y));
            motorArm.setPower(gamepad2.left_stick_y/2);
            position-=gamepad2.right_stick_y/100;
            position=Math.min(position,1);
            position=Math.max(position,0);
            motorLeft.setPower(leftPower);
            motorRight.setPower(rightPower);
            sweeper.setPower(0);
            if (gamepad2.right_bumper){
                sweeper.setPower(-1);
            }
            else if (gamepad2.left_bumper){
                sweeper.setPower(1);
            }
            if (gamepad1.a) {
                servoClaw.setPower(1);
            } else if (gamepad1.y) {
                servoClaw.setPower(-1);
            } else {
                servoClaw.setPower(0);
            }
            telemetry.addData("Left Motor Power", motorLeft.getPower());
            telemetry.addData("Right Motor Power", motorRight.getPower());
            telemetry.addData("Servo Position", position);
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}