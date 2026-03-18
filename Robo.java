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
	
    Mover[] pesos = { Mover.UP, Mover.DOWN, Mover.LEFT, Mover.RIGHT };
    
    int ultMovimento = -1, sorteios = 0;

    public void run() {
    	while (true) {
        	
    		setGunColor(Color.PINK);
        	setBodyColor(
            	new Color(
        			(int) (Math.random() * 255),
        			(int) (Math.random() * 255),
        			(int) (Math.random() * 255)
            	)
            );
            setRadarColor(Color.RED);

            int novoMovimento = sortearMovimento();

            if (novoMovimento != ultMovimento) {
                resetarPesos();
                sorteios = 0;
            }

            pesos[novoMovimento].peso += 2;
            
            ultMovimento = novoMovimento;
            
            sorteios++;

            if (sorteios >= 4) {
                resetarPesos();
                sorteios = 0;
            }
            
            switch (novoMovimento) {

            	case 0: {
	                ahead(150);
	                turnRight(0);
	            } break;

	            case 1: {
	                back(150);
	                turnRight(0);
	            } break;

	            case 2: {
	                ahead(150);
	                turnRight(45);
	            } break;

	            case 3: {
	                ahead(150);
	                turnLeft(45);
	            } break;

	        }

        }
    }

    public void onHitWall(HitWallEvent e) { resetarPesos(); }
    
    private int sortearMovimento() {
        int somaTotal = 0;

        for (Mover p : pesos) somaTotal += p.peso;

        double sorteio = Math.random() * somaTotal;

        double acumulado = 0;

        for (int i = 0; i < pesos.length; i++) {
            acumulado += pesos[i].peso;
            if (sorteio <= acumulado) return i;
        }
        return 0;
    }

    private void resetarPesos() { for (int i = 0; i < pesos.length; i++) pesos[i].peso = 1; }

}
