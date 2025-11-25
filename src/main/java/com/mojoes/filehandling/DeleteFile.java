package com.mojoes.filehandling;

import java.io.File;

public class DeleteFile {
    public static void main(String[] args) {
        File file = new File("notes.txt");

        if(file.exists()){
            file.delete();
            System.out.println("File deleted successfully");
        }else{
            System.out.println("File not exists..");
        }
    }
}
