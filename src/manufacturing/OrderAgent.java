package manufacturing;

import jade.core.Agent;

public class OrderAgent extends Agent {
    // Class variable to store what operation this order needs
    private String requiredOperation;

    @Override
    protected void setup() {
        // Read the arguments passed to the agent
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            requiredOperation = (String) args[0];
            System.out.println("Order Agent " + getAID().getName() + " is ready.");
            System.out.println("Required operation: " + requiredOperation);
        } else {
            System.out.println("WARNING: Order Agent " + getAID().getName() + " started with NO operation argument!");
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Order Agent " + getAID().getName() + " terminating.");
    }

}
