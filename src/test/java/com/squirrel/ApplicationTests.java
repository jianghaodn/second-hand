package com.squirrel;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Resource
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Test
	public void test() {
		System.out.println(bCryptPasswordEncoder.encode("dahaozi"));
	}

	@Test
	public void testVersion(){
		System.out.println(SpringBootVersion.getVersion());
	}

}
