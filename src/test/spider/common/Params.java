package test.spider.common;

public interface Params {

    static int MAX_THREADS = 5;//线程池最大线程数

    static final String SERIES_ROOT_PATH = "http://car.bitauto.com";//易车网拼接URL使用

    static final String PEI_ZHI = "peizhi/";//易车网拼接URL使用

    static final String BITAUTO_BRAND = "bitauto_brand";//易车网品牌数据库表名

    static final String BITAUTO_BRAND_ID_SEQ = "bitauto_brand_id_seq";//易车网品牌id序列

    static final String BITAUTO_SERIES = "bitauto_series";//易车网系列数据库表名

    static final String BITAUTO_SERIES_ID_SEQ = "bitauto_series_id_seq";//易车网系列id序列

    static final String BITAUTO_MODEL = "bitauto_model";//易车网车辆型号数据库表名

    static final String BITAUTO_MODEL_ID_SEQ = "bitauto_model_id_seq";//易车网车辆型号id序列
    
    static final String AUTOHOME_BRAND = "autohome_brand";//汽车之家品牌数据库表名

    static final String AUTOHOME_BRAND_ID_SEQ = "autohome_brand_id_seq";//汽车之家品牌id序列

    static final String AUTOHOME_SERIES = "autohome_series";//汽车之家系列数据库表名

    static final String AUTOHOME_SERIES_ID_SEQ = "autohome_series_id_seq";//汽车之家系列id序列

    static final String AUTOHOME_MODEL = "autohome_model";//汽车之家车辆型号数据库表名

    static final String AUTOHOME_MODEL_ID_SEQ = "autohome_model_id_seq";//汽车之家车辆型号id序列

}
