package manufacturing;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
// --- PHASE 5 IMPORT ---
import java.util.ArrayList;

public class OrderAgent extends Agent {

    private String requiredOperation;

    @Override
    protected void setup() {
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            requiredOperation = (String) args[0];
            System.out.println("Order Agent " + getAID().getLocalName() + " is ready. Looking for: " + requiredOperation);

            // Start the process
            addBehaviour(new FindMachineBehaviour());
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Order Agent " + getAID().getLocalName() + " terminating.");
    }

    private class FindMachineBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription searchSd = new ServiceDescription();
            searchSd.setType("manufacturing-operation");
            searchSd.setName(requiredOperation);
            template.addServices(searchSd);

            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);

                if (result.length > 0) {
                    System.out.println("Success! Found " + result.length + " capable machine(s). Sending CFPs...");

                    ACLMessage cfpMsg = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < result.length; ++i) {
                        cfpMsg.addReceiver(result[i].getName()); // BROADCAST: Adding ALL found machines
                    }
                    cfpMsg.setContent("Job_Request");
                    myAgent.send(cfpMsg);

                    // --- PHASE 5: Start listening for the bids ---
                    // We pass 'result.length' so the behaviour knows exactly how many replies to wait for!
                    myAgent.addBehaviour(new NegotiationBehaviour(result.length));

                } else {
                    System.out.println("Failed: No machines found.");
                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }
    }

    // --- PHASE 5: New Cyclic Behaviour to handle the incoming bids ---
    private class NegotiationBehaviour extends CyclicBehaviour {
        private int expectedReplies; // How many machines we sent the CFP to
        private int receivedReplies = 0; // How many have answered so far
        private ArrayList<ACLMessage> proposals = new ArrayList<>(); // To store the bids

        // Constructor to pass in the expected number of replies
        public NegotiationBehaviour(int expected) {
            this.expectedReplies = expected;
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if (msg != null) {
                // If the message is a bid (PROPOSE)
                if (msg.getPerformative() == ACLMessage.PROPOSE) {
                    System.out.println(">>> " + myAgent.getLocalName() + " received bid from "
                            + msg.getSender().getLocalName() + ": " + msg.getContent() + " minutes.");

                    // Add it to our list and count it
                    proposals.add(msg);
                    receivedReplies++;

                    // --- THE AWARD: Have we received all the bids we were expecting? ---
                    if (receivedReplies >= expectedReplies) {
                        System.out.println("\n--- All bids received! Evaluating ---");

                        int bestTime = 9999; // Start with a ridiculously high time
                        ACLMessage bestProposal = null; // Track the winning message

                        // 1. Loop through all stored proposals to find the lowest time
                        for (ACLMessage prop : proposals) {
                            int time = Integer.parseInt(prop.getContent());
                            if (time < bestTime) {
                                bestTime = time;
                                bestProposal = prop;
                            }
                        }

                        // 2. Loop through the proposals again to send out the Accept/Reject messages
                        for (ACLMessage prop : proposals) {
                            ACLMessage reply = prop.createReply(); // Auto-targets the sender of the proposal

                            if (prop == bestProposal) {
                                // This is the winner!
                                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                reply.setContent("You are hired!");
                                System.out.println("<<< Awarding job to: " + prop.getSender().getLocalName() + " (Time: " + bestTime + ")");
                            } else {
                                // This is a loser
                                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                reply.setContent("Too slow!");
                            }
                            myAgent.send(reply);
                        }

                        // The negotiation is over, so we can remove this behaviour to stop looping
                        System.out.println("--- Negotiation Complete ---\n");
                        myAgent.removeBehaviour(this);
                    }
                }
            } else {
                block(); // Wait for next message
            }
        }
    }
}