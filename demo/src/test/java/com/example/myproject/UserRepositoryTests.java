package com.example.myproject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.myproject.domain.User;
import com.example.myproject.domain.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTests {

	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void test() {
		User user = new User();
		user.setEmail("123@163.com");
		user.setPassWord("123456");
		user.setUserName("һ��С����");
		user.setAddress("��������·");
		user.setAge("12");
		userRepository.save(user);
		//User user = userRepository.findByUserName("һ��С����");
		System.out.println(user.getPassWord());
	}

}
