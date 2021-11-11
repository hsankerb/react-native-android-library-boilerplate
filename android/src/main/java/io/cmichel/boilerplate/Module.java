package io.cmichel.boilerplate;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.google.gson.Gson;
import com.robotemi.sdk.BatteryData;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.permission.Permission;
import com.robotemi.sdk.sequence.SequenceModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
  public void exitApp()
  {
    android.os.Process.killProcess(android.os.Process.myPid());

  }

  /*
  Get Battery info
  * */
  @ReactMethod
  public void getBatteryInfo(final Promise promise)
  {
    BatteryData batteryData = robot.getBatteryData();
    promise.resolve(batteryData.getBatteryPercentage());
    //Toast.makeText(getReactApplicationContext(), batteryData.getBatteryPercentage() + " percent battery and charging.",duration).show();
  }

  @ReactMethod
  public void getSequenceList(Callback successCallback) throws JSONException
  {
    List<SequenceModel> sequenceList = Robot.getInstance().getAllSequences();
    Gson g = new Gson();

    WritableArray array = new WritableNativeArray();
    for (SequenceModel co : sequenceList)
    {
      JSONObject jo = new JSONObject(g.toJson(co));
      WritableMap wm = convertJsonToMap(jo);
      array.pushMap(wm);
    }
    successCallback.invoke(array);
  }


  private static WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException
  {
    WritableMap map = new WritableNativeMap();

    Iterator<String> iterator = jsonObject.keys();
    while (iterator.hasNext())
    {
      String key = iterator.next();
      Object value = jsonObject.get(key);
      if (value instanceof JSONObject) {
        map.putMap(key, convertJsonToMap((JSONObject) value));
      } else if (value instanceof Boolean) {
        map.putBoolean(key, (Boolean) value);
      } else if (value instanceof Integer) {
        map.putInt(key, (Integer) value);
      } else if (value instanceof Double) {
        map.putDouble(key, (Double) value);
      } else if (value instanceof String) {
        map.putString(key, (String) value);
      } else {
        map.putString(key, value.toString());
      }
    }
    return map;
  }

/**
 * for temi movements
 * */
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

  @ReactMethod
  public void getAllLocation(final Promise promise){
    try {
      StringBuffer str1 =new StringBuffer();
      List<String> getAllLocation = new ArrayList<>();
      for (String str : robot.getLocations())
      {
        str1.append(str);
        getAllLocation.add(str);
      }
      //Toast.makeText(getReactApplicationContext(), "location:"+str1.toString()+ " ",Toast.LENGTH_LONG).show();
      WritableArray promiseArray= Arguments.createArray();
      for(int i=0;i<getAllLocation.size();i++)
      {
        promiseArray.pushString(getAllLocation.get(i));
      }
     // Toast.makeText(getReactApplicationContext(), "Promise::"+promiseArray+ " ",Toast.LENGTH_LONG).show();
      promise.resolve(promiseArray);
    }
    catch(Exception e)
    {
      promise.reject(e);
    }
  }

  /**
       * This is an example of saving locations. 1
     **/
  @ReactMethod
  public void saveLocation(String location)
  {
    boolean result = robot.saveLocation(location);
    if (result)
    {
      robot.speak(TtsRequest.create("I've successfully saved the " + location + " location.", true));
    } else
      {
      robot.speak(TtsRequest.create("Saved the " + location + " location failed.", true));
    }
   // hideKeyboard();
  }

/**
 * tiltAngle controls temi's head by specifying which angle you want
 * to tilt to and at which speed.
 */

   @ReactMethod
    public void tiltAngle(int degree,float angle) {
        robot.tiltAngle(degree,angle);//--
    }

/**
 * turnBy allows for turning the robot around in place. You can specify
 * the amount of degrees to turn by and at which speed.
 */

 @ReactMethod
 public void turnBy(int degree,float speed) {
        robot.turnBy(degree,speed);
    }

/**
 * tiltBy is used to tilt temi's head from its current position.
 */

  @ReactMethod
  public void tiltBy(int degree,float speed) {
          robot.tiltBy(degree,speed);
      }

 @ReactMethod
  public void getVolume(final Promise promise) {
     try {
       int volume = robot.getVolume();
       promise.resolve(volume);
     }catch(Exception e)
     {
       promise.reject(e);
     }
  }

  @ReactMethod
  public void showTopBar() {
    robot.showTopBar();
  }

  @ReactMethod
  public void hideTopBar() {
    robot.hideTopBar();
  }

  @ReactMethod
  public void getTemiNickName(final Promise promise) {
    try {
      String name = robot.getNickName();
      promise.resolve(name);
    }catch(Exception e)
    {
      promise.reject(e);
    }

  }

  @ReactMethod
  public void getTemiSerialNumber(final Promise promise) {
    try {
      promise.resolve(robot.getSerialNumber());
    }catch(Exception e)
    {
      promise.reject(e);
    }

  }




//public void setVolume(View view) {
//        if (requestPermissionIfNeeded(Permission.SETTINGS, REQUEST_CODE_NORMAL)) {
//            return;
//        }
//        List<String> volumeList = new ArrayList<>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
//        final CustomAdapter adapter = new CustomAdapter(this, android.R.layout.simple_selectable_list_item, volumeList);
//        final AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("Set Volume")
//                .setAdapter(adapter, null)
//                .create();
//        dialog.getListView().setOnItemClickListener((parent, view1, position, id) -> {
//            robot.setVolume(Integer.parseInt(Objects.requireNonNull(adapter.getItem(position))));
//            Toast.makeText(TemiMainActivity.this, adapter.getItem(position), Toast.LENGTH_SHORT).show();
//            dialog.dismiss();
//        });
//        dialog.show();
//    }



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
//          break;,
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
//    //}
//
//
//  }
}