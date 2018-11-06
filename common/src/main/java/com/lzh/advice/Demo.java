package com.lzh.advice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Demo {
    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        long l = runtime.freeMemory();
        try {
            // 执行cmd命令
            Process mspaint = runtime.exec("mspaint");
            Thread.sleep(150);
//            int i = mspaint.waitFor();
//            System.out.println(i);
            mspaint.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(l);
        ArrayList<String> list = new ArrayList<>();
        list.add(null);
        List<String> collect = list.stream().filter(e -> e.length() > 0).collect(Collectors.toList());

    }
}
