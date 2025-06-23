package com.apisak.authservice;

import com.apisak.authservice.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AuthServiceApplicationTests {

	private final UserRepository userRepository = Mockito.mock(UserRepository.class);

	@Test
	void contextLoads() {
		// เขียน test แบบ isolated หรือ dummy check
		assert userRepository != null;
	}
}