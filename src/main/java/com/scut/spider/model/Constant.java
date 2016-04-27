package com.scut.spider.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 2016/2/25.
 */
public class Constant {

    public static final String SCUT_EE_URL = "http://www2.scut.edu.cn/";
    public static final String DOCTOR_URL = "http://www2.scut.edu.cn/s/166/t/334/p/1/c/6009/d/6036/list.htm";
    public static final String MASTER_URL_1 = "http://www2.scut.edu.cn/s/166/t/334/p/1/c/6009/d/6037/list.htm";
    public static final String MASTER_URL_2 = "http://www2.scut.edu.cn/s/166/t/334/p/1/c/6009/d/6037/i/1/list.htm";
    private static List<String> URL_LIST = null;

    public static List<String> getUrlList() {
        if (URL_LIST == null) {
            URL_LIST = new ArrayList<>();
            URL_LIST.add(DOCTOR_URL);
            URL_LIST.add(MASTER_URL_1);
            URL_LIST.add(MASTER_URL_2);
        }
        return URL_LIST;
    }
}
