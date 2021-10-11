package io.cmichel.boilerplate;

import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.robotemi.sdk.BatteryData;
import com.robotemi.sdk.Robot;

import java.util.HashMap;
import java.util.Map;

public class Module extends ReactContextBaseJavaModule {

  private static final String DURATION_SHORT_KEY = "SHORT";
  private static final String DURATION_LONG_KEY = "LONG";

  public Module(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "Boilerplate";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
    constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
    return constants;
  }

  @ReactMethod
  public void show(String message, int duration) {
    Robot robot = Robot.getInstance();
    BatteryData batteryData = robot.getBatteryData();
    if (batteryData == null) {
      Toast.makeText(getReactApplicationContext(), "Battery data is null", duration).show();

      return;
    }
    if (batteryData.isCharging()) {
      Toast.makeText(getReactApplicationContext(), batteryData.getBatteryPercentage() + " percent battery and charging.", duration).show();

    } else {
      Toast.makeText(getReactApplicationContext(), batteryData.getBatteryPercentage() + " percent battery and not charging.", duration).show();
    }

  }
}