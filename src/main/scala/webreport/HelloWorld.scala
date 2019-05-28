package webreport

import java.io.File
import java.util.Properties

import org.apache.spark.{SparkConf, SparkContext}

object HelloWorld {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("HelloWorld").setMaster("local")
    val sc = new SparkContext(conf)
    val inputStream =  this.getClass.getResourceAsStream("/data.txt")
    val prop = new Properties()
    prop.load(inputStream)
    println(prop.getProperty("data"))

  }
}
