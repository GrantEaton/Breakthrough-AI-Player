# Breakthrough-AI-Player
An artificial intelligence player that plays the game Breakthrough, using the AI methodology of Alpha-beta to play against you.


Minimax Pseudocode

alpaBetaMinimax(node, alpha, beta) 

   """ 
   Returns best score for the player associated with the given node.
   Also sets the variable bestMove to the move associated with the
   best score at the root node.  
   """

   # check if at search bound
   if node is at depthLimit
      return staticEval(node)

   # check if leaf
   children = successors(node)
   if len(children) == 0
      if node is root
         bestMove = [] 
      return staticEval(node)

   # initialize bestMove
   if node is root
      bestMove = operator of first child
      # check if there is only one option
      if len(children) == 1
         return None

   if it is MAX's turn to move
      for child in children
         result = alphaBetaMinimax(child, alpha, beta)
         if result > alpha
            alpha = result
            if node is root
               bestMove = operator of child
         if alpha >= beta
            return alpha
      return alpha

   if it is MIN's turn to move
      for child in children
         result = alphaBetaMinimax(child, alpha, beta)
         if result < beta
            beta = result
            if node is root
               bestMove = operator of child
         if beta <= alpha
            return beta
      return beta
