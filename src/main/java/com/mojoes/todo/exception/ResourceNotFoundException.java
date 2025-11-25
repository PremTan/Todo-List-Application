package com.mojoes.todo.exception;

public class ResourceNotFoundException extends  RuntimeException{

    public ResourceNotFoundException(String msg){
        super(msg);
    }
}
