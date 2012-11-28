import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Stephen and Kyle
 *	Class containing the kinematics UI.
 */
@SuppressWarnings("serial")
public class Kinematics extends JPanel {

	public static final int LINK_LENGTH = 10;
	public static final int HIGHLIGHT_THRESHOLD = 6;
	
	boolean changingAngle = false;
	boolean movingEnd = false;
	boolean selectEnd = false;
	
	ChainBot bot;
	Joint highlight;
	Joint endJoint;

	public Kinematics(int linkNum) {
		this.setPreferredSize(new Dimension(512, 512));
		this.setBackground(Color.WHITE);
		
		this.bot = new ChainBot(linkNum);
		this.endJoint = this.bot.getEndJoint();
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				Vector2 mousePos = new Vector2(arg0.getX() - 256, 512 - arg0.getY());
				if (changingAngle) {
					Vector2 jointPos = highlight.pos;
					Vector2 dir = mousePos.sub(jointPos).normalize();
					double angle = Math.atan2(dir.y, dir.x);
					bot.rotateLinkAbsolute(highlight, angle);
					repaint();
				}
				else if (movingEnd){
					Vector3 ee = bot.getEndEffector();
					bot.moveEndEffector(mousePos.sub(new Vector2(ee.x, ee.y)));
					repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				Vector2 mousePos = new Vector2(arg0.getX() - 256, 512 - arg0.getY());
				if (!changingAngle) {
					highlight = bot.getNearestJoint((int) mousePos.x, (int) mousePos.y);
				}
				selectEnd = false;
				Vector3 ee3 = bot.getEndEffector();
				if (highlight == null && Point.distance(ee3.x, ee3.y, mousePos.x, mousePos.y) <= Kinematics.HIGHLIGHT_THRESHOLD) {
					selectEnd = true;
				}
				repaint();
			}
			
		});
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (highlight != null)
					changingAngle = true;
				if (selectEnd)
					movingEnd = true;
					
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				changingAngle = false;
				selectEnd = false;
				movingEnd = false;
				repaint();
			}
			
		});
	}
	
	@Override
	public void paint(Graphics gr) {
		super.paint(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setStroke(new BasicStroke(2));
		this.bot.draw(g);
		if (this.selectEnd) {
			g.setColor(Color.GREEN);
			Vector3 ee = bot.getEndEffector();
			g.drawRect(
				(int) (ee.x + 256 - Kinematics.HIGHLIGHT_THRESHOLD / 2),
				512 - (int) (ee.y + Kinematics.HIGHLIGHT_THRESHOLD / 2),
				Kinematics.HIGHLIGHT_THRESHOLD,
				Kinematics.HIGHLIGHT_THRESHOLD
			);
		}
		if (this.highlight != null) {
			g.setColor(Color.BLACK);
			g.drawOval(
					(int)this.highlight.pos.x + 256 - Kinematics.HIGHLIGHT_THRESHOLD / 2,
					512 - (int)this.highlight.pos.y - Kinematics.HIGHLIGHT_THRESHOLD / 2,
					Kinematics.HIGHLIGHT_THRESHOLD,
					Kinematics.HIGHLIGHT_THRESHOLD
			);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Kinematics");
		frame.setSize(512, 512);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new JScrollPane(new Kinematics(Integer.parseInt(args[0]))));
		
		frame.pack();
		frame.setVisible(true);
	}
}
