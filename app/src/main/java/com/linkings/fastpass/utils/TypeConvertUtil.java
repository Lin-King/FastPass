package com.linkings.fastpass.utils;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Lin on 2016/9/21.
 * Time: 17:22
 * Description: TOO 类型转换工具
 */

public class TypeConvertUtil {

    public static String nullOfString(String str) {
        if (str == null) {
            str = "";
        }
        return str;
    }

    public static byte stringToByte(String str) {
        byte b = 0;
        if (str != null) {
            try {
                b = Byte.parseByte(str);
            } catch (Exception e) {

            }
        }
        return b;
    }

    public static boolean stringToBoolean(String str) {
        if (str == null) {
            return false;
        } else {
            if (str.equals("1")) {
                return true;
            } else if (str.equals("0")) {
                return false;
            } else {
                try {
                    return Boolean.parseBoolean(str);
                } catch (Exception e) {
                    return false;
                }
            }
        }
    }

    public static int stringToInt(String str) {
        int i = 0;
        if (str != null) {
            try {
                i = Integer.parseInt(str.trim());
            } catch (Exception e) {
                i = 0;
            }

        } else {
            i = 0;
        }
        return i;
    }

    public static short stringToShort(String str) {
        short i = 0;
        if (str != null) {
            try {
                i = Short.parseShort(str.trim());
            } catch (Exception e) {
                i = 0;
            }
        } else {
            i = 0;
        }
        return i;
    }


    public static double stringToDouble(String str) {
        double i = 0;
        if (str != null) {
            try {
                i = Double.parseDouble(str.trim());
            } catch (Exception e) {
                i = 0;
            }
        } else {
            i = 0;
        }
        return i;
    }

    public static String intToString(int i) {
        String str = "";
        try {
            str = String.valueOf(i);
        } catch (Exception e) {
            str = "";
        }
        return str;
    }


    public static long doubleToLong(double d) {
        long lo = 0;
        try {
//double转换成long前要过滤掉double类型小数点后数据
            lo = Long.parseLong(String.valueOf(d).substring(0, String.valueOf(d).lastIndexOf(".")));
        } catch (Exception e) {
            lo = 0;
        }
        return lo;
    }

    public static int doubleToInt(double d) {
        int i = 0;
        try {
//double转换成long前要过滤掉double类型小数点后数据
            i = Integer.parseInt(String.valueOf(d).substring(0, String.valueOf(d).lastIndexOf(".")));
        } catch (Exception e) {
            i = 0;
        }
        return i;
    }

    public static double longToDouble(long d) {
        double lo = 0;
        try {
            lo = Double.parseDouble(String.valueOf(d));
        } catch (Exception e) {
            lo = 0;
        }
        return lo;
    }

    public static int longToInt(long d) {
        int lo = 0;
        try {
            lo = Integer.parseInt(String.valueOf(d));
        } catch (Exception e) {
            lo = 0;
        }
        return lo;
    }

    public static long stringToLong(String str) {
        Long li = new Long(0);
        try {
            li = Long.valueOf(str);
        } catch (Exception e) {
            //li = new Long(0);
        }
        return li.longValue();
    }

    public static String longToString(long li) {
        String str = "";
        try {
            str = String.valueOf(li);
        } catch (Exception e) {

        }
        return str;
    }

    /**
     * User: Lin
     * Date: 2017/9/6 16:48
     * Description: 对象转Json数据
     */
    public static <T> String toJsonStr(List<T> Object) {
        return new Gson().toJson(Object);
    }

    /**
     * User: Lin
     * Date: 2017/9/6 16:48
     * Description: 对象转Json数据
     */
    public static <T> String toJsonStr(T mTClass) {
        return new Gson().toJson(mTClass);
    }


    /**
     * User: Lin
     * Date: 2017/9/6 16:48
     * Description: Json数据转对象
     */
    public static <T> T toObject(String jsonStr, Class<T> mClass) {
        return new Gson().fromJson(jsonStr, mClass);
    }

}
