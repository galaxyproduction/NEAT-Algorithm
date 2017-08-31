import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Game {

    public static final int PIPE_DELAY = 100;
    public static int POPSIZE = 200; //Amount of network and birds
    
    private Neat neat = new Neat(POPSIZE, 4, 2);//Initializes neat
    private float fitness;//Fitness is increased every frame
    private static int MAX_FITNESS = 1000;//Max fitness of the networks if birds make it
    private int deadCount;//Keeps track of the amount of dead birds
    
    private Boolean paused;

    private int pauseDelay;
    private int restartDelay;
    private int pipeDelay;

    public Bird[] birds;
    private ArrayList<Pipe> pipes;
    private Keyboard keyboard;

    public int score;
    public Boolean gameover;
    public Boolean started;
    
    public Game() {
        keyboard = Keyboard.getInstance();
        restart();
    }

    public void restart() {
        paused = false;
        started = true;
        gameover = false;

        score = 0;
        pauseDelay = 0;
        restartDelay = 0;
        pipeDelay = 0;
        
        //Sets fitness and deadCount to 0
        fitness = 0;
        deadCount = 0;
        
        birds = new Bird[POPSIZE];
        for(int i = 0; i < birds.length; i++){
        	birds[i] = new Bird();
        }
        pipes = new ArrayList<Pipe>();
    }

    public void update() {
        //watchForStart();

        /*if (!started)
            return;

        watchForPause();
        watchForReset();

        if (paused)
            return;*/

        for(Bird bird : birds){
            bird.update();        	
        }
        
        movePipes();
        checkForCollisions();    
        
		for(int i = 0; i < POPSIZE; i++){
			float [] input = new float[4];//Input[] for the networks
			input[0] = birds[i].x; //Horizontal position of the bird
			input[1] = birds[i].y; //Vertical position of the bird
			input[2] = pipes.get(0).x; //Horizontal position of the pipe
			input[3] = pipes.get(0).y; //Vertical position of the pipe
			if(neat.population[i].isActive){//Activates network if the network is active
				neat.population[i].networkActivate(input);
			}
			if(neat.population[i].output == 0){ //If the output the first output neuron is higher than the seccond output Neuron, then jump
				birds[i].jump();
			}
		}
		
		for(int i = 0; i < POPSIZE; i++){//Sets the fitness for the networks if the bird is dead
			if(birds[i].dead && neat.population[i].isActive){
				neat.population[i].setFitness(fitness);
				deadCount++;
			}
		}
		
		if(deadCount == POPSIZE || fitness == MAX_FITNESS){//If all birds are dead, run the genetic algorithm, then restart
			neat.runNeat();
			restart();
		}
		fitness++;
    }

    public ArrayList<Render> getRenders() {
        ArrayList<Render> renders = new ArrayList<Render>();
        renders.add(new Render(0, 0, "lib/background.png"));
        for (Pipe pipe : pipes)
            renders.add(pipe.getRender());
        renders.add(new Render(0, 0, "lib/foreground.png"));
        for(Bird bird : birds){
            renders.add(bird.getRender());        	
        }
        return renders;
    }

    /*private void watchForStart() {
        if (!started && keyboard.isDown(KeyEvent.VK_SPACE)) {
            started = true;
        }
    }*/

    /*private void watchForPause() {
        if (pauseDelay > 0)
            pauseDelay--;

        if (keyboard.isDown(KeyEvent.VK_P) && pauseDelay <= 0) {
            paused = !paused;
            pauseDelay = 10;
        }
    }

    private void watchForReset() {
        if (restartDelay > 0)
            restartDelay--;

        if (keyboard.isDown(KeyEvent.VK_R) && restartDelay <= 0) {
            restart();
            restartDelay = 10;
            return;
        }
    }*/

    private void movePipes() {
        pipeDelay--;

        if (pipeDelay < 0) {
            pipeDelay = PIPE_DELAY;
            Pipe northPipe = null;
            Pipe southPipe = null;

            // Look for pipes off the screen
            for (Pipe pipe : pipes) {
                if (pipe.x - pipe.width < 0) {
                    if (northPipe == null) {
                        northPipe = pipe;
                    } else if (southPipe == null) {
                        southPipe = pipe;
                        break;
                    }
                }
            }

            if (northPipe == null) {
                Pipe pipe = new Pipe("north");
                pipes.add(pipe);
                northPipe = pipe;
            } else {
                northPipe.reset();
            }

            if (southPipe == null) {
                Pipe pipe = new Pipe("south");
                pipes.add(pipe);
                southPipe = pipe;
            } else {
                southPipe.reset();
            }

            northPipe.y = southPipe.y + southPipe.height + 175;
        }

        for (Pipe pipe : pipes) {
            pipe.update();
        }
    }

    private void checkForCollisions() {
    	for(Bird bird : birds){
	        for (Pipe pipe : pipes) {
	            if (pipe.collides(bird.x, bird.y, bird.width, bird.height)) {
	                //gameover = true;
	                bird.dead = true;
	            } else if (pipe.x == bird.x && pipe.orientation.equalsIgnoreCase("south")) {
	                score++;
	            }
	        }
	        // Ground + Bird collision
	        if (bird.y + bird.height > App.HEIGHT - 80 || bird.y < 0) {
	            //gameover = true;
	        	bird.dead = true;
	            bird.y = App.HEIGHT - 80 - bird.height;
	        }
    	}
    }
    
    /*public Bird getBird(){
    	return bird;
    }*/
}
