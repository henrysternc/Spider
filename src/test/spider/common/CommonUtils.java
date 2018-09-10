package test.spider.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommonUtils {

    /**
     * 判断是否为 null 或 "" 或 "null"
     * @param obj
     * @return true : 为空   false： 非空
     */
    public static boolean isNull(Object obj){
        boolean result = true;
        try{
            if(obj instanceof String){
                if(obj!=null && ((String)obj).length()>0 && !"null".equalsIgnoreCase((String)obj)){
                    result = false;
                }
            } else {
                if(obj != null){
                    result = false;
                }
            }
        } catch (Exception e) { }
        return result;
    }
    
    
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return object -> seen.putIfAbsent(keyExtractor.apply(object), Boolean.TRUE) == null;
    }
}
