package gmod.testing;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class ImageFrame extends JFrame {
	
	private BufferedImage image;
	private BufferStrategy buffer;
	
	private JPanel contentPane;
	
	public ImageFrame(BufferedImage image) {
		this.image = image;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(image.getWidth(), image.getHeight());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		Canvas canvas = new Canvas();
		getContentPane().add(canvas);
		
		super.setVisible(true);
		
		canvas.createBufferStrategy(2);
		this.buffer = canvas.getBufferStrategy();
	}

	public void showBuffer() {
		Graphics g = buffer.getDrawGraphics();
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
		buffer.show();
	}

}
