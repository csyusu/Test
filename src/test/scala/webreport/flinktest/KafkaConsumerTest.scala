package webreport.flinktest


import java.time.LocalDateTime

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition
import webreport.flinktest.conf.KafkaProperties

/*
    @author    YuSu
    @createTime    2019-08-26
   */
object KafkaConsumerTest {

  def main(args: Array[String]): Unit = {
    val properties = KafkaProperties.getConsumerProperties

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置基于事件时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setParallelism(2)
//    // create a checkpoint every 5 seconds
//    env.enableCheckpointing(5000)
    //指定checkpoint语义
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    val kafkaConsumer = new FlinkKafkaConsumer010(
      //正则表达式指定topic，支持topic动态检测
      java.util.regex.Pattern.compile("test\\S*"),
//      "test",
      new SimpleStringSchema,
      properties)
    //指定offset
    /**

    val offsets = new java.util.HashMap[KafkaTopicPartition,java.lang.Long];
    offsets.put(new KafkaTopicPartition("netstream-flow-data", 0), 11111111l);
    offsets.put(new KafkaTopicPartition("netstream-flow-data", 1), 222222l);
    offsets.put(new KafkaTopicPartition("netstream-flow-data", 2), 33333333l);

    /**
      * Flink从topic中最初的数据开始消费
      */
    kafkaConsumer.setStartFromEarliest();

    /**
      * Flink从topic中指定的时间点开始消费，指定时间点之前的数据忽略
      */
    kafkaConsumer.setStartFromTimestamp(1559801580000l);

    /**
      * Flink从topic中指定的offset开始，这个比较复杂，需要手动指定offset
      */
    kafkaConsumer.setStartFromSpecificOffsets(offsets);

    /**
      * Flink从topic中最新的数据开始消费
      */
    kafkaConsumer.setStartFromLatest();

    /**
      * Flink从topic中指定的group上次消费的位置开始消费，所以必须配置group.id参数
      */
    kafkaConsumer.setStartFromGroupOffsets();
      */
    val messageStream  = env.addSource(kafkaConsumer)
    messageStream.print()
    env.execute("kafkaTest")
  }
}
