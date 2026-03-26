package main;

import java.awt.Color;

import javax.xml.stream.events.EndElement;

import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class Robo extends Robot {


	Movement[] movements = { Movement.UP, Movement.LEFT, Movement.RIGHT, Movement.DOWN };

	Movement last_move = null;

    int count_sort = 0;

    double enemyBearing;
    double enemyDistance;
    double enemyHeading;
    double enemyVelocity;
    boolean hasTarget = false;
    boolean sort = true;
    
    double targetDistance = 200;
    int direction = 1;
    
    int current = 0;

    {
    	
    }

    // ------------------------------
 // ------------------------------
    // ------------------------------
    	// ------------------------------		


    public void run() {
    	setGunColor(Color.PINK); setRadarColor(Color.RED);
    	
		while (true) {
    		setBodyColor(
    			new Color(
            		(int) (Math.random() * 255),
        			(int) (Math.random() * 255),
        			(int) (Math.random() * 255)
            	)
    		);
    		
    		Stages.map[current].accept(this);
    	}

    }


    // ------------------------------
 // ------------------------------
    // ------------------------------
    	// ------------------------------


    private void resetWeights() { for (int i = 0; i < movements.length; i++) movements[i].weight = 1; }

    private int sortMovement() {
        int soma = 0;

        for (Movement p : movements) {
        	soma += p.weight;
        }

        double sorteio = Math.random() * soma;

        double acumulado = 0;

        for (int i = 0; i < movements.length; i++) {

        	acumulado += movements[i].weight;

        	if (sorteio <= acumulado) {
            	return i;
            }

        }

        return 0;
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
    public void onHitWall(HitWallEvent e) {
    	sort = true;
    	back(50);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) { back(25); }    

    @Override
    public void onRobotDeath(RobotDeathEvent e) {
        hasTarget = false;
    }

    // ------------------------------
// ------------------------------
	// ------------------------------
       	// ------------------------------


    private enum Movement {

    	UP, LEFT, RIGHT, DOWN;

		int weight;

		{
			weight = 1;
		}

	}

    private static class Stages {

    	static Consumer[] map = {
    		Stages::stalker,
    		Stages::random
    	};

    	static void random(Robo r) {

    		if (r.hasTarget) {
    			r.fire( r.enemyDistance > 200 ? 1 : 3 );
    			r.hasTarget = false;
    		}

            r.count_sort++;

            if (r.count_sort >= 6) {
            	r.resetWeights();
            	r.count_sort = 0;
            	r.sort = true;
            }

            if (r.sort) {
            	Movement move = r.movements[r.sortMovement()];

                if (move != r.last_move) {
                	r.resetWeights();
                	r.count_sort = 0;
                }

                move.weight += 2;

                r.last_move = move;

                r.sort = false;
            }

            switch (r.last_move) {

            	case UP: {
            		r.ahead(50);
	            } break;

	            case LEFT: {
	            	r.turnLeft(45);
	            	r.ahead(50);
            	} break;

	            case RIGHT: {
	            	r.turnRight(45);
	            	r.ahead(50);
            	} break;

	            case DOWN: {
	            	r.back(50);
	            } break;

            }

    	}
    	
    	static void stalker(Robo r) {

    		if (r.hasTarget) {

    			r.fire( r.enemyDistance > 200 ? 1 : 3 );

    			r.hasTarget = false;

    			r.turnRight(10 * r.direction);

    			if (!r.hasTarget) {

    				r.direction = r.direction < 1 ? 1 : -1;

    			}

    			if ( r.enemyDistance > 200  ) {

    				r.ahead(50);	

    			}

    		}

    		r.turnRight(10 * r.direction);

    	}

    }

    @FunctionalInterface private interface Consumer { void accept(Robo robo); }

}
