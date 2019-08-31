package webreport.sparktest

import java.util.Properties

import kafka.common.KafkaException
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.SparkSession
import webreport.sparktest.tools.{GetOracleDataframe, KafkaSink}

import scala.util.parsing.json.JSONObject

/*
    @author    YuSu
    @createTime    2019-06-13
   */
object KafkaProducer {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[*]").appName("Spark2Kafka").getOrCreate()
    val brokers = """10.121.44.174:6667,10.121.44.176:6667,10.121.44.177:6667"""
    val topic = "test"
    val kafkaProducer:Broadcast[KafkaSink[String,String]]={
      val kafkaProducerConfig={
        val prop = new Properties()
        prop.setProperty("bootstrap.servers",brokers)
        prop.setProperty("key.serializer", classOf[StringSerializer].getName)
        prop.setProperty("value.serializer", classOf[StringSerializer].getName)
        prop
      }
      println("kafkaProducer init done")
      spark.sparkContext.broadcast(KafkaSink[String,String](kafkaProducerConfig))
    }
    try{
      for(i <- 1 to 10){
        kafkaProducer.value.send(topic,i.toString,i.toString)
        println(i+"生产成功")
      }
    }
    catch {
      case e:KafkaException=>{
        print(e.getMessage)
      }
      case e:Exception=>{
        print(e.getStackTrace)
      }
    }
    finally {
      spark.close()
    }
  }
}
