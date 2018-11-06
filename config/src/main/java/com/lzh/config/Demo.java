package com.lzh.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Demo {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
//        Integer[] integers = list.stream().toArray(Integer[]::new);
        list = list.stream().filter(a -> a > 1).collect(Collectors.toList());
        long count = list.stream().count();
        System.out.println(count);
        System.out.println(list);
        String a = String.format("---- %s ----", 12.2);
        System.out.println(a);
//        System.out.println(Arrays.toString(integers));
    }
}
