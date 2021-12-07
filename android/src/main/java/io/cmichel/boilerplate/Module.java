package io.cmichel.boilerplate;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.common.internal.Ints;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;
import com.robotemi.sdk.BatteryData;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.map.Layer;
import com.robotemi.sdk.map.MapDataModel;
import com.robotemi.sdk.map.MapImage;
import com.robotemi.sdk.map.MapModel;
import com.robotemi.sdk.permission.Permission;
import com.robotemi.sdk.sequence.SequenceModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Module extends ReactContextBaseJavaModule implements OnGoToLocationStatusChangedListener, LifecycleEventListener {

    private static final String DURATION_SHORT_KEY = "SHORT";
    private static final String DURATION_LONG_KEY = "LONG";

    private final ReactApplicationContext reactContext;

    //private ExecutorService  executor= Executors.newSingleThreadExecutor();

    public Module(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        Robot.getInstance().addOnGoToLocationStatusChangedListener(this);
        reactContext.addLifecycleEventListener(this);

    }

    private static Robot robot = Robot.getInstance();

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
    public void exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid());

    }

    /*
    Get Battery info
    * */
    @ReactMethod
    public void getBatteryInfo(final Promise promise)
    {
        BatteryData batteryData = robot.getBatteryData();
        String charging ="N";
        WritableMap map = new WritableNativeMap();
        Log.d("isbattery Charging : ", ""+batteryData.isCharging());
        if(batteryData.isCharging())
        {
             charging ="Y";
        }
        map.putString("batteryPercent",String.valueOf(batteryData.getBatteryPercentage()));
        map.putString("batteryIsCharging",charging);
        promise.resolve(map);
    }

        /*
     Get Battery :is charging
     * */
    @ReactMethod
    public void getBatteryChargingInfo(final Promise promise) {
        boolean isCharging = robot.getBatteryData().isCharging();
        promise.resolve(isCharging);
    }


    @ReactMethod
    public void playSequence(String sequenceId) {
        callSequence(sequenceId);
    }

    private void callSequence(String sequenceId)
    {
        checkPermission();
        Log.d("id: ", sequenceId);
        int i=0;
        for (SequenceModel x : robot.getAllSequences()) {
            if (x.getId().equals(sequenceId)) {
                Log.d(" Sequence id present ", sequenceId);
                i=1;
                break;
            }
        }
        if(i==0){
            Log.d(" Sequence id  not prst ", sequenceId);
        }
        Log.d("Sequence id : ", sequenceId);
        if (sequenceId != "")
        {
            robot.playSequence(sequenceId);
        }
    }


    public void checkPermission()
    {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.SEQUENCE);
        robot.requestPermissions(permissions, 4);
    }

  @ReactMethod
  public void getSequenceData(final Promise promise)
  {
      try
      {
          checkPermission();
          List<SequenceModel> sequenceList = robot.getAllSequences();
          Log.d("Size ", "--"+sequenceList.size());
          Log.d("Size1 ", "--"+sequenceList.toString());

          Gson g = new Gson();
          Log.i("sequenceList",""+sequenceList);
          WritableArray array = new WritableNativeArray();
          for (SequenceModel co : sequenceList)
          {
             Log.i("model",""+co);
             Log.i("","");
             JSONObject jo = new JSONObject(g.toJson(co));
             Log.i("jo",""+jo);
             WritableMap wm = convertJsonToMap(jo);
             Log.i("wm",wm.toString());
              array.pushMap(wm);
         }
            Log.i("array",array.toString());
          promise.resolve(array);
  } catch (Exception e)
      {
          Log.d("Sequene error", "Error" + e);
          promise.reject(e);
    }
  }

    @SuppressLint("LongLogTag")
    public void requestMap() {

//
//        Log.d("map Model ----", "Inside request Map Method");
//
//        List<Permission> permissions = new ArrayList<>();
//        permissions.add(Permission.MAP);
//        robot.requestPermissions(permissions, 4);
//
//        executor.execute(()->{
//            MapDataModel mapModel = robot.getMapData();
//            MapImage k = mapModel.getMapImage();
//            Log.d("Inside exceutor-", "mapModel" + k.getData().toString() +"End------------------");
//            Log.d("Inside exceutor-", "mapModel size" + k.getData().size());
//
//            Log.d("Inside exceutor-", "mapModel Info" + mapModel.getMapInfo());
//
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            ArrayList<Integer> ik = new ArrayList<Integer>();
//
//            int m =0;
//            for (Integer t : k.getData()) {
//
//                ik.add(Color.argb((int) (t * 2.55), 0, 0, 0));
//                m++;
//            }
//
//            Log.d("Inside exceutor-", "loop size" + m);
//            int[] ret = new int[ik.size()];
//            for (int i = 0; i < ret.length; i++) {
//                ret[i] = ik.get(i);
//            }
//
//            Log.d("inside executor----", "ret[i] -->" + ret);
//
//            Log.d("inside executor- ----", "col -->" + k.getCols());
//            Log.d("inside executor- ----", "row -->" + k.getRows());
//
//            Bitmap b = Bitmap.createBitmap(ret, k.getCols(), k.getRows(), Bitmap.Config.ARGB_8888);
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            //add support for jpg and more.
//            b.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
//            byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
//
//            Log.d("inside executor-----", "encoded value"+encoded+"-------END--");
//
//        });
//
//
//
//        MapDataModel mapModel = robot.getMapData();
//        MapImage k = mapModel.getMapImage();
//
//        Log.d("map Model ----", "Load Map" + mapModel.getMapId());
//        Log.d("map Model ----", "Load Map" + mapModel.getVirtualWalls());
//        Log.d("Map Data", "Image Model ==>" + k.getData());
//        Log.d("Map Data", "Image Model Size ==>" + k.getData().size());
//
//        Log.d("map Model ----", "Printing getLocation Details" + mapModel.getLocations());
//        Log.d("map Model ----", "Printing getLocation Size" + mapModel.getLocations().size());
//        for (Layer l1 : mapModel.getLocations()) {
//
//            Log.d("Layer Location of Id :" + l1.getLayerId(), "  Description :" + l1.describeContents());
//            Log.d("Layer Location of Id :" + l1.getLayerId(), "  Layer Category :" + l1.describeContents());
//            Log.d("Layer Location of Id :" + l1.getLayerId(), "  Layer Creation UTC :" + l1.getLayerCreationUTC());
//            Log.d("Layer Location of Id :" + l1.getLayerId(), "  Layer Thickness :" + l1.getLayerThickness());
//            Log.d("Layer Location of Id :" + l1.getLayerId(), "  Layer Posses :" + l1.getLayerPoses());
//        }
//
//
//        Log.d("map Model ----", "Printing Virtual wall Details" + mapModel.getVirtualWalls());
//        Log.d("map Model ----", "Printing Virtual wall Details Size" + mapModel.getVirtualWalls().size());
//        for (Layer l1 : mapModel.getVirtualWalls()) {
//            Log.d("Virtual wall of Id :" + l1.getLayerId(), "  Description :" + l1.describeContents());
//            Log.d("Virtual wall of Id :" + l1.getLayerId(), "  Layer Category :" + l1.describeContents());
//            Log.d("Virtual wall of Id :" + l1.getLayerId(), "  Layer Creation UTC :" + l1.getLayerCreationUTC());
//            Log.d("Virtual wall of Id :" + l1.getLayerId(), "  Layer Thickness :" + l1.getLayerThickness());
//            Log.d("Virtual wall of Id :" + l1.getLayerId(), "  Layer Posses :" + l1.getLayerPoses());
//        }
//        ArrayList<Integer> ik = new ArrayList<Integer>();
//        for (Integer t : k.getData()) {
//            ik.add(Color.argb((int) (t * 2.55), 0, 0, 0));
//        }
//
//        int[] ret = new int[ik.size()];
//        for (int i = 0; i < ret.length; i++) {
//            ret[i] = ik.get(i);
//        }
//
//        Log.d("Int arrayy ----", "ret[i] -->" + ret);
//
//        Log.d("Int arrayy ----", "col -->" + k.getCols());
//        Log.d("Int arrayy ----", "row -->" + k.getRows());
//
//        Bitmap b = Bitmap.createBitmap(ret, k.getCols(), k.getRows(), Bitmap.Config.ALPHA_8);
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        //add support for jpg and more.
//        b.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
//
//        Log.d("encoded", "ALPHA_8--------->" + encoded);
//        Log.d("encoded", "ALPHA_8 to Stirng ------------------>" + encoded.toString());
//
//
//        Bitmap b2 = Bitmap.createBitmap(ret, k.getCols(), k.getRows(), Bitmap.Config.ARGB_8888);
//
//        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
//        //add support for jpg and more.
//        b2.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream2);
//        byte[] byteArray2 = byteArrayOutputStream2.toByteArray();
//
//        String encoded2 = Base64.encodeToString(byteArray2, Base64.DEFAULT);
//
//        Log.d("encoded", "value ARGB_8888--------->" + encoded2+"-----END-------");
//        Log.d("encoded", "value to ARGB_8888 ------------------>" + encoded2.toString());


//        Log.d("map Model ----","Map zxzxzxModel "+mapModel);
//
//        Log.d("map Model ----","Map image"+mapModel.getVirtualWalls().get(0).);
//
//        Log.d("map Model ----","Map image id"+mapModel.getMapId());
//        Log.d("map Model ----","Map image to string"+mapModel.getMapInfo().toString());
//
//        Log.d("map Model ----","Map image descriptionzxz"+mapModel.describeContents());
//
//

//        List<MapModel> mapModel1 =robot.getMapList();
//
//
//        for(MapModel mmap:mapModel1){
//            Log.d("map Model inside for loo-","Map Model"+mmap);
//
//        }
    }


//    @ReactMethod
//    public void getSequenceList(Callback successCallback) throws JSONException
//    {
//        List<Permission> permissions = new ArrayList<>();
//        permissions.add(Permission.SEQUENCE);
//        robot.requestPermissions(permissions, 4);
//
//        List<SequenceModel> sequenceList = robot.getAllSequences();
//        Log.d("Size ", "--" + robot.checkSelfPermission(Permission.SEQUENCE));
//
//        if (robot.checkSelfPermission(Permission.SEQUENCE) == Permission.GRANTED) {
//            robot.getInstance().speak(TtsRequest.create("Saved the " + sequenceList.size() + " location failed.", true));
//        }
//        Log.d("sequence ----", "" + sequenceList.get(0).component1());
//        Log.d("sequence o ----", "" + sequenceList.get(0));
//        Log.d("sequence ----", "" + sequenceList.get(0).component2());
//        Log.d("sequence ----", "" + sequenceList.get(0).component3());
//        Log.d("sequence ----", "" + sequenceList.get(0).component4());
//
//
//        for (String t : sequenceList.get(0).component5()) {
//            Log.d("++++++", "" + t);
//        }
//
//        robot.getInstance().speak(TtsRequest.create("Saved the " + sequenceList.size() + " location failed.", true));
//        Gson g = new Gson();
//
//        WritableArray array = new WritableNativeArray();
//        for (SequenceModel co : sequenceList) {
//            JSONObject jo = new JSONObject(g.toJson(co));
//            jo.put("id", co.getId());
//            jo.put("name", co.getName());
//            jo.put("desc", co.getDescription());
//            WritableMap wm = convertJsonToMap(jo);
//            array.pushMap(wm);
//
//        }
//
//        successCallback.invoke(array);
//    }

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
     */
    @ReactMethod
    public void temiMovement(float x, float y) {
        robot.skidJoy(x, y);
    }

    @ReactMethod
    public void stopMovement() {
        robot.stopMovement();
    }

    @ReactMethod
    public void goToLocation(String Location) {
        robot.goTo(Location);
    }

    @ReactMethod
    public void getAllLocation(final Promise promise) {
        try {
            StringBuffer str1 = new StringBuffer();
            List<String> getAllLocation = new ArrayList<>();
            for (String str : robot.getLocations()) {
                str1.append(str);
                getAllLocation.add(str);
            }
            //Toast.makeText(getReactApplicationContext(), "location:"+str1.toString()+ " ",Toast.LENGTH_LONG).show();
            WritableArray promiseArray = Arguments.createArray();
            for (int i = 0; i < getAllLocation.size(); i++) {
                promiseArray.pushString(getAllLocation.get(i));
            }
            // Toast.makeText(getReactApplicationContext(), "Promise::"+promiseArray+ " ",Toast.LENGTH_LONG).show();
            promise.resolve(promiseArray);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    /**
     * This is an example of saving locations. 1
     **/
    @ReactMethod
    public void saveLocation(String location) {
        boolean result = robot.saveLocation(location);
        if (result) {
            robot.speak(TtsRequest.create("I've successfully saved the " + location + " location.", true));
        } else {
            robot.speak(TtsRequest.create("Saved the " + location + " location failed.", true));
        }
        // hideKeyboard();
    }

    /**
     * tiltAngle controls temi's head by specifying which angle you want
     * to tilt to and at which speed.
     */

    @ReactMethod
    public void tiltAngle(int degree, float angle) {
        robot.tiltAngle(degree, angle);//--
    }

    /**
     * turnBy allows for turning the robot around in place. You can specify
     * the amount of degrees to turn by and at which speed.
     */

    @ReactMethod
    public void turnBy(int degree, float speed) {
        robot.turnBy(degree, speed);
    }

    /**
     * tiltBy is used to tilt temi's head from its current position.
     */

    @ReactMethod
    public void tiltBy(int degree, float speed) {
        robot.tiltBy(degree, speed);
    }

    @ReactMethod
    public void getVolume(final Promise promise) {
        try {
            int volume = robot.getVolume();
            promise.resolve(volume);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void showTopBar()
    {
//        Log.d("inside ----", "play sequence in showTop Bar");
//        String sequenceName = "test";
//        String sequenceId = "test";
//        for (SequenceModel x : robot.getAllSequences()) {
//            if (x.getName().equals(sequenceName)) {
//                sequenceId = x.getId();
//                break;
//            }
//        }
//        Log.d("Sequence id : ", sequenceId);
//        if (sequenceId != "") {
//            robot.playSequence(sequenceId);
//        }
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
        } catch (Exception e) {
            promise.reject(e);
        }

    }

    @ReactMethod
    public void getTemiSerialNumber(final Promise promise) {
        try {
            promise.resolve(robot.getSerialNumber());

           // requestMap();

        } catch (Exception e) {
            promise.reject(e);
        }

    }


    @Override
    public void onGoToLocationStatusChanged(@NotNull String location, String status, int descriptionId, @NotNull String description) {
        Log.i("GoToStabolerplate", "status=" + status + ", descriptionId=" + location + ", description=" + description);
        robot.speak(TtsRequest.create(description, false));
        String locStatus= "";
        switch (status) {
            case OnGoToLocationStatusChangedListener.START:
                locStatus ="Starting";
                robot.speak(TtsRequest.create("Starting", false));
                break;

            case OnGoToLocationStatusChangedListener.CALCULATING:
                locStatus ="Calculating";
                robot.speak(TtsRequest.create("Calculating", false));
                break;

            case OnGoToLocationStatusChangedListener.GOING:
                locStatus ="Going";
                robot.speak(TtsRequest.create("Going", false));
                break;

            case OnGoToLocationStatusChangedListener.COMPLETE:
                locStatus ="Completed";
                robot.speak(TtsRequest.create("Completed", false));
                break;

            case OnGoToLocationStatusChangedListener.ABORT:
                locStatus ="Cancelled";
                robot.speak(TtsRequest.create("Cancelled", false));
                break;
        }
       sendEvent(reactContext, "locStatus", locStatus);
  }

    public void sendEvent(ReactContext reactContext, String eventName, String params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName,params);
    }
    @Override
    public void onHostResume() {

    }
    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {

    }

//  @Override
//  public void onHostResume() {
//
//  }
//
//  @Override
//  public void onHostPause() {
//
//  }
//
//  @Override
//  public void onHostDestroy() {
//
//  }

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
   // }

}