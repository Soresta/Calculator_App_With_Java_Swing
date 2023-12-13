import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Drawing extends JPanel {
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(new Color(0xFFFACD));
        g.setFont(new Font("Raleway", Font.BOLD, 13));
        g.drawString("Hex:" + Integer.toHexString((int) HesapMakinasi.num2), 10, 20);
        g.drawString("Dec:" + HesapMakinasi.num2, 10, 45);
        g.drawString("Oct:" + Integer.toOctalString((int) HesapMakinasi.num2), 10, 70);
        g.drawString("Bin:" + Integer.toBinaryString((int) HesapMakinasi.num2), 10, 95);
    }
}

public class HesapMakinasi extends JFrame implements ActionListener, Runnable {
    // Değişkenler
    String operator = "";
    String currentNumber = "";
    static double num1, num2; // Değiştirilen tamsayıları ondalıklı sayılara çevirdik.
    boolean calculated = false;

    // Frame'de kullanılacak bileşenler
    JPanel panelTop = new JPanel();
    JPanel panelBottom = new JPanel();
    JPanel panelLeft = new Drawing();
    JLabel label1 = new JLabel(" ");
    JLabel label2 = new JLabel("0");

    HesapMakinasi() {
        setLayout(new BorderLayout());
        // panelTop ayarları:
        panelTop.setLayout(new BorderLayout());
        panelTop.setBackground(new Color(0x202020));

        // panelBottom ayarları:
        panelBottom.setLayout(new GridLayout(5, 4, 7, 7));
        panelBottom.setBorder(BorderFactory.createEmptyBorder(15, 5, 5, 5));
        panelBottom.setBackground(new Color(0x202020));

        // label1 ayarları:
        label1.setHorizontalAlignment(JLabel.TRAILING);
        label1.setFont(new Font("Cambria", Font.PLAIN, 20));
        label1.setForeground(new Color(0xFFFACD));

        // panelLeft Ayarları:
        panelLeft.setPreferredSize(new Dimension(150, 100));
        panelLeft.setBackground(new Color(0x202020));

        // label2 ayarları:
        label2.setHorizontalAlignment(JLabel.TRAILING);
        label2.setFont(new Font("Cambria", Font.PLAIN, 50));
        label2.setForeground(new Color(0xFFFACD));

        // buttons
        JButton[] buttons = new JButton[20];
        buttons[0] = new JButton("%");
        buttons[1] = new JButton("C");
        buttons[2] = new JButton("Sil");
        buttons[3] = new JButton("÷");
        buttons[4] = new JButton("7");
        buttons[5] = new JButton("8");
        buttons[6] = new JButton("9");
        buttons[7] = new JButton("x");
        buttons[8] = new JButton("4");
        buttons[9] = new JButton("5");
        buttons[10] = new JButton("6");
        buttons[11] = new JButton("-");
        buttons[12] = new JButton("1");
        buttons[13] = new JButton("2");
        buttons[14] = new JButton("3");
        buttons[15] = new JButton("+");
        buttons[16] = new JButton("+/-");
        buttons[17] = new JButton("0");
        buttons[18] = new JButton(".");
        buttons[19] = new JButton("=");

        // buttons ayarları (arka plan rengi, Font, Dokunabilirlik, Ekleme)
        for (int i = 0; i < 20; i++) {
            buttons[i].setBackground(new Color(0x484848));
            if ((i >= 0 && i < 4) || ((i + 1) % 4 == 0 && i != 0)) {
                buttons[i].setBackground(new Color(0x904848));
            }
            buttons[i].setFont(new Font("Cambria", Font.PLAIN, 22));
            buttons[i].addActionListener(this);
            buttons[i].setForeground(Color.white);
            buttons[i].setFocusable(false);
            panelBottom.add(buttons[i]);
        }

        // Panellere ekleme işlemleri
        panelTop.add(panelLeft, BorderLayout.WEST);
        panelTop.add(label1, BorderLayout.CENTER);
        panelTop.add(label2, BorderLayout.SOUTH);
        getContentPane().add(panelTop, BorderLayout.NORTH);
        getContentPane().add(panelBottom, BorderLayout.CENTER);

        // Ana panel ayarları
        setTitle("Hesap Makinesi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(340, 540);
        setLocationRelativeTo(null);
        setVisible(true);

        // Thread başlatılması
        new Thread(HesapMakinasi.this).start();
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        double sonGirilen = 0; // Son girilen değeri ondalıklı yapmak için double kullanıyoruz.
        switch (command) {
            case "+":
            case "-":
            case "x":
            case "÷":
            case "%":
                // İşlemler girildiğinde yapılacaklar:
                if (!label2.getText().equals("0")) {
                    if (!operator.isEmpty()) {
                        double num2 = Double.parseDouble(label2.getText()); // Double olarak çeviriyoruz.
                        double result = calculateResult(num1, num2, operator);
                        label2.setText(String.valueOf(result));
                        num1 = result;
                    } else {
                        num1 = Double.parseDouble(label2.getText()); // Double olarak çeviriyoruz.
                    }
                    operator = command;
                    label1.setText(num1 + operator);
                    calculated = false;
                } else if (label2.getText().equals("0") && (command.equals("+") || command.equals("-") || command.equals("x") || command.equals("÷"))) {
                    operator = command;
                    label1.setText(num1 + operator);
                }
                label2.setText("0");
                break;

            case "=":
                if (operator.isEmpty()) return;
                double num2 = Double.parseDouble(label2.getText()); // Double olarak çeviriyoruz.
                double result = calculateResult(num1, num2, operator);
                label1.setText(num1 + operator + num2 + "=");
                label2.setText(String.valueOf(result));
                operator = "";
                calculated = true;
                break;

            case "+/-":
                currentNumber = label2.getText();
                if (!currentNumber.equals("0")) {
                    double number = Double.parseDouble(currentNumber); // Double olarak çeviriyoruz.
                    number = -number;
                    label2.setText(String.valueOf(number));
                }
                break;

            case ".":
                if (!label2.getText().contains(".")) {
                    label2.setText(label2.getText() + ".");
                }
                break;

            case "C":
                label1.setText("");
                label2.setText("0");
                operator = "";
                break;

            case "Sil":
                currentNumber = label2.getText();
                if (currentNumber.length() == 2 && currentNumber.startsWith("-")) {
                    label2.setText("0");
                    currentNumber = "0";
                } else if (currentNumber.length() > 1) {
                    currentNumber = currentNumber.substring(0, currentNumber.length() - 1);
                } else {
                    currentNumber = "0";
                }
                label2.setText(currentNumber);
                break;

            default:
                if (calculated) {
                    label2.setText("0");
                    calculated = false;
                }
                if (label2.getText().equals("0")) {
                    label2.setText(command);
                } else {
                    label2.setText(label2.getText() + command);
                }
                break;
        }
    }

    private double calculateResult(double num1, double num2, String operator) {
        double result = 0;
        switch (operator) {
            case "+":
                result = num1 + num2;
                break;
            case "-":
                result = num1 - num2;
                break;
            case "x":
                result = num1 * num2;
                break;
            case "÷":
                if (num2 != 0) {
                    result = num1 / num2;
                }
                break;
            case "%":
                result = num1 % num2;
                break;
        }
        return result;
    }

    @Override
    public void run() {
        while (true) {

            if (label2.getText().startsWith("-") && label2.getText().length() > 1)
                num2 = Double.valueOf(label2.getText().substring(1, label2.getText().length()));
            else
                num2 = Double.valueOf(label2.getText());

            repaint();
        }
    }

    public static void main(String[] args) {
        HesapMakinasi makine = new HesapMakinasi();
    }
}
