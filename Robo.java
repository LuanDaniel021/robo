package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class Robo extends Robot {


	final List<Movement> movements = new ArrayList<>();

    double enemyBearing;
    double enemyDistance;
    double enemyHeading;
    double enemyVelocity;

    boolean onHitByBullet;
    boolean onHitByRobot;
    boolean onHitWall;
    boolean hasTarget;

    int direction;
    int current;
    int count;
    int steps;


    // ------------------------------
 // ------------------------------
    // ------------------------------
    	// ------------------------------


    @Override
    public void run() {
    	setGunColor(Color.PINK); setRadarColor(Color.RED);

    	movements.add(Movement.UP);
		movements.add(Movement.LEFT);
		movements.add(Movement.RIGHT);

		current = Stage.TESTE;

		direction = 1;

		while (true) {
    		setBodyColor(
    			new Color(
            		(int) (Math.random() * 255),
        			(int) (Math.random() * 255),
        			(int) (Math.random() * 255)
            	)
    		);

    		Stage.map[current].accept(this);

    		steps++;
    	}

    }


    // ------------------------------
 // ------------------------------
    // ------------------------------
    	// ------------------------------


    void reset() {
    	movements.clear();

    	enemyBearing = 0;
    	enemyDistance = 0;
    	enemyHeading = 0;
    	enemyVelocity = 0;

    	onHitByBullet = false;
        onHitByRobot = false;
        onHitWall = false;
        hasTarget = false;

        direction = -1;
        current = -1;
        count = 0;
        steps = 0;
    }

    public Movement sort( List<Movement> movements ) {
    	Movement move = movements.get(0);

    	int sum = 0;

        for (Movement p : movements) {
        	if (p.weight > move.weight ) {
        		move = p;
        	}
        	sum += p.weight;
        }

        if ( count < 5 ) count++;
        else {
        	Collections.shuffle( movements );
        	move.weight = Movement.DEFAULT;
        	count = 0;
        } 

        double sort = Math.random() * sum;
    	double acc = 0;

        for (int i = 0; i < movements.size(); i++) {
        	acc += movements.get(i).weight;
        	if (sort <= acc) {
        		move = movements.get(i);
        		break;
            }
        }

        return move;
    }


    // ------------------------------
 // ------------------------------
    // ------------------------------
    	// ------------------------------    


    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
    	hasTarget = true;
        enemyBearing = e.getBearing();
        enemyDistance = e.getDistance();
        enemyHeading = e.getHeading();
        enemyVelocity = e.getVelocity();
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
    	onHitByBullet = true;
    }  

    @Override
    public void onHitRobot(HitRobotEvent event) {
    	onHitByRobot = true;
    }

    @Override
    public void onHitWall(HitWallEvent e) {
    	onHitWall = true;
    }

    @Override
    public void onRobotDeath(RobotDeathEvent e) {
        hasTarget = false;
    }


    // ------------------------------
// ------------------------------
	// ------------------------------
       	// ------------------------------


    enum Movement { UP, DOWN, LEFT, RIGHT; static int DEFAULT = 1; int weight = 1; }

    static class Stage {

    	static Consumer[] map = {
    		Stage::random,
    		Stage::evade,
    		Stage::tracker,
    		Stage::tracker_C,
    	};

    	static final int TESTE = map.length - 1;

    	static final int RANDOM  = 0;
    	static final int EVADE   = 1;
    	static final int TRACKER = 2;

    	static void random(Robo r) {

    		if ( r.hasTarget ) {
    			r.turnGunRight( r.getHeading() - r.getGunHeading() + r.enemyBearing );

    			r.fire( 1 );

    			r.hasTarget = false;
    		}

    		if ( r.onHitWall ) {

    			r.back( 50 );

    			if ( !r.onHitByBullet && !r.onHitByBullet ) {

    				r.count = 5;

    			} 

    			r.onHitByBullet = false;
        		r.onHitByRobot = false;
    			r.onHitWall = false;

    		}

    		switch ( r.sort( r.movements ) ) {

    			case UP: {
					r.ahead( 45 );
				} break;

				case LEFT: {
					r.turnLeft( 45 );
				} break;

				case RIGHT: {
					r.turnRight( 45 );
				} break;

				default: break;

    		}

    	}

    	static void evade(Robo r) {

    		if ( r.steps > 2 ) {

            	r.current = 1;

            	r.steps = 0;

            	r.direction = -1;

            	r.hasTarget = false;

            	return;

            }

    		r.steps++;

    		if ( r.onHitWall ) {

    			r.turnLeft(20);

    			r.onHitWall = false;

    		} else {

    			r.turnRight(25);	

    			r.ahead(100);

    		}

    	}

    	static void tracker(Robo r) {

    		if ( r.hasTarget ) {

    			r.hasTarget = false;

    			r.turnRight( r.getHeading() * r.direction);

    			if (!r.hasTarget) {

    				r.direction = r.direction < 1 ? 1 : -1;

    			} else {

    				r.fire( r.enemyDistance > 200 ? 1 : 3 );

    			}

    			if ( r.enemyDistance > 200  ) {

    				r.ahead(50);	

    			} else {

    				r.back(25);

    			}

    		}

    		r.turnRight(15 * r.direction);

    	}
    	
    	static void tracker_B(Robo r) {

    		if ( r.hasTarget ) {

    			r.hasTarget = false;

    			r.direction = r.enemyBearing > 0 ? 1 : -1;

    			if ( r.enemyDistance > 200  ) {

    				r.ahead(50);	

    			} else {

    				r.back(25);

    			}

    			r.fire( r.enemyDistance > 200 ? 1 : 3 );

    		}

    		r.turnRight(10 * r.direction);

    	}

    	static void tracker_C(Robo r) {

    		if ( r.hasTarget ) {

    			r.hasTarget = false;

    			r.direction = r.enemyBearing > 0 ? 1 : -1;

    			double diff = (r.getHeading() - r.getGunHeading() + r.enemyBearing) + r.enemyVelocity;

    			if ( r.enemyDistance < 300 ) {
    				r.turnGunRight( diff );
        			r.fire( r.enemyDistance > 150 ? 1 : 3 );
        			r.turnGunLeft( diff );	
    			}

    			if ( r.enemyDistance > 200  ) {

    				r.ahead(45);

    			} else {

    				r.back(25);

    			}

    		}

    		r.turnRight(10 * r.direction);

    	}

    	static void tracker_D(Robo r) {

    		if ( r.hasTarget ) {

    			r.hasTarget = false;

    			r.direction = r.enemyBearing > 0 ? 1 : -1;

    			double diff = (r.getHeading() - r.getGunHeading() + r.enemyBearing) + r.enemyVelocity;

    			r.turnGunRight( diff );

    			r.fire( r.enemyDistance > 150 ? 1 : 3 );

    			r.ahead(50);

    			r.turnGunLeft( diff );

    		}

    		r.turnRight(15 * r.direction);

    	}

    }

    @FunctionalInterface private interface Consumer { void accept(Robo robo); }

}
