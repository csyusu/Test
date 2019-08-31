package webreport.sparktest


import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

/*
    @author    YuSu
    @createTime    2019-04-29
   */
object Kafka2Hive {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Kafka2Hive")
    sparkConf.set("spark.serializer","org.apache.spark.serializer.KryoSerializer")
    sparkConf.set("spark.streaming.backpressure.enabled","true")
    //每个partition每秒取的记录数
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition","10000")
    //val sparkSession = SparkSession.builder.config(sparkConf).enableHiveSupport().getOrCreate()
    //sparkConf.set("spark.kryo.registrator","com.lm.kryo.MyRegistrator")
    val ssc = new StreamingContext(sparkConf,Seconds(60))
//    ssc.checkpoint("/tmp/checkpoint")
    val brokers = """10.79.2.62:9092,10.79.2.63:9092,10.79.2.64:9092"""
    //latest earliest
    val kafkaParams = Map[String,Object](
      "bootstrap.servers"->brokers,
      "key.deserializer"->classOf[StringDeserializer],
      "value.deserializer"->classOf[StringDeserializer],
      "group.id"->"report",
      "auto.offset.reset"->"earliest",
      "enable.auto.commit"->(false:java.lang.Boolean)
    )
    val topics = Array("dwr_pnl")
    //指定offset值
//    val fromOffSet:Map[TopicPartition,Long] = Map(new TopicPartition("dwr_pnl",0)->22L,
//      new TopicPartition("dwr_pnl",3)->233964L)
//    val messages = KafkaUtils.createDirectStream[String,String](ssc,PreferConsistent,Subscribe[String,String](topics,kafkaParams,fromOffSet))
    val messages = KafkaUtils.createDirectStream[String,String](ssc,PreferConsistent,Subscribe[String,String](topics,kafkaParams))
    //foreachRDD运行在driver端，类似jdbc等需序列化的对象，无法从driver传输到executor，因此jdbc连接应使用懒加载连接池在foreachPartition或partition中建立连接
    messages.foreachRDD{record=>
      if(record.count()>0) {
        //获得offset值
        record.asInstanceOf[HasOffsetRanges].offsetRanges.foreach(println)
        val spark = SparkSession.builder.config(record.sparkContext.getConf).enableHiveSupport().getOrCreate()
        import spark.sql
        //使用toDF需要import隐式转换
        import spark.implicits._
        // val df = record.map(_.value()).toDF()
        val df = spark.read.json(record.map(_.value()))

        println("df:")
        df.show()
        df.createOrReplaceTempView("pnl")
//        saveToHive(spark)
//        Thread.sleep(60000)
      }
    }
    ssc.start()
    ssc.awaitTermination()
  }
  def saveToHive(spark: SparkSession): Unit ={
    import spark._
    val pnl = sql("""select 'test' as event_timekey,pnl_id,factory from pnl""")
    sql("use mdw")
    sql("set hive.exec.dynamic.partition.mode=nonstrict")
    sql("""set hive.enforce.bucketing=true""")
    //        sql("""insert into test partition(factory) select * from pnl""")
    println("succeed")
    pnl.write.mode("append").format("hive").partitionBy("factory").saveAsTable("test")

  }
}
