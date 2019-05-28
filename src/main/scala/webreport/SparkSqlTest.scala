package webreport

import java.util.Properties

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object SparkSqlTest {

  def main(args: Array[String]): Unit = {
    val conf = new  SparkConf().setAppName("SparkSqlTest").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val url = "jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb"
    val tableName="DWR_PNL"
    //写入oracle
    val connection = new Properties()
    connection.setProperty("user","MDWADM")
    connection.setProperty("password","mdwadm1234")
    connection.setProperty("driver","oracle.jdbc.driver.OracleDriver")
    connection.setProperty("oracle_url","jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb")
    //val dataFrame = sqlContext.read.json("H:\\BDP\\scala\\webreport\\src\\main\\scala\\webreport\\data.txt")
    //dataFrame.write.mode("append").jdbc(url,tableName,connection)
    //读取oracle
    val oracleConf = sqlContext.read.format("jdbc").options(
      Map(
        "driver"->"oracle.jdbc.driver.OracleDriver",
        "url"->url,
        "dbtable"->tableName,
        "user"->"MDWADM",
        "password"->"mdwadm1234",
        "fetchsize"->"3"
      )).load()
    oracleConf.registerTempTable("DWR_PNL")
    //注册临时表字段名表名皆大写
    val sqlText = "select PNL_ID from DWR_PNL"
    oracleConf.sqlContext.sql(sqlText).take(10).foreach(println)


  }
}
