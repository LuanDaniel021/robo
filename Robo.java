package main;

import java.awt.Color;

import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.Robot;

public class Robo extends Robot {


	Movement[] movements = { Movement.UP, Movement.LEFT, Movement.RIGHT, Movement.DOWN };

	Movement last_move = null;

    int count_sort = 0;

    boolean sort = true;


    // ------------------------------
 // ------------------------------
    // ------------------------------
    	// ------------------------------		


    public void run() { Stages.map[0].accept(this); }


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
    public void onHitWall(HitWallEvent e) {
    	sort = true;
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
    	resetWeights();
    	sort = true;
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
    		Stages::random
    	};

    	static void random(Robo r) {

    		r.setGunColor(Color.PINK);
    		r.setRadarColor(Color.RED);

    		while (true) {

        		r.setBodyColor(
        			new Color(
                		(int) (Math.random() * 255),
            			(int) (Math.random() * 255),
            			(int) (Math.random() * 255)
                	)
                );

                r.count_sort++;

                if (r.count_sort >= 4) {
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
                		r.turnRight(0);
                		r.ahead(150);
    	            } break;

    	            case LEFT: {
    	            	r.turnLeft(90);
    	            	r.ahead(150);
                	} break;

    	            case RIGHT: {
    	            	r.turnRight(90);
    	            	r.ahead(150);
                	} break;

    	            case DOWN: {
    	            	r.back(150);
    	            } break;

    	            default: break;

                }

            }

    	}

    }

    @FunctionalInterface private interface Consumer { void accept(Robo robo); }

}
