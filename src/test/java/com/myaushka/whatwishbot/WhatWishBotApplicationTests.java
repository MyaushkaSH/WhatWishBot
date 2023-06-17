package com.myaushka.whatwishbot;

import static org.assertj.core.api.Assertions.assertThat;
import com.myaushka.whatwishbot.telegram.WhatWishBotHeart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WhatWishBotApplicationTests {
	@Autowired
	private WhatWishBotHeart whatWishBotHeart;
	@Test
	void contextLoads() {
		assertThat(whatWishBotHeart).isNotNull();
	}

}
