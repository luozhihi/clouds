package com.lzh.advice;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
//        Runtime runtime = Runtime.getRuntime();
//        long l = runtime.freeMemory();
//        try {
//            // 执行cmd命令
//            Process mspaint = runtime.exec("mspaint");
//            Thread.sleep(150);
////            int i = mspaint.waitFor();
////            System.out.println(i);
//            mspaint.destroy();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(l);
//        ArrayList<String> list = new ArrayList<>();
//        list.add(null);
//        List<String> collect = list.stream().filter(e -> e.length() > 0).collect(Collectors.toList());
        // 压缩图片，使用
        //        <dependency>
        //            <groupId>net.coobird</groupId>
        //            <artifactId>thumbnailator</artifactId>
        //            <version>0.4.8</version>
        //        </dependency>
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("D:/image/t3.jpg");
            List<FileOutputStream> list = new ArrayList<>();
            list.add(fileOutputStream);
            Thumbnails
                    .of("D:/image/test.jpg")
                    .scale(1)
                    .watermark(Positions.BOTTOM_CENTER, ImageIO.read(new File("D:/image/water.jpg")), 0.6f)
                    .outputQuality(0.5)
                    .toOutputStreams(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
