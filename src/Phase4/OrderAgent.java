package Phase4;

import jade.core.Agent;
public class OrderAgent extends Agent {

    private String requiredOperation;

    @Override
    protected void setup() {
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            requiredOperation = (String) args[0];
            System.out.println("Order Agent " + getAID().getLocalName() + " is ready.");
            System.out.println("Looking for operation: " + requiredOperation);

            // Add the behaviour to search and send messages
            addBehaviour(new FindMachineBehaviour());

        } else {
            System.out.println("WARNING: Order Agent " + getAID().getLocalName() + " started with NO operation argument!");
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Order Agent " + getAID().getLocalName() + " terminating.");
    }

    // --- PHASE 4: Updated OneShot Behaviour ---
    private class FindMachineBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription searchSd = new ServiceDescription();
            searchSd.setType("manufacturing-operation");
            searchSd.setName(requiredOperation);
            template.addServices(searchSd);

            try {
                System.out.println(myAgent.getLocalName() + " is searching the DF...");
                DFAgentDescription[] result = DFService.search(myAgent, template);

                if (result.length > 0) {
                    System.out.println("Success! Found " + result.length + " capable machine(s).");

                    // --- PHASE 4: Sending the ACL Message ---

                    // 1. Create a new message with the CFP (Call for Proposal) performative
                    ACLMessage cfpMsg = new ACLMessage(ACLMessage.CFP);

                    // 2. Add all the machines we found as receivers of this message
                    for (int i = 0; i < result.length; ++i) {
                        cfpMsg.addReceiver(result[i].getName());
                    }

                    // 3. Set the actual text payload of the message
                    cfpMsg.setContent("Job_Request");

                    // 4. Send the message out into the JADE environment
                    myAgent.send(cfpMsg);
                    System.out.println("<<< " + myAgent.getLocalName() + " sent CFP to the found machines.");

                } else {
                    System.out.println("Failed: No machines found currently offering " + requiredOperation);
                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }
    }
}