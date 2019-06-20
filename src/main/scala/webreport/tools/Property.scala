package webreport.tools

import java.io.InputStreamReader
import java.util.Map.Entry
import java.util.Properties

/*
    @author    YuSu
    @createTime    2019-06-19
   */
object Property {
  def getValue(filepath:String,key:String): String ={
    //getResourceAsStream 读二进制流，有中文会乱码，通过InputStreamReader设置编码格式UTF-8
    val in = new InputStreamReader(getClass.getResourceAsStream(filepath),"UTF-8")
    if(in == null){
      println("文件路径不存在或文件内容为空")
      return null
    }
    val prop = new Properties()
    prop.load(in)
    val value = prop.getProperty(key)
    if(value == null){
      println("key不存在")
      return null
    }
    value
  }
  def getProperty(filepath:String): Properties ={
    val in = getClass.getResourceAsStream(filepath)
    if(in == null){
      println("文件路径不存在或文件内容为空")
      return null
    }
    val prop = new Properties()
    prop.load(in)
    prop
  }

  def main(args: Array[String]): Unit = {
    val filePath = "/test.properties"
    val value = getValue(filePath,"name")
    println(value)
  }
}
