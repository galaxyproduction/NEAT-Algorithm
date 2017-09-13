# To Do

- Set up a date for our next meeting.
- Add a better way to visually debug the program, using rintNetworkGeno() 
- Understand why the program sometimes just do not run.
- Precise diff of the code used versus the original code of the game [1]
- Understand why on Dr. Aubert's computer is the panel moving without any birds on it.
- Identify the expensive operations.
- How to *run* the network?
- (Long run) Extension to other games (Tetris, Brick-breaker?)

# Questions

- In `Neat`, why are we using those values for `mutateWeight`, `mutateNeuron` and `mutateSynapse`?
- In `Neat`, in `selectReject`, how can we justify the test `population[parentId].fitness > rnd.nextFloat()`, i.e., why does every network gets a chance? It is not something done in the original paper [2], as it seems.
- Did Hunter really simplified the Excess / Disjoint mechanism, or are we missing something?
- Try to implement speciating, or understand how the implementation without it can do as well as implementations with it.

# Notes

The new files are `Main_neat`, `Neat`, `Neuron`, `Network`, `Synapse`.

# References
[1] https://github.com/stronglink/FlappyBird
[2] http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf
[3] https://www.youtube.com/watch?v=H4WnRLEG73Q
