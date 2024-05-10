package com.learnKafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnKafka.domain.LibraryEvent;
import com.learnKafka.domain.LibraryEventType;
import com.learnKafka.producer.LibraryEventsProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventsController {
    private final LibraryEventsProducer libraryEventsProducer;

    public LibraryEventsController(LibraryEventsProducer libraryEventsProducer) {
        this.libraryEventsProducer = libraryEventsProducer;
    }

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(
            @RequestBody @Valid LibraryEvent libraryEvent
    ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("libraryEvent......... : {} ",libraryEvent);
        LibraryEvent libraryEvent1 = libraryEvent.withLibraryEventType(LibraryEventType.NEW);
        //invoke the kafka producer
        //...............preferred Asynchronous..........................
         libraryEventsProducer.sendLibraryEvent(libraryEvent);
//        LibraryEvent libraryEvent1 = new LibraryEvent(libraryEvent.libraryEventId(),
//                LibraryEventType.NEW,
//                libraryEvent.book());

        //----------------Synchronous....................................
         //libraryEventsProducer.sendLibraryEvent_approach2(libraryEvent1);

        //...............Asynchronous.......................... ........
        //libraryEventsProducer.sendLibraryEvent_approach3(libraryEvent); //Asynchronous

        log.info("After Sending libraryEvent");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent1);
    }

    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> updateLibraryEvent(
            @RequestBody @Valid LibraryEvent libraryEvent
    ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("libraryEvent......... : {} ",libraryEvent);
        LibraryEvent libraryEvent1 = libraryEvent.withLibraryEventType(LibraryEventType.UPDATE);
        ResponseEntity<String> BAD_REQUEST = getStringResponseEntity(libraryEvent1);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        libraryEventsProducer.sendLibraryEvent_approach2(libraryEvent1);


        log.info("After Sending libraryEvent");
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent1);
    }

    private static ResponseEntity<String> getStringResponseEntity(LibraryEvent libraryEvent) {
        if (libraryEvent.libraryEventId()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("please pass the LibraryEventId");
        }

        if (!libraryEvent.libraryEventType().equals(LibraryEventType.UPDATE)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only UPDATE event type is supported");
        }
        return null;
    }
}
