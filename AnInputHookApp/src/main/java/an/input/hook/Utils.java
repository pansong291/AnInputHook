package an.input.hook;

import android.os.Environment;
import de.robv.android.xposed.XposedBridge;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Date;
import java.util.ArrayList;

public class Utils
{
 static String[] KEYCODE_NAME = new String[]{
  "UNKNOWN","SOFT_LEFT","SOFT_RIGHT","HOME","BACK","CALL",
  "ENDCALL","0","1","2","3","4","5","6","7","8","9","STAR",
  "POUND","DPAD_UP","DPAD_DOWN","DPAD_LEFT","DPAD_RIGHT",
  "DPAD_CENTER","VOLUME_UP","VOLUME_DOWN","POWER","CAMERA",
  "CLEAR","A","B","C","D","E","F","G","H","I","J","K",
  "L","M","N","O","P","Q","R","S","T","U","V","W","X",
  "Y","Z","COMMA","PERIOD","ALT_LEFT","ALT_RIGHT","SHIFT_LEFT",
  "SHIFT_RIGHT","TAB","SPACE","SYM","EXPLORER","ENVELOPE",
  "ENTER","DEL","GRAVE","MINUS","EQUALS","LEFT_BRACKET",
  "RIGHT_BRACKET","BACKSLASH","SEMICOLON","APOSTROPHE",
  "SLASH","AT","NUM","HEADSETHOOK","FOCUS","PLUS","MENU",
  "NOTIFICATION","SEARCH","MEDIA_PLAY_PAUSE","MEDIA_STOP",
  "MEDIA_NEXT","MEDIA_PREVIOUS","MEDIA_REWIND","MEDIA_FAST_FORWARD",
  "MUTE","PAGE_UP","PAGE_DOWN","PICTSYMBOLS","SWITCH_CHARSET",
  "BUTTON_A","BUTTON_B","BUTTON_C","BUTTON_X","BUTTON_Y",
  "BUTTON_Z","BUTTON_L1","BUTTON_R1","BUTTON_L2","BUTTON_R2",
  "BUTTON_THUMBL","BUTTON_THUMBR","BUTTON_START","BUTTON_SELECT",
  "BUTTON_MODE","ESCAPE","FORWARD_DEL","CTRL_LEFT","CTRL_RIGHT",
  "CAPS_LOCK","SCROLL_LOCK","META_LEFT","META_RIGHT",
  "FUNCTION","SYSRQ","BREAK","MOVE_HOME","MOVE_END","INSERT",
  "FORWARD","MEDIA_PLAY","MEDIA_PAUSE","MEDIA_CLOSE",
  "MEDIA_EJECT","MEDIA_RECORD","F1","F2","F3","F4","F5",
  "F6","F7","F8","F9","F10","F11","F12","NUM_LOCK","NUMPAD_0",
  "NUMPAD_1","NUMPAD_2","NUMPAD_3","NUMPAD_4","NUMPAD_5",
  "NUMPAD_6","NUMPAD_7","NUMPAD_8","NUMPAD_9","NUMPAD_DIVIDE",
  "NUMPAD_MULTIPLY","NUMPAD_SUBTRACT","NUMPAD_ADD","NUMPAD_DOT",
  "NUMPAD_COMMA","NUMPAD_ENTER","NUMPAD_EQUALS","NUMPAD_LEFT_PAREN",
  "NUMPAD_RIGHT_PAREN","VOLUME_MUTE","INFO","CHANNEL_UP",
  "CHANNEL_DOWN","ZOOM_IN","ZOOM_OUT","TV","WINDOW","GUIDE",
  "DVR","BOOKMARK","CAPTIONS","SETTINGS","TV_POWER","TV_INPUT",
  "STB_POWER","STB_INPUT","AVR_POWER","AVR_INPUT","PROG_RED",
  "PROG_GREEN","PROG_YELLOW","PROG_BLUE","APP_SWITCH",
  "BUTTON_1","BUTTON_2","BUTTON_3","BUTTON_4","BUTTON_5",
  "BUTTON_6","BUTTON_7","BUTTON_8","BUTTON_9","BUTTON_10",
  "BUTTON_11","BUTTON_12","BUTTON_13","BUTTON_14","BUTTON_15",
  "BUTTON_16","LANGUAGE_SWITCH","MANNER_MODE","3D_MODE",
  "CONTACTS","CALENDAR","MUSIC","CALCULATOR","ZENKAKU_HANKAKU",
  "EISU","MUHENKAN","HENKAN","KATAKANA_HIRAGANA","YEN",
  "RO","KANA","ASSIST","BRIGHTNESS_DOWN","BRIGHTNESS_UP",
  "MEDIA_AUDIO_TRACK","SLEEP","WAKEUP","PAIRING","MEDIA_TOP_MENU",
  "11","12","LAST_CHANNEL","TV_DATA_SERVICE","VOICE_ASSIST",
  "TV_RADIO_SERVICE","TV_TELETEXT","TV_NUMBER_ENTRY",
  "TV_TERRESTRIAL_ANALOG","TV_TERRESTRIAL_DIGITAL","TV_SATELLITE",
  "TV_SATELLITE_BS","TV_SATELLITE_CS","TV_SATELLITE_SERVICE",
  "TV_NETWORK","TV_ANTENNA_CABLE","TV_INPUT_HDMI_1","TV_INPUT_HDMI_2",
  "TV_INPUT_HDMI_3","TV_INPUT_HDMI_4","TV_INPUT_COMPOSITE_1",
  "TV_INPUT_COMPOSITE_2","TV_INPUT_COMPONENT_1","TV_INPUT_COMPONENT_2",
  "TV_INPUT_VGA_1","TV_AUDIO_DESCRIPTION","TV_AUDIO_DESCRIPTION_MIX_UP",
  "TV_AUDIO_DESCRIPTION_MIX_DOWN","TV_ZOOM_MODE","TV_CONTENTS_MENU",
  "TV_MEDIA_CONTEXT_MENU","TV_TIMER_PROGRAMMING","HELP",
  "NAVIGATE_PREVIOUS","NAVIGATE_NEXT","NAVIGATE_IN","NAVIGATE_OUT",
  "STEM_PRIMARY","STEM_1","STEM_2","STEM_3","DPAD_UP_LEFT",
  "DPAD_DOWN_LEFT","DPAD_UP_RIGHT","DPAD_DOWN_RIGHT",
  "MEDIA_SKIP_FORWARD","MEDIA_SKIP_BACKWARD","MEDIA_STEP_FORWARD",
  "MEDIA_STEP_BACKWARD","SOFT_SLEEP","CUT","COPY","PASTE",
  "SYSTEM_NAVIGATION_UP","SYSTEM_NAVIGATION_DOWN","SYSTEM_NAVIGATION_LEFT",
  "SYSTEM_NAVIGATION_RIGHT"};
 
 static String superDirectory = "/Android/data/an.input.hook";
 
 public static int getHookMode(String pn)
 {
  int hookMode = -1;
  try{
   File file = new File(Environment.getExternalStorageDirectory().getPath() + superDirectory);
   if(!file.exists())
   {
    file.mkdirs();
   }
   File fileConfig = new File(file, "配置.txt");
   if(!fileConfig.exists())
   {
    PrintStream ps = new PrintStream(new FileOutputStream(fileConfig, true));
    ps.println("//这是单行注释，以两个斜除号开头，注释的内容会被忽略掉");
    ps.println("//配置文件有固定格式，请勿随意修改");
    ps.println("//需重启手机才能生效");
    ps.println("监控模式:0 //可填值有0和1两种模式");
    ps.close();
    hookMode = 0;
   }else
   {
    BufferedReader bufRead = new BufferedReader(new FileReader(fileConfig));
    String readStr = null;
    while((readStr = bufRead.readLine()) != null)
    {
     int outcomIndex = readStr.indexOf("//");
     if(outcomIndex == 0)continue;
     else if(outcomIndex > 0)
     {
      readStr = readStr.substring(0, outcomIndex);
     }
     readStr = readStr.replaceAll("\\s", "");
     if(readStr.contains("监控模式"))
     {
      hookMode = Integer.parseInt(readStr.split(":")[1]);
     }
    }
    bufRead.close();
    if(hookMode < 0)
    {
     PrintStream ps = new PrintStream(new FileOutputStream(fileConfig, true));
     ps.println("监控模式:0 //可填值有0和1两种模式");
     ps.close();
     hookMode = 0;
    }
   }

   log2F("hookMode:" + hookMode);
  }catch(Exception e)
  {
   XposedBridge.log(e.getMessage()+"\n文件读写异常:"+pn);
   writeLogFile(e.getMessage()+"\n文件读写异常:"+pn);
  }
  return hookMode;
 }

 public static boolean exceptPackage(String packageName)
 {
  boolean except = false;
  try{
   File file = new File(Environment.getExternalStorageDirectory().getPath() + superDirectory);
   if(!file.exists())
   {
    file.mkdirs();
   }
   File fileExcept = new File(file, "过滤名单.txt");

   if(!fileExcept.exists())
   {
    PrintStream ps = new PrintStream(new FileOutputStream(fileExcept, true));
    ps.println("//这是单行注释，以两个斜除号开头，注释的内容会被忽略掉");
    ps.println("//过滤名单里填写包名，一行一个包名，过滤的包将不会被记录");
    ps.println("//需重启手机才能生效");
    ps.println("com.google.android.webview //Android System WebView");
    ps.close();
    except = packageName.equals("com.google.android.webview");
   }else
   {
    BufferedReader bufRead = new BufferedReader(new FileReader(fileExcept));
    String readStr = null;
    while((readStr = bufRead.readLine()) != null)
    {
     int outcomIndex = readStr.indexOf("//");
     if(outcomIndex == 0)continue;
     else if(outcomIndex > 0)
     {
      readStr = readStr.substring(0, outcomIndex);
     }
     readStr = readStr.replaceAll("\\s","");
     except = packageName.equals(readStr);
     log2F(packageName + "读取过滤名单:" + readStr + "=" + except);
     if(except)break;
    }
    bufRead.close();
   }
  }catch(Exception e)
  {
   XposedBridge.log(e.getMessage()+"\n文件读写异常:"+packageName);
   writeLogFile(e.getMessage()+"\n文件读写异常:"+packageName);
  }
  return except;
 }

 public static ArrayList<String> getExceptList()
 {
  ArrayList<String> exceptList = new ArrayList<String>();
  try{
   File file = new File(Environment.getExternalStorageDirectory().getPath() + superDirectory);
   if(!file.exists())
   {
    file.mkdirs();
   }
   File fileExcept = new File(file, "过滤名单.txt");

   if(!fileExcept.exists())
   {
    PrintStream ps = new PrintStream(new FileOutputStream(fileExcept, true));
    ps.println("//这是单行注释，以两个斜除号开头，注释的内容会被忽略掉");
    ps.println("//过滤名单里填写包名，一行一个包名，过滤的包将不会被记录");
    ps.println("//需重启手机才能生效");
    ps.println("com.google.android.webview //Android System WebView");
    ps.close();
    exceptList.add("com.google.android.webview");
   }else
   {
    BufferedReader bufRead = new BufferedReader(new FileReader(fileExcept));
    String readStr = null;
    while((readStr = bufRead.readLine()) != null)
    {
     int outcomIndex = readStr.indexOf("//");
     if(outcomIndex == 0)continue;
     else if(outcomIndex > 0)
     {
      readStr = readStr.substring(0, outcomIndex);
     }
     readStr = readStr.replaceAll("\\s","");
     if(!readStr.isEmpty())exceptList.add(readStr);
     log2F("读取过滤名单:"+readStr);
    }
    bufRead.close();
   }
  }catch(Exception e)
  {
   XposedBridge.log(e.getMessage());
   writeLogFile(e.getMessage());
  }
  return exceptList;
 }
 
 public static void writeToFile(String str)
 {
  try{
   Date today = new Date();
   File file = new File(Environment.getExternalStorageDirectory().getPath() + superDirectory + "/files");
   if(!file.exists())file.mkdirs();
   PrintStream ps = new PrintStream(new FileOutputStream(
                                     new File(file, String.format("%tY年%tm月%td日.txt",today,today,today)), true));
   ps.println(str);
   ps.close();
  }catch(Exception e)
  {
   XposedBridge.log(e.getMessage());
   writeLogFile(e.getMessage());
  }
 }

 public static void writeLogFile(String str)
 {
  try{
   Date today = new Date();
   File file = new File(Environment.getExternalStorageDirectory().getPath() + superDirectory + "/logs");
   if(!file.exists())file.mkdirs();
   PrintStream ps = new PrintStream(new FileOutputStream(
                                     new File(file, String.format("%tY年%tm月%td日.log",today,today,today)), true));
   ps.println(str);
   ps.close();
  }catch(Exception e)
  {
   XposedBridge.log(e.getMessage());
  }
 }

 public static String getKeyCodeName(int keyCode)
 {
  String KeyCodeName = "KEYCODE_" + KEYCODE_NAME[keyCode];
  return KeyCodeName;
 }
 
 public static void log2F(String str)
 {
  log(str);
  if(BuildConfig.DEBUG)writeLogFile(str);
 }
 
 public static void log(String str)
 {
  if(BuildConfig.DEBUG)XposedBridge.log(str);
 }
 
}
