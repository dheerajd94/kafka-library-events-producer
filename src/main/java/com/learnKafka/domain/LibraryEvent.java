package com.learnKafka.domain;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
public record LibraryEvent(
        Integer libraryEventId,
        LibraryEventType libraryEventType,
        @NotNull
        @Valid
        Book book)
{
        public LibraryEvent withLibraryEventType(LibraryEventType libraryEventType){
                return new LibraryEvent(this.libraryEventId, libraryEventType, this.book);
        }
}
