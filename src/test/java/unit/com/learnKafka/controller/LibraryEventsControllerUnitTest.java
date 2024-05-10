package com.learnKafka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnKafka.domain.LibraryEvent;
import com.learnKafka.producer.LibraryEventsProducer;
import com.learnKafka.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.isA;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Test Slice


@WebMvcTest(LibraryEventsController.class)
class LibraryEventsControllerUnitTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LibraryEventsProducer libraryEventsProducer;

    @Test
    void postLibraryEvent_invalidValues() throws Exception {
        //given
        var json = objectMapper.writeValueAsString(TestUtil.libraryEventRecordWithInvalidBook());
        Mockito.when(libraryEventsProducer.sendLibraryEvent_approach3(isA(LibraryEvent.class)))
                .thenReturn(null);
        var expectedErrorMessage = "book.bookId - must not be null, book.bookName - must not be blank";
        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));






        //then
    }
}