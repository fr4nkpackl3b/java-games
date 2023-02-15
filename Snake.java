import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.awt.*;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Random;
import javax.swing.*;

public class Game extends JFrame {
	public enum Direction {
		up, down, left, right;
	}
	private int height=20,width=20,delay=100,lengthInit=5,lengthAdd=3,appleCount=1,sizeIcon=20;

	private final static String START_KEY = "Press any key to start.";
	private final static String SCORE_COUNT = "score: ";
	private final static String RESTART = "Game Over. Play again?:";

	private ImageIcon FieldIcon, SnakeIcon, appleIcon;
	private JPanel base;
	private JLabel[][] field;
	private JLabel scores;


	private LinkedList<int[]> snake;
	private Timer clock;
	private Direction dir, Lastdir;
	private Random rand;
	private int score, delWait;
	private boolean finish;

	public static void main(String args[]){
		JFrame snake = new Game("Snake");
		snake.setVisible(true);
		snake.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Game(String s){
		super(s);
		setLayout(new BorderLayout());
		addKeyListener(new DirectionListener());
		FieldIcon=createIcon(Color.BLACK);
		SnakeIcon=createIcon(Color.WHITE);
		appleIcon=createIcon(Color.RED);

		rand=new Random();
		scores=new JLabel();
		add(scores, BorderLayout.NORTH);
		clock=new Timer(delay, gLoop);
		
		setup();
	}

	private ImageIcon createIcon(Color color){
		BufferedImage img=new BufferedImage(sizeIcon, sizeIcon, BufferedImage.TYPE_INT_RGB);
		Graphics2D g=img.createGraphics();
		g.setPaint(color);
		g.fillRect(0,0,sizeIcon,sizeIcon);
		return new ImageIcon(img);
	}

	private void setup(){
		initGUI();
		initSnake();
	}

	private void initGUI(){
		base=new JPanel(new GridLayout(height, width));
		field=new JLabel[height][width];
		for(int a=0;a<height;a++)
			for(int j=0;j<width;j++)
				base.add(field[a][j]=new JLabel(FieldIcon));
		scores.setText(START_KEY);
		add(base, BorderLayout.CENTER);
		pack();
	}

	private void initSnake(){
		snake = new LinkedList<int[]>();
		for(int a=0;a<lengthInit;a++){
			add(new int[] { height/2, width/2 - lengthInit/2+a});
		}
		for(int a=0;a<appleCount;a++)
			addFood(a);
		Lastdir = dir = Direction.right;
		delWait = score = 0;
		finish = false;
		clock.setDelay(delay);
	}

	private void add(int[] pos){
		snake.add(pos);
		field[pos[0]][pos[1]].setIcon(SnakeIcon);
	}

	private void remove(){
		if(delWait>0)
			delWait--;
		else{
			int tail[]=snake.remove();
			field[tail[0]][tail[1]].setIcon(FieldIcon);
		}
	}

	private void addFood(){
		addFood(appleCount);
	}

	private void addFood(int food){
		if(food == appleCount)
			scores.setText("Score: "+ ++score);
		int whiteCount=height*width-snake.size()-food;
		if(whiteCount<=0)
			return;
		int appleIndex = rand.nextInt(whiteCount) + 1;
		whiteCount = 0;
			for(JLabel[] a : field)
				for(JLabel j : a){
					whiteCount += j.getIcon()==FieldIcon ? 1 : 0;
					if(whiteCount == appleIndex){
						j.setIcon(appleIcon);
						return;
					}
				}
	}

	public boolean isFood(int[] pos){
		return field[pos[0]][pos[1]].getIcon() == appleIcon;
	}
	
	public boolean isSnake(int[] pos){
		return(!(pos[0] == snake.getFirst()[0] && pos[1] == snake.getFirst()[1]) || delWait>0) 
				&& field[pos[0]][pos[1]].getIcon() == SnakeIcon;
	}

	ActionListener gLoop = new ActionListener(){
		public void actionPerformed(ActionEvent e){
			int[] head = Arrays.copyOf(snake.getLast(), 2);
			switch(dir){
				case up:
					head[0] -= 1;
					break;
				case down:
					head[0] += 1;
					break;
				case left:
					head[1] -= 1;
					break;
				case right:
					head[1] += 1;
					break;
			}
			Lastdir = dir;
			head[0] = (head[0]+height)%height;
			head[1] = (head[1]+width)%width;

			if(isSnake(head)){
				clock.stop();
				finish=true;
				if(snake.size() == height * width)
						System.exit(0);
						switch(JOptionPane.showConfirmDialog(Game.this,RESTART)){
							case JOptionPane.YES_OPTION:
								setup();
								break;
							case JOptionPane.NO_OPTION:
								System.exit(0);
								break;
						}
			}
			else{
				if(isFood(head)){
					delWait += lengthAdd;
					addFood();
				}
				remove();
				add(head);
			}
		}
	};
	
	private class DirectionListener implements KeyListener{

		public void keyPressed(KeyEvent e){
			boolean skip = false;
			switch(e.getKeyCode()){
				case KeyEvent.VK_UP:
					case KeyEvent.VK_NUMPAD8:
					case KeyEvent.VK_W:
					if(Lastdir != Direction.down)
						dir = Direction.up;
					break;
					case KeyEvent.VK_DOWN:
					case KeyEvent.VK_NUMPAD2:
					case KeyEvent.VK_S:
					if(Lastdir != Direction.up)
						dir = Direction.down;
					break;
					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_NUMPAD4:
					case KeyEvent.VK_A:
					if(Lastdir != Direction.right)
						dir = Direction.left;
					break;
					case KeyEvent.VK_RIGHT:
					case KeyEvent.VK_NUMPAD6:
					case KeyEvent.VK_D:
					if(Lastdir != Direction.left)
						dir = Direction.right;
					break;
					case KeyEvent.VK_R:
					setup();

					case KeyEvent.VK_P:
					clock.stop();
					skip = true;
					break;
					case KeyEvent.VK_ESCAPE:
					System.exit(0);
					break;
			}
			if (!(clock.isRunning() || finish || skip)) {
				clock.start();
				scores.setText(SCORE_COUNT + score);
			}
		}
		
		public void keyTyped(KeyEvent e){
		}
		public void keyReleased(KeyEvent e){
		}
	}
}
