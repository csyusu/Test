package webreport.flinktest

import java.util
import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer010, FlinkKafkaProducer010}
import org.apache.flink.streaming.api.scala._
import webreport.flinktest.conf.KafkaProperties


/*
    @author    YuSu
    @createTime    2019-08-27
   */
object KafkaProducerTest {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(2)
    val properties = KafkaProperties.getProducerProperties
    val kafkaProducer = new FlinkKafkaProducer010(
      "test",
      new SimpleStringSchema,
      properties)
    val list = List("1","2","3","4")
    val message = env.fromCollection(list)
    val message2 = env.fromElements("A","B","C","D")
    message.addSink(kafkaProducer)
    message2.addSink(kafkaProducer)
    println("生产结束")
    env.execute("ProducerTest")
  }
}
