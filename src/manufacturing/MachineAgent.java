package manufacturing;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
// --- PHASE 5 IMPORT ---
import java.util.Random;

public class MachineAgent extends Agent {

    private String capability;

    @Override
    protected void setup() {
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            capability = (String) args[0];
            System.out.println("Machine Agent " + getAID().getLocalName() + " is ready. Capability: " + capability);

            // Register with the DF (Yellow Pages)
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType("manufacturing-operation");
            sd.setName(capability);
            dfd.addServices(sd);

            try {
                DFService.register(this, dfd);
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }

            // Start listening for messages
            addBehaviour(new ProcessOrderBehaviour());
        }
    }

    @Override
    protected void takeDown() {
        try { DFService.deregister(this); } catch (FIPAException fe) { fe.printStackTrace(); }
        System.out.println("Machine Agent " + getAID().getLocalName() + " terminating.");
    }

    // --- PHASE 5: Updated Cyclic Behaviour for Bidding ---
    private class ProcessOrderBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage receivedMsg = myAgent.receive();

            if (receivedMsg != null) {
                // Determine what kind of message we just received
                switch (receivedMsg.getPerformative()) {

                    // 1. We received a Call for Proposal (A job request)
                    case ACLMessage.CFP:
                        System.out.println(">>> " + myAgent.getLocalName() + " received CFP from " + receivedMsg.getSender().getLocalName());

                        // Generate a random processing time between 10 and 50
                        int processingTime = 10 + new Random().nextInt(41);

                        // Create an automatic reply to the sender
                        ACLMessage reply = receivedMsg.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE); // Intent: "I am placing a bid"
                        reply.setContent(String.valueOf(processingTime)); // Payload: Our random time

                        System.out.println("<<< " + myAgent.getLocalName() + " proposing time: " + processingTime);
                        myAgent.send(reply); // Send the bid!
                        break;

                    // 2. We won the bid!
                    case ACLMessage.ACCEPT_PROPOSAL:
                        System.out.println("WINNER! >>> " + myAgent.getLocalName() + " got the job! Starting manufacturing...");
                        break;

                    // 3. We lost the bid :(
                    case ACLMessage.REJECT_PROPOSAL:
                        System.out.println("LOSER   >>> " + myAgent.getLocalName() + " lost the bid. Back to waiting.");
                        break;
                }
            } else {
                block(); // Wait for next message
            }
        }
    }
}