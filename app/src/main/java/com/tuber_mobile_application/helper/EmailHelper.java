package com.tuber_mobile_application.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailHelper extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    private String mEmail;
    private String mSubject;
    private String mMessage;

    //Constructor
    public EmailHelper(Context mContext, String mEmail, String mSubject, String mMessage) {
        this.mContext = mContext;
        this.mEmail = mEmail;
        this.mSubject = mSubject;
        this.mMessage = mMessage;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("a link was sent to your email address.")
                .setCancelable(true).show(); // success message
    }

    @Override
    protected Void doInBackground(Void... params) {

        //Creating properties
        Properties props = new Properties();

        //Configuring properties for g mail
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        //Authenticating the password
        Session mSession = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(App_Global_Variables.EMAIL, App_Global_Variables.PASSWORD);
                    }
                });

        try
        {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(mSession);

            //Setting sender address
            mm.setFrom(new InternetAddress(App_Global_Variables.EMAIL));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
            //Adding subject
            mm.setSubject(mSubject);
            //Adding message
            mm.setContent(mMessage,"text/html");
            //Sending email
            Transport.send(mm);


        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
