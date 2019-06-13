package webreport.tools

import java.sql.DriverManager
import java.util.Properties

import org.apache.spark.sql.{DataFrame, SparkSession}

object OracleJdbc {
  def apply(spark:SparkSession,tableName:String): DataFrame ={
    val connection = new Properties()
    connection.put("user","MDWADM")
    connection.put("password","mdwadm1234")
    val url = "jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb"
    spark.read.jdbc(url,tableName,connection)
  }
  def apply(spark:SparkSession,tableName:String,columnName:String,lower:Long,upper:Long,partitions:Int): DataFrame ={
    val connection = new Properties()
    connection.put("user","MDWADM")
    connection.put("password","mdwadm1234")
    val url = "jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb"
    spark.read.jdbc(url,tableName,columnName,lower,upper,partitions,connection)
  }
  def apply(spark:SparkSession,tableName:String,predicates:Array[String]): DataFrame ={
    val connection = new Properties()
    connection.put("user","MDWADM")
    connection.put("password","mdwadm1234")
    val url = "jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb"
    spark.read.jdbc(url,tableName,predicates,connection)
  }
}
