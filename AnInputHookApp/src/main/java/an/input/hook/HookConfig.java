package an.input.hook;

public class HookConfig
{
 static final String[] defCfgValStr = {
  "监控模式:0 // 可填值有0和1两种模式",
  "记录类名:0 // 1记录，0不记录"
 };
 static final int[] defCfgValInt = {
  0,
  0
 };
 
 static int hookMode = -1;
 static int noteClassName = -1;
 
 static int[] getConfigArray()
 {
  return new int[]{hookMode,noteClassName};
 }
 
 static void setConfig(int[] array)
 {
  hookMode = array[0];
  noteClassName = array[1];
 }
 
 static boolean initedValue()
 {
  int[] array = getConfigArray();
  for(int i : array)
  {
   if(i == -1)return false;
  }
  return true;
 }
 
 private HookConfig(){}
}
