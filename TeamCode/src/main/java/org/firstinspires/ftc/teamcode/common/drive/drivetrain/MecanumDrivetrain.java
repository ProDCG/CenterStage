package org.firstinspires.ftc.teamcode.common.drive.drivetrain;

import com.arcrobotics.ftclib.drivebase.RobotDrive;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.common.drive.pathing.geometry.Pose;
import org.firstinspires.ftc.teamcode.common.drive.pathing.geometry.Vector2D;
import org.firstinspires.ftc.teamcode.common.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.common.util.wrappers.WSubsystem;

import java.util.Arrays;

public class MecanumDrivetrain extends WSubsystem implements Drivetrain {
    private RobotHardware robot = RobotHardware.getInstance();
//    public MecanumDrive drive;

//    public DcMotorEx frontLeft, frontRight, backLeft, backRight;

    double[] ws = new double[4];

    public MecanumDrivetrain(DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
//        this.frontLeft = frontLeft;
//        this.frontRight = frontRight;
//        this.backLeft = backLeft;
//        this.backRight = backRight;
//        this.drive = new MecanumDrive(frontLeft, frontRight, backLeft, backRight);
//        this.frontLeft = frontLeft;
//        this.frontRight = frontRight;
//        this.backLeft = backLeft;
//        this.backRight = backRight;

    }

    public MecanumDrivetrain() {
//        this.frontLeft = robot.dtFrontLeftMotor;
//        this.frontRight = robot.dtFrontRightMotor;
//        this.backLeft = robot.dtBackLeftMotor;
//        this.backRight = robot.dtBackRightMotor;
    }

    @Override
    public void set(Pose pose) {
        set(pose, 0);
    }

    public void set(double strafeSpeed, double forwardSpeed,
                    double turnSpeed, double gyroAngle) {

        Vector2D input = new Vector2D(strafeSpeed, forwardSpeed).rotate(-gyroAngle);

        strafeSpeed = input.x;
        forwardSpeed = input.y;

        double[] wheelSpeeds = new double[4];

        wheelSpeeds[RobotDrive.MotorType.kFrontLeft.value] = forwardSpeed + strafeSpeed + turnSpeed;
        wheelSpeeds[RobotDrive.MotorType.kFrontRight.value] = forwardSpeed - strafeSpeed - turnSpeed;
        wheelSpeeds[RobotDrive.MotorType.kBackLeft.value] = (forwardSpeed - strafeSpeed + turnSpeed);
        wheelSpeeds[RobotDrive.MotorType.kBackRight.value] = (forwardSpeed + strafeSpeed - turnSpeed);
        // 1.06, 1.04

        double max = Arrays.stream(wheelSpeeds).max().getAsDouble();

        if (Math.abs(max) > 1) {
            wheelSpeeds[RobotDrive.MotorType.kFrontLeft.value] /= max;
            wheelSpeeds[RobotDrive.MotorType.kFrontRight.value] /= max;
            wheelSpeeds[RobotDrive.MotorType.kBackLeft.value] /= max;
            wheelSpeeds[RobotDrive.MotorType.kBackRight.value] /= max;
        }

        ws[0] = wheelSpeeds[0];
        ws[1] = wheelSpeeds[1];
        ws[2] = wheelSpeeds[2];
        ws[3] = wheelSpeeds[3];
    }

    public void set(Pose pose, double angle) {
        set(pose.x, pose.y, pose.heading, angle);
//        double strafeSpeed = MathUtils.clamp(pose.x, -1, 1);
//        double forwardSpeed = MathUtils.clamp(pose.heading, -1, 1);
//        double turnSpeed = MathUtils.clamp(pose.y, -1, 1);
//
//        Vector2D input = new Vector2D(strafeSpeed, forwardSpeed);
//        input = input.rotate(-angle);
//
//        double theta = input.angle();
//
//        double[] wheelSpeeds = new double[4];
//        wheelSpeeds[0] = Math.sin(theta + Math.PI / 4);
//        wheelSpeeds[1] = Math.sin(theta - Math.PI / 4);
//        wheelSpeeds[2] = Math.sin(theta - Math.PI / 4);
//        wheelSpeeds[3] = Math.sin(theta + Math.PI / 4);
//
//        normalize(wheelSpeeds, input.magnitude());
//
//        wheelSpeeds[0] += turnSpeed;
//        wheelSpeeds[1] -= turnSpeed;
//        wheelSpeeds[2] += turnSpeed;
//        wheelSpeeds[3] -= turnSpeed;
//
//        normalize(wheelSpeeds);
//
//        ws[0] = wheelSpeeds[0];
//        ws[1] = wheelSpeeds[1];
//        ws[2] = wheelSpeeds[2];
//        ws[3] = wheelSpeeds[3];
    }

    @Override
    public void periodic() {
        // Nothing here
    }

    @Override
    public void read() {
        // Nothing here
    }

    @Override
    public void write() {
//        robot.dtFrontLeftMotor.setPower(ws[0]);
//        robot.dtFrontRightMotor.setPower(ws[1]);
//        robot.dtBackLeftMotor.setPower(ws[2]);
//        robot.dtBackRightMotor.setPower(ws[3]);

        robot.dtFrontLeftMotor.setPower(ws[0]);
        robot.dtFrontRightMotor.setPower(ws[1]);
        robot.dtBackLeftMotor.setPower(ws[2]);
        robot.dtBackRightMotor.setPower(ws[3]);
    }

    @Override
    public void reset() {

    }

    public String toString() {
        return "WS0: " + ws[0] + "WS1: " + ws[1] + "WS2: " + ws[2] + "WS3: " + ws[3];

    }
}
