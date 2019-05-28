package webreport

import java.io.StringReader

import au.com.bytecode.opencsv.CSVReader
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.storage.StorageLevel
//import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{HashPartitioner, Partitioner, SparkConf, SparkContext}
import org.codehaus.janino.Java

/*
    @author    YuSu
    @createTime    2019-03-26
   */

//Spark 1.6.0
object Spark1Test {

  def partitionTest: Unit ={
    val conf = new SparkConf().setAppName("Test").setMaster("local[*]")
    val sc = new SparkContext(conf)
    //val partitionRdd = sc.sequenceFile("H:\\BDP\\scala\\webreport\\src\\JavaTest\\scala\\webreport\\wordcount.txt").partitionBy( new HashPartitioner(3)).persist(StorageLevel.MEMORY_AND_DISK_SER)

  }

  def fileTest={
    val conf = new SparkConf().setAppName("Test").setMaster("local[1]")
    val sc = new SparkContext(conf)
    val wholeTextFile = sc.wholeTextFiles("H:\\BDP\\scala\\webreport\\src\\main\\scala\\resources\\data*.txt").persist(StorageLevel.MEMORY_AND_DISK_SER)
    println(wholeTextFile.collectAsMap().mkString)
    //val textFile = sc.textFile("H:\\BDP\\scala\\webreport\\src\\main\\scala\\resources\\data*.txt").persist(StorageLevel.MEMORY_AND_DISK_SER)
    val textFile = sc.textFile("H:/tmp/test5.txt").persist(StorageLevel.MEMORY_AND_DISK_SER)
    textFile.foreach(println)
  }
  def csvTest={
    val conf = new SparkConf().setMaster("local[*]").setAppName("csvTest")
    val sc = new SparkContext(conf)
    val csvFile = sc.textFile("H:\\BDP\\scala\\webreport\\src\\main\\scala\\resources\\data.csv")
    val result = csvFile.map { line =>
      val reader = new CSVReader(new StringReader(line));
      reader.readNext()
    }
    result.foreach(x=>println(x.mkString(" ")))
  }
  def jsonTest={
    val conf = new SparkConf().setAppName("jsonTest").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val hc = new HiveContext(sc)
    val json = hc.read.json("H:\\BDP\\scala\\webreport\\src\\main\\scala\\resources\\data.json")
    json.registerTempTable("person")
    val result=hc.sql("select person from person")
    result.foreach(x=>println(x.toString()))
  }

  def logTest={
    val conf = new SparkConf().setMaster("local[*]").setAppName("logCount").set("spark.sql.codegen", "true")
    val sc = new SparkContext(conf)
    val logFile=sc.textFile("H:\\BDP\\scala\\webreport\\src\\main\\scala\\resources\\data.txt").persist()
    val rdd1 = logFile.flatMap(_.split(" ").take(1)).filter(x =>x.length>0)
    val rdd2 = rdd1.map((_,1)).reduceByKey(_+_)
    println(rdd1.getNumPartitions)
    println(rdd2.getNumPartitions)
    println(rdd2.toDebugString)
    rdd2.coalesce(10)
  }
  def sqlTest={
    val conf = new SparkConf().setAppName("sqlTest").setMaster("local[*]").set("spark.sql.codegen", "true")
    val sc = new SparkContext(conf)
    val hiveCtx = new HiveContext(sc)
    val file = hiveCtx.read.json("H:\\BDP\\scala\\webreport\\src\\main\\scala\\resources\\data.json")
    file.registerTempTable("person")
    val person=hiveCtx.sql("""select * from person""")
    person.printSchema()
    person.show()
  }
  def streamingSocketTest={
    val conf = new SparkConf().setMaster("local[*]").setAppName("streaming")
    val ssc = new StreamingContext(conf,Seconds(10))
    ssc.checkpoint("/tmp")
    val lines = ssc.socketTextStream("10.79.1.61",8081)
    val lineMap = lines.map((_,1))
    //窗口函数（窗口时长，步长）
    val linesWindow = lineMap.window(Seconds(30),Seconds(20))

    val linesWindow2 = lineMap.reduceByKeyAndWindow(_+_,Seconds(30))
    //提供逆函数，增量规约
    val linesWindow3 = lineMap.reduceByKeyAndWindow(_+_,_-_,Seconds(30),Seconds(10))
    linesWindow.count().print()
    //val lines = ssc.socketTextStream("10.68.27.140",520)
    val errors = lines//.filter(_.contains("error"))
    errors.print()
    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
  def streamingFileTest={
    val conf = new SparkConf().setAppName("streamingFileTest").setMaster("local[*]")
    val ssc = new StreamingContext(conf,Seconds(60))
    ssc.checkpoint("/tmp")
    val dir = "H:/tmp/"
    val logData = ssc.textFileStream(dir)
    logData.foreachRDD(_.foreach(println))
    logData.print()
    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
  /*
  def streamingKafkaTest={
    val conf = new SparkConf().setMaster("local[2]").setAppName("kafkaTest")
    val ssc = new StreamingContext(conf,Seconds(30))
    ssc.checkpoint("/tmp")
    val zk = "10.79.2.92:2181/chroot"
    //val zk = "10.79.2.41:2181/chroot"
    val group = "test"
    val topic = Map("test"->1)
    val lines = KafkaUtils.createStream(ssc,zk,group,topic)
    lines.print()
    Thread.sleep(10000)
    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
  */
  def main(args: Array[String]): Unit ={
    //Thread.sleep(360000)
  }
}

