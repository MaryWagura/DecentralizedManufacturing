package Phase3;

import jade.core.Agent;
public class MachineAgent extends Agent {

    private String capability;

    @Override
    protected void setup() {
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            capability = (String) args[0];
            System.out.println("Machine Agent " + getAID().getLocalName() + " is ready.");
            System.out.println("Capability: " + capability);

            // --- Register with the DF ---
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType("manufacturing-operation");
            sd.setName(capability);
            dfd.addServices(sd);

            try {
                DFService.register(this, dfd);
                System.out.println(getAID().getLocalName() + " successfully registered in the DF.");
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }

            // --- PHASE 3: Add the continuous listening behaviour ---
            addBehaviour(new ProcessOrderBehaviour());

        } else {
            System.out.println("WARNING: Machine Agent " + getAID().getLocalName() + " started with NO capability argument!");
        }
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println(getAID().getLocalName() + " deregistered from the DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Machine Agent " + getAID().getLocalName() + " terminating.");
    }

    // --- PHASE 3: Inner Class for Cyclic Behaviour ---
    private class ProcessOrderBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            System.out.println("Machine " + getAID().getLocalName() + " ready for jobs...");

            // block() tells JADE to pause this behaviour until a new message arrives,
            // preventing it from hogging the CPU by looping endlessly in the background.
            block();
        }
    }
}