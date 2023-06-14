package com.myaushka.whatwishbot;

import lombok.Getter;
import org.springframework.data.annotation.Id;

@Getter
public class Greeting {
    private final long id;
    private final String content;

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }
}
