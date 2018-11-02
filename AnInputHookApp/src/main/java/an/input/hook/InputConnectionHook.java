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
   writeToFile(new Date().toLocaleString().split(" ")[1]
               + ", 包:" + lpp.packageName
               + ", 类:" + param.thisObject.getClass().getCanonicalName()
               + ", 事件:添加, 位置:" + cursorStartIndex + "-" +cursorEndIndex
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
    keyName += ", 位置:" + cursorStartIndex + "-" + cursorEndIndex
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
   writeToFile(new Date().toLocaleString().split(" ")[1]
               + ", 包:" + lpp.packageName
               + ", 类:" + param.thisObject.getClass().getCanonicalName()
               + ", 事件:" + operation
               + ", 位置:" + cursorStartIndex + "-" + cursorEndIndex
               + ", 内容:" + value);
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
   cursorStartIndex = param.args[0];
   cursorEndIndex = param.args[1];
   if(BuildConfig.DEBUG)
    writeLogFile("光标位置="+cursorStartIndex+"-"+cursorEndIndex);
  }
 }
 
 
}
