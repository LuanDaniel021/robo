package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

public class Robo extends Robot {


    // ------------------------------
 // ------------------------------
    // ------------------------------
    	// ------------------------------


	final List<Movement> movements = new ArrayList<>();

	String enemyName;

    double enemyBearing;
    double enemyDistance;
    double enemyHeading;

    boolean onHitByBullet;
    boolean onHitByRobot;
    boolean onHitWall;
    boolean hasTarget;
    boolean runing;

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

		current = Stage.RAMFIRE;
		direction = 1;

		runing = true;

		while (runing) {

			setBodyColor(
    			new Color(
            		(int) (Math.random() * 255),
        			(int) (Math.random() * 255),
        			(int) (Math.random() * 255)
            	)
    		);
			
			if ( getEnergy() < 35) Stage.map[Stage.RAGEBOT].accept(this);
    		else {
    			Stage.map[current].accept(this);

    			steps++;

    			if ( Math.random() < 0.001 ) {
    				Comment.dev();
    			}
    		}

		}
    }


    // ------------------------------
 // ------------------------------
    // ------------------------------
    	// ------------------------------


    public Movement sort( List<Movement> movements ) {
    	Movement move = movements.get(0);

    	double acc = 0;
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
        enemyName = e.getName();
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
        if ( enemyName.equals( e.getName() ) ) {
        	//Comment.kill();
        	enemyName = "";
        	hasTarget = false;
        }
    }
    
    @Override
    public void onDeath(DeathEvent event) {
    	Comment.death();
    }
    
    @Override
    public void onWin(WinEvent event) {
    	Comment.victory();
    }


    // ------------------------------
// ------------------------------
	// ------------------------------
       	// ------------------------------


    enum Movement { UP, DOWN, LEFT, RIGHT; static int DEFAULT = 1; int weight = 1; }

    static class Stage {

    	static Consumer[] map = {
    		Stage::random,
    		Stage::ramfire,
    		Stage::tracker,
    		Stage::ragebot,
    		Stage::trackfire,
    		Stage::teste,
    	};

    	static final int TESTE = map.length - 1;

    	static final int RANDOM    = 0;
    	static final int RAMFIRE   = 1;
    	static final int TRACKER   = 2;
    	static final int RAGEBOT   = 3;
    	static final int TRACKFIRE = 4;    	
    	static final int EVADE     = 5;

    	static void random(Robo r) {
    		if ( r.steps > 25 ) r.current = RAMFIRE;
    		else {
    			if ( r.hasTarget ) {
	    			r.turnGunRight( r.getHeading() - r.getGunHeading() + r.enemyBearing );

	    			r.fire( 1 );

	    			r.hasTarget = false;
	    		}

	    		if ( r.onHitWall ) {

	    			if ( !r.onHitByRobot && !r.onHitByBullet ) {
	    				r.count = 5;
	    			}

	    			r.back( 50 );

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
    	}

    	static void ramfire(Robo r) {
    		if ( r.getOthers() < 8 ) r.current = TRACKER;
    		else {
    			if ( !r.hasTarget ) r.turnRight(10 * r.direction);
    			else {
    				if ( r.enemyDistance > 300 ) r.turnRight(10 * r.direction);
    				else {
    					r.direction = r.enemyBearing > 0 ? 1 : -1;

        				double diff = (r.getHeading() - r.getGunHeading() + r.enemyBearing);

        				if (r.enemyDistance < 80) r.fire(3);

        				else {
        					r.turnGunRight( diff );

            				r.fire( 3 );

            				r.ahead(45);

            				r.turnGunLeft( diff );
        				}
    				}
    				r.hasTarget = false;
    				r.scan();
        		}
    		}
    	}

    	static void tracker(Robo r) {
    		if ( r.getOthers() < 4 ) r.current = Stage.TRACKFIRE;
    		else {
	    		if ( !r.hasTarget ) r.turnRight(15 * r.direction);
	    		else {
	    			r.direction = r.enemyBearing > 0 ? 1 : -1;

	    			double diff = (r.getHeading() - r.getGunHeading() + r.enemyBearing);

	    			if ( r.enemyDistance > 300 ) r.turnRight( diff );
	    			else {

	    				if (r.enemyDistance < 80) r.fire(3);
	    				else {
	    					r.turnRight( diff );

	        				r.fire( r.enemyDistance > 200 ? 2 : 3 );
	    				}
	    			}

	    			if ( r.enemyDistance > 200  ) r.ahead(40);
	    			else {
	    				r.back(25);
	    			}

	    			r.hasTarget = false;
	    			r.scan();
	    		}
	    	}

    	}

    	static void ragebot(Robo r) {
    		if (r.getHeading() - r.getGunHeading() != 0) {
    			r.turnGunRight( r.getHeading() );
    		}
			if ( !r.hasTarget ) r.turnRight(15 * r.direction); 
			else {
				r.direction = r.enemyBearing > 0 ? 1 : -1;

				if (r.enemyDistance < 50) r.fire(3);
				else {

					r.turnRight( r.getHeading() - r.getGunHeading() + r.enemyBearing );

    				r.fire( r.enemyDistance < 200 ? 3 : 2 );

    				r.ahead(45);

    				r.hasTarget = false;
				}
    		}
			r.scan();
    	}

    	static void trackfire(Robo r) {
    		if ( !r.hasTarget ) r.turnGunRight( 15 * r.direction );
    		else {

    			r.direction = r.enemyBearing > 0 ? 1 : -1;

    			r.turnGunRight( r.getHeading() - r.getGunHeading() + r.enemyBearing );

    			do {
    				r.fire( 3 );
    				r.hasTarget = false;
    				r.scan();
    			} while ( r.hasTarget && r.getEnergy() > 50);

    		}
    	}

    	static void teste(Robo r) {}

    }

    static class Comment {

	   private static final String[][] COMMENTS = {
			   { // DEV
				   "// isso funciona, não mexe",
				   "// gambiarra temporária (permanente)",
				   "// por que isso funciona?",
				   "// não faço ideia do que estou fazendo",
				   "// magia negra abaixo",
				   "// não encoste nisso",
				   "// se quebrar, culpa do usuário",
				   "// funciona em produção, tá ótimo",
				   "// otimizar depois",
				   "// aqui mora o perigo",
				   "// não sei, mas funciona",
				   "// hack rápido, resolve depois",
				   "// quem escreveu isso foi um gênio ou um louco",
				   "// isso deveria ser ilegal",
				   "// talvez isso ajude",
				   "// remove isso e tudo quebra",
				   "// precisa de mais café",
				   "// não documentado por motivos óbvios",
				   "// se chegou aqui, já deu ruim",
			   },
			   { // KILL
				   "Menos outro.",
				   "Obrigado pelo loot.",
				   "Sistema inferior detectado.",
				   "Achei fácil.",
				   "Desinstalado com sucesso.",
				   "Menos um processo rodando.",
				   "Latency 0, precisão 100.",
				   "Você tentou.",
				   "Execução concluída.",
				   "Alvo neutralizado.",
				   "Foi mal, era teste A/B.",
				   "Bug corrigido.",
				   "Stack trace: você morreu.",
				   "Press F… ah não, já foi.",
				   "Isso não foi pessoal!",
				   "Caiu no if errado.",
				   "Garbage collected.",
				   "Faltou um null check aí.",
				   "Código limpo, inimigo não.",
				   "Versão 1.0: dominante.",
			   },
			   { // DEATH
				   "Ok… isso não era pra acontecer.",
				   "Quem deixou esse bot rodar?",
				   "Morri...",
				   "Claramente lag.",
				   "Ta bugado ali ó.",
				   "Preciso de mais logs.",
				   "Isso vai pro backlog.",
				   "Teste falhou com sucesso.",
				   "Alguém mexeu no código.",
				   "Nota mental: não fazer isso.",
				   "Stack overflow de vergonha.",
				   "Po era zoeira.",
				   "Chama o ADM!",
				   "Vou fingir que foi estratégia.",
				   "Isso não escalou bem.",
				   "Inimigo usou hack, certeza.",
				   "Quem escreveu isso?",
				   "Ok, isso ficou feio.",
				   "Pausa pro café.",
				   "Reiniciando dignidade...",
			   },
			   { // VICTORY
				   "GG EZ.",
				   "Apaga a luz que eu fui o último.",
				   "Só tinha bot fácil?",
				   "Nem aqueci ainda.",
				   "Como que sai do tutorial?",
				   "Skill issue de vocês.",
				   "Fui nerfado e mesmo assim ganhei.",
				   "Alguém chama um adulto.",
				   "Meu código rodou, vocês não.",
				   "Era pra ser difícil?",
				   "Lagou pra vocês também?",
				   "Instalei vitória.exe",
				   "Respawn cancelado com sucesso.",
				   "Clipa isso!",
				   "Mais um dia normal no servidor.",
				   "Se isso é desafio, eu sou compilador.",
				   "Tá pago.",
				   "Eu nem mirei direito.",
				   "Top 1 sem esforço.",
				   "Chora mais.",
			   },
	   };

	   final static int DEV     = 0;
	   final static int KILL    = 1;
	   final static int DEATH   = 2;
	   final static int VICTORY = 3;

	   static String dev() {
		   return println( random(DEV) );
	   }

	   static String death() {
		   return println( random(DEATH) );
	   }

	   static String kill() {
		   return println( random(KILL) );
	   }

	   static String victory() {
		   return println( random(VICTORY) );
	   }

	   static String dev( int col ) {
		   return println( DEV, col );
	   }

	   static String death( int col ) {
		   return println( DEATH, col );
	   }

	   static String kill( int col ) {
		   return println( KILL, col );
	   }

	   static String victory( int col ) {
		   return println( VICTORY, col );
	   }

	   static String println( String str ) {
		   System.out.println( str );
		   return str;
	   }

	   static String println( int row, int col ) { return println( COMMENTS[row][col] ); }

	   static String random( int idx ) { return COMMENTS[idx][(int)(Math.random() * (COMMENTS[idx].length))]; }

    }

    @FunctionalInterface private interface Consumer { void accept(Robo robo); }

}
