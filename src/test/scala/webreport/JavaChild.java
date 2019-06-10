package webreport;/*
    @author    YuSu
    @createTime    2019-06-10
   */

public class JavaChild extends JavaParent{
    public JavaChild(String name,int age){
        super(name,age);
    }
    public int getAgeUp(){
        return super.getAge()+1;
    }
}
