package my.Threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import my.Static.Static;
import uuid.hashid;

public class HandleTopic extends Thread
{
	 private final CloseableHttpClient httpClient;
     private final HttpContext context;
     private final HttpPost httppost;
     private final String topicid;
     private int count;//用于计数爬取的个数
     public HandleTopic(CloseableHttpClient httpClient, HttpPost httpost,String topicid) {
         this.httpClient = httpClient;
         this.context = HttpClientContext.create();
         this.httppost = httpost;
         this.topicid= topicid;
         this.count=0;
     }

     
     @Override
     public void run() {
         try {
        	 
        	 
        	System.out.println("Thread"+topicid+"Start.....");
        	 
        	Integer offset=0;
			while (true)
			{

				
				//1 设置请求herder
				//用cookie得不到数据
				//httppost.setHeader("Cookie", "d_c0=\"AFBAQf6XlgqPTtYdnN-RzRPzpjaWuytWSLo=|1474684865\"; _za=fb1a1ef3-9484-4225-8849-9fab90e0feba; _zap=7cdbe920-a6de-40f5-b7b2-af7821420a3e; q_c1=5a2cb729d3f14133939942b8dc9e1bc3|1489287092000|1474684865000; nweb_qa=heifetz; _xsrf=fa2a707345fb80b139e35af06eb4e9d0; _ga=GA1.2.1704872840.1490540507; r_cap_id=\"N2EzZWYyODUyYThhNGM5ZGIyZDViNzcyMDBkZmNhOTM=|1490871539|1087959e67d38f95dce1de17ae45ff72b091a0bc\"; cap_id=\"NTY5OWJkNTc0NjhjNGM4OGI0OGU4MDNlODgxN2Q3NmU=|1490871539|957b6c5112a97f9f9e913cde8f50e94f927d1a43\"; l_cap_id=\"MWIxNjMxNzI3N2M5NGI0ZThhYzMyZTFmYjY3Yzg0NDg=|1490871539|37abd23213f75b9455033e372c8c9f48edf084ab\"; capsion_ticket=\"2|1:0|10:1490871560|14:capsion_ticket|44:ODMyNWY4MzMwZjA1NDUzYWI0NGZiNjFlNzNiZjllNWY=|7e45182e5ee7a4d1f38ce1cc0d42545fd5216f4f3291865bc77cbea893abad68\"; aliyungf_tc=AQAAALbJ9w6oqw0ABdv7cQppXcBjz+RV; __utma=51854390.1704872840.1490540507.1490927228.1490927228.1; __utmb=51854390.0.10.1490927228; __utmc=51854390; __utmz=51854390.1490927228.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmv=51854390.100--|2=registration_date=20151203=1^3=entry_date=20151203=1; z_c0=Mi4wQUJDTUMycXFHUWtBVUVCQl9wZVdDaVlBQUFCZ0FsVk5FWElFV1FCeGtmX3VrZU1rTmVRelBwRkY4c1dxRk1lcmlB|1490927780|19e4bce6f6e472afedcd07a9390692427b0d6145");
				
				
				//2 设置请求内容
				List<NameValuePair> formparams = new ArrayList<NameValuePair>();
				formparams.add(new BasicNameValuePair("method", "next"));		
				hashid haid= new hashid();
				String hashid= haid.getUUID();
				String p2="{\"topic_id\":"+topicid+","+"\"offset\":"+offset+",\"hash_id\":\""+hashid+"\"}";
				//System.out.println(p2);
				formparams.add(new BasicNameValuePair("params",  p2));
				UrlEncodedFormEntity entityPost = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
				httppost.setEntity(entityPost);

				
				CloseableHttpResponse response = httpClient.execute(httppost,context);
				
				HttpEntity entity = response.getEntity();
				try
				{		
						
					if (entity != null)
					{

						
						String body = EntityUtils.toString(entity, "UTF-8");
						if(body.length()<100)
						{
							System.out.println("-----------------------线程结束----------------------");
							 
							break;
						}
						
						
						
						//正则匹配
						 String regex="topic..[0-9]{1,10}";
						 Pattern p=Pattern.compile(regex);
						 Matcher m=p.matcher(body);  
						 
						while (m.find())
						{
							//System.out.println(m.group());
							String s = m.group();
							System.out.println("topicID为:"+s.substring(7)+"入队");

							count++;
							Static.SecondtopicID.add(s.substring(7));
						}
						  
						System.out.println("topicid为"+topicid+"的offset="+offset);
						offset=offset+20;
						
					}
				} finally
				{
									
					EntityUtils.consume(entity);
					response.close();
						
					
				} 
				
				
				
			}
			
			System.out.println("Thread--------------------------------------:"+topicid+"爬取数量:"+count);
			
			 
		} catch (ClientProtocolException ex)
		{
			ex.printStackTrace();

		} catch (IOException ex)
		{
			ex.printStackTrace();
		}

	}
     

}
