package com.example.danguen;

import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class ChatTest extends BaseTest{



    @Test
    public void 메세지_송신() throws Exception{
        mockMvc.perform(get("/mes"));
    }
}
