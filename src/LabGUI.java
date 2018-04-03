import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


class LabGUI extends JFrame {
    private JTabbedPane contentPane;

    private LabGUI() {
        setComponents();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 1000));
        setTitle("Lab 6");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setComponents() {
        contentPane = new JTabbedPane();
        setContentPane(contentPane);
        setTask1();
        //setTask2();
    }

    private JPanel panel1;
    private BufferedImage image;
    private JLabel imageLabel;
    private int rows, cols, chunks;
    private Map<Integer, JLabel> labels;
    private JPanel puzzlePanel;
    private List<Integer> permutation;
    private int curIdx;
    private Border usualBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
    private Border selectedBorder = BorderFactory.createLineBorder(Color.MAGENTA, 2);
    private MouseListener listenerMouse;
    private void setTask1() {
        rows = 3;
        cols = 3;
        chunks = rows*cols;
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        contentPane.addTab("6.1", panel1);
        JPanel controlPanel = new JPanel();
        JPanel viewPanel = new JPanel();
        JPanel extraPanel = new JPanel();
        viewPanel.setLayout(new GridLayout(1,2));
        extraPanel.setLayout(null);
        panel1.add(controlPanel, BorderLayout.SOUTH);
        JButton loadButton = new JButton("Load Image");
        controlPanel.add(loadButton);
        imageLabel = new JLabel();
        puzzlePanel = new JPanel();
        panel1.add(imageLabel, BorderLayout.NORTH);
        panel1.add(extraPanel, BorderLayout.CENTER);
        extraPanel.add(puzzlePanel);
        loadButton.addActionListener(e-> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open File");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "gif", "jpeg", "jpg"));
            int res = fileChooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    image = ImageIO.read(file);
                    if (image == null) throw new IOException("Incorrect file format!");
                    imageLabel.setIcon(new ImageIcon(image));
                    int r = Integer.valueOf(JOptionPane.showInputDialog("Rows: "));
                    int c = Integer.valueOf(JOptionPane.showInputDialog("Columns: "));
                    if (r <= 0 || c <= 0 || r > image.getHeight() || c > image.getWidth()) throw new NumberFormatException("Incorrect column or row number!");
                    rows = r;
                    cols = c;
                    chunks = rows*cols;
                    makePuzzle();
                    repaint();
                }
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "File Read Error", JOptionPane.ERROR_MESSAGE);
                }
                catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Wrong input", JOptionPane.ERROR_MESSAGE);
                    makePuzzle();
                    repaint();
                }
            }
        });
        puzzlePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: {
                        int newIdx = (chunks + curIdx - 1) % chunks;
                        JLabel curLabel = labels.get(curIdx), newLabel = labels.get(newIdx);
                        Collections.swap(permutation, curIdx, newIdx);
                        System.out.println(permutation);
                        curIdx = newIdx;
                        Icon temp = curLabel.getIcon();
                        curLabel.setIcon(newLabel.getIcon());
                        newLabel.setIcon(temp);
                        newLabel.setBorder(selectedBorder);
                        curLabel.setBorder(usualBorder);
                        checkPermutation();
                        break;
                    }
                    case KeyEvent.VK_RIGHT: {
                        int newIdx = (curIdx + 1) % chunks;
                        Collections.swap(permutation, curIdx, newIdx);
                        JLabel curLabel = labels.get(curIdx), newLabel = labels.get(newIdx);
                        curIdx = newIdx;
                        Icon temp = curLabel.getIcon();
                        curLabel.setIcon(newLabel.getIcon());
                        newLabel.setIcon(temp);
                        newLabel.setBorder(selectedBorder);
                        curLabel.setBorder(usualBorder);
                        checkPermutation();
                        break;
                    }
                    case KeyEvent.VK_DOWN: {
                        int newIdx = (chunks + curIdx + cols) % chunks;
                        Collections.swap(permutation, curIdx, newIdx);
                        JLabel curLabel = labels.get(curIdx), newLabel = labels.get(newIdx);
                        curIdx = newIdx;
                        Icon temp = curLabel.getIcon();
                        curLabel.setIcon(newLabel.getIcon());
                        newLabel.setIcon(temp);
                        newLabel.setBorder(selectedBorder);
                        curLabel.setBorder(usualBorder);
                        checkPermutation();
                        break;
                    }
                    case KeyEvent.VK_UP: {
                        int newIdx = (chunks + curIdx - cols) % chunks;
                        Collections.swap(permutation, curIdx, newIdx);
                        JLabel curLabel = labels.get(curIdx), newLabel = labels.get(newIdx);
                        curIdx = newIdx;
                        Icon temp = curLabel.getIcon();
                        curLabel.setIcon(newLabel.getIcon());
                        newLabel.setIcon(temp);
                        newLabel.setBorder(selectedBorder);
                        curLabel.setBorder(usualBorder);
                        checkPermutation();
                        break;
                    }
                }
            }
        });
        listenerMouse = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                puzzlePanel.requestFocus();
                for (int i = 0; i < chunks; i++) {
                    labels.get(i).setBorder(usualBorder);
                }
                JLabel label = (JLabel)e.getSource();
                label.setBorder(selectedBorder);
                for (int i : labels.keySet()) {
                    if (labels.get(i).equals(label)) {
                        curIdx = i;
                        break;
                    }
                }
            }
        };
    }
    private void makePuzzle() {
        puzzlePanel.requestFocus();
        puzzlePanel.removeAll();
        puzzlePanel.setSize(new Dimension(image.getWidth(), image.getHeight()));
        BufferedImage[] imgs = getImages();
        puzzlePanel.setLayout(new GridLayout(rows, cols));
        labels = new HashMap<>();
        permutation = new ArrayList<>();
        for (int i = 0; i < chunks; i++) {
            JLabel label = new JLabel();
            labels.put(i, label);
            label.setBorder(usualBorder);
            puzzlePanel.add(label);
            permutation.add(i);
        }
        Collections.shuffle(permutation);
        curIdx = 0;
        labels.get(0).setBorder(selectedBorder);
        for (int i = 0; i < chunks; i++) {
            JLabel label = labels.get(i);
            label.addMouseListener(listenerMouse);
            label.setIcon(new ImageIcon(imgs[permutation.get(i)]));
        }
    }
    private void checkPermutation() {
        for (int i = 0; i < chunks; ++i) {
            if (i != permutation.get(i)) return;
        }
        JOptionPane.showMessageDialog(this, "Well done!", "Finished", JOptionPane.PLAIN_MESSAGE);
    }

    private BufferedImage[] getImages() {
        int chunkWidth = image.getWidth() / rows;
        int chunkHeight = image.getHeight() / cols;
        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks];
        for (int x = 0; x < rows; ++x) {
            for (int y = 0; y < cols; ++y) {
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }
        return imgs;
    }

    public static void main(String[] args) {
        new LabGUI();
    }
}


