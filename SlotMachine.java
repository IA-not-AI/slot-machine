import javax.swing.*;
import java.awt.*;
import java.awt.event.*;                                                         
import java.io.*;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
//imports for audio stuff
import javax.sound.sampled.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

//image implement from this vid https://www.youtube.com/watch?v=FdQX8sUNO-s
//Buttons and display from this vid https://www.youtube.com/watch?v=35N6eeG2MW0

//Thank you to this stack overflow article for explaining https://stackoverflow.com/questions/31144154/how-do-java-imports-work-swing

public class SlotMachine extends JPanel implements ActionListener
{
   private final ArrayList<String>[] wordLists = new ArrayList[4];//One list for each reel
   private final JLabel[] reels = new JLabel[4];//The visual reels
   private final javax.swing.Timer timer;
   private final Random random = new Random();
    
   private JButton spinButton;
   private JButton likeButton;
   private JButton dislikeButton;
   
   private Clip womp;
    
   private ImageIcon image1;//*************************************************************For image display idk if it works tbh*******************************************************************//
   private JLabel label1;   //*********************************For the images we just need to copy paste it into the folder and in constuctor use the file name************************************//
                             //*****************************************************Still need to figure out how stacking of images works***********************************************************//
   public SlotMachine() throws UnsupportedAudioFileException, IOException, LineUnavailableException
   {
      setLayout(null);//absolute positioning because of layering
      
      //loading background image
      ImageIcon backgroundImg = new ImageIcon(getClass().getResource("Screenshot 2025-05-15 101631.png")); //ignore this image, im thinking we can just have an art deco background and just have the rectangle slots on top of the background
      JLabel backgroundLabel = new JLabel(backgroundImg);                              // and we can just have the art deco background have built in buttons and just make our buttons transparent.
      backgroundLabel.setBounds(0, 0, backgroundImg.getIconWidth(), backgroundImg.getIconHeight()); // we can do this together later
      //JLayeredPane to layer components
      JLayeredPane layeredPane = new JLayeredPane();
      layeredPane.setPreferredSize(new Dimension(backgroundImg.getIconWidth(), backgroundImg.getIconHeight()));
      //background to bottom layer
      backgroundLabel.setBounds(0, 0, backgroundImg.getIconWidth(), backgroundImg.getIconHeight());
      layeredPane.add(backgroundLabel, Integer.valueOf(0));
      
      setLayout(new GridLayout(1, 3, 10, 10));//basically taken straight from pinBall.java
      setBackground(Color.BLACK);
      Font font = new Font("SansSerif", Font.BOLD, 15);
   
        //reels and word lists
      for (int i = 0; i < 4; i++)
      {
         reels[i] = new JLabel("Loading...", SwingConstants.CENTER);
         reels[i].setFont(font);
         reels[i].setOpaque(true);
         reels[i].setBackground(Color.WHITE);
         add(reels[i]);
         reels[i].setBounds(300 + i * 210, 100, 200, 400);//Adjust position
         layeredPane.add(reels[i], Integer.valueOf(1));//Above background
         wordLists[i] = new ArrayList<>();
      }
        //creating the buttons
      spinButton = new JButton("Spin");
      likeButton = new JButton("\uD83D\uDC4D");
      dislikeButton = new JButton("\uD83D\uDC4E");
        //the setFocusable gets rid of the box around the text when the button is clicked
      spinButton.setFocusable(false);
      likeButton.setFocusable(false);
      dislikeButton.setFocusable(false);
        //setting button dimensions   //mr.pugh said we can make the buttons transparent so it blends in with the graphic (so it'll be like youre actually clicking the graphic)
      spinButton.setBounds(400, 700, 600, 50);
      likeButton.setBounds(1150, 700, 80, 50);
      dislikeButton.setBounds(200, 700, 80, 50);
        //allows buttons to be pressed
      spinButton.addActionListener(e -> startSpinning());
      likeButton.addActionListener(this);//I added stuff into the actionPerformed method to play the 
      dislikeButton.addActionListener(this);//womp womp sound when disliked and we can change the background when liked
        //adds to GUI
      layeredPane.add(spinButton, Integer.valueOf(2));
      layeredPane.add(likeButton, Integer.valueOf(2));
      layeredPane.add(dislikeButton, Integer.valueOf(2));
      add(layeredPane);
   
        //buzzwords from external text files
      loadWords("reel1.txt", 0);
      loadWords("reel2.txt", 1);
      loadWords("reel3.txt", 2);
      loadWords("reel4.txt", 3);
   
      timer = new javax.swing.Timer(200, this);//spin speed
      
      //this is for uploading the womp sound
      File file = new File("womp-womp.wav");
      AudioInputStream au = AudioSystem.getAudioInputStream(file);
      womp = AudioSystem.getClip();
      womp.open(au);
   }//end of constructor
    
    //method to load words into a specific list
   private void loadWords(String filename, int index)
   {//to stop a null from crashing program
      try(Scanner scanner = new Scanner(new File(filename)))
      {
         while (scanner.hasNextLine())
         {
            wordLists[index].add(scanner.nextLine().trim());
         }
      }
      catch (IOException e)
      {
         wordLists[index].add("ERROR");
         System.err.println("Could not read " + filename);
      }
   }
    //start spinning the slot machine
   public void startSpinning()
   {
      timer.start();//start spinning
   
      //create a one-time task to stop spinning after given time
      Timer stopTimer = new Timer();
      stopTimer.schedule(
         new TimerTask()
         {
            public void run()
            {
               timer.stop();//stop the spin
               stopTimer.cancel();//cancel the timer
            }
         }
         , 1000);//1000 milliseconds = 1 second
   }

    //this runs every time the timer ticks
   public void actionPerformed(ActionEvent e)
   {
      for(int i = 0; i < 4; i++)
      {
         ArrayList<String> words = wordLists[i];
         if (!words.isEmpty())
         {
            String word = words.get(random.nextInt(words.size()));
            reels[i].setText(word);
         }
      }
        
      if(e.getSource()==dislikeButton) //plays sound or changes graphic depending on which button is clicked
      {
         
         womp.start(); //plays the audio //only works once and after it plays it changes the sentence. we gotta fix this (make a seperate method)
      }
      if(e.getSource()==likeButton)
      {
         //we can change the graphics (once i create them) for a few seconds when the like button is clicked
      }
   }

    //main methodset up the frame and UI
    //we stan youtube
    //https://www.youtube.com/watch?v=-IMys4PCkIA
   public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException
   {
      JFrame frame = new JFrame("Conspiracy Slot Machine");
      SlotMachine machine = new SlotMachine();
        
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//*************************************************************lowkey, don't know how this works*******************************************************************//
      frame.getContentPane().add(machine);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
   }
}
