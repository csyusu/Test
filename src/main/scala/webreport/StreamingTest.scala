package webreport

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.LoggerFactory
import webreport.tools.GetJdbc

/*
    @author    YuSu
    @createTime    2019-06-24
   */
object StreamingTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("StreamingTest")
      //.setMaster("local[*]")
    val ssc = new StreamingContext(conf,Seconds(60))
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val logger = LoggerFactory.getLogger(this.getClass.getName)
    val streaming = ssc.textFileStream("/home")
    streaming.repartition(1).foreachRDD(rdd=>{
      println(rdd.count())
      for( n <-1 to 5) {
        val curTime = dateFormat.format(new Date())
        println("curTime" + curTime)
        logger.info("curTime" + curTime)
        GetJdbc.insert(s"insert into test(timekey) values ('$curTime')")
        Thread.sleep(10000)
      }
    })

    ssc.start()
    ssc.awaitTermination()
    ssc.stop(true,true)
//    Runtime.getRuntime.addShutdownHook(
//      new Thread()
//      {
//        println("stop gracefully start")
//        ssc.stop(true,true)
//        println("stop gracefully stop")
//      })
//    scala.sys.addShutdownHook({
//      println("stop gracefully start")
//      ssc.stop(true,true)
//      println("stop gracefully stop")
//      })
  }

}
