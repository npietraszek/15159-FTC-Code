package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous
public class test extends LinearOpMode {
    // Declare OpMode members.
    private DcMotor motorLeft;
    private DcMotor motorRight;
    private DcMotor latch;
    private DcMotor sweeper;
    private ElapsedTime runtime = new ElapsedTime();
    static final double     COUNTS_PER_MOTOR_REV    = 1440;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 2.0;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION)/(WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.4;
    static final double     TURN_SPEED              = 0.5;
    static final double     ARM_SPEED               = 1.0;
    @Override
    public void runOpMode() {

        // Initialize the drive system variables.
        motorLeft = hardwareMap.get(DcMotor.class, "motorLeft");
        motorRight = hardwareMap.get(DcMotor.class, "motorRight");
        latch = hardwareMap.get(DcMotor.class, "latch");
        sweeper = hardwareMap.get(DcMotor.class, "sweeper");
        // Send telemetry message to signify robot waiting.
        telemetry.addData("Status", "Resetting Encoders");
        telemetry.update();

        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        latch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        latch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset.
        telemetry.addData("Path0",  "Starting at %7d :%7d",
                motorLeft.getCurrentPosition(),
                motorRight.getCurrentPosition(),
                latch.getCurrentPosition());
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Unlatch.
        if(!opModeIsActive()) return;
        runtime.reset();
        latch.setPower(-1);
        while (opModeIsActive() && runtime.seconds()<4.5){
        }
        latch.setPower(0);
        if(!opModeIsActive()) return;
        runtime.reset();
        while (opModeIsActive() && runtime.seconds()<1){
        }
        // Clear latch.
        if(!opModeIsActive()) return;
        encoderDrive(DRIVE_SPEED,  5,  -5, 5.0);
        // Lower latch.
        if(!opModeIsActive()) return;
        runtime.reset();
        latch.setPower(1);
        while (opModeIsActive() && runtime.seconds()<4.5){
        }
        latch.setPower(0);
        // Move to depot.
        if(!opModeIsActive()) return;
        encoderDrive(DRIVE_SPEED,  20,  -20, 5.0);
        // Drop team marker.
        if(!opModeIsActive()) return;
        runtime.reset();
        sweeper.setPower(-0.2);
        while (opModeIsActive() && runtime.seconds()<0.4){
        }
        sweeper.setPower(0);
        // Move away from depot.
        if(!opModeIsActive()) return;
        encoderDrive(DRIVE_SPEED,   -20, 20, 5.0);
        if(!opModeIsActive()) return;
        /*
        encoderDrive(TURN_SPEED,   10, 10, 4.0);
        if(!opModeIsActive()) return;
        encoderDrive(DRIVE_SPEED,  10,  -10, 5.0);
        if(!opModeIsActive()) return;
        //robot.leftClaw.setPosition(1.0);
        //robot.rightClaw.setPosition(0.0);
        //sleep(1000);     // pause for servos to move
        encoderDrive(DRIVE_SPEED,-35,35,15);
        if(!opModeIsActive()) return;
        sleep(1000);     // pause for servos to move
        encoderDrive(TURN_SPEED,-3,-3,3);
        if(!opModeIsActive()) return;
        encoderDrive(DRIVE_SPEED,-5,5,15);
        if(!opModeIsActive()) return;
        */
        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = motorLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = motorRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            motorLeft.setTargetPosition(newLeftTarget);
            motorRight.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            motorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            motorLeft.setPower(Math.abs(speed));
            motorRight.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (motorLeft.isBusy() && motorRight.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        motorLeft.getCurrentPosition(),
                        motorRight.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            motorLeft.setPower(0);
            motorRight.setPower(0);

            // Turn off RUN_TO_POSITION
            motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            // sleep(250); // optional pause after each move
        }
    }

    /*
    Method to perform a relative move, based on encoder counts.
    Encoders are not reset as the move is based on the current position.
    Move will stop if any of three conditions occur:
    1) Move gets to the desired position
    2) Move runs out of time
    3) Driver stops the opmode running.
    */
    /*
    public void encoderArm(double speed,
                           double armInches,
                           double timeoutS) {
        int newArmTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newArmTarget = motorArm.getCurrentPosition() + (int)(armInches * COUNTS_PER_INCH);
            motorArm.setTargetPosition(newArmTarget);

            // Turn On RUN_TO_POSITION
            motorArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            motorArm.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (motorArm.isBusy())) {

                //Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newArmTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        motorArm.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            motorArm.setPower(0);

            // Turn off RUN_TO_POSITION
            motorArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            // sleep(250); // optional pause after each move
        }
    }
    */
}