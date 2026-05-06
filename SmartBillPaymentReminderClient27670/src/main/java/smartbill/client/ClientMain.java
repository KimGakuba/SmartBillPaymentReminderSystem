package smartbill.client;

import smartbill.client.view.LoginFrame;

public class ClientMain {

    public static void main(String[] args) {
        // Launch the Login screen
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }

}