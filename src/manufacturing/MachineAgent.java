package manufacturing;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
// --- PHASE 4 IMPORT ---
import jade.lang.acl.ACLMessage;

public class MachineAgent extends Agent {

    private String capability;

    @Override
    protected void setup() {
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            capability = (String) args[0];
            System.out.println("Machine Agent " + getAID().getLocalName() + " is ready.");
            System.out.println("Capability: " + capability);

            // Register with the DF
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

            // Start listening for messages
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

    // --- PHASE 4: Updated Cyclic Behaviour ---
    private class ProcessOrderBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // 1. Check the agent's message queue to see if anything arrived
            ACLMessage receivedMsg = myAgent.receive();

            // 2. If we actually got a message...
            if (receivedMsg != null) {
                // 3. Check the "Performative" (the intent of the message).
                // We are specifically looking for a CFP (Call For Proposal)
                if (receivedMsg.getPerformative() == ACLMessage.CFP) {
                    System.out.println(">>> " + myAgent.getLocalName() + " received a job request from: "
                            + receivedMsg.getSender().getLocalName());
                    System.out.println("    Message content: " + receivedMsg.getContent());
                }
            } else {
                // 4. CRITICAL: If the inbox is empty, block the behaviour.
                // This pauses the loop until a NEW message arrives, saving CPU power.
                block();
            }
        }
    }
}