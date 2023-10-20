package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.outoftheboxrobotics.photoncore.Photon;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.common.centerstage.ClawSide;
import org.firstinspires.ftc.teamcode.common.commandbase.state.ClawCommand;
import org.firstinspires.ftc.teamcode.common.drive.drivetrain.MecanumDrivetrain;
import org.firstinspires.ftc.teamcode.common.drive.pathing.geometry.Pose;
import org.firstinspires.ftc.teamcode.common.hardware.Globals;
import org.firstinspires.ftc.teamcode.common.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.common.subsystem.ExtensionSubsystem;
import org.firstinspires.ftc.teamcode.common.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.common.util.MathUtils;

@Config
@TeleOp(name = "OpMode")
public class OpMode extends CommandOpMode {

    private final RobotHardware robot = RobotHardware.getInstance();
    private MecanumDrivetrain drivetrain;
    private ExtensionSubsystem extension;
    private IntakeSubsystem intake;

    private GamepadEx gamepadEx;
    private GamepadEx gamepadEx2;

    @Override
    public void initialize() {
        CommandScheduler.getInstance().reset();

        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry());
        Globals.IS_AUTO = false;
        Globals.IS_USING_IMU = true;

        gamepadEx = new GamepadEx(gamepad1);
        gamepadEx2 = new GamepadEx(gamepad2);

        robot.init(hardwareMap, telemetry);
        drivetrain = new MecanumDrivetrain();
        extension = new ExtensionSubsystem();
        intake = new IntakeSubsystem();
        robot.addSubsystem(drivetrain, extension, intake);

        gamepadEx.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whenPressed(new InstantCommand(() -> robot.intakeClawLeftServo.setPosition(0.13)));
        gamepadEx.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenPressed(new InstantCommand(() -> robot.intakeClawRightServo.setPosition(0.54)));
        gamepadEx.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(new SequentialCommandGroup(
                        new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(-0.025)),
                        new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(350)),
                        new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.455)),
                        new WaitCommand(250),
                        new ClawCommand(intake, IntakeSubsystem.ClawState.OPEN, ClawSide.BOTH)
                ))
                        .whenPressed(new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(-0.05))
                                .alongWith(new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(300))
                                        .alongWith(new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.435)))));
        gamepadEx.getGamepadButton(GamepadKeys.Button.B)
                        .whenPressed(new SequentialCommandGroup(
                                new ClawCommand(intake, IntakeSubsystem.ClawState.CLOSED, ClawSide.BOTH),
                                new WaitCommand(250),
                                new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(0.0)),
                                new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(10)),
                                new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.1))));
        gamepadEx.getGamepadButton(GamepadKeys.Button.X)
                .whenPressed(new SequentialCommandGroup(
                        new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(2.75)),
                        new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(300)),
                        new WaitCommand(200),
                        new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.67))
                ));
//        gamepadEx2.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
//                        .whenPressed(new InstantCommand(() -> robot.intakeClawLeftServo.setPosition(0.12)));
//        gamepadEx2.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
//                .whenPressed(new InstantCommand(() -> robot.intakeClawLeftServo.setPosition(0.53)));

        robot.read();
        while (opModeInInit()) {
            telemetry.addLine("Robot Initialized. Mason is very cool and he is the best perosn to ever exist in the owrld and java ois the owrst progmraming kanguage nad ih ate it so so os much LLL + Ratio + cope + cget out of my game L");
            telemetry.update();
        }
    }

    @Override
    public void run() {
        robot.clearBulkCache();
        robot.read();

        drivetrain.set(new Pose(gamepad1.left_stick_x, -gamepad1.left_stick_y, MathUtils.joystickScalar(-gamepad1.left_trigger + gamepad1.right_trigger, 0.01)), 0);

        // input
        super.run();
        robot.periodic();

        robot.write();
    }
}
