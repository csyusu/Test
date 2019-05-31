package webreport

import java.util
import java.util.Properties

import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

object SparkSqlTest {

  def spark1Test: Unit ={
        val conf = new  SparkConf().setAppName("SparkSqlTest").setMaster("local[*]")
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
  def spark2Test: Unit ={
    val spark = SparkSession.builder().appName("sparkSqlTest").master("local[*]").getOrCreate()
    val dwrPnl1 = "(select event_timekey,pnl_id from dwr_pnl_hist where shift_timekey='20190530 060000') a"
    val dwrPnl2 = "(select event_timekey,pnl_id from dwr_pnl_hist where shift_timekey='20190530 180000') a"
    val connection = new Properties()
    connection.put("user","MDWADM")
    connection.put("password","mdwadm1234")
    val url = "jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb"
    val pnl1 = spark.read.jdbc(url,dwrPnl1,connection)
    val pnl2 = spark.read.jdbc(url,dwrPnl2,connection)
    pnl1.join(pnl2,pnl1("pnl_id")===pnl2("pnl_id"),"left").show(10)
  }
  def main(args: Array[String]): Unit = {

    spark2Test
  }
}
