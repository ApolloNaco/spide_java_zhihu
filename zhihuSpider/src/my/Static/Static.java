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
	//1��topicID
	public static   BlockingQueue<String> topicID = new  ArrayBlockingQueue<String>(50); 
	
	//2��topicID
	public static BlockingQueue<String> SecondtopicID = new ArrayBlockingQueue<String>(50000);
	
	//3 �ȴ���һ���̰߳�ȫ��int
	public static AtomicInteger UserCount=new AtomicInteger(0);
	//public static HashMap<String,Integer> map=new HashMap<String,Integer>();
	
	public static  Map<String,Integer> map = Collections.synchronizedMap(new HashMap<String,Integer>());
	
	//4 ���userurl Ҫ���������ö��ٴ�С
	public static BlockingQueue<String> userurl = new ArrayBlockingQueue<String>(100000);

	//5 ���ݿ�
	public static Connection conn=getConn();
    public static Connection getConn()
	{

    	
		 String driver = "com.mysql.jdbc.Driver";
		    //��ʾ����Ҫ�ַ���
		    String url = "jdbc:mysql://localhost:3306/my?useUnicode=true&characterEncoding=utf8";
		    String username = "root";
		    String password = "root";
		    Connection conn = null;
		    try {
		        Class.forName(driver); //classLoader,���ض�Ӧ����
		        conn = (Connection) DriverManager.getConnection(url, username, password);
		    } catch (ClassNotFoundException e) {
		        e.printStackTrace();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return conn;
	}
    
    
    //6 ��ȡһ��cookie
    public static String[] cookies=new String[22];    
    static{
    	
    	//����cookie���� ��Ҫ���Ա��򼸸��ţ�Ҳ�������Լ��ĺţ�ץ���� �ҵ�zc_0=".." �м�Ĳ��ּӵ�������鵱�У� ���¾���һ��zc_0��ֵ��֪����������ж��û������õ�Խ��Խ�����׷�ţ�
    	cookies[0]="Mi4wQUZEQ243R3JrUXNBQUlKU1lBQ1hDeGNBQUFCaEFsVk5lOGNWV1FEUkxIMkZrdG5mLWp4eTBTbEhaejdldU9rVm5B|1492007551|10c21c3b1bab28a836483200300f73d8e38f6be2";
     
    }
    
    public static String getCookie()
    {        
    	Random random = new Random();
        int a=random.nextInt(21);
        return cookies[a];
    }
	
	
}