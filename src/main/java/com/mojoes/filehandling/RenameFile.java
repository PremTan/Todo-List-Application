package com.mojoes.filehandling;

import java.io.File;

public class RenameFile {
    public static void main(String[] args) {
        File file = new File("notes.txt");
        File newFile = new File("my_notes.txt");

        if(file.exists()){
            file.renameTo(newFile);
            System.out.println("File renamed successfully..");
        }else{
            System.out.println("Rename failed.. File not exists..");
        }
    }
}
