package webreport

/*
    @author    YuSu
    @createTime    2019-04-29
   */

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.serializer.KryoRegistrator;

import com.esotericsoftware.kryo.Kryo;

class MyRegistrator extends KryoRegistrator {

  override def registerClasses(kyro:Kryo) {
    //kyro.register(ConsumerRecord)
}

}