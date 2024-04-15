package GUI;

import BusinessLogic.SelectionPolicy;
import BusinessLogic.SimulationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SimulationFrame implements ActionListener{

    JFrame frame;
    JTextField clientTxt, queueTxt, intervalTxt, minArrivalTxt, maxArrivalTxt, minServiceTxt, maxServiceTxt;
    JLabel clientLabel, queueLabel, intervalLabel, minArrivalLabel, maxArrivalLabel, titleLabel, minServiceLabel, maxServiceLabel, startLabel;
    JButton okBtn, startBtn;
    JComboBox<String> strategyButton;

    Font myFont = new Font(Font.SANS_SERIF, Font.ITALIC, 15);
    Font myFont2 = new Font(Font.SANS_SERIF, Font.BOLD, 15);

    public SimulationFrame(){
        frame = new JFrame("Queue Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 550);
        frame.setLayout(null);
        frame.getContentPane().setBackground(new Color(241, 228, 229));

        titleLabel = new JLabel("Queue System Management");
        titleLabel.setFont(myFont2);
        titleLabel.setBounds(100, 5, 200, 30);

        clientTxt = new JTextField();
        clientTxt.setBounds(50, 50, 100, 30);
        clientTxt.setFont(myFont);
        clientLabel = new JLabel("number of clients");
        clientLabel.setBounds(200, 50, 200, 30);
        clientLabel.setFont(myFont);

        queueTxt = new JTextField();
        queueTxt.setBounds(50, 80, 100, 30);
        queueTxt.setFont(myFont);
        queueLabel = new JLabel("number of queues");
        queueLabel.setBounds(200, 80, 200, 30);
        queueLabel.setFont(myFont);

        intervalTxt = new JTextField();
        intervalTxt.setBounds(50, 110, 100, 30);
        intervalTxt.setFont(myFont);
        intervalLabel = new JLabel("simulation interval");
        intervalLabel.setBounds(200, 110, 200, 30);
        intervalLabel.setFont(myFont);

        minArrivalTxt = new JTextField();
        minArrivalTxt.setBounds(50, 170, 100, 30);
        minArrivalTxt.setFont(myFont);
        minArrivalLabel = new JLabel("min. arrival time");
        minArrivalLabel.setBounds(50, 195, 200, 30);
        minArrivalLabel.setFont(myFont);

        maxArrivalTxt = new JTextField();
        maxArrivalTxt.setBounds(230, 170, 100, 30);
        maxArrivalTxt.setFont(myFont);
        maxArrivalLabel = new JLabel("max. arrival time");
        maxArrivalLabel.setBounds(230, 195, 200, 30);
        maxArrivalLabel.setFont(myFont);

        minServiceTxt = new JTextField();
        minServiceTxt.setBounds(50, 230, 100, 30);
        minServiceTxt.setFont(myFont);
        minServiceLabel = new JLabel("min. service time");
        minServiceLabel.setBounds(50, 255, 200, 30);
        minServiceLabel.setFont(myFont);

        maxServiceTxt = new JTextField();
        maxServiceTxt.setBounds(230, 230, 100, 30);
        maxServiceTxt.setFont(myFont);
        maxServiceLabel = new JLabel("max. service time");
        maxServiceLabel.setBounds(230, 255, 200, 30);
        maxServiceLabel.setFont(myFont);

        okBtn = new JButton("START");
        okBtn.setFont(myFont2);
       // okBtn.setBounds(150, 300, 100, 30);
        okBtn.setBounds(150, 400, 100, 30);
        // Set background color of the button
        okBtn.setBackground(new Color(205, 171, 173));
        // Set text color of the button
        okBtn.setForeground(Color.black);

        startLabel = new JLabel();
        startLabel.setFont(myFont2);
        startLabel.setText("START SIMULATION!");
        startLabel.setHorizontalAlignment(SwingConstants.CENTER);
        startLabel.setBounds(100, 350, 200, 30);

        String[] strategies = {"StrategyTime", "StrategyQueue"};
        strategyButton = new JComboBox<>(strategies);
        strategyButton.setFont(myFont2);
        strategyButton.setBounds(100, 300, 200, 30);
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        renderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
        strategyButton.setRenderer(renderer);
        strategyButton.setBackground(new Color(205, 171, 173)); // Set button background color

        strategyButton.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                renderer.setBackground(new Color(205, 171, 173)); // Set renderer background color
                return renderer;
            }
        });

        frame.add(strategyButton);
        frame.add(startLabel);
        frame.add(okBtn);
        frame.add(maxServiceLabel);
        frame.add(maxServiceTxt);
        frame.add(minServiceLabel);
        frame.add(minServiceTxt);
        frame.add(maxArrivalLabel);
        frame.add(maxArrivalTxt);
        frame.add(minArrivalTxt);
        frame.add(minArrivalLabel);
        frame.add(intervalLabel);
        frame.add(intervalTxt);
        frame.add(queueLabel);
        frame.add(queueTxt);
        frame.add(titleLabel);
        frame.add(clientTxt);
        frame.add(clientLabel);
        frame.setVisible(true);

        okBtn.addActionListener(this);
    }

    public List<Integer> getInputs(){
        List<Integer> inputs = new ArrayList<>();
        inputs.add(Integer.parseInt(clientTxt.getText()));
        inputs.add(Integer.parseInt(queueTxt.getText()));
        inputs.add(Integer.parseInt(intervalTxt.getText()));
        inputs.add(Integer.parseInt(minArrivalTxt.getText()));
        inputs.add(Integer.parseInt(maxArrivalTxt.getText()));
        inputs.add(Integer.parseInt(minServiceTxt.getText()));
        inputs.add(Integer.parseInt(maxServiceTxt.getText()));

        return inputs;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okBtn) {
            List<Integer> inputs = getInputs();
            SimulationManager simManager = new SimulationManager(this);
            simManager.setInputs(inputs);

            String selectedStrategy = (String) strategyButton.getSelectedItem();
            if (selectedStrategy.equals("StrategyTime")) {
                simManager.getScheduler().changeStrategy(SelectionPolicy.SHORTEST_TIME);
            } else if (selectedStrategy.equals("StrategyQueue")) {
                simManager.getScheduler().changeStrategy(SelectionPolicy.SHORTEST_QUEUE);
            }

            Thread thread = new Thread(simManager);
            thread.start();
        }
    }
}
