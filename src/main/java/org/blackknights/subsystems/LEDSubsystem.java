/* Black Knights Robotics (C) 2025 */
package org.blackknights.subsystems;

import com.ctre.phoenix.led.*;
import com.ctre.phoenix.led.CANdle.LEDStripType;
import com.ctre.phoenix.led.CANdle.VBatOutputMode;
import com.ctre.phoenix.led.ColorFlowAnimation.Direction;
import com.ctre.phoenix.led.LarsonAnimation.BounceMode;
import com.ctre.phoenix.led.TwinkleAnimation.TwinklePercent;
import com.ctre.phoenix.led.TwinkleOffAnimation.TwinkleOffPercent;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.blackknights.constants.LEDConstants;

public class LEDSubsystem extends SubsystemBase {
    private final CANdle candle = new CANdle(LEDConstants.CANDLE_ID, "rio");

    private Animation toAnimate = null;

    private int red;
    private int green;
    private int blue;
    private double brightness = 1.0;

    public enum AnimationTypes {
        ColorFlow,
        Fire,
        Larson,
        Rainbow,
        RgbFade,
        SingleFade,
        GreenStrobe,
        YellowStrobe,
        RedStrobe,
        Twinkle,
        TwinkleOff,
        None
    }

    public LEDSubsystem() {
        setAnimation(AnimationTypes.None);
        CANdleConfiguration configAll = new CANdleConfiguration();
        configAll.statusLedOffWhenActive = true;
        configAll.disableWhenLOS = false;
        configAll.stripType = LEDStripType.GRB;
        configAll.brightnessScalar = 0.1;
        configAll.vBatOutputMode = VBatOutputMode.Modulated;
        candle.configAllSettings(configAll, 100);
    }

    /**
     * Set the color of the LEDs.
     *
     * <p>Only call <b><u>once</u></b>.
     *
     * @param r The red value (0-255)
     * @param g The green value (0-255)
     * @param b The blue value (0-255)
     * @param bright The brightness (0-1)
     */
    public void setColors(int r, int g, int b, double bright) {
        setAnimation(AnimationTypes.None);
        red = r;
        green = g;
        blue = b;
        brightness = bright;
    }

    /**
     * Set the animation of the LEDs.
     *
     * <p>Only call <b><u>once</u></b>.
     *
     * @param toChange The animation to set
     */
    public void setAnimation(AnimationTypes toChange) {
        int ledCount = LEDConstants.LED_COUNT;
        switch (toChange) {
            case ColorFlow:
                toAnimate =
                        new ColorFlowAnimation(128, 20, 70, 0, 0.7, ledCount, Direction.Forward);
                break;
            case Fire:
                toAnimate = new FireAnimation(0.5, 0.7, ledCount, 0.7, 0.5);
                break;
            case Larson:
                toAnimate = new LarsonAnimation(0, 255, 46, 0, 1, ledCount, BounceMode.Front, 3);
                break;
            case Rainbow:
                toAnimate = new RainbowAnimation(1, 0.1, ledCount);
                break;
            case RgbFade:
                toAnimate = new RgbFadeAnimation(0.7, 0.4, ledCount);
                break;
            case SingleFade:
                toAnimate = new SingleFadeAnimation(50, 2, 200, 0, 0.5, ledCount);
                break;
            case GreenStrobe:
                toAnimate = new StrobeAnimation(0, 255, 0, 0, 98.0 / 256.0, ledCount);
                break;
            case YellowStrobe:
                toAnimate = new StrobeAnimation(255, 255, 0, 0, 98.0 / 256.0, ledCount);
                break;
            case RedStrobe:
                toAnimate = new StrobeAnimation(255, 0, 0, 0, 98.0 / 256.0, ledCount);
                break;
            case Twinkle:
                toAnimate =
                        new TwinkleAnimation(30, 70, 60, 0, 0.4, ledCount, TwinklePercent.Percent6);
                break;
            case TwinkleOff:
                toAnimate =
                        new TwinkleOffAnimation(
                                70, 90, 175, 0, 0.8, ledCount, TwinkleOffPercent.Percent100);
                break;
            case None:
                toAnimate = null;
                break;
        }
        System.out.println("Changed to " + toChange);
    }

    @Override
    public void periodic() {
        if (toAnimate == null) {
            candle.setLEDs(red, green, blue);
        } else {
            candle.animate(toAnimate);
        }
        candle.modulateVBatOutput(brightness);
    }
}
