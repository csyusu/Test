package webreport;/*
    @author    YuSu
    @createTime    2019-07-05
   */


import java.text.SimpleDateFormat;
import java.util.Date;

public class SynchronizedClass  {
    private  volatile int n=1;
    public synchronized void addN(){
        n++;
    }
    public synchronized void printA() {
        while(n%2==0){
            try{
                this.wait();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }

        }
        System.out.println("printA  "+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SS").format(new Date()));
        addN();
        this.notifyAll();
    }
    public synchronized void printB(){
        while(n%2==1){
            try{
                this.wait();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }

        }
        System.out.println("printB  "+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SS").format(new Date()));
        addN();
        this.notifyAll();
    }
}
