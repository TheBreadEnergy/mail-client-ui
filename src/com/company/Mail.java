package com.company;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.FlagTerm;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;

public class Mail {
    static final String ENCODING = "UTF-8";

    public static void sendSimpleMessage(String login, String password, String from, String to, String content, String subject)
            throws MessagingException, UnsupportedEncodingException {
        Authenticator auth = new MyAuthenticator(login, password);

        Properties props = System.getProperties();
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.mime.charset", ENCODING);
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getDefaultInstance(props, auth);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setText(content);
        Transport.send(msg);
    }

    public static void sendMultiMessage(String login, String password, String from, String to, String content, String subject, String attachment) throws MessagingException, UnsupportedEncodingException {
        Properties props = System.getProperties();
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.mime.charset", ENCODING);
        props.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new MyAuthenticator(login, password);
        Session session = Session.getDefaultInstance(props, auth);

        MimeMessage msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject, ENCODING);

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(content, "text/plain; charset=" + ENCODING + "");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(attachment);
        attachmentBodyPart.setDataHandler(new DataHandler(source));
        attachmentBodyPart.setFileName(MimeUtility.encodeText(source.getName()));
        multipart.addBodyPart(attachmentBodyPart);

        msg.setContent(multipart);

        Transport.send(msg);
    }

    public static void receiveMessage(String user, String password) throws MessagingException, IOException {
        Authenticator auth = new MyAuthenticator(user, password);

        Properties props = System.getProperties();
        props.put("mail.user", user);

        props.put("mail.pop3.port", "995");
        props.put("mail.pop3.host", "pop.gmail.com");
        props.setProperty("mail.pop3.ssl.enable", "true");
        props.put("mail.debug", "false");
        props.put("mail.store.protocol", "pop3");
        props.put("mail.transport.protocol", "smtp");


        Session session = Session.getDefaultInstance(props, auth);
        Store store = session.getStore();
        store.connect();
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        ArrayList<String> attachments = new ArrayList<String>();

        LinkedList<MessageBean> listMessages = getPart(messages, attachments);


        inbox.setFlags(messages, new Flags(Flags.Flag.SEEN), true);
        inbox.close(false);
        store.close();
    }

    private static LinkedList<MessageBean> getPart(Message[] messages, ArrayList<String> attachments) throws MessagingException, IOException {
        LinkedList<MessageBean> listMessages = new LinkedList<MessageBean>();
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        for (Message inMessage : messages) {

            //Запись сообщения в файл
                    try(FileWriter writer = new FileWriter("note.txt", true))
        {
            writer.append("Отправитель:");
            writer.append('\n');
            writer.write(inMessage.getFrom()[0].toString());
            writer.append('\n');
            writer.append("Тема:");
            writer.append('\n');
            writer.write(inMessage.getSubject());
            writer.append('\n');
            writer.append("Сообщение:");
            writer.append('\n');
            writer.write(inMessage.getContent().toString());
            writer.append('\n');
            writer.append("Дата:");
            writer.append('\n');
            writer.write(inMessage.getSentDate().toString());
            writer.append('\n');
            writer.append("_______________________________________________________________________________________________________________________________________");
            writer.append('\n');
            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }


            attachments.clear();
            if (inMessage.isMimeType("text/plain")) {
                MessageBean message = new MessageBean(inMessage.getMessageNumber(), MimeUtility.decodeText(inMessage.getSubject()), inMessage.getFrom()[0].toString(), null, f.format(inMessage.getSentDate()), (String) inMessage.getContent(), false, null);
                listMessages.add(message);
            } else if (inMessage.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) inMessage.getContent();
                MessageBean message = null;
                for (int i = 0; i < mp.getCount(); i++) {
                    Part part = mp.getBodyPart(i);
                    if ((part.getFileName() == null || part.getFileName() == "") && part.isMimeType("text/plain")) {
                        message = new MessageBean(inMessage.getMessageNumber(), inMessage.getSubject(), inMessage.getFrom()[0].toString(), null, f.format(inMessage.getSentDate()), (String) part.getContent(), false, null);
                    } else if (part.getFileName() != null || part.getFileName() != ""){
                        if ((part.getDisposition() != null) && (part.getDisposition().equals(Part.ATTACHMENT))) {
                            attachments.add(saveFile(MimeUtility.decodeText(part.getFileName()), part.getInputStream()));
                            if (message != null) message.setAttachments(attachments);
                        }
                    }
                }
                listMessages.add(message);
            }
        }
        return listMessages;
    }

    private static String saveFile(String filename, InputStream input) {
        String path = "attachments\\"+filename;
        try {
            byte[] attachment = new byte[input.available()];
            input.read(attachment);
            File file = new File(path);
            FileOutputStream out = new FileOutputStream(file);
            out.write(attachment);
            input.close();
            out.close();
            return path;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}


class MyAuthenticator extends Authenticator {
    private String user;
    private String password;

    MyAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        String user = this.user;
        String password = this.password;
        return new PasswordAuthentication(user, password);
    }
}
