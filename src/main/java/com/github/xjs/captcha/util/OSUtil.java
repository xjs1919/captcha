/** 
 * copyright(c) 2019-2029 mamcharge.com
 */
 
package com.github.xjs.captcha.util;
/**
 *  判断OS的类型
 *
 * @author xujs@mamcharge.com
 * @date 2019/11/14 16:45
 **/
public class OSUtil {
    private static final String WINDOWS = "WINDOWS";
    private static final double FREE_TOTALL_PERCENT = 0.05;

    /**
     * 判断当前操作系统的类型
     *
     * @return false means window system ,true means unix like system
     */
    public static boolean isUnixLikeSystem() {
        String name = System.getProperty("os.name");
        if (name != null) {
            if (name.toUpperCase().indexOf(WINDOWS) == -1) { // it means it's unix like system
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前JVM可用内存大小
     *
     * @return
     */
    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    /**
     * 检测JVN当前是否会内存泄露
     *
     * @return
     */
    public static boolean checkJVMHealth() {
        double freeMem = Runtime.getRuntime().freeMemory();
        double totlMem = Runtime.getRuntime().totalMemory();
        double percent = freeMem / totlMem;
        if (percent <= FREE_TOTALL_PERCENT) {
            return true;
        }
        return false;
    }
}
