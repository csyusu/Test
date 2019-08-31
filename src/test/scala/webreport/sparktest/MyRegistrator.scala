package webreport.sparktest

/*
    @author    YuSu
    @createTime    2019-04-29
   */

import com.esotericsoftware.kryo.Kryo
import org.apache.spark.serializer.KryoRegistrator;

class MyRegistrator extends KryoRegistrator {

  override def registerClasses(kyro:Kryo) {
    //kyro.register(ConsumerRecord)
}

}