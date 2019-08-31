package webreport.sparktest

import java.util.Properties

object HelloWorld {

  def main(args: Array[String]): Unit = {
    val inputStream =  this.getClass.getResourceAsStream("/data.txt")
    val prop = new Properties()
    prop.load(inputStream)
    println(prop.getProperty("data"))

  }
}
