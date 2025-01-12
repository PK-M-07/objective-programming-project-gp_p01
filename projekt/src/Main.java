import Interface.CMainForm;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Weather App");

        // Ustawienie zawartości okna na główny panel z klasy interface
        // instancja klasy CMainForm
        CMainForm form = new CMainForm();
        frame.setContentPane(new CMainForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(360, 640);
        frame.setVisible(true);
        frame.pack();  // Automatyczne dopasowanie okna do rozmiaru komponentów

        // obsluga zamkniecia okna
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                form.stopAutoUpdate();
            }
        });
    }
}
