package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.ConditionalCommand;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.common.centerstage.ClawSide;
import org.firstinspires.ftc.teamcode.common.commandbase.state.ClawCommand;
import org.firstinspires.ftc.teamcode.common.drive.drivetrain.MecanumDrivetrain;
import org.firstinspires.ftc.teamcode.common.drive.localizer.ThreeWheelLocalizer;
import org.firstinspires.ftc.teamcode.common.drive.pathing.geometry.Pose;
import org.firstinspires.ftc.teamcode.common.drive.pathing.geometry.profile.ProfileConstraints;
import org.firstinspires.ftc.teamcode.common.hardware.Globals;
import org.firstinspires.ftc.teamcode.common.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.common.subsystem.ExtensionSubsystem;
import org.firstinspires.ftc.teamcode.common.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.common.util.MathUtils;

@Config
@TeleOp(name = "TuningOpMode")
public class TuningOpMode extends CommandOpMode {

    private final RobotHardware robot = RobotHardware.getInstance();
    private ExtensionSubsystem extension;
    private IntakeSubsystem intake;

    private GamepadEx gamepadEx;

    private double loopTime = 0.0;
    private static double armPivot = 0.0;

    public static double targetpos = 0.1;

    // 1.3, 0, 0.035
    public static double P = 1.3;
    public static double I = 0.0;
    public static double D = 0.035;
    public static double F = 0.0;

    public static double A = 4.7;
    public static double V = 20.0;
    public static double Decel = 4.0;

    @Override
    public void initialize() {
        CommandScheduler.getInstance().reset();

        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry());
        Globals.IS_AUTO = false;
        Globals.IS_USING_IMU = true;

        gamepadEx = new GamepadEx(gamepad1);

        robot.init(hardwareMap, telemetry);
        extension = new ExtensionSubsystem();
        intake = new IntakeSubsystem();
        robot.addSubsystem(extension, intake);

        robot.intakePivotActuator.setTargetPosition(targetpos);
        robot.intakePivotActuator.write();

//        gamepadEx.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
//                .whenPressed(new ConditionalCommand(
//                        new ClawCommand(intake, IntakeSubsystem.ClawState.INTERMEDIATE, ClawSide.LEFT),
//                        new ClawCommand(intake, IntakeSubsystem.ClawState.OPEN, ClawSide.LEFT),
//                        () -> (intake.leftClaw == (IntakeSubsystem.ClawState.CLOSED))
//                ));
//
//        gamepadEx.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
//                .whenPressed(new ConditionalCommand(
//                        new ClawCommand(intake, IntakeSubsystem.ClawState.INTERMEDIATE, ClawSide.RIGHT),
//                        new ClawCommand(intake, IntakeSubsystem.ClawState.OPEN, ClawSide.RIGHT),
//                        () -> (intake.rightClaw == (IntakeSubsystem.ClawState.CLOSED))
//                ));
//
//        gamepadEx.getGamepadButton(GamepadKeys.Button.A)
//                .whenPressed(new ConditionalCommand(
//                        new ClawCommand(intake, IntakeSubsystem.ClawState.INTERMEDIATE, ClawSide.BOTH),
//                        new ClawCommand(intake, IntakeSubsystem.ClawState.OPEN, ClawSide.BOTH),
//                        () -> (intake.rightClaw == (IntakeSubsystem.ClawState.CLOSED) || (intake.leftClaw == IntakeSubsystem.ClawState.CLOSED))
//                ));
//
//        gamepadEx2.getGamepadButton(GamepadKeys.Button.A)
//                .whenPressed(new SequentialCommandGroup(
//                        new InstantCommand(() -> extension.setScoring(false)),
//                        new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(-0.025)),
//                        new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(350)),
//                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.FLAT)),
//                        new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.455)),
//                        new WaitCommand(250),
//                        new ClawCommand(intake, IntakeSubsystem.ClawState.OPEN, ClawSide.BOTH)
//                ))
//                .whenPressed(new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(-0.05))
//                        .alongWith(new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(300))
//                                .alongWith(new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.435)))));
//        gamepadEx2.getGamepadButton(GamepadKeys.Button.B)
//                .whenPressed(
//                        new ConditionalCommand(
//                                new SequentialCommandGroup(
//                                        new InstantCommand(() -> extension.setScoring(false)),
//                                        new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(0.0)),
//                                        new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(0)),
//                                        new WaitCommand(250),
//                                        new ClawCommand(intake, IntakeSubsystem.ClawState.CLOSED, ClawSide.BOTH),
//                                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.STORED)),
//                                        new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.1))),
//                                new SequentialCommandGroup(
//                                        new InstantCommand(() -> extension.setScoring(false)),
//                                        new ClawCommand(intake, IntakeSubsystem.ClawState.CLOSED, ClawSide.BOTH),
//                                        new WaitCommand(250),
//                                        new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(0.0)),
//                                        new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(0)),
//                                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.STORED)),
//                                        new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.1))),
//                                () -> extension.getScoring())
//
//
//                );
//        gamepadEx2.getGamepadButton(GamepadKeys.Button.X)
//                .whenPressed(new SequentialCommandGroup(
//                        new InstantCommand(() -> extension.setScoring(true)),
//                        new InstantCommand(() -> extension.setUpdated(false)),
//                        new WaitCommand(200),
//                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.SCORING))
//                ));
//        gamepadEx2.getGamepadButton(GamepadKeys.Button.DPAD_UP)
//                .whenPressed(new InstantCommand(() -> extension.incrementBackdropHeight(1)));
//        gamepadEx2.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
//                .whenPressed(new InstantCommand(() -> extension.incrementBackdropHeight(-1)));
//
//
//

//        gamepadEx.getGamepadButton(GamepadKeys.Button.A)
//                .whenPressed(new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(0.0)));
//        gamepadEx.getGamepadButton(GamepadKeys.Button.B)
//                .whenPressed(new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(2 * Math.PI / 3)));
//        gamepadEx.getGamepadButton(GamepadKeys.Button.X)
        gamepadEx.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(() -> armPivot = 0.0);
        gamepadEx.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(() -> armPivot = 2 * Math.PI / 3);
        gamepadEx.getGamepadButton(GamepadKeys.Button.X)
                .whenPressed(() -> robot.pitchActuator.setMotionProfile(armPivot, new ProfileConstraints(V, A, Decel)));

        //                .whenPressed(new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(armPivot)));
        // combination of angle and extension amount, get minimums, get maximums, math.map
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

        // input
        super.run();
        robot.periodic();

        robot.pitchActuator.setPID(P, I, D);

//        telemetry.addData("extension", robot.extensionActuator.getPosition());
//        telemetry.addData("angle", robot.pitchActuator.getPosition());
        telemetry.addData("LEVEL", extension.getBackdropHeight());
//        telemetry.addData("targetAngle", extension.t_angle);
//        telemetry.addData("targetExtension", extension.t_extension);
//        telemetry.addData("diffX", extension.diff_x);
//        telemetry.addData("diffy", extension.diff_y);
//        telemetry.addData("velocity", localizer.getNewPoseVelocity());
        telemetry.addData("arm position", robot.pitchActuator.getPosition());
        telemetry.addData("arm target position", robot.pitchActuator.getTargetPosition());
        double loop = System.nanoTime();
        telemetry.addData("hz ", 1000000000 / (loop - loopTime));
        loopTime = loop;
        telemetry.update();
        robot.write();
    }
}
