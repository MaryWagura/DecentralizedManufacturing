package Phase2;

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

            // --- PHASE 2: Register with the Directory Facilitator (DF) ---
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());

            ServiceDescription sd = new ServiceDescription();
            sd.setType("manufacturing-operation");
            sd.setName(capability); // Name the service after its capability (e.g., "Drilling")
            dfd.addServices(sd);

            try {
                DFService.register(this, dfd);
                System.out.println(getAID().getLocalName() + " successfully registered in the DF.");
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }

        } else {
            System.out.println("WARNING: Machine Agent " + getAID().getLocalName() + " started with NO capability argument!");
        }
    }

    @Override
    protected void takeDown() {
        // --- PHASE 2: Deregister from the DF on shutdown ---
        try {
            DFService.deregister(this);
            System.out.println(getAID().getLocalName() + " deregistered from the DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        System.out.println("Machine Agent " + getAID().getLocalName() + " terminating.");
    }
}