package my.test;

import uuid.hashid;

public class test
{
	public static void main(String[] args)
	{
		hashid a= new hashid();
		for(int i=0;i<10;i++)
		{
		String id=a.getUUID();
		System.out.println(id);
		}

	}
}
