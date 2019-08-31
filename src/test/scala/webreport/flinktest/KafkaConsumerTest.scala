package webreport.flinktest


import java.time.LocalDateTime
import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import webreport.flinktest.conf.KafkaProperties

/*
    @author    YuSu
    @createTime    2019-08-26
   */
object KafkaConsumerTest {

  def main(args: Array[String]): Unit = {
    val properties = KafkaProperties.getConsumerProperties

    val env = StreamExecutionEnvironment.getExecutionEnvironment
//    // create a checkpoint every 5 seconds
//    env.enableCheckpointing(5000)
    val kafkaConsumer = new FlinkKafkaConsumer010(
      "test",
      new SimpleStringSchema,
      properties)

    val messageStream  = env.addSource(kafkaConsumer)
    println(LocalDateTime.now())
    messageStream.print()
    env.execute("kafkaTest")
  }
}
