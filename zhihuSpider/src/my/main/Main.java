package my.main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import my.Static.Static;
import my.Threads.GetUserInfo;
import my.Threads.GetUserUrl;
import my.Threads.HandleTopic;


//bug :1 4.3的包不支持ssl 直接发个get请求会报错 换为4.5的包解决
public class Main
{

	public static void getTopicID() throws ClientProtocolException, IOException, InterruptedException
	{
		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("https://www.zhihu.com/topics");
		CloseableHttpResponse response = (CloseableHttpResponse) client.execute(get);

		// 得到String
		HttpEntity enity = response.getEntity();
		String body = EntityUtils.toString(enity, "UTF-8");

		//System.out.println(body);

		// 字符串处理 及入队
		String regex = "data-id=\"[0-9]{0,6}\"";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(body);

		while (m.find())
		{
			// System.out.println(m.group());
			// System.out.println(m.start()+"->"+m.end());
			String s = m.group();
			System.out.println(s.substring(9, s.length() - 1));
			Static.topicID.add(m.group().substring(9, s.length() - 1));
		}

		response.close();
		EntityUtils.consume(enity);

	}

	public static void getAllSubTopic() throws InterruptedException, SQLException, IOException
	{
		
		//这里的线程不能设置太大 由于知乎的反爬机制，同一ip同一时间发过多请求只能响应部分请求
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(200);// 设置最大连接数
		cm.setDefaultMaxPerRoute(200);// 对每个指定连接的服务器（指定的ip）可以创建并发20 socket进行访问
		CloseableHttpClient httpClient = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler())// 设置请求超时后重试次数
																													// 默认3次
				.setConnectionManager(cm).build();

		System.out.println("--------------- " + Static.topicID.size() + "--------------- ");
		// 注意这里不能写 i < MyQueue.topicID.size() 因为下面拿一个这里始终要变
		int len = Static.topicID.size();
		for (int i = 0; i < len; i++)
		{
			String url = "https://www.zhihu.com/node/TopicsPlazzaListV2";
			HttpPost httppost = new HttpPost(url);

			fixedThreadPool.execute(new HandleTopic(httpClient, httppost, Static.topicID.poll()));
			httppost.releaseConnection();
			System.out.println(i + "---------------------");

		}

		// 爬取完后进入下一步
		fixedThreadPool.shutdown();

		while (true)
		{

			if (fixedThreadPool.isTerminated())
			{
				httpClient.close();
				System.out.println(Static.topicID.size());
				System.out.println(
						"所有的子线程都结束了 获取secondTopicId个数为: "
								+ Static.SecondtopicID.size() + "\n");
				break;
			}
			Thread.sleep(1000);
		}

	}

	public static void getAllUserUrl() throws InterruptedException, SQLException, IOException
	{
		
		//这里的线程不能设置太大 由于知乎的反爬机制，同一ip同一时间发过多请求只能响应部分请求
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(200);// 设置最大连接数
		cm.setDefaultMaxPerRoute(200);// 对每个指定连接的服务器（指定的ip）可以创建并发20 socket进行访问
		// 上面的设置是我本机对服务器最大的连接数
		
		//建立client
		CloseableHttpClient httpClient = HttpClients.custom()
				.setRetryHandler(new DefaultHttpRequestRetryHandler())// 设置请求超时后重试次数默认3次
				.setConnectionManager(cm).build();

		System.out.println(Static.SecondtopicID.size());
		// 注意这里还是不能用i<MyQueue.SecondtopicID.size()
		int len = Static.SecondtopicID.size();
		for (int i = 0; i < len; i++)
		{
			try
			{

				HttpPost httppost = new HttpPost(
						"https://www.zhihu.com/topic/" + Static.SecondtopicID.take() + "/followers");
				System.out.println(httppost.getURI());
				 fixedThreadPool.execute(new GetUserUrl(httpClient,httppost));
				 httppost.releaseConnection();
			
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		fixedThreadPool.shutdown();
		while (true)
		{
			System.out.println("queue ..........................." + Static.SecondtopicID.size());
			System.out.println("Map size.........." + Static.map.size());
			
			 if(fixedThreadPool.isTerminated()){
		    httpClient.close();
		    System.out.println("所有的子线程都结束了！" + Static.SecondtopicID.size());


		 }
			Thread.sleep(1000);
		}

	}

	public static void getFromDB() throws SQLException
	{
		Connection conn = Static.getConn();
		java.sql.Statement s = conn.createStatement();

		String sql = "SELECT * FROM userurl";

		// 获取ResultSet 返回的实际上就是一张数据表，有一个指针指向数据表的第一行的前面
		ResultSet rs = s.executeQuery(sql);
		int i = 0;
		while (rs.next())
		{

			String url = rs.getString("url");
			i++;

			Static.userurl.add(url);
			System.out.println(url);
		}
		System.out.println("共543ms" + i);
	}

	public static void getAllUser() throws InterruptedException, SQLException, IOException
	{

		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(200);// 设置最大连接数
		cm.setDefaultMaxPerRoute(200);// 对每个指定连接的服务器（指定的ip）可以创建并发20 socket进行访问
		// 上面的设置是我本机对服务器最大的连接数
		CloseableHttpClient httpClient = HttpClients.custom()
				.setRetryHandler(new DefaultHttpRequestRetryHandler())// 设置请求超时后重试次数默认3次
				.setConnectionManager(cm).build();

		// 从数据库中拿出
		getFromDB();
		int len = Static.userurl.size();

		System.out.println(len);
		for (int i = 0; i < len; i++)
		{
			String userurl = "https://www.zhihu.com/people/" + Static.userurl.take() + "/about";
			HttpGet httpget = new HttpGet(userurl);
			fixedThreadPool.execute(new GetUserInfo(httpClient, httpget));
			httpget.releaseConnection();
		}

		fixedThreadPool.shutdown();
		while (true)
		{

			if (fixedThreadPool.isTerminated())
			{
				httpClient.close();
				System.out.println("所有的线程都结束了！");
				break;
			}
			Thread.sleep(1000);
		}

	}

	public static void main(String[] args)
	{

		try
		{
			long t1 = System.currentTimeMillis();
			getTopicID();
			getAllSubTopic();
			
			//注:userurl爬完后存入数据库后才能调用getAllUser();
			getAllUserUrl();
			//getAllUser();
			long t2 = System.currentTimeMillis();
			System.out.println("花费" + (t2 - t1) + "ms");
		} catch (Exception e)
		{

			e.printStackTrace();
		}

	}

}
