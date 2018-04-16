package my.Threads;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import my.Static.Static;
import my.user.ZhiHuUser;

public class GetUserInfo extends Thread
{
	 private final CloseableHttpClient httpClient;
     private final HttpContext context;
     private final HttpGet httpget;

     public GetUserInfo(CloseableHttpClient httpClient, HttpGet httpget) {
         this.httpClient = httpClient;
         this.context = HttpClientContext.create();
         this.httpget = httpget;

     }

     
     public void writeToDB(ZhiHuUser user) throws SQLException
     {
    	 Connection conn = Static.getConn();
    	 
 		
         String sql ="insert into zhihuuser(username,sex,location,bussiness,employment,postion,education,education_extra,suppose,thanks,question,answer,article,followers,following,bewatched)"
         		+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  
        
         PreparedStatement ptmt = (PreparedStatement) conn.prepareStatement(sql);
            
         
         ptmt.setString(1, user.getName());
         ptmt.setString(2, user.getSex());
         ptmt.setString(3, user.getLocation());
         ptmt.setString(4, user.getBussiness());
         ptmt.setString(5, user.getEmployment());
         ptmt.setString(6, user.getPostion());
         ptmt.setString(7, user.getEducation());
         ptmt.setString(8, user.getEducation_extra());
         ptmt.setInt(9, user.getSuppose());
         ptmt.setInt(10, user.getThanks());
         ptmt.setInt(11, user.getQuestion());
         ptmt.setInt(12, user.getAnswer());
         ptmt.setInt(13, user.getArticle());
         ptmt.setInt(14, user.getFollowers());
         ptmt.setInt(15, user.getFollowing());
         ptmt.setInt(16, user.getBewatched());
         
 		 ptmt.executeUpdate();
 		 
     }

     @Override
     public void run() {
         try {
        	 
        	 //必须要手机发包才能访问       	 
        	 httpget.setHeader("Cookie","d_c0=\"AFBAQf6XlgqPTtYdnN-RzRPzpjaWuytWSLo=|1474684865\"; _za=fb1a1ef3-9484-4225-8849-9fab90e0feba; _zap=7cdbe920-a6de-40f5-b7b2-af7821420a3e; nweb_qa=heifetz; _xsrf=fa2a707345fb80b139e35af06eb4e9d0; _ga=GA1.2.1704872840.1490540507; capsion_ticket=\"2|1:0|10:1490871560|14:capsion_ticket|44:ODMyNWY4MzMwZjA1NDUzYWI0NGZiNjFlNzNiZjllNWY=|7e45182e5ee7a4d1f38ce1cc0d42545fd5216f4f3291865bc77cbea893abad68\"; aliyungf_tc=AQAAAPgLXlk4mQgABdv7ccQfshtJdRmP; q_c1=2878e530c4594d11b86b945817daeda5|1490947400000|1490947400000; r_cap_id=\"Y2E5Zjc1MDM4MjQzNDgyNGEzZjMwNjQ2MDc4OGZlMWY=|1490947400|fbcc43997eb568bb122dd768bb6ac62ab8b1c471\"; cap_id=\"OGIyZmQxZjQyZjNlNDllODhmMGE3ODAzOGMzODc0OTU=|1490947400|98209fe4abe8cadf0f672b5ff0c58223efce859e\"; l_n_c=1; __utma=51854390.1704872840.1490540507.1490942040.1490947224.2; __utmb=51854390.0.10.1490947224; __utmc=51854390; __utmz=51854390.1490942040.1.1.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/topic/19550517/followers; __utmv=51854390.100--|2=registration_date=20151203=1^3=entry_date=20151203=1; "
         	 		+ "z_c0="+Static.getCookie());
          	 httpget.setHeader("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7");
        	
             CloseableHttpResponse response = httpClient.execute(
                     httpget, context);
             HttpEntity entity = response.getEntity();
             try {
              
                 if (entity != null) {
     	          
     	        String body=EntityUtils.toString(entity, "UTF-8");

     	                	           
     	           
     	        //正则匹配
     	          String regex="(<title>.*</title>)|(data-gender=\".\")|(location item\" title=.{0,30}\">)|(business item\" title=.{0,30}\">)|"
     	 		 		+ "(employment item\" title=.{0,30}\">)|"+"(position item\" title=.{0,30}\">)|"+"(education item\" title=.{0,30}\">)|"
     	 				+ "(education-extra item\" title=.{0,30}>)|"+"([0-9]*</strong>赞同)|"+"([0-9]*</strong>感谢)|"+"(提问\\n<span class=\"num\">[0-9]*)|"+"(回答\\n<span class=\"num\">[0-9]*)|"
     	 		        + "(文章\\n<span class=\"num\">[0-9]*)|"+"(关注者</span><br />\\n<strong>[0-9]*)|"+"(关注了</span><br />\\n<strong>[0-9]*)|" 
     	 				+ "(个人主页被 <strong>[0-9]*)|"+"(<title>.*</title>)|"+"(data-gender=\".\")";
     	 		 
     	 		 
     	          
     	 		 Pattern p=Pattern.compile(regex);
     	 		 Matcher m=p.matcher(body); 
     	 		 
     	 		ZhiHuUser user=new ZhiHuUser();
     	 		while (m.find())
     	 		{

     	 			//System.out.println(m.group());
     	 			String s = m.group();
     	 			s=s.replace("\n", "");
     	 			if(s.startsWith("<title>"))
     	 			{
     	 				s=s.substring(8, s.length()-13);
     	 				user.setName(s);
     	 			}
     	 			
     	 			if(s.startsWith("location"))
     	 			{
     	 				s=s.substring(22, s.length()-2);
     	 				user.setLocation(s);
     	 			}
     	 			if(s.startsWith("business"))
     	 			{
     	 				s=s.substring(22, s.length()-2);
     	 				user.setBussiness(s);
     	 			}
     	 			if(s.startsWith("employment"))
     	 			{
     	 				s=s.substring(24, s.length()-2);
     	 				user.setEmployment(s);
     	 			}
     	 			if(s.startsWith("position"))
     	 			{
     	 				s=s.substring(22, s.length()-2);
     	 				user.setPostion(s);
     	 			}
     	 			if(s.startsWith("education-extra"))
     	 			{
     	 				s=s.substring(29, s.length()-2);
     	 				user.setEducation_extra(s);
     	 			}
     	 			
     	 			if(s.startsWith("education "))
     	 			{
     	 				s=s.substring(23, s.length()-2);
     	 				user.setEducation(s);
     	 			}
     	 			
     	 			if(s.endsWith("赞同"))
     	 			{
     	 				s=s.substring(0, s.length()-11);
     	 				try{
     	 				    user.setSuppose(Integer.parseInt(s));
     	 				}catch(NumberFormatException e)
     	 				{
     	 					user.setSuppose(-1);
     	 				}
     	 			}
     	 				
     	 			if(s.endsWith("感谢"))
     	 			{
     	 				try{
     	 				s=s.substring(0, s.length()-11);
     	 				user.setThanks(Integer.parseInt(s));
     	 				}catch(NumberFormatException e)
     	 				{
     	 					user.setThanks(-1);
     	 				}
     	 			}
     	 			
     	 			//sex
     	 			if(s.startsWith("data-"))
     	 			{
     	 				s=s.substring(13,s.length()-1);
     	 				user.setSex(s);			
     	 			}
     	 			
     	 			if(s.startsWith("提问"))
     	 			{
     	 				s=s.substring(20);
     	 				try{
     	 				 user.setQuestion(Integer.parseInt(s));
     	 				}catch(NumberFormatException e)
     	 				{
     	 					user.setQuestion(-1);
     	 				}
     	 			}
     	 			
     	 			if(s.startsWith("回答"))
     	 			{
     	 				s=s.substring(20);
     	 				try{
     	 					user.setAnswer(Integer.parseInt(s));
     	 				}catch(NumberFormatException e)
     	 				{
     	 					user.setAnswer(-1);
     	 				}
     	 			}
     	 			
     	 			if(s.startsWith("文章"))
     	 			{
     	 				s=s.substring(20);
     	 				try{
     	 					user.setArticle(Integer.parseInt(s));
     	 				}catch(NumberFormatException e)
     	 				{
     	 					user.setArticle(-1);
     	 				}
     	 			}
     	 			
     	 			if(s.startsWith("关注了"))
     	 			{
     	 				s=s.substring(24);
     	 				try{
     	 					user.setFollowing(Integer.parseInt(s));
     	 				}catch(NumberFormatException e)
     	 				{
     	 					user.setFollowing(-1);
     	 				}
     	 			}
     	 			
     	 			if(s.startsWith("关注者"))
     	 			{
     	 				s=s.substring(24);
     	 				try{
     	 					user.setFollowers(Integer.parseInt(s));
     	 				}catch(NumberFormatException e)
     	 				{
     	 					user.setFollowers(-1);
     	 				}
     	 			}
     	 			if(s.startsWith("个人"))
     	 			{
     	 				s=s.substring(14);
     	 				try{
     	 					user.setBewatched(Integer.parseInt(s));
     	 				}catch(NumberFormatException e)
     	 				{
     	 					user.setBewatched(-1);
     	 				}
     	 			}
     	 			//System.out.println(s);
     	 		}
     	 		
     	 		System.out.println(user.getName());
     	 		writeToDB(user);

     	        }
             } catch (SQLException e)
			{
				e.printStackTrace();
			}  finally {
				
            	 EntityUtils.consume(entity);
                 response.close();
              
                 
             }
         } catch (ClientProtocolException ex) {
             
         } catch (IOException ex) {
             
         }
     }

}