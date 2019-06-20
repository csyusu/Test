package webreport;/*
    @author    YuSu
    @createTime    2019-04-23
   */


import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.janino.Java;

import java.util.*;

public class JavaTest {
        //调用swap时，会生成局部变量 a b ，因为a b 存储的是对象的地址，因此调用.setId更改了局部变量a指向地址的内容，a =b 修改了局部变量a的内容
        private static void swap(Person a, Person b) {
            a.setId(20);
            a=b;
        }
        //java除8种基本数据类型外，对象变量都是存储的内存地址
        public void variableTest(){
            Person a = new Person(23, "a");
            Person b = new Person(22, "b");
            System.out.println("交换前\na:" + a.getId());
            swap(a, b);
            System.out.println("交换后\na:" +a.getId());
        }
        public void mapTest(){
            Map<String,Integer> map = new HashMap<String, Integer>(2);
            map.put("a",1);
            map.put("a",map.get("a")+1);
            map.put("b",2);
            map.put("abd",4);
            for(Map.Entry entry:map.entrySet()){
                System.out.println(entry.hashCode());

            }
        }
        /*
        父类引用指向了子类对象，叫做协变+T
         */
        public void extendsTest(){
            JavaParent parent = new JavaParent("P",20);
            JavaChild child = new JavaChild("C",20);
            JavaParent[] parents = new JavaParent[10];
            //父类的引用可以指向子类对象，子类引用不能指向父类对象。
            parents[0] = child;
            System.out.println(parents[0].getName());
            System.out.println("Parent:"+parent.getClass());
            System.out.println("Child"+child.getClass());
        }
        /*
        根据IEEE标准，float0.3实际存储略大于0.3
         */
        public void floatTest(){
            float a = 0.3f;
            //float :0.30000001192092896000
            System.out.printf("float :%.20f\n",a);
            //double :0.30000000000000000000
            System.out.printf("double :%.20f\n",0.3);
            //字面量0.3，默认为double类型，比较时把float类型转变为double类型，float后面补0，因此float 0.3>double 0.3
            System.out.println("float>double:"+(a>0.3));
        }
        /*
        HBase rowKey Md5取前三位 不如hashcode取模，当取模数m=2的N次方时，取模可以优化为对m-1做位与（参考hashMap实现方式）
         */
        public void md5Test(){
            System.out.println("Md5:"+DigestUtils.md5Hex("9ABA8X0762-1"));
            int m =16;
            System.out.println("9ABA8X0762-1".hashCode()%m);
            System.out.println("9ABA8X0762-1".hashCode()&(m-1));
        }
        public void forEachTest(){
            ArrayList<String> arrayList = new ArrayList<String>(10);
            arrayList.add("a");
            arrayList.add("b");
            arrayList.forEach(x->{
                System.out.println(x);
            });

        }
        public static void main(String[] args) {
            JavaTest javaTest = new JavaTest();
            List<Integer> list = new LinkedList<>();
            list.add(1);
            list.add(2);
            ListIterator listIterator=list.listIterator();
            listIterator.next();
            listIterator.add(3);
            System.out.println(listIterator.next());
            list.forEach(x->{
                System.out.println(x);
            });
        }


    }
