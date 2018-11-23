package com.lzh.advice;

import com.lzh.config.User;
import com.lzh.utils.ESQueryBuilder;
import com.lzh.utils.ESUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream("D:/image/t3.jpg");
//            List<FileOutputStream> list = new ArrayList<>();
//            list.add(fileOutputStream);
//            Thumbnails
//                    .of("D:/image/test.jpg")
//                    .scale(1)
//                    .watermark(Positions.BOTTOM_CENTER, ImageIO.read(new File("D:/image/water.jpg")), 0.6f)
//                    .outputQuality(0.5)
//                    .toOutputStreams(list);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
       /* List<Map<String,Object>> list = new ArrayList<>();
        for(int i = 0; i< 1000;i++){
            User user = new User();
            user.setUsernane("testNAME"+(i+1));
            user.setPassword("testPWD"+(i+1));
            user.setSex(i%2 == 0 ?"man":"women");

            Map<String,Object> map = getMap(user);
            list.add(map);
        }
        try {
            ESUtil.bulkInsertData("estest","user",list);
        } catch (Exception e) {
            System.err.print("[erro]:");
            e.printStackTrace();
        }*/
       /* 添加索引
        ESQueryBuilder esQueryBuilder = new ESQueryBuilder();
        TransportClient client = ESUtil.getClient();
        try {
            createIndex(client,"estest","user");
        } catch (IOException e) {
            System.out.println("添加索引失败");
            e.printStackTrace();
        }*/
        try {
            ESQueryBuilder esQueryBuilder = new ESQueryBuilder();
            long l = ESUtil.statCount("estest", "user", esQueryBuilder);
            System.out.print("长度是："+l);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Map<String,Object> getMap(User user){
        HashMap<String, Object> map = new HashMap<>();
        map.put("username",user.getUsernane());
        map.put("password",user.getPassword());
        map.put("sex",user.getSex());
        return map;
    }
    private static void createIndex(TransportClient client,String index,String type) throws IOException {
        CreateIndexRequestBuilder createIndex=client.admin().indices().prepareCreate(index);
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties") //设置之定义字段
                .startObject("username").field("type","text").endObject() //设置分析器
                .startObject("password").field("type","text").endObject()
                .startObject("sex").field("type","text").endObject()
                .endObject()
                .endObject();
        createIndex.addMapping(type, mapping);
        CreateIndexResponse res=createIndex.execute().actionGet();
        System.out.println(res);
    }
}
