import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import javax.swing.JFrame;

public class KochSnowflake extends Applet implements ActionListener, Runnable{

  private Thread thread;
  private Graphics dbGraphics;
  private Image dbImage;

  private final Point[] BASE_POINTS = new Point[]{ new Point(280, 73), new Point(480, 420), new Point(80, 420) };

  private int iterations;
  private Button iterateButton, deiterateButton;

  private String perimeter, area;

  private final Font buttonFont = new Font("Times New Roman", Font.BOLD, 15);
  private final Font textFont = new Font("Papyrus", Font.BOLD, 20);
  private final Font graphFont = new Font("Headings", Font.BOLD, 10);
  private final DecimalFormat df = new DecimalFormat("######.0");

  public void init(){ //ZOOM IN to see repeats!!! also animation of infinite koch curve

    setSize(900, 600);
    setBackground(new Color(135, 206, 250));

    iterations = 0;

    iterateButton = new Button("ITERATE");
    iterateButton.setBackground(new Color(220, 230, 255));
    iterateButton.setFont(buttonFont);
    iterateButton.setFocusable(false);
    iterateButton.addActionListener(this);
    add(iterateButton);

    deiterateButton = new Button("DEITERATE");
    deiterateButton.setBackground(new Color(156, 176, 202));
    deiterateButton.setFont(buttonFont);
    deiterateButton.setFocusable(false);
    deiterateButton.addActionListener(this);
    add(deiterateButton);
  }

  public void paint(Graphics g){ //GRAPH AREA VS PERIMETER

    setAWTComponentBounds();
    g.setColor(Color.BLACK);
    drawStrings(g);
    setUpGraph(g);

    Point p1 = BASE_POINTS[0];
    Point p2 = BASE_POINTS[1];
    Point p3 = BASE_POINTS[2];

    g.setColor(Color.WHITE);

    koch(iterations, p1.x, p1.y, p2.x, p2.y, g);
    koch(iterations, p2.x, p2.y, p3.x, p3.y, g);
    koch(iterations, p3.x, p3.y, p1.x, p1.y, g);

    area = findArea(iterations) + "  hpx";
    perimeter = findPerimeter(iterations) + "  hpx";

    drawGraph(iterations, g);
  }

  private void koch(int order, int x1, int y1, int x5, int y5, Graphics g){ //equilateral triangles for one line

    if(order == 0) g.drawLine(x1, y1, x5, y5);

    else{

      int deltaX, deltaY, x2, y2, x3, y3, x4, y4;

      deltaX = x5 - x1; //change in x
      deltaY = y5 - y1; //change in y

      x2 = (int) ( x1 + (deltaX / 3.0) ); //1/3 length
      y2 = (int) ( y1 + (deltaY / 3.0) );

      x3 = (int)( ( (x1+x5)/2.0 ) + ( Math.sqrt(3) * (y5-y1)/6.0 ) ); //tip of new triangle
      y3 = (int)( ( (y1+y5)/2.0 ) - ( Math.sqrt(3) * (x5-x1)/6.0 ) );

      x4 = (int) ( x1 + (deltaX*2/3.0) ); //2/3 length
      y4 = (int) ( y1 + (deltaY*2/3.0) );

      koch(order-1, x1, y1, x2, y2, g);
      koch(order-1, x2, y2, x3, y3, g);
      koch(order-1, x3, y3, x4, y4, g);
      koch(order-1, x4, y4, x5, y5, g);
    }

  }

  private String findArea(int n){ //number of iterations

    double a = 0;

    double s = 4; //original side length (/100)

    double summation = 0;

    for(int i=1; i<=n; i++){

      summation += ( (3 * Math.pow(4, i-1))/Math.pow(9, i) );
    }

    a = (Math.sqrt(3)/4.0)*s*s*( 1 + summation );

    String aString = df.format(a);

    return aString;
  }

  private String findPerimeter(int n){ //number of iterations

    double p = 0;

    p = (3*Math.pow(4, n)) * (4*Math.pow(3, -n));

    String pString = df.format(p);

    return pString;
  }

  private void setUpGraph(Graphics g){

    g.setColor(Color.BLACK);
    g.drawLine(550, 500, 850, 500); //x axis
    g.setFont(textFont);
    g.drawString("Iterations", 650, 540);
    g.setFont(graphFont);
    for(int i=0; i<=10; i++) g.drawString(i + "", 550 + (29*i), 515);

    g.drawLine(550, 250, 550, 500); //y axis
    for(int i=0; i<=180; i+=20) g.drawString(i + "", 525, 500 - (int)(1.39 * i));
  }

  private void drawGraph(int i, Graphics g){

    Graphics2D g2 = (Graphics2D) (g);
    g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));

    int[] xs = new int[i+1];
    int[] aYs = new int[xs.length];
    int[] pYs = new int[xs.length];

    for(int j=0; j<xs.length; j++){

      xs[j] = 550 + (j*29);

      double aY = Double.parseDouble( findArea(j) );
      aYs[j] = (int)(500 - aY);

      double pY = Double.parseDouble( findPerimeter(j) );
      pYs[j] = (int)(500 - pY);
    }

    g2.setColor(new Color(25, 25, 112)); //area
    for(int j=0; j<xs.length-1; j++) g2.drawLine(xs[j], aYs[j], xs[j+1], aYs[j+1]);

    g2.setColor(new Color(250, 240, 230)); //perimeter
    for(int j=0; j<xs.length-1; j++) g2.drawLine(xs[j], pYs[j], xs[j+1], pYs[j+1]);

    g2.setStroke(new BasicStroke());
  }

  public void update(Graphics g){

    if(dbImage == null){

      dbImage = createImage(getSize().width, getSize().height);
      dbGraphics = dbImage.getGraphics();
    }

    dbGraphics.setColor(getBackground());
    dbGraphics.fillRect(0, 0, getSize().width, getSize().height);
    dbGraphics.setColor(getForeground());
    paint(dbGraphics);

    g.drawImage(dbImage, 0, 0, this);
  }

  public void actionPerformed(ActionEvent e){

    Object source = e.getSource();

    if(source == iterateButton) if(iterations < 12) iterations++;
    if(source == deiterateButton) if(iterations > 0) iterations--;
  }

  private void setAWTComponentBounds(){

    iterateButton.setBounds(620, 70, 100, 30);
    deiterateButton.setBounds(730, 70, 100, 30);
  }

  private void drawStrings(Graphics g){

    g.setFont(textFont);
    g.setColor(Color.BLACK);
    g.drawString("Iterations: " + iterations, 660, 135);
    g.setColor(new Color(25, 25, 112));
    g.drawString("Area: " + area, 660, 205);
    g.setFont(new Font("Papyrus", Font.BOLD, 10)); //squared font
    g.drawString("2", 804, 195);
    g.setFont(textFont);
    g.setColor(new Color(250, 240, 230));
    g.drawString("Perimeter: " + perimeter, 640, 170);
  }

  public void start(){

    if(thread == null){

      thread = new Thread(this);
      thread.start();
    }
  }

  public void run(){

    while(thread != null){

      repaint();

      try{
        Thread.sleep(20);
      }
      catch(InterruptedException e){
      }
    }
  }

  public void stop(){

    thread = null;
  }

  public static void main(String[] args){

    Applet thisApplet = new KochSnowflake();
    thisApplet.init();
    thisApplet.start();

    JFrame frame = new JFrame("Koch Snowflake (Recursion)");
    frame.setSize(thisApplet.getSize().width, thisApplet.getSize().height);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    frame.getContentPane().add(thisApplet, BorderLayout.CENTER);
    frame.setVisible(true);
  }
}
