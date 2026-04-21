package Phase1;

import jade.core.Agent;

public class MachineAgent extends Agent {

    // Class variable to store the machine's capability
    private String capability;

    @Override
    protected void setup() {
        // Read the arguments passed to the agent
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            capability = (String) args[0];
            System.out.println("Machine Agent " + getAID().getName() + " is ready.");
            System.out.println("Registered capability: " + capability);
        } else {
            System.out.println("WARNING: Machine Agent " + getAID().getName() + " started with NO capability argument!");
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Machine Agent " + getAID().getName() + " terminating.");
    }
}
