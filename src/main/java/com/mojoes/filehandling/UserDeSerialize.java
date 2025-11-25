package com.mojoes.filehandling;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class UserDeSerialize {
    public static void main(String[] args) {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("user.dat"))){
            User user = (User) ois.readObject();
            System.out.println("User deserialized : "+user);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
