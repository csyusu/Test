package webreport.sparktest

import java.time.LocalDateTime
import java.util.Date

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

/*
    @author    YuSu
    @createTime    2019-08-26
   */
object KafkaTest {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("KafkaTest")
    sparkConf.set("spark.serializer","org.apache.spark.serializer.KryoSerializer")
    sparkConf.set("spark.streaming.backpressure.enabled","true")
    //每个partition每秒取的记录数
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition","10000")
    //sparkConf.set("spark.kryo.registrator","com.lm.kryo.MyRegistrator")
    val ssc = new StreamingContext(sparkConf,Seconds(10))
    //    ssc.checkpoint("/tmp/checkpoint")
    val brokers = """10.121.44.174:6667,10.121.44.176:6667,10.121.44.177:6667"""
    //latest earliest
    val kafkaParams = Map[String,Object](
      "bootstrap.servers"->brokers,
      "key.deserializer"->classOf[StringDeserializer],
      "value.deserializer"->classOf[StringDeserializer],
      "group.id"->"report",
      "auto.offset.reset"->"earliest",
      "enable.auto.commit"->(false:java.lang.Boolean)
    )
    val topics = Array("test")
    //指定offset值
    //    val fromOffSet:Map[TopicPartition,Long] = Map(new TopicPartition("dwr_pnl",0)->22L,
    //      new TopicPartition("dwr_pnl",3)->233964L)
    //    val messages = KafkaUtils.createDirectStream[String,String](ssc,PreferConsistent,Subscribe[String,String](topics,kafkaParams,fromOffSet))
    val messages = KafkaUtils.createDirectStream[String,String](ssc,PreferConsistent,Subscribe[String,String](topics,kafkaParams))
    //foreachRDD运行在driver端，类似jdbc等需序列化的对象，无法从driver传输到executor，因此jdbc连接应使用懒加载连接池在foreachPartition或partition中建立连接
    print("???")
    messages.foreachRDD{ rdd=>
      print("进来了")
      print(rdd.collect().length)
      print(LocalDateTime.now())
      if(rdd.count()>0) {
        //获得offset值
        rdd.asInstanceOf[HasOffsetRanges].offsetRanges.foreach(println)
        rdd.foreach(record=>{
          println(record)
        })
      }
    }
    ssc.start()
    ssc.awaitTermination()
  }

}
