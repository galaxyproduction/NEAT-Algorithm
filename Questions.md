# General Infos

- Mentoring: 8 hours
- Thesis: "Will AI take over the world?"

# To Do

- Set up a date for our next meeting.
- Add a better way to visually debug the program, using rintNetworkGeno() 
- Understand why the program sometimes just do not run.
- Understand why on Dr. Aubert's computer is the panel moving without any birds on it.
- Identify the expensive operations.
- How to *run* the network?
- (Long run) Extension to other games (Tetris, Brick-breaker?)
- Pull files and make sure we can both contribute to the code.

# Questions

## In `Neat`, why are we using those values for `mutateWeight`, `mutateNeuron` and `mutateSynapse`?

> The values came from this video I believed I have shared before: https://www.youtube.com/watch?v=H4WnRLEG73Q . In the video, a group of college students use NEAT to play flappy birds and I used the same values that they did. The values are at 12:55 in the video, if you are curious.

##  In `Neat`, in `selectReject`, how can we justify the test `population[parentId].fitness > rnd.nextFloat()`, i.e., why does every network gets a chance? It is not something done in the original paper [2], as it seems.

> Here is a reference to the selectReject algorithm I am using. https://en.wikipedia.org/wiki/Rejection_sampling . As a side note, I think the selection algorithm will be unnecessary once we implement specialization because it uses it own method to select networks.

The original paper uses speciation, and claims that it is necessary to the system as a whole (cf. abstract). That raise a new question:

##  Could selectReject do better than speciation?

##  What about recursive synapses? Do we need / want to implement them? Those are arrows between hidden nodes, or between output nodes and hidden nodes, that go in the other direction.

##  Did Hunter really simplified the Excess / Disjoint mechanism, or are we missing something?
##  Try to implement speciating, or understand how the implementation without it can do as well as implementations with it.

=> Work in progress.

1. Define an arraylist of species (themselves arraylists)
2. Define a `compute_distance` method, taking two networks and returning their distance as a float. Should that distance be computed using excess / disjoint genes, using which factors?
3. After each generation, group the genome in species (using a $\delta_t$ distance). First come / first serve basis, or other methods? Should we try to have genome in two species at once?
4. Compute the fitness of each individual, compute the fitness of a specie by summing the fitness of their individual, and divide by the number of individual in that specie.
5. Assign a number of individual to each species, using their fitness.
6. Kill some, reproduce, obtain the number allowed number of individuals for each specie.

##  When a node is added, why is the synapses wheigthed with 1 and N (cf. p. 108)?

# Notes

The new files are `Main_neat`, `Neat`, `Neuron`, `Network`, `Synapse`.

## Original FlappyBird code

Available at [1].

- `Keyboard`, `Pipe`, `Render`, `Util`: no changes
- `App`, `GamePanel`:minor changes
- `Bird`, `Game`: major changes

# References

[1] <https://github.com/stronglink/FlappyBird>

[2] <http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf>

[3] <https://www.youtube.com/watch?v=H4WnRLEG73Q>
