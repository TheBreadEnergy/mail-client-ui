package com.company;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageBean implements Serializable {
    private String subject;
    private String from;
    private String to;
    private String dateSent;
    private String content;
    private boolean isNew;
    private int msgId;
    private ArrayList<String> attachments;

    public MessageBean(int msgId, String subject, String from, String to, String dateSent, String content, boolean isNew, ArrayList<String> attachments) {
        this.subject = subject;
        this.from = from;
        this.to = to;
        this.dateSent = dateSent;
        this.content = content;
        this.isNew = isNew;
        this.msgId = msgId;
        this.attachments = attachments;
        }

public String getSubject() {
        return subject;
        }

public void setSubject(String subject) {
        this.subject = subject;
        }

public String getFrom() {
        return from;
        }

public void setFrom(String from) {
        this.from = from;
        }

public String getDateSent() {
        return dateSent;
        }

public void setDateSent(String dateSent) {
        this.dateSent = dateSent;
        }

public String getContent() {
        return content;
        }

public void setContent(String content) {
        this.content = content;
        }

public String getTo() {
        return to;
        }

public void setTo(String to) {
        this.to = to;
        }

public boolean isNew() {
        return isNew;
        }

public void setNew(boolean aNew) {
        isNew = aNew;
        }

public int getMsgId() {
        return msgId;
        }

public void setMsgId(int msgId) {
        this.msgId = msgId;
        }

public ArrayList<String> getAttachments() {
        return attachments;
        }

public void setAttachments(ArrayList<String> attachments) {
        this.attachments = new ArrayList<String>(attachments);
        }
        }
