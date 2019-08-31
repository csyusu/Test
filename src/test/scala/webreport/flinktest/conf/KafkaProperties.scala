package webreport.flinktest.conf

import java.util.Properties

/*
    @author    YuSu
    @createTime    2019-08-27
   */
object KafkaProperties {
  def getProducerProperties: Properties ={
    val props = new Properties()
    props.put("bootstrap.servers", "10.121.44.174:6667,10.121.44.176:6667,10.121.44.177:6667")
    /**
      * Producer希望leader返回接受消息后的确认信息. 可选值 all, -1, 0 1. 默认值为1.
      * 1.> acks=0 不需要等待leader确认. 此时retries设置无效. 响应里来自服务端的offset总是-1.
      *     Producer只管发不管发送成功与否。延迟低，容易丢失数据。
      * 2.> acks=1 表示leader写入成功（但是并没有刷新到磁盘）后即向Producer响应。延迟中等，但是一旦
      *     leader副本挂了，就会丢失数据。
      * 3.> acks=all 等待数据完成副本的复制, 等同于-1. 假如需要保证消息不丢失, 需要使用该设置. 同时
      *     需要设置unclean.leader.election.enable为false, 提高可靠性降低了可用性,min.insync.replicas 保持同步的副本数大于1,topic replication.factor 大于1
      *     为true时，当ISR(In-Sync Replicas)列表为空时, 如果leader（消息接收到5）和ISR中的follower都挂掉，可选择其他存活的非ISR follower副本（消息接收到3）作为新的leader，可提高可用性，
      *     但新leader之前属于非ISR，消息并不是最新的，原leader恢复之后作为follower，为了保持和leader同步会删除4和5，导致消息丢失。
      */
    props.put("acks", "all")
    /**
      * 设置大于零的值时，Producer会发送失败后会进行重试。
      */
    props.put("retries", "1000")
    /**
      * Producer批量发送同一个partition消息以减少请求的数量从而提升客户端和服务端的性能，默认大小是16348 byte(16k).
      * 发送到broker的请求可以包含多个batch, 每个batch的数据属于同一个partition，太小的batch会降低吞吐.太大会浪费内存.
      */
    props.put("batch.size", "16348")
    /**
      * batch.size和liner.ms配合使用，前者限制大小后者限制时间。前者条件满足的时候，同一partition的消息会立即发送,
      * 此时linger.ms的设置无效，假如要发送的消息比较少, 则会等待指定的时间以获取更多的消息，此时linger.ms生效
      * 默认设置为0ms(没有延迟).
      */
    props.put("linger.ms", "1")
    /**
      * Producer可以使用的最大内存来缓存等待发送到server端的消息.默认值33554432 byte(32m)
      */
    props.put("buffer.memory", "33554432")
    props.put("compression.type", "snappy")
    props.put("max.request.size", "10485760")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
    props
  }
  def getConsumerProperties: Properties ={
    val props = new Properties()
    props.put("bootstrap.servers", "10.121.44.174:6667,10.121.44.176:6667,10.121.44.177:6667");
    props.put("group.id", "test");
    props.put("auto.offset.reset","earliest")
    props.put("enable.auto.commit", "false");
    props.put("auto.commit.interval.ms", "1000");
    props.put("session.timeout.ms", "30000");
    //动态感知kafka主题分区的增加 单位毫秒
    props.put("flink.partition-discovery.interval-millis", "5000")
    /**
      * 用来限制每次consumer fetch数据的大小限制，只是限制partition的，无法限制到一次拉取的总量。
      */
    props.put("max.partition.fetch.bytes", "10485760")
    props.put("fetch.message.max.bytes", "3072000")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
    props
  }
}
