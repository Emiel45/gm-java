package gmod.testing;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import gmod.events.InitializeEvent;
import gmod.events.MoveEvent;
import gmod.events.NPCKilledEvent;
import gmod.objects.Vector;

import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

public class Hooks {

	private ImageFrame imageFrame;
	private BufferedImage image;

	@Subscribe
	public void onInitialize(InitializeEvent e) {
		System.out.println("Gamemode has initialized!");

		this.image = new BufferedImage(720, 720, BufferedImage.TYPE_INT_RGB);
		this.imageFrame = new ImageFrame(image);
	}

	@Subscribe
	public void onNPCKilled(NPCKilledEvent e) {
		System.out.println(e.getKiller().getName() + " killed " + e.getVictim().getClassName() + " with a " + e.getWeapon().getClassName());

		/*
		 * Entity ent = e.getVictim(); Entity killer = e.getKiller();
		 * 
		 * Entity newEnt = Entities.create(ent.getClassName());
		 * System.out.println("Created new entity");
		 * 
		 * Vector newPos = new Vector(killer.getPos());
		 * System.out.println("Cloned pos");
		 * 
		 * newPos.add(new Vector(0, 0, 100));
		 * System.out.println("Added 100 z to pos");
		 * 
		 * System.out.println("Index of newEnt: " + newEnt.index());
		 * System.out.println("Index of newPos: " + newPos.index());
		 * 
		 * newEnt.setPos(newPos); System.out.println("Set pos");
		 * 
		 * Lua.dump_stack();
		 */
	}

	private Map<Integer, Integer[]> positions = Maps.newHashMap();
	private Map<Integer, Color> colors = Maps.newHashMap();

	private Random random = new Random();

	@Subscribe
	public void onMove(MoveEvent e) {
		try {
			Vector vPos = e.getPlayer().getPos();
			Integer[] pos = new Integer[] { (int) vPos.getX(), (int) vPos.getY() };

			int uid = e.getPlayer().getUniqueID();

			if (!colors.containsKey(uid)) {
				colors.put(uid, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
				positions.put(uid, pos);
			} else {
				Integer[] prevPos = positions.get(uid);
				if (prevPos[0] != pos[0] || prevPos[1] != pos[1]) {
					Graphics g = image.getGraphics();
					g.setColor(colors.get(uid));
					g.drawLine(500 + prevPos[0] / 4, 500 + prevPos[1] / 4, 500 + pos[0] / 4, 500 + pos[1] / 4);
					g.dispose();
					imageFrame.showBuffer();
					positions.put(uid, pos);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
