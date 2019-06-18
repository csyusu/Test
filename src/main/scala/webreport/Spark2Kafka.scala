package webreport

import java.util.Properties

import kafka.common.KafkaException
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.SparkSession
import webreport.tools.{KafkaSink, OracleJdbc}

import scala.util.parsing.json.JSONObject


/*
    @author    YuSu
    @createTime    2019-06-13
   */
object Spark2Kafka {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[*]").appName("Spark2Kafka").getOrCreate()
    val brokers = """10.79.2.62:9092,10.79.2.63:9092,10.79.2.64:9092"""
    val topic = "dwr_pnl"
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
    val tableName="(select * from dwr_pnl_hist where shift_timekey='20190617 060000') a"
    val pnl = OracleJdbc(spark,tableName)
    try{
      pnl.foreach(record=>{
        kafkaProducer.value.send(topic,"dwr_pnl",JSONObject(record.getValuesMap( Seq("PNL_ID","FACTORY","SITE"))).toString())
        println(record.getValuesMap( Seq("PNL_ID","FACTORY"))+" kafka succeed")
      })
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
