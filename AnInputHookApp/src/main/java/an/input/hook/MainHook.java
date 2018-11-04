package an.input.hook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage
{
 InputConnectionHook ich = null;
 TextWatcherHook twh = null;
 
 public MainHook()
 {
  Utils.log("    new MainHook()");
  ich = new InputConnectionHook();
  twh = new TextWatcherHook();
 }
 
 @Override
 public void handleLoadPackage(LoadPackageParam p1) throws Throwable
 {
  if(Utils.exceptPackage(p1.packageName))
  {
   Utils.log2F("已过滤 " + p1.packageName);
   return;
  }
  Utils.loadHookConfig(p1.packageName);
  switch(HookConfig.hookMode)
  {
   case 0:
    ich.hook(p1);
    break;
   case 1:
    twh.hook(p1);
    break;
  }
 }
 
}
