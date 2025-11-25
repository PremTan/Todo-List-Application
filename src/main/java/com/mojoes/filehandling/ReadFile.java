package com.mojoes.filehandling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ReadFile {
    public static void main(String[] args) {
        File file = new File("todo.txt");

        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            System.out.println("File content : ");
            while ((line = reader.readLine()) != null){
                System.out.println(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
