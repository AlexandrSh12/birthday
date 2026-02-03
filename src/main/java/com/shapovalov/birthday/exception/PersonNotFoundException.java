package com.shapovalov.birthday.exception;

public class PersonNotFoundException extends RuntimeException{
    public PersonNotFoundException (String message){
        super(message);
    }
}
