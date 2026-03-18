package main;

import robocode.*;

import java.awt.Color;

public class Robo extends Robot {
    
	enum Mover {
		UP, LEFT, RIGHT, DOWN;

		int peso;

		{ // logica para peso
			peso = 1;
		}
	}
	
    Mover[] movimentos = { Mover.UP, Mover.LEFT, Mover.RIGHT, Mover.DOWN };
    
    Mover ultMovimento = null;
    
    int sorteios = 0;
    
    boolean sort = true;

    public void run() {
    	
    	setGunColor(Color.PINK);
    	setRadarColor(Color.RED);

    	while (true) {

    		setBodyColor(
            	new Color(
        			(int) (Math.random() * 255),
        			(int) (Math.random() * 255),
        			(int) (Math.random() * 255)
            	)
            );

            sorteios++;

            if (sorteios >= 4) {
                resetarPesos();
                sorteios = 0;
                sort = true;
            }

            if (sort) {
            	Mover novoMovimento = movimentos[sortearMovimento()];

                if (novoMovimento != ultMovimento) {
                    resetarPesos();
                    sorteios = 0;
                }

                novoMovimento.peso += 2;

                ultMovimento = novoMovimento;

                sort = false;
            }

            switch (ultMovimento) {

            	case UP: {
	                turnRight(0);
	                ahead(150);
	            } break;

	            case LEFT: {
	                turnLeft(90);
	                ahead(150);
            	} break;

	            case RIGHT: {
	                turnRight(90);
	                ahead(150);
            	} break;

	            case DOWN: {
	                back(150);
	            } break;

	            default: break;

            }

        }

    }

    public void onHitWall(HitWallEvent e) { sort = true; }

    public void onHitByBullet(HitByBulletEvent e) { resetarPesos(); sort = true; }

    private int sortearMovimento() {
        int somaTotal = 0;

        for (Mover p : movimentos) {
        	somaTotal += p.peso;
        }

        double sorteio = Math.random() * somaTotal;
        double acumulado = 0;

        for (int i = 0; i < movimentos.length; i++) {
            acumulado += movimentos[i].peso;
            if (sorteio <= acumulado) {
            	return i;
            }
        }
        return 0;
    }

    private void resetarPesos() { for (int i = 0; i < movimentos.length; i++) movimentos[i].peso = 1; }

}
