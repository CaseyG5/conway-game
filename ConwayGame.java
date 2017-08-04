package conwaygame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

class GameDemo extends JFrame implements ActionListener, 
        MouseListener {
    
    public final int WIDTH = 500;
    public final int HEIGHT = 500;
    
    private final int yPanels;
    private final int xPanels;
    
    private JPanel[][] colorPanel;
    private Color[][] creatures;          // 2nd grid of just colors for next generation
    private int[][] neighbors;            // 2-D ragged array of cells and neighboring cells
    
    private Color[] colors = { Color.WHITE, Color.BLACK };
    
    private Timer timer;
    private boolean timerOn;
    
    public GameDemo(int n) {                // constructor takes a value n and
        super("Conway's Game of Life");     // creates an nxn grid
        
        setSize(WIDTH, HEIGHT);
        xPanels = n; yPanels = n;
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setLayout(new BorderLayout());
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(yPanels, xPanels));   // NxN
        colorPanel = new JPanel[yPanels][xPanels];
        creatures = new Color[n][n];
        neighbors = new int[n*n][];
        prepNeighbors(n,n);
        
        // initialize panels and creature cells
        for(int i=0; i<yPanels; i++) {
            for(int j=0; j<xPanels; j++) {
                colorPanel[i][j] = new JPanel();
                colorPanel[i][j].setBackground(Color.WHITE);
                colorPanel[i][j].addMouseListener(this);
                centerPanel.add(colorPanel[i][j]);
                creatures[i][j] = Color.WHITE;
            }
        }
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        buttonPanel.setLayout(new FlowLayout());
        
        JButton clrButton = new JButton("Clear");
        clrButton.addActionListener(this);
        buttonPanel.add(clrButton);
        
        JButton randButton = new JButton("Populate");
        randButton.addActionListener(this);
        buttonPanel.add(randButton);
        
        JButton stepButton = new JButton("Next Gen");
        stepButton.addActionListener(this);
        buttonPanel.add(stepButton);
        
        JButton runButton = new JButton("Run");
        runButton.addActionListener(this);
        buttonPanel.add(runButton);
        
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(this);
        buttonPanel.add(stopButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        timerOn = false;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonString = e.getActionCommand();
        switch(buttonString) {
            case "Clear":
                for(int i=0; i<colorPanel.length; i++) {
                    for(int j=0; j<colorPanel[i].length; j++) {
                        colorPanel[i][j].setBackground(Color.WHITE);
                        creatures[i][j] = Color.WHITE;      // required to fully  
                    }                                       // erase previous 
                }                                           //generation
                break;
                
            case "Populate":
                int borw;
                for(int i=0; i<colorPanel.length; i++) {
                    for(int j=0; j<colorPanel[i].length; j++) {
                        borw = (int) (Math.random() * 2.0);
                        colorPanel[i][j].setBackground(colors[borw]);
                        //creatures[i][j] = colors[borw];
                    }
                }
                break;
                
            case "Next Gen":
                int nbrs;                                 // neighbors
                for(int i=0; i<colorPanel.length; i++) {
                    for(int j=0; j<colorPanel[i].length; j++) {
                        nbrs = countNeighbors(i, j);    // current # of neighbors
                        
                        // if cell is live
                        if(colorPanel[i][j].getBackground().equals(Color.BLACK)) {
                            // if < 2 or > 3 neighbors, cell becomes dead
                            if(nbrs > 3 || nbrs < 2) creatures[i][j] = Color.WHITE;
                            // live cell stays live
                            else creatures[i][j] = Color.BLACK;
                        }
                        else {  // cell is dead
                            // if neighbors is exactly 3, cell becomes live
                            if(nbrs == 3) creatures[i][j] = Color.BLACK;
                        }
                    }
                }
                // refresh color panels to reflect the new generation
                for(int i=0; i<colorPanel.length; i++) {
                    for(int j=0; j<colorPanel[i].length; j++) {
                        colorPanel[i][j].setBackground(creatures[i][j]);
                    }
                }
                break;
                
            case "Run":
                if(timerOn == false) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {
                            
                            int nbrs;
                            for(int i=0; i<colorPanel.length; i++) {
                                for(int j=0; j<colorPanel[i].length; j++) {
                                    nbrs = countNeighbors(i, j);
                                    if(colorPanel[i][j].getBackground() == Color.BLACK) {
                                        if(nbrs > 3 || nbrs < 2) creatures[i][j] = Color.WHITE;
                                        else creatures[i][j] = Color.BLACK;
                                    }
                                    else {
                                        if(nbrs == 3) creatures[i][j] = Color.BLACK;
                                    }
                                }
                            }
                            for(int i=0; i<colorPanel.length; i++) {
                                for(int j=0; j<colorPanel[i].length; j++) {
                                    colorPanel[i][j].setBackground(creatures[i][j]);
                                }
                            }
                        }
                    }, 0, 500);
                    timerOn = true;
                }
                break;
                
            case "Stop":
                if(timerOn == true) {
                    timer.cancel();
                    timerOn = false;
                }
                break;
            
            default: 
                System.out.println("unexpected error");
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        //System.out.println("component: " + me.getComponent());
        // flip color
        if(me.getComponent().getBackground().equals(Color.WHITE) )
            me.getComponent().setBackground(Color.BLACK);
        else me.getComponent().setBackground(Color.WHITE);
    }
    
    public void mousePressed(MouseEvent me) { }
    public void mouseReleased(MouseEvent me) { }
    public void mouseEntered(MouseEvent me) { }
    public void mouseExited(MouseEvent me) { }
    
    public void run() {
        System.out.println("timer working");
    }
    
    private int countNeighbors(int r, int c) {
        int count = 0;
        
        // get cell # from row & column (above parameters)
        int cell = r * xPanels + c;     
        // then for that cell, check each neighbor (alive or dead?)
        for(int i=0; i<neighbors[cell].length; i++) {
            int row = neighbors[cell][i] / xPanels;     // get row/col from each
            int col = neighbors[cell][i] % xPanels;     // neighbor's cell #
            // check if neighbor is black (alive)
            if(colorPanel[row][col].getBackground() == Color.BLACK) count++;
        }
        
        return count;
    }
    
    // This function prepares a table of cells and their neighboring cells,
    private void prepNeighbors(int m, int n) {
        //System.out.print("\nPreparing proximity table...");
        
        // Corner cell #s for reference
        // A = 0
        final int B = m - 1;
        final int C = m * (n - 1);
        final int D = m * n - 1;
        
        // Corners and their 3 neighbors
        neighbors[0] = new int[] { 1,   m,     m+1 };
        neighbors[B] = new int[] { B-1, B+m-1, B+m };
        neighbors[C] = new int[] { C-m, C-m+1, C+1 };
        neighbors[D] = new int[] { D-m, D-m-1, D-1 };
        
        // Neighbors of TOP row of cells (5 neighbors each)
        for(int i=1; i<B; i++) 
            neighbors[i] = new int[] { i-1, i+1, i+m-1, i+m, i+m+1 };
        
        // Neighbors of BOTTOM row of cells (5 neighbors each)
        for(int i=C+1; i<D; i++) 
            neighbors[i] = new int[] { i-m-1, i-m, i-m+1, i-1, i+1 };
        
        // Neighbors of LEFT column of cells (5 neighbors each)
        for(int i=m; i<C; i+=m) 
            neighbors[i] = new int[] { i-m, i-m+1, i+1, i+m, i+m+1 };
        
        // Neighbors of RIGHT column of cells (5 neighbors each)
        for(int i=B+m; i<D; i+=m) 
            neighbors[i] = new int[] { i-m-1, i-m, i-1, i+m-1, i+m };
        
        // Neighbors of all other cells (8 neighbors each)
        for(int i=m; i<C; i+=m)
            for(int j=1; j<B; j++) 
                neighbors[i+j] = new int[] { i+j-m-1, i+j-m, i+j-m+1, i+j-1, 
                                             i+j+1, i+j+m-1, i+j+m, i+j+m+1 };
        //System.out.println("done.");
    }
}

public class ConwayGame {
    public static void main(String[] args) {
        GameDemo gui = new GameDemo(25);
        gui.setVisible(true);
    }
}
