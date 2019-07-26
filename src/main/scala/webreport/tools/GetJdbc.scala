package webreport.tools

import java.sql.{DriverManager, ResultSet}
import java.util.Properties

import webreport.tools.GetOracleDataframe.conn

/*
    @author    YuSu
    @createTime    2019-06-21
   */
object GetJdbc {
  val url = """jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb"""
  val prop = new Properties()
  prop.put("user","MDWADM")
  prop.put("password","mdwadm1234")
  prop.put("driver","oracle.jdbc.driver.OracleDriver")
  val conn = DriverManager.getConnection(url,prop)
  val stateMent = conn.createStatement()
  def  select(sqltext:String):ResultSet ={
    stateMent.executeQuery(sqltext)
  }
  def update(sqlTexts:Array[String]):Unit ={
    conn.setAutoCommit(false)
    for(sqlText <-sqlTexts){
      stateMent.addBatch(sqlText)
    }
    stateMent.executeBatch()
    conn.commit()
  }
  @Override
  def update(sqlText:String): Unit ={
    conn.setAutoCommit(false)
    stateMent.execute(sqlText)
    conn.commit()
  }
  def insert(sqlText:String): Unit ={
    conn.setAutoCommit(false)
    stateMent.execute(sqlText)
    conn.commit()
  }

  def selectTest(): Unit ={
    val sqlText = """select pnl_id,oper_code from dwr_pnl where rownum<10"""
    val resultSet = select(sqlText)
    while(resultSet.next()){
      println("pnl_id:"+resultSet.getString("pnl_id")+"\toper_code:"+resultSet.getString(1))
    }
  }
  def main(args: Array[String]): Unit = {

  }

}
