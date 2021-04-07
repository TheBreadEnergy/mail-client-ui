package com.company;
import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Draw extends  JFrame{

    private static class Account {
        private String login;
        private String password;
        private File file;

        public Account() {
            login = "";
            password = "";
        }
        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }

    private static JMenuBar menuBar = new JMenuBar();

    private static JMenu menu = new JMenu("Файл");

    public Draw() {

        Account user = new Account();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();

        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        Panel navigationPanel = new Panel();
        Panel contentPanel = new Panel();
        Panel subNavigatePanel = new Panel();
        Panel headerPanel = new Panel();
        Panel mainPanel = new Panel();

        Button inboxBtn = new Button("Входящие сообщения");
        Button postBtn = new Button("Написать сообщение");
        Button accountBtn = new Button("Аккаунт");

        Label headerLabel = new Label();

        navigationPanel.setBackground(Color.decode("#1B272F"));
        navigationPanel.setLayout(new BorderLayout());

        subNavigatePanel.setLayout(new BoxLayout(subNavigatePanel, BoxLayout.PAGE_AXIS));
        subNavigatePanel.add(inboxBtn);
        subNavigatePanel.add(postBtn);

        navigationPanel.add(subNavigatePanel, BorderLayout.NORTH);
        navigationPanel.add(accountBtn, BorderLayout.SOUTH);

        headerPanel.setBackground(Color.decode("#5F9EA0"));
        headerPanel.add(headerLabel);

        mainPanel.setBackground(Color.decode("#F4F3F2"));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(mainPanel, BorderLayout.CENTER);

        add(navigationPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        setTitle("Почтовый Клиент");
        setBounds(dimension.width / 2 - 400, dimension.height / 2 - 300, 800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        inboxBtn.addActionListener(e -> {
            headerLabel.setText("Входящие сообщения Загрузка сообщений...");
            mainPanel.removeAll();
            revalidate();

            if (user.getLogin().isEmpty() || user.getPassword().isEmpty()) {
                JOptionPane.showMessageDialog(new Frame(), "Вы не вошли в аккаунт", "Ошибка", JOptionPane.ERROR_MESSAGE);
            } else {
                Mail mail = new Mail();
                try {
                    mail.receiveMessage(user.getLogin(), user.getPassword());

                    ArrayList<String> list = new ArrayList<String>();

                    File file = new File("note.txt");
                    FileReader fr = new FileReader(file);
                    BufferedReader reader = new BufferedReader(fr);
                    String line = reader.readLine();

                    while (line != null) {
                        line = line.trim();
                        System.out.println(line);
                        list.add(line);
                        line = reader.readLine();
                    }

                    final JList<String> listArea = new JList<String>(list.toArray(new String[list.size()]));
                    listArea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    listArea.setFont(new Font("Serif", Font.PLAIN, 15));
                    JScrollPane listScroller = new JScrollPane();
                    listScroller.setViewportView(listArea);
                    listArea.setLayoutOrientation(JList.VERTICAL);
                    headerLabel.setText("Входящие сообщения");
                    mainPanel.add(listScroller);
                    revalidate();

                } catch (MessagingException | IOException messagingException) {
                    messagingException.printStackTrace();
                }
            }
        });

        postBtn.addActionListener(e -> {
            headerLabel.setText("Написать сообщение");

            mainPanel.removeAll();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

            Panel subMainPanel = new Panel();
            Panel clearPanel = new Panel();
            Panel btnPanel = new Panel();

            Label toLabel = new Label("Получатель");
            Label subjectLabel = new Label("Тема");
            Label bodyLabel = new Label("Сообщение");

            TextField toTextField = new TextField(50);
            TextField subjectTextField = new TextField(50);
            TextArea bodyTextArea = new TextArea();

            Button sendMailBtn = new Button("Отправить письмо");
            Button choiceBtn = new Button("Выбрать файл");

            clearPanel.setBackground(Color.decode("#F4F3F2"));

            subMainPanel.setLayout(new BoxLayout(subMainPanel, BoxLayout.PAGE_AXIS));
            subMainPanel.add(toLabel);
            subMainPanel.add(toTextField);
            subMainPanel.add(subjectLabel);
            subMainPanel.add(subjectTextField);
            subMainPanel.add(bodyLabel);
            subMainPanel.add(bodyTextArea);

            btnPanel.setLayout(new GridLayout(1,2));
            btnPanel.add(choiceBtn);
            btnPanel.add(sendMailBtn);

            subMainPanel.add(btnPanel);

            mainPanel.add(subMainPanel);

            revalidate();

            sendMailBtn.addActionListener(e12 -> {
                if (user.getLogin().isEmpty()) {
                    JOptionPane.showMessageDialog(new Frame(), "Не введены данные авторизации", "Ошибка", JOptionPane.ERROR_MESSAGE);
                } else if (user.getFile() != null){
                    Mail mail = new Mail();
                    try {
                        mail.sendMultiMessage(user.getLogin(), user.getPassword(), user.getLogin(), toTextField.getText(), bodyTextArea.getText(), subjectTextField.getText(), user.getFile().getPath());
                        JOptionPane.showMessageDialog(new Frame(), "Сообщение отправлено");
                    } catch (MessagingException | UnsupportedEncodingException messagingException) {
                        messagingException.printStackTrace();
                        JOptionPane.showMessageDialog(new Frame(), "Ошибка отправки", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                    user.setFile(null);
                }else{
                    Mail mail = new Mail();
                    try {
                        mail.sendSimpleMessage(user.getLogin(), user.getPassword(), user.getLogin(), toTextField.getText(), bodyTextArea.getText(), subjectTextField.getText());
                        JOptionPane.showMessageDialog(new Frame(), "Сообщение отправлено");
                    } catch (MessagingException | UnsupportedEncodingException messagingException) {
                        messagingException.printStackTrace();
                        JOptionPane.showMessageDialog(new Frame(), "Ошибка отправки", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            choiceBtn.addActionListener(e13 -> {
                JFileChooser fileChooser = new JFileChooser();
                int ret = fileChooser.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    user.setFile(fileChooser.getSelectedFile());
                }
            });
        });

        accountBtn.addActionListener(e -> {
            headerLabel.setText("Аккаунт");
            mainPanel.removeAll();
            Panel subMainPanel = new Panel();
            Panel clearPanel = new Panel();

            Label loginLabel = new Label("Логин");
            Label passwordLabel = new Label("Пароль");

            TextField loginTextField = new TextField(50);
            TextField passwordTextField = new TextField(50);

            passwordTextField.setEchoChar('*');

            Button confirmBtn = new Button("Сохранить");


            mainPanel.setLayout(new GridLayout(3, 3));

            clearPanel.setPreferredSize(new Dimension(100, 100));
            clearPanel.setBackground(Color.decode("#F4F3F2"));

            subMainPanel.setLayout(new BoxLayout(subMainPanel, BoxLayout.PAGE_AXIS));
            subMainPanel.add(loginLabel);
            subMainPanel.add(loginTextField);
            subMainPanel.add(passwordLabel);
            subMainPanel.add(passwordTextField);
            subMainPanel.add(confirmBtn);

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i == 2 && j == 2) {
                        mainPanel.add(subMainPanel);
                    } else {
                        mainPanel.add(clearPanel);
                    }
                }
            }

            revalidate();

            confirmBtn.addActionListener(e1 -> {

                if (loginTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(new Frame(), "Заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(new Frame(), "Данные сохранены");
                    user.setLogin(loginTextField.getText());
                    user.setPassword(passwordTextField.getText());
                }
            });

        });
    }
}
