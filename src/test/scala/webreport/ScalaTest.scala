package webreport

/*
    @author    YuSu
    @createTime    2019-03-05
   */
import java.lang

import scala.annotation.tailrec
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex

object ScalaTest {

  def fileRead(path: String, compressWhiteSpace: Boolean = false): Seq[String] = {
    for{
      line <- scala.io.Source.fromFile(path).getLines().toSeq
      if(line.matches("""^\s*$"""))==false
      line2 = if(compressWhiteSpace) line.replaceAll("\\s+"," ")
      else line
    } yield line2
  }

  def forTest: Unit ={
    val list = List((1,2),(3,4))
    val y =0
    val result = for{
    x<-list
    } {
      println("x:"+x+"y:"+y)
    }
  }
  def forTest2={
    val kvRegex = """(\w*[.]\w*)=(\w*)""".r
    val propertie=Array("book.authors=Deane","book.name=Programmin")
    val kvPairs = for {
      //prop <- properties.split("#")
      prop <- propertie
      //if ignoreRegex.findFirstIn(prop) == None

      kvRegex(k,v)=prop
    } yield (k.trim,v.trim)
    println(kvPairs.mkString("\n"))
  }

  def regexTest(): Unit ={
    val date="""(\d\d\d\d-\d\d-\d\d)""".r
    "2018-09-01" match{
      case date(x)=>println(s"year:${x}")
      case _ =>println("未匹配")
    }
    val embeddedDate = date.unanchored
    "Date: 2004-01-20 17:25:18 GMT (10 years, 28 weeks, 5 days, 17 hours and 51 minutes ago)" match {
      case embeddedDate(y) => println(s"${y}")
    }
    println(date.findAllIn("Important dates in history: 2004-01-20, 1958-09-05, 2010-10-06, 2011-07-15").mkString(" "))
    val datePattern = new Regex("""(\d\d\d\d)-(\d\d)-(\d\d)""", "year", "month", "day")
    val text = "From 2011-07-15 to 2011-07-17"
    val repl = datePattern.replaceAllIn(text, m => "")
    println(repl)
    val pattern="""((\d+)([a-z]+))""".r
    val pattern(x,y,z)="123abc"
    println(s"x:${x}\ty:${y}\tz:${z}")
  }
  def optionTest: Unit ={
    val results :Seq[Option[Int]] = Vector(Some(10),None,Some(20))
    val result = for{
      Some(i)<-results
    }yield i*2
    println(result)
  }

  def positive(i: Int): Either[String,Int] =
    if (i > 0) Right(i) else Left(s"nonpositive number $i")

  def time={
    println("获取时间")
    System.nanoTime()
  }
  //传名调用 变量名和变量类型中使用 => 表示传名调用，在函数内部进行参数表达式的值计算
  def delay(t : => Long)={
    println("进入delay")
    println(f"参数:$t")
    t
  }
  //传值调用 默认，把参数表达式的值传入函数内部
  def noDelay(t:Long)={
    println("进入delay")
    println(f"参数:$t")
    t
  }

  def main(args: Array[String]): Unit = {
    val predicates =
      Array(
        "2015-09-16" -> "2015-09-30",
        "2015-10-01" -> "2015-10-15",
        "2015-10-16" -> "2015-10-31",
        "2015-11-01" -> "2015-11-14",
        "2015-11-15" -> "2015-11-30",
        "2015-12-01" -> "2015-12-15"
      ).map {
        case (start, end) =>
          s"cast(modified_time as date) >= date '$start' " + s"AND cast(modified_time as date) <= date '$end'"
      }
    predicates.foreach(print)

  }
}


