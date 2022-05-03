package stepa.project;

import stepa.project.Point;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MyFrame extends JFrame implements ActionListener {

    public final JButton button_ADD;
    public final JButton button_RESET;
    public final JButton button_SOLVE;

    public final JButton button_ADD_RANDOM;
    public final JButton button_SAVE_TO_FILE;
    public final JButton button_READ_FILE;


    public final JTextField textFIELD;
    public final JTextArea textFIELD2;
    public final JTextComponent textArea;

    public final JTextField countOfRandomCircles;


    Solution solve = new Solution(new ArrayList<>());

    List<stepa.project.Point> allPoints = new ArrayList<>();



    public MyFrame(String title) {
        super(title);

        textArea = new JTextArea("          Условие задачи :" + "\n    Среди всего множества окружностей " + "\n    найти пару окружностей," +
                " такую," + "\n    что длина их общей хорды будет максимальна" );
        textArea.setBounds(1000, 10, 350, 80);

        button_SOLVE = new JButton("РЕШИТЬ");
        button_SOLVE.setBounds(1000, 250, 170, 50);
        button_SOLVE.addActionListener(this);

        button_RESET = new JButton("СБРОС");
        button_RESET.setBounds(1180, 250, 170, 50);
        button_RESET.addActionListener(this);

        button_ADD = new JButton("ДОБАВИТЬ");
        button_ADD.setBounds(1000, 180, 350, 50);
        button_ADD.addActionListener(this);

        button_ADD_RANDOM = new JButton("ДОБАИВТЬ РАНДОМНО");
        button_ADD_RANDOM.setBounds(1070, 460, 280, 50);
        button_ADD_RANDOM.addActionListener(this);

        button_SAVE_TO_FILE = new JButton("СОХРАНИТЬ В ФАЙЛ");
        button_SAVE_TO_FILE.setBounds(1000, 320, 165, 50);
        button_SAVE_TO_FILE.addActionListener(this);

        button_READ_FILE = new JButton("ДОБАВИТЬ ИЗ ФАЙЛА"+ " (кол-во окр потом их координаты)");
        button_READ_FILE.setBounds(1000, 390, 350, 50);
        button_READ_FILE.addActionListener(this);

        countOfRandomCircles = new JTextField();
        countOfRandomCircles.setBounds(1000, 460, 50, 50);

        textFIELD = new HintTextField("Сюда вводить 4  координаты");
        textFIELD.setBounds(1000, 100, 350, 50);

        textFIELD2 = new JTextArea("Последняя веденная точка :");
        textFIELD2.setBounds(1000, 600, 350, 200);


        add(countOfRandomCircles);
        add(textFIELD);
        add(textFIELD2);
        add(textArea);

        add(button_ADD);
        add(button_SOLVE);
        add(button_RESET);
        add(button_ADD_RANDOM);
        add(button_SAVE_TO_FILE);
        add(button_READ_FILE);

        setLayout(null);

        setSize(1400, 900);
        getContentPane().setBackground(new Color(11, 31, 194));
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

        for (stepa.project.Point allPoint : allPoints) {
            g.drawOval((int) allPoint.x - 3, (int) allPoint.y - 3, 5, 5);
            g.fillOval((int) allPoint.x - 3, (int) allPoint.y - 3, 5, 5);
        }

        for (int i = 0; i < solve.circles.size(); i++) {
            Circle newCircle = solve.circles.get(i);
            g.setColor(Color.BLACK);
            drawCircle(g, newCircle);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (!allPoints.isEmpty()) {
            textFIELD2.setText("Последняя введенная точка : \n    " + allPoints.get(allPoints.size() - 1));}
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

                stepa.project.Point centre = new stepa.project.Point(coordinates.get(0), coordinates.get(1));
                stepa.project.Point pointOnCircle = new stepa.project.Point(coordinates.get(2), coordinates.get(3));

                System.out.println(coordinates);

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

        if (e.getSource() == button_ADD_RANDOM) {
            int count = Integer.parseInt(countOfRandomCircles.getText());
            solve.addRandomCircles(solve.circles, count);
            repaint();
        }

        if (e.getSource() == button_SAVE_TO_FILE) {
            try {
                File output = new File("out.txt");
                BufferedWriter out = new BufferedWriter(new FileWriter(output));

                List<Circle> circles = solve.circles;
                for (int i = 0; i < circles.size(); i++) {
                    Circle circle = circles.get(i);
                    out.write(i + 1 + " окружность:");
                    out.write(circle + "\n");
                    out.write("\n");
                }

                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        if (e.getSource() == button_READ_FILE) {
            try {
                Scanner in = new Scanner(new File("in.txt"));
                int n = in.nextInt();

                for (int i = 0; i < n; i++) {
                    int a = in.nextInt();
                    int b = in.nextInt();

                    allPoints.add(new stepa.project.Point(a, b));

                    int c = in.nextInt();
                    int d = in.nextInt();

                    allPoints.add(new stepa.project.Point(c, d));
                    solve.circles.add(new Circle(new stepa.project.Point(a, b), new stepa.project.Point(c, d)));
                }

                in.close();
                repaint();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showSolution(Graphics g1) {
        super.paint(g1);

        Graphics2D g = (Graphics2D) g1;
        BasicStroke pen1 = new BasicStroke(5);
        g.setStroke(pen1);
        g.setColor(Color.WHITE);

        for (stepa.project.Point allPoint : allPoints) {
            g.drawOval((int) allPoint.x - 3, (int) allPoint.y - 3, 5, 5);
            g.fillOval((int) allPoint.x - 3, (int) allPoint.y - 3, 5, 5);
        }


        for (int i = 0; i < solve.circles.size(); i++) {
            Circle newCircle = solve.circles.get(i);
            g.setColor(Color.BLACK);
            drawCircle(g, newCircle);
        }


        List<Line> lines = solve.findLines(solve.circles);


        //for (Line value : lines) {
            //g.drawLine((int) value.p1.x, (int) value.p1.y, (int) value.p2.x, (int) value.p2.y);
        //}

        Line line = solve.findMaxLength(lines);

        g.setColor(new Color(8, 215, 207));


        drawCircle(g, line.c1);
        drawCircle(g, line.c2);
        g.setColor(new Color(0, 255, 0));
        g.drawLine((int) line.p1.x, (int) line.p1.y, (int) line.p2.x, (int) line.p2.y);
    }

    private class MyMouseListener implements MouseListener {

        stepa.project.Point rememberedPoint;

        @Override
        public void mouseClicked(MouseEvent e) {
            stepa.project.Point newPoint = new Point(e.getX(), e.getY());
            allPoints.add(newPoint);
            if (rememberedPoint != null) {
                Circle circle = new Circle(rememberedPoint, newPoint);
                solve.circles.add(circle);
                rememberedPoint = null;
                repaint();
            } else {
                rememberedPoint = newPoint;
            }


            if (!allPoints.isEmpty() && !solve.circles.isEmpty()) {

                textFIELD2.setText("Последняя введенная точка : \n    " + allPoints.get(allPoints.size() - 1));
                textFIELD2.append("\nПоследняя введенная окружность : \n" + solve.circles.get(solve.circles.size() - 1));
            }

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


