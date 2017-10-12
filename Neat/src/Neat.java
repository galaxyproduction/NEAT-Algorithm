import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Neat {
	Random rnd = new Random();
	Network[] population; //Holds all Networks
	
	int generation = 0;
	int neuronId = 0; //Keeps track of the Id
	int synInnoNum = 0; //Keeps track of the synapse's innovation numbers
	
	float maxFitness; //Used to scale network's fitness between 0 and 1
	
	//Percentages of mutating  
	float mutateWeight = 0.6f;
	float mutateNeuron = 0.05f;
	float mutateSynapse = 0.05f;
	
	public Neat(int netCount, int inputs_, int outputs_){ //Neat's constructor
		population = new Network[netCount];
		for(int i = 0; i < population.length; i++){
			population[i] = new Network(inputs_, outputs_, this);
		}
		
		neuronId = inputs_ + outputs_ - 1; //Sets the neuronId to the amount on initial neurons
		synInnoNum = inputs_ * outputs_ -1; //Sets the synInnoNum to the amount of initial synapses
	}
	
	public void runNeat(){ //Runs the genetic algorithm
		recalcFitness();
		crossover();
		mutate();
		generation++;
		System.out.println("Generation: "+ generation);
	}
	
	void crossover(){ //Crossover networks and refill population[]
		Network[] popCopy = new Network[population.length]; //Used to hold breed networks 
		for(int i = 0; i < population.length; i++){
			Network parentA = selectReject();//Selects parents based on fitness
			Network parentB = selectReject();//Selects parents based on fitness
			
			ArrayList<Neuron> childNeurons = new ArrayList<Neuron>(); //Child Neurons for new Network
			for(Neuron neuronA : parentA.neurons){ //Adds all Neurons from parentA
				childNeurons.add(new Neuron(neuronA.type, neuronA.id));
			}
			for(Neuron neuronB : parentB.neurons){//Adds all neurons from parentB that are not in parentA
				boolean addNeuron = true;
				for(Neuron neuronChild : childNeurons){
					if(neuronB.id == neuronChild.id){
						addNeuron = false;
					}
				}
				if(addNeuron){
					childNeurons.add(new Neuron(neuronB.type, neuronB.id));
				}
			}
			
			for(Synapse synA : parentA.synapses){ //Sets the neuron reference in the 'In' and Out'- 
				synA.in = findNeuron(synA.in.id, childNeurons); //-to the newly created Neurons in childNeurons
				synA.out = findNeuron(synA.out.id, childNeurons);
			}
			for(Synapse synB : parentB.synapses){  //Sets the neuron reference in the 'In' and Out'- 
				synB.in = findNeuron(synB.in.id, childNeurons);//-to the newly created Neurons in childNeurons
				synB.out = findNeuron(synB.out.id, childNeurons);
			}
						
			ArrayList<Synapse> childSynapses = new ArrayList<Synapse>();//Child Synapses for the new Network
			
			for(Synapse synA : parentA.synapses){//Adds all Synapses from parentA
				childSynapses.add(new Synapse(synA));
			}

			for(Synapse synB : parentB.synapses){//Adds all Synapses from parentB that are not in parentA
				boolean add = true;
					for(Synapse synC : childSynapses){
						if(synB.innovationNum == synC.innovationNum){//50% chance to change the new synape's attributes to parentB's
							add = false;
							if(0.5 > rnd.nextFloat()){
								synC.weight = synB.weight;
								synC.enabled = synB.enabled;
							}
						}
					}
				if(add){
					childSynapses.add(new Synapse(synB));
				}
			}
			
			popCopy[i] = new Network(childNeurons, childSynapses, parentA); //Creates new Network from childNeurons and ChildSynapses			
		}
		population = popCopy.clone(); //Clones the popCopy to the population
	}
		
	void mutate(){ //Mutates the networks in 3 ways: Weight, New Neuron, and new Synapse
		for(Network net : population){
			if(mutateWeight > rnd.nextFloat()){ //Mutates the weight of a synapse in a network
				net.synapses.get(rnd.nextInt(net.synapses.size())).weight = -1 + rnd.nextFloat() * (1 - -1);
			}
			
			if(mutateNeuron > rnd.nextFloat()){ //Adds new Neuron
				Synapse disSyn = net.synapses.get(rnd.nextInt(net.synapses.size()));
				disSyn.enabled = false; //Sets disSyn enabled to false
				Neuron newNeuron = new Neuron("Hidden", newNeuronId()); //Creates a new Neuron
				net.neurons.add(newNeuron);
				net.synapses.add(new Synapse(disSyn.in, newNeuron, newSynInnoNum(disSyn.in.id, newNeuron.id))); //Creates a new Synapse between the old 'In' and the new Neuron
				net.synapses.add(new Synapse(newNeuron, disSyn.out, newSynInnoNum(newNeuron.id, disSyn.out.id))); //Creates a new Synapse between the new Neuron and the old 'Out'
			}
			
			if(mutateSynapse > rnd.nextFloat()){
				Neuron inputN;
				Neuron outputN;
				while(true){//Gets the input Neuron, 
					inputN = net.neurons.get(rnd.nextInt(net.neurons.size()));
					if(!inputN.type.equals("Output")){//Cannot be an output
						break;
					}
				}
				while(true){//Gets the output Neuron
					outputN = net.neurons.get(rnd.nextInt(net.neurons.size()));
					if(inputN.type.equals("Hidden") && outputN.type.equals("Output") && outputN.id != inputN.id){//Hidden Input neurons must connect to output neurons
						break;
					}
					if(inputN.type.equals("Input") && !outputN.type.equals("Input") && outputN.id != inputN.id){
						break;
					}
				}
				
				boolean add = true;
				for(Synapse syn : net.synapses){//Checks if the synapse is already in the network
					if(syn.in.id == inputN.id && syn.out.id == outputN.id){
						add = false;
					}
				}
				if(add){//If it isn't in the network then it is added
					net.synapses.add(new Synapse(inputN, outputN, newSynInnoNum(inputN.id, outputN.id)));
				}
			}
		}
	}	
	
	float distComp(Network a, Network b){ //Returns the distance of compatibility between two networks
		float c1 = 1f; //Coefficent of disjointed and excess genes
		float c3 = 0.4f; //Coefficent of average weight between matching genes
		
		int disjoint = 0; //Nums of different genes
		float avgWeightDiff = 0; //Average of the weight differences
		int N = (a.synapses.size() > b.synapses.size()) ? a.synapses.size() : b.synapses.size(); //Synapse size of the larger genome
		int matchingGenes = 0;
		
		Network larger = (a.synapses.size() > b.synapses.size()) ? a : b;
		Network smaller = (a.synapses.size() < b.synapses.size()) ? a : b;
		
		for(int i = 0; i < larger.synapses.size(); i++){
			if(i < smaller.synapses.size()){ //Adds all disjointed genes and calcs avgWeightDiff
				if(larger.synapses.get(i).innovationNum == smaller.synapses.get(i).innovationNum){ //Checks if same synapse
					avgWeightDiff += larger.synapses.get(i).weight - smaller.synapses.get(i).weight;
					matchingGenes++;
				}else{ //Adds to disjoint if not the same synapse
					disjoint++;
				}
			}else{ //Adds all excess genes in the larger network
				disjoint++;
			}
		}
		
		avgWeightDiff /= matchingGenes;
		
		float dist = (c1 * disjoint) / N + c3 * avgWeightDiff; //Distance of compatibility
		return dist;
	}
	
	Network selectReject(){ //Selects a parent based on its fitness
		Network parent = null;
		while(parent == null){
			int parentId = rnd.nextInt(population.length);
			if(population[parentId].fitness > rnd.nextFloat()){ //Higher fitness are more likely to be chosen than lower fitness
				parent = population[parentId];
			}
		}
		return parent;
	}
	
	Neuron findNeuron(int id, ArrayList<Neuron> neurons){//Used to set the old Synapse's 'In' and 'Out' neurons to the newly created neurons
		Neuron foundN = null;
		for(Neuron n : neurons){
			if(n.id == id){
				foundN = n;
				break;
			}
		}
		return foundN;
	}
	
	public void setMaxFit(float fit){ //Sets the maxFitness to the highest fitness among the networks
		if(fit > maxFitness){
			maxFitness = fit;
		}
	}
	
	public void recalcFitness(){//Scales the fitness of the networks between 0 and 1
		for(Network net : population){
			net.fitness = net.fitness / maxFitness;
		}
	}
	
	int newSynInnoNum(int inId, int outId){//New Synapse Innovation Number
		for(Network net : population){
			for(Synapse syn : net.synapses){
				if(inId == syn.in.id && outId == syn.out.id){
					return syn.innovationNum;//If the synapse exist in the network, it returns the innovation number of that synapse
				}
			}	
		}
		synInnoNum++;
		return synInnoNum;//If synapse doesn't already exist, return a new innovation number
	}
	
	int newNeuronId(){ //New Neuron Id
		neuronId++;
		return neuronId;
	}
}
