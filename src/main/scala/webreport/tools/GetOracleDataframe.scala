package webreport.tools


import java.util.Properties

import org.apache.spark.sql.{DataFrame, SparkSession}

object GetOracleDataframe {
  final val url = "jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb"
  final val conn = new Properties()
  conn.put("user","MDWADM")
  conn.put("password","mdwadm1234")
  conn.put("driver","oracle.jdbc.driver.OracleDriver")
  def apply(spark:SparkSession,tableName:String): DataFrame ={
    spark.read.jdbc(url,tableName,conn)
  }
  def apply(spark:SparkSession,tableName:String,columnName:String,lower:Long,upper:Long,partitions:Int): DataFrame ={
    spark.read.jdbc(url,tableName,columnName,lower,upper,partitions,conn)
  }
  def apply(spark:SparkSession,tableName:String,predicates:Array[String]): DataFrame ={
    spark.read.jdbc(url,tableName,predicates,conn)
  }
}
