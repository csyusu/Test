package webreport.sparktest.tools

import java.text.SimpleDateFormat
import java.util.Date
object Time {

  def getTimeDValue(startTime:String,endTime:String):String={
    var df:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val stime = df.parse(startTime)
    val etime = df.parse(endTime)
    return ((etime.getTime-stime.getTime)/1000).toDouble.toString

  }

  def getTime(): String ={
    var df:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val time = new Date()

    return df.format(time)
  }

  def printTime(className:String,start_time:String,end_time:String,use_time:String):Unit={

    println("\n\n=====================================spark==============================================")
    println("running job ： " + className.substring(0,className.length-1) )
    println("start time : " + start_time)
    println("end time : " + end_time)
    println("total time consuming : " + use_time+"\n\n")
  }
}

