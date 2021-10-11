
package io.cmichel.boilerplate;

import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.robotemi.sdk.BatteryData;
import com.robotemi.sdk.Robot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Module extends ReactContextBaseJavaModule {

  private static final String DURATION_SHORT_KEY = "SHORT";
  private static final String DURATION_LONG_KEY = "LONG";

  public Module(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  private static Robot robot = Robot.getInstance();

  @Override
  public String getName() {
    return "Boilerplate";
  }

  @Override
  public Map<String, Object> getConstants()
  {
    final Map<String, Object> constants = new HashMap<>();
    constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
    constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
    return constants;
  }
  @ReactMethod
  public void getBatteryInfo(int duration)
  {
    BatteryData batteryData = robot.getBatteryData();
    Toast.makeText(getReactApplicationContext(), batteryData.getBatteryPercentage() + " percent battery and charging.",duration).show();
  }

  @ReactMethod
  public void temiMovement(float x,float y)
  {
    robot.skidJoy(x,y);
  }

  @ReactMethod
  public void goToLocation(String Location)
  {
    robot.goTo(Location);
  }


//  @ReactMethod
//  public void show(String command, int duration) {
//
//    robot.goTo(mUser.getTarget());
////    BatteryData batteryData = robot.getBatteryData();
////    if (batteryData == null) {
////      Toast.makeText(getReactApplicationContext(), "Battery data is null", duration).show();
////
////      return;
////    }
////    if (batteryData.isCharging()) {
////      Toast.makeText(getReactApplicationContext(), batteryData.getBatteryPercentage() + " percent battery and charging.", duration).show();
////
////    } else {
////      Toast.makeText(getReactApplicationContext(), batteryData.getBatteryPercentage() + " percent battery and not charging.", duration).show();
////    }
//
//    //public void navigateTheRobo(Robot robot, String command, Date lastUpdatedDate) {
//      switch("UP") {
//        case "UP":
//          Log.d("Navigation UP ", String.valueOf(command));
//          robot.skidJoy(1,0);
//          break;
//        case "DOWN":
//          Log.d("Navigation DOWN ", String.valueOf(command));
//          robot.skidJoy(-1,0);
//          break;
//        case "LEFT":
//          Log.d("Navigation LEFT ", String.valueOf(command));
//          robot.skidJoy(0,1);
//          break;
//        case "RIGHT":
//          Log.d("Navigation RIGHT ", String.valueOf(command));
//          robot.skidJoy(0,-1);
//          break;
//        default:
//          robot.stopMovement();
//      }
//      //Sleep for 500ms
//      //robot.skidJoy(1,0);
//    //}//
//
//  }
}

