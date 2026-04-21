package manufacturing;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class OrderAgent extends Agent {

    private String requiredOperation;

    @Override
    protected void setup() {
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            requiredOperation = (String) args[0];
            System.out.println("Order Agent " + getAID().getLocalName() + " is ready.");
            System.out.println("Looking for operation: " + requiredOperation);

            // --- PHASE 2: Search the Directory Facilitator (DF) ---
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription searchSd = new ServiceDescription();
            searchSd.setType("manufacturing-operation");
            searchSd.setName(requiredOperation); // We only want machines offering our required operation
            template.addServices(searchSd);

            try {
                System.out.println(getAID().getLocalName() + " is searching the DF...");
                DFAgentDescription[] result = DFService.search(this, template);

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

        } else {
            System.out.println("WARNING: Order Agent " + getAID().getLocalName() + " started with NO operation argument!");
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Order Agent " + getAID().getLocalName() + " terminating.");
    }
}