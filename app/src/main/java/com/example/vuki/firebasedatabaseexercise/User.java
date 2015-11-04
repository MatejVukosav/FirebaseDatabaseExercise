package com.example.vuki.firebasedatabaseexercise;

import java.io.Serializable;

/**
 * Created by Vuki on 3.11.2015..
 */
public class User implements Serializable{

    public User(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    String name;
    String surname;
    String email;

}
