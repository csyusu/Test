package webreport

import java.util
import java.util.Properties

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

object SparkSqlTest {

  def spark1Test: Unit ={
        val conf = new  SparkConf().setAppName("SparkSqlTest").setMaster("local[2]")
        val sc = new SparkContext(conf)
        val spark = new SQLContext(sc)
    val url = "jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb"
    val tableName="DWR_PNL"
    //spark.read.jdbc方式
    val connection = new Properties()
    connection.setProperty("user","MDWADM")
    connection.setProperty("password","mdwadm1234")
    connection.setProperty("driver","oracle.jdbc.driver.OracleDriver")
    connection.setProperty("oracle_url","jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb")
    val dataFrame = spark.read.json("H:\\BDP\\scala\\webreport\\src\\main\\scala\\webreport\\data.txt")
    dataFrame.write.mode("append").jdbc(url,tableName,connection)
    //spark.read.format方式
    val oracleConf = spark.read.format("jdbc").options(
      Map(
        "driver"->"oracle.jdbc.driver.OracleDriver",
        "url"->url,
        "dbtable"->tableName,
        "user"->"MDWADM",
        "password"->"mdwadm1234",
        "fetchsize"->"3"
      )).load()
    oracleConf.registerTempTable("DWR_PNL")
    //注册临时表字段名区分大小写
    val sqlText = "select PNL_ID from DWR_PNL"
    oracleConf.sqlContext.sql(sqlText).take(10).foreach(println)
  }
  def oracleJdbc(spark:SparkSession,tableName:String): DataFrame ={
    val connection = new Properties()
    connection.put("user","MDWADM")
    connection.put("password","mdwadm1234")
    val url = "jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb"
    spark.read.jdbc(url,tableName,connection)
  }
  def oracleJdbc(spark:SparkSession,tableName:String,columnName:String,lower:Long,upper:Long,partitions:Int): DataFrame ={
    val connection = new Properties()
    connection.put("user","MDWADM")
    connection.put("password","mdwadm1234")
    val url = "jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb"
    spark.read.jdbc(url,tableName,columnName,lower,upper,partitions,connection)
  }
  def oracleJdbc(spark:SparkSession,tableName:String,predicates:Array[String]): DataFrame ={
    val connection = new Properties()
    connection.put("user","MDWADM")
    connection.put("password","mdwadm1234")
    val url = "jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb"
    spark.read.jdbc(url,tableName,predicates,connection)
  }
  def spark2Test: Unit ={
    val spark = SparkSession.builder().appName("sparkSqlTest").master("local[*]")
      //设置并行度 默认200，但jdbc不指定partition，只会有一个task读数据库
      .config("spark.sql.shuffle.partitions",100)
      //开启hash join，不倾向于sort merge
      .config("spark.sql.join.preferSortMergeJoin","false")
      //默认少于10485760（10M）的join表会进行自动广播，使用broadcast join
      //.config("spark.sql.autoBroadcastJoinThreshold ",100000000)
      .getOrCreate()
    val dwrPnl1 = "(select * from (select a.event_timekey,a.pnl_id,rownum as no from dwr_pnl_hist a where a.shift_timekey='20190530 060000') b) c"
    val columnName = "no"
    //总记录数1100，第十个分区200条记录
    val pnl1 = oracleJdbc(spark,dwrPnl1,columnName,1,1000,10)
    println(pnl1.rdd.partitions.size)
    pnl1.foreachPartition(partitionRecord=>println(partitionRecord.length))
    //predicate方式支持string，date等类型
    val dwrPnl2 = "(select event_timekey,pnl_id from dwr_pnl_hist where shift_timekey='20190530 180000') a"
    val predicates = Array("event_timekey >'20190530180000' and event_timekey <='20190530181000'"
      ,"event_timekey >'20190530181000' and event_timekey <='20190530182000'")
    val pnl2 = oracleJdbc(spark,dwrPnl2,predicates)
    val pnl2partitions = pnl2.rdd.partitions.size
    println(s"pnl2:$pnl2partitions")
    pnl2.foreachPartition(partitionRecord=>{partitionRecord.foreach(print)})
    //pnl1.join(pnl2,pnl1("pnl_id")===pnl2("pnl_id"),"left").show(10)
  }
  def partitionTest(): Unit ={
    val spark = SparkSession.builder().appName("partitionTest").master("local[3]").getOrCreate()
    //val person = spark.read.json("file:///home/data.json","file:///home/data.json")
    val person = spark.read.json("H:\\BDP\\scala\\webreport\\src\\main\\resources\\data.json")
    person.createOrReplaceTempView("pnl")
    val person1 = person.repartition(3)
    person1.createOrReplaceTempView("pnl")
    println(person1.rdd.partitions.size)
    person1.foreachPartition(partition =>{println("partition3: ");partition.foreach(print)})
    import spark._
    println(sql("select firstName,sum(seq) over (partition by firstName order by seq) as qty  from pnl").queryExecution)
    val window =  Window.partitionBy(person("firstName")).orderBy(person("seq"))

//    val person2 = person.repartition(2,person("firstName"))
//    println(person2.rdd.partitions.size)
//    person2.foreachPartition(partition =>{println("partition2: ");partition.foreach(print)})
//    val person3 = person.repartition(4,person("firstName"))
//    println(person3.rdd.partitions.size)
//    person3.foreachPartition(partition =>{println("partition4: ");partition.foreach(print)})
    spark.close()
  }
  def main(args: Array[String]): Unit = {

    //spark2Test
    partitionTest()
    //Thread.sleep(10000000)
  }
}
