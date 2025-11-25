package com.mojoes.filehandling;

// Create file and write into file

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CreateAndWrite {
    public static void main(String[] args) {

        File file = new File("todo.txt");

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            writer.write("Learn java and springboot");
            writer.newLine();
            writer.write("Done.");
            writer.newLine();
            writer.write("Complete Spring Boot Project");
            writer.newLine();
            System.out.println("File created and written successfully!");
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
