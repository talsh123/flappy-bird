package container;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import elements.Board;

@SuppressWarnings("serial")
public class FlappyBird extends JFrame{
	
	private final int B_WIDTH = 1280;
	private final int B_HEIGHT = 720;
	
	public FlappyBird() {
		initUI();
	}
	
	private void initUI() {
		this.add(new Board());
		
		this.setResizable(false);
		pack();
		
		this.setTitle("Flappy Bird");
		this.setLocationRelativeTo(null);
		this.setSize(this.B_WIDTH, this.B_HEIGHT);
		this.setVisible(true);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
 
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			JFrame frame = new FlappyBird();
			frame.setVisible(true);
		});
	}

}
