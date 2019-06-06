/*
DRIVE SPEED IS A MEASURE OF HOW FAST THE ROBOT GOES
INCHES IS HOW FAR THE WHEELS MOVE IN ENCODER DRIVE
THE TIMEOUT STOPS THE ROBOT AT THAT TIME IF IT DOESN'T REACH THE DISTANCE IN THE ALLOWED TIME
Can turn robot by setting motor power of one motor or two
Can also turn robot by using encoder drive with same signs on left and right values
Can go forwards or backwards by setting motors to opposite powers or encoder drive
The monster wheel diameter is 5 inches.
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
// left=1
//right=-1
@Autonomous
public class TestAutoOp extends LinearOpMode {
    //public static class thread extends Thread{

    //}
    // Declare OpMode members.
    private ColorSensor csensor1;
    private ColorSensor csensor2;
    private DcMotor motorLeft1;
    private DcMotor motorRight1;
    private DcMotor latch;
    private DcMotor sweeper;
    //    private CRServo wrist;
//    private CRServo fingers;
//    private DcMotor elbow;
//    private DcMotor extension;
    private ElapsedTime runtime = new ElapsedTime();
    static final double COUNTS_PER_MOTOR_REV = 1440;    // eg: TETRIX Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 2.0;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 5.0;     // For figuring circumference.
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double DRIVE_SPEED = 0.4;
    static final double TURN_SPEED = 0.5;
    static final double ARM_SPEED = 1.0;

    @Override
    public void runOpMode() {

        // Initialize the drive system variables.
        latch = hardwareMap.get(DcMotor.class, "latch");
        sweeper = hardwareMap.get(DcMotor.class, "sweeper");
        motorLeft1 = hardwareMap.get(DcMotor.class, "motorLeft1");
        motorRight1 = hardwareMap.get(DcMotor.class, "motorRight1");
//        elbow=hardwareMap.get(DcMotor.class, "elbow");
//        extension = hardwareMap.get(DcMotor.class, "extension");
//        wrist = hardwareMap.get(CRServo.class, "wrist");
//        fingers = hardwareMap.get(CRServo.class, "fingers");
        csensor1 = hardwareMap.colorSensor.get("csensor1");
        csensor2 = hardwareMap.colorSensor.get("csensor2");
        // Send telemetry message to signify robot waiting.
        telemetry.addData("Status", "Resetting Encoders");
        telemetry.update();

        motorLeft1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRight1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        latch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeft1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRight1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        latch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset.
        telemetry.addData("Path0", "Starting at %7d :%7d",
                motorLeft1.getCurrentPosition(),
                motorRight1.getCurrentPosition(),
                latch.getCurrentPosition());
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        //CENTER MINERAL CODE
        //****************************************************************************************
        // Unlatch. Currently commented out so testing can continue without it.
        /*
        runtime.reset();
        if(!opModeIsActive()) return;
         latch.setPower(-1);
         while (opModeIsActive() && runtime.seconds() < 4.0) {
        }
        latch.setPower(0);
        if (!opModeIsActive()) return;
        runtime.reset();
        */

        // Section: move mineral in front of crater


        // First forward move to move towards the minerals.
        while (opModeIsActive() && runtime.seconds() < 1.0) {
        }
        if (!opModeIsActive()) return;
        runtime.reset();
        //(Some compensation turns for the wheels' inability to move straight)
        //encoderDrive(DRIVE_SPEED/2, -0.5, -0.5, 5.0);
        encoderDrive(DRIVE_SPEED, 8.5, -8.5, 5.0);


        // Move back after nudging the mineral

        if (!opModeIsActive()) return;
        runtime.reset();

        encoderDrive(DRIVE_SPEED, -5, 5, 5.0);
        // Lower latch. Currently commented out. May decide to lower this while performing other
        // actions to speed up code.


        //Left turn to compensate for wheels

        //encoderDrive(DRIVE_SPEED/2, 0.5, 0.5, 5.0);

        //Driving to the depot

        if (!opModeIsActive()) return;
        runtime.reset();

        //motorLeft.setPower(0.4);
        //while (opModeIsActive() && !isStopRequested() && runtime.seconds() < 1) {
        //}
        encoderDrive(DRIVE_SPEED, 4.25, 4.25, 5.0);
        if (!opModeIsActive()) return;
        runtime.reset();

        encoderDrive(DRIVE_SPEED, 11, -11, 8.0);
        if (!opModeIsActive()) return;
        runtime.reset();

        encoderDrive(DRIVE_SPEED, 4.5, 4.5, 5.0);
        if (!opModeIsActive()) return;

        encoderDrive(DRIVE_SPEED, 14.5, -14.5, 5.0);
        if (!opModeIsActive()) return;
        runtime.reset();
        sweeper.setPower(0.75);
        while (opModeIsActive() && !isStopRequested() && runtime.seconds() < 0.033) {
        }

        if (!opModeIsActive()) return;
        runtime.reset();
        sweeper.setPower(-0.9);
        while (opModeIsActive() && !isStopRequested() && runtime.seconds() < 0.1) {
        }
        sweeper.setPower(0);
        encoderDrive(DRIVE_SPEED, 0.5, 0.5, 20.0);
        encoderDrive(DRIVE_SPEED, -13.5, 13.5, 20.0);
        encoderDrive(DRIVE_SPEED, 0.65, 0.65, 20.0);
        encoderDrive(DRIVE_SPEED, -15.5, 15.5, 20.0);

        //***************************************************************************************
        //Right mineral crater
        /*
        // Unlatch. Currently commented out so testing can continue without it.

        //if(!opModeIsActive()) return;
        // latch.setPower(-1);
        // while (opModeIsActive() && runtime.seconds() < 3.0) {
        //}
        //latch.setPower(0);
        if (!opModeIsActive()) return;
        runtime.reset();


        // Section: move mineral in front of crater


        // First forward move to move towards the minerals.
        while (opModeIsActive() && runtime.seconds() < 1.0) {
        }
        if (!opModeIsActive()) return;
        runtime.reset();
        encoderDrive(DRIVE_SPEED, -1, -1, 5.0);
        //(Some compensation turns for the wheels' inability to move straight)
        //encoderDrive(DRIVE_SPEED/2, -0.5, -0.5, 5.0);
        encoderDrive(DRIVE_SPEED, 10.5, -10.5, 5.0);


        // Move back after nudging the mineral

        if (!opModeIsActive()) return;
        runtime.reset();

        encoderDrive(DRIVE_SPEED, -6, 6, 5.0);
        // Lower latch. Currently commented out. May decide to lower this while performing other
        // actions to speed up code.
        if (!opModeIsActive()) return;
        runtime.reset();
        //latch.setPower(1);
        //while (opModeIsActive() && !isStopRequested() && runtime.seconds() < 3.0) {
        //}
        // if (!opModeIsActive()) return;
        //latch.setPower(0);

        //Left turn to compensate for wheels

        //encoderDrive(DRIVE_SPEED/2, 0.5, 0.5, 5.0);

        //Driving to the depot

        if (!opModeIsActive()) return;
        runtime.reset();

        //Turn towards the depot
        encoderDrive(DRIVE_SPEED, 5, 5, 5.0);
        if (!opModeIsActive()) return;
        runtime.reset();
        //Go to the depot
        encoderDrive(DRIVE_SPEED, 16.5, -16.5, 8.0);
        if (!opModeIsActive()) return;
        runtime.reset();

        encoderDrive(DRIVE_SPEED, 2.25, 2.25, 5.0);
        if (!opModeIsActive()) return;

        encoderDrive(DRIVE_SPEED, 12, -12, 5.0);
        if (!opModeIsActive()) return;
        runtime.reset();
        sweeper.setPower(0.25);
        while (opModeIsActive() && !isStopRequested() && runtime.seconds() < 0.1) {
        }

        if (!opModeIsActive()) return;
        runtime.reset();
        sweeper.setPower(-0.25);
        while (opModeIsActive() && !isStopRequested() && runtime.seconds() < 0.3) {
        }
        sweeper.setPower(0);
        encoderDrive(DRIVE_SPEED, 0.25, 0.25, 20.0);
        encoderDrive(DRIVE_SPEED, -13.5, 13.5, 20.0);
        encoderDrive(DRIVE_SPEED, 0.5, 0.5, 20.0);
        encoderDrive(DRIVE_SPEED, -13.5, 13.5, 20.0);
        */
        //**************************************************************************************
        //Left mineral crater.
        // Unlatch. Currently commented out so testing can continue without it.
        /*
        //if(!opModeIsActive()) return;
        // latch.setPower(-1);
        // while (opModeIsActive() && runtime.seconds() < 3.0) {
        //}
        //latch.setPower(0);
        if (!opModeIsActive()) return;
        runtime.reset();


        // Section: move mineral in front of crater


        // First forward move to move towards the minerals.
        while (opModeIsActive() && runtime.seconds() < 1.0) {
        }
        if (!opModeIsActive()) return;
        runtime.reset();
        encoderDrive(DRIVE_SPEED, 1, 1, 5.0);
        //(Some compensation turns for the wheels' inability to move straight)
        //encoderDrive(DRIVE_SPEED/2, -0.5, -0.5, 5.0);
        encoderDrive(DRIVE_SPEED, 10, -10, 5.0);


        // Move back after nudging the mineral

        if (!opModeIsActive()) return;
        runtime.reset();

        encoderDrive(DRIVE_SPEED, -6, 6, 5.0);
        // Lower latch. Currently commented out. May decide to lower this while performing other
        // actions to speed up code.
        if (!opModeIsActive()) return;
        runtime.reset();
        //latch.setPower(1);
        //while (opModeIsActive() && !isStopRequested() && runtime.seconds() < 3.0) {
        //}
        // if (!opModeIsActive()) return;
        //latch.setPower(0);

        //Left turn to compensate for wheels


        //encoderDrive(DRIVE_SPEED/2, 0.5, 0.5, 5.0);

        //Driving to the depot

        if (!opModeIsActive()) return;
        runtime.reset();

        //motorLeft.setPower(0.4);
        //while (opModeIsActive() && !isStopRequested() && runtime.seconds() < 1) {
        //}
        encoderDrive(DRIVE_SPEED, 4.25, 4.25, 5.0);
        if (!opModeIsActive()) return;
        runtime.reset();

        encoderDrive(DRIVE_SPEED, 11, -11, 8.0);
        if (!opModeIsActive()) return;
        runtime.reset();

        encoderDrive(DRIVE_SPEED, 4.5, 4.5, 5.0);
        if (!opModeIsActive()) return;

        encoderDrive(DRIVE_SPEED, 14.5, -14.5, 5.0);
        if (!opModeIsActive()) return;
        runtime.reset();
        sweeper.setPower(0.75);
        while (opModeIsActive() && !isStopRequested() && runtime.seconds() < 0.033) {
        }

        if (!opModeIsActive()) return;
        runtime.reset();
        sweeper.setPower(-0.9);
        while (opModeIsActive() && !isStopRequested() && runtime.seconds() < 0.1) {
        }
        sweeper.setPower(0);
        encoderDrive(DRIVE_SPEED, 0.5, 0.5, 20.0);
        encoderDrive(DRIVE_SPEED, -13.5, 13.5, 20.0);
        encoderDrive(DRIVE_SPEED, 0.65, 0.65, 20.0);
        encoderDrive(DRIVE_SPEED, -15.5, 15.5, 20.0);
*/
    }

    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = motorLeft1.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = motorRight1.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            motorLeft1.setTargetPosition(newLeftTarget);
            motorRight1.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            motorLeft1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motorRight1.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            motorLeft1.setPower(Math.abs(speed));
            motorRight1.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (motorLeft1.isBusy() && motorRight1.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        motorLeft1.getCurrentPosition(),
                        motorRight1.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            motorLeft1.setPower(0);
            motorRight1.setPower(0);

            // Turn off RUN_TO_POSITION
            motorLeft1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motorRight1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            // sleep(250); // optional pause after each move
        }
    }
}