package com.mojoes.filehandling;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class UserSerializable {
    public static void main(String[] args) {
        User user = new User("Prem", 22, 8652124596L);

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.dat"))){
            oos.writeObject(user);
            System.out.println("User object serialized..");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
