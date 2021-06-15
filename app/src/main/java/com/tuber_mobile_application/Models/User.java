package com.tuber_mobile_application.Models;

public class User {

    private int id;
    private String fullNames;
    private String surname;
    private String valid_Phone_Number;
    private String email_Address;
    private String passWord;
    private String gender;
    private int image;
    private int age;
    private String user_Discriminator;
    private int isActive;

    public User(int id, String fullNames, String surname, String valid_Phone_Number, String email_Address, String passWord, String gender, int image, int age, String user_Discriminator, int isActive) {

        this.id = id;
        this.fullNames = fullNames;
        this.surname = surname;
        this.valid_Phone_Number = valid_Phone_Number;
        this.email_Address = email_Address;
        this.passWord = passWord;
        this.gender = gender;
        this.image = image;
        this.age = age;
        this.user_Discriminator = user_Discriminator;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullNames() {
        return fullNames;
    }

    public void setFullNames(String fullNames) {
        this.fullNames = fullNames;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getValid_Phone_Number() {
        return valid_Phone_Number;
    }

    public void setValid_Phone_Number(String valid_Phone_Number) {
        this.valid_Phone_Number = valid_Phone_Number;
    }

    public String getEmail_Address() {
        return email_Address;
    }

    public void setEmail_Address(String email_Address) {
        this.email_Address = email_Address;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUser_Discriminator() {
        return user_Discriminator;
    }

    public void setUser_Discriminator(String user_Discriminator) {
        this.user_Discriminator = user_Discriminator;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
}
