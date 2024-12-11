package com.wisenut.ebk.spring;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class HobanApplicationTests {

	@Test
	void contextLoads() throws Exception {

		List< String > list1 = new ArrayList<>( );
		list1.add( "FILENAME" );
		list1.add( "DOCUMENTNAME" );
		list1.add( "TAGLIST" );
		list1.add( "CREATOROID" );
		list1.add( "CREATORNAME" );
		list1.add( "LASTMODIFIEROID" );
		list1.add( "LASTMODIFIEDAT" );
		list1.add( "LASTMODIFIEDATN" );
		list1.add( "FILETYPE" );
		list1.add( "FILESIZE" );
		list1.add( "FOLDERFULLPATHOID" );
		list1.add( "FOLDERFULLPATHNAME" );
		list1.add( "MANAGERGROUPOID" );
		list1.add( "MANAGERGROUPFULLPATHOID" );
		list1.add( "DOCTYPEOID" );
		list1.add( "CHECKOUT" );
		list1.add( "CONTENT" );
		list1.add( "ACLKEYCODE" );
		String str = String.join( "," , list1 );
		System.out.println(str);
	}
}
