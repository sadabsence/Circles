package stepa.project.stepa_project;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;


public class MyFrame extends JFrame implements ActionListener {
    public final JButton button_ADD;
    public final JButton button_RESET;
    public final JButton button_SOLVE;

    public final JTextField textFIELD;
    public final JTextArea textFIELD2;

    public final JTextComponent textArea;

    Solution solve = new Solution(new ArrayList<>());
    List<stepa.project.stepa_project.Point> allPoints = new ArrayList<>();


    public MyFrame(String title) {
        super(title);

        textArea = new JTextArea("          Условие задачи :" + "\n    Среди всего множества окружностей " + "\n    найти пару окружностей," +
                " такую," + "\n    что длина их общей хорды будет максимальна");
        textArea.setBounds(1000, 10, 350, 70);

        button_SOLVE = new JButton("SOLVE");
        button_SOLVE.setBounds(1000, 250, 175, 50);
        button_SOLVE.addActionListener(this);

        button_RESET = new JButton("RESET");
        button_RESET.setBounds(1180, 250, 175, 50);
        button_RESET.addActionListener(this);

        button_ADD = new JButton("Добавить окружность");
        button_ADD.setBounds(1000, 180, 350, 50);
        button_ADD.addActionListener(this);

        textFIELD = new JTextField("Сюда вводить 4  координаты с точностью 1 знак после точки:   ");
        textFIELD.setBounds(1000, 100, 350, 50);

        textFIELD2 = new JTextArea("Последняя веденная точка :");
        textFIELD2.setBounds(1000, 400, 350, 200);

        add(textFIELD);
        add(textFIELD2);
        add(textArea);
        add(button_ADD);
        add(button_SOLVE);
        add(button_RESET);

        setLayout(null);

        setSize(1400, 900);
        getContentPane().setBackground(new Color(0, 128, 128));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addMouseListener(new MyMouseListener());
        setVisible(true);
    }


    public void drawCircle(Graphics g, Circle newCircle) {
        g.drawOval((int) (newCircle.point.x - newCircle.radius), (int) (newCircle.point.y - newCircle.radius), (int) newCircle.radius * 2, (int) newCircle.radius * 2);
    }

    @Override
    public void paint(Graphics g1) {
        super.paint(g1);

        Graphics2D g = (Graphics2D) g1;
        BasicStroke pen1 = new BasicStroke(5);
        g.setStroke(pen1);
        g.setColor(Color.WHITE);

        for (stepa.project.stepa_project.Point allPoint : allPoints) {
            g.drawOval((int) allPoint.x - 3, (int) allPoint.y - 3, 5, 5);
            g.fillOval((int) allPoint.x - 3, (int) allPoint.y - 3, 5, 5);
        }

        for (int i = 0; i < solve.circles.size(); i++) {
            Circle newCircle = solve.circles.get(i); // Запоминаем текущий круг
            g.setColor(Color.WHITE);// Меняем цвет на черный
            drawCircle(g, newCircle);// Рисуем его
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            textFIELD2.setText("Последняя введенная точка : \n    " + allPoints.get(allPoints.size() - 1));
        } catch (IndexOutOfBoundsException e1) {
            e1.printStackTrace();
        }

        if (e.getSource() == button_ADD) {
            try {
                String data = textFIELD.getText();
                data += " ";

                StringBuilder newDATA = new StringBuilder();

                for (int i = 0; i < data.length(); i++) {
                    if (data.charAt(i) != '(' && data.charAt(i) != ')') {
                        newDATA.append(data.charAt(i));
                    }
                }

                List<Double> coordinates = new ArrayList<>(4);
                StringBuilder coord = new StringBuilder();

                for (int i = 0; i < newDATA.length(); i++) {
                    if (!Character.isWhitespace(newDATA.charAt(i))) {
                        coord.append(newDATA.charAt(i));
                    } else {
                        if (coord.length() != 0) {
                            coordinates.add(Double.parseDouble(coord.toString()));
                            coord = new StringBuilder();
                        }
                    }
                }

                stepa.project.stepa_project.Point centre = new stepa.project.stepa_project.Point(coordinates.get(0), coordinates.get(1));
                stepa.project.stepa_project.Point pointOnCircle = new stepa.project.stepa_project.Point(coordinates.get(2), coordinates.get(3));

                allPoints.add(centre);
                allPoints.add(pointOnCircle);

                solve.circles.add(new Circle(centre, pointOnCircle));
                coordinates.clear();

                repaint();
            } catch (NumberFormatException err) {
                System.out.println("Вы неправильно ввели координаты :" + textFIELD.getText());
            }
        }

        if (e.getSource() == button_RESET) {
            getContentPane().repaint();
            allPoints.clear();
            solve.circles.clear();
        }

        if (e.getSource() == button_SOLVE) {
            showSolution(getGraphics());
            textFIELD2.setText("        Задача решена!\n"
                                + " Нужная пара окружностей :\n"
                                + "    Первая окружность :\n   " + solve.c1 + "\n" + "\n"
                                + "    Вторая окружность :\n        " + solve.c2);
        }
    }

    private void showSolution(Graphics g1) {
        super.paint(g1);

        Graphics2D g = (Graphics2D) g1;
        BasicStroke pen1 = new BasicStroke(5);
        g.setStroke(pen1);
        g.setColor(Color.WHITE);

        for (stepa.project.stepa_project.Point allPoint : allPoints) {
            g.drawOval((int) allPoint.x - 3, (int) allPoint.y - 3, 5, 5);
            g.fillOval((int) allPoint.x - 3, (int) allPoint.y - 3, 5, 5);
        }


        for (int i = 0; i < solve.circles.size(); i++) {
            Circle newCircle = solve.circles.get(i);
            g.setColor(Color.WHITE);
            drawCircle(g, newCircle);
        }


        List<Line> lines = solve.findLines(solve.circles);


        for (Line value : lines) {
            g.drawLine((int) value.p1.x, (int) value.p1.y, (int) value.p2.x, (int) value.p2.y);
        }

        Line line = solve.findMaxLength(lines);

        g.setColor(new Color(0, 0, 128));


        drawCircle(g, line.c1);
        drawCircle(g, line.c2);
        g.setColor(new Color(30, 200, 0));
        g.drawLine((int) line.p1.x, (int) line.p1.y, (int) line.p2.x, (int) line.p2.y);
    }

    private class MyMouseListener implements MouseListener {

        stepa.project.stepa_project.Point rememberedPoint;

        @Override
        public void mouseClicked(MouseEvent e) {
            stepa.project.stepa_project.Point newPoint = new Point(e.getX(), e.getY());
            allPoints.add(newPoint);
            if (rememberedPoint != null) {
                Circle circle = new Circle(rememberedPoint, newPoint);
                solve.circles.add(circle);
                rememberedPoint = null;
                repaint();
            } else {
                rememberedPoint = newPoint;
            }

            textFIELD2.setText("Последняя введенная точка : \n    " + allPoints.get(allPoints.size() - 1));
            textFIELD2.append("\nПоследняя введенная окружность : \n" + solve.circles.get(solve.circles.size() - 1));

            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    public static void main(String[] args) {
        new MyFrame("project");
    }
}


