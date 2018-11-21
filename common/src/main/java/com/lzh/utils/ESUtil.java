package com.lzh.utils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lzh.utils.ESQueryBuilder.DateHistogram;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @ClassName: ESUtil
 * @Description: Elasticsearch工具类
 * @author chenxiaojian
 * @date 2018年1月9日 下午5:03:34
 */
//@Component
//@RefreshScope
public class ESUtil {

    private static final Logger logger = LoggerFactory.getLogger(ESUtil.class);

    private final static int MAX = 10000;

    private static TransportClient client;

    private static String clusterName = "elasticsearch";

    private static String clusterAddress = "111.230.47.161:9300";


    /**
     * client初始化
     */
    static{
        try {
            Settings settings = Settings.builder()
                    .put("cluster.name", clusterName).build();
            /**
             * 如果安装了x-pack组件，需要引入 x-pack-transport 使用PreBuiltXPackTransportClient实现 增加认证参数
             * https://www.elastic.co/guide/en/x-pack/current/java-clients.html
             */

            client = new PreBuiltTransportClient(settings);
            String[] nodes = clusterAddress.split(",");
            for(String node : nodes) {
                if (node.length() > 0) {
                    String[] hostPort = node.split(":");
                    client.addTransportAddress(
                            new TransportAddress(InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1])));
                }
            }
        } catch (Exception e) {
            logger.error("es init failed!", e);
        }
    }

    /**
     * 插入数据
     * @param index 索引名
     * @param type 类型
     * @param _id 数据id
     * @param json 数据
     */
    public static void insertData(String index, String type, String _id, Map<String, ?> source)
            throws Exception {
        client.prepareIndex(index, type).setId(_id)
                .setSource(source)
                .get();
    }

    /**
     * 更新数据
     * @param index 索引名
     * @param type 类型
     * @param _id 数据id
     * @param json 数据
     */
    public static void updateData(String index, String type, String _id, String field,boolean value)
            throws Exception {
        UpdateRequest updateRequest = new UpdateRequest(index, type, _id)
                .doc(jsonBuilder()
                        .startObject()
                        .field(field, value)
                        .endObject());
        UpdateResponse resp = client.update(updateRequest).get();
        resp.getGetResult();
    }

    /**
     * 删除数据
     * @param index 索引名
     * @param type 类型
     * @param _id 数据id
     */
    public static void deleteData(String index, String type, String _id)
            throws Exception {
        client.prepareDelete(index, type, _id)
                .get();
    }

    /**
     * 删除数据
     * @param index 索引名
     * @param type 类型
     * @param _id 数据id
     */
    public static long deleteData(String index, String type, ESQueryBuilder builder)
            throws Exception {
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(builder.listBuilders()).source(index)
                .get();
        long deleted = response.getDeleted();
        return deleted;
    }


    /**
     * 批量插入数据
     * @param index 索引名
     * @param type 类型
     * @param data (_id 主键, json 数据)
     */
    public static void bulkInsertData(String index, String type, Map<String, Object> data)
            throws Exception {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        data.forEach((param1, param2) -> {
            bulkRequest.add(client.prepareIndex(index, type, param1)
                    .setSource(param2)
            );
        });
        bulkRequest.get();
    }

    /**
     * 批量插入数据
     * @param index 索引名
     * @param type 类型
     * @param jsonList 批量数据
     */
    public static void bulkInsertData(String index, String type, List<Map<String, Object>> data)
            throws Exception {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        data.forEach(item -> {
            bulkRequest.add(client.prepareIndex(index, type).setSource(item)
            );
        });
        bulkRequest.get();
    }

    /**
     * 查询
     * @param index 索引名
     * @param type 类型
     * @param constructor 查询构造
     */
    public static String searchById(String index, String type, String _id)
            throws Exception {
        return client.prepareGet(index, type, _id).get().getSourceAsString();
    }

    /**
     * 查询
     * @param index 索引名
     * @param type 类型
     * @param constructor 查询构造
     */
    public static Map<String, Object> searchMapById(String index, String type, String _id)
            throws Exception {
        return client.prepareGet(index, type, _id).get().getSourceAsMap();
    }

    /**
     * 查询
     * @param index 索引名
     * @param type 类型
     * @param constructor 查询构造
     */
    public static List<String> search(String index, String type, ESQueryBuilder builder)
            throws Exception {
        List<String> result = new ArrayList<>();
        SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(index, type, builder)
                .addSort(createSortBuilder(builder));
        SearchResponse sr = searchRequestBuilder.execute().actionGet();

        SearchHits hits = sr.getHits();
        SearchHit[] searchHists = hits.getHits();
        for (SearchHit sh : searchHists) {
            result.add(sh.getSourceAsString());
        }
        return result;
    }


    /**
     * 查询
     * @param index 索引名
     * @param type 类型
     * @param constructor 查询构造
     */
    public static SearchResponse searchResponse(String index, String type, QueryBuilder queryBuilder, FieldSortBuilder sortBuilder, Integer from, Integer size)
            throws Exception {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type).setQuery(queryBuilder);
        if (sortBuilder != null) {
            searchRequestBuilder.addSort(sortBuilder);
        }
        if (size != null) {
            size = size < 0 ? 0 : size;
            size = size > MAX ? MAX : size;
            //返回条目数
            searchRequestBuilder.setSize(size);
        }
        if (from != null) {
            searchRequestBuilder.setFrom(from);
        }
        SearchResponse sr = searchRequestBuilder.execute().actionGet();
        return sr;
    }

    public static Map<String, Long> aggTopForTask(String index, String type, QueryBuilder queryBuilder, String groupBy,Integer from, Integer size)
            throws Exception {

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type).setQuery(queryBuilder);

        Map<String, Long> map = Maps.newLinkedHashMap();

        searchRequestBuilder.addAggregation(
                AggregationBuilders.terms("agg").field(groupBy).order(BucketOrder.count(false)).size(size)
        );

        SearchResponse sr = searchRequestBuilder.get();
        Terms agg = sr.getAggregations().get("agg");
        Iterator<Terms.Bucket> iter = (Iterator<Bucket>) agg.getBuckets().iterator();
        while (iter.hasNext()) {
            Terms.Bucket gradeBucket = iter.next();
            map.put(gradeBucket.getKey().toString(), gradeBucket.getDocCount());
        }
        return map;

    }

    public static Map<String, Long> aggAlarmCountByDay(String index, String type, DateHistogramAggregationBuilder field, QueryBuilder builder)
            throws Exception{
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type).setQuery(builder);

        Map<String, Long> map = Maps.newLinkedHashMap();

        searchRequestBuilder.addAggregation(field).setSize(0);

        SearchResponse response = searchRequestBuilder.execute().actionGet();
        Histogram histogram = response.getAggregations().get("alarmCount");
        for(Histogram.Bucket entry : histogram.getBuckets()){
            String keyAsString = entry.getKeyAsString();
            Long count = entry.getDocCount();
            map.put(keyAsString, count);
        }
        return map;
    }

    public static Map<String, Long> aggDynamicForTask(String index, String type, QueryBuilder queryBuilder, String groupBy, Integer size)
            throws Exception{
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type).setQuery(queryBuilder);

        Map<String, Long> map = Maps.newLinkedHashMap();

        searchRequestBuilder.addAggregation(
                AggregationBuilders.terms("agg").field(groupBy).size(size)
        );

        SearchResponse sr = searchRequestBuilder.get();
        Terms agg = sr.getAggregations().get("agg");
        Iterator<Terms.Bucket> iter = (Iterator<Bucket>) agg.getBuckets().iterator();
        while (iter.hasNext()) {
            Terms.Bucket gradeBucket = iter.next();
            map.put(gradeBucket.getKey().toString(), gradeBucket.getDocCount());
        }
        return map;
    }

    /**
     * 查询并聚合
     * @param index 索引名
     * @param type 类型
     * @param constructor 查询构造
     */
    public static ConcurrentHashMap<String,HashSet<String>> searchResponseByMap(String index, String type, QueryBuilder queryBuilder, Integer taskCount)
            throws Exception {
        return fetchAllRecordsByScrollId(index,type,queryBuilder,taskCount);
    }
    public static ConcurrentHashMap<String,HashSet<String>> fetchAllRecordsByScrollId(String index, String type, QueryBuilder queryBuilder, Integer taskCount) {
        //要将所有命中的记录取出，拿到他的taskSerial，最终统计有多少有效的TaskSerial，用来做分页的依据
        ConcurrentHashMap<String,HashSet<String>> targetsByTask = new ConcurrentHashMap();

        //指定一个index和type
        SearchRequestBuilder search = client.prepareSearch(index).setTypes(type).setFetchSource(new String[]{"tarName","tarIdentityId","taskSerial","targetSerial"}, null);
        //使用原生排序优化性能
        search.addSort("_doc", SortOrder.ASC);
        //设置每批读取的数据量
        search.setSize(100);
        //默认是查询所有
        search.setQuery(queryBuilder);
        //设置 search context 维护1分钟的有效期
        search.setScroll(TimeValue.timeValueMinutes(1));

        //获得首次的查询结果
        SearchResponse scrollResp=search.get();
        //打印命中数量
        //System.out.println("命中总数量："+scrollResp.getHits().getTotalHits());

        //打印计数
        //int count=1;
        do {
            //System.out.println("第"+count+"次打印数据：");
            //读取结果集数据
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                String key = hit.getSourceAsMap().get("taskSerial").toString();//taskSerial 作为key
                if( targetsByTask.get(key) != null ) {
                    HashSet<String> targets = targetsByTask.get(key);
                    targets.add(hit.getSourceAsMap().get("targetSerial").toString());
                } else {
                    HashSet<String> targets = Sets.newHashSet();
                    targets.add(hit.getSourceAsMap().get("targetSerial").toString());
                    targetsByTask.put(key, targets);
                }

            }
            //count++;
            //将scorllId循环传递
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(TimeValue.timeValueMinutes(1)).execute().actionGet();

            //当searchHits的数组为空的时候结束循环，至此数据全部读取完毕
        } while(scrollResp.getHits().getHits().length != 0);

        return targetsByTask;
    }

    /**
     * 查询
     * @param index 索引名
     * @param type 类型
     * @param constructor 查询构造
     */
    public static List<String> searchStr(String index, String type, QueryBuilder builder, FieldSortBuilder sortBuilder, Integer from, Integer size)
            throws Exception {
        List<String> ret = new LinkedList<>();
        SearchResponse sr = searchResponse(index, type, builder, sortBuilder, from, size);
        SearchHits hits = sr.getHits();
        SearchHit[] searchHists = hits.getHits();
        for (SearchHit sh : searchHists) {
            ret.add(sh.getSourceAsString());
        }
        return ret;
    }


    /**
     * 计数
     * @param index 索引名
     * @param type 类型
     * @param constructor 查询构造
     */
    public static long statCount(String index, String type, ESQueryBuilder builder) throws Exception {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type)
                .setQuery(builder.listBuilders());
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        return searchResponse.getHits().totalHits;
    }

    /**
     * 计数
     * @param index 索引名
     * @param type 类型
     * @param constructor 查询构造
     */
    public static long statCount(String index, String type, QueryBuilder builder) throws Exception {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type)
                .setQuery(builder)
                .setSize(0);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        return searchResponse.getHits().totalHits;
    }

    /**
     *
     * 查询单条记录
     * @param index
     * @param type
     * @param builder
     * @return
     */
    public static String searchOne(String index, String type, ESQueryBuilder builder) throws Exception {
        SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(index, type, builder)
                .addSort(createSortBuilder(builder));
        searchRequestBuilder.setSize(1);
        SearchResponse searchResponse = searchRequestBuilder.get();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHists = hits.getHits();
        if (searchHists.length == 0) {
            return null;
        }
        return searchHists[0].getSourceAsString();
    }


    /**
     * 分组统计查询(只统计每个分组数量, 不包含每个分组下的数据项)
     * @param index
     * @param type
     * @param builder
     * @param groupBy
     * @return
     */
    public static Map<Object, Object> statSearch(String index, String type, ESQueryBuilder builder, String groupBy) throws Exception  {

        Map<Object, Object> map = new HashMap<>();
        builder = new ESQueryBuilder();
        builder.setQueryBuilder(map);
        SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(index, type, builder)
                .addSort(createSortBuilder(builder));
        searchRequestBuilder.addAggregation(
                AggregationBuilders.terms("agg").field(groupBy)
        );
        SearchResponse sr = searchRequestBuilder.get();
        Terms agg = sr.getAggregations().get("agg");
        Iterator<Terms.Bucket> iter = (Iterator<Bucket>) agg.getBuckets().iterator();
        while (iter.hasNext()) {
            Terms.Bucket gradeBucket = iter.next();
            map.put(gradeBucket.getKey(), gradeBucket.getDocCount());
        }
        return map;
    }

    public static List<InternalDateHistogram.Bucket> statSearchByDateHistogram(String index, String type, QueryBuilder builder, DateHistogram dh)
            throws Exception {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
        searchRequestBuilder.setQuery(builder);
        DateHistogramAggregationBuilder dhab = AggregationBuilders
                .dateHistogram("agg")
                .field(dh.getFieldName())
                .dateHistogramInterval(dh.getDateInterval())
                .format(dh.getFormat())
                .timeZone(DateTimeZone.forID("Asia/Shanghai"));
        dhab.minDocCount(0);
        if (dh.getMin() != null && dh.getMax() != null) {
            dhab.extendedBounds(new ExtendedBounds(dh.getMin(), dh.getMax()));
        }
        searchRequestBuilder.addAggregation(dhab);
        searchRequestBuilder.setSize(0);

        SearchResponse sr = searchRequestBuilder.get();
        List<InternalDateHistogram.Bucket> buckets = ((InternalDateHistogram)sr.getAggregations().get("agg")).getBuckets();
        return buckets;
    }


    /**
     *
     * 构造QueryBuilder
     * @param index
     * @param type
     * @param builder
     * @return
     */
    private  static SearchRequestBuilder createSearchRequestBuilder(String index, String type, ESQueryBuilder builder) throws Exception {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
        searchRequestBuilder.setQuery(builder.listBuilders());
        int size = builder.getSize();
        if (size < 0) {
            size = 0;
        }
        if (size > MAX) {
            size = MAX;
        }
        //返回条目数
        builder.setSize(size);
        builder.setFrom(builder.getFrom() < 0 ? 0 : builder.getFrom());
        searchRequestBuilder.setSize(size);
        searchRequestBuilder.setFrom(builder.getFrom());
        return searchRequestBuilder;
    }


    /**
     * 构造排序Builder
     * @param queryBuilder
     * @return
     */
    private static FieldSortBuilder createSortBuilder(ESQueryBuilder queryBuilder) {
        FieldSortBuilder sortBuilder = null;
        if (queryBuilder.getAsc() != null && queryBuilder.getAsc().length() > 0) {
            sortBuilder = new FieldSortBuilder(queryBuilder.getAsc());
            sortBuilder.order(SortOrder.ASC);
            sortBuilder.unmappedType("date");
        }
        if (queryBuilder.getDesc() != null && queryBuilder.getDesc().length() > 0) {
            sortBuilder = new FieldSortBuilder(queryBuilder.getDesc());
            sortBuilder.order(SortOrder.DESC);
            sortBuilder.unmappedType("date");
        }
        return sortBuilder;
    }

    /**
     * 关闭链接
     */
    public static void close() {
        client.close();
    }

    public static TransportClient getClient() {
        return client;
    }


}