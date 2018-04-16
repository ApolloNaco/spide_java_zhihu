package my.Static;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Static
{
	//1主topicID
	public static   BlockingQueue<String> topicID = new  ArrayBlockingQueue<String>(50); 
	
	//2次topicID
	public static BlockingQueue<String> SecondtopicID = new ArrayBlockingQueue<String>(50000);
	
	//3 先创建一个线程安全的int
	public static AtomicInteger UserCount=new AtomicInteger(0);
	//public static HashMap<String,Integer> map=new HashMap<String,Integer>();
	
	public static  Map<String,Integer> map = Collections.synchronizedMap(new HashMap<String,Integer>());
	
	//4 存放userurl 要爬多少设置多少大小
	public static BlockingQueue<String> userurl = new ArrayBlockingQueue<String>(100000);

	//5 数据库
	public static Connection conn=getConn();
    public static Connection getConn()
	{

    	
		 String driver = "com.mysql.jdbc.Driver";
		    //显示中文要字符集
		    String url = "jdbc:mysql://localhost:3306/my?useUnicode=true&characterEncoding=utf8";
		    String username = "root";
		    String password = "root";
		    Connection conn = null;
		    try {
		        Class.forName(driver); //classLoader,加载对应驱动
		        conn = (Connection) DriverManager.getConnection(url, username, password);
		    } catch (ClassNotFoundException e) {
		        e.printStackTrace();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return conn;
	}
    
    
    //6 获取一个cookie
    public static String[] cookies=new String[22];    
    static{
    	
    	//关于cookie部分 需要在淘宝买几个号（也可以用自己的号）抓个包 找到zc_0=".." 中间的部分加到这个数组当中， 如下就是一个zc_0的值（知乎根据这个判断用户，设置的越多越不容易封号）
    	cookies[0]="Mi4wQUZEQ243R3JrUXNBQUlKU1lBQ1hDeGNBQUFCaEFsVk5lOGNWV1FEUkxIMkZrdG5mLWp4eTBTbEhaejdldU9rVm5B|1492007551|10c21c3b1bab28a836483200300f73d8e38f6be2";
     
    }
    
    public static String getCookie()
    {        
    	Random random = new Random();
        int a=random.nextInt(21);
        return cookies[a];
    }
	
	
}