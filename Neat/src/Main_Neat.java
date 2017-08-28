
import javax.swing.*;

public class Main_Neat {
    public static int WIDTH = 500, HEIGHT = 500;
	public static Game game;
	
	public static void main(String[] args){
		runFlappyBirds();
	}
	
    public static void runFlappyBirds(){
        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);

        Keyboard keyboard = Keyboard.getInstance();
        frame.addKeyListener(keyboard);
        
        GamePanel panel = new GamePanel();
        game = panel.getGame();
        frame.add(panel);
    }
}
