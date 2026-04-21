package Phase3;

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

            // --- PHASE 3: Add the OneShot behaviour to find machines ---
            addBehaviour(new FindMachineBehaviour());

        } else {
            System.out.println("WARNING: Order Agent " + getAID().getLocalName() + " started with NO operation argument!");
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Order Agent " + getAID().getLocalName() + " terminating.");
    }

    // --- PHASE 3: Inner Class for OneShot Behaviour ---
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

                // CRUCIAL CHANGE: We use 'myAgent' here instead of 'this'
                DFAgentDescription[] result = DFService.search(myAgent, template);

                if (result.length > 0) {
                    System.out.println("Success! Found " + result.length + " capable machine(s):");
                    for (int i = 0; i < result.length; ++i) {
                        System.out.println(" - " + result[i].getName().getLocalName());
                    }
                } else {
                    System.out.println("Failed: No machines found currently offering " + requiredOperation);
                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }
    }
}}