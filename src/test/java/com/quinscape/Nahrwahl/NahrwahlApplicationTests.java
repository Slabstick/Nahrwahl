package com.quinscape.Nahrwahl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
		"spring.data.mongodb.uri=mongodb://localhost:27017/test"
})

@SpringBootTest
class NahrwahlApplicationTests {

	@Test
	void contextLoads() {
	}

}
