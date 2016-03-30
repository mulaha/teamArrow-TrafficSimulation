/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CellAutomaton;

/**
 *
 * @author Mohammad Ali
 */
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;

public class RoadEnvironment implements ActionListener, KeyListener {

    public static RoadEnvironment re;
    public JFrame mainFrame;
    public DisplayWindow dw;
    public Timer timer = new Timer(50, this);

//    public ArrayList<Point> carSet = new ArrayList<Point>();
//    public ArrayList<Point> carSet2 = new ArrayList<Point>();
    public Point skyline, veyron, ferrari;

    public boolean paused = false;
    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;// do not alter
    public static final int SCALE = 5;
    public int direction;
    public int carLength = 2;

    OneWayRoad northRoadU = new OneWayRoad(3, 50, 50, 0, UP, null);
    OneWayRoad northRoadD = new OneWayRoad(3, 50, 53, 0, DOWN, null);

    OneWayRoad eastRoadR = new OneWayRoad(50, 3, 56, 50, RIGHT, null);
    OneWayRoad eastRoadL = new OneWayRoad(50, 3, 56, 53, LEFT, null);

    OneWayRoad southRoadU = new OneWayRoad(3, 50, 50, 56, UP, null);
    OneWayRoad southRoadD = new OneWayRoad(3, 50, 53, 56, DOWN, null);

    OneWayRoad westRoadR = new OneWayRoad(50, 3, 0, 50, RIGHT, null);
    OneWayRoad westRoadL = new OneWayRoad(50, 3, 0, 53, LEFT, null);

    ArrayList<OneWayRoad> roadArray = new ArrayList<>();//list of all the roads in the network
    ArrayList<Junction> junctArray = new ArrayList<>();//list of all the junctions in the network
    ArrayList<OneWayRoad> networkEntr = new ArrayList<>();//list of roads where cars spawn into the network
    ArrayList<OneWayRoad> networkExit = new ArrayList<>();//list of roads where cars exit from the network

    OneWayRoad[] crossroadEntr = new OneWayRoad[4];
    OneWayRoad[] crossroadExit = new OneWayRoad[4];

    Junction crossroad; 
    AutomatonModel model;
    private int stopCounter;

    public RoadEnvironment() {
        crossroad = new Junction(50, 50, crossroadExit, crossroadEntr, 6, 6);

        //set junction exits
        crossroadExit[UP] = northRoadD;
        crossroadExit[DOWN] = southRoadD;
        crossroadExit[LEFT] = westRoadL;
        crossroadExit[RIGHT] = eastRoadR;

        //set junction entrances
        crossroadEntr[UP] = southRoadU;
        crossroadEntr[DOWN] = northRoadD;
        crossroadEntr[LEFT] = westRoadR;
        crossroadEntr[RIGHT] = eastRoadL;

        //populate junction list
        junctArray.add(crossroad);

        //set road-junction connections
        westRoadR.setExit(crossroad);
        eastRoadL.setExit(crossroad);

        //set traffic light switches
        northRoadU.setStopLight(true);
        northRoadD.setStopLight(true);
        southRoadU.setStopLight(true);
        southRoadD.setStopLight(true);
//        westRoadR.setStopLight(true);

        crossroad.setXTravel(true);

        //populate road list
        roadArray.add(northRoadU);
        roadArray.add(northRoadD);
        roadArray.add(eastRoadR);
        roadArray.add(eastRoadL);
        roadArray.add(southRoadU);
        roadArray.add(southRoadD);
        roadArray.add(westRoadR);
        roadArray.add(westRoadL);

        //populate network entrance list
        networkEntr.add(northRoadD);
        networkEntr.add(eastRoadL);
        networkEntr.add(southRoadU);
        networkEntr.add(westRoadR);

        //populate network exit list
        networkExit.add(northRoadU);
        networkExit.add(eastRoadR);
        networkExit.add(southRoadD);
        networkExit.add(westRoadL);

        model = new AutomatonModel(networkExit);
//        
        stopCounter = 0;

        mainFrame = new JFrame("Traffic Simulation App.");
        mainFrame.setVisible(true);
        mainFrame.setSize(800, 800);
        mainFrame.setResizable(false);
        mainFrame.add(dw = new DisplayWindow());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addKeyListener(this);
        start();
    }

    public void updateRoads() {
        // To demonstrate stoplights, will be removed later

        boolean stopSwitch = false;
        if (stopCounter > 50) {
            stopSwitch = true;
            stopCounter = 0;
        }

        for (OneWayRoad road : roadArray) {

            if (stopSwitch) {
                model.switchLightRoad(road);
                crossroad.switchXTravel();
            }
        }
        for (OneWayRoad road : roadArray) {
            int direction = road.getDirection();
            model.resetCarsChk(road);

            switch (direction) {
                case RIGHT:
                    model.updateCarsRight(road);
                    break;

                case LEFT:
                    model.updateCarsLeft(road);
                    break;

                case UP:
                    model.updateCarsUp(road);
                    break;

                case DOWN:
                    model.updateCarsDown(road);
                    break;
            }
            model.resetCarsChk(road);
        }

        for (OneWayRoad road : networkEntr) {
            model.addCar(road);
        }

        for (Junction junct : junctArray) {
            model.updateJunct(junct);
        }
    }

    public void start() {
//        direction = DOWN;
//        skyline = new Point(0, -1);
//        veyron = new Point(2, -1);
//        ferrari = new Point(3, -1);
//        carSet.clear();
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        updateRoads();
        dw.repaint();
        stopCounter++;

//        if (skyline != null && veyron != null && !paused) {
//            carSet.add(new Point(skyline.x, skyline.y));
//            carSet2.add(new Point(veyron.x, veyron.y));
//
//            if (direction == UP) {
//                skyline = new Point(skyline.x, skyline.y - 1);
//                veyron = new Point(veyron.x, veyron.y - 1);
//
//            }
//            if (direction == DOWN) {
//                skyline = new Point(skyline.x, skyline.y + 1);
//                veyron = new Point(veyron.x, veyron.y + 1);
//            }
//            if (direction == LEFT) {
//                skyline = new Point(skyline.x - 1, skyline.y);
//                veyron = new Point(veyron.x - 1, veyron.y);
//            }
//            if (direction == RIGHT) {
//                skyline = new Point(skyline.x + 1, skyline.y);
//                veyron = new Point(veyron.x + 1, veyron.y);
//            }
//            if (carSet.size() > carLength) {
//                carSet.remove(0);
//            }
//            if (carSet2.size() > carLength) {
//                carSet2.remove(0);
//            }
//        }
    }

//    public boolean noTailAt(int x, int y) {
//        for (Point point : carSet) {
//            if (point.equals(new Point(x, y))) {
//                return false;
//            }
//        }
//        for (Point point : carSet2) {
//            if (point.equals(new Point(x, y))) {
//                return false;
//            }
//        }
//        return true;
//    }
    public static void main(String[] args) {
        re = new RoadEnvironment();
    }

    @Override
    public void keyPressed(KeyEvent e) {
//		int i = e.getKeyCode();
//
//		if ((i == KeyEvent.VK_A || i == KeyEvent.VK_LEFT) && direction != RIGHT)
//			direction = LEFT;
//		if ((i == KeyEvent.VK_D || i == KeyEvent.VK_RIGHT) && direction != LEFT)
//			direction = RIGHT;
//		if ((i == KeyEvent.VK_W || i == KeyEvent.VK_UP) && direction != DOWN)
//			direction = UP;
//		if ((i == KeyEvent.VK_S || i == KeyEvent.VK_DOWN) && direction != UP)
//			direction = DOWN;
//		if (i == KeyEvent.VK_SPACE)
//			start();
//		if(i== KeyEvent.VK_ENTER)
//			paused = !paused;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}