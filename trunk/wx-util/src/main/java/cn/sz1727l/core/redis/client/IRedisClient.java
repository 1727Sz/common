package cn.sz1727l.core.redis.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.ZParams;

/**
 * cn.sz1727l.core.redis.client.IRedisClient
 * 
 * @author: Dongd_Zhou
 * @date: 2017年2月24日 下午5:00:34
 */
public interface IRedisClient extends JedisCommands {
    /**
     * 功能描述: 删除该key，返回删除key的个数；如果key不存在，则忽略该命令
     * 
     * @param key 要删的Key值
     * @return 删除key的个数
     */
    Long del(String key);

    /**
     * 功能描述: 将一个或多个值 value插入到列表key的表头，如果key不存在，一个空列表会被创建并执行lpush操作。
     * 
     * @param key 列表Key值
     * @param fields 要插入的一个或多个value值
     * @return 插入数据后的列表元素个数
     */
    Long lpush(String key, String... fields);

    /**
     * 功能描述: 将一个或多个值value插入到列表key的表尾，如果key不存在，一个空列表会被创建并执行rpush操作。
     * 
     * @param key 列表Key值
     * @param fields 要插入的一个或多个value值
     * @return 插入数据后的列表元素个数
     */
    Long rpush(String key, String... fields);

    /**
     * 功能描述: 同时设置一个或多个key-value对
     * 
     * @param keyValues 要设置的key-value对
     * @return Status code reply Basically +OK as MSET can't fail
     */
    String mset(Map<String, String> keyValues);

    /**
     * 功能描述: 查询匹配 pattern的所有key值，数据量大的时候慎用
     * 
     * @param pattern 正则表达式
     * @return 匹配的Key集合
     */
    Set<String> keys(String pattern);

    /**
     * 功能描述: 返回所有(一个或多个)给定key的值。如果给定的key里面，有某个key不存在，那么这个key返回特殊值nil
     * 
     * @param keys 一个或多个Key值
     * @return 所有Key值对应的Value集合
     */
    List<String> mget(String... keys);

    /**
     * 功能描述: 刷新配置 重建连接
     * 
     * @param config 配置文件路径
     */
    void refresh(String config);

    /**
     * 功能描述: 清除数据库上的所有数据
     * 
     * @return "OK"
     */
    String flushDB();

    /**
     * 功能描述: 求多个集合的交集
     * 
     * @param keys 多个集合的Key
     * @return 交集的集合，不存在时为空
     */
    Set<String> sinter(String... keys);

    /**
     * 功能描述: 求多个集合的交集，并把结果存在dstkey中，dstkey已存在则覆盖
     * 
     * @param dstkey 目标集合Key值
     * @param keys 多个源集合Key值
     * @return 结果集中的成员数量
     */
    Long sinterstore(String dstkey, String... keys);

    /**
     * 功能描述: 求多个集合的并集
     * 
     * @param keys 多个集合的Key值
     * @return 并集的集合，不存在时为空
     */
    Set<String> sunion(String... keys);

    /**
     * 功能描述: 求多个集合的并集，并把结果存在dstkey中，dstkey已存在则覆盖
     * 
     * @param dstkey 目标集合Key值
     * @param keys 多个集合的Key值
     * @return 结果集中的成员数量
     */
    Long sunionstore(String dstkey, String... keys);

    /**
     * 功能描述: 求参数里第一个set与其他set的差集
     * 
     * @param keys 若干个集合的Key值
     * @return 差集的集合，不存在时为空
     */
    Set<String> sdiff(String... keys);

    /**
     * 功能描述: 求参数里第一个set与其他set的差集，并把结果存在dstkey中，dstkey已存在则覆盖
     * 
     * @param dstkey 目标集合Key值
     * @param keys 若干个集合的Key值
     * @return 结果集中的成员数量
     */
    Long sdiffstore(String dstkey, String... keys);

    /**
     * 功能描述: 求多个有序集合的交集，并把结果存在dstkey中，dstkey已存在则覆盖，score值默认按照权重为1，累加。
     * 
     * @param dstkey 目标集合Key值
     * @param sets 多个有序集合的Key值
     * @return 结果集中的成员数量
     */
    Long zinterstore(String dstkey, String... sets);

    /**
     * 功能描述: 求多个有序集合的交集，并把结果存在dstkey中，dstkey已存在则覆盖 score值，按照params参数中设置的权重和算法计算
     * 
     * @param dstkey 目标集合Key值
     * @param params 权重算法参数
     * @param sets 多个有序集合的Key值
     * @return 结果集中的成员数量
     */
    Long zinterstore(String dstkey, ZParams params, String... sets);

    /**
     * 功能描述: 求多个有序集合的并集，并把结果存在dstkey中，dstkey已存在则覆盖，score值默认按照权重为1，累加。
     * 
     * @param dstkey 目标集合Key值
     * @param sets 多个有序集合的Key值
     * @return 结果集中的成员数量
     */
    Long zunionstore(String dstkey, String... sets);

    /**
     * 功能描述: 求多个有序集合的并集，并把结果存在dstkey中，dstkey已存在则覆盖 score值按照params参数中设置的权重和算法计算
     * 
     * @param dstkey 目标集合Key值
     * @param params 权重算法参数
     * @param sets 多个有序集合的Key值
     * @return 结果集中的成员数量
     */
    Long zunionstore(String dstkey, ZParams params, String... sets);

    /**
     * 功能描述: 清除双写状态下节点不可用的标记
     * 
     */
    void clearDisableFlags();
}
