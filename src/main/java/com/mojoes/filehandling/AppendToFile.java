package com.mojoes.filehandling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class AppendToFile {
    public static void main(String[] args) {
        File file = new File("todo.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))){
            writer.write("Practice File handling completed..");
            writer.newLine();
            System.out.println("Append to existing file successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
