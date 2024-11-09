package com.example;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import games.Wordle;

public class Client extends JFrame {
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private JTextArea chatArea;
    private JTextField messageField;
    private String username, pwd;
    private Map<String, String> clientCredentials = new HashMap<>();    
    private final JMenu lobby = new JMenu("Lobby");
    private static List<String> connectedUsers = new ArrayList<>();
    //private static Map<String, String> clientCredentials = new HashMap<String, String>();


    // Panels for different screens
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public Client(String hostname, int port) {
        // Apply Nimbus Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Simulate existing users for testing
        clientCredentials.put("user1", "pwd1");
        clientCredentials.put("user2", "pwd2");
        clientCredentials.put("user3", "pwd3");


        // Set up CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        // Initialize screens
        initializeScreen1();
        initializeScreen2Login();
        initializeScreen2Register();
        initializeScreen3Lobby();
        initializeScreen4Invite();
        initializeScreen5Game();

        // Set default configurations
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Connect to server
        connectToServer(hostname, port);

        // Show the first screen
        cardLayout.show(mainPanel, "screen1");
    }

    private void initializeScreen1() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton loginButton = new JButton("Log In");
        loginButton.setBackground(new Color(100, 149, 237));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.addActionListener(e -> cardLayout.show(mainPanel, "screen2Login"));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(loginButton, gbc);

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "screen2Register"));
        gbc.gridy = 1;
        panel.add(registerButton, gbc);

        mainPanel.add(panel, "screen1");
    }

    private void initializeScreen2Login() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Usuari:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(userLabel, gbc);

        JTextField userField = new JTextField(15);
        userField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(userField, gbc);

        JLabel passLabel = new JLabel("Contrasenya:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(passField, gbc);

        JButton loginButton = new JButton("Iniciar Sessió");
        loginButton.setBackground(new Color(100, 149, 237));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String enteredUsername = userField.getText();
            String password = new String(passField.getPassword());

            if (clientCredentials.getOrDefault(username, "").equals(password)) { //TODO: COMPROBAR CONTRA HASH
                username = enteredUsername; // Assign the entered username
                setTitle("Chat "+username);
                sendMessage("#NEWUSER:"+username);
                cardLayout.show(mainPanel, "screen3Lobby");
            } else {
                JOptionPane.showMessageDialog(this, "Usuari o contrasenya incorrecta.", "Error d'inici de sessió", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        mainPanel.add(panel, "screen2Login");
    }

    private void initializeScreen2Register() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel userLabel = new JLabel("Usuari:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(userLabel, gbc);

        JTextField userField = new JTextField(15);
        userField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(userField, gbc);

        JLabel passLabel = new JLabel("Contrasenya:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(passField, gbc);

        JLabel confirmPassLabel = new JLabel("Repetir Contrasenya:");
        confirmPassLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(confirmPassLabel, gbc);

        JPasswordField confirmPassField = new JPasswordField(15);
        confirmPassField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(confirmPassField, gbc);

        JButton registerButton = new JButton("Registrar-se");
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(registerButton, gbc);

        registerButton.addActionListener(e -> {
            String enteredUsername = userField.getText().trim();  // Eliminar espais en blanc
            String password = new String(passField.getPassword()).trim();
            String confirmPassword = new String(confirmPassField.getPassword()).trim();
        
            // Comprovació de camps buits
            if (enteredUsername.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tots els camps són obligatoris.", "Error de registre", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            // Comprovació si l'usuari ja existeix
            if (clientCredentials.containsKey(username)) {
                JOptionPane.showMessageDialog(this, "Aquest usuari ja existeix.", "Error de registre", JOptionPane.ERROR_MESSAGE);
            } else if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Les contrasenyes no coincideixen.", "Error de registre", JOptionPane.ERROR_MESSAGE);
            } else {
                clientCredentials.put(enteredUsername, password);
                username = enteredUsername; // Assignar el nom d'usuari en registrar-se
                JOptionPane.showMessageDialog(this, "Registre complet!");
                setTitle("Chat "+username);
                sendMessage("#NEWUSER:"+username);
                cardLayout.show(mainPanel, "screen3Lobby");
            }
        });

        mainPanel.add(panel, "screen2Register");
    }

    private void initializeScreen3Lobby() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Sidebar for chat list with title "XATS"
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(150, getHeight()));
        sidebar.setBackground(new Color(200, 200, 200));
        JLabel chatsLabel = new JLabel("XATS");
        chatsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        chatsLabel.setForeground(new Color(50, 50, 50));
        chatsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); 
        chatsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(chatsLabel);

        for (String user : connectedUsers) {
            JLabel userLabel = new JLabel(user);
            userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            sidebar.add(userLabel);
        }
        
        
        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        
        // Message input field with "Escriu el teu missatge:"
        JPanel messagePanel = new JPanel(new BorderLayout());
        messageField = new JTextField("Escriu el teu missatge:");
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        messageField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                messageField.setText("");
            }
        });

        messageField.addActionListener(e -> {
            String message = messageField.getText();
            sendMessage(message);
            messageField.setText("");  
        });

        JButton sendButton = new JButton("Enviar");
        sendButton.setBackground(new Color(60, 179, 113));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            sendMessage(message);
            messageField.setText("");
        });

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        JButton playButton = new JButton("Jugar");
        playButton.setBackground(new Color(255, 165, 0));
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        playButton.setFont(new Font("Arial", Font.BOLD, 14));
        playButton.addActionListener(e -> cardLayout.show(mainPanel, "screen4Invite"));


        // Menu to open lobby
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(lobby);
        JMenuItem lobbyItem = new JMenuItem("");
        lobby.add(lobbyItem);
        for(String user : connectedUsers){
            lobby.add(new JMenuItem(user));
        }

        lobby.setBackground(new Color(255, 165, 0));
        lobby.setForeground(Color.BLACK);
        lobby.setFocusPainted(false);
        lobby.setFont(new Font("Arial", Font.BOLD, 14));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(menuBar, BorderLayout.WEST);
        topPanel.add(playButton, BorderLayout.EAST);

        panel.add(sidebar, BorderLayout.WEST);
        panel.add(chatScrollPane, BorderLayout.CENTER);
        panel.add(messagePanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);

        mainPanel.add(panel, "screen3Lobby");

    }

    private void initializeScreen4Invite() {
        JPanel panel = new JPanel(new GridLayout(connectedUsers.size() + 1, 1));
        List<JCheckBox> checkBoxes = new ArrayList<>();
        for (String user : connectedUsers) {
            JCheckBox checkBox = new JCheckBox(user);
            checkBoxes.add(checkBox);
            panel.add(checkBox);
        }

        JButton okButton = new JButton("Wordle");
        okButton.setBackground(new Color(0, 128, 128));
        okButton.setForeground(Color.WHITE);
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton cancelButton = new JButton("Cancel·lar");
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        okButton.addActionListener(e -> {
            List<String> selectedUsers = new ArrayList<>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    selectedUsers.add(checkBox.getText());
                }
            }
            // Show main chat screen and open new window for Wordle game
            cardLayout.show(mainPanel, "screen3Lobby");
            sendMessage("#GAME1 "+this.username);
        });
        
        cancelButton.addActionListener(e -> cardLayout.show(mainPanel, "screen3Lobby"));

        panel.add(okButton);
        panel.add(cancelButton);
        mainPanel.add(panel, "screen4Invite");
    }

    private void initializeScreen5Game() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField();
        JButton cancelButton = new JButton("Cancel·lar");

        cancelButton.addActionListener(e -> cardLayout.show(mainPanel, "screen3Lobby"));
        inputField.addActionListener(e -> {
            String input = inputField.getText();
            sendMessage(input);
            inputField.setText("");
        });

        panel.add(inputField, BorderLayout.CENTER);
        panel.add(cancelButton, BorderLayout.EAST);
        mainPanel.add(panel, "screen5Game");
    }

    private void connectToServer(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread thread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = input.readLine()) != null) {
                        if(serverMessage.contains("#NEWUSER:")){    //If new user, add it to the list and to the lobby tab
                            JMenuItem newUser = new JMenuItem(addUser(serverMessage));
                            lobby.add(newUser);
                        } else if(serverMessage.contains("#KEY")){
                            //key = extractKey(serverMessage);
                        } else if(serverMessage.contains("#NEWLIST")){      //If server says to create new list (client was erased or added)
                            lobby.removeAll();
                            connectedUsers = new ArrayList<String>();
                        } else if(serverMessage.contains("#REFRESH:")){
                            addUserToList(serverMessage);
                        } else if(serverMessage.contains("#DONEREFRESHING")){   //Stop refreshing means we can create the new lobby list
                            for(String user : connectedUsers){
                                lobby.add(new JMenuItem(user));
                            }
                        } else if(serverMessage.contains("#STARTWORDLE")){
                            int response = JOptionPane.showConfirmDialog(this,"Vols jugar al Wordle?","Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                            switch (response) {
                                case JOptionPane.YES_OPTION:
                                    JOptionPane.showMessageDialog(this, "Has acceptat! Gaudeix del joc.");
                                    sendMessage("#GAME1ACCEPT "+this.username);
                                    break;
                                case JOptionPane.NO_OPTION:
                                    JOptionPane.showMessageDialog(this, "Has Rebutjat!");
                                    sendMessage("#GAME1DECLINE "+this.username);
                                    break;
                                default:
                                    JOptionPane.showMessageDialog(this, "Has Rebutjat!");
                                    sendMessage("#GAME1DECLINE");
                                    break;
                            }
                        } else if(serverMessage.contains("#CONTINUEWORDLE")){
                            startWordle();
                        } else {
                            appendMessage(serverMessage, Color.BLACK, false);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();


            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void sendMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String timestamp = LocalDateTime.now().format(formatter);
            output.println("[" + timestamp + "] " + username + ": " + message);
        }
    }

    private void appendMessage(String message, Color color, boolean isUser) {
        // Afegeix el missatge al xat
        String formattedMessage = isUser ? "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + message : message; 
        chatArea.setForeground(color);
        chatArea.append(formattedMessage + "\n");
        chatArea.setForeground(Color.BLACK);
    }

    private String addUser(String message) {
        int separatorIndex = message.indexOf(':');      //Split the message through the ':'
        if (separatorIndex != -1) {
            String u = message.substring(separatorIndex + 1).trim();    //Contains username
            connectedUsers.add(u);
            return u;
        }
        return "";
    }

    private void addUserToList(String message) {
        int separatorIndex = message.indexOf(':');      //Split the message through the ':'
        if (separatorIndex != -1) {
            String u = message.substring(separatorIndex + 1).trim();    //Contains username
            if(!connectedUsers.contains(u)){
                connectedUsers.add(u);
            }
        }
    }

    private void startWordle(){
        SwingUtilities.invokeLater(() -> new Wordle());
    }

    

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public PrintWriter getOutput() {
		return output;
	}

	public void setOutput(PrintWriter output) {
		this.output = output;
	}

	public BufferedReader getInput() {
		return input;
	}

	public void setInput(BufferedReader input) {
		this.input = input;
	}

	public JTextArea getChatArea() {
		return chatArea;
	}

	public void setChatArea(JTextArea chatArea) {
		this.chatArea = chatArea;
	}

	public JTextField getMessageField() {
		return messageField;
	}

	public void setMessageField(JTextField messageField) {
		this.messageField = messageField;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return pwd;
	}

	public void setPassword(String password) {
		this.pwd = password;
	}

	public static List<String> getconnectedUsers() {
		return connectedUsers;
	}

	public static void setconnectedUsers(List<String> connectedUsers) {
		Client.connectedUsers = connectedUsers;
	}

	public JMenu getLobby() {
		return lobby;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(JPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public CardLayout getCardLayout() {
		return cardLayout;
	}

	public void setCardLayout(CardLayout cardLayout) {
		this.cardLayout = cardLayout;
	}

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 8888);      //Connect to port 8888 from localhost , change on lab computer IP 
        client.setVisible(true);    //Set gui visible
    }

}